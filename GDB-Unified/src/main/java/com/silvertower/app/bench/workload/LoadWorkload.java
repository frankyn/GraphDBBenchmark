package com.silvertower.app.bench.workload;

import java.io.Serializable;

import com.silvertower.app.bench.annotations.Custom;
import com.silvertower.app.bench.datasets.Dataset;

@Custom
public class LoadWorkload implements Serializable, Workload {
	private static final long serialVersionUID = -7530047412055350898L;
	private int bufferSize;
	private Dataset d;
	public LoadWorkload(int bufferSize, Dataset d) {
		this.bufferSize = bufferSize;
		this.d = d;
	}
	
	public Type getType() {
		return Type.LOAD;
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public Dataset getDataset() {
		return d;
	}
	
	public String toString() {
		return String.format("Load workload with dataset %s with buffer size=%d", d.toString(), bufferSize);
	}
}
