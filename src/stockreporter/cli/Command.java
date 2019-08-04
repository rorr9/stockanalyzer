package stockreporter.cli;

import java.util.logging.Level;
import java.util.logging.Logger;
import stockreporter.StockDao;
import stockreporter.StockReporter;
import stockreporter.daomodels.StockSummary;
import stockreporter.daomodels.StockTicker;
import stockreporter.scrapers.Scraper;
import stockreporter.scrapers.ScraperFactory;

/**
 * The Command class parses parameters passed in via the Command Line Interface
 * to perform specific operations such as adding or removing stock tickers and
 * scraping summary data for specific stocks.
 */
public class Command {

    private static final Logger logger = Logger.getLogger(StockReporter.class.getName());

    public static void parseParams(String[] params) {
        //if no parameters are present, scrape for all stocks
        if (params.length == 0) {
            scrapeAllStocks();
            return;
        }

        switch (params[0]) {

            case "add":
                if (params.length == 3) {
                    addTicker(params[1], params[2]);
                } else {
                    printHelp();
                }
            case "remove":
                if (params.length == 2) {
                    removeTicker(params[1]);
                } else {
                    printHelp();
                }
                break;
            case "summary":
                if (params.length == 2) {
                    getSummary(params[1]);
                } else {
                    printHelp();
                }
                break;
            case "historical":
                if (params.length == 2) {
                    getSummary(params[1]);
                } else {
                    printHelp();
                }
                break;
            case "help":
                printHelp();
                break;
            default:
                printHelp();

        }

    }

    public static void printHelp() {
        System.out.println("                 Stock Analyzer Command Line Interface                             ");
        System.out.println("------------------------------------------------------------------------------------");
        System.out.println("Command     Parameters                       Description                            ");
        System.out.println("add         [symbol] [\"description\"]       adds a stock ticker to the database    ");
        System.out.println("remove      [symbol]                         removes a stock ticker to the database ");
        System.out.println("summary     [symbol]                         scrapes summary data for ticker        ");
        System.out.println("historical  [symbol]                         scrapes historical data for ticker     ");
        System.out.println("null                                         DEFAULT: scrapes all tickers for data  ");
        System.out.println("help                                         prints this info                       ");
        System.out.println("------------------------------------------------------------------------------------");
    }

    public static void addTicker(String symbol, String description) {
        StockDao.getInstance().setStockTickerData(symbol, description);
    }

    public static void removeTicker(String symbol) {
        StockDao.getInstance().deleteFromStockTicker(symbol);
    }

    public static void getSummary(String symbol) {

        int tickerID = StockDao.getInstance().getStockTickerBySymbol(symbol);
        if (tickerID == 0) {
            System.out.println("Stock not in DB");
        }

        ScraperFactory scraperFactory = new ScraperFactory();
        //create scrapers
        logger.log(Level.INFO, "Get database instance");
        StockDao dao = StockDao.getInstance();
        logger.log(Level.INFO, "Create scraper instances");

        Scraper investopediaScraper = scraperFactory.getScraper("INVESTOPEDIA");
        Scraper yahooScraper = scraperFactory.getScraper("YAHOO");
        Scraper marketWatchScraper = scraperFactory.getScraper("MARKETWATCH");
        Scraper fidelityScraper = scraperFactory.getScraper("FIDELITY");

        StockTicker ticker = StockDao.getInstance().getStockTickerByID(tickerID);
        logger.log(Level.INFO, "Scrap single summary data for Yahoo...");
        yahooScraper.scrapeSingleSummaryData(ticker);
        logger.log(Level.INFO, "Scrap single summary data for Investopedia...");
        investopediaScraper.scrapeSingleSummaryData(ticker);
        logger.log(Level.INFO, "Scrap single summary data for MarketWatch...");
        //marketWatchScraper.scrapeSingleSummaryData(ticker);
        logger.log(Level.INFO, "Scrap single summary data for Fidelity...");
        fidelityScraper.scrapeSingleSummaryData(ticker);

        printSummary("Yahoo", symbol, yahooScraper.getSummaryData());
        printSummary("Investopedia", symbol, investopediaScraper.getSummaryData());
        printSummary("Fidelity", symbol, fidelityScraper.getSummaryData());

    }

    public static void getHistorical(String symbol) {
        int tickerID = StockDao.getInstance().getStockTickerBySymbol(symbol);
        if (tickerID == 0) {
            System.out.println("Stock not in DB");
        }
        //TO:DO
    }

    public static void scrapeAllStocks() {
        ScraperFactory scraperFactory = new ScraperFactory();
        logger.log(Level.INFO, "Get database instance");
        StockDao dao = StockDao.getInstance();
        logger.log(Level.INFO, "Create scraper instances");
        Scraper investopediaScraper = scraperFactory.getScraper("INVESTOPEDIA");
        Scraper yahooScraper = scraperFactory.getScraper("YAHOO");
        Scraper marketWatchScraper = scraperFactory.getScraper("MARKETWATCH");
        Scraper fidelityScraper = scraperFactory.getScraper("FIDELITY");

        logger.log(Level.INFO, "Scrap summary data for Yahoo...");
        yahooScraper.scrapeAllSummaryData();
        logger.log(Level.INFO, "Scrap summary data for Investopedia...");
        investopediaScraper.scrapeAllSummaryData();
        logger.log(Level.INFO, "Scrap single summary data for MarketWatch...");
        //marketWatchScraper.scrapeAllSummaryData();
        logger.log(Level.INFO, "Scrap single summary data for Fidelity...");
        fidelityScraper.scrapeAllSummaryData();

    }

    public static void printSummary(String source, String symbol, StockSummary summary) {

        System.out.println("--------------------------------------------------------------");
        System.out.println("[" + symbol + "] (from " + source + ") Prev. Close Price: " + summary.getPrevClosePrice() + " Open Price: " + summary.getOpenPrice() + "  Bid Price: " + summary.getBidPrice() + " Ask Price: " + summary.getAskPrice() + " Dividend Yield: " + summary.getDividentYield() + " 52 Week Min: " + summary.getFiftyTwoWeeksMin() + " 52 Week Max: " + summary.getFiftyTwoWeeksMax() + " Volume: " + summary.getVolume());

    }
}
