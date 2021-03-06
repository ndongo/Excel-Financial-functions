package fr.ndongo.financialfunctions.model;

import junit.framework.TestCase;

import fr.ndongo.financialfunctions.model.Simulation.SimulationField;

public class SimulationTest extends TestCase {
	

	public void testCheckMissingField(){
		Simulation simulation = new Simulation();
		
		assertTrue(simulation.isFieldMissing(SimulationField.INITIAL_CAPITAL));
		assertTrue(simulation.isFieldMissing(SimulationField.MONTHLY_SAVINGS));
		assertTrue(simulation.isFieldMissing(SimulationField.OFFERING_PERIOD));
		assertTrue(simulation.isFieldMissing(SimulationField.AVERAGE_ANNUAL_RATE));
		assertTrue(simulation.isFieldMissing(SimulationField.FINAL_CAPITAL));
		
		simulation._pv = 100000; 
		simulation._pmt = 100;
		simulation._npr = 8;
		simulation._rate = 0.01;
		simulation._fv = 109600.96f;
		
		assertFalse(simulation.isFieldMissing(SimulationField.INITIAL_CAPITAL));
		assertFalse(simulation.isFieldMissing(SimulationField.MONTHLY_SAVINGS));
		assertFalse(simulation.isFieldMissing(SimulationField.OFFERING_PERIOD));
		assertFalse(simulation.isFieldMissing(SimulationField.AVERAGE_ANNUAL_RATE));
		assertFalse(simulation.isFieldMissing(SimulationField.FINAL_CAPITAL));
	}
	

	public void testCalculateFinalCapital(){
		Simulation simulation = new Simulation();
		simulation._pv = 120000; 
		simulation._pmt = 100;
		simulation._npr = 8;
		simulation._rate = 0.01;
		
		try {
			simulation.calculateFinalCapital();
			assertEquals(139931.10, simulation._fv);
		} catch (Exception e) {
		    fail("Failed to calculate the final capital");
		}
	}
	

	public void testCalculateInitialCapital()
	{
		Simulation simulation = new Simulation();
		simulation._fv = 10000; 
		simulation._pmt = 50;
		simulation._npr = 8;
		simulation._rate = 0.04; //4%
		
		try {
			simulation.calculateInitialCapital();
			assertEquals(3193.72, simulation._pv);
		} catch (Exception e) {
            fail("Failed to calculate the Initial capital");
		}
	}
	

	public void testCalculateMonthlySavings(){
		Simulation simulation = new Simulation();
		simulation._pv = 100000; 
		simulation._fv = 130000; 
		simulation._npr = 5;
		simulation._rate = 0.04; //4%
		
		try {
			simulation.calculateMonthlySavings();
			assertEquals(125.94, simulation._pmt);
		} catch (Exception e) {
            fail("Failed to calculate the monthly savings ");
		}
	}


	public void testCalculateOfferingPeriod(){
		Simulation simulation = new Simulation();
		simulation._pv = 10000; 
		simulation._fv = 100000; 
		simulation._pmt = 80;
		simulation._rate = 0.05; 
		
		try {
			simulation.calculateOfferingPeriod();
			assertEquals(28.6f, simulation._npr);
		} catch (Exception e) {
            fail("Failed to calculate the offering period ");
		}
	}
	

	public void testCalculateAverageAnnualRateOfReturn(){
		Simulation simulation = new Simulation();
		simulation._pv = 20000; 
		simulation._fv = 40000; 
		simulation._pmt = 80;
		simulation._npr = 8; 
		
		try {
			simulation.calculateAverageAnnualRateOfReturn();
			assertEquals(0.054, simulation._rate);
		} catch (Exception e) {
            fail("Failed to calculate the average annual rate of return ");
		}
	}
	

	public void testGetAccumulatedSavings(){
		Simulation simulation = new Simulation();
		simulation._pv = 120000; 
		simulation._pmt = 100;
		simulation._npr = 8;
		simulation._rate = 0.01;
		simulation._fv = 139931.10;
		try {
			assertEquals(129600.00, simulation.getAccumulatedSavings());
		} catch (Exception e) {
            fail("Failed to calculate the accumulated savings");
		}
	}
	

	public void testGetReceivedInterest(){
		Simulation simulation = new Simulation();
		simulation._pv = 120000; 
		simulation._pmt = 100;
		simulation._npr = 8;
		simulation._rate = 0.01;
		simulation._fv = 139931.10;
		try {
			assertEquals(10331.10, simulation.getReceivedInterest());
		} catch (Exception e) {
            fail("Failed to calculate the  received interest ");
		}
	}

}
