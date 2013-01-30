package com.silvertower.app.bench.dbinitializers;

import java.util.ArrayList;

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