package com.silvertower.app.bench.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;

import org.apache.commons.lang.math.NumberUtils;

public class AdditionalInformationJDialog extends JDialog {
	private List<Object> convertedTFObjects;
	private JPanel infosPanel;
	private final List<Class> expectedTypes;
	private JFrame parent;
	public AdditionalInformationJDialog(JFrame parent) {
		super(parent, "Enter additional information", true);
		this.parent = parent;
		this.convertedTFObjects = new ArrayList<Object>();
		this.expectedTypes = new ArrayList<Class>();
		
		this.infosPanel = new JPanel();
		infosPanel.setLayout(new GridLayout(0, 2));
		
		JPanel buttonsPanel = new JPanel();
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < infosPanel.getComponentCount(); i+=2) {
					Component component = infosPanel.getComponent(i);
					if (component instanceof JTextField) {
						String infoValue = ((JTextField) component).getText();
						Class expectedType = expectedTypes.get(i);
						if (expectedType.equals(Integer.class)) {
							convertedTFObjects.add(NumberUtils.toInt(infoValue, 0));
						}
						else if (expectedType.equals(Boolean.class)) {
							convertedTFObjects.add(Boolean.parseBoolean(infoValue));
						}
					}
					else if (component instanceof JComboBox) {
						Object selection = ((JComboBox) component).getSelectedItem();
						convertedTFObjects.add(selection);
					}
				}
				setVisible(false);
				dispose();
			}
		});
		buttonsPanel.add(okButton);
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		buttonsPanel.add(cancelButton);
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(infosPanel, BorderLayout.NORTH);
		this.getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
	}
	
	public Object[] showDialog() {
		this.pack();
		this.setLocationRelativeTo(parent);
		this.setVisible(true);
		if (convertedTFObjects.size() == 0) return null;
		return convertedTFObjects.toArray();
	}
	
	public void addAdditionalTF(String label, Class expectedType) {
		infosPanel.add(new JLabel(label));
		infosPanel.add(new JTextField("Enter value"));
		expectedTypes.add(expectedType);
	}
	
	public void addAdditionalComboBox(String label, Object[] choices, ListCellRenderer r) {
		infosPanel.add(new JLabel(label));
		JComboBox comboChoices = new JComboBox(choices);
		comboChoices.setRenderer(r);
		infosPanel.add(comboChoices);
	}
}
