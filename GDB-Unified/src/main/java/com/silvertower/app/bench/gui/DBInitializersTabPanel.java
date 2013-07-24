package com.silvertower.app.bench.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.reflections.Reflections;

import com.silvertower.app.bench.dbinitializers.DBInitializer;

public class DBInitializersTabPanel extends TabPanel {
	protected void addButtons(JPanel buttonsPanel) {
		Reflections reflections = new Reflections("com.silvertower.app.bench.dbinitializers");
		for (final Class c: reflections.getTypesAnnotatedWith(com.silvertower.app.bench.annotations.Custom.class)) {
			JButton b = new JButton(c.getSimpleName());
			b.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						DBInitializer dbInit = (DBInitializer) c.getConstructors()[0].newInstance();
						chosenElementsObjects.add(dbInit);
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