/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stockreporter.scrapers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import stockreporter.daomodels.StockSummary;
import stockreporter.daomodels.StockTicker;
import stockreporter.service.StockService;

/**
 * Scrap stock financial data from investopedia
 */
public class InvestopediaScraper implements Scraper {

    /**
     * default constructor
     */
    private boolean test = false;
    private Document document;
    private StockSummary summaryData;
    private List<StockSummary> summaryDataList;
    private StockService stockService;
    private List<StockTicker> stockTickers;

    public InvestopediaScraper(StockService stockService, List<StockTicker> stockTickers) {
        this.stockService = stockService;
        this.stockTickers = stockTickers;
    }

    /**
     * scrap summary data
     */
    @Override
    public void scrapeAllSummaryData() {
        summaryDataList = new ArrayList<StockSummary>();
        for (StockTicker stockTicker : stockTickers) {
            scrapeSingleSummaryData(stockTicker);
            summaryDataList.add(summaryData);
        }
    }

    /**
     * Scrap summary data by stock ticker
     *
     * @param stockTicker
     */
    @Override
    public void scrapeSingleSummaryData(StockTicker stockTicker) {
        System.out.println("Scrapping: " + stockTicker.getSymbol());
        String url = "https://www.investopedia.com/markets/stocks/" + stockTicker.getSymbol().toLowerCase();
        try {
            if (!test) {
                Connection jsoupConn = Jsoup.connect(url);
                document = jsoupConn.referrer("http://www.google.com").timeout(1000 * 10).get();
            }
            StockDateMap stockDateMap = new StockDateMap();
            stockDateMap.setSourceId(stockService.getStockSourceIdByName(Constants.SCRAP_DATA_FROM_INVESTOPEDIA));
            stockDateMap.setTickerId(stockTicker.getId());
            stockDateMap.setDate(new SimpleDateFormat("MM-dd-yyyy").format(new Date()));
            int last_inserted_id = stockService.insertStockDateMap(stockDateMap);

            Element table2 = document.select("table").get(2);
            Elements rows = table2.select("tr");
            summaryData = new StockSummary();

            summaryData.setStockDtMapId(last_inserted_id);

            int rowNum = 0;
            String prevClosePrice = rows.get(rowNum).select("td").get(1).text();
            summaryData.setPrevClosePrice(Utility.convertStringCurrency(Utility.isBlank(prevClosePrice) ? "0" : prevClosePrice));
            rowNum++;

            String openPrice = rows.get(rowNum).select("td").get(1).text();
            summaryData.setOpenPrice(Utility.convertStringCurrency(Utility.isBlank(openPrice) ? "0" : openPrice));
            rowNum++;

            String daysRangeMax = Utility.getRangeMinAndMax(rows.get(rowNum).select("td").get(1).text())[0].trim();
            summaryData.setDaysRangeMin(Utility.convertStringCurrency(Utility.isBlank(daysRangeMax) ? "0" : daysRangeMax));
            String daysRangeMin = Utility.getRangeMinAndMax(rows.get(rowNum).select("td").get(1).text())[1].trim();
            summaryData.setDaysRangeMax(Utility.convertStringCurrency(Utility.isBlank(daysRangeMin) ? "0" : daysRangeMin));
            rowNum++;

            String fiftyTwoWeeksMin = Utility.getRangeMinAndMax(rows.get(rowNum).select("td").get(1).text())[0].trim();
            summaryData.setFiftyTwoWeeksMin(Utility.convertStringCurrency(Utility.isBlank(fiftyTwoWeeksMin) ? "0" : fiftyTwoWeeksMin));
            String fiftyTwoWeeksMax = Utility.getRangeMinAndMax(rows.get(rowNum).select("td").get(1).text().trim())[1].trim();
            summaryData.setFiftyTwoWeeksMax(Utility.convertStringCurrency(Utility.isBlank(fiftyTwoWeeksMax) ? "0" : fiftyTwoWeeksMax));
            rowNum++;

            String peRatio = rows.get(rowNum).select("td").get(1).text();
            summaryData.setPeRatio(Utility.convertStringCurrency(Utility.isBlank(peRatio) ? "0" : peRatio));

            rowNum = 0;
            Element table3 = document.select("table").get(3);
            rows = table3.select("tr");

            String betaCoefficient = rows.get(rowNum).select("td").get(1).text();
            summaryData.setBetaCoefficient(Utility.convertStringCurrency(Utility.isBlank(betaCoefficient) ? "0" : betaCoefficient));
            rowNum++;

            String volume = rows.get(rowNum).select("td").get(1).text();
            summaryData.setVolume(Utility.convertStringCurrency(Utility.isBlank(volume) ? "0" : volume).longValue());
            rowNum++;

            String dividend = Utility.getNumeratorAndDenominator(rows.get(rowNum).select("td").get(1).text())[0].trim();
            summaryData.setDividentYield(Utility.convertStringCurrency(Utility.isBlank(dividend) ? "0" : dividend));
            rowNum++;

            String marketCap = rows.get(rowNum).select("td").get(1).text();
            summaryData.setMarketCap(Utility.convertStringCurrency(Utility.isBlank(marketCap) ? "0" : marketCap));
            rowNum++;

            String eps = rows.get(rowNum).select("td").get(1).text();
            summaryData.setEps(Utility.convertStringCurrency(Utility.isBlank(eps) ? "0" : eps));

            stockService.insertStockSummaryData(summaryData);
        } catch (IOException ex) {
            Logger.getLogger(StockReporter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public StockSummary getSummaryData() {
        return summaryData;
    }

    public List<StockSummary> getAllSummaryData() {
        return summaryDataList;
    }
}
