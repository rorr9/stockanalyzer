/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 * Test of YahooScraper class's scraping functionality using manually-inputted 
 * expected values.
 * 
 * You must input expected values from http://finance.yahoo.com/quote/MSFT
 * before running the test.
 */
public class YahooScraperLiveDataTest {
    private StockDao dao;
    private StockTicker stockTicker = new StockTicker();
    private StockSummary master = new StockSummary();
    
    public YahooScraperLiveDataTest() {
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
            * http://finance.yahoo.com/quote/MSFT and change the values below
            * to today's values, as they appear in the sample data below. 
            * You may copy and paste from the website, as long as you keep
            * the quotation marks as they are.
            * 
            * The test WILL fail otherwise.
            */
            //Previous Close
            master.setPrevClosePrice(Utility.convertStringCurrency("138.06"));
            //Open
            master.setOpenPrice(Utility.convertStringCurrency("138.09"));
            //Bid
            master.setBidPrice(Utility.convertStringCurrency("136.90")); //Ignore the x and everything after it
            //Ask
            master.setAskPrice(Utility.convertStringCurrency("136.95")); //Ignore the x and everything after it
            //Day's Range (first value)
            master.setDaysRangeMin(Utility.convertStringCurrency("135.26")); //The number before the hyphen
            //Day's Range (second value)
            master.setDaysRangeMax(Utility.convertStringCurrency("138.32")); //The number after the hyphen
            //52 Week Range (first value)
            master.setFiftyTwoWeeksMin(Utility.convertStringCurrency("93.96")); //The number before the hyphen
            //52 Week Range (second value)
            master.setFiftyTwoWeeksMax(Utility.convertStringCurrency("141.68")); //The number after the hyphen
            //Volume
            master.setVolume(Utility.convertStringCurrency("30,791,624.00").longValue()); //Include commas and a x.00 trailing zero
            //Avg. Volume
            master.setAvgVolume(Utility.convertStringCurrency("24,323,060.00").longValue()); //Include commas and a x.00 trailing zero
            //Market Cap
            master.setMarketCap(Utility.convertStringCurrency("1.046T")); //Include any letter after the number
            //Beta (3Y Monthly)
            master.setBetaCoefficient(Utility.convertStringCurrency("1.02"));
            //PE Ratio (TTM)
            master.setPeRatio(Utility.convertStringCurrency("27.06"));
            //EPS (TTM)
            master.setEps(Utility.convertStringCurrency("5.06"));
            //Earnings Date
            master.setEarningDate("Oct 22, 2019 - Oct 28, 2019"); //Exactly as it appears
            //Forward Dividend & Yield
            master.setDividentYield(Utility.convertStringCurrency("1.84/1.33%")); //Replace the parentheses with a /
            //Ex-Dividend Date
            master.setExDividentDate("2019-08-14"); //Exactly as it appears
            //1y Target Est
            master.setOneYearTargetEst(Utility.convertStringCurrency("154.41"));
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
                           "to today's Yahoo values before running this test.");
        try{
            YahooScraper yahoo = new YahooScraper();
            
            yahoo.scrapeSingleSummaryData(stockTicker);
            
            StockSummary results = (StockSummary)yahoo.getSummaryData();
            
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
