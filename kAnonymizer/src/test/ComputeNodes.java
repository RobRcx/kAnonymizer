package test;

import static org.junit.jupiter.api.Assumptions.assumingThat;

import java.util.ArrayList;

public class ComputeNodes {

	public static void main(String[] args) {
		int n = 8, res = 0;
		
		ArrayList<Integer> count = new ArrayList<>();
		
		for (int i = 0; i < n; i++) {
			count.add(0);
		}	
		count.add(1);
		
		for (int i = count.size() - 1; i >= 1; i--) {
			int c = count.get(i);
			res += c;
			for (int j = 0; j < i; j++) {
				int el = count.remove(j);
				count.add(j, el + c);
			}
		}
		res += count.get(0);
		System.out.println(res);
	}
}
