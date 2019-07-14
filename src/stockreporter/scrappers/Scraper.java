package stockreporter.scrappers;

import stockreporter.daomodels.StockTicker;

public interface Scraper {
    void scrapeAllSummaryData();
    void scrapeSingleSummaryData(StockTicker stockTicker);
}
