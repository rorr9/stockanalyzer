CREATE VIEW STOCK_SUMMARY_VIEW AS
    SELECT SDM.STOCK_DATE STK_DATE, ST.SYMBOL STOCK, AVG(SS.PREV_CLOSE_PRICE) AVG_PRICE FROM STOCK_SUMMARY SS
    INNER JOIN STOCK_DATE_MAP SDM ON SS.STOCK_DT_MAP_ID = SDM.STOCK_DT_MAP_ID
    INNER JOIN STOCK_TICKER ST ON ST.TICKER_ID = SDM.TICKER_ID
    GROUP BY SDM.STOCK_DATE, ST.SYMBOL