package kAnonymizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.function.Supplier;

/**
 * 
 * @author Roberto Ronco, Dario Capozzi
 * 
 * Dataset class has three members.
 * ArrayList<Tuple> data represents a database that needs to be anonymized.
 * ArrayList<LinkedHashMap<String, Generalizer>> contains a 
 * LinkedHashMap<String, Generalizer> for each attribute available in the 
 * target table structure. 
 */

public class Dataset {
	
	private ArrayList<Tuple> data;
	
	/*
	 * Both generalizer, activeGeneralizer are made with the following 
	 * structure: generalizer is the array of the arrays of all the 
	 * generalizer for each attribute. In particular, generalizer.get(i) 
	 * is the array of generalizers for the i-th attribute, and 
	 * generalizer.get(i).get(j) is the j-th generalizer for the i-th 
	 * attribute.
	 */
	
	private ArrayList<ArrayList<Generalizer>> generalizer, activeGeneralizer;
	private TupleComparator tupleComparator;
	
	private static final boolean debug = true;
	
	public Dataset(ArrayList<ArrayList<String>> data, 
			ArrayList<ArrayList<Generalizer>> generalizer) {
		
		/*
		 * Fills this.data with tuples provided in the first constructor's argument.
		 */
		
		this.data = new ArrayList<Tuple>();
		for (ArrayList<String> tuple : data) {
			this.data.add(new Tuple(tuple));
		}
		
		/*
		 * Assigns this.generalizer to the one provided in the second 
		 * constructor argument, then, for each generalizer element, i.e.
		 * for each attribute in a tuple, adds a LinkedHashMap to the 
		 * activeGeneralizer and puts into it the first generalization 
		 * available for that attribute from the set of all generalizations. 
		 */
		
		
		this.generalizer = new ArrayList<>(generalizer);
		
		activeGeneralizer = new ArrayList<>();
		
		for (int i = 0; i < generalizer.size(); i++) {
			activeGeneralizer.add(new ArrayList<>());
			assert(generalizer.get(i).size() != 0);
			activeGeneralizer.get(i).add(generalizer.get(i).get(0));
			for (int j = 1; j < generalizer.get(i).size(); j++) {
				this.activeGeneralizer.get(i).add(null);
			}
		}
		
		tupleComparator = new TupleComparator(generalizer, activeGeneralizer);
	}
	
	
	/*public void addActiveGeneralizers(ArrayList<LinkedHashMap<String, Generalizer>> newGeneralizer) {
		assert(activeGeneralizer.size() == newGeneralizer.size());
		for (int i = 0; i < activeGeneralizer.size(); i++) {
			Iterator<Entry<String, Generalizer>> it = 
					newGeneralizer.get(i).entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, Generalizer> e = it.next();
				activeGeneralizer.get(i).put(e.getKey(), e.getValue());
			}		
		}
	}*/
	
	public int getNumberOfGeneralizers() {
		int res = 0;
		
		for (int i = 0; i < generalizer.size(); i++) {
			for (int j = 0; j < generalizer.get(i).size(); j++) {
				res++;
			}
		}
		
		return res;
	}
	
	public void addActiveGeneralizer(ArrayList<ArrayList<Generalizer>> newGeneralizer) {
		activeGeneralizer = new ArrayList<ArrayList<Generalizer>>(newGeneralizer);
	}
	
	public void resetActiveGeneralizer(int attributeIndex, int generalizerIndex) {
		activeGeneralizer.get(attributeIndex).remove(generalizerIndex);
		activeGeneralizer.get(attributeIndex).add(generalizerIndex, null);
	}

	/**
	 * Provides a method to specify a new activeGeneralizer for the selected tuple attribute. 
	 * @param newGeneralizer
	 */
	public void addActiveGeneralizer(int attributeIndex, int generalizerIndex) {
		activeGeneralizer.get(attributeIndex).remove(generalizerIndex);
		activeGeneralizer.get(attributeIndex).add(generalizerIndex, 
				generalizer.get(attributeIndex).get(generalizerIndex));
	}
	
	/**
	 * Perform an efficient sort of the tuples, according to the activeGeneralizer specified for
	 * each attribute in the tuples structure
	 */
	public void sort() {
		//System.out.println("sort() : Active generalizers before sorting: " + activeGeneralizer);
		tupleComparator.setActiveGeneralizer(activeGeneralizer);
		Collections.sort(data, tupleComparator);
	}
	
	public void sort(int start, int end) {
		//System.out.println("Active generalizers before sorting: " + activeGeneralizer
		//		+ " from " + start + " to " + end);
		tupleComparator.setActiveGeneralizer(activeGeneralizer);
		Collections.sort(data.subList(start, end), tupleComparator);
	}
	
	public ArrayList<Tuple> getData() {
		return data;
	}
	
	public ArrayList<ArrayList<Generalizer>> getActiveGeneralizer() {
		return activeGeneralizer;
	}
	
