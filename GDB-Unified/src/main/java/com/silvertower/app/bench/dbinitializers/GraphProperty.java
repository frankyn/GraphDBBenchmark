package com.silvertower.app.bench.dbinitializers;

import java.io.Serializable;
import java.util.ArrayList;
/**
 * A GraphProperty represents a pair composed of a field and all its possible values.
 * Such a graph property will be used by a GraphDescriptor.
 * @author Vansteenberghe Valentin
 *
 */
public class GraphProperty implements Serializable {
	private static final long serialVersionUID = 6059754581664164146L;
	private String fieldName;
	private ArrayList<Object> fieldPossibleValues;
	
	public GraphProperty (String fieldName, ArrayList<Object> fieldPossibleValues) {
		this.fieldName = fieldName;
		this.fieldPossibleValues = fieldPossibleValues;
	}
	
	public String getFieldName() {
		return fieldName;
	}
	
	public ArrayList<Object> getFieldPossibleValues() {
		return fieldPossibleValues;
	}
}