package stockreporter.scrapers;

import java.io.File;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import static org.junit.Assert.*;
import stockreporter.StockDao;
import java.lang.reflect.*;
import stockreporter.Utility;
import stockreporter.daomodels.StockSummary;
import stockreporter.daomodels.StockTicker;

public class MarketWatchScraperTest {

    private StockDao dao;
    private StockTicker test = new StockTicker();
    private StockSummary master = new StockSummary();
    
    public MarketWatchScraperTest() {
        try {
            File tempFile = File.createTempFile("dbTest", "sqlite");
            tempFile.deleteOnExit();
            String tempUrl = "jdbc:sqlite:" + tempFile.getAbsolutePath();
            
            dao = new StockDao();
            Class daoClass = dao.getClass();

            Field f1 = daoClass.getDeclaredField("dbName");
            f1.setAccessible(true);
            f1.set(dao, tempFile.getName());
            Field f2 = daoClass.getDeclaredField("url");
            f2.setAccessible(true);
            f2.set(dao, tempUrl);
            Field f3 = daoClass.getDeclaredField("instance");
            f3.setAccessible(true);
            f3.set(dao, null);
            
            dao.getInstance();
            dao.deleteAll();
            
            test.setId(1);
            test.setName("Apple Inc.");
            test.setSymbol("AAPL");
            
            master.setPrevClosePrice(Utility.convertStringCurrency("195.35"));
            master.setBetaCoefficient(Utility.convertStringCurrency("1.247"));
            master.setDaysRangeMax(Utility.convertStringCurrency("196.37"));
            master.setDaysRangeMin(Utility.convertStringCurrency("193.14"));
            master.setDividentYield(Utility.convertStringCurrency("2.92/1.49%"));
            master.setEps(Utility.convertStringCurrency("12.16"));
            master.setFiftyTwoWeeksMax(Utility.convertStringCurrency("233.47"));
            master.setFiftyTwoWeeksMin(Utility.convertStringCurrency("142.00"));
            master.setMarketCap(Utility.convertStringCurrency("934.08B"));
            master.setOpenPrice(Utility.convertStringCurrency("194.79"));
            master.setPeRatio(Utility.convertStringCurrency("16.09"));
            master.setSummaryId(0);
            master.setVolume(Utility.convertStringCurrency("19,105,393.00").longValue());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testScrapeSingleSummaryData() {
        try{
            File input = new File("Apr_4_2019_AAPL_Investopedia.html");
            Document tmpDocument = Jsoup.parse(input, "UTF-8", "");
            
            MarketWatchScraper is = new MarketWatchScraper();
            Class isClass = is.getClass();

            Field f1 = isClass.getDeclaredField("test");
            f1.setAccessible(true);
            f1.set(is, true);
            
            Field f2 = isClass.getDeclaredField("document");
            f2.setAccessible(true);
            f2.set(is, tmpDocument);
            
            is.scrapeSingleSummaryData(test);
            
            Field f3 = isClass.getDeclaredField("summaryData");
            f3.setAccessible(true);
            StockSummary results = (StockSummary)f3.get(is);
            
            master.setStockDtMapId(results.getStockDtMapId());
            
            String expected = master.toString();
            String actual = results.toString();
            System.out.println("Expected:" + expected);
            System.out.println("Actual:" + actual);
            
            assertEquals(expected, actual);
            
            dao.getInstance();
            dao.deleteAll();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
