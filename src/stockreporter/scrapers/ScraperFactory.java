package stockreporter.scrapers;

public class ScraperFactory {
    public Scraper getScraper(String scraperType) {
        if (scraperType == null) {
            return null;
        }

        if (scraperType.equalsIgnoreCase("INVESTOPEDIA")) {
            return new InvestopediaScraper();
        }

        if (scraperType.equalsIgnoreCase("YAHOO")) {
            return new YahooScraper();
        }

        return null;
    }
}