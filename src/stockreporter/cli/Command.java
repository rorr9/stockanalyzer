package stockreporter.cli;

import java.util.logging.Level;
import java.util.logging.Logger;
import stockreporter.StockReporter;
import stockreporter.daomodels.StockTicker;
import stockreporter.scrapers.Scraper;
import stockreporter.scrapers.ScraperFactory;
import stockreporter.service.StockService;
import stockreporter.service.StockServiceImpl;

/**
 * The Command class parses parameters passed in via the Command Line Interface
 * to perform specific operations such as adding or removing stock tickers and
 * scraping summary data for specific stocks.
 */
public class Command {
	private static StockService stockService = new StockServiceImpl();

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
        System.out.println("                 Stock Analyzier Command Line Interface                             ");
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
        stockService.setStockTickerData(symbol, description);
    }

    public static void removeTicker(String symbol) {
        stockService.deleteFromStockTicker(symbol);
    }

    public static void getSummary(String symbol) {

        int tickerID = stockService.getStockTickerBySymbol(symbol);
        if (tickerID == 0) {
            System.out.println("Stock not in DB");
        }

        ScraperFactory scraperFactory = new ScraperFactory();
        logger.log(Level.INFO, "Create scraper instances");

        Scraper investopediaScraper = scraperFactory.getScraper("INVESTOPEDIA");
        Scraper yahooScraper = scraperFactory.getScraper("YAHOO");
//        Scraper marketWatchScraper = scraperFactory.getScraper("MARKETWATCH");
        Scraper fidelityScraper = scraperFactory.getScraper("FIDELITY");

        StockTicker ticker = stockService.getStockTickerByID(tickerID);
        logger.log(Level.INFO, "Scrap single summary data for Yahoo...");
        yahooScraper.scrapeSingleSummaryData(ticker);
        logger.log(Level.INFO, "Scrap single summary data for Investopedia...");
        investopediaScraper.scrapeSingleSummaryData(ticker);
        logger.log(Level.INFO, "Scrap single summary data for MarketWatch...");
        //marketWatchScraper.scrapeSingleSummaryData(ticker);
        logger.log(Level.INFO, "Scrap single summary data for Fidelity...");
        fidelityScraper.scrapeAllSummaryData();

    }

    public static void getHistorical(String symbol) {
        int tickerID = stockService.getStockTickerBySymbol(symbol);
        if (tickerID == 0) {
            System.out.println("Stock not in DB");
        }
        //TO:DO
    }

    public static void scrapeAllStocks() {
        ScraperFactory scraperFactory = new ScraperFactory();
   
        logger.log(Level.INFO, "Create scraper instances");
        Scraper investopediaScraper = scraperFactory.getScraper("INVESTOPEDIA");
        Scraper yahooScraper = scraperFactory.getScraper("YAHOO");
//        Scraper marketWatchScraper = scraperFactory.getScraper("MARKETWATCH");
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
}
