package stockreporter.daomodels;

import org.junit.Test;
import static org.junit.Assert.*;

public class StockDateMapTest {
    
    StockDateMap master = new StockDateMap();
    
    public StockDateMapTest() {
        master.setId(100);
        master.setSourceId(101);
        master.setTickerId(102);
        master.setDate("2019-04-01");
    }

    @Test
    public void testGetId() {
        long test = 100;
        assertEquals(master.getId(), test);
    }

    @Test
    public void testSetId() {
        StockDateMap instance = new StockDateMap();
        instance.setId(100);
        assertEquals(master.getId(), instance.getId());
    }

    @Test
    public void testGetDate() {
        String test = "2019-04-01";
        assertEquals(master.getDate(), test);
    }

    @Test
    public void testSetDate() {
        StockDateMap instance = new StockDateMap();
        instance.setDate("2019-04-01");
        assertEquals(master.getDate(), instance.getDate());
    }

    @Test
    public void testGetTickerId() {
        long test = 102;
        assertEquals(master.getTickerId(), test);
    }

    @Test
    public void testSetTickerId() {
        StockDateMap instance = new StockDateMap();
        instance.setTickerId(102);
        assertEquals(master.getTickerId(), instance.getTickerId());
    }

    @Test
    public void testGetSourceId() {
        long test = 101;
        assertEquals(master.getSourceId(), test);
    }

    @Test
    public void testSetSourceId() {
        StockDateMap instance = new StockDateMap();
        instance.setSourceId(101);
        assertEquals(master.getSourceId(), instance.getSourceId());
    }
    
}
