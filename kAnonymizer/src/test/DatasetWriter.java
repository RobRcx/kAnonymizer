package test;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

import kAnonymizer.CategoricGeneralizer;
import kAnonymizer.Generalizer;
import kAnonymizer.NumericGeneralizer;

public class DatasetWriter {
	
	public static void main(String[] args) {
		int tuples = 2000000;
		
		@SuppressWarnings("serial")
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
		
		ArrayList<ArrayList<String>> dataset = DatasetGenerator.generate(tuples, generalizer);
		ArrayList<String> CSVDataset = generateCSV(dataset);
		try {
			writeToFile("dataset.csv", CSVDataset);
		} catch (Exception e) {
			System.err.println("Something went wrong while creating output file...");
			System.err.println("Error message: " + e.getMessage());
		}
		System.out.println("Creation succeed.");
	}
	
	public static ArrayList<String> 
		generateCSV(ArrayList<ArrayList<String>> dataset){
		ArrayList<String> output = new ArrayList<String>();
		for(ArrayList<String> row : dataset) {
			StringBuilder csvRow = new StringBuilder();
			for(String element: row) {
				csvRow.append(element + ";");
			}
			csvRow = csvRow.deleteCharAt(csvRow.length()-1);
			csvRow.append("\r\n");
			output.add(csvRow.toString());
		}
		return output;
	}
	
	public static void writeToFile(String filename, ArrayList<String> content) throws Exception {
		PrintWriter writer = new PrintWriter(filename, "UTF-8");
		for(String row : content) {
			writer.print(row);
		}
		writer.close();
	}
}
