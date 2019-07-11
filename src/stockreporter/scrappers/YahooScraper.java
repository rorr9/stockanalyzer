/*
 * Stock Analyzer Project | YahooScraper.java
 */
package stockreporter.scrappers;

import java.io.IOException;
import java.math.BigDecimal;
import stockreporter.daomodels.StockSummary;
import stockreporter.daomodels.StockTicker;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import stockreporter.Constants;
import stockreporter.StockReporter;
import stockreporter.Utility;
import stockreporter.daomodels.StockDateMap;


/**
 * This class scrapes pages for each tracked stock ticker symbol from
 * Yahoo Finance  
 * 
 * Yahoo Finance creates a page for each ticker symbol
 * with the url "http://finance.yahoo.com/quote/[symbol]"  Each page
 * is populated with a large table that carries each data point in a
 * different <td> tag
 * 
 * This class iterates through all <td> tags, compares their attributes
 * to the list of required stock data, and stores their plaintext contents
 * in the matching StockSummary parameter.
 */
public class YahooScraper extends StockScraper {
    //Top-level object references
    private Document document;
    private StockSummary summaryData;
    
    //Default Constructor, will be refactored after ScraperFactory is implemented
    public YahooScraper(){
        super();
    }
    
    /**
     * Scrape summary data for each StockTicker in the superclass's List<StockTicker>
     */
    public void scrapeAllSummaryData(){
        for(StockTicker stockTicker: stockTickers)
            scrapeSingleSummaryData(stockTicker);
    }
    
