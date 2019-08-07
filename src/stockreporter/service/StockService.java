package stockreporter.service;

import java.util.List;

import stockreporter.daomodels.StockDateMap;
import stockreporter.daomodels.StockSummary;
import stockreporter.daomodels.StockTicker;

public interface StockService {
	List<StockTicker> getAllstockTickers();

    void setStockTickerData(String stockSymbol, String stockName);

    void setStockSource(String stockSource);

    int getStockTickerBySymbol(String symbol);

    int getStockSourceIdByName(String name);

    int getStockDateMapCount();

    int getStockSummaryCount();

    int getStockSourceCount();

    int getStockTickerCount();

    int getStockDateMapID(String date, String symbol, String stockSource);

    int getAvgStockSummaryView();

    int insertStockDateMap(StockDateMap stockDateMap);

    void insertStockSummaryData(StockSummary stockSummary);

    void deleteAll();

    void deleteFromStockTicker(String symbol);

    StockTicker getStockTickerByID(int id);
}