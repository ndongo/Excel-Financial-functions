package fr.ndongo.financialfunctions.model;

import fr.ndongo.financialfunctions.Utils;
import fr.ndongo.financialfunctions.exception.MissingFieldException;


/**
 * Based on :
 * 		http://lecompagnon.info/intelligencefinanciere/mathematiquefianciere.htm#.TpP80JtEe4c
 * 		http://office.microsoft.com/fr-ca/excel-help/va-HP005209225.aspx
 * 		http://stackoverflow.com/questions/3198939/recreate-excel-rate-function-using-newtons-method
 * 
 * Allows to calculate each field based on the others fields if there is no missing one.
 * @author yaya.ndongo
 *
 */
public class Simulation {

	/**
	 * 
	 * Simulation fields
	 */
	public enum SimulationField{
		INITIAL_CAPITAL,
		MONTHLY_SAVINGS,
		OFFERING_PERIOD,
		AVERAGE_ANNUAL_RATE,
		FINAL_CAPITAL,
		UNDEFINED
	}

	private static final int MONTHS_PER_YEAR = 12;
	private static final int UNDEFINED = Utils.UNDEFINED;
	private static final double FINANCIAL_PRECISION = 0.00000001; //1.0e-08
	private static final double FINANCIAL_MAX_ITERATIONS = 128;
	/**
	 * PV  Initial Capital
	 */
	public double _pv; 
	/**
	 * PMT  Monthly Savings
	 */
	public double _pmt;
	/**
	 * NPR   Offering Period
	 */
	public float _npr; 
	/**
	 * Rate
	 */
	public double _rate;
	/**
	 * FV Final Capital
	 */
	public double _fv;
	
	/**
	 * Type (0 or 1)
	 */
	public int _type;
	
	public Simulation() {
		_pv =  UNDEFINED;
		_pmt = UNDEFINED;
		_npr = UNDEFINED;
		_rate = UNDEFINED;
		_fv = UNDEFINED;
		_type = 0;
	}
	
	/**
	 * Gets the missing field for the calculation of the given field
	 * @param field
	 * @return  the missing field
	 */
	public SimulationField getMissingField(SimulationField field) {
		switch (field) {
		case INITIAL_CAPITAL:
			if (_pmt == UNDEFINED ) {
				return SimulationField.MONTHLY_SAVINGS;
			}
			if (_npr == UNDEFINED) {
				return SimulationField.OFFERING_PERIOD;
			}
			if (_rate == UNDEFINED) {
				return SimulationField.AVERAGE_ANNUAL_RATE;
			}
			if (_fv == UNDEFINED) {
				return SimulationField.FINAL_CAPITAL;
			}
			return SimulationField.UNDEFINED;
		case MONTHLY_SAVINGS:
			if (_pv == UNDEFINED ) {
				return SimulationField.INITIAL_CAPITAL;
			}
			if (_npr == UNDEFINED) {
				return SimulationField.OFFERING_PERIOD;
			}
			if (_rate == UNDEFINED) {
				return SimulationField.AVERAGE_ANNUAL_RATE;
			}
			if (_fv == UNDEFINED) {
				return SimulationField.FINAL_CAPITAL;
			}
			return SimulationField.UNDEFINED;
		case OFFERING_PERIOD:
			if (_pv == UNDEFINED ) {
				return SimulationField.MONTHLY_SAVINGS;
			}
			if (_pmt == UNDEFINED) {
				return SimulationField.MONTHLY_SAVINGS;
			}
			if (_rate == UNDEFINED) {
				return SimulationField.AVERAGE_ANNUAL_RATE;
			}
			if (_fv == UNDEFINED) {
				return SimulationField.FINAL_CAPITAL;
			}
			return SimulationField.UNDEFINED;
		case AVERAGE_ANNUAL_RATE:
			if (_pv == UNDEFINED ) {
				return SimulationField.MONTHLY_SAVINGS;
			}
			if (_npr == UNDEFINED) {
				return SimulationField.OFFERING_PERIOD;
			}
			if (_pmt == UNDEFINED) {
				return SimulationField.MONTHLY_SAVINGS;
			}
			if (_fv == UNDEFINED) {
				return SimulationField.FINAL_CAPITAL;
			}
			return SimulationField.UNDEFINED;
		case FINAL_CAPITAL:
			if (_pv == UNDEFINED ) {
				return SimulationField.MONTHLY_SAVINGS;
			}
			if (_npr == UNDEFINED) {
				return SimulationField.OFFERING_PERIOD;
			}
			if (_rate == UNDEFINED) {
				return SimulationField.AVERAGE_ANNUAL_RATE;
			}
			if (_pmt == UNDEFINED) {
				return SimulationField.MONTHLY_SAVINGS;
			}
			return SimulationField.UNDEFINED;
		default:
			return SimulationField.UNDEFINED;
		}
	}
	

