    
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
 *
 * @author klacayo
 */
public class YahooScraperTest {
    private StockDao dao;
    
    private StockTicker stockTicker = new StockTicker();
    private StockSummary master = new StockSummary();
    
    
    public YahooScraperTest() {
    
    
    
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
            stockTicker.setName("Apple Inc.");
            stockTicker.setSymbol("AAPL");
            
            master.setPrevClosePrice(Utility.convertStringCurrency("195.35"));
            master.setBetaCoefficient(Utility.convertStringCurrency("0.99"));
            master.setDaysRangeMax(Utility.convertStringCurrency("196.36"));
            master.setDaysRangeMin(Utility.convertStringCurrency("194.71"));
            master.setDividentYield(Utility.convertStringCurrency("2.92/1.49%"));
            master.setEps(Utility.convertStringCurrency("12.12"));
            master.setFiftyTwoWeeksMax(Utility.convertStringCurrency("233.47"));
            master.setFiftyTwoWeeksMin(Utility.convertStringCurrency("142.00"));
            master.setMarketCap(Utility.convertStringCurrency("922.733B"));
            master.setOpenPrice(Utility.convertStringCurrency("194.79"));
            master.setPeRatio(Utility.convertStringCurrency("16.14"));
            master.setSummaryId(0);
            master.setVolume(Utility.convertStringCurrency("18,747,318.00").longValue());
            master.setBidPrice(Utility.convertStringCurrency("195.43"));
            master.setAskPrice(Utility.convertStringCurrency("195.56"));
            master.setAvgVolume(30598950);
            master.setEarningDate("Apr 30, 2019");
            master.setExDividentDate("2019-02-08");
            master.setOneYearTargetEst(Utility.convertStringCurrency("190.94"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
   

    /**
     * Test of scapeSingleSummaryData method, of class YahooScraper.
     */
    @Test
    public void testScapeSingleSummaryData() {
                try{
            File input = new File("Apr_4_2019_AAPL_Yahoo.html");
            Document tmpDocument = Jsoup.parse(input, "UTF-8", "");
            
            YahooScraper ys = new YahooScraper();
            Class isClass = ys.getClass();

            Field f1 = isClass.getDeclaredField("test");
            f1.setAccessible(true);
            f1.set(ys, true);
            
            Field f2 = isClass.getDeclaredField("document");
            f2.setAccessible(true);
            f2.set(ys, tmpDocument);
            
            ys.scrapeSingleSummaryData(stockTicker);
            
            Field f3 = isClass.getDeclaredField("summaryData");
            f3.setAccessible(true);
            StockSummary results = (StockSummary)f3.get(ys);
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
