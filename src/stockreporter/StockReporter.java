package stockreporter;

import java.util.logging.Level;
import java.util.logging.Logger;

import stockreporter.scrapers.Scraper;
import stockreporter.scrapers.ScraperFactory;

/**
 * Main class for scrapping the data
 */
public class StockReporter {
    
    private static final Logger logger = Logger.getLogger(StockReporter.class.getName());
    
    public static void main(String[] args) {
        ScraperFactory scraperFactory = new ScraperFactory();
        
        logger.log(Level.INFO, "Get database instance");
        StockDao dao = StockDao.getInstance();
        
        logger.log(Level.INFO, "Create scraper instances");
        Scraper investopediaScraper = scraperFactory.getScraper("INVESTOPEDIA");
        Scraper yahooScraper = scraperFactory.getScraper("YAHOO");

        logger.log(Level.INFO, "Scrap summary data for Yahoo...");
        yahooScraper.scrapeAllSummaryData();
        
        logger.log(Level.INFO, "Scrap summary data for Investopedia...");
        investopediaScraper.scrapeAllSummaryData();
        
    }
}