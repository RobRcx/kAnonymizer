package test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.function.Supplier;

import kAnonymizer.CategoricGeneralizer;
import kAnonymizer.Dataset;
import kAnonymizer.Dataset.ArrayListStringRef;
import kAnonymizer.NumericGeneralizer;
import kAnonymizer.Generalizer;
import kAnonymizer.Tuple;



public class DatasetGenerator {
	
	// TODO: Implement main in KAnonymizer test JUnit module instead
	public static void main(String args[]) {
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
		ArrayList<ArrayList<String>> data = generate(100, generalizer);
		
		for (ArrayList<String> t : data)
			System.out.println(t);
		
		Dataset dataset = new Dataset(data, generalizer);
		dataset.addActiveGeneralizers(0, generalizer.get(0).get(2));
		dataset.addActiveGeneralizers(1, generalizer.get(1).get(1));
		dataset.sort();
		
		ArrayList<Tuple> tupleDataset = dataset.data;
		for (Tuple t : tupleDataset)
			System.out.println(t.data);
	}
	
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
					generalizer.get(1).get(Math.abs(random.nextInt()) % 2);
			data.add(cg.value);
			
			cg = (CategoricGeneralizer) 
					generalizer.get(2).get(Math.abs(random.nextInt()) % 4);
			data.add(cg.value);
			
			dataset.add(data);
		}
		return dataset;
	}
}
