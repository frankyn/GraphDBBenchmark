package com.silvertower.app.bench.gui;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import java.awt.GridBagLayout;
import javax.swing.JButton;
import javax.swing.JProgressBar;

public class MainGui {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainGui window = new MainGui();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainGui() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 600, 375);
		frame.setMinimumSize(new Dimension(450, 300));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{Double.MIN_VALUE, 0.0, 0.0, 0.0, 0.0};
		frame.getContentPane().setLayout(gridBagLayout);
		
		JTabbedPane content = new JTabbedPane();
		JPanel dbsPanel = new DBInitializersTabPanel(frame);
		content.addTab("Databases", dbsPanel);
		JPanel workloadsPanel = new WorkloadsTabPanel(frame);
		content.addTab("Workloads", workloadsPanel);
		
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
		
		JProgressBar progressBar = new JProgressBar();
		progressBar.setPreferredSize(new Dimension (300, 15));
		startAndProgressPanel.add(progressBar, c2);
	}
}
