CREATE TABLE IF NOT EXISTS STOCK_DATE_MAP (
    STOCK_DT_MAP_ID INTEGER PRIMARY KEY AUTOINCREMENT,
    STOCK_DATE TEXT,
    TICKER_ID INTEGER REFERENCES STOCK_TICKER(TICKER_ID),
    SOURCE_ID INTEGER REFERENCES STOCK_SOURCE(SOURCE_ID)
);