package kAnonymizer;

import java.util.ArrayList;

public class Tuple implements Comparable<Tuple> {
	public ArrayList<String> data;
	public ArrayList<Type> type;

	public Tuple(ArrayList<String> data, ArrayList<Type> type) {
		this.data = data;
		this.type = type;
	}

	@Override
	public int compareTo(Tuple t) {
		int m = Math.min(this.data.size(), t.data.size());
		
		for (int i = 0; i < m; i++) {
			switch (type.get(i)) {
				case NUMERIC:
					Float thisElement = Float.parseFloat(this.data.get(i));
					Float otherElement = Float.parseFloat(t.data.get(i));
					if (thisElement < otherElement)
						return 1;
					else if (otherElement < thisElement)
						return -1;
				break;
				
				case STRING:
				case CATEGORICAL:
					int result = this.data.get(i).compareTo(t.data.get(i));
					if (result != 0)
						return result;
				break;
					
				default:
				break;
			}
		}
		
		return 0;
	}
}
