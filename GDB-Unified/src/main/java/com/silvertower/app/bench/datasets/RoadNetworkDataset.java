package com.silvertower.app.bench.datasets;


public class RoadNetworkDataset extends Dataset {
	private static final long serialVersionUID = 6346344739974032046L;

	public RoadNetworkDataset(int nVertices, String datasetFP) {
		super(nVertices, "Road");
	}

	public void generate() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isDirected() {
		// TODO Auto-generated method stub
		return false;
	}
}
