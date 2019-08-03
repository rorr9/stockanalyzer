/*
 * AS OF 8/2/19 INVESTOPEDIA'S LAYOUT HAS CHANGED
 * DO NOT USE THIS TEST.  IT HAS BEEN COMMENTED OUT
 * OF THE TEST SUITE.
 */
package stockreporter.scrapers;

import java.io.File;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.lang.reflect.Field;
import org.junit.Test;
import static org.junit.Assert.*;
import stockreporter.StockDao;
import stockreporter.Utility;
import stockreporter.daomodels.StockTicker;
import stockreporter.daomodels.StockSummary;

/**
 * Test of InvestopediaScraper class's scraping functionality using manually-inputted 
 * expected values.
 * 
 * You must input expected values from https://www.investopedia.com/markets/stocks/msft
 * before running the test.
 */
public class InvestopediaScraperLiveDataTest {
    private StockDao dao;
    private StockTicker stockTicker = new StockTicker();
    private StockSummary master = new StockSummary();
    
    public InvestopediaScraperLiveDataTest() {
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
            
            stockTicker.setId(1);
            stockTicker.setName("Microsoft Inc.");
            stockTicker.setSymbol("MSFT");
            
            /*
            * IMPORTANT: Before running this test, open a web browser to
            * https://www.investopedia.com/markets/stocks/msft and change the 
            * values below to today's values, as they appear in the sample data 
            * below. 
            * You may copy and paste from the website, as long as you keep
            * the quotation marks as they are.
            * 
            * The test WILL fail otherwise.
            */
            //Prev Close
            master.setPrevClosePrice(Utility.convertStringCurrency("136.62"));
            //Open
            master.setOpenPrice(Utility.convertStringCurrency("137.33"));
            //Day's Range (first value)
            master.setDaysRangeMin(Utility.convertStringCurrency("137.3")); //The number before the hyphen
            //Day's Range (second value)
            master.setDaysRangeMax(Utility.convertStringCurrency("139.19")); //The number after the hyphen
            //52 Wk Range (first value)
            master.setFiftyTwoWeeksMin(Utility.convertStringCurrency("93.96")); //The number before the hyphen
            //52 Wk Range (second value)
            master.setFiftyTwoWeeksMax(Utility.convertStringCurrency("140.67")); //The number after the hyphen
            //P/E
            master.setPeRatio(Utility.convertStringCurrency("26.95"));
            //Beta
            master.setBetaCoefficient(Utility.convertStringCurrency("1.377"));
            //Volume
            master.setVolume(Utility.convertStringCurrency("18,306,281.00").longValue()); //Include commas and a x.00 trailing zero
            //Div & Yield
            master.setDividentYield(Utility.convertStringCurrency("1.84/1.35%")); //As it appears
            //Market Cap
            master.setMarketCap(Utility.convertStringCurrency("1.06T")); //Include any letter after the number
            //EPS
            master.setEps(Utility.convertStringCurrency("5.07"));
            /*
            * That's all the values you need to enter.  You can now 
            * run the test.
            */
            
            master.setSummaryId(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Test of scapeSingleSummaryData method, of class YahooScraper.
     */
    @Test
    public void testScapeSingleSummaryData() {
        System.out.println("IMPORTANT: Make sure the expected values are updated \n" +
                           "to today's Investopedia values before running this test.");
        try{
            InvestopediaScraper investo = new InvestopediaScraper();
            
            investo.scrapeSingleSummaryData(stockTicker);
            
            StockSummary results = (StockSummary)investo.getSummaryData();
            
            //This isn't scraped so don't care about the vaule since it is a DB thing.
            master.setStockDtMapId(results.getStockDtMapId());
            
            String expected = master.toString();
            String actual = results.toString();
            System.out.println("Expected:" + expected);
            System.out.println("Actual:  " + actual);
            
            assertEquals(expected, actual);
            
            //truncate the data after test
            dao.getInstance();
            dao.deleteAll();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    
}
