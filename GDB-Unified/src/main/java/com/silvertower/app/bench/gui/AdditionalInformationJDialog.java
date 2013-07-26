package com.silvertower.app.bench.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.lang.math.NumberUtils;

public class AdditionalInformationJDialog extends JDialog {
	private List<Object> convertedTFObjects;
	public AdditionalInformationJDialog(JFrame parent, List<String> labels, final List<Class> expectedTypes) {
		super(parent, "Enter additional information", true);
		this.convertedTFObjects = new ArrayList<Object>();
		
		JPanel labelsPanel = new JPanel();
		labelsPanel.setLayout(new GridLayout(labels.size(), 1));
		JPanel infosPanel = new JPanel();
		infosPanel.setLayout(new GridLayout(labels.size(), 1));
		final List<JTextField> tfs = new ArrayList<JTextField>();
		for (String label: labels) {
			labelsPanel.add(new JLabel(label));
			JTextField tf = new JTextField("Enter value");
			infosPanel.add(tf);
			tfs.add(tf);
		}
		
		JPanel buttonsPanel = new JPanel();
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < tfs.size(); i++) {
					String infoValue = tfs.get(i).getText();
					Class expectedType = expectedTypes.get(i);
					if (expectedType.equals(Integer.class)) {
						convertedTFObjects.add(NumberUtils.toInt(infoValue, 0));
					}
					else if (expectedType.equals(Boolean.class)) {
						convertedTFObjects.add(Boolean.parseBoolean(infoValue));
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
		this.getContentPane().add(infosPanel, BorderLayout.WEST);
		this.getContentPane().add(labelsPanel, BorderLayout.EAST);
		this.getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
		this.pack();
		this.setLocationRelativeTo(parent);
	}
	
	public Object[] showDialog() {
		this.setVisible(true);
		if (convertedTFObjects.size() == 0) return null;
		return convertedTFObjects.toArray();
	}
}