    /**
     * Scrape summary data by stock ticker
     * @param stockTicker 
     */
    public void scrapeSingleSummaryData(StockTicker stockTicker){     
        System.out.println("Scraping: "+stockTicker.getSymbol());
        String url = "https://finance.yahoo.com/quote/"+stockTicker.getSymbol().toLowerCase();
        try {
            Connection jsoupConn = Jsoup.connect(url);
            document = jsoupConn.referrer("http://www.google.com") .timeout(1000*20).get();

            StockDateMap stockDateMap = new StockDateMap();
            stockDateMap.setSourceId(dao.getStockSourceIdByName(Constants.SCRAP_DATA_FROM_YAHOO));
            stockDateMap.setTickerId(stockTicker.getId());
            stockDateMap.setDate(new SimpleDateFormat("MM-dd-yyyy").format(new Date()));
            int last_inserted_id = dao.insertStockDateMap(stockDateMap);
        
            summaryData = new StockSummary();
            
            summaryData.setStockDtMapId(last_inserted_id);
            
            //Scrape all <td> tags from the document
            Elements yahooTDs = document.select("td");

            //Scan each <td> for attribute "data-test" and record its text if it matches various
            //price data fields
            for(Element td: yahooTDs){
                //Check that each td has an attribute named "data-test"
                if (td.hasAttr("data-test")){
                    //Create a map of the td's data-set attributes <AttributeTag, Value>
                    Map<String, String> tdData = td.dataset();
                    //Get the plain-text from each td
                    String tdText = td.text();
                    
                    /* Stuff to refactor the Utility usage
                    //Declare three BigDecimals for a single amount, range minimum, and range maximum
                    BigDecimal tdAmount = Utility.convertStringCurrency("0");
                    BigDecimal tdMin = Utility.convertStringCurrency("0");
                    BigDecimal tdMax = Utility.convertStringCurrency("0");
                    
                    if (!(tdText.contains(","))) {
                        //Check if tdText is a Range (contains a hyphen)
                        if(tdText.contains("-")){
                            //Use Utility.getRangeMinAndMax to break tdText into two Strings
                            String rangeMin = Utility.getRangeMinAndMax(tdText)[0].trim();
                            String rangeMax = Utility.getRangeMinAndMax(tdText)[1].trim();
                            //Convert those Strings to currency format
                            tdMin = Utility.convertStringCurrency(Utility.isBlank(rangeMin)?"0":rangeMin);
                            tdMax = Utility.convertStringCurrency(Utility.isBlank(rangeMax)?"0":rangeMax);
                        //Else, check if tdText has a parenthesis, and get only the text before it
                        } else if (tdText.contains("(")){
                            String dividend = Utility.getNumberBeforeParantheses(tdText).trim();
                            //Convert that text to currency format
                            tdAmount = Utility.convertStringCurrency(Utility.isBlank(dividend)?"0":dividend);
                        //Else it's a normal number and can be converted to currency format
                        }else {
                            tdAmount = Utility.convertStringCurrency(Utility.isBlank(tdText)?"0":tdText);
                        }
                    }
                    */ 
                    
                    String rangeMin;
                    String rangeMax;
                    
                    //Case statement for each data field (dataset() stores each attribute minus its HTML5 "data-" prefix
                    //so this switch just uses Map.get("test"))
                    switch(tdData.get("test")){
                        //Cases should hold up regardless of Yahoo table order
                        case "PREV_CLOSE-value" :
                            //summaryData.setPrevClosePrice(tdAmount);
                            summaryData.setPrevClosePrice(Utility.convertStringCurrency(Utility.isBlank(tdText)?"0":tdText));
                            break;
                        case "OPEN-value" :
                            //summaryData.setOpenPrice(tdAmount);
                            summaryData.setOpenPrice(Utility.convertStringCurrency(Utility.isBlank(tdText)?"0":tdText));
                            break;
                        case "BID-value" :
                            //summaryData.setBidPrice(tdAmount);
                            summaryData.setBidPrice(Utility.convertStringCurrency(Utility.isBlank(tdText)?"0":tdText));
                            break;
                        case "ASK-value" :
                            //summaryData.setAskPrice(tdAmount);
                            summaryData.setAskPrice(Utility.convertStringCurrency(Utility.isBlank(tdText)?"0":tdText));
                            break;
                        case "DAYS_RANGE-value" :
                            //summaryData.setDaysRangeMin(tdMin);
                            //summaryData.setDaysRangeMax(tdMax);
                            rangeMin = Utility.getRangeMinAndMax(tdText)[0].trim();
                            rangeMax = Utility.getRangeMinAndMax(tdText)[1].trim();
                            summaryData.setDaysRangeMin(Utility.convertStringCurrency(Utility.isBlank(rangeMin)?"0":rangeMin));
                            summaryData.setDaysRangeMax(Utility.convertStringCurrency(Utility.isBlank(rangeMax)?"0":rangeMax));
                            break;
                        case "FIFTY_TWO_WK_RANGE-value" :
                            //summaryData.setFiftyTwoWeeksMin(tdMin);
                            //summaryData.setFiftyTwoWeeksMax(tdMax);
                            rangeMin = Utility.getRangeMinAndMax(tdText)[0].trim();
                            rangeMax = Utility.getRangeMinAndMax(tdText)[1].trim();
                            summaryData.setFiftyTwoWeeksMin(Utility.convertStringCurrency(Utility.isBlank(rangeMin)?"0":rangeMin));
                            summaryData.setFiftyTwoWeeksMax(Utility.convertStringCurrency(Utility.isBlank(rangeMax)?"0":rangeMax));
                            break;
                        case "TD_VOLUME-value" :
                            //This uses long instead of BigDecimal
                            //summaryData.setVolume(tdAmount.longValue());
                            summaryData.setVolume(Utility.convertStringCurrency(Utility.isBlank(tdText)?"0":tdText).longValue());
                            break;
                        case "AVERAGE_VOLUME_3MONTH-value" :
                            //This uses long instead of BigDecimal
                            //summaryData.setAvgVolume(tdAmount.longValue());
                            summaryData.setAvgVolume(Utility.convertStringCurrency(Utility.isBlank(tdText)?"0":tdText).longValue());
                            break;
                        case "MARKET_CAP-value" :
                            //summaryData.setMarketCap(tdAmount);
                            summaryData.setMarketCap(Utility.convertStringCurrency(Utility.isBlank(tdText)?"0":tdText));
                            break;
                        case "BETA_3Y-value" :
                            //summaryData.setBetaCoefficient(tdAmount);
                            summaryData.setBetaCoefficient(Utility.convertStringCurrency(Utility.isBlank(tdText)?"0":tdText));
                            break;
                        case "PE_RATIO-value" :
                            //summaryData.setPeRatio(tdAmount);
                            summaryData.setPeRatio(Utility.convertStringCurrency(Utility.isBlank(tdText)?"0":tdText));
                            break;
                        case "EPS_RATIO-value" :
                            //summaryData.setEps(tdAmount);
                            summaryData.setEps(Utility.convertStringCurrency(Utility.isBlank(tdText)?"0":tdText));
                            break;
                        case "EARNINGS_DATE-value" :
                            //This uses the plaintext tdText, not a converted value
                            summaryData.setEarningDate(tdText);
                            break;
                        case "DIVIDEND_AND_YIELD-value" :
                            //summaryData.setDividentYield(tdAmount);
                            String dividend = Utility.getNumberBeforeParantheses(tdText).trim();
                            summaryData.setDividentYield(Utility.convertStringCurrency(Utility.isBlank(dividend)?"0":dividend));
                            break;
                        case "EX_DIVIDEND_DATE-value" :
                            //This uses the plaintext tdText, not a converted value
                            summaryData.setExDividentDate(tdText);
                            break;
                        case "ONE_YEAR_TARGET_PRICE-value" :
                            //summaryData.setOneYearTargetEst(tdAmount);
                            summaryData.setOneYearTargetEst(Utility.convertStringCurrency(Utility.isBlank(tdText)?"0":tdText));
                            break;
                        default:
                            break;
                    }
                }
            }
            dao.insertStockSummaryData(summaryData);
            
        } catch (IOException ex) {
            //Generic log message for failed Jsoup connection
            Logger.getLogger(StockReporter.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (NullPointerException ex){
            //Yahoo can and will make a blank page if given the URL of an invalid stock ticker symbol
            //This exception will be thrown by the scraper if it tries to scrape a blank page
            Logger.getLogger(StockReporter.class.getName()).log(Level.SEVERE, "Empty Page Found for given symbol: " + stockTicker.getSymbol(), ex);
        }
    }
}