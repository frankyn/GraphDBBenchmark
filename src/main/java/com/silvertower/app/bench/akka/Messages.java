package com.silvertower.app.bench.akka;


import com.silvertower.app.bench.datasetsgeneration.Dataset;
import com.silvertower.app.bench.dbinitializers.DBInitializer;
import com.silvertower.app.bench.dbinitializers.GraphDescriptor;
import com.silvertower.app.bench.workload.Workload;

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
	
	static class AggregateResult {
	}
	
	static class TimeResult {
		private double wallTime;
		private double cpuTime;
	    public TimeResult(double wallTime, double cpuTime) {
	    	this.wallTime = wallTime;
	        this.cpuTime = cpuTime;
	    }
	    
		public double getWallTime() {
			return wallTime;
		}
		
		public double getCpuTime() {
			return cpuTime;
		}
	}
	
	static class LoadingEnded {
		private final GraphDescriptor gDesc;
		public LoadingEnded(GraphDescriptor gDesc) {
			this.gDesc = gDesc;
		}
		public GraphDescriptor getGraphDesc() {
			return gDesc;
		}
	}
	
	static class Work {
		private final Workload w;
		public Work(Workload w) {
			this.w = w;
		}
		
		public Workload getWork() {
			return w;
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
