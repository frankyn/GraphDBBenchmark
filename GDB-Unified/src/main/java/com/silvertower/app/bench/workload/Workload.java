package com.silvertower.app.bench.workload;

public interface Workload {
	public enum Type {INTENSIVE, TRAVERSAL, LOAD};
	
	/**
	 * 
	 * @return the type of this workload
	 */
	public Type getType();
	
}
