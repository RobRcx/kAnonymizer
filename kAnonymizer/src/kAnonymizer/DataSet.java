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

public class DataSet {
	public ArrayList<Tuple> data;
	public ArrayList<LinkedHashMap<String, Generalizer>> generalizer;
	public ArrayList<LinkedHashMap<String, Generalizer>> activeGeneralizer;

	public DataSet(ArrayList<ArrayList<String>> data,
			ArrayList<LinkedHashMap<String, Generalizer>> generalizer) {
		
		this.data = new ArrayList<Tuple>();
		for (ArrayList<String> tuple : data) {
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
	
	public interface ArrayListStringRef extends Supplier<ArrayList<ArrayList<String>>> {}
	public DataSet(ArrayListStringRef data, ArrayList<ArrayList<Generalizer>> generalizer) {
		
		this.data = new ArrayList<Tuple>();
		for (ArrayList<String> tuple : data.get()) {
			this.data.add(new Tuple(tuple));
		}
		
		this.generalizer = new ArrayList<>();
		for (int i = 0; i < generalizer.size(); i++) {
			this.generalizer.add(new LinkedHashMap<String, Generalizer>());
			for (Generalizer g : generalizer.get(i)) {
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
		activeGeneralizer = generalizer;
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
				boolean f0, f1;
				f0 = f1 = false;
				int c0, c1;
				c0 = c1 = 0;
				for (int j = 0; j < activeGeneralizer.get(i).size(); j++) {
					//System.out.println("Processing generalizer " + j);
					
					Iterator<Entry<String, Generalizer>> activeGeneralizerIt = 
							activeGeneralizer.get(i).entrySet().iterator();
					
					ArrayList<Entry<String, Generalizer>> generalizerEntry = new ArrayList<>();
					Iterator<Entry<String, Generalizer>> it = generalizer.get(i).entrySet().iterator();
					while (it.hasNext()) {
						Entry<String, Generalizer> entry = it.next();
						generalizerEntry.add(entry);
					}
					ListIterator<Entry<String, Generalizer>> generalizerIt = generalizerEntry.listIterator();
					
					int count = 0;
					while (activeGeneralizerIt.hasNext()) {
						Entry<String, Generalizer> entry = activeGeneralizerIt.next();
						Entry<String, Generalizer> nextActiveEntry = null;
						if (activeGeneralizerIt.hasNext())
							nextActiveEntry = activeGeneralizerIt.next();
						
						//System.out.println("Active generalizer : " + entry.getKey());
						//if (nextActiveEntry != null)
						//	System.out.println("Next active generalizer : " + nextActiveEntry.getKey());
						
						while (!f0 && !f1 && generalizerIt.hasNext()) {
							entry = generalizerIt.next();
							if (nextActiveEntry != null && !entry.getKey().equals(nextActiveEntry.getKey())) {
								generalizerIt.previous();
								break;
							}
							
							try {
								if (!f0) {
									f0 = entry.getValue().isWithin(t0.data.get(i));
									if (f0)
										c0 = count;
								}
								if (!f1) {
									f1 = entry.getValue().isWithin(t1.data.get(i));
									if (f1)
										c1 = count;
								}
							} catch (Exception ex) {
								System.out.println("Error comparing tuples.");
								return 1;
							}
								
						}
						count++;
						if (f0 && f1)
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
	
	}
}
