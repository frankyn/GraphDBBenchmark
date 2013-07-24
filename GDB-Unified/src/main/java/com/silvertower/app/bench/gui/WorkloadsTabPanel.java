package com.silvertower.app.bench.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.reflections.Reflections;

import com.silvertower.app.bench.workload.IntensiveWorkload;
import com.silvertower.app.bench.workload.TraversalWorkload;

public class WorkloadsTabPanel extends TabPanel {
	
	public WorkloadsTabPanel(JFrame parent) {
		super(parent);
	}

	protected void addButtons(JPanel buttonsPanel) {
		Reflections reflections = new Reflections("com.silvertower.app.bench.workload");
		for (final Class c: reflections.getTypesAnnotatedWith(com.silvertower.app.bench.annotations.Custom.class)) {
			JButton b = new JButton(c.getSimpleName());
			b.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						if (IntensiveWorkload.class.isAssignableFrom(c)) {
							List<String> labels = new ArrayList<String>();
							labels.add("Number of ops");
							labels.add("Number of clients");
							labels.add("Rexpro");
							List<Class> expectedTypes = new ArrayList<Class>();
							expectedTypes.add(Integer.class);
							expectedTypes.add(Integer.class);
							expectedTypes.add(Boolean.class);
							new AdditionalInformationJDialog(parent, labels, expectedTypes).setVisible(true);
							
							IntensiveWorkload workload = (IntensiveWorkload) c.getConstructors()[0].newInstance();
							chosenElementsObjects.add(workload);
							
						}
						
						else {
							List<String> labels = new ArrayList<String>();
							labels.add("Number of hops");
							List<Class> expectedTypes = new ArrayList<Class>();
							expectedTypes.add(Integer.class);
							new AdditionalInformationJDialog(parent, labels, expectedTypes).setVisible(true);
							
							TraversalWorkload workload = (TraversalWorkload) c.getConstructors()[0].newInstance();
							chosenElementsObjects.add(workload);
						}
						
						elementsModel.addElement(c.getSimpleName());
					} catch (InstantiationException | IllegalAccessException
							| IllegalArgumentException
							| InvocationTargetException | SecurityException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				
			});
			buttonsPanel.add(b);
		}
	}
}
