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
	private ArrayList<Pair> generalizerIndices;
	
	public KAnonymizer(int k, 
					   ArrayList<ArrayList<String>> dataset,
					   ArrayList<ArrayList<Generalizer>> generalizer) {
		this.k = k;
		this.dataset = new Dataset(dataset, generalizer);
		
		// Sort the dataset to determine the equivalence classes with the most general specialization
		this.dataset.sort();
		
		// Stores generalizers id
		this.generalizerIndices = new ArrayList<>();
		for (int i = 0; i < generalizer.size(); i++) {
			for (int j = 0; j < generalizer.get(i).size(); j++) {
				generalizerIndices.add(new Pair(i, j));
			}
		}
	}
	
	public int kOptimize(int k) {
		this.k = k;
		return kOptimize();
	}
	
	public int kOptimize() {
		// Builds head set. No attribute generalization at the beginning
		ArrayList<Pair> headSet = new ArrayList<>();
		
		// Builds tail set. All the attribute generalizations are contained there
		ArrayList<Pair> tailSet = new ArrayList<>();
	    for (Pair p : generalizerIndices) {
	    	tailSet.add(p);
	    }
		
		return kOptimizeRecursive(headSet, tailSet, Integer.MAX_VALUE);
	}
	
	private int kOptimizeRecursive(ArrayList<Pair> headSet, ArrayList<Pair> tailSet, Integer bestCost) {
		
		return 0;
	}
	
	// Anonymize with the headSet
	private int computeAnonymizationCost(ArrayList<Pair> headSet) {
		assert(headSet.size() != 0);

		// Remove all generalizers
		for (Pair p : generalizerIndices) {
			dataset.resetActiveGeneralizer(p.getAttributeIndex(), p.getGeneralizerIndex());
		}
		
		// Set generalizers in headSet as active generalizers
		for (Pair p : headSet) {
			dataset.addActiveGeneralizer(p.getAttributeIndex(), p.getGeneralizerIndex());
		}
		
		dataset.sort();
		ArrayList<Tuple> sortedData = dataset.getData();
		
		int cost = 0; // Init cost to 0
		int count = 1; // Init count of tuples in equivalence class to 0
		for (int i = 0; i < sortedData.size(); i++) {
			if (sortedData.get(i).compareTo(sortedData.get(i + 1)) == 0) {
				count++;
			}
			else {
				// new equivalence class found
				if (count >= k) {
					cost += count * count;
				}
				else
					cost += count * sortedData.size();
				
				// reset the count
				count = 1;
			}
		}
		
		return cost;
	}
	
	public class Pair {
		private int attributeIndex, generalizerIndex;

		public int getAttributeIndex() {
			return attributeIndex;
		}

		public int getGeneralizerIndex() {
			return generalizerIndex;
		}

		public Pair(int attributeIndex, int generalizerIndex) {
			super();
			this.attributeIndex = attributeIndex;
			this.generalizerIndex = generalizerIndex;
		}
		
	}
}
