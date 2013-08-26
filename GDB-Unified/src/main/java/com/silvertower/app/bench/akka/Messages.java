package com.silvertower.app.bench.akka;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.silvertower.app.bench.dbinitializers.DBInitializer;
import com.silvertower.app.bench.utils.IP;
import com.silvertower.app.bench.utils.Port;

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
	
	public static class MasterClientInit implements Serializable {
		private static final long serialVersionUID = -4987874748111482448L;
		private IP[] slaveIps;
		private Port[] ports;

		public MasterClientInit(IP[] slaveIps, Port[] ports) {
			this.slaveIps = slaveIps;
			this.ports = ports;
		}
		
		public IP[] getIps() {
			return slaveIps;
		}
		
		public Port[] getPorts() {
			return ports;
		}
	}
	
	public static class AggregateResult implements Serializable {
		private static final long serialVersionUID = -4805106473248761185L;
		private List<TimeResult> times;
		public AggregateResult() {
			this.times = new ArrayList<TimeResult>();
		}
		
		public AggregateResult(Double result) {
			this.times = new ArrayList<TimeResult>();
			times.add(new TimeResult(result));
		}
		
		public AggregateResult(List<Double> results) {
			this.times = new ArrayList<TimeResult>();
			for (Double d: results) {
				TimeResult r = new TimeResult(d);
				times.add(r);
			}
		}
		
		public void addTime(TimeResult t) {
			times.add(t);
		}
		
		public void mergeWith(AggregateResult r) {
			for (TimeResult t: r.getAllTimes()) {
				times.add(t);
			}
		}
		
		public TimeResult getMean() {
			double wallMeanTime = 0;
			for (TimeResult t: times) {
				wallMeanTime += t.getTime();
			}
			wallMeanTime /= times.size();
			return new TimeResult(wallMeanTime);
		}
		
		public TimeResult getMin() {
			double min = Double.MAX_VALUE;
			for (TimeResult t: times) {
				min = t.getTime() < min ? t.getTime() : min;
			}
			return new TimeResult(min);
		}
		
		public TimeResult getMax() {
			double max = Double.MIN_VALUE;
			for (TimeResult t: times) {
				max = t.getTime() > max ? t.getTime() : max;
			}
			return new TimeResult(max);
		}
		
		public List<TimeResult> getAllTimes() {
			return times;
		}
		
		public String toString() {
			StringBuilder s = new StringBuilder();
			for (TimeResult r: times) {
				s.append("Measure: " + r.toString() + "\n");
			}
			s.append(String.format("Min %s - Mean %s - Max %s", getMin().toString(), 
					getMean().toString(), getMax().toString()));
			return s.toString();
		}
		
		public List<Double> getAllResultsAsDouble() {
			List<Double> results = new ArrayList<Double>();
			for (TimeResult r: times) {
				results.add(r.getTime());
			}
			return results;
		}
	}
	
	public static class Result implements Serializable {
		private static final long serialVersionUID = -4544518770275209076L;
		int graphSize;
		double time;
		public Result(int graphSize, double time) {
			this.graphSize = graphSize;
			this.time = time;
		}
		
		public int getGraphSize() {
			return graphSize;
		}
		
		public double getTime() {
			return time;
		}
		
		public String toString() {
			return graphSize + " graph elements loaded in " + (time / 1000000000.0) + "\n";
		}
	}
	
	public static class LoadResults implements Serializable {
		private static final long serialVersionUID = 3213333307648081315L;
		private ArrayList<Result> results;
		public LoadResults() {
			results = new ArrayList<Result>();
		}
		
		public void addResult(Result r) {
			results.add(r);
		}
		
		public ArrayList<Result> getResults() {
			return results;
		}
		
		public String toString() {
			StringBuilder b = new StringBuilder();
			for (Result r: results) {
				b.append(r.toString());
			}
			return b.toString();
		}
	}
	
	public static class TimeResult implements Serializable {
		private static final long serialVersionUID = -3820776238428789950L;
		private double wallTime;
	    public TimeResult(double wallTime) {
	    	this.wallTime = wallTime;
	    }
	    
		public double getTime() {
			return wallTime;
		}
		
		public String toString() {
			return String.format("Time: %f", wallTime);
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
	
	public static class ShutdownMessage implements Serializable {
		private static final long serialVersionUID = 143585913399369423L;
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
