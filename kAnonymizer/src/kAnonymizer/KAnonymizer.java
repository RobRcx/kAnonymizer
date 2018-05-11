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
	private ArrayList<Tuple> dataset;
	// Maps each generalizer to the corresponding attribute id
	private HashMap<String, Integer> generalizerAttributeMap; 
	
	
	public KAnonymizer(int k, 
					   ArrayList<ArrayList<String>> dataset,
					   ArrayList<Type> attributeType,
					   ArrayList<ArrayList<String>> generalizer) {
		this.k = k;
		
		// Builds data structure for dataset.
		for (ArrayList<String> tuple : dataset) {
			this.dataset.add(new Tuple(tuple, attributeType));
		}
		
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
		// TODO Anonymize with the headSet
		
		// Sort the dataset to determine the equivalence classes
		Collections.sort(dataset);
		
		int cost, inducedEquivalenceClassSize;
		
		cost = 0;
		inducedEquivalenceClassSize = 1;
		for (int i = 0; i < dataset.size() - 1; i++) {
			// if consequent elements in the dataset are equal then the current
			// equivalence class size must be incremented by one 
			if (dataset.get(i).compareTo(dataset.get(i + 1)) == 0)
				inducedEquivalenceClassSize += 1; 
			else {
				inducedEquivalenceClassSize = 1;
				// Add the cost related to the equivalence class basing
				// on the discernibility metric (cfr. Bayardo05)
				if (inducedEquivalenceClassSize < k) {
					// The induced equivalence classes of size less than k
					// must be penalized as much as the entired dataset
					// dimension.
					cost += inducedEquivalenceClassSize * dataset.size();
					// TODO: remove suppressed tuples from the dataset?
				}
				else {
					cost += inducedEquivalenceClassSize * inducedEquivalenceClassSize;
				}
			}
		}
		
		return cost;
	}
	
	/*
	private ArrayList<Integer> pruneUselessValues(ArrayList<Integer> headSet,
			ArrayList<Integer> tailSet) {
		return null;
	}*/
}
