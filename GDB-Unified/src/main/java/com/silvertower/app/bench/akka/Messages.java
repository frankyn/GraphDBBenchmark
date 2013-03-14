package com.silvertower.app.bench.akka;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.silvertower.app.bench.datasets.Dataset;
import com.silvertower.app.bench.dbinitializers.DBInitializer;
import com.silvertower.app.bench.workload.IntensiveWorkload;
import com.silvertower.app.bench.workload.TraversalWorkload;

public class Messages {
	public static class Initializer implements Serializable {
		private static final long serialVersionUID = 2534437055158370075L;
		private final DBInitializer i;
		public Initializer(DBInitializer i) {
			this.i = i;
		}
		public DBInitializer getInitializer() {
			return i;
		}
	}
	
	public static class LoadBench implements Serializable {
		private static final long serialVersionUID = -6399708538774605441L;
		private final Dataset d;
		private final boolean batchLoading;
		public LoadBench(Dataset d, boolean batchLoading) {
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
	
	public static class Load implements Serializable {
		private static final long serialVersionUID = -4879740030488164881L;
		private final Dataset d;
		public Load(Dataset d) {
			this.d = d;
		}
		
		public Dataset getDataset() {
			return d;
		}
	}
	
	public static class SlaveInfos implements Serializable {
		private static final long serialVersionUID = -4333435361885242243L;
		private final int nCores;
		public SlaveInfos(int nCores) {
			this.nCores = nCores;
		}
		
		public int getNCores() {
			return nCores;
		}
	}
	
	public static class AggregateResult implements Serializable {
		private static final long serialVersionUID = -4805106473248761185L;
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
			return new TimeResult(wallMeanTime, cpuMeanTime);
		}
	}
	
	public static class TimeResult implements Serializable {
		private static final long serialVersionUID = -3820776238428789950L;
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
		
		public String toString() {
			return String.format("Wall time: %f and CPU time: %f", wallTime, cpuTime);
		}
	}
	
	public static class IntensiveWork implements Serializable {
		private static final long serialVersionUID = -6005829209802293908L;
		private final IntensiveWorkload w;
		private final int howManyOp;
		private final int howManyClients;
		public IntensiveWork(IntensiveWorkload w, int howManyOp, int howManyClients) {
			this.w = w;
			this.howManyOp = howManyOp;
			this.howManyClients = howManyClients;
		}
		
		public IntensiveWorkload getWorkload() {
			return w;
		}
		
		public int getHowManyOp() {
			return howManyOp;
		}

		public int getHowManyClients() {
			return howManyClients;
		}

		public String getDescription() {
			return String.format("%s with %d operations and %d clients", w.getName(), howManyOp, howManyClients);
		}
	}
	
	public static class TraversalWork implements Serializable {
		private static final long serialVersionUID = -3734788843421921270L;
		private TraversalWorkload w;
		public TraversalWork(TraversalWorkload w) {
			this.w = w;
		}
		
		public TraversalWorkload getWorkload() {
			return w;
		}
		
		public String getDescription() {
			return w.getName();
		}
	}
	
	public static class NumberOfClients implements Serializable {
		private static final long serialVersionUID = -5095668060726594399L;
		private final int howManyClients;
		public NumberOfClients(int n) {
			this.howManyClients = n;
		}
		public int getHowManyClients() {
			return howManyClients;
		}
	}
	
	public static class Ack implements Serializable {
		private static final long serialVersionUID = -4287805372778660583L;
	}
	
	public static class GetResult implements Serializable {
		private static final long serialVersionUID = 2324105582368990705L;
	}
	
	public static class StopCurrentDB implements Serializable {
		private static final long serialVersionUID = -1624035463475420728L;
	}
	
	public static class SlaveInitialization implements Serializable {
		private static final long serialVersionUID = 5895081999653072251L;
		private final int id;
		public SlaveInitialization(int id) {
			this.id = id;
		}
		
		public int getId() {
			return id;
		}
	}
	
	public static class StartWork implements Serializable {
		private static final long serialVersionUID = -711201189331133788L;
	}
	
	public static class Error implements Serializable {
		private static final long serialVersionUID = 7219750301860126474L;
		private final String s;
		public Error(String s) {
			this.s = s;
		}
		public String getMessage() {
			return s;
		}
	}
}
