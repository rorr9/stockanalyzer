package stockreporter.persistence;

import stockreporter.Constants;
import stockreporter.daomodels.StockSummary;
import stockreporter.daomodels.StockTicker;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
    private String BASE_SQL_FILE_PATH = "./resources/";

    private Collection<String> tableNames = Arrays.asList(
            Constants.TABLE_STOCK_TICKER,
            Constants.TABLE_STOCK_DATE_MAP,
            Constants.TABLE_STOCK_SOURCE,
            Constants.TABLE_STOCK_SUMMARY
    );

    /**
     * Default constructor to check if database exist otherwise create new
     * database with tables, views and indexes
     */
    public StockDao() {
        if (databaseAlreadyInitialized()) {
            return;
        }

        Collection<String> sqlFileNames = Arrays.asList(
                "create_table_stock_ticker.sql",
                "create_table_stock_source.sql",
                "create_table_stock_date_map.sql",
                "create_table_stock_summary.sql",
                "create_index_stock_date.sql",
                "create_view_stock_summary.sql"
        );

        List<String> sqlStrings = sqlFileNames.stream()
                .map(fileName -> this.getScript(this.BASE_SQL_FILE_PATH + fileName))
                .collect(Collectors.toList());

        logger.log(Level.INFO, "Creating database and DDL statements...");

        try {
            connect();
            stmt = conn.createStatement();
            for (String str : sqlStrings) {
                stmt.execute(str);
                conn.commit();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            disconnect();
        }

        insertAllStockSources();
        insertAllTickers();
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

        try {
            connect();
            pstmt = conn.prepareStatement("INSERT INTO STOCK_TICKER (SYMBOL, NAME) VALUES (?, ?);");
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

        try {
            connect();
            pstmt = conn.prepareStatement("SELECT SYMBOL, NAME FROM STOCK_TICKER");
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

        try {
            connect();
            pstmt = conn.prepareStatement("INSERT INTO STOCK_SOURCE (NAME) VALUES (?);");
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
        return this.getTableCount(Constants.TABLE_STOCK_DATE_MAP);
    }

    /**
     * Count number of records from summary
     *
     * @return
     */
    public int getStockSummaryCount() {
        return this.getTableCount(Constants.TABLE_STOCK_SUMMARY);
    }

    /**
     * Count number of records from stock source
     *
     * @return
     */
    public int getStockSourceCount() {
        return this.getTableCount(Constants.TABLE_STOCK_SOURCE);
    }

    /**
     * Count number of records from stock ticker
     *
     * @return
     */
    public int getStockTickerCount() {
        return this.getTableCount(Constants.TABLE_STOCK_TICKER);
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

        connect();
        try (
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM STOCK_SUMMARY_VIEW;")) {
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
        this.tableNames.forEach(tableName -> this.deleteFromTable(tableName));
    }

    /**
     * Delete data from stock_source
     */
    void deleteFromStockSource() {
        this.deleteFromTable(Constants.TABLE_STOCK_SOURCE);
    }

    /**
     * Delete data from stock_ticker
     */
    void deleteFromStockTicker() {
        this.deleteFromTable(Constants.TABLE_STOCK_TICKER);
    }

    /**
     * Delete data from stock_ticker
     */
    void deleteFromStockDateMap() {
        this.deleteFromTable(Constants.TABLE_STOCK_DATE_MAP);
    }

    /**
     * Delete data from stock_ticker
     */
    void deleteFromStockSummary() {
        this.deleteFromTable(Constants.TABLE_STOCK_SUMMARY);
    }

    /**
     * Helper method for retrieving the count of records in a table.
     *
     * @param tableName - the name of the table to retrieve the count from
     * @return
     */
    private int getTableCount(String tableName) {
        int recCount = 0;

        try {
            connect();
            pstmt = conn.prepareStatement("SELECT COUNT(*) as CNT FROM " + tableName);
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
     * Helper method for deleting the contents of a table.
     *
     * @param tableName - the name of the table to delete data from
     */
    private void deleteFromTable(String tableName) {
        logger.log(Level.INFO, "Delete data from {0}...", tableName);

        try {
            connect();
            stmt = conn.createStatement();
            stmt.executeQuery("DELETE FROM " + tableName);
            stmt.close();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            disconnect();
        }
    }

    private String getScript(String filePath) {
        StringBuffer sqlString = new StringBuffer();

        try {
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                sqlString.append(str + "\n ");
            }
            bufferedReader.close();
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
        }

        return sqlString.toString();
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
