package com.silvertower.app.bench.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class FormJDialog extends JDialog {
	private JFrame parent;
	private final FormJPanel form;
	private OKCancelButtonsPanel buttons;
	public FormJDialog(final JFrame parent, final FormJPanel form) {
		super(parent, "Enter additional information", true);
		this.parent = parent;
		this.form = form;
		this.buttons = new OKCancelButtonsPanel();
		buttons.setOKActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!form.validateContent()) {
					JOptionPane.showMessageDialog(parent, "Empty field(s)!", 
							"Error", JOptionPane.ERROR_MESSAGE);
				}
				else {
					setVisible(false);
					dispose();
				}
			}
		});
		buttons.setCancelActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(form, BorderLayout.NORTH);
		this.getContentPane().add(buttons, BorderLayout.SOUTH);
	}
	
	public Object[] showDialog() {
		this.pack();
		this.setLocationRelativeTo(parent);
		this.setVisible(true);
		if (form.getConvertedObjects().size() == 0) return null;
		return form.getConvertedObjects().toArray();
	}
}
