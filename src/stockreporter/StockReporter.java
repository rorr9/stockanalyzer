package stockreporter;

import java.util.logging.Level;
import java.util.logging.Logger;
import stockreporter.scrapers.YahooScraper;
import stockreporter.scrapers.InvestopediaScraper;
import stockreporter.scrapers.FidelityScraper;

/**
 * Main class for scrapping the data
 */
public class StockReporter {
    
    private static final Logger logger = Logger.getLogger(StockReporter.class.getName());
    
    public static void main(String[] args) {    
        
        logger.log(Level.INFO, "Get database instance");
        StockDao dao = StockDao.getInstance();
        
        logger.log(Level.INFO, "Create scraper instances");
        InvestopediaScraper investopediaScraper = new InvestopediaScraper();
        YahooScraper yahooScraper = new YahooScraper();
        FidelityScraper fidelityScraper = new FidelityScraper();
        
        logger.log(Level.INFO, "Scrap summary data for Yahoo...");
        yahooScraper.scrapeAllSummaryData();
        
        logger.log(Level.INFO, "Scrap summary data for Investopedia...");
        investopediaScraper.scrapeAllSummaryData();
        
        logger.log(Level.INFO, "Scrap summary data for Fidelity...");
        fidelityScraper.scrapeAllSummaryData();
        
    }
}