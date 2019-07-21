/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stockreporter.scrapers;
import java.util.List;
import stockreporter.StockDao;
import stockreporter.daomodels.StockTicker;

/**
 * Initialize stock scrapper with dao and stock tickers
 */
public class StockScraper implements Scraper {
    protected StockDao dao;
    protected List<StockTicker> stockTickers;
    
    public StockScraper(){
        dao = StockDao.getInstance();
        stockTickers = dao.getAllstockTickers();
    }
    
    public void scrapeAllSummaryData() {}
    public void scrapeSingleSummaryData(StockTicker stockTicker) {}
}