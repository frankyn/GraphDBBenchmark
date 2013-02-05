package com.silvertower.app.bench.main;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;

public class ExampleClass {
	class LongRunningThread extends Thread {
		private int n;
		public LongRunningThread(int n) {
			this.n = n;
		}
		public void run() {
			ArrayList l = new ArrayList();
			for (int i = 0; i < n; i++) {
				l.add(new Object());
			}
			long time = ManagementFactory.getThreadMXBean().getThreadUserTime(this.getId());
			System.out.println("Long running thread " + this.getId() + " execution time: " + time);
		}
	}
	
	class MyThread extends Thread {
		int n;
		public MyThread(int n) {
			this.n = n;
		}
		public void run() {
			/*ArrayList l = new ArrayList();
			for (int i = 0; i < n; i++) {
				l.add(new Object());
			}
			*/
			while(true) {
				try {
					this.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				long time = ManagementFactory.getThreadMXBean().getThreadUserTime(this.getId());
				System.out.println("My thread " + this.getId() + " execution time: " + time);
			}
		}
	}
	
	public void example() {
		/*System.out.println(System.nanoTime());
		System.out.println(System.nanoTime());
		System.out.println("Cpu time supported? " + ManagementFactory.getThreadMXBean().isThreadCpuTimeSupported());
		System.out.println("Cpu time enabled? " + ManagementFactory.getThreadMXBean().isThreadCpuTimeEnabled());
		for (int i = 1; i < 10; ++i) {
			new LongRunningThread(i*1000000).start();
		}
		
		for (int i = 1; i < 10; ++i) {
			new MyThread(i*100).start();
		}*/
		new MyThread(0).start();
	}	
}
