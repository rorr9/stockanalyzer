package stockreporter;

/**
 * This will keep the initial stocks for scraping
 * The program will insert these stocks if the table is empty
 */
public class Constants {
    public static final String TABLE_STOCK_SOURCE = "STOCK_SOURCE";
    public static final String TABLE_STOCK_TICKER = "STOCK_TICKER";
    public static final String TABLE_STOCK_DATE_MAP = "STOCK_DATE_MAP";
    public static final String TABLE_STOCK_SUMMARY = "STOCK_SUMMARY";
    
    public static final String[] stockSymbols = {"MSFT", "AAPL", "GOOG", "BA", "NFLX", "AMZN", "FB", "CSCO", "TSLA", "TIF"};
    public static final String[] stockNames = {"Microsoft Corporation.", "Apple Inc.", "Alphabet Inc.", "The Boeing Company", "Netflix Inc.", "Amazon.com Inc.", "Facebook Inc.", "Cisco Systems Inc.", "Tesla Inc.", "Tiffany & Co."};
    
    public static final String[] stockSourceNames = {"Yahoo", "Investopedia", "Fidelity"};
    
    public static final String SCRAP_DATA_FROM_YAHOO = stockSourceNames[0];
    public static final String SCRAP_DATA_FROM_INVESTOPEDIA = stockSourceNames[1];
    public static final String SCRAP_DATA_FROM_FIDELITY = stockSourceNames[2];
    
}