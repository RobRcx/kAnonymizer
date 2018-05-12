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

public class Dataset {
	public ArrayList<Tuple> data;
	public ArrayList<LinkedHashMap<String, Generalizer>> generalizer;
	public ArrayList<LinkedHashMap<String, Generalizer>> activeGeneralizer;

	public interface ArrayListStringRef extends Supplier<ArrayList<ArrayList<String>>> {}
	public Dataset(ArrayListStringRef data, ArrayList<LinkedHashMap<String, Generalizer>> generalizer) {
		
		this.data = new ArrayList<Tuple>();
		for (ArrayList<String> tuple : data.get()) {
			this.data.add(new Tuple(tuple));
		}
		
		this.generalizer = generalizer;
		this.activeGeneralizer = new ArrayList<LinkedHashMap<String, Generalizer>>();
		
		for (int i = 0; i < generalizer.size(); i++) {
			this.activeGeneralizer.add(new LinkedHashMap<String, Generalizer>());
			assert(this.generalizer.get(i).size() != 0);
			Entry<String, Generalizer> firstGeneralizationForCurrentAttribute =
					this.generalizer.get(i).entrySet().iterator().next();
			this.activeGeneralizer.get(i)
				.put(firstGeneralizationForCurrentAttribute.getKey(),
						firstGeneralizationForCurrentAttribute.getValue());
		}
	}
	
	
	public Dataset(ArrayList<ArrayList<String>> data, 
			ArrayList<ArrayList<Generalizer>> generalizer) {
		
		this.data = new ArrayList<Tuple>();
		for (ArrayList<String> tuple : data) {
			this.data.add(new Tuple(tuple));
		}
		
		this.generalizer = new ArrayList<>();
		for (int i = 0; i < generalizer.size(); i++) {
			this.generalizer.add(new LinkedHashMap<String, Generalizer>());
			for (Generalizer g : generalizer.get(i)) {
				assert(!this.generalizer.get(i).containsKey(g.id));
				this.generalizer.get(i).put(g.id, g);
			}
		}
		
		this.activeGeneralizer = new ArrayList<LinkedHashMap<String, Generalizer>>();
		
		for (int i = 0; i < generalizer.size(); i++) {
			this.activeGeneralizer.add(new LinkedHashMap<String, Generalizer>());
			assert(this.generalizer.get(i).size() != 0);
			Entry<String, Generalizer> firstGeneralizationForCurrentAttribute =
					this.generalizer.get(i).entrySet().iterator().next();
			this.activeGeneralizer.get(i)
				.put(firstGeneralizationForCurrentAttribute.getKey(),
						firstGeneralizationForCurrentAttribute.getValue());
		}
	}
	
	public void addActiveGeneralizers(ArrayList<LinkedHashMap<String, Generalizer>> 
		newGeneralizer) {
		assert(activeGeneralizer.size() == newGeneralizer.size());
		for (int i = 0; i < activeGeneralizer.size(); i++) {
			Iterator<Entry<String, Generalizer>> it = 
					newGeneralizer.get(i).entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, Generalizer> e = it.next();
				activeGeneralizer.get(i).put(e.getKey(), e.getValue());
			}		
		}
	}

	public void sort() {
		//activeGeneralizer = (ArrayList<LinkedHashMap<String, Generalizer>>) generalizer.clone();
		System.out.println("Generalizers : " + generalizer);
		System.out.println("Active generalizers : " + activeGeneralizer);
		Collections.sort(data, new Comparer(generalizer, activeGeneralizer));
	}
	
	public class Comparer implements Comparator<Tuple> {
		
		private ArrayList<LinkedHashMap<String, Generalizer>> generalizer,
													activeGeneralizer;
		
		public Comparer(ArrayList<LinkedHashMap<String, Generalizer>> generalizer,
				ArrayList<LinkedHashMap<String, Generalizer>> activeGeneralizer) {
			this.generalizer = generalizer;
			this.activeGeneralizer = activeGeneralizer;
		}
		
		@Override
		public int compare(Tuple t0, Tuple t1) {
			for (int i = 0; i < t0.data.size(); i++) {
				//System.out.println("Processing tuple " + i + "...");
				int c0, c1;
				c0 = c1 = -1;
				for (int j = 0; j < activeGeneralizer.get(i).size(); j++) {
					//System.out.println("Processing generalizer " + j);
					
					c0 = c1 = -1;
					
					ListIterator<Entry<String, Generalizer>> activeGeneralizerIt = 
							this.getListIteratorFrom(activeGeneralizer.get(i).entrySet().iterator());
					
					ListIterator<Entry<String, Generalizer>> generalizerIt = 
							this.getListIteratorFrom(generalizer.get(i).entrySet().iterator());
					
					int count = 0;
					
					while (activeGeneralizerIt.hasNext()) {
						Entry<String, Generalizer> entry = activeGeneralizerIt.next();
						Entry<String, Generalizer> nextActiveEntry = null;
						if (activeGeneralizerIt.hasNext()) {
							nextActiveEntry = activeGeneralizerIt.next();
							activeGeneralizerIt.previous();
						}
						
						//System.out.println("Active generalizer : " + entry.getKey());
						//if (nextActiveEntry != null)
						//	System.out.println("Next active generalizer : " + nextActiveEntry.getKey());
						
						while ((c0 == -1 || c1 == -1) && generalizerIt.hasNext()) {
							entry = generalizerIt.next();
							if (nextActiveEntry != null && !entry.getKey().equals(nextActiveEntry.getKey())) {
								generalizerIt.previous();
								break;
							}
							
							try {
								if (entry.getValue().contains(t0.data.get(i))) {
									c0 = count;
								}
								if (entry.getValue().contains(t1.data.get(i))) {
									c1 = count;
								}
							} catch (Exception ex) {
								System.out.println("Error comparing tuples.");
								return 1;
							}
							count++;	
						}
						
						if (c0 != -1 && c1 != -1)
							break;
					}
				}
				if (c0 < c1) {
					System.out.println("The tuples " + t0.toString() + " and " + t1.toString() + " are different");
					return 1;
				}
					
				else if (c0 > c1){
					System.out.println("The tuples " + t0.toString() + " and " + t1.toString() + " are different");
					return -1;
				}
			}
			System.out.println("The tuples " + t0.toString() + " and " + t1.toString() + " are equal");
			return 0;
		}
		
		private <T, V>ListIterator<Entry<T, V>> getListIteratorFrom(Iterator<Entry<T, V>> it) {
			ArrayList<Entry<T, V>> array = new ArrayList<>();
			while (it.hasNext()) {
				Entry<T, V> entry = it.next();
				array.add(entry);
			}
			return array.listIterator();
		}
	}
}
