package test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import kAnonymizer.CategoricGeneralizer;
import kAnonymizer.Dataset;
import kAnonymizer.NumericGeneralizer;
import kAnonymizer.Generalizer;
import kAnonymizer.Tuple;



public class DatasetGenerator {

	public static ArrayList<ArrayList<String>> generate(int tupleQuantity, 
			ArrayList<ArrayList<Generalizer>> generalizer) {
		ArrayList<ArrayList<String>> dataset = new ArrayList<ArrayList<String>>();
		Random random = new Random();
		for (int i = 0; i < tupleQuantity; i++) {
			ArrayList<String> data = new ArrayList<String>();
			
			NumericGeneralizer ng = (NumericGeneralizer) 
					generalizer.get(0).get(Math.abs(random.nextInt()) % generalizer.get(0).size());
			data.add(String.valueOf(random.nextInt((ng.ub - ng.lb) + 1) + ng.lb));
			
			
			CategoricGeneralizer cg = (CategoricGeneralizer) 
					generalizer.get(1).get(Math.abs(random.nextInt()) % generalizer.get(1).size());
			data.add(cg.value);
			
			cg = (CategoricGeneralizer) 
					generalizer.get(2).get(Math.abs(random.nextInt()) % generalizer.get(2).size());
			data.add(cg.value);
			
			dataset.add(data);
		}
		return dataset;
	}
	
	// to be finished
	public static ArrayList<ArrayList<String>> generateFromFile(String filename) throws Exception{
		ArrayList<ArrayList<String>> output = new ArrayList<ArrayList<String>>();
		Files.lines(Paths.get(filename)).forEach(item->output.add(parseRow(item)));
		return output;
	}
	
	private static ArrayList<String> parseRow(String rawRow){
		ArrayList<String> row = new ArrayList<String>();
		int firstSeparator = rawRow.indexOf(';', 0);
		int secondSeparator = rawRow.indexOf(';', firstSeparator + 1);
		row.add(rawRow.substring(0, firstSeparator));
		row.add(rawRow.substring(firstSeparator + 1, secondSeparator));
		row.add(rawRow.substring(secondSeparator + 1));
		return row;
	}
	
}
