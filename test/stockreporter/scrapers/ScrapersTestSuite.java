/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stockreporter.scrapers;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 *
 * @author Jared Smith
 */
@RunWith(Suite.class)
//Investopedia Tests commented out 8/2/19 after Investopedia changed its website layout
@Suite.SuiteClasses({/*stockreporter.scrapers.InvestopediaScraperTest.class,
                     stockreporter.scrapers.InvestopediaScraperLiveDataTest.class, */
                     stockreporter.scrapers.YahooScraperTest.class,
                     stockreporter.scrapers.YahooScraperLiveDataTest.class})
public class ScrapersTestSuite {

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }
    
}
