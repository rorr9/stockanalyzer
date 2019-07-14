/*
 * Stock Analyzer Project | InvestopediaScraper.java
 */
package stockreporter.scrappers;

import java.io.IOException;
import stockreporter.daomodels.StockSummary;
import stockreporter.daomodels.StockTicker;
import java.text.SimpleDateFormat;
import java.util.Date;
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
 * Investopedia's market pages.  
 * 
 * Investopedia creates a page for each ticker symbol
 * with the url "https://www.investopedia.com/markets/stocks/[symbol]"  Each page
 * is populated with several tables that carry each data point in a
 * different <tr> tag
 * 
 * This class iterates through all <tr> tags, compares their children's attributes
 * to the list of required stock data, and stores their plaintext contents
 * in the matching StockSummary parameter, converting as necessary.
 */

public class InvestopediaScraper extends StockScraper {
    //Top-level object references
    private Document document;
    private StockSummary summaryData;
    
    //Default Constructor, will be refactored after ScraperFactory is implemented
    public InvestopediaScraper(){
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
     * Scrap summary data by stock ticker
     * @param stockTicker 
     */
    public void scrapeSingleSummaryData(StockTicker stockTicker){        
        System.out.println("Scraping: "+stockTicker.getSymbol());
        String url = "https://www.investopedia.com/markets/stocks/"+stockTicker.getSymbol().toLowerCase();
        try {
            Connection jsoupConn = Jsoup.connect(url);
            document = jsoupConn.referrer("http://www.google.com") .timeout(1000*10).get();
            StockDateMap stockDateMap = new StockDateMap();
            stockDateMap.setSourceId(dao.getStockSourceIdByName(Constants.SCRAP_DATA_FROM_INVESTOPEDIA));
            stockDateMap.setTickerId(stockTicker.getId());
            stockDateMap.setDate(new SimpleDateFormat("MM-dd-yyyy").format(new Date()));
            int last_inserted_id = dao.insertStockDateMap(stockDateMap);
        
            summaryData = new StockSummary();
            
            summaryData.setStockDtMapId(last_inserted_id);
            
            //Get all <tr> tags on the page
            Elements investopediaTRs = document.select("tr");
            
            //For each <tr>:
            for(Element tr: investopediaTRs){
                //Get all <td> tags within each <tr>
                Elements tds = tr.select("td");
                
                //Check to make sure there are actual <td> elements in the new selection
                if(!tds.isEmpty()){
                    //Only scrape the text if there are exactly two <td>s in the <tr> (Label and Value)
                    if (tds.size()==2){
                        //Trim the first <td> to eliminate any whitespace
                        String firstTD = tds.get(0).text().trim();
                        //Second <td> should not need trimming
                        String secondTD = tds.get(1).text();
                        //Switch firstTD and see if it matches one of the summary data field cases
                        //If it does, convert secondTD as needed and add it to summaryData
                        switch (firstTD) {
                            case "Prev Close":
                                summaryData.setPrevClosePrice(Utility.convertStringCurrency(Utility.isBlank(secondTD)?"0":secondTD));
                                break;
                            case "Open":
                                summaryData.setOpenPrice(Utility.convertStringCurrency(Utility.isBlank(secondTD)?"0":secondTD));
                                break;
                            case "Bid":
                                //Investopedia does not currently track Bid Price, but if they ever do, this block can be used to scrape it
                                summaryData.setBidPrice(Utility.convertStringCurrency(Utility.isBlank(secondTD)?"0":secondTD));
                                break;
                            case "Ask":
                                //Investopedia does not currently track Ask Price, but if they ever do, this block can be used to scrape it
                                summaryData.setAskPrice(Utility.convertStringCurrency(Utility.isBlank(secondTD)?"0":secondTD));
                                break;
                            case "Day's Range":
                                //Ranges are split into range min and range max
                                String dayRangeMin = Utility.getRangeMinAndMax(secondTD)[0].trim();
                                String dayRangeMax = Utility.getRangeMinAndMax(secondTD)[1].trim();
                                summaryData.setDaysRangeMin(Utility.convertStringCurrency(Utility.isBlank(dayRangeMin)?"0":dayRangeMin));
                                summaryData.setDaysRangeMax(Utility.convertStringCurrency(Utility.isBlank(dayRangeMax)?"0":dayRangeMax));
                                break;
                            case "52 Wk Range":
                                //Ranges are split into range min and range max
                                String yearRangeMin = Utility.getRangeMinAndMax(secondTD)[0].trim();
                                String yearRangeMax = Utility.getRangeMinAndMax(secondTD)[1].trim();
                                summaryData.setFiftyTwoWeeksMin(Utility.convertStringCurrency(Utility.isBlank(yearRangeMin)?"0":yearRangeMin));
                                summaryData.setFiftyTwoWeeksMax(Utility.convertStringCurrency(Utility.isBlank(yearRangeMax)?"0":yearRangeMax));
                                break;
                            case "Volume":
                                //summaryData.setVolume takes a long-typed value
                                summaryData.setVolume(Utility.convertStringCurrency(Utility.isBlank(secondTD)?"0":secondTD).longValue());
                                break;
                            case "Avg Volume":
                                //Investopedia does not currently track Average Volume, but if they ever do, this block can be used to scrape it
                                //summaryData.setAvgVolume takes a long-typed value
                                summaryData.setAvgVolume(Utility.convertStringCurrency(Utility.isBlank(secondTD)?"0":secondTD).longValue());
                                break;
                            case "Market Cap":
                                summaryData.setMarketCap(Utility.convertStringCurrency(Utility.isBlank(secondTD)?"0":secondTD));
                                break;
                            case "Beta":
                                summaryData.setBetaCoefficient(Utility.convertStringCurrency(Utility.isBlank(secondTD)?"0":secondTD));
                                break;
                            case "P/E":
                                summaryData.setPeRatio(Utility.convertStringCurrency(Utility.isBlank(secondTD)?"0":secondTD));
                                break;
                            case "EPS":
                                summaryData.setEps(Utility.convertStringCurrency(Utility.isBlank(secondTD)?"0":secondTD));
                                break;
                            case "Earning Date":
                                //Investopedia does not currently track Earning Date, but if they ever do, this block can be used to scrape it
                                //Earning Date should be scraped and set as a plain string.  Date conversion may be required in the long run.
                                summaryData.setEarningDate(secondTD);
                                break;
                            case "Div & Yield":
                                //Dividend needs to scrape just the numerator of the listed entry (denominator is the Yield % for future reference)
                                String dividend = Utility.getNumeratorAndDenominator(secondTD)[0].trim();
                                summaryData.setDividentYield(Utility.convertStringCurrency(Utility.isBlank(dividend)?"0":dividend));
                                break;
                            case "Ex Dividend Date":
                                //Investopedia does not currently track Ex Dividend Date, but if they ever do, this block can be used to scrape it
                                //Ex Dividend Date should be scraped and set as a plain string.  Date conversion may be required in the long run.
                                summaryData.setEarningDate(secondTD);
                                break;
                            case "Target":
                                /*
                                * MM: I cannot get this to scrape properly. Investopedia has a <tr> that contains a <td>       Target      </td>
                                * but it doesn't seem to work without the quotation marks that every other <td/> has.
                                */
                                summaryData.setOneYearTargetEst(Utility.convertStringCurrency(Utility.isBlank(secondTD)?"0":secondTD));
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
            //Insert summaryData into the dao
            dao.insertStockSummaryData(summaryData);
        } catch (IOException ex) {
            Logger.getLogger(StockReporter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}