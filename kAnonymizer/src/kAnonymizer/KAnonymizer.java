package kAnonymizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
	
	/**
	 * This function returns the lowest cost of any anonymization within the
	 * sub-tree rooted at headSet that has a cost less than bestCost (if one
	 * exists). Otherwise, it returns bestCost. 
	 * @param headSet
	 * @param tailSet
	 * @param bestCost
	 * @return
	 */
	private int kOptimizeRecursive(ArrayList<Pair> headSet, ArrayList<Pair> tailSet, Integer bestCost) {
		tailSet = new ArrayList<Pair>(tailSet);
		tailSet = pruneUselessValues(headSet, tailSet);
		int nodeAnonymizationCost = computeCost(headSet);
		bestCost = nodeAnonymizationCost < bestCost ? nodeAnonymizationCost : bestCost;
		tailSet = prune(headSet, tailSet, bestCost);
		tailSet = reorderTail(headSet, tailSet);
		Iterator<Pair> iterator = tailSet.iterator();
		while (!tailSet.isEmpty()) {
			Pair p = iterator.next();
			ArrayList<Pair> newHeadSet = new ArrayList<Pair>(headSet); newHeadSet.add(p);
			iterator.remove();
			bestCost = kOptimizeRecursive(newHeadSet, tailSet, bestCost);
		}
		return 0;
	}
	
	
	private ArrayList<Pair> pruneUselessValues(ArrayList<Pair> headSet, ArrayList<Pair> tailSet) {
		// TODO
		return tailSet;
	}
	
	/**
	 * This function creates and returns a new tail by removing values from T
	 * that cannot lead to anonymizations with cost lower than bestCost
	 * @param headSet
	 * @param tailSet
	 * @param bestCost
	 * @return
	 */
	private ArrayList<Pair> prune(ArrayList<Pair> headSet, ArrayList<Pair> tailSet, Integer bestCost) {
		ArrayList<Pair> allSet = new ArrayList<>(headSet);
		// ArrayList<Pair> tailSetBackup = new ArrayList(tailSet);
		
		// allSet.removeAll(tailSet);
		allSet.addAll(tailSet);
		Collections.sort(allSet); // really necessary?
		
		if (computeLowerBound(headSet, allSet) >= bestCost)
			return null;
		
		ArrayList<Pair> newTailSet = new ArrayList<>(tailSet);
		
		for (int i = 0; i < newTailSet.size(); i++) {
			ArrayList<Pair> newHeadSet = new ArrayList<>(headSet);
			newHeadSet.add(newTailSet.get(i));
			Pair pairBackup = newTailSet.get(i);
			newTailSet.remove(i);
			if (prune(newHeadSet, newTailSet, bestCost) == null) {
				i--;
			}
			else {
				newTailSet.add(i, pairBackup);
			}
		}
		
		if (tailSet.size() == newTailSet.size()) {
			for (int i = 0; i < tailSet.size(); i++) {
				if (tailSet.get(i).compareTo(newTailSet.get(i)) != 0)
					return prune(headSet, newTailSet, bestCost);
			}
			return newTailSet;
		}
		else return prune(headSet, newTailSet, bestCost);
		
	}
	
	private int computeLowerBound(ArrayList<Pair> headSet, ArrayList<Pair> allSet) {
		// TODO
		return Integer.MAX_VALUE;
	}
	
	private ArrayList<Pair> reorderTail(ArrayList<Pair> headSet, ArrayList<Pair> tailSet) {
		// TODO
		return tailSet;
	}
	
	// Anonymize with the headSet
	private int computeCost(ArrayList<Pair> headSet) {
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
	
	public class Pair implements Comparable<Pair> {
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

		@Override
		public int compareTo(Pair pair) {
			if (attributeIndex < pair.attributeIndex)
				return -1;
			else if (attributeIndex > pair.attributeIndex)
				return 1;
			else {
				if (generalizerIndex < pair.generalizerIndex)
					return -1;
				else if (generalizerIndex > pair.generalizerIndex)
					return 1;
			}
			return 0;
		}
		
	}
}
