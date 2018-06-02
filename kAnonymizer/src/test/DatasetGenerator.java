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
		Files.lines(Paths.get(filename)).forEach(item->System.out.println(item));
		return output;
	}
}
