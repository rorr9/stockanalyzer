package stockreporter.scrapers;

import java.util.List;

import stockreporter.daomodels.StockTicker;
import stockreporter.service.StockService;
import stockreporter.service.StockServiceImpl;

public class ScraperFactory {
    private StockService stockService = new StockServiceImpl();
    private List<StockTicker> stockTickers = this.stockService.getAllstockTickers();

    public Scraper getScraper(String scraperType) {
        if (scraperType == null) {
            return null;
        }

        if (scraperType.equalsIgnoreCase("INVESTOPEDIA")) {
            return new InvestopediaScraper(stockService, stockTickers);
        }

        if (scraperType.equalsIgnoreCase("YAHOO")) {
            return new YahooScraper(stockService, stockTickers);
        }
        
        if (scraperType.equalsIgnoreCase("FIDELITY")) {
        	return new FidelityScraper(stockService, stockTickers);
        }

        if (scraperType.equalsIgnoreCase("MARKETWATCH")) {
            return new MarketWatchScraper(stockService, stockTickers);
        }

        return null;
    }
}
