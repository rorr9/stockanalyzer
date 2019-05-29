StockReporter will be able to scrape financial data from websites such as Yahoo, Investopedia and store that data, and also conduct data analysis on the data for research or to aid in the decision-making process regarding investing.

Project setup
-------------
Step 1: On local, run "git init" and followed by git clone https://github.com/kennylg2/swen_sa_tool.git.

Step 2: Switch to "master" branch.

Step 3: Open "StockReporter" project.

Step 4: You need to add project dependencies. 

  (Netbeans): Right click the project -> properties -> libraries -> Add Jar/Folder and select "jarfiles" folder. 

  (Eclipse): Right click the project -> properties -> Java Build Path -> Add JARs and select the project name and "jarfiles"                folder.

Databases (Auto)
----------------
The application will generate "stockreporter.prod" database with tables, indexes, and views when the application is executed for the first time.

Database (Manual)
-----------------
Use "create_tbl_vw_master_summary.sql" to create master, summary tables, indexes, and summary view.

Use "master_data.sql" to insert initial data into STOCK_TICKER and STOCK_SOURCE tables.

Running application
-------------------
src/stockreporter/StockReporter is the main class to run the application. The application does not ship with database but "stockreporter.prod" will be auto-created (tables, initial master data for STOCK_SOURCE and STOCK_TICKER, indexes, and views) when the application is executed for the first time. The application scraps the stock data based on STOCK_SOURCE and STOCK_TICKER data. When you run test cases, please drop the database to have clean data.

Note:
----
As of Apr 07, 2019 the application scraps the data based on STOCK_SOURCE. The application may not function if there is a change in the source data format. e.g. The website may be redesigned or change in html format.

Running Test
------------
The application has three test suites under test/* folder for dao, models, and scraper testing. The system will insert some sample data into the default database for StockReporterTestSuite and ScrappersTestSuite (auto-creates if the database does not exist) and truncates the data at the end of the test. Once all testsuites are executed, it is advisable to drop the database before running main application as it trunctates data including STOCK_SOURCE and STOCK_TICKER for some test cases.
