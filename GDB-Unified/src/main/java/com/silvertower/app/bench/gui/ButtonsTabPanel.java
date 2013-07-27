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


public abstract class ButtonsTabPanel <T> extends JPanel {
	protected final DefaultListModel<T> elementsModel;
	protected JFrame parent;
	public ButtonsTabPanel(JFrame parent) {
		this.parent = parent;
		this.elementsModel = new DefaultListModel<T>();
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
		
		final JList<T> pList = new JList<T>(elementsModel);
		pList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		pList.setLayoutOrientation(JList.VERTICAL);
		pList.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DELETE) {
					int indexRemoved = pList.getSelectedIndex();
					elementsModel.remove(indexRemoved);
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
	
	public List<T> getCollectedElements() {
		List<T> collectedElements = new ArrayList<T>();
		for (int i = 0; i < elementsModel.size(); i++) {
			collectedElements.add(elementsModel.get(i));
		}
		return collectedElements;
	}
	
	public DefaultListModel<T> getElementsModel() {
		return elementsModel;
	}
	
	protected abstract void addButtons(JPanel buttonsPanel);
}
