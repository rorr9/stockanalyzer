/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stockreporter.daomodels;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Entity object for stock summary with setter/getters
 */
public class StockSummary implements Serializable {
        
        private long summaryId;
	
	private BigDecimal prevClosePrice;
	
	private BigDecimal openPrice;
	
	private BigDecimal bidPrice;
	
	private BigDecimal askPrice;
	
	private BigDecimal daysRangeMin;
	
	private BigDecimal daysRangeMax;
	
	private BigDecimal fiftyTwoWeeksMin;
	
	private BigDecimal fiftyTwoWeeksMax;
	
	private long volume;
	
	private long avgVolume;
	
	private BigDecimal marketCap;
	
	private BigDecimal betaCoefficient;
	
	private BigDecimal peRatio;
	
	private BigDecimal eps;
	
	private String earningDate;
	
	private BigDecimal dividentYield;
	
	private String exDividentDate;
	
	private BigDecimal oneYearTargetEst;
	
	private long stockDtMapId;

	public long getSummaryId() {
		return summaryId;
	}

	public void setSummaryId(long summaryId) {
		this.summaryId = summaryId;
	}

	public BigDecimal getPrevClosePrice() {
		return prevClosePrice;
	}

	public void setPrevClosePrice(BigDecimal prevClosePrice) {
		this.prevClosePrice = prevClosePrice;
	}

	public BigDecimal getOpenPrice() {
		return openPrice;
	}

	public void setOpenPrice(BigDecimal openPrice) {
		this.openPrice = openPrice;
	}

	public BigDecimal getBidPrice() {
		return bidPrice;
	}

	public void setBidPrice(BigDecimal bidPrice) {
		this.bidPrice = bidPrice;
	}

	public BigDecimal getAskPrice() {
		return askPrice;
	}

	public void setAskPrice(BigDecimal askPrice) {
		this.askPrice = askPrice;
	}

	public BigDecimal getDaysRangeMin() {
		return daysRangeMin;
	}

	public void setDaysRangeMin(BigDecimal daysRangeMin) {
		this.daysRangeMin = daysRangeMin;
	}

	public BigDecimal getDaysRangeMax() {
		return daysRangeMax;
	}

	public void setDaysRangeMax(BigDecimal daysRangeMax) {
		this.daysRangeMax = daysRangeMax;
	}

	public BigDecimal getFiftyTwoWeeksMin() {
		return fiftyTwoWeeksMin;
	}

	public void setFiftyTwoWeeksMin(BigDecimal fiftyTwoWeeksMin) {
		this.fiftyTwoWeeksMin = fiftyTwoWeeksMin;
	}

	public BigDecimal getFiftyTwoWeeksMax() {
		return fiftyTwoWeeksMax;
	}

	public void setFiftyTwoWeeksMax(BigDecimal fiftyTwoWeeksMax) {
		this.fiftyTwoWeeksMax = fiftyTwoWeeksMax;
	}

	public long getVolume() {
		return volume;
	}

	public void setVolume(long volume) {
		this.volume = volume;
	}

	public long getAvgVolume() {
		return avgVolume;
	}

	public void setAvgVolume(long avgVolume) {
		this.avgVolume = avgVolume;
	}

	public BigDecimal getMarketCap() {
		return marketCap;
	}

	public void setMarketCap(BigDecimal marketCap) {
		this.marketCap = marketCap;
	}

	public BigDecimal getBetaCoefficient() {
		return betaCoefficient;
	}

	public void setBetaCoefficient(BigDecimal betaCoefficient) {
		this.betaCoefficient = betaCoefficient;
	}

	public BigDecimal getPeRatio() {
		return peRatio;
	}

	public void setPeRatio(BigDecimal peRatio) {
		this.peRatio = peRatio;
	}

	public BigDecimal getEps() {
		return eps;
	}

	public void setEps(BigDecimal eps) {
		this.eps = eps;
	}

	public String getEarningDate() {
		return earningDate;
	}

	public void setEarningDate(String earningDate) {
		this.earningDate = earningDate;
	}

	public BigDecimal getDividentYield() {
		return dividentYield;
	}

	public void setDividentYield(BigDecimal dividentYield) {
		this.dividentYield = dividentYield;
	}

	public String getExDividentDate() {
		return exDividentDate;
	}

	public void setExDividentDate(String exDividentDate) {
		this.exDividentDate = exDividentDate;
	}

	public BigDecimal getOneYearTargetEst() {
		return oneYearTargetEst;
	}

	public void setOneYearTargetEst(BigDecimal oneYearTargetEst) {
		this.oneYearTargetEst = oneYearTargetEst;
	}

	public long getStockDtMapId() {
		return stockDtMapId;
	}

	public void setStockDtMapId(long stockDtMapId) {
		this.stockDtMapId = stockDtMapId;
	}

	@Override
	public String toString() {
		return "StockSummary [summaryId=" + summaryId + ", prevClosePrice=" + prevClosePrice + ", openPrice="
				+ openPrice + ", bidPrice=" + bidPrice + ", askPrice=" + askPrice + ", daysRangeMin=" + daysRangeMin
				+ ", daysRangeMax=" + daysRangeMax + ", fiftyTwoWeeksMin=" + fiftyTwoWeeksMin + ", fiftyTwoWeeksMax="
				+ fiftyTwoWeeksMax + ", volume=" + volume + ", avgVolume=" + avgVolume + ", marketCap=" + marketCap
				+ ", betaCoefficient=" + betaCoefficient + ", peRatio=" + peRatio + ", eps=" + eps + ", earningDate="
				+ earningDate + ", dividentYield=" + dividentYield + ", exDividentDate=" + exDividentDate
				+ ", oneYearTargetEst=" + oneYearTargetEst + ", stockDtMapId=" + stockDtMapId + "]";
	}
    
}
