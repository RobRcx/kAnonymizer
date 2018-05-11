package test;

import java.util.ArrayList;
import java.util.Map.Entry;

import kAnonymizer.Tuple;
import kAnonymizer.Type;

public abstract class DataSetGenerator {
	
	public DataSetGenerator(ArrayList<Entry<Type, ArrayList<?>>> schema) {
	}
	public abstract ArrayList<Tuple> generate();
}
