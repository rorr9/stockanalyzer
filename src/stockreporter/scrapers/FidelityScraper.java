/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stockreporter.scrapers;

import java.io.IOException;
import java.text.SimpleDateFormat;
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
 *
 * @author craigscheiderer
 */
public class FidelityScraper implements Scraper {

    /**
     * default constructor
     */
    private Document document;
    private StockSummary summaryData;
    private StockService stockService;
    private List<StockTicker> stockTickers;
    private boolean test = false;

    public FidelityScraper(StockService stockService, List<StockTicker> stockTickers) {
        this.stockService = stockService;
        this.stockTickers = stockTickers;
    }

    /**
     * Scrap summary data
     */
    @Override
    public void scrapeAllSummaryData() {
        for (StockTicker stockTicker : stockTickers) {
            scrapeSingleSummaryData(stockTicker);
        }
    }

    /**
     * Scrap summary data by stock ticker
     *
     * @param stockTicker
     */
    @Override
    public void scrapeSingleSummaryData(StockTicker stockTicker) {
        System.out.println("Scraping: " + stockTicker.getSymbol());
        String url = "https://eresearch.fidelity.com/eresearch/goto/evaluate/snapshot.jhtml?symbols=" + stockTicker.getSymbol().toLowerCase();
        try {
            if (!test) {
                Connection jsoupConn = Jsoup.connect(url);
                document = jsoupConn.referrer("http://www.google.com").timeout(1000 * 20).get();
            }

            StockDateMap stockDateMap = new StockDateMap();
            stockDateMap.setSourceId(stockService.getStockSourceIdByName(Constants.SCRAP_DATA_FROM_FIDELITY));
            stockDateMap.setTickerId(stockTicker.getId());
            stockDateMap.setDate(new SimpleDateFormat("MM-dd-yyyy").format(new Date()));
            int last_inserted_id = stockService.insertStockDateMap(stockDateMap);

            Element table1 = document.select("table").get(2);
            Elements rows = table1.select("tr");
            summaryData = new StockSummary();

            summaryData.setStockDtMapId(last_inserted_id);

            String bidPrice = rows.get(0).select("td").get(0).text();
            summaryData.setBidPrice(Utility.convertStringCurrency(Utility.isBlank(bidPrice) ? "0" : bidPrice));

            String askPrice = rows.get(2).select("td").get(0).text();
            summaryData.setAskPrice(Utility.convertStringCurrency(Utility.isBlank(askPrice) ? "0" : askPrice));

            String openPrice = rows.get(4).select("td").get(0).text();
            summaryData.setOpenPrice(Utility.convertStringCurrency(Utility.isBlank(openPrice) ? "0" : openPrice));

            String daysRangeMax = rows.get(5).select("td").get(0).text();
            summaryData.setDaysRangeMax(Utility.convertStringCurrency(Utility.isBlank(daysRangeMax) ? "0" : daysRangeMax));

            String daysRangeMin = rows.get(6).select("td").get(0).text();
            summaryData.setDaysRangeMin(Utility.convertStringCurrency(Utility.isBlank(daysRangeMin) ? "0" : daysRangeMin));

            String prevClosePrice = rows.get(7).select("td").get(0).text();
            summaryData.setPrevClosePrice(Utility.convertStringCurrency(Utility.isBlank(prevClosePrice) ? "0" : prevClosePrice));

            String fiftyTwoWeeksMax = rows.get(8).select("td").get(0).text();
            summaryData.setFiftyTwoWeeksMax(Utility.convertStringCurrency(Utility.isBlank(fiftyTwoWeeksMax) ? "0" : fiftyTwoWeeksMax));

            String fiftyTwoWeeksMin = rows.get(9).select("td").get(0).text();
            summaryData.setFiftyTwoWeeksMin(Utility.convertStringCurrency(Utility.isBlank(fiftyTwoWeeksMin) ? "0" : fiftyTwoWeeksMin));

            String volume = rows.get(9).select("td").get(0).text();
            summaryData.setVolume(Utility.convertStringCurrency(Utility.isBlank(volume) ? "0" : volume).longValue());

            String avgVolume = rows.get(10).select("td").get(0).text().substring(1, 6);
            summaryData.setAvgVolume(Utility.convertStringCurrency(Utility.isBlank(avgVolume) ? "0" : avgVolume).longValue());

            Element table2 = document.select("table").get(19);
            rows = table2.select("tr");

            String marketCap = rows.get(1).select("td").get(0).text();
            String marketCapFormatted = marketCap.substring(1, marketCap.length() - 1);
            summaryData.setMarketCap(Utility.convertStringCurrency(Utility.isBlank(marketCapFormatted) ? "0" : marketCapFormatted));

            String betaCoefficient = rows.get(3).select("td").get(0).text();
            summaryData.setBetaCoefficient(Utility.convertStringCurrency(Utility.isBlank(betaCoefficient) ? "0" : betaCoefficient));

            String eps = rows.get(4).select("td").get(0).text().substring(1);
            summaryData.setEps(Utility.convertStringCurrency(Utility.isBlank(eps) ? "0" : eps));

            String peRatio = rows.get(7).select("td").get(0).text();

            if (peRatio.matches("[^0-9]+$")) {
                peRatio = "0";
            }

            summaryData.setPeRatio(Utility.convertStringCurrency(Utility.isBlank(peRatio) ? "0" : peRatio));

            String exDividendDate = rows.get(8).select("th").get(0).text().substring(27);
            summaryData.setExDividentDate(exDividendDate);

            String dividend = rows.get(8).select("td").get(0).text();
            if (dividend.matches("[^0-9]+$")) {
                dividend = "0";
            }
            summaryData.setDividentYield(Utility.convertStringCurrency(Utility.isBlank(dividend) ? "0" : dividend));

            String earningDate = null;//Fidelity does not provide this data on their website

            String onYearTargetEst = null;//Fidelity does not provide this data on their website

            stockService.insertStockSummaryData(summaryData);

        } catch (IOException ex) {
            Logger.getLogger(StockReporter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public StockSummary getSummaryData() {
        return summaryData;
    }
}