	/**
	 * Calculates the final capital (fv)
	 * @throws MissingFieldException
	 */
	public void calculateFinalCapital() throws MissingFieldException {
		if (isFieldMissing(SimulationField.FINAL_CAPITAL)) {
			throw  new MissingFieldException();
		}
		double effectiveRate = getEffectiveRate();
		double pow = Math.pow((1 + effectiveRate), (_npr * MONTHS_PER_YEAR));
		
		double vf =  _pv * pow;
		double inter =  _pmt  * (1 + effectiveRate * _type)* ( (pow - 1) / effectiveRate);
		
		_fv = Utils.round(vf + inter, Utils.PRECISION);// bg.doubleValue();
	}

	
	/**
	 * calculates the initial capital (pv)
	 * @throws MissingFieldException
	 */
	public void calculateInitialCapital() throws MissingFieldException {
		if (isFieldMissing(SimulationField.INITIAL_CAPITAL)) {
			throw  new MissingFieldException();
		}
		
		double effectiveRate = getEffectiveRate();
		// pow = (effRate +1)^(npr * MONTHS)
		//
		double pow = Math.pow((1 + effectiveRate), (_npr * MONTHS_PER_YEAR));
		// inter = -pmt *( 1 + effRate * type) * (pow -1)/effRate + fv
		//
		double inter =  -_pmt * (1 + effectiveRate * _type) * ((pow - 1)/effectiveRate) + _fv;
		// va = inter/pow
		//
		_pv = Utils.round(inter / pow,Utils.PRECISION);
	}
	
	/**
	 * Calculates the monthly savings (pmt)
	 * @throws MissingFieldException
	 */
	public void calculateMonthlySavings() throws MissingFieldException {
		if (isFieldMissing(SimulationField.MONTHLY_SAVINGS)) {
			throw  new MissingFieldException();
		}
		double effectiveRate = getEffectiveRate();
		// pow = (effRate +1)^(npr * MONTHS)
		//
		double pow = Math.pow((1 + effectiveRate), (_npr * MONTHS_PER_YEAR));
		
		double inter = effectiveRate * ( -_fv + (_pv * pow) );
		_pmt = -( inter / ( pow - 1) * (1 + effectiveRate * _type) );
		_pmt = Utils.round(_pmt, Utils.PRECISION);
	}
	
	/**
	 * Calculates the offering period ( npr)
	 * @throws MissingFieldException
	 */
	public void calculateOfferingPeriod() throws MissingFieldException {
		if (isFieldMissing(SimulationField.OFFERING_PERIOD)) {
			throw  new MissingFieldException();
		}
		double effectiveRate = getEffectiveRate();
		// (-te * fv) - pmt = a
		//
		double a = (- effectiveRate * _fv) -  _pmt * (1 + effectiveRate * _type);
		double b = (effectiveRate * (- _pv)) - _pmt * (1 + effectiveRate * _type);
		// y = a/b
		//
		double y = a /b;
		// x = (1 +effRate)
		//
		double x = 1 + effectiveRate;
		
		// x^pow = y => pow = ln(y)/ln(x)
		//
		double pow = Math.log(y)/Math.log(x);
		
		_npr = (float)Utils.round(pow/MONTHS_PER_YEAR, Utils.PRECISION);
	}
	
	/**
	 * Calculates the average annual rate of return (rate)
	 * @throws MissingFieldException
	 */
	public void calculateAverageAnnualRateOfReturn() throws MissingFieldException {
		if (isFieldMissing(SimulationField.AVERAGE_ANNUAL_RATE)) {
			throw  new MissingFieldException();
		}
		double effectiveRate = rate( _npr * MONTHS_PER_YEAR, -_pmt, -_pv, _fv, _type);
		
		// (((1+ effectiveRate/(100*MOUNTHS))^MOUNTHS)-1)
		//
		_rate = Math.pow(1+effectiveRate, MONTHS_PER_YEAR) - 1;
		_rate = Utils.round(_rate, Utils.PRECISION + 1);
	}
	
