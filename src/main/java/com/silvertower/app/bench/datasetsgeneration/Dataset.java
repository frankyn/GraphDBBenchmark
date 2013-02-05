package com.silvertower.app.bench.datasetsgeneration;

import java.util.ArrayList;

import com.silvertower.app.bench.dbinitializers.GraphDescriptor;
import com.silvertower.app.bench.dbinitializers.GraphProperty;

public interface Dataset {
	
	public ArrayList<GraphProperty> generate();
}
