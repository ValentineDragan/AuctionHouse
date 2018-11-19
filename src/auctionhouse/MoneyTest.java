/**
 * 
 */
package auctionhouse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author pbj
 *
 */
public class MoneyTest {

    @Test    
    public void testAdd() {
        Money val1 = new Money("12.34");
        Money val2 = new Money("0.66");
        Money result = val1.add(val2);
        assertEquals("13.00", result.toString());
    }

    /*
     ***********************************************************************
     * BEGIN MODIFICATION AREA
     ***********************************************************************
     * Add all your JUnit tests for the Money class below.
     */

    @Test
    public void testValueRoundedDown() {
    	Money money = new Money("0.123");
    	assertEquals("0.12", money.toString());
    }
    
    @Test
    public void testValueRoundedUp() {
    	Money money = new Money("0.127");
    	assertEquals("0.13", money.toString());
    }
    
    @Test
    public void testSubstractNoRounding() {
    	Money val1 = new Money("12.34");
    	Money val2 = new Money("0.66");
    	Money result = val1.subtract(val2);
    	assertEquals("11.68", result.toString());
    }

    @Test
    public void testSubstractWithRounding() {
    	Money val1 = new Money("12.345");
    	Money val2 = new Money("0.663");
    	Money result = val1.subtract(val2);
    	assertEquals("11.69", result.toString());
    }
    
    @Test
    public void testAddWithRounding() {
    	Money val1 = new Money("12.379");
    	Money val2 = new Money("0.662");
    	Money result = val1.add(val2);
    	assertEquals("13.04", result.toString());
    }
    
    @Test
    public void testAddPercentNoRounding() {
    	Money money = new Money("80");
    	Money result = money.addPercent(10);
    	assertEquals("88.00", result.toString());
    }
    
    @Test
    public void testAddPercentWithRounding() {
    	Money money = new Money("80.12");
    	Money result = money.addPercent(10);
    	assertEquals("88.13", result.toString());
    }
    

    @Test 
    public void testCompareEqual() {
    	Money val1 = new Money("13.135");
    	Money val2 = new Money("13.14");
    	assertEquals(val1, val2);
    }
    
    @Test 
    public void testCompareLess() {
    	Money val1 = new Money("13.129");
    	Money val2 = new Money("13.139");
    	assertEquals(val1.compareTo(val2), -1);
    }
    
    @Test 
    public void testCompareBigger() {
    	Money val1 = new Money("0.89");
    	Money val2 = new Money("0.57");
    	assertEquals(val1.compareTo(val2), 1);
    }
    
    @Test 
    public void testLessEqual() {
    	Money val1 = new Money("0.89");
    	Money val2 = new Money("0.57");
    	assertTrue(val2.lessEqual(val1));
    }
    
    @Test 
    public void testEquals() {
    	Money val1 = new Money("0.65");
    	Money val2 = new Money("0.65");
    	assertTrue(val1.equals(val2));
    }
    /*
     * Put all class modifications above.
     ***********************************************************************
     * END MODIFICATION AREA
     ***********************************************************************
     */


}
