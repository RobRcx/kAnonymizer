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
		ArrayList<ArrayList<String>> dataset = readDataset(args[1]);
		ArrayList<Type> attributeType = new ArrayList<Type>(
				Arrays.asList(new Type[] {Type.NUMERIC, Type.STRING, Type.CATEGORICAL}));
		
		// TODO: read generalizations from file
		String[] ageGeneralizers = new String[] {"10 <= 19", "20 <= 39", "40 <= 49"};
		String[] genderGeneralizers = new String[] {"M", "F"};
		String[] statusGeneralizers = new String[] {"Married", "Widowed", "Divorced", "Never Married"};
		ArrayList<ArrayList<String>> generalizer = new ArrayList<ArrayList<String>>() {{
			add(new ArrayList<String>(Arrays.asList(ageGeneralizers)));
			add(new ArrayList<String>(Arrays.asList(genderGeneralizers)));
			add(new ArrayList<String>(Arrays.asList(statusGeneralizers)));
		}};
		
		
		KAnonymizer kAnonymizer = new KAnonymizer(k, dataset, attributeType, generalizer);
		kAnonymizer.kOptimize();
	}
	
	public static ArrayList<ArrayList<String>> readDataset(String datasetPath) {
		ArrayList<ArrayList<String>> dataset = new ArrayList<ArrayList<String>>();
		return dataset;
	}

}
