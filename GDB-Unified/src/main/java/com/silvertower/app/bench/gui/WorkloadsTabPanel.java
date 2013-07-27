package com.silvertower.app.bench.gui;

import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.reflections.Reflections;

import com.silvertower.app.bench.workload.Workload;
import com.silvertower.app.bench.annotations.*;

public class WorkloadsTabPanel extends ButtonsTabPanel <Workload> {
	
	public WorkloadsTabPanel(JFrame parent) {
		super(parent);
	}

	protected void addButtons(JPanel buttonsPanel) {
		Reflections workloadReflections = new Reflections("com.silvertower.app.bench.workload");
		Set<Class<?>> workloadClasses = workloadReflections.getTypesAnnotatedWith(Custom.class);
		for (Class c: workloadClasses) {
			JButton b = new JButton(c.getSimpleName());
			b.addActionListener(new WorkloadsTabActionListener(this, parent, c));
			buttonsPanel.add(b);
		}
	}
}
