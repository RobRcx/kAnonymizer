package test;

public class ComputeNodes {

	public static void main(String[] args) {
		int n, res;
		
		res = 1; // first node
		n = 7;
		
		for (int i = 0; i < n; i++) { // i : recursion level
			for (int j = 0; j <= i; j++) { // 
				for (int k = j; k >= 0; k--)
					res += n - k;
			}
		}
	}
}
