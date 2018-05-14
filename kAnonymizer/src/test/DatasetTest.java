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
import kAnonymizer.NumericGeneralizer;
import kAnonymizer.Tuple;

/**
 * @author Rob
 *
 */
class DatasetTest {

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
	void testSort() {
		int tuples = 20;
		
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
		
		System.out.println("Generating random dataset with " + tuples + " tuples...");
		
		ArrayList<ArrayList<String>> data = DatasetGenerator.generate(tuples, generalizer);
		
		for (ArrayList<String> t : data)
			System.out.println(t);
		
		System.out.println("All the generalizers: ");
		
		printGeneralizers(generalizer);
		
		Dataset dataset = new Dataset(data, generalizer);
		
		/*
		 * Test 0
		 */
		
		System.out.println("Testing dataset sort with active generalizers: ");
		
		printGeneralizers(dataset.getActiveGeneralizer());
		
		dataset.sort();
		
		printDataset(dataset);
		
		/*
		 * Test 1
		 */
		
		dataset.addActiveGeneralizers(0, 2, generalizer.get(0).get(2));
		dataset.addActiveGeneralizers(1, 1, generalizer.get(1).get(1));
		
		System.out.println("Testing dataset sort with active generalizers: ");
		
		printGeneralizers(dataset.getActiveGeneralizer());
		
		dataset.sort();
		
		printDataset(dataset);
		
		/*
		 * Test 2
		 */
		
		dataset.addActiveGeneralizers(generalizer);
		
		System.out.println("Testing dataset sort with active generalizers: ");
		
		printGeneralizers(dataset.getActiveGeneralizer());
		
		dataset.sort();
		
		printDataset(dataset);
		
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
	
	private static void printDataset(Dataset dataset) {
		ArrayList<Tuple> tupleDataset = dataset.getData();
		for (Tuple t : tupleDataset) 
			System.out.println(t.data);
	}
}
