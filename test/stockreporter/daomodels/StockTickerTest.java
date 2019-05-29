package stockreporter.daomodels;


import org.junit.Test;
import static org.junit.Assert.*;

public class StockTickerTest {
    
    StockTicker master = new StockTicker();
    
    public StockTickerTest() {
        master.setId(100);
        master.setName("Testing Corp");
        master.setSymbol("TC");

    }

    @Test
    public void testGetId() {
        long test = 100;
        assertEquals(test, master.getId());
    }

    @Test
    public void testSetId() {
        StockTicker instance = new StockTicker();
        instance.setId(100);
        assertEquals(master.getId(), instance.getId());
    }

    @Test
    public void testGetSymbol() {
        String test = "TC";
        assertEquals(test, master.getSymbol());
    }

    @Test
    public void testSetSymbol() {
        StockTicker instance = new StockTicker();
        instance.setSymbol("TC");
        assertEquals(master.getSymbol(), instance.getSymbol());
    }

    @Test
    public void testGetName() {
        String test = "Testing Corp";
        assertEquals(test, master.getName());
    }

    @Test
    public void testSetName() {
        StockTicker instance = new StockTicker();
        instance.setName("Testing Corp");
        assertEquals(master.getName(), instance.getName());
    }
    
}
