/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stockreporter.scrappers;
import java.util.List;
import stockreporter.StockDao;
import stockreporter.daomodels.StockTicker;

/**
 * Initialize stock scrapper with dao and stock tickers
 */
public class StockScraper {
    protected StockDao dao;
    protected List<StockTicker> stockTickers;
    
    public StockScraper(){
        dao = StockDao.getInstance();
        stockTickers = dao.getAllstockTickers();
    }
}
