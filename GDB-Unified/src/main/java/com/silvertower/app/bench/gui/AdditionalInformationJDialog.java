package com.silvertower.app.bench.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class AdditionalInformationJDialog extends JDialog {
	public AdditionalInformationJDialog(JFrame parent, List<String> labels, List<Class> expectedTypes) {
		super(parent, "Enter additional information", true);
		JPanel labelsPanel = new JPanel();
		JPanel infosPanel = new JPanel();
		labelsPanel.setLayout(new GridLayout(labels.size(), 1));
		infosPanel.setLayout(new GridLayout(labels.size(), 1));
		for (String label: labels) {
			labelsPanel.add(new JLabel(label));
			infosPanel.add(new JTextField("Enter value"));
		}
		
		JPanel buttonsPanel = new JPanel();
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		}
		JButton cancelButton = new JButton("Cancel");
		buttonsPanel.add(okButton);
		buttonsPanel.add(cancelButton);
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(infosPanel, BorderLayout.WEST);
		this.getContentPane().add(labelsPanel, BorderLayout.EAST);
		this.getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
		this.pack();
	}
	
	public List<Object> showDialog() {
		this.setVisible(true);
		
	}
}
