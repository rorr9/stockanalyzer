/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stockreporter.scrappers;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import stockreporter.Constants;
import stockreporter.StockReporter;
import stockreporter.Utility;
import stockreporter.daomodels.StockDateMap;
import stockreporter.daomodels.StockSummary;
import stockreporter.daomodels.StockTicker;

//Scraps MarketWatch Financial data 
public class MarketWatchScraper extends StockScraper {

    private boolean test = false;
    private Document document;
    private StockSummary summaryData;
    
 //Default constructor 
   
    public MarketWatchScraper(){
        super();
    }

    public void scrapeAllSummaryData() throws ParseException{
        for (StockTicker stockTicker: stockTickers)
            scrapeSingleSummaryData(stockTicker);
    }

    
    
     public void scrapeSingleSummaryData(StockTicker stockTicker) throws ParseException{ 
        System.out.println("Scrapping: "+stockTicker.getSymbol());
        String url = "https://www.marketwatch.com/investing/stock/"+stockTicker.getSymbol().toLowerCase();
        
        try {
            if(!test){
            Connection jsoupConn = Jsoup.connect(url);
            document = jsoupConn.referrer("http://www.google.com") .timeout(1000*20).get();
            }

            StockDateMap stockDateMap = new StockDateMap();
            stockDateMap.setSourceId(dao.getStockSourceIdByName(Constants.SCRAP_DATA_FROM_MARKETWATCH));
            stockDateMap.setTickerId(stockTicker.getId());
            stockDateMap.setDate(new SimpleDateFormat("MM-dd-yyyy").format(new Date()));
            int last_inserted_id = dao.insertStockDateMap(stockDateMap);;    
            summaryData = new StockSummary();           
            summaryData.setStockDtMapId(last_inserted_id);
            
// Elements marketWatchdiv=document.select("div.element.element--list");
        Elements list = document.select("ul.list.list--kv.list--col50 li");
        String openPrice = list.select("li.kv__item:nth-of-type(1) > .kv__primary.kv__value").text();
        System.out.println(openPrice);

//Getting closing stock price
        Elements row = document.select("table.table--primary.align--right tr");
        String closingPrice = row.select(".u-semi.table__cell").text();
        
//Converting string closing stock price to double value
        NumberFormat format = NumberFormat.getCurrencyInstance();
        Number number = format.parse(closingPrice);
        double dValueOfClosingPrice = number.doubleValue();

//Getting the stock change
        Elements row1 = document.select("table.table--primary.align--right tr");
        String change = row1.select("td.positive.not-fixed.table__cell:nth-of-type(2)").text();

//  Previous day stock closing price is closing stock price minus the difference in stock (change)
        Double doublePrevClosePrice = dValueOfClosingPrice - Double.parseDouble(change);
        String prevClosePrice=Double.toString(doublePrevClosePrice); 
        summaryData.setPrevClosePrice(Utility.convertStringCurrency(Utility.isBlank(prevClosePrice)?"0":prevClosePrice));
        
// Getting daily max and min stock string values
        String daysRangeMaxAndMin = list.select("li.kv__item:nth-of-type(2) > .kv__primary.kv__value").text();

//Splitting daily range max and min stock value
        String[] arrayOfDailySplit = daysRangeMaxAndMin.split("-");

//Days range minimum value
        String daysRangeMin = arrayOfDailySplit[0];
        summaryData.setDaysRangeMin(Utility.convertStringCurrency(Utility.isBlank(daysRangeMin)?"0":daysRangeMin));

//Days range maximum value
        String daysRangeMax = arrayOfDailySplit[1];
        summaryData.setDaysRangeMax(Utility.convertStringCurrency(Utility.isBlank(daysRangeMax)?"0":daysRangeMax)); 
        
//Getting 52 week max and min stock value
        String fiftyTwoWeekRangeMaxAndMin = list.select("li.kv__item:nth-of-type(3) > .kv__primary.kv__value").text();

//Splitting 52 week max and min stock value
        String[] arrayOfFiftyTwoWeekSplit = fiftyTwoWeekRangeMaxAndMin.split("-");

//Days range minimum value
        String fiftyTwoWeeksMin = arrayOfFiftyTwoWeekSplit[0];
        summaryData.setFiftyTwoWeeksMin(Utility.convertStringCurrency(Utility.isBlank(fiftyTwoWeeksMin)?"0":fiftyTwoWeeksMin));
        
//Days range maximum value
        String fiftyTwoWeeksMax = arrayOfFiftyTwoWeekSplit[1];
        summaryData.setFiftyTwoWeeksMin(Utility.convertStringCurrency(Utility.isBlank(fiftyTwoWeeksMax)?"0":fiftyTwoWeeksMax));
        
//Volume
        Elements div = document.select("div.range__details span");
        String volume = div.select(".last-value.volume").text();
        summaryData.setVolume(Utility.convertStringCurrency(Utility.isBlank(volume)?"0":volume).longValue());
        
//Average Volume
        String avgVolume = list.select("li.kv__item:nth-of-type(16) > .kv__primary.kv__value").text();
        summaryData.setAvgVolume(Utility.convertStringCurrency(Utility.isBlank(avgVolume)?"0":avgVolume).longValue());
        
//Market Cap
        String marketCap = list.select("li.kv__item:nth-of-type(4) > .kv__primary.kv__value").text();
        summaryData.setMarketCap(Utility.convertStringCurrency(Utility.isBlank(marketCap)?"0":marketCap));
        
//Beta Coefficient
        String betaCoefficient = list.select("li.kv__item:nth-of-type(7) > .kv__primary.kv__value").text();
        summaryData.setBetaCoefficient(Utility.convertStringCurrency(Utility.isBlank(betaCoefficient)?"0":betaCoefficient));
        
//P/E Ratio

        String peRatio = list.select("li.kv__item:nth-of-type(9) > .kv__primary.kv__value").text();
        summaryData.setPeRatio(Utility.convertStringCurrency(Utility.isBlank(peRatio)?"0":peRatio));
        
//EPS
        String eps = list.select("li.kv__item:nth-of-type(10) > .kv__primary.kv__value").text();
        summaryData.setEps(Utility.convertStringCurrency(Utility.isBlank(eps)?"0":eps));
//Dividend

        String dividend = list.select("li.kv__item:nth-of-type(12) > .is-na.kv__primary.kv__value").text();
        summaryData.setDividentYield(Utility.convertStringCurrency(Utility.isBlank(dividend)?"0":dividend));

//EX-DIVIDEND DATE
        String exDividendDate = list.select("li.kv__item:nth-of-type(13) > .is-na.kv__primary.kv__value").text();
        summaryData.setExDividentDate(exDividendDate);
                 
 
  } catch (IOException ex) {
            Logger.getLogger(StockReporter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
         
     }
    
    

