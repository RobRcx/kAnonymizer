package kAnonymizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class KAnonymizer {
	
	private int k;
	private Dataset dataset;
	private ArrayList<AttributeGeneralizerIndicesInfo> generalizerIndices;
	
	public KAnonymizer(int k, 
					   ArrayList<ArrayList<String>> dataset,
					   ArrayList<ArrayList<Generalizer>> generalizer) {
		this.k = k;
		this.dataset = new Dataset(dataset, generalizer);
		
		// Sort the dataset to determine the equivalence classes with the most general specialization
		// this.dataset.sort();
		
		// Stores generalizers id
		this.generalizerIndices = new ArrayList<>();
		for (int i = 0; i < generalizer.size(); i++) {
			for (int j = 1; j < generalizer.get(i).size(); j++) {
				generalizerIndices.add(new AttributeGeneralizerIndicesInfo(generalizer.get(i).get(j).getId(), i, j));
			}
		}
	}
	
	public Long kOptimize(int k) {
		this.k = k;
		return kOptimize();
	}
	
	public Long kOptimize() {
		// Builds head set. No attribute generalization at the beginning
		ArrayList<AttributeGeneralizerIndicesInfo> headSet = new ArrayList<>();
		
		// Builds tail set. All the attribute generalizations are contained there
		ArrayList<AttributeGeneralizerIndicesInfo> tailSet = new ArrayList<>();
	    for (AttributeGeneralizerIndicesInfo p : generalizerIndices) {
	    	tailSet.add(p);
	    }
		
	    //System.out.println("Starting recursion...\n");
	    
		return kOptimizeRecursive(headSet, tailSet, Long.MAX_VALUE);
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
	private static int c = 0;
	private Long kOptimizeRecursive(ArrayList<AttributeGeneralizerIndicesInfo> headSet, ArrayList<AttributeGeneralizerIndicesInfo> tailSet, Long bestCost) {
		c++;
		System.out.println("Node " + c);
		
		System.out.println("HeadSet : " + headSet + "  TailSet : " + tailSet);
		
		pruneUselessValues(headSet, tailSet);
		
		Long nodeAnonymizationCost = computeCost(headSet);
		
		//if (nodeAnonymizationCost < bestCost)
		//	System.out.println("New best cost! " + nodeAnonymizationCost + " < " + bestCost);
		
		bestCost = nodeAnonymizationCost < bestCost ? nodeAnonymizationCost : bestCost;
		
		System.out.println("Current best cost : " + bestCost);
		
		tailSet = prune(headSet, tailSet, bestCost);
		
		/*
		 * Check difference between tailSet and new tailSet for debugging purposes
		 */
		
		if (tailSet == null)
			return bestCost;
		
		tailSet = reorderTail(headSet, tailSet);
		
		Iterator<AttributeGeneralizerIndicesInfo> iterator = tailSet.iterator();
		while (!tailSet.isEmpty()) {
			AttributeGeneralizerIndicesInfo p = iterator.next();
			ArrayList<AttributeGeneralizerIndicesInfo> newHeadSet = new ArrayList<AttributeGeneralizerIndicesInfo>(headSet); newHeadSet.add(p);
			iterator.remove();
			ArrayList<AttributeGeneralizerIndicesInfo> newTailSet = new ArrayList<AttributeGeneralizerIndicesInfo>(tailSet);
			bestCost = kOptimizeRecursive(newHeadSet, newTailSet, bestCost);
		}
		return bestCost;
	}
	
	
	private void pruneUselessValues(ArrayList<AttributeGeneralizerIndicesInfo> headSet, ArrayList<AttributeGeneralizerIndicesInfo> tailSet) {
		ArrayList<Tuple> sortedData = sortDataset(headSet);
		ArrayList<Integer> classesIndex = dataset.getEquivalenceClassesBoundaries();
		
		// TO CHECK!
		//
		// classesIndex.add(sortedData.size() - 1);
		// 
		
		for (int t = 0; t < tailSet.size(); t++) {
			headSet.add(tailSet.get(t));
			
			//ArrayList<Integer> newClassesIndex = getEquivalenceClassesStartingIndex(sortDataset(headSet));
			int i;
			for (i = 0; i < classesIndex.size() - 1; i++) {
				// Sorts the i-th equivalence class basing on (headSet united with p)
				ArrayList<Tuple> newSortedData = sortDataset(headSet, classesIndex.get(i), classesIndex.get(i + 1));
				// Gets the new starting indices of equivalence classes within the considered equivalence class
				ArrayList<Integer> newClassesIndex = dataset.getEquivalenceClassesBoundaries();
				// If the added generalizer (p) splits the equivalence class
				if (newClassesIndex.size() > classesIndex.size()) {
					int j;
					for (j = 0; j < newClassesIndex.size(); j++)
						if (newClassesIndex.get(j + 1) - newClassesIndex.get(j) - 1 >= k)
							break;
					if (j < newClassesIndex.size())
						break;
				}
			}
			
			
			/*
			 * If the condition j < newClassesIndex.size() is never satisfied,
			 * then the generalizer tailset.get(t).getGeneralizerIndex of the
			 * attribute tailSet.get(t).getAttributeIndex splits every 
			 * equivalence class of sortedData in equivalence classes sized
			 * less than k.
			 * Therefore, the t-th generalizer of tailSet can be removed.
			 */
			if (i == classesIndex.size()) {
				tailSet.remove(t);
				t--;
			}
			
			headSet.remove(headSet.size() - 1);
		}
	}
	
	/**
	 * This function creates and returns a new tail by removing values from tailSet
	 * that cannot lead to anonymizations with cost lower than bestCost
	 * @param headSet
	 * @param tailSet
	 * @param bestCost
	 * @return
	 */
	private ArrayList<AttributeGeneralizerIndicesInfo> prune(ArrayList<AttributeGeneralizerIndicesInfo> headSet, ArrayList<AttributeGeneralizerIndicesInfo> tailSet, Long bestCost) {
		ArrayList<AttributeGeneralizerIndicesInfo> allSet = new ArrayList<>(headSet);
		// ArrayList<Pair> tailSetBackup = new ArrayList(tailSet);
		
		// allSet.removeAll(tailSet);
		allSet.addAll(tailSet);
		// Collections.sort(allSet); // really necessary?
		
		System.out.println("prune() with headSet = " + headSet + ", allSet " + allSet);
		
		Long lowerBound = computeLowerBound(headSet, allSet);
		if (lowerBound >= bestCost) {
			System.out.println("Pruning with\nheadSet " + headSet + "\ntailSet " + tailSet + "\nallSet" + allSet
					+ "\nLower bound " + lowerBound + "\nBest cost " + bestCost);
			return null;
		}
		
		ArrayList<AttributeGeneralizerIndicesInfo> newHeadSet = new ArrayList<>(headSet);
		ArrayList<AttributeGeneralizerIndicesInfo> newTailSet = new ArrayList<>(tailSet);
		
		for (int i = 0; i < tailSet.size(); i++) {
			newHeadSet.add(tailSet.get(i));
			//Collections.sort(newHeadSet); // really necessary?
			
			AttributeGeneralizerIndicesInfo pairBackup = newTailSet.get(i);
			newTailSet.remove(i);
			if (prune(new ArrayList<>(newHeadSet), new ArrayList<>(newTailSet), bestCost) != null) {
				newTailSet.add(i, pairBackup);
			}
			
			newHeadSet.remove(newHeadSet.size() - 1);
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
	
	private ArrayList<Tuple> sortDataset(ArrayList<AttributeGeneralizerIndicesInfo> generalizer) {
		// Removes all generalizers
		for (AttributeGeneralizerIndicesInfo p : generalizerIndices)
			dataset.resetActiveGeneralizer(p.getAttributeIndex(), p.getGeneralizerIndex());
		
		// Sets generalizers in headSet as active generalizers
		if (generalizer != null)
			for (AttributeGeneralizerIndicesInfo p : generalizer)
				dataset.addActiveGeneralizer(p.getAttributeIndex(), p.getGeneralizerIndex());
		
		dataset.sort();
		return dataset.getData();
	}
	
	private ArrayList<Tuple> sortDataset(ArrayList<AttributeGeneralizerIndicesInfo> generalizer, int start, int end) {
		// Removes all generalizers
		for (AttributeGeneralizerIndicesInfo p : generalizerIndices)
			dataset.resetActiveGeneralizer(p.getAttributeIndex(), p.getGeneralizerIndex());
		
		// Sets generalizers in headSet as active generalizers
		if (generalizer != null)
			for (AttributeGeneralizerIndicesInfo p : generalizer)
				dataset.addActiveGeneralizer(p.getAttributeIndex(), p.getGeneralizerIndex());
		
		dataset.sort(start, end);
		return dataset.getData();
	}
	
	private Long computeLowerBound(ArrayList<AttributeGeneralizerIndicesInfo> headSet, ArrayList<AttributeGeneralizerIndicesInfo> allSet) {
		// System.out.println("computeLowerBound()...");
		ArrayList<Tuple> headSetSortedData = sortDataset(headSet);
		ArrayList<Integer> headSetClassesIndex = dataset.getEquivalenceClassesBoundaries();
		
		ArrayList<Tuple> allSetSortedData = sortDataset(allSet);
		ArrayList<Integer> allSetClassesIndex = dataset.getEquivalenceClassesBoundaries();
		
		/*
		 * Implements the lower bound equation in Bayardo05, page 6
		 */
		
		Long lowerBound = 0l; // Init cost to 0
		
		HashMap<Tuple, Boolean> map = new HashMap<>();
		
		for (int i = 0; i < headSetClassesIndex.size() - 1; i++) {
			int diff = headSetClassesIndex.get(i + 1) - headSetClassesIndex.get(i);
			boolean val = false;
			if (diff < k) { 
				lowerBound += diff * headSetSortedData.size();
				val = true;
			}
			for (int j = headSetClassesIndex.get(i); j < headSetClassesIndex.get(i + 1); j++)
				map.put(headSetSortedData.get(j), val);
		}
		
		for (int i = 0; i < allSetClassesIndex.size() - 1; i++) {
			int max = Math.max(allSetClassesIndex.get(i + 1) - allSetClassesIndex.get(i), k);
			
			for (int j = allSetClassesIndex.get(i); j < allSetClassesIndex.get(i + 1); j++) {
				if (!map.get(allSetSortedData.get(j))) {
					lowerBound += max;
				}
			}
		}
		
		
		
		return lowerBound;
	}
	
	private ArrayList<AttributeGeneralizerIndicesInfo> reorderTail(ArrayList<AttributeGeneralizerIndicesInfo> headSet, ArrayList<AttributeGeneralizerIndicesInfo> tailSet) {
		System.out.print("reorderTail() : ");
		
		ArrayList<AttributeGeneralizerIndicesInfo> newTailSet = new ArrayList<>();
		
		ArrayList<Pair> equivalenceClassesArray = new ArrayList<>();
		
		for (int i = 0; i < tailSet.size(); i++) {
			headSet.add(tailSet.get(i));
			
			ArrayList<Tuple> sortedData = sortDataset(headSet);
			equivalenceClassesArray.add(new Pair(i, dataset.getEquivalenceClassesBoundaries().size()));
			
			headSet.remove(headSet.size() - 1);
		}
		
		Collections.sort(equivalenceClassesArray);
		
		for (int i = 0; i < equivalenceClassesArray.size(); i++) {
			int key = equivalenceClassesArray.get(i).getKey();
			int value = equivalenceClassesArray.get(i).getValue();
		
			newTailSet.add(tailSet.get(key));
			System.out.print("(" + key + ", " + value + ")  ");
		}
	    
	    System.out.println("");
		
		return newTailSet;
	}
	
	private Long computeCost(ArrayList<AttributeGeneralizerIndicesInfo> headSet) {
		//assert(headSet.size() != 0);
		System.out.println("computeCost() : ");
		
		ArrayList<Tuple> sortedData = sortDataset(headSet); 
		ArrayList<Integer> classesIndex = dataset.getEquivalenceClassesBoundaries();
		
		Long cost = 0l; // Init cost to 0
		
		System.out.print(" Equivalence classes sizes : ");
		for (int i = 0; i < classesIndex.size() - 1; i++) {
			int diff = classesIndex.get(i + 1) - classesIndex.get(i);
			System.out.print(diff + "  ");
			// If the equivalence class size is greater than k, then:
			if (diff >= k) {
				//System.out.println(" =>(1)  " + Math.multiplyExact((long) diff, (long) diff) + "  ");
				cost += Math.multiplyExact((long) diff, (long) diff);
			}
			else { // otherwise it is a suppressed group of tuples, hence the cost addition:
				//System.out.println(" =>(2)  " + Math.multiplyExact((long) diff, (long) sortedData.size()) + "  ");
				cost += Math.multiplyExact((long) diff, (long) sortedData.size());
			}
		}
		System.out.println("\nEquivalence classes dim : " + (classesIndex.size() - 1));
		System.out.println("cost = " + cost);
		return cost;
	}
	
	protected class AttributeGeneralizerIndicesInfo implements Comparable<AttributeGeneralizerIndicesInfo> {
		private String id;
		private int attributeIndex, generalizerIndex;

		public int getAttributeIndex() {
			return attributeIndex;
		}

		public int getGeneralizerIndex() {
			return generalizerIndex;
		}

		public AttributeGeneralizerIndicesInfo(String id, int attributeIndex, int generalizerIndex) {
			this.id = id;
			this.attributeIndex = attributeIndex;
			this.generalizerIndex = generalizerIndex;
		}

		@Override
		public int compareTo(AttributeGeneralizerIndicesInfo pair) {
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
		
		@Override
		public String toString() {
			return id;
		}
	}
	
	protected class Pair implements Comparable<Pair> {
		private int key, value;
		
		public Pair(int key, int value) {
			this.key = key;
			this.value = value;
		}

		public int getKey() {
			return key;
		}

		public int getValue() {
			return value;
		}

		@Override
		public int compareTo(Pair pair) {
			if (value < pair.value)
				return -1;
			if (value > pair.value)
				return 1;
			return 0;
		}
		
	}
}
