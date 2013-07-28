package com.silvertower.app.bench.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JProgressBar;

import com.silvertower.app.bench.dbinitializers.DBInitializer;
import com.silvertower.app.bench.main.GDBMain;
import com.silvertower.app.bench.utils.IP;
import com.silvertower.app.bench.utils.Port;
import com.silvertower.app.bench.workload.Workload;

public class MainGui {
	public JFrame frame;

	public MainGui() {
		frame = new JFrame();
		frame.setBounds(100, 100, 600, 375);
		frame.setMinimumSize(new Dimension(450, 300));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setTitle("GDB GUI");
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{Double.MIN_VALUE, 0.0, 0.0, 0.0, 0.0};
		frame.getContentPane().setLayout(gridBagLayout);
		
		JTabbedPane content = new JTabbedPane();
		final DBInitializersTabPanel dbsPanel = new DBInitializersTabPanel(frame);
		content.addTab("Databases", dbsPanel);
		final WorkloadsTabPanel workloadsPanel = new WorkloadsTabPanel(frame);
		content.addTab("Workloads", workloadsPanel);
		JPanel configFormPanel = new JPanel();
		configFormPanel.setLayout(new BorderLayout());
		final FormJPanel configForm = new FormJPanel();
		configForm.addAdditionalTF("Server IP", IP.class, "127.0.0.1");
		configForm.addAdditionalTF("Master client IP", IP.class, "127.0.0.1");
		configForm.addAdditionalTF("Slave client IP", IP.class, "127.0.0.1");
		JScrollPane scrollForm = new JScrollPane(configForm);
		JPanel buttonPanel = new JPanel();
		JButton addSlaveButton = new JButton("Add new slave");
		addSlaveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				configForm.addAdditionalTF("Slave client IP", IP.class);
				frame.repaint();
			}
		});
		buttonPanel.add(addSlaveButton);
		configFormPanel.add(scrollForm, BorderLayout.NORTH);
		configFormPanel.add(buttonPanel, BorderLayout.SOUTH);
		content.addTab("Configuration", configFormPanel);
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 4;
		c.fill = GridBagConstraints.BOTH;
		
		frame.getContentPane().add(content, c);
		
		GridBagConstraints c1 = new GridBagConstraints();
		c1.fill = GridBagConstraints.HORIZONTAL;
		c1.gridx = 0;
		c1.gridy = 4;
		
		JPanel startAndProgressPanel = new JPanel();
		
		frame.getContentPane().add(startAndProgressPanel, c1);
		
		JButton startButton = new JButton("Start");
		startButton.setPreferredSize(new Dimension (80, 25));
		startAndProgressPanel.add(startButton);
		
		GridBagConstraints c2 = new GridBagConstraints();
		c2.fill = GridBagConstraints.HORIZONTAL;
		
		final JProgressBar progressBar = new JProgressBar();
		progressBar.setPreferredSize(new Dimension (300, 15));
		progressBar.setStringPainted(true);
		startAndProgressPanel.add(progressBar, c2);
		
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				List<DBInitializer> dbs = dbsPanel.getCollectedElements();
				if (dbs.size() == 0) {
					JOptionPane.showMessageDialog(frame, "No database selected!", 
							"Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				List<Workload> workloads = workloadsPanel.getCollectedElements();
				if (workloads.size() == 0) {
					JOptionPane.showMessageDialog(frame, "No workload selected!", 
							"Error", JOptionPane.ERROR_MESSAGE);
					return;

				}
				
				if (!configForm.validateContent()) {
					JOptionPane.showMessageDialog(frame, "Incorrect config form", 
							"Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				List<Object> rawIps = configForm.getConvertedObjects();
				List<IP> ips = new ArrayList<IP>();
				for (Object o: rawIps) {
					ips.add((IP) o);
				}
				
				// TODO
				List<Port> ports = new ArrayList<Port>();
				ports.add(new Port("2552"));
				ports.add(new Port("2553"));
				
				for (int i = 3; i <= ips.size(); i++) {
					ports.add(new Port("2554"));
				}
				
				BenchmarkGUIExecutor worker = new BenchmarkGUIExecutor(progressBar, dbs, workloads);
				GDBMain.startActors(ips, ports, worker);
				progressBar.setMinimum(0);
				progressBar.setMaximum(worker.getTaskLength());
				worker.execute();
			}
		});
	}
}
