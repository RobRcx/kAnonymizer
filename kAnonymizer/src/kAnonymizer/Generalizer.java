package kAnonymizer;

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
