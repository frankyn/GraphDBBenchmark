package com.silvertower.app.bench.workload;


import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.silvertower.app.bench.akka.GraphDescriptor;

public abstract class IntensiveWorkload implements Serializable, Workload {
	private static final long serialVersionUID = 8599937655942079452L;
	private String name;
	private int nOps;
	private int nClients;
	private boolean rexPro;
	public IntensiveWorkload(String name, int nOps, int nClients, boolean rexPro) {
		this.name = name;
		this.nOps = nOps;
		this.nClients = nClients;
		this.rexPro = rexPro;
	}
	
	public String toString() {
		return String.format("%s with %d ops, %d clients, rexpro=%b", name, nOps, nClients, rexPro);
	}
	
	public abstract String generateRequest(GraphDescriptor gDesc, int threadId, int number);
	
	public abstract void operation(GraphDescriptor gDesc, int threadId);
	
	public String getName() {
		return name;
	}

	public int getnOps() {
		return nOps;
	}

	public int getnClients() {
		return nClients;
	}

	public boolean isRexPro() {
		return rexPro;
	}

	public IntensiveWorkload reduceWorkload(int nOps, int nClients) {
		IntensiveWorkload reducedWorkload = null;
		try {
			Constructor specificConstructor = this.getClass().getConstructor(Integer.TYPE, Integer.TYPE, Boolean.TYPE);
			reducedWorkload = (IntensiveWorkload) specificConstructor.newInstance(nOps, nClients, rexPro);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return reducedWorkload;
	}
	
	public Type getType() {
		return Type.INTENSIVE;
	}
}