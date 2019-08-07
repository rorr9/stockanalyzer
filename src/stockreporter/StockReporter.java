package stockreporter;

import java.util.logging.Logger;

import stockreporter.cli.Command;

/**
 * Main class for scrapping the data
 */
public class StockReporter {

    private static final Logger logger = Logger.getLogger(StockReporter.class.getName());

    public static void main(String[] args) {

        Command.parseParams(args);
    }
}
 