package com.silvertower.app.bench.dbinitializers;

import java.io.Serializable;

public class GraphConnectionInformations implements Serializable {
	private static final long serialVersionUID = -913277476505428369L;
	private int port;
	private String add;
	private String graphName;
	public GraphConnectionInformations(String add, int port, String graphName) {
		this.port = port;
		this.add = add;
		this.graphName = graphName;
	}
	public int getPort() {
		return port;
	}

	public String getAddress() {
		return add;
	}

	public String getGraphName() {
		return graphName;
	}
}