	/**
	 * This method returns an array that contains the first index of
	 * each equivalence class of the dataset.
	 * @return
	 */
	public ArrayList<Integer> getEquivalenceClassesBoundaries() {
		//System.out.println("getEquivalenceClassesBoundaries() : Active generalizers " + activeGeneralizer);
		
		ArrayList<Integer> index = new ArrayList<Integer>(Arrays.asList(new Integer[] {0}));
		
		for (int i = 0; i < data.size() - 1; i++) {
			if (tupleComparator.compare(data.get(i), data.get(i + 1)) != 0) {
				// New equivalence class found => adds starting index to the 'index' array
				index.add(i + 1);
			}
		}
		
		/*
		 *  The following add is performed to take into account
		 *	the last equivalence class.
		 */
		
		index.add(data.size()); 
		
		//System.out.println("Computed indices : " + index);
		
		return index;
	}
	
	
	/**
	 * Comparer implements the Comparator interface in order to allow the 
	 * utilization of the Collections.sort() method.
	 * @author Roberto Ronco, Dario Capozzi
	 *
	 */
	protected class TupleComparator implements Comparator<Tuple> {
		
		private ArrayList<ArrayList<Generalizer>> generalizer, activeGeneralizer;
		
		public TupleComparator(ArrayList<ArrayList<Generalizer>> generalizer, 
				ArrayList<ArrayList<Generalizer>> activeGeneralizer) {
			this.generalizer = generalizer;
			this.activeGeneralizer = activeGeneralizer;
		}
		
		public void setActiveGeneralizer(ArrayList<ArrayList<Generalizer>> activeGeneralizer) {
			this.activeGeneralizer = activeGeneralizer;
		}
		
		/**
		 * Returns -1 if t0 > t1; 1 if t0 < t1; 0 if they are equals. 
		 */
		@Override
		public int compare(Tuple t0, Tuple t1) {
			assert(t0.data.size() == t1.data.size());
			/*
			 * Iterates over all the available attributes of t0 
			 */
			for (int i = 0; i < t0.data.size(); i++) {
				//System.out.println("Processing tuple " + i + "...");
				/*
				 * c0, c1 represents in what union of intervals contains 
				 * the i-th attribute of t0 and t1, respectively. 
				 */
				int c0, c1;
				c0 = c1 = -1;
				/*
				 * For the i-th attribute, iterates over the activeGeneralizer
				 * available for that attribute.
				 */
				for (int j = 0; j < activeGeneralizer.get(i).size(); j++) {
					//System.out.println("Processing generalizer " + j);
					
					c0 = c1 = -1;
					
					/*
					 * activeGeneralizerIt and generalizerIt are used in order 
					 * to iterate over all available activeGeneralizers and 
					 * generalizers, respectively. The ListIterator interface
					 * is used in order to have the possibility to iterate 
					 * forward and backward.
					 */
					
					ListIterator<Generalizer> activeGeneralizerIt = activeGeneralizer.get(i).listIterator();
					ListIterator<Generalizer> generalizerIt = generalizer.get(i).listIterator();
					
					/*
					 * count represents the actual union of intervals, from all
					 * the available intervals for the i-th attribute.
					 */
					
					int count = 0;
					
					/*
					 * while there is at least an activeGeneralizer available,
					 * gets the next activeGeneralizer, and checks for the 
					 * availability of the following one. If it is available,
					 * it is stored, and the activeGeneralizerIt is stepped 
					 * back.
					 */
					
					while (activeGeneralizerIt.hasNext()) {
						Generalizer entry = activeGeneralizerIt.next();
						Generalizer nextActiveEntry = null;
						while (activeGeneralizerIt.hasNext()) {
							nextActiveEntry = activeGeneralizerIt.next();
							if (nextActiveEntry != null) {
								activeGeneralizerIt.previous();
								break;
							}
						}
						
						/*
						 * Iterates until both tuples attributes match
						 * with a union of generalizer intervals, or no 
						 * generalizer is left.   
						 */
						
						while ((c0 == -1 || c1 == -1) && generalizerIt.hasNext()) {
							/*
							 * The next generalizer in the considered union of 
							 * intervals is stored.
							 * If it matches with the nextActiveEntry (i.e. it
							 * belongs to the next union of intervals), then 
							 * the generalizerIt is stepped back and the 
							 * iteration ends.
							 */
							entry = generalizerIt.next();
							if (nextActiveEntry != null && entry.getId().equals(nextActiveEntry.getId())) {
								generalizerIt.previous();
								break;
							}
							/*
							 * Checks if t0 and t1 i-th attribute value is 
							 * contained in the current generalizer (entry). 
							 * If it is true, it saves the current value of
							 * count to c0, c1, respectively.
							 */
							try {
								if (entry.contains(t0.data.get(i))) {
									c0 = count;
								}
								if (entry.contains(t1.data.get(i))) {
									c1 = count;
								}
							} catch (Exception ex) {
								System.out.println("Error comparing tuples.");
								return 1;
							}
							
						}
						count++;
						/*
						 * If c0 and c1 are not equal to -1, then the current 
						 * union of intervals is the one which the tuples 
						 * attributes belong to.
						 */
						if (c0 != -1 && c1 != -1)
							break;
						//System.out.println(c0 + " " + c1);
					}
				}
				assert(c0 != -1 && c1 != -1);
				/*
				 * If c0 < c1 then the i-th attribute of t0 is in a union of 
				 * intervals before than the one containing the i-th attribute
				 * of t1, according to the total order between intervals.
				 */
				if (c0 < c1) {
					//System.out.println("The tuples " + t0.toString() + " and " + t1.toString() + " are different");
					return 1;
				}
				else if (c0 > c1){
					//System.out.println("The tuples " + t0.toString() + " and " + t1.toString() + " are different");
					return -1;
				}
			}
			//System.out.println("The tuples " + t0.toString() + " and " + t1.toString() + " are equal");
			return 0;
		}
	}
}
