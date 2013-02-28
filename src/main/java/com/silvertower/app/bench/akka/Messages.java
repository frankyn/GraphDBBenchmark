package com.silvertower.app.bench.akka;


import java.util.ArrayList;
import java.util.List;

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
		private final boolean batchLoading;
		public FillDB(Dataset d, boolean batchLoading) {
			this.d = d;
			this.batchLoading = batchLoading;
		}
		
		public Dataset getDataset() {
			return d;
		}
		
		public boolean isBatchLoading() {
			return batchLoading;
		}
	}
	
	static class SlaveInfos {
		private final int nCores;
		public SlaveInfos(int nCores) {
			this.nCores = nCores;
		}
		
		public int getNCores() {
			return nCores;
		}
	}
	
	static class AggregateResult {
		private List<TimeResult> times;
		public AggregateResult() {
			this.times = new ArrayList<TimeResult>();
		}
		
		public void addTime(TimeResult t) {
			times.add(t);
		}
		
		public TimeResult getMean() {
			double cpuMeanTime = 0;
			double wallMeanTime = 0;
			for (TimeResult t: times) {
				cpuMeanTime += t.getCpuTime();
				wallMeanTime += t.getWallTime();
			}
			cpuMeanTime /= times.size();
			wallMeanTime /= times.size();
			return new TimeResult(cpuMeanTime, wallMeanTime);
		}
	}
	
	public static class TimeResult {
		private double wallTime;
		private double cpuTime;
		private int x;
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
		
		public int getX() {
			return x;
		}
		
		public String toString() {
			return String.format("Wall time: %f and CPU time: %f", wallTime, cpuTime);
		}
	}
	
	static class GDesc {
		private final GraphDescriptor gDesc;
		public GDesc(GraphDescriptor gDesc) {
			this.gDesc = gDesc;
		}
		
		public GraphDescriptor getGraphDesc() {
			return gDesc;
		}
	}
	
	public static class Work {
		private final Workload w;
		private final int howManyOp;
		private final int howManyClients;
		public Work(Workload w, int howManyOp, int howManyClients) {
			this.w = w;
			this.howManyOp = howManyOp;
			this.howManyClients = howManyClients;
		}
		
		public Workload getWorkload() {
			return w;
		}
		
		public int getHowManyOp() {
			return howManyOp;
		}

		public int getHowManyClients() {
			return howManyClients;
		}

		public String getDescription() {
			if (w.isMT()) {
				return String.format("%s with %d operations and %d clients", w.getName(), howManyOp, howManyClients);
			}
			else {
				return String.format("%s using a single thread", w.getName());
			}
		}
	}
	
	static class NumberOfClients {
		private final int howManyClients;
		public NumberOfClients(int n) {
			this.howManyClients = n;
		}
		public int getHowManyClients() {
			return howManyClients;
		}
	}
	
	static class GetResult {
	}
	
	static class VanishDB {
	}
	
	static class GetNbCores {
	}
	
	static class StartWork {
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
