/**
 * Represents a value of Money in pounds.
 * The class has methods to compare it to other Money objects, or alter (normalise or round) its own Money value
 */
package auctionhouse;

/**
 * @author pbj
 */
public class Money implements Comparable<Money> {
 
	/**
	 * the amount of money that this object stores.
	 */
    private double value;
    
    /**
     * Converts an amount in pounds to a long-value in pence, rounded to the nearest pence
     * @param pounds amount to be converted
     * @return the amount in pence, rounded to the nearest pence
     */
    private static long getNearestPence(double pounds) {
        return Math.round(pounds * 100.0);
    }
 
    /**
     * Normalises an amount in pounds, keeping only two rounded decimals
     * @param pounds amount to be normalised
     * @return the amount in pounds, normalised with only two decimals
     */
    private static double normalise(double pounds) {
        return getNearestPence(pounds)/100.0;
        
    }
 
    /**
     * Constructor, which normalises the pounds value
     * @param pounds
     */
    public Money(String pounds) {
        value = normalise(Double.parseDouble(pounds));
    }
    
    private Money(double pounds) {
        value = pounds;
    }
    
    /**
     * Adds some Money to the current Money
     * @param m the Money to be added
     * @return a Money object whose value is the sum of the two Money values
     */
    public Money add(Money m) {
        return new Money(value + m.value);
    }
    
    /**
     * Subtracts some Money from the current Money
     * @param m the Money to be subtracted
     * @return a Money object whose value is the difference of the two Money values
     */
    public Money subtract(Money m) {
        return new Money(value - m.value);
    }
 
    /**
     * Adds a % percent of Money to the current Money
     * @param percent between 0 and 1, to be added
     * @return a Money object whose value equals the previous value * (1 + percent)
     */
    public Money addPercent(double percent) {
        return new Money(normalise(value * (1 + percent/100.0)));
    }
     

    /**
     * @return the Money value in String format with two decimals
     */
    @Override
    public String toString() {
        return String.format("%.2f", value);
        
    }
    
    /**
     * Compares the current Money with a second Money object, rounded to the nearest pence
     * @param m the second Money to compare
     * @return 0 if the two Money values are equal; 
     *         a value less than 0 if the first value < the second;
     *         a value greater than 0 if the first value > the second
     */
    public int compareTo(Money m) {
        return Long.compare(getNearestPence(value),  getNearestPence(m.value)); 
    }
    
    /**
     * Checks if the current Money is less or equal in value to a second Money object
     * @param m the second Money to compare 
     * @return True if the first value <= the second
     * 	       False if the first value > the second
     */
    public Boolean lessEqual(Money m) {
        return compareTo(m) <= 0;
    }
    
    
    /**
     * Checks if two Money objects are equal in value
     * @param o the second Money object
     * @return True if the two Money objects have the same value, rounded to the nearest pence; False otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Money)) return false;
        Money oM = (Money) o;
        return compareTo(oM) == 0;       
    }
    
    @Override
    public int hashCode() {
        return Long.hashCode(getNearestPence(value));
    }
      

}
