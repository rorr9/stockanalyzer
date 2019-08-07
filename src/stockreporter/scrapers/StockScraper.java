/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stockreporter.scrapers;
import java.util.List;
import stockreporter.daomodels.StockTicker;
import stockreporter.service.StockService;
import stockreporter.service.StockServiceImpl;

/**
 * Initialize stock scrapper with dao and stock tickers
 */
public class StockScraper implements Scraper {
    protected StockService stockService = new StockServiceImpl();
    protected List<StockTicker> stockTickers;
    
    public StockScraper() {
        stockTickers = this.stockService.getAllstockTickers();
    }
    
    public void scrapeAllSummaryData() {}
    public void scrapeSingleSummaryData(StockTicker stockTicker) {}
}