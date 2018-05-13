package kAnonymizer;

/**
 * 
 * @author Roberto Ronco, Dario Capozzi
 * 
 * Concrete class that contains a particular String value.
 *
 */
public class CategoricGeneralizer extends Generalizer{
	
	public String value;
	
	public CategoricGeneralizer(String id, String value) {
		super(id);
		this.value = value;
	}
	
	
	@Override
	public boolean contains(Object o) throws Exception {
		return o.toString().equals(this.value);
	}

}
