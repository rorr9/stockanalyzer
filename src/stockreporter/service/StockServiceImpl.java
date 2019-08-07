package stockreporter.service;

import java.util.List;

import stockreporter.daomodels.StockDateMap;
import stockreporter.daomodels.StockSummary;
import stockreporter.daomodels.StockTicker;
import stockreporter.persistence.StockDao;

public class StockServiceImpl implements StockService {
	private StockDao stockDao = StockDao.getInstance();

	@Override
	public List<StockTicker> getAllstockTickers() {
		return this.stockDao.getAllstockTickers();
	}

	@Override
	public void setStockTickerData(String stockSymbol, String stockName) {
		this.stockDao.setStockTickerData(stockSymbol, stockName);

	}

	@Override
	public void setStockSource(String stockSource) {
		this.stockDao.setStockSource(stockSource);
	}

	@Override
	public int getStockTickerBySymbol(String symbol) {
		return this.stockDao.getStockTickerBySymbol(symbol);
	}

	@Override
	public int getStockSourceIdByName(String name) {
		return this.stockDao.getStockSourceIdByName(name);
	}

	@Override
	public int getStockDateMapCount() {
		return this.stockDao.getStockDateMapCount();
	}

	@Override
	public int getStockSummaryCount() {
		return this.stockDao.getStockSummaryCount();
	}

	@Override
	public int getStockSourceCount() {
		return this.stockDao.getStockSourceCount();
	}

	@Override
	public int getStockTickerCount() {
		return this.stockDao.getStockTickerCount();
	}

	@Override
	public int getStockDateMapID(String date, String symbol, String stockSource) {
		return this.stockDao.getStockDateMapID(date, symbol, stockSource);
	}

	@Override
	public int getAvgStockSummaryView() {
		return this.stockDao.getAvgStockSummaryView();
	}

	@Override
	public int insertStockDateMap(StockDateMap stockDateMap) {
		return this.stockDao.insertStockDateMap(stockDateMap);
	}

	@Override
	public void insertStockSummaryData(StockSummary stockSummary) {
		this.stockDao.insertStockSummaryData(stockSummary);
	}

	@Override
	public void deleteAll() {
		this.stockDao.deleteAll();
	}

	@Override
	public void deleteFromStockTicker(String symbol) {
		this.stockDao.deleteFromStockTicker(symbol);
	}

	@Override
	public StockTicker getStockTickerByID(int id) {
		return this.stockDao.getStockTickerByID(id);
	}
}
