package kAnonymizer;

/**
 * 
 * @author Roberto Ronco, Dario Capozzi
 * 
 * Generalizer is an abstract class that is used to check if a value is 
 * contained or not in a specified interval. A concrete class has to be 
 * specified for each tuple attribute.
 *
 */
public abstract class Generalizer {
	protected String id;
	
	public Generalizer(String id) {
		this.id = id;
	}
	public String getId() {
		return id;
	}
	public abstract boolean contains(Object o) throws Exception;
}
