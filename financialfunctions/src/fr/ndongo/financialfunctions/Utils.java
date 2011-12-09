package fr.ndongo.financialfunctions;

import java.text.DecimalFormat;

/**
 * Represents the class that contains the utils methods
 * @author yaya.ndongo
 *
 */
public class Utils {
	/**
	 * The precision for the round
	 */
	public static final int PRECISION = 2;
	/**
	 * Default value for an undefined field
	 */
	public static final int UNDEFINED = -1;
	
	/**
	 * Rounds the value to the given precision
	 * @param value
	 * @param precision
	 * @return
	 */
	public static double round(double value, int precision) {
		final String format = format(value, precision);
		try {
			return Double.valueOf(format).doubleValue();
		} catch (Exception e) {
			return Double.valueOf(format.replace(",", ".")).doubleValue();
		}
		
	}
	//return (double)( (int)(value * Math.pow(10,precision) + .5) ) / Math.pow(10,precision);
	
	/**
	 * Formats the value to the given precision
	 */
	public static String format(double value, int precision) {
		final StringBuilder pattern = new StringBuilder("########.");
		for (int i = 0; i < precision; i++) {
			pattern.append("0");
		}
		final DecimalFormat df = new DecimalFormat(pattern.toString());
		df.setMaximumFractionDigits(precision);
		final String format = df.format(value);
		return format;
	}
}
