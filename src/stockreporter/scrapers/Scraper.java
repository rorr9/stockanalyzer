package stockreporter.scrapers;

import java.util.List;
import stockreporter.daomodels.StockSummary;
import stockreporter.daomodels.StockTicker;

public interface Scraper {

    void scrapeAllSummaryData();

    void scrapeSingleSummaryData(StockTicker stockTicker);

    StockSummary getSummaryData();

    List<StockSummary> getAllSummaryData();
}
