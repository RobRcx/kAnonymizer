package kAnonymizer;

import java.nio.file.Files;
import java.nio.file.Paths;
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
		if (args.length < 3) {
			System.out.println("Usage: kAnonymizer <k> <dataset> <generalizers>");
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
		String datasetPath = args[1];
		ArrayList<ArrayList<String>> dataset = null;
		try {
			dataset = readDataset(datasetPath);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			System.out.println("Error opening " + datasetPath);
			System.exit(-2);
		}
		
		String generalizersPath = args[2];
		ArrayList<ArrayList<Generalizer>> generalizer = null;
		try {
			generalizer = readGeneralizers(generalizersPath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Error opening " + generalizersPath);
			System.exit(-2);
		} finally {
			System.out.println("Generalizers found: ");
			for (ArrayList<Generalizer> generalizerArray : generalizer) {
				for (Generalizer g : generalizerArray) {
					System.out.print(g + "  ");
				}
				System.out.println("");
			}
		}
		
		// TODO: read generalizations from file
		
		/*ArrayList<ArrayList<Generalizer>> generalizer = new ArrayList<ArrayList<Generalizer>>() {{
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
		}};*/
		
		KAnonymizer kAnonymizer = new KAnonymizer(k, dataset, generalizer);
		
		long startTime = System.currentTimeMillis();
		
		Long bestCost = kAnonymizer.kOptimize();
		
		long stopTime = System.currentTimeMillis();
		
		System.out.println("\nExecution ended.\nOptimal cost : " + bestCost 
				+ " obtained in " + ((stopTime - startTime) / 1000d) + " sec.");
		
		System.out.println("sortCounter: " + KAnonymizer.sortCounter);
	}
	
	
	public static ArrayList<ArrayList<String>> readDataset(String filename) throws Exception{
		ArrayList<ArrayList<String>> output = new ArrayList<ArrayList<String>>();
		Files.lines(Paths.get(filename)).forEach(item->output.add(parseDatasetRow(item)));
		return output;
	}
	
	private static ArrayList<String> parseDatasetRow(String rawRow){
		ArrayList<String> row = new ArrayList<String>();
		int firstSeparator = rawRow.indexOf(';', 0);
		int secondSeparator = rawRow.indexOf(';', firstSeparator + 1);
		row.add(rawRow.substring(0, firstSeparator));
		row.add(rawRow.substring(firstSeparator + 1, secondSeparator));
		row.add(rawRow.substring(secondSeparator + 1));
		return row;
	}
	
	public static ArrayList<ArrayList<Generalizer>> readGeneralizers(String filename) throws Exception{
		ArrayList<ArrayList<Generalizer>> generalizer = new ArrayList<>();
		IntegerWrapper count = new IntegerWrapper(0);
		Files.lines(Paths.get(filename)).forEach(item->generalizer.add(parseGeneralizerRow(count, item)));
		return generalizer;
	}
	
	private static ArrayList<Generalizer> parseGeneralizerRow(IntegerWrapper count, String rawRow){
		ArrayList<Generalizer> row = new ArrayList<>();
		String[] res = rawRow.split("\\s+");
		if (res[0].equals("Numeric") ) {
			if (res.length % 2 == 0)
				return row;
			for (int i = 1; i < res.length - 1; i += 2) {
				count.incrementByOne();
				row.add(new NumericGeneralizer(count.getValue().toString(), 
						Integer.parseInt(res[i]), Integer.parseInt(res[i+1])));
			}
		} else if (res[0].equals("Categoric")) {
			for (int i = 1; i < res.length; i++) {
				count.incrementByOne();
				row.add(new CategoricGeneralizer(count.getValue().toString(), res[i].replace('_', ' ')));
			}
		}
		return row;
	}

	protected static class IntegerWrapper{
		
		private Integer value;

		public IntegerWrapper(int value) {
			this.value = value;
		}
		
		public void incrementByOne() {
			this.value++;
		}
		
		public Integer getValue() {
			return this.value;
		}

		public void setValue(Integer value) {
			this.value = value;
		}
		
	}
	
}

