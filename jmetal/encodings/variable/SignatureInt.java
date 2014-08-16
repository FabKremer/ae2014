package jmetal.encodings.variable;

import java.util.HashMap;

import jmetal.core.Variable;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;

/**
 * This class implements an integer decision encodings.variable
 */
public class SignatureInt extends Variable {
	
	private int value_;       //Stores the value of the encodings.variable
	private int lowerBound_;  //Stores the lower limit of the encodings.variable
	private int upperBound_;  //Stores the upper limit of the encodings.variable
	private int[] signatureCodes_ = {1937,1918,5914,1628,1438,1444,1849,9999,1445,1331,1939,5915,1631,1835,1941,1749,1316,1328,1333,1450,1057,1767,1340,1758,1442,1766,1751,1740,1759,1933,1731,1449,1448};
    
	/**
	 * Constructor
	 */
	public SignatureInt() {
		lowerBound_ = 1057 ;
		upperBound_ = 9999 ;
		value_      = 0    ;
	} // Int

	/**
	 * Constructor
	 * @param lowerBound Variable lower bound
	 * @param upperBound Variable upper bound
	 */
	public SignatureInt(int lowerBound, int upperBound){
		lowerBound_ = lowerBound;
		upperBound_ = upperBound;
		int aux = PseudoRandom.randInt(0,signatureCodes_.length-1);
		value_ = signatureCodes_[aux];
	} // Int

	/**
	 * Constructor
	 * @param value Value of the encodings.variable
	 * @param lowerBound Variable lower bound
	 * @param upperBound Variable upper bound
	 */
	public SignatureInt(int value, int lowerBound, int upperBound) {
		super();

		value_      = value      ;
		lowerBound_ = lowerBound ;
		upperBound_ = upperBound ;
	} // Int

	/**
	 * Copy constructor.
	 * @param variable Variable to be copied.
	 * @throws JMException 
	 */
	public SignatureInt(Variable variable) throws JMException{
		lowerBound_ = (int)variable.getLowerBound();
		upperBound_ = (int)variable.getUpperBound();
		value_ = (int)variable.getValue();        
	} // Int

	/**
	 * Returns the value of the encodings.variable.
	 * @return the value.
	 */
	public double getValue() {
		return value_;
	} // getValue

	/**
	 * Assigns a value to the encodings.variable.
	 * @param value The value.
	 */ 
	public void setValue(double value) {
		value_ = (int)value;
	} // setValue

	/**
	 * Creates an exact copy of the <code>Int</code> object.
	 * @return the copy.
	 */ 
	public Variable deepCopy(){
		try {
			return new Int(this);
		} catch (JMException e) {
			Configuration.logger_.severe("Int.deepCopy.execute: JMException");
			return null ;
		}
	} // deepCopy

	/**
	 * Returns the lower bound of the encodings.variable.
	 * @return the lower bound.
	 */ 
	public double getLowerBound() {
		return lowerBound_;
	} // getLowerBound

	/**
	 * Returns the upper bound of the encodings.variable.
	 * @return the upper bound.
	 */ 
	public double getUpperBound() {
		return upperBound_;
	} // getUpperBound

	/**
	 * Sets the lower bound of the encodings.variable.
	 * @param lowerBound The lower bound value.
	 */	    
	public void setLowerBound(double lowerBound)  {
		lowerBound_ = (int)lowerBound;
	} // setLowerBound

	/**
	 * Sets the upper bound of the encodings.variable.
	 * @param upperBound The new upper bound value.
	 */          
	public void setUpperBound(double upperBound) {
		upperBound_ = (int)upperBound;
	} // setUpperBound

	/**
	 * Returns a string representing the object
	 * @return The string
	 */ 
	public String toString(){
		return value_+"";
	} // toString
} // Int
