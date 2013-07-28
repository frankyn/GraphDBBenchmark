package com.silvertower.app.bench.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;

import org.apache.commons.lang.math.NumberUtils;

import com.silvertower.app.bench.utils.IP;

public class FormJPanel extends JPanel {
	private List<Object> convertedObjects;
	private final List<Class> expectedTypes;
	public FormJPanel() {
		this.convertedObjects = new ArrayList<Object>();
		this.expectedTypes = new ArrayList<Class>();
		this.setLayout(new GridLayout(0, 2));
	}
	
	public boolean validateContent() {
		boolean validity = true;
		List<Object> possibleConvertedObjectsList = new ArrayList<Object>();
		int j = -1; // For expected type array
		for (int i = 1; i < this.getComponentCount(); i+=2) {
			Component component = this.getComponent(i);
			if (component instanceof JTextField) {
				String infoValue = ((JTextField) component).getText();
				if (infoValue.length() == 0) validity = false;
				Class expectedType = expectedTypes.get(++j);
				if (expectedType.equals(Integer.class)) {
					Integer integer = checkInt(infoValue);
					if (integer != null) possibleConvertedObjectsList.add(integer);
					else validity = false;
				}
				else if (expectedType.equals(Boolean.class)) {
					Boolean bool = checkBoolean(infoValue);
					if (bool != null) possibleConvertedObjectsList.add(bool);
					else validity = false;
				}
				else if (expectedType.equals(IP.class)) {
					IP ip = checkIp(infoValue);
					if (ip != null) possibleConvertedObjectsList.add(ip);
					else validity = false;
				}
			}
			else if (component instanceof JComboBox) {
				Object selection = ((JComboBox) component).getSelectedItem();
				possibleConvertedObjectsList.add(selection);
			}
		}
		if (validity) convertedObjects = possibleConvertedObjectsList;
		return validity;
	}
	
	public void addAdditionalTF(String label, Class expectedType) {
		this.add(new JLabel(label));
		JTextField tf = new JTextField();
		tf.setPreferredSize(new Dimension(80, 20));
		this.add(tf);
		expectedTypes.add(expectedType);
		this.repaint();
	}
	
	public void addAdditionalTF(String label, Class expectedType, String defaultValue) {
		this.add(new JLabel(label));
		JTextField tf = new JTextField(defaultValue);
		tf.setPreferredSize(new Dimension(80, 20));
		this.add(tf);
		expectedTypes.add(expectedType);
		this.repaint();
	}
	
	public void addAdditionalComboBox(String label, Object[] choices, ListCellRenderer r) {
		this.add(new JLabel(label));
		JComboBox comboChoices = new JComboBox(choices);
		comboChoices.setRenderer(r);
		this.add(comboChoices);
		this.repaint();
	}
	
	public List<Object> getConvertedObjects() {
		return convertedObjects;
	}
	
	private Boolean checkBoolean(String s) {
		if (s.equalsIgnoreCase("true")) return true;
		else if (s.equalsIgnoreCase("false")) return false;
		else return null;
	}
	
	private Integer checkInt(String s) {
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	private IP checkIp(String s) {
		try {
			return new IP(s);
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
