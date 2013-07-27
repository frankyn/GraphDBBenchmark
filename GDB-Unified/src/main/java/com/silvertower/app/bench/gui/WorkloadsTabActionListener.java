package com.silvertower.app.bench.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ListCellRenderer;

import org.reflections.Reflections;

import com.silvertower.app.bench.annotations.Custom;
import com.silvertower.app.bench.datasets.Dataset;
import com.silvertower.app.bench.workload.IntensiveWorkload;
import com.silvertower.app.bench.workload.LoadWorkload;
import com.silvertower.app.bench.workload.TraversalWorkload;
import com.silvertower.app.bench.workload.Workload;

public class WorkloadsTabActionListener implements ActionListener {
	private ButtonsTabPanel tabPanel;
	private JFrame mainFrame;
	private Class workloadClassAssociated;
	public WorkloadsTabActionListener(ButtonsTabPanel tabPanel, JFrame mainFrame, Class workloadClassAssociated) {
		this.tabPanel = tabPanel;
		this.mainFrame = mainFrame;
		this.workloadClassAssociated = workloadClassAssociated;
	}
	
	public void actionPerformed(ActionEvent e) {
		try {
			DefaultListModel<Workload> elementsModel = tabPanel.getElementsModel();
			if (IntensiveWorkload.class.isAssignableFrom(workloadClassAssociated)) {
				// The first workload must be a load workload
				if (elementsModel.isEmpty() || !(elementsModel.get(0) instanceof LoadWorkload)) {
					JOptionPane.showMessageDialog(mainFrame, "The first workload must be a load workload!", 
							"Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				
				FormJPanel form = new FormJPanel();
				form.addAdditionalTF("Number of ops", Integer.class);
				form.addAdditionalTF("Number of clients", Integer.class);
				form.addAdditionalTF("RexPro", Boolean.class);
				FormJDialog infosDialog = new FormJDialog(mainFrame, form);
				Object[] params = infosDialog.showDialog();
				
				// Do nothing if the dialog was canceled
				if (params == null) return;
				
				IntensiveWorkload workload = (IntensiveWorkload) workloadClassAssociated.getConstructors()[0].newInstance(params);
				elementsModel.addElement(workload);
			}
			
			else if (TraversalWorkload.class.isAssignableFrom(workloadClassAssociated)) {
				// The first workload must be a load workload
				if (elementsModel.isEmpty() || !(elementsModel.get(0) instanceof LoadWorkload)) {
					JOptionPane.showMessageDialog(mainFrame, "The first workload must be a load workload!", 
							"Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				FormJPanel form = new FormJPanel();
				form.addAdditionalTF("Number of hops", Integer.class);
				FormJDialog infosDialog = new FormJDialog(mainFrame, form);
				Object[] params = infosDialog.showDialog();
				
				// Do nothing if the dialog was canceled
				if (params == null) return;
				
				TraversalWorkload workload = (TraversalWorkload) workloadClassAssociated.getConstructors()[0].newInstance(params);
				elementsModel.addElement(workload);
			}
			
			else if (LoadWorkload.class.isAssignableFrom(workloadClassAssociated)) {
				if (elementsModel.size() != 0) {
					JOptionPane.showMessageDialog(mainFrame, "Only one load workload allowed!", 
							"Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				FormJPanel form = new FormJPanel();
				form.addAdditionalTF("Buffer size", Integer.class);
				
				ListCellRenderer r = new DefaultListCellRenderer() {
					public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
						String stringValue = ((Class) value).getSimpleName();
						return super.getListCellRendererComponent(list, stringValue, index, isSelected, cellHasFocus);
					}
				};
				
				Reflections datasetsReflections = new Reflections("com.silvertower.app.bench.datasets");
				Object[] datasetClasses = datasetsReflections.getTypesAnnotatedWith(Custom.class).toArray();
				
				form.addAdditionalComboBox("Dataset type", datasetClasses, r);
				
				form.addAdditionalTF("Number of vertices desired", Integer.class);
				
				FormJDialog infosDialog = new FormJDialog(mainFrame, form);
				
				Object[] params = infosDialog.showDialog();
				
				// Do nothing if the dialog was canceled
				if (params == null) return;
				
				Dataset d = (Dataset) ((Class)params[1]).getConstructors()[0].newInstance(params[2]);
				
				LoadWorkload workload = (LoadWorkload) workloadClassAssociated.getConstructors()[0].newInstance(params[0], d);
				elementsModel.addElement(workload);
			}
			
			
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException
				| InvocationTargetException | SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
