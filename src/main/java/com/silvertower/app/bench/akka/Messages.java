package com.silvertower.app.bench.akka;

import java.util.ArrayList;
import java.util.List;

import com.silvertower.app.bench.datasetsgeneration.Dataset;
import com.silvertower.app.bench.dbinitializers.DBInitializer;
import com.silvertower.app.bench.dbinitializers.GraphDescriptor;

public class Messages {
	static class InitializeDB {
		private final DBInitializer i;
		public InitializeDB(DBInitializer i) {
			this.i = i;
		}
		public DBInitializer getInitializer() {
			return i;
		}
	}
	
	static class FillDB {
		private final Dataset d;
		public FillDB(Dataset d) {
			this.d = d;
		}
		public Dataset getDataset() {
			return d;
		}
	}
	
	static class FillDBBatch {
		private final Dataset d;
		public FillDBBatch(Dataset d) {
			this.d = d;
		}
		public Dataset getDataset() {
			return d;
		}
	}
	
	static class Result {
		private final List<Double> values;
		public Result() {
			this.values = new ArrayList<Double>();
		}
		public void addValue(Double n) {
			values.add(n);
		}
		public void addValues(double[] numbers) {
			for (Double n: numbers) {
				values.add(n);
			}
		}
		public List<Double> getValues() {
			return values;
		}
	}
	
	static class GraphDescReturned {
		private final GraphDescriptor gDesc;
		public GraphDescReturned(GraphDescriptor gDesc) {
			this.gDesc = gDesc;
		}
		public GraphDescriptor getGraphDesc() {
			return gDesc;
		}
	}
	
	static class GetResult {
	}
	
	static class VanishDB {
	}
	
	static class Error {
		private final String s;
		public Error(String s) {
			this.s = s;
		}
		public String getMessage() {
			return s;
		}
	}
}
