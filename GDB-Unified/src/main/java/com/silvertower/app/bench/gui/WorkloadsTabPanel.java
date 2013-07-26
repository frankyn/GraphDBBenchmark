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
							AdditionalInformationJDialog infosDialog = new AdditionalInformationJDialog(parent, labels, expectedTypes);
							Object[] params = infosDialog.showDialog();
							
							// Do nothing if the dialog was canceled
							if (params == null) return;
							
							IntensiveWorkload workload = (IntensiveWorkload) c.getConstructors()[0].newInstance();
							StringBuilder listElementString = new StringBuilder();
							listElementString.append(workload + "(");
							for (int i = 0; i < params.length; i++) {
								if (i != params.length - 1) {
									listElementString.append(params[i] + ", ");
								}
								else {
									listElementString.append(params[i] + ")");
								}
							}
							chosenElementsObjects.add(workload);
							elementsModel.addElement(listElementString.toString());
						}
						
						else {
							List<String> labels = new ArrayList<String>();
							labels.add("Number of hops");
							List<Class> expectedTypes = new ArrayList<Class>();
							expectedTypes.add(Integer.class);
							AdditionalInformationJDialog infosDialog = new AdditionalInformationJDialog(parent, labels, expectedTypes);
							Object[] params = infosDialog.showDialog();
							
							// Do nothing if the dialog was canceled
							if (params == null) return;
							
							// There is only one element in params, the hops limit
							TraversalWorkload workload = (TraversalWorkload) c.getConstructors()[0].newInstance(params[0]);
							
							chosenElementsObjects.add(workload);
							elementsModel.addElement(workload);
						}
						
						
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
