/*
 * UtilityTest.java
 * JUnit Class to test the Utility.java class
 * 
 */
package stockreporter;

import java.math.BigDecimal;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author klacayo
 */
public class UtilityTest {
    //Utility util;
    
    public UtilityTest() {
        //util = new Utility();
    }

    /*
    * Test of convertStringCurrency utilizing 'M'
    */
    @Test
    public void testConvertStringCurrencyUsingM() {
        System.out.println("convertStringCurrencyUsingM");
        String stringCurrency = "1M";
        Double expResult = 1000.0;
        Double result = Utility.convertStringCurrency(stringCurrency).doubleValue();
        assertEquals(expResult, result);  
    }
    
    /*
    * Test of convertStringCurrency utilizing 'B'
    */
    @Test
    public void testConvertStringCurrencyUsingB() {
        System.out.println("convertStringCurrencyUsingB");
        String stringCurrency = "1B";
        Double expResult = 1000000.0;
        Double result = Utility.convertStringCurrency(stringCurrency).doubleValue();
        assertEquals(expResult, result); 
    }
    
    /*
    * Test of convertStringCurrency utilizing 'T'
    */
    @Test
    public void testConvertStringCurrencyUsingT() {
        System.out.println("convertStringCurrencyUsingT");
        String stringCurrency = "1T";
        Double expResult = 1000000000.0;
        Double result = Utility.convertStringCurrency(stringCurrency).doubleValue();
        assertEquals(expResult, result); 
    }
    
    /*
    * Test of convertStringCurrency utilizing numerical value
    */    
    @Test
    public void testConvertStringCurrencyUsingOther() {
        System.out.println("convertStringCurrencyUsingOther");
        String stringCurrency = "100";
        BigDecimal expResult = BigDecimal.valueOf(100.0);
        BigDecimal result = Utility.convertStringCurrency(stringCurrency);
        assertEquals(expResult, result);  
    }
    
    /*
    * Test of getThousands utilizing M (for Million)
    */ 
    @Test
    public void testGetThousandsUsingM() {
        System.out.println("getThousandsUsingM");
        char letter = 'M';
        double expResult = 1000.0;
        double result = Utility.getThousands(letter);
        assertEquals(expResult, result, 0.0);      
    }
    
     /*
    * Test of getThousands utilizing B (for Billion)
    */     
    @Test
    public void testGetThousandsUsingB() {
        System.out.println("getThousandsUsingB");
        char letter = 'B';
        double expResult = 1000000.0;
        double result = Utility.getThousands(letter);
        assertEquals(expResult, result, 0.0);      
    }
    
     /*
    * Test of getThousands utilizing T (for Trillion)
    */     
    @Test
    public void testGetThousandsUsingT() {
        System.out.println("getThousandsUsingT");
        char letter = 'T';
        double expResult = 1000000000.0;
        double result = Utility.getThousands(letter);
        assertEquals(expResult, result, 0.0);      
    }
    
     /*
    * Test of getThousands utilizing a character other than M, B, or T
    */ 
     @Test
    public void testGetThousandsUsingOther() {
        System.out.println("getThousandsUsingOther");
        char letter = 'X';
        double expResult = 1.0;
        double result = Utility.getThousands(letter);
        assertEquals(expResult, result, 0.0);      
    }
    
     /*
    * Test of getDoubleCurrency utilizing 100
    */ 
    @Test
    public void testGetDoubleCurrency() {
        System.out.println("getDoubleCurrency");
        String stringCurrency = "100";
        double expResult = 100.0;
        double result = Utility.getDoubleCurrency(stringCurrency);
        assertEquals(expResult, result, 0.0); 
    }
    
    /*
    * Test of removeTrailingZero
    */
    @Test
    public void testRemoveTrailingZero(){
        System.out.println("removeTrailingZero");
        double doubleWithZeros = 123.00;
        String expResult = "123";
        String result = Utility.removeTrailingZero(doubleWithZeros);
        assertEquals(expResult, result);
    }
    
    /*
    * Test of getRangeMinAndMax
    */
    @Test
    public void testGetRangeMinAndMax(){
        System.out.println("getRangeMinAndMax");
        String rangeValue = "123-456";
        String[] expResult = {"123", "456"};
        String[] result = Utility.getRangeMinAndMax(rangeValue);
        assertEquals(expResult[0], result[0]);
        assertEquals(expResult[1], result[1]);
    }
    
    /*
    * Test of getNumeratorAndDenominator
    */
    @Test
    public void testGetNumeratorAndDenominator(){
        System.out.println("getNumeratorAndDenominator");
        String fractionValue = "2/3";
        String[] expResult = {"2", "3"};
        String[] result = Utility.getNumeratorAndDenominator(fractionValue);
        assertEquals(expResult[0], result[0]);
        assertEquals(expResult[1], result[1]);
    }
    
    /*
    * Test of computeStringValues
    */
    @Test
    public void testComputeStringValues(){
        System.out.println("computeStringValues");
        String multiplicationString = "3x10";
        String expResult = "30.0";
        String result = Utility.computeStringValues(multiplicationString);
        assertEquals(expResult, result);
    }
    
    /*
    * Test of getNumberBeforeParantheses
    */
    @Test
    public void testGetNumberBeforeParantheses(){
        System.out.println("getNumberBeforeParantheses");
        String parentheses = "40 (other)";
        String expResult = "40";
        String result = Utility.getNumberBeforeParantheses(parentheses);
        assertEquals(expResult, result);
    }
    
    /*
    * Test of isBlank
    */
    @Test
    public void testIsBlank(){
        System.out.println("isBlank");
        String blankData = "N/A";
        String validData = "1234.00";
        assertTrue(Utility.isBlank(blankData));
        assertFalse(Utility.isBlank(validData));
    }
    
}
