package stockreporter;

import stockreporter.daomodels.StockSummary;
import stockreporter.daomodels.StockTicker;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import stockreporter.daomodels.StockDateMap;

/**
 * This is the Data Access Layer (DAO) between database and business logic It
 * contains all CRUD and DDL statements to database operation
 */
public final class StockDao {

    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private static StockDao instance = null;
    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rs = null;
    private PreparedStatement pstmt = null;

    //For testing change the name to something like "stockreporter.test"
    // and drop the database after testing
    private String dbName = "stockreporter.prod";
    private String url = "jdbc:sqlite:stockreporter.prod";

    /**
     * Default constructor to check if database exist otherwise create new
     * database with tables, views and indexes
     */
    public StockDao() {
        if (!databaseAlreadyInitialized()) {
            ArrayList<String> sqlStrings = new ArrayList<>();
            //Create the tables
            String stockTicker = "CREATE TABLE IF NOT EXISTS STOCK_TICKER (\n"
                    + "	TICKER_ID INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                    + "	SYMBOL TEXT NOT NULL UNIQUE,\n"
                    + "	NAME TEXT NOT NULL UNIQUE\n"
                    + ");";

            sqlStrings.add(stockTicker);

            String stockSource = "CREATE TABLE IF NOT EXISTS STOCK_SOURCE (\n"
                    + "	SOURCE_ID INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                    + "	NAME TEXT NOT NULL UNIQUE\n"
                    + ");";

            sqlStrings.add(stockSource);

            String stockDateMap = "CREATE TABLE IF NOT EXISTS STOCK_DATE_MAP (\n"
                    + "	STOCK_DT_MAP_ID INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                    + "	STOCK_DATE TEXT,\n"
                    + "	TICKER_ID INTEGER REFERENCES STOCK_TICKET(TICKET_ID),\n"
                    + "	SOURCE_ID INTEGER REFERENCES STOCK_SOURCE(SOURCE_ID)\n"
                    + ");";

            sqlStrings.add(stockDateMap);

            String stockSummary = "CREATE TABLE IF NOT EXISTS STOCK_SUMMARY (\n"
                    + "	SUMMARY_ID INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                    + "	PREV_CLOSE_PRICE REAL,\n"
                    + "	OPEN_PRICE REAL,\n"
                    + "	BID_PRICE REAL,\n"
                    + "	ASK_PRICE REAL,\n"
                    + "	DAYS_RANGE_MIN REAL,\n"
                    + "	DAYS_RANGE_MAX REAL,\n"
                    + "	FIFTY_TWO_WEEKS_MIN REAL,\n"
                    + "	FIFTY_TWO_WEEKS_MAX REAL,\n"
                    + "	VOLUME INTEGER,\n"
                    + "	AVG_VOLUME INTEGER,\n"
                    + "	MARKET_CAP REAL,\n"
                    + "	BETA_COEFFICIENT REAL,\n"
                    + "	PE_RATIO REAL,\n"
                    + "	EPS REAL,\n"
                    + "	EARNING_DATE TEXT,\n"
                    + "	DIVIDEND_YIELD REAL,\n"
                    + "	EX_DIVIDEND_DATE TEXT,\n"
                    + "	ONE_YEAR_TARGET_EST REAL,\n"
                    + "	STOCK_DT_MAP_ID INTEGER REFERENCES STOCK_DATE_MAP(STOCK_DT_MAP_ID)\n"
                    + ");";

            sqlStrings.add(stockSummary);

            //Placeholder for the StockHistorical string.
            //Creating the index
            String index = "CREATE INDEX STOCK_DATE_IDX ON STOCK_DATE_MAP(STOCK_DATE);";

            sqlStrings.add(index);

            //Creating the View strings
            String stockSummaryView = "CREATE VIEW STOCK_SUMMARY_VIEW AS\n"
                    + " SELECT SDM.STOCK_DATE STK_DATE, ST.SYMBOL STOCK, AVG(SS.PREV_CLOSE_PRICE) AVG_PRICE FROM STOCK_SUMMARY SS\n"
                    + " INNER JOIN STOCK_DATE_MAP SDM ON SS.STOCK_DT_MAP_ID = SDM.STOCK_DT_MAP_ID\n"
                    + " INNER JOIN STOCK_TICKER ST ON ST.TICKER_ID = SDM.TICKER_ID\n"
                    + " GROUP BY SDM.STOCK_DATE, ST.SYMBOL";

            sqlStrings.add(stockSummaryView);

            //Placeholder for stockhistorical view
            //Execute the SQL strings in the DB.
            logger.log(Level.INFO, "Creating database and DDL statements...");
            try {
                connect();
                stmt = conn.createStatement();
                for (String str : sqlStrings) {
                    stmt.execute(str);
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, e.getMessage());
            } finally {
                disconnect();
            }
            insertAllStockSources();
            insertAllTickers();
        }
    }

