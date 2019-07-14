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
                    //Check the first <td> of each <tr> and see if it matches one of the summary data fields
                    //If it does, get the plaintext of the second <td> and convert it as necessary
                    if(tds.get(0).text().trim().equals("Prev Close")){
                        String prevClose = tds.get(1).text();
                        summaryData.setPrevClosePrice(Utility.convertStringCurrency(Utility.isBlank(prevClose)?"0":prevClose));
                    }
                    else if (tds.get(0).text().trim().equals("Open")){
                        String open = tds.get(1).text();
                        summaryData.setOpenPrice(Utility.convertStringCurrency(Utility.isBlank(open)?"0":open));
                    }
                    else if (tds.get(0).text().trim().equals("Bid")){
                        //Investopedia does not currently track Bid Price, but if they ever do, this block can be used to scrape it
                        String bid = tds.get(1).text();
                        summaryData.setBidPrice(Utility.convertStringCurrency(Utility.isBlank(bid)?"0":bid));
                    }
                    else if (tds.get(0).text().trim().equals("Ask")){
                        //Investopedia does not currently track Ask Price, but if they ever do, this block can be used to scrape it
                        String ask = tds.get(1).text();
                        summaryData.setAskPrice(Utility.convertStringCurrency(Utility.isBlank(ask)?"0":ask));
                    }
                    else if (tds.get(0).text().trim().equals("Day's Range")){
                        //Ranges are split into range min and range max
                        String dayRangeMin = Utility.getRangeMinAndMax(tds.get(1).text())[0].trim();
                        String dayRangeMax = Utility.getRangeMinAndMax(tds.get(1).text())[1].trim();
                        summaryData.setDaysRangeMin(Utility.convertStringCurrency(Utility.isBlank(dayRangeMin)?"0":dayRangeMin));
                        summaryData.setDaysRangeMax(Utility.convertStringCurrency(Utility.isBlank(dayRangeMax)?"0":dayRangeMax));
                    }
                    else if (tds.get(0).text().trim().equals("52 Wk Range")){
                        //Ranges are split into range min and range max
                        String yearRangeMin = Utility.getRangeMinAndMax(tds.get(1).text())[0].trim();
                        String yearRangeMax = Utility.getRangeMinAndMax(tds.get(1).text())[1].trim();
                        summaryData.setFiftyTwoWeeksMin(Utility.convertStringCurrency(Utility.isBlank(yearRangeMin)?"0":yearRangeMin));
                        summaryData.setFiftyTwoWeeksMax(Utility.convertStringCurrency(Utility.isBlank(yearRangeMax)?"0":yearRangeMax));
                    }
                    else if (tds.get(0).text().trim().equals("Volume")){
                        String volume = tds.get(1).text();
                        summaryData.setVolume(Utility.convertStringCurrency(Utility.isBlank(volume)?"0":volume).longValue());
                    }
                    else if (tds.get(0).text().trim().equals("Avg Volume")){
                        //Investopedia does not currently track Average Volume, but if they ever do, this block can be used to scrape it
                        String avgVolume = tds.get(1).text();
                        summaryData.setVolume(Utility.convertStringCurrency(Utility.isBlank(avgVolume)?"0":avgVolume).longValue());
                    }
                    else if (tds.get(0).text().trim().equals("Market Cap")){
                        String marketCap = tds.get(1).text();
                        summaryData.setMarketCap(Utility.convertStringCurrency(Utility.isBlank(marketCap)?"0":marketCap));
                    }
                    else if (tds.get(0).text().trim().equals("Beta")){
                        String beta = tds.get(1).text();
                        summaryData.setBetaCoefficient(Utility.convertStringCurrency(Utility.isBlank(beta)?"0":beta));
                    }
                    else if (tds.get(0).text().trim().equals("P/E")){
                        String peRatio = tds.get(1).text();
                        summaryData.setPeRatio(Utility.convertStringCurrency(Utility.isBlank(peRatio)?"0":peRatio));
                    }
                    else if (tds.get(0).text().trim().equals("EPS")){
                        String eps = tds.get(1).text();
                        summaryData.setEps(Utility.convertStringCurrency(Utility.isBlank(eps)?"0":eps));
                    }
                    else if (tds.get(0).text().trim().equals("Earning Date")){
                        //Investopedia does not currently track Earning Date, but if they ever do, this block can be used to scrape it
                        String earningDate = tds.get(1).text();
                        //Earning Date should be scraped and set as a plain string.  Date conversion may be required in the long run.
                        summaryData.setEarningDate(earningDate);
                    }
                    else if (tds.get(0).text().trim().equals("Div & Yield")){
                        //Dividend needs to scrape just the numerator of the listed entry (denominator is the Yield % for future reference)
                        String dividend = Utility.getNumeratorAndDenominator(tds.get(1).text())[0].trim();
                        summaryData.setDividentYield(Utility.convertStringCurrency(Utility.isBlank(dividend)?"0":dividend));
                    }
                    else if (tds.get(0).text().trim().equals("Ex Dividend Date")){
                        //Investopedia does not currently track Ex Dividend Date, but if they ever do, this block can be used to scrape it
                        String exDividendDate = tds.get(1).text();
                        //Ex Dividend Date should be scraped and set as a plain string.  Date conversion may be required in the long run.
                        summaryData.setEarningDate(exDividendDate);
                    }
                    else if (tds.get(0).text().trim().equals("Target")){
                        /*
                         * MM: I cannot get this to scrape properly. Investopedia has a <tr> that contains a <td>       Target      </td>
                         * but it doesn't seem to work without the quotation marks that every other <td/> has.
                         */
                        String yearTarget = tds.get(1).text();
                        summaryData.setOneYearTargetEst(Utility.convertStringCurrency(Utility.isBlank(yearTarget)?"0":yearTarget));
                    }
                    //No else clause
                }
                
                
            }
            //Insert summaryData into the dao
            dao.insertStockSummaryData(summaryData);
        } catch (IOException ex) {
            Logger.getLogger(StockReporter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}