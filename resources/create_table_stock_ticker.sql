CREATE TABLE IF NOT EXISTS STOCK_TICKER (
    TICKER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
    SYMBOL TEXT NOT NULL UNIQUE,
    NAME TEXT NOT NULL UNIQUE
);