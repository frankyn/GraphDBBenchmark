package com.silvertower.app.bench.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;


@SuppressWarnings("serial")
public abstract class TabPanel extends JPanel {
	protected final DefaultListModel<String> elementsModel;
	protected List<Object> chosenElementsObjects;
	protected JFrame parent;
	public TabPanel(JFrame parent) {
		this.parent = parent;
		this.chosenElementsObjects = new ArrayList<Object>();
		this.setLayout(new GridBagLayout());
		
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setPreferredSize(new Dimension(180, 150));
		buttonsPanel.setLayout(new FlowLayout());
		addButtons(buttonsPanel);
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridwidth = 2;
		c.weightx = 0.4;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		
		this.add(buttonsPanel, c);
		
		this.elementsModel = new DefaultListModel();
		final JList<String> pList = new JList<String>(elementsModel);
		pList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		pList.setLayoutOrientation(JList.VERTICAL);
		pList.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DELETE) {
					int indexRemoved = pList.getSelectedIndex();
					elementsModel.remove(indexRemoved);
					chosenElementsObjects.remove(indexRemoved);
				}
			}
		});
		pList.setPreferredSize(new Dimension(220, 150));
		JScrollPane listScrollPane = new JScrollPane(pList);
		
		GridBagConstraints c1 = new GridBagConstraints();
		c1.gridy = 0;
		c1.gridwidth = 3;
		c1.fill = GridBagConstraints.BOTH;
		c1.weightx = 0.6;
		c1.weighty = 1;
		this.add(listScrollPane, c1);
	}
	
	protected abstract void addButtons(JPanel buttonsPanel);
}
