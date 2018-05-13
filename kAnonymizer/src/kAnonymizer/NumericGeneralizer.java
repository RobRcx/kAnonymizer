package kAnonymizer;

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

}
