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
	private DataSet dataset;
	// Maps each generalizer to the corresponding attribute id
	private HashMap<String, Integer> generalizerAttributeMap; 
	
	
	public KAnonymizer(int k, 
					   ArrayList<ArrayList<String>> dataset,
					   ArrayList<ArrayList<String>> generalizer) {
		this.k = k;
		this.dataset = new Dataset(dataset, generalizer);
		
		// Sort the dataset to determine the equivalence classes
		Collections.sort(this.dataset);
		
		
		// Builds generalizer -> attribute map.
		// It is needed in order to manage head and tail set of generalizers,
		// and map them immediately to the corresponding attributes.
		this.generalizerAttributeMap = new HashMap<String, Integer>();
		for (int i = 0; i < generalizer.size(); i++) {
			for (String g : generalizer.get(i)) {
				this.generalizerAttributeMap.put(g, i);
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
		Iterator<Entry<String, Integer>> it = generalizerAttributeMap.entrySet().iterator();
	    while (it.hasNext()) {
	        Entry<String, Integer> pair = it.next();
	        tailSet.add(pair.getKey());
	        // System.out.println(pair.getKey() + " = " + pair.getValue());
	        // it.remove(); // avoids a ConcurrentModificationException
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
