package com.silvertower.app.bench.dbinitializers;

import java.util.ArrayList;
/**
 * A GraphProperty represents a pair composed of a field and all its possible values.
 * Such a graph property will be used by a GraphDescriptor.
 * @author Vansteenberghe Valentin
 *
 */
public class GraphProperty {
	private Object fieldName;
	private ArrayList<Object> fieldPossibleValues;
	
	public GraphProperty (Object fieldName, ArrayList<Object> fieldPossibleValues) {
		this.fieldName = fieldName;
		this.fieldPossibleValues = fieldPossibleValues;
	}
	
	public Object getFieldName() {
		return fieldName;
	}
	
	public ArrayList<Object> getFieldPossibleValues() {
		return fieldPossibleValues;
	}
}