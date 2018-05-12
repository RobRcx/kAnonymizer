package kAnonymizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * 
 * Aloha, chico.
 * Esto programa, es muy bueno.
 * Mucho tacos, mucho gusto, mucho love.
 * 
 * @author Gino
 *
 */

public class Main {

	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Usage: kAnonymizer <dataset> <k>");
			System.exit(-1);
		}
		
		Integer k = null;
		try {
			k = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			System.out.println("Numeric value for k required.");
			System.exit(-2);
		}
		
		// TODO: read dataset into generic ArrayLists, and then 
		// parse values type in KAnonymizer.java
		String path = args[1];
		ArrayList<ArrayList<String>> dataset = readDataset(path);
		
		// TODO: read generalizations from file
		
		ArrayList<ArrayList<Generalizer>> generalizer = new ArrayList<ArrayList<Generalizer>>() {{
			add( new ArrayList<Generalizer>(Arrays.asList(new NumericGeneralizer[] { 
					new NumericGeneralizer("1", 10, 29), new NumericGeneralizer("2", 30, 39),
					new NumericGeneralizer("3", 40, 49)
			})));
			add( new ArrayList<Generalizer>(Arrays.asList(new CategoricGeneralizer[] { 
					new CategoricGeneralizer("4", "M"), new CategoricGeneralizer("5", "F")
			})));
			add( new ArrayList<Generalizer>(Arrays.asList(new CategoricGeneralizer[] { 
					new CategoricGeneralizer("6", "Married"), new CategoricGeneralizer("7", "Widowed"),
					new CategoricGeneralizer("8", "Divorced"), new CategoricGeneralizer("9", "Never Married")
			})));
		}};
		
		KAnonymizer kAnonymizer = new KAnonymizer(k, dataset, generalizer);
		kAnonymizer.kOptimize();
	}
	
	public static ArrayList<ArrayList<String>> readDataset(String datasetPath) {
		ArrayList<ArrayList<String>> dataset = new ArrayList<ArrayList<String>>();
		// TODO: read from file
		return dataset;
	}

}
