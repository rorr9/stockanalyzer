package stockreporter.cli;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import stockreporter.StockReporter;
import stockreporter.daomodels.StockSummary;
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

    public static final boolean USE_INVESTOPEDIA_SCRAPER = false, USE_MARKETWATCH_SCRAPER = true, USE_YAHOO_SCRAPER = true, USE_FIDELITY_SCRAPER = true;

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
        stockService.setStockTickerData(symbol, description);
    }

    public static void removeTicker(String symbol) {
        stockService.deleteFromStockTicker(symbol);
    }

    private static Map<String, Scraper> chooseScrapers() {
        ScraperFactory scraperFactory = new ScraperFactory();
        Map<String, Scraper> scrapers = new HashMap<String, Scraper>();

        if (USE_INVESTOPEDIA_SCRAPER) {
            scrapers.put("Investopedia", scraperFactory.getScraper("INVESTOPEDIA"));
        }
        if (USE_YAHOO_SCRAPER) {
            scrapers.put("Yahoo", scraperFactory.getScraper("YAHOO"));
        }
        if (USE_MARKETWATCH_SCRAPER) {
            scrapers.put("MarketWatch", scraperFactory.getScraper("MARKETWATCH"));
        }
        if (USE_FIDELITY_SCRAPER) {
            scrapers.put("Fidelity", scraperFactory.getScraper("FIDELITY"));
        }

        return scrapers;
    }

    public static List<StockTicker> getTickers() {
        return stockService.getAllstockTickers();
    }

    public static void getSummary(String symbol) {

        int tickerID = stockService.getStockTickerBySymbol(symbol);
        if (tickerID == 0) {
            System.out.println("Stock not in DB");
        }

        StockTicker ticker = stockService.getStockTickerByID(tickerID);
        Map<String, Scraper> scrapers = chooseScrapers();

        for (Map.Entry<String, Scraper> scraper : scrapers.entrySet()) {
            logger.log(Level.INFO, "Scrape single summary data for %s...", scraper.getKey());
            scraper.getValue().scrapeSingleSummaryData(ticker);
            printSummary(scraper.getKey(), symbol, scraper.getValue().getSummaryData());
        }
    }

    public static void getHistorical(String symbol) {
        int tickerID = stockService.getStockTickerBySymbol(symbol);
        if (tickerID == 0) {
            System.out.println("Stock not in DB");
        }
        //TO:DO when historical data gets implemented
    }

    public static void scrapeAllStocks() {

        Map<String, Scraper> scrapers = chooseScrapers();

        for (Map.Entry<String, Scraper> scraper : scrapers.entrySet()) {

            logger.log(Level.INFO, "Scrape single summary data for %s...", scraper.getKey());
            scraper.getValue().scrapeAllSummaryData();

            for (int i = 0; i < scraper.getValue().getAllSummaryData().size(); i++) {
                printSummary(scraper.getKey(), getTickers().get(i).getSymbol(), scraper.getValue().getAllSummaryData().get(i));
            }

        }
    }

    public static void printSummary(String source, String symbol, StockSummary summary) {

        System.out.println("--------------------------------------------------------------");
        System.out.println("[" + symbol + "] (from " + source + ") Prev. Close Price: " + summary.getPrevClosePrice() + " Open Price: " + summary.getOpenPrice() + "  Bid Price: " + summary.getBidPrice() + " Ask Price: " + summary.getAskPrice() + " Dividend Yield: " + summary.getDividentYield() + " 52 Week Min: " + summary.getFiftyTwoWeeksMin() + " 52 Week Max: " + summary.getFiftyTwoWeeksMax() + " Volume: " + summary.getVolume());

    }
}