    /**
     * Get database instance
     *
     * @return
     */
    public static StockDao getInstance() {
        if (instance == null) {
            instance = new StockDao();
        }
        return instance;
    }

    /**
     * Make database connection
     */
    public void connect() {
        logger.log(Level.INFO, "Connect to database...");
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
    }

    /**
     * Disconnect from database
     */
    private void disconnect() {
        logger.log(Level.INFO, "Disconnect from database...");
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
        }
    }

    /**
     * Method to check if the database already exist
     *
     * @return boolean
     */
    public boolean databaseAlreadyInitialized() {
        logger.log(Level.INFO, "Check database already initialized...");
        String tableName = null;
        try {
            connect();
            DatabaseMetaData meta = conn.getMetaData();
            rs = meta.getTables(null, null, "STOCK_TICKER", new String[]{"TABLE"});
            while (rs.next()) {
                tableName = rs.getString("TABLE_NAME");
            }
            rs.close();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            disconnect();
        }
        return tableName != null;
    }

    /**
     * Insert stock_ticker data if the table is empty
     */
    private void insertAllTickers() {
        for (int i = 0; i <= Constants.stockSymbols.length - 1; i++) {
            setStockTickerData(Constants.stockSymbols[i], Constants.stockNames[i]);
        }
    }

    /**
     * Insert stock_source data if the table is empty
     */
    private void insertAllStockSources() {
        for (int cnt = 0; cnt <= Constants.stockSourceNames.length - 1; cnt++) {
            setStockSource(Constants.stockSourceNames[cnt]);
        }
    }

    /**
     * Insert STOCK_TICKER data
     *
     * @param stockSymbol
     * @param stockName
     */
    public void setStockTickerData(String stockSymbol, String stockName) {
        logger.log(Level.INFO, "Insert STOCK_TICKER data...");

        String sql = "INSERT INTO STOCK_TICKER (SYMBOL, NAME) VALUES (?, ?);";
        try {
            connect();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, stockSymbol);
            pstmt.setString(2, stockName);
            pstmt.executeUpdate();
            conn.commit();
            pstmt.close();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            disconnect();
        }
    }

    /**
     * Load STOCK_TICKER data
     *
     * @return
     */
    public List<StockTicker> getAllstockTickers() {
        logger.log(Level.INFO, "Get all STOCK_TICKER data...");
        List<StockTicker> stockTickers = new ArrayList<>();
        String query = "SELECT SYMBOL, NAME FROM STOCK_TICKER";
        try {
            connect();
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            StockTicker stockticker = null;

            while (rs.next()) {
                stockticker = new StockTicker();
                stockticker.setId(rs.getRow());
                stockticker.setName(rs.getString("name"));
                stockticker.setSymbol(rs.getString("symbol"));
                stockTickers.add(stockticker);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            disconnect();
        }
        return stockTickers;
    }

    /**
     * Insert data into STOCK_SOURCE table
     *
     * @param stockSource
     */
    public void setStockSource(String stockSource) {
        logger.log(Level.INFO, "Insert STOCK_SOURCE data...");
        String sql = "INSERT INTO STOCK_SOURCE (NAME) VALUES (?);";
        try {
            connect();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, stockSource);
            pstmt.executeUpdate();
            conn.commit();
            pstmt.close();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            disconnect();
        }
    }

    /**
     * Insert data into STOCK_DATE_MAP table
     *
     * @param stockDateMap
     * @return
     */
    public int insertStockDateMap(StockDateMap stockDateMap) {
        logger.log(Level.INFO, "Insert data into STOCK_DATE_MAP...");
        int last_inserted_id = -1;
        String sql = "INSERT INTO STOCK_DATE_MAP (STOCK_DATE,"
                + " TICKER_ID,"
                + " SOURCE_ID) VALUES (?, ?, ?);";
        if (stockDateMap.getTickerId() != -1 && stockDateMap.getSourceId() != -1) {
            try {
                connect();
                pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                pstmt.setString(1, stockDateMap.getDate());
                pstmt.setInt(2, (int) stockDateMap.getTickerId());
                pstmt.setInt(3, (int) stockDateMap.getSourceId());
                pstmt.executeUpdate();

                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    last_inserted_id = rs.getInt(1);
                }
                conn.commit();
                pstmt.close();
                rs.close();
            } catch (SQLException e) {
                logger.log(Level.SEVERE, e.getMessage());
            } finally {
                disconnect();
            }
        }
        return last_inserted_id;
    }

    /**
     * Get stock ticker by symbol
     *
     * @param symbol
     * @return
     */
    public int getStockTickerBySymbol(String symbol) {
        String symbolQuery = "SELECT TICKER_ID FROM STOCK_TICKER WHERE SYMBOL = ?";
        int tickerID = 0;
        try {
            connect();
            pstmt = conn.prepareStatement(symbolQuery);
            pstmt.setString(1, symbol);
            rs = pstmt.executeQuery();
            tickerID = rs.getInt("ticker_id");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();

                    if (rs != null) {
                        rs.close();
                    }
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, ex.getLocalizedMessage());
                }
            }

            disconnect();
        }
        return tickerID;
    }

    /**
     * Get stock source id by name
     *
     * @param name
     * @return source id
     */
    public int getStockSourceIdByName(String name) {
        int tickerID = -1;
        String symbolQuery = "SELECT SOURCE_ID FROM STOCK_SOURCE WHERE NAME = ?";

        try {
            connect();
            pstmt = conn.prepareStatement(symbolQuery);
            pstmt.setString(1, name);
            rs = pstmt.executeQuery();
            tickerID = rs.getInt("SOURCE_ID");
            pstmt.close();
            rs.close();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();

                    if (rs != null) {
                        rs.close();
                    }
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, ex.getLocalizedMessage());
                }
            }

            disconnect();
        }
        return tickerID;
    }

    /**
     * Count number of records from stock date mapper
     *
     * @return
     */
    public int getStockDateMapCount() {
        String SQL = "SELECT COUNT(*) as CNT FROM STOCK_DATE_MAP";
        int recCount = 0;
        try {
            connect();
            pstmt = conn.prepareStatement(SQL);
            rs = pstmt.executeQuery();
            recCount = rs.getInt("CNT");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                    if (rs != null) {
                        rs.close();
                    }
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, ex.getLocalizedMessage());
                }
            }
            disconnect();
        }
        return recCount;
    }

    /**
     * Count number of records from summary
     *
     * @return
     */
    public int getStockSummaryCount() {
        String SQL = "SELECT COUNT(*) as CNT FROM STOCK_SUMMARY";
        int recCount = 0;
        try {
            connect();
            pstmt = conn.prepareStatement(SQL);
            rs = pstmt.executeQuery();
            recCount = rs.getInt("CNT");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                    if (rs != null) {
                        rs.close();
                    }
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, ex.getLocalizedMessage());
                }
            }
            disconnect();
        }
        return recCount;
    }

    /**
     * Count number of records from stock source
     *
     * @return
     */
    public int getStockSourceCount() {
        String SQL = "SELECT COUNT(*) as CNT FROM STOCK_SOURCE";
        int recCount = 0;
        try {
            connect();
            pstmt = conn.prepareStatement(SQL);
            rs = pstmt.executeQuery();
            recCount = rs.getInt("CNT");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                    if (rs != null) {
                        rs.close();
                    }
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, ex.getLocalizedMessage());
                }
            }
            disconnect();
        }
        return recCount;
    }

    /**
     * Count number of records from stock ticker
     *
     * @return
     */
    public int getStockTickerCount() {
        String SQL = "SELECT COUNT(*) as CNT FROM STOCK_TICKER";
        int recCount = 0;
        try {
            connect();
            pstmt = conn.prepareStatement(SQL);
            rs = pstmt.executeQuery();
            recCount = rs.getInt("CNT");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                    if (rs != null) {
                        rs.close();
                    }
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, ex.getLocalizedMessage());
                }
            }
            disconnect();
        }
        return recCount;
    }

    /**
     * Get stock date map by date, tickerid and sourceid
     *
     * @param date
     * @param tickerID
     * @param sourceID
     * @return
     */
    private int getStockDateMapId(String date, int tickerID, int sourceID) {
        String stockDtMapId = "SELECT STOCK_DT_MAP_ID FROM STOCK_DATE_MAP WHERE STOCK_DATE = ? AND TICKER_ID = ? AND SOURCE_ID = ?";
        int stockDateMapID = 0;

        try {
            connect();
            pstmt = conn.prepareStatement(stockDtMapId);
            pstmt.setString(1, date);
            pstmt.setInt(2, tickerID);
            pstmt.setInt(3, sourceID);
            rs = pstmt.executeQuery();
            stockDateMapID = rs.getInt("stock_dt_map_id");
            pstmt.close();
            rs.close();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();

                    if (rs != null) {
                        rs.close();
                    }
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, ex.getLocalizedMessage());
                }
            }
            disconnect();
        }
        return stockDateMapID;
    }

    /**
     * Used to get the stockdatemap id key from the DB to add into the stock
     * summary/historical objects.
     *
     * @param date
     * @param symbol
     * @param stockSource
     * @return
     */
    public int getStockDateMapID(String date, String symbol, String stockSource) {
        logger.log(Level.INFO, "Calling getStockDateMapID...");

        int stockDateMapID = -1;
        int sourceID = getStockSourceIdByName(stockSource);
        int tickerID = getStockTickerBySymbol(symbol);

        if (tickerID > 0 && sourceID > 0) {
            stockDateMapID = getStockDateMapId(date, tickerID, sourceID);
        }
        return stockDateMapID;
    }

    /**
     * insert data into STOCK_SUMMARY table
     *
     * @param stockSummary
     */
    public void insertStockSummaryData(StockSummary stockSummary) {
        logger.log(Level.INFO, "Insert data into STOCK_SUMMARY...");
        String sql = "INSERT INTO STOCK_SUMMARY (PREV_CLOSE_PRICE,"
                + " OPEN_PRICE,"
                + " BID_PRICE,"
                + " ASK_PRICE,"
                + " DAYS_RANGE_MIN,"
                + " DAYS_RANGE_MAX,"
                + " FIFTY_TWO_WEEKS_MIN,"
                + " FIFTY_TWO_WEEKS_MAX,"
                + " VOLUME,"
                + " AVG_VOLUME,"
                + " MARKET_CAP,"
                + " BETA_COEFFICIENT,"
                + " PE_RATIO,"
                + " EPS,"
                + " EARNING_DATE,"
                + " DIVIDEND_YIELD,"
                + " EX_DIVIDEND_DATE,"
                + " ONE_YEAR_TARGET_EST,"
                + " STOCK_DT_MAP_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        try {
            connect();
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement(sql);
            pstmt.setBigDecimal(1, stockSummary.getPrevClosePrice());
            pstmt.setBigDecimal(2, stockSummary.getOpenPrice());
            pstmt.setBigDecimal(3, stockSummary.getBidPrice());
            pstmt.setBigDecimal(4, stockSummary.getAskPrice());
            pstmt.setBigDecimal(5, stockSummary.getDaysRangeMin());
            pstmt.setBigDecimal(6, stockSummary.getDaysRangeMax());
            pstmt.setBigDecimal(7, stockSummary.getFiftyTwoWeeksMin());
            pstmt.setBigDecimal(8, stockSummary.getFiftyTwoWeeksMax());
            pstmt.setLong(9, stockSummary.getVolume());
            pstmt.setLong(10, stockSummary.getAvgVolume());
            pstmt.setBigDecimal(11, stockSummary.getMarketCap());
            pstmt.setBigDecimal(12, stockSummary.getBetaCoefficient());
            pstmt.setBigDecimal(13, stockSummary.getPeRatio());
            pstmt.setBigDecimal(14, stockSummary.getEps());
            pstmt.setString(15, stockSummary.getEarningDate());
            pstmt.setBigDecimal(16, stockSummary.getDividentYield());
            pstmt.setString(17, stockSummary.getExDividentDate());
            pstmt.setBigDecimal(18, stockSummary.getOneYearTargetEst());
            pstmt.setLong(19, stockSummary.getStockDtMapId());
            pstmt.executeUpdate();
            conn.commit();
            pstmt.close();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            disconnect();
        }
    }

    /**
     * Get Stock summary data from view
     */
    public int getAvgStockSummaryView() {
        logger.log(Level.INFO, "Get STOCK_SUMMARY_VIEW data...");
        int totalRecords = 0;

        String sql = "SELECT * FROM STOCK_SUMMARY_VIEW;";
        connect();
        try (
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            logger.log(Level.INFO, "getAvgStockSummaryView results...");
            while (rs.next()) {
                logger.log(Level.INFO, rs.getString("STK_DATE") + "\t"
                        + rs.getString("STOCK") + "\t"
                        + rs.getBigDecimal("AVG_PRICE"));
                ++totalRecords;
            }
            rs.close();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            disconnect();
        }
        return totalRecords;
    }

    /**
     * Delete data
     */
    public void deleteAll() {
        deleteFromStockSource();
        deleteFromStockTicker();
        deleteFromStockDateMap();
        deleteFromStockSummary();
    }

    /**
     * Delete data from stock_source
     */
    void deleteFromStockSource() {
        logger.log(Level.INFO, "Delete data from STOCK_SOURCE...");

        String sql = "DELETE FROM " + Constants.TABLE_STOCK_SOURCE;
        try {
            connect();
            stmt = conn.createStatement();
            stmt.executeQuery(sql);
            stmt.close();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            disconnect();
        }
    }

    /**
     * Delete data from stock_ticker
     */
    void deleteFromStockTicker() {
        logger.log(Level.INFO, "Delete data from STOCK_TICKER...");

        String sql = "DELETE FROM " + Constants.TABLE_STOCK_TICKER;
        try {
            connect();
            stmt = conn.createStatement();
            stmt.executeQuery(sql);
            stmt.close();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            disconnect();
        }
    }

    /**
     * Delete data from stock_ticker
     */
    void deleteFromStockDateMap() {
        logger.log(Level.INFO, "Delete data from STOCK_DATE_MAP...");

        String sql = "DELETE FROM " + Constants.TABLE_STOCK_DATE_MAP;
        try {
            connect();
            stmt = conn.createStatement();
            stmt.executeQuery(sql);
            stmt.close();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            disconnect();
        }
    }

    /**
     * Delete data from stock_ticker
     */
    void deleteFromStockSummary() {
        logger.log(Level.INFO, "Delete data from STOCK_SUMMARY...");

        String sql = "DELETE FROM " + Constants.TABLE_STOCK_SUMMARY;
        try {
            connect();
            stmt = conn.createStatement();
            stmt.executeQuery(sql);
            stmt.close();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            disconnect();
        }
    }

    /**
     * Delete Stock Ticker by Symbol
     *
     * @param symbol
     * @return
     */
    public void deleteFromStockTicker(String symbol) {
        logger.log(Level.INFO, "Delete data from STOCK_TICKER...");

        String sql = "DELETE FROM " + Constants.TABLE_STOCK_TICKER + " WHERE SYMBOL = ?";
        try {
            connect();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, symbol);
            pstmt.executeUpdate();
            conn.commit();
            stmt.close();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            disconnect();
        }
    }

    /**
     * Get stock ticker by ID
     *
     * @param id
     * @return StockTicker
     */
    public StockTicker getStockTickerByID(int id) {
        logger.log(Level.INFO, "Get STOCK_TICKER by id...");
        StockTicker stockTicker = null;

        String sql = "SELECT SYMBOL, NAME FROM STOCK_TICKER WHERE TICKER_ID = ?";
        try {
            connect();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                stockTicker = new StockTicker();
                stockTicker.setId(id);
                stockTicker.setName(rs.getString("name"));
                stockTicker.setSymbol(rs.getString("symbol"));
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            disconnect();
        }

        return stockTicker;
    }

}
