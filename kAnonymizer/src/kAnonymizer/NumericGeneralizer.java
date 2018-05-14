package kAnonymizer;

/**
 * 
 * @author Roberto Ronco, Dario Capozzi
 * 
 * Concrete class that contains lower and upper bound of an interval
 * in order to check if a number matches that interval.
 *
 */
public class NumericGeneralizer extends Generalizer {
	public int lb, ub;
	
	public NumericGeneralizer(String id, int lb, int ub) {
		super(id);
		this.lb = lb;
		this.ub = ub;
	}

	@Override
	public boolean contains(Object o) throws Exception {
		Float f = Float.parseFloat(o.toString());
		return lb <= f && f <= ub;
	}
	
	@Override
	public String toString() {
		return id + ", (" + lb + ", " + ub + ")";
	}

}
