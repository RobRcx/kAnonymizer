/**
 * 
 */
package test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import kAnonymizer.CategoricGeneralizer;
import kAnonymizer.Dataset;
import kAnonymizer.Generalizer;
import kAnonymizer.KAnonymizer;
import kAnonymizer.NumericGeneralizer;
import kAnonymizer.Tuple;

/**
 * @author Rob
 *
 */
class KAnonymizerTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void test() throws Exception {
		
		
		ArrayList<ArrayList<Generalizer>> generalizer = new ArrayList<ArrayList<Generalizer>>() {{
			add( new ArrayList<Generalizer>(Arrays.asList(new NumericGeneralizer[] { 
					new NumericGeneralizer("1", 10, 29), 
					new NumericGeneralizer("2", 30, 39),
					new NumericGeneralizer("3", 40, 49)
			})));
			add( new ArrayList<Generalizer>(Arrays.asList(new CategoricGeneralizer[] { 
					new CategoricGeneralizer("4", "M"), 
					new CategoricGeneralizer("5", "F")
			})));
			add( new ArrayList<Generalizer>(Arrays.asList(new CategoricGeneralizer[] { 
					new CategoricGeneralizer("6", "Married"), 
					new CategoricGeneralizer("7", "Widowed"),
					new CategoricGeneralizer("8", "Divorced"), 
					new CategoricGeneralizer("9", "Never Married")
			})));
		}};
		
		int tuples = 3500;
		System.out.println("Generating random dataset with " + tuples + " tuples...");
		ArrayList<ArrayList<String>> data = DatasetGenerator.generate(tuples, generalizer);
		System.out.println("Writing to file \"dataset3.csv\"...");
	    DatasetWriter.writeToFile("dataset3.csv", DatasetWriter.generateCSV(data));
		
		/*String filename = "dataset2.csv";
		System.out.println("Loading tuples..." );
		ArrayList<ArrayList<String>> data = DatasetGenerator.generateFromFile(filename);
		System.out.println("Test set " + filename + " of " + data.size() + " tuples loaded.");*/
		//for (ArrayList<String> t : data) System.out.println(t);
		
		System.out.println("All the generalizers: ");
		
		printGeneralizers(generalizer);
		
		ArrayList<Integer> kArray = new ArrayList<Integer>
				//(Arrays.asList(new Integer[] {5000, 2500, 1000, 500, 250, 100, 50, 10, 5, 2}));
				//(Arrays.asList(new Integer[] {5000, 2500, 1000, 500, 250, 100, 50, 10, 5, 2}));
				(Arrays.asList(new Integer[] {800}));
		/*
		 * Test phase
		 */
		
		for (int k : kArray) {
			System.out.println("k-anonymizing with k = " + k + "...");
			
			long startTime = System.currentTimeMillis();
			
			KAnonymizer kAnonymizer = new KAnonymizer(k, data, generalizer);
			Long bestCost = kAnonymizer.kOptimize();
			
			long stopTime = System.currentTimeMillis();
			
			System.out.println("\nExecution ended.\nOptimal cost : " + bestCost 
					+ " obtained in " + ((stopTime - startTime) / 1000d) + " sec.");
			
			System.out.println("sortCounter: " + KAnonymizer.sortCounter);
		}		
	}

	private static void printGeneralizers(ArrayList<ArrayList<Generalizer>> generalizer) {
		for (ArrayList<Generalizer> a : generalizer) {
			for (Generalizer g : a) {
				if (g != null)
					System.out.print(g.toString() + "   ");
			}
			System.out.println("");
		}
	}
	
}
