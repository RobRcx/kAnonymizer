package kAnonymizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class KAnonymizer {
	
	private int k;
	private Dataset dataset;
	private ArrayList<String> generalizerId;
	
	public KAnonymizer(int k, 
					   ArrayList<ArrayList<String>> dataset,
					   ArrayList<ArrayList<Generalizer>> generalizer) {
		this.k = k;
		this.dataset = new Dataset(dataset, generalizer);
		
		// Sort the dataset to determine the equivalence classes with the most general specialization
		this.dataset.sort();
		
		// Stores generalizers id
		this.generalizerId = new ArrayList<>();
		for (int i = 0; i < generalizer.size(); i++) {
			for (int j = 0; j < generalizer.get(i).size(); j++) {
				generalizerId.add(generalizer.get(i).get(j).getId());
			}
		}
	}
	
	public int kOptimize(int k) {
		this.k = k;
		return kOptimize();
	}
	
	public int kOptimize() {
		// Builds head set. No attribute generalization at the beginning
		ArrayList<String> headSet = new ArrayList<>();
		
		// Builds tail set. All the attribute generalizations are contained there
		ArrayList<String> tailSet = new ArrayList<>();
	    for (String str : generalizerId) {
	    	tailSet.add(str);
	    }
		
		return kOptimizeRecursive(headSet, tailSet, Integer.MAX_VALUE);
	}
	
	private int kOptimizeRecursive(ArrayList<String> headSet, 
			ArrayList<String> tailSet, Integer bestCost) {
		
		return 0;
	}
	
	private int computeAnonymizationCost(ArrayList<String> headSet) {
		// Anonymize with the headSet
		// => use implementation for category 1 equivalence classes
		
		int cost = 0;
		
		if (headSet.size() == 0)
			return -1;
		
		String newGeneralizer = headSet.get(headSet.size() - 1);
		
		
		
		return cost;
	}
}
