/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stockreporter.scrapers;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import stockreporter.Constants;
import stockreporter.StockDao;
import stockreporter.StockReporter;
import stockreporter.Utility;
import stockreporter.daomodels.StockDateMap;
import stockreporter.daomodels.StockSummary;
import stockreporter.daomodels.StockTicker;

//Scraps MarketWatch Financial data
public class MarketWatchScraper implements Scraper {

    private boolean test = false;
    private Document document;
    private StockSummary summaryData;
    private StockDao dao;
    private List<StockTicker> stockTickers;

    //Default constructor
    public MarketWatchScraper() {
        dao = StockDao.getInstance();
        stockTickers = dao.getAllstockTickers();
    }

    @Override
    public void scrapeAllSummaryData() {
        for (StockTicker stockTicker : stockTickers) {
            scrapeSingleSummaryData(stockTicker);
        }
    }

    public void scrapeSingleSummaryData(StockTicker stockTicker) {
        System.out.println("Scrapping: " + stockTicker.getSymbol());
        String url = "https://www.marketwatch.com/investing/stock/" + stockTicker.getSymbol().toLowerCase();

        try {
            if (!test) {
                Connection jsoupConn = Jsoup.connect(url);
                document = jsoupConn.referrer("http://www.google.com").timeout(1000 * 20).get();
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
            String openPrice = list.select("li.kv__item:nth-of-type(1) > .kv__primary.kv__value").text().substring(1).trim();
            summaryData.setOpenPrice(Utility.convertStringCurrency(Utility.isBlank(openPrice) ? "0" : openPrice));
            
//Getting closing stock price
            Elements row = document.select("table.table--primary.align--right tr");
            String prevClosingPrice = row.select(".u-semi.table__cell").text().substring(1).trim();
            summaryData.setPrevClosePrice(Utility.convertStringCurrency(Utility.isBlank(prevClosingPrice) ? "0" : prevClosingPrice));

// Getting daily max and min stock string values
            String daysRangeMaxAndMin = list.select("li.kv__item:nth-of-type(2) > .kv__primary.kv__value").text();

//Splitting daily range max and min stock value
            String[] arrayOfDailySplit = daysRangeMaxAndMin.split("-");

//Days range minimum value
            String daysRangeMin = arrayOfDailySplit[0].trim();
            summaryData.setDaysRangeMin(Utility.convertStringCurrency(Utility.isBlank(daysRangeMin) ? "0" : daysRangeMin));

//Days range maximum value
            String daysRangeMax = arrayOfDailySplit[1].trim();
            summaryData.setDaysRangeMax(Utility.convertStringCurrency(Utility.isBlank(daysRangeMax) ? "0" : daysRangeMax));

//Getting 52 week max and min stock value
            String fiftyTwoWeekRangeMaxAndMin = list.select("li.kv__item:nth-of-type(3) > .kv__primary.kv__value").text();

//Splitting 52 week max and min stock value
            String[] arrayOfFiftyTwoWeekSplit = fiftyTwoWeekRangeMaxAndMin.split("-");

//Days range minimum value
            String fiftyTwoWeeksMin = arrayOfFiftyTwoWeekSplit[0].trim();
            summaryData.setFiftyTwoWeeksMin(Utility.convertStringCurrency(Utility.isBlank(fiftyTwoWeeksMin) ? "0" : fiftyTwoWeeksMin));

//Days range maximum value
            String fiftyTwoWeeksMax = arrayOfFiftyTwoWeekSplit[1].trim();
            summaryData.setFiftyTwoWeeksMin(Utility.convertStringCurrency(Utility.isBlank(fiftyTwoWeeksMax) ? "0" : fiftyTwoWeeksMax));

//Volume
            Elements div = document.select("div.range__details span");
            String volume = div.select("span.last-value.volume").text();
            summaryData.setVolume(Utility.convertStringCurrency(Utility.isBlank(volume) ? "0" : volume).longValue());

//Average Volume
            String avgVolume = list.select("li.kv__item:nth-of-type(16) > .kv__primary.kv__value").text();
            summaryData.setAvgVolume(Utility.convertStringCurrency(Utility.isBlank(avgVolume) ? "0" : avgVolume).longValue());

//Market Cap
            String marketCap = list.select("li.kv__item:nth-of-type(4) > .kv__primary.kv__value").text().substring(1).trim();
            summaryData.setMarketCap(Utility.convertStringCurrency(Utility.isBlank(marketCap) ? "0" : marketCap));

//Beta Coefficient
            String betaCoefficient = list.select("li.kv__item:nth-of-type(7) > .kv__primary.kv__value").text();
            summaryData.setBetaCoefficient(Utility.convertStringCurrency(Utility.isBlank(betaCoefficient) ? "0" : betaCoefficient));

//P/E Ratio
            String peRatio = list.select("li.kv__item:nth-of-type(9) > .kv__primary.kv__value").text();
            if (peRatio.matches("[^0-9]+$")) {
                 peRatio = "0";
            }
            summaryData.setPeRatio(Utility.convertStringCurrency(Utility.isBlank(peRatio) ? "0" : peRatio));

//EPS
            String eps = list.select("li.kv__item:nth-of-type(10) > .kv__primary.kv__value").text().substring(1).trim();
            summaryData.setEps(Utility.convertStringCurrency(Utility.isBlank(eps) ? "0" : eps));
//Dividend

            String dividend = list.select("li.kv__item:nth-of-type(12) > .kv__primary.kv__value").text().substring(1).trim();
            if (dividend.matches("[^0-9]+$")) {
                 dividend = "0";
            }
            summaryData.setDividentYield(Utility.convertStringCurrency(Utility.isBlank(dividend) ? "0" : dividend));

//EX-DIVIDEND DATE
            String exDividendDate = list.select("li.kv__item:nth-of-type(13) > .is-na.kv__primary.kv__value").text();
            summaryData.setExDividentDate(exDividendDate);

        } catch (IOException ex) {
            Logger.getLogger(StockReporter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public StockSummary getSummaryData() {
        return summaryData;
    }

}
