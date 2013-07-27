package com.silvertower.app.bench.gui;

import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class OKCancelButtonsPanel extends JPanel {
	private JButton okButton;
	private JButton cancelButton;
	public OKCancelButtonsPanel() {
		this.okButton = new JButton("OK");
		this.cancelButton = new JButton("Cancel");
		this.add(okButton);
		this.add(cancelButton);
	}
	
	public void setOKActionListener(ActionListener al) {
		okButton.addActionListener(al);
	}
	
	public void setCancelActionListener(ActionListener al) {
		cancelButton.addActionListener(al);
	}
}