	/**
	 * Calculates the interests received for offering period
	 * @return the amount of interests
	 * @throws MissingFieldException
	 */
	public double getReceivedInterest() throws MissingFieldException{
		if (isFieldMissing(SimulationField.FINAL_CAPITAL)) {
			throw  new MissingFieldException();
		}
		double value = _fv - getAccumulatedSavings();
		return Utils.round(value, Utils.PRECISION); 
	}
	
	/**
	 * Calculates the savings that have been accumulated for the offering period
	 * @return the amount of accumulated savings
	 * @throws MissingFieldException
	 */
	public double getAccumulatedSavings() throws MissingFieldException{
		if (isFieldMissing(SimulationField.INITIAL_CAPITAL)) {
			throw  new MissingFieldException();
		}
		if (isFieldMissing(SimulationField.MONTHLY_SAVINGS)) {
			throw  new MissingFieldException();
		}
		if (isFieldMissing(SimulationField.OFFERING_PERIOD)) {
			throw  new MissingFieldException();
		}
		double value = _pv + (_pmt * _npr * MONTHS_PER_YEAR);
		return Utils.round(value, Utils.PRECISION); 
	}
	
	

	/**
	 * Is there a missing field for the calculation of the given one ?
	 * @param field  field
	 * @return true if there a missing field, false otherwise
	 */
	protected boolean isFieldMissing(SimulationField field) {
		switch (field) {
		case INITIAL_CAPITAL:
			return _pmt == UNDEFINED 
				|| _npr == UNDEFINED
				|| _rate == UNDEFINED
				|| _fv == UNDEFINED;
		case MONTHLY_SAVINGS:
			return _pv == UNDEFINED 
			|| _npr == UNDEFINED
			|| _rate == UNDEFINED
			|| _fv == UNDEFINED;
		case OFFERING_PERIOD:
			return _pv == UNDEFINED 
			|| _pmt == UNDEFINED
			|| _rate == UNDEFINED
			|| _fv == UNDEFINED;
		case AVERAGE_ANNUAL_RATE:
			return _pv == UNDEFINED 
			|| _npr == UNDEFINED
			|| _pmt == UNDEFINED
			|| _fv == UNDEFINED;
		case FINAL_CAPITAL:
			return _pv == UNDEFINED 
			|| _npr == UNDEFINED
			|| _rate == UNDEFINED
			|| _pmt == UNDEFINED;
		default:
			return false;
		}
	}
	
	/**
	 * Based on http://stackoverflow.com/questions/7064753/problem-with-rate-function
	 * @param npr  NPR
	 * @param pmt  PMT
	 * @param pv    PV
	 * @param fv    FV
	 * @param type  Type
	 * @return the rate
	 */
	private static double rate(double npr, double pmt, double pv, double fv, int type){
		double rate = 0.1;
		double y;
		double f = 0.0;
		if (Math.abs(rate) < FINANCIAL_PRECISION) {
	        y = pv * (1 + npr * rate) + pmt * (1 + rate  * type) * npr + fv;
	    } else {
	        f = Math.exp(npr * Math.log(1 + rate));
	        y = pv * f + pmt * (1 / rate + type) * (f - 1) + fv;
	    }
		
		double y0 = pv + pmt * npr + fv;
	    double y1 = pv * f + pmt * (1 / rate + type) * (f - 1) + fv;

	    // find root by secant method
		int i = 0;
	    double x0 = 0.0;
	    double x1 = rate;
		while ((Math.abs(y0 - y1) > FINANCIAL_PRECISION) && (i < FINANCIAL_MAX_ITERATIONS)) {
			rate = (y1 * x0 - y0 * x1) / (y1 - y0);
			x0 = x1;
			x1 = rate;

			if (Math.abs(rate) < FINANCIAL_PRECISION) {
	            y = pv * (1 + npr * rate) + pmt * (1 + rate  * type) * npr + fv;
			} else {
				f = Math.exp(npr * Math.log(1 + rate));
				y = pv * f + pmt * (1 / rate + type) * (f - 1) + fv;
			}

			y0 = y1;
			y1 = y;
			i++;
	    }
		return rate;
	}
	
	private double getEffectiveRate() {
		double rate = _rate + 1;
		double power = (double) 1/MONTHS_PER_YEAR;
		double res =( Math.pow(rate, power) - 1 );
		return res;
	}
	
}
