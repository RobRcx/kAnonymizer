package kAnonymizer;

import java.util.ArrayList;

public class Tuple implements Comparable<String>{
	public ArrayList<String> data;

	public Tuple(ArrayList<String> data) {
		this.data = data;
	}

	@Override
	public int compareTo(String o) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public String toString() {
		String res = "";
		for (String str : data)
			res = res + str + " ";
		return res.substring(0, res.length() - 1);
	}
	
}
