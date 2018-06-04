package kAnonymizer;

public class AnonymizationResult {
	public Integer k, equivalenceClasses;
	public Double prunedNodes;
	public Double totalNodes;
	public Long cost, executionTime;

	public AnonymizationResult(Integer k) {
		this.k = k;
		prunedNodes = 0d;
	}
	
	public AnonymizationResult(Integer k, Integer equivalenceClasses, 
			Double prunedNodes, Double totalNodes, Long cost, Long executionTime) {
		this.k = k;
		this.equivalenceClasses = equivalenceClasses;
		this.prunedNodes = prunedNodes;
		this.totalNodes = totalNodes;
		this.cost = cost;
		this.executionTime = executionTime;
	}
	
	public String prettyPrintableString() {
		return "k = " +  k 
				+ "\ncost : " + cost 
				+ "\nexecutionTime : " + (executionTime / 1000d) + "sec."
				+ "\nequivalence classes : " + equivalenceClasses 
				+ "\npruned nodes : " + Math.ceil(prunedNodes) + " over " + Math.ceil(totalNodes);
	}

	@Override
	public String toString() {
		return k + ";" + cost + ";" + (executionTime / 1000d) + ";" 
				+ equivalenceClasses + ";" + prunedNodes;
	}
}
