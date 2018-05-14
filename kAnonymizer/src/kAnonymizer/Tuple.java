package kAnonymizer;

import java.util.ArrayList;

public class Tuple implements Comparable<Tuple>{
	public ArrayList<String> data;

	public Tuple(ArrayList<String> data) {
		this.data = data;
	}

	public ArrayList<String> getData() {
		return data;
	}
	
	@Override
	public int compareTo(Tuple t) {
		ArrayList<String> data = t.getData();
		
		assert(this.data.size() == data.size());
		
		int res = 0;
		for (int i = 0; i < data.size(); i++) {
			res = this.data.get(i).compareTo(data.get(i));
			if (res != 0)
				return res;
		}
		
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
