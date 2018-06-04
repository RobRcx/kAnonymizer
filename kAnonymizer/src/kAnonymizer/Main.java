package kAnonymizer;

import java.io.IOException;
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
		if (args.length < 5) {
			System.out.println("Usage: kAnonymizer <kStart> <kEnd> <kStep> <dataset> <generalizers>");
			System.exit(-1);
		}
		
		Integer kStart, kEnd, kStep;
		kStart = kEnd = kStep = null;
		ArrayList<ArrayList<String>> dataset = null;
		
		/*
		 * Parsing k and opening the dataset
		 */
		
		try {
			kStart = Integer.parseInt(args[0]);
			kEnd = Integer.parseInt(args[1]);
			kStep = Integer.parseInt(args[2]);
			
			if (kStart < 1 || kEnd < 1 || kStep < 0) {
				throw new Exception("Error: The following must hold:"
						+ "kStart < 1, kEnd < 1, kStep < 0.\nQuitting.");
			}
			
			if (kStart < kEnd) {
				Integer tmp = kStart;
				kStart = kEnd;
				kEnd = tmp;
			}
			
			System.out.println("Loading tuples..." );
			dataset = readDataset(args[3]);
		} catch (NumberFormatException e) {
			System.out.println("Numeric value for k required.");
			System.exit(-2);
		} catch (IOException e) {
			System.out.println("Error opening dataset path specified by " + args[1]);
			System.exit(-3);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.exit(-2);
		} finally {
			System.out.println("k0 = " + kStart + " kFinal = " + kEnd + " kStep = " + kStep);
			System.out.println("Test set " + args[3] + " of " + dataset.size() + " tuples loaded.");
		}
		
		/*
		 * Opening and parsing the generalizers file
		 */
		
		ArrayList<ArrayList<Generalizer>> generalizer = null;
		try {
			generalizer = readGeneralizers(args[4]);
		} catch (Exception e) {
			System.out.println("Error opening " + args[4]);
			System.exit(-4);
		} finally {
			System.out.println("Generalizers found: ");
			for (ArrayList<Generalizer> generalizerArray : generalizer) {
				for (Generalizer g : generalizerArray) {
					System.out.print(g + "  ");
				}
				System.out.println("");
			}
		}
		
		/*
		 * Performs the k-anonymization over the values of k as specified by 
		 * the input interval.
		 */
		assert (kStart > kEnd);
	
		KAnonymizer kAnonymizer = new KAnonymizer(kStart, dataset, generalizer);
		
		while (kStart > kEnd) {
			if (kStart - kStep < kEnd)
				kStart = kEnd;
			else
				kStart = kStart - kStep;
			
			kAnonymizer.setK(kStart);
			System.out.println("k-anonymizing with k = " + kStart + "...");
			
			long startTime = System.currentTimeMillis();
			
			Long bestCost = kAnonymizer.kOptimize();
			
			long stopTime = System.currentTimeMillis();
			
			System.out.println("\nExecution ended.\nOptimal cost : " + bestCost 
					+ " obtained in " + ((stopTime - startTime) / 1000d) + " sec.");
			
			System.out.println("sortCounter: " + KAnonymizer.sortCounter);
		}
	}
	
	
	private static ArrayList<ArrayList<String>> readDataset(String filename) throws IOException {
		ArrayList<ArrayList<String>> output = new ArrayList<ArrayList<String>>();
		Files.lines(Paths.get(filename)).forEach(item->output.add(parseDatasetRow(item)));
		return output;
	}
	
	private static ArrayList<String> parseDatasetRow(String rawRow) {
		ArrayList<String> row = new ArrayList<String>();
		int firstSeparator = rawRow.indexOf(';', 0);
		int secondSeparator = rawRow.indexOf(';', firstSeparator + 1);
		row.add(rawRow.substring(0, firstSeparator));
		row.add(rawRow.substring(firstSeparator + 1, secondSeparator));
		row.add(rawRow.substring(secondSeparator + 1));
		return row;
	}
	
	private static ArrayList<ArrayList<Generalizer>> readGeneralizers(String filename) throws IOException  {
		ArrayList<ArrayList<Generalizer>> generalizer = new ArrayList<>();
		IntegerWrapper count = new IntegerWrapper(0);
		Files.lines(Paths.get(filename)).forEach(item->generalizer.add(parseGeneralizerRow(count, item)));
		return generalizer;
	}
	
	private static ArrayList<Generalizer> parseGeneralizerRow(IntegerWrapper count, String rawRow) {
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

	protected static class IntegerWrapper {
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

