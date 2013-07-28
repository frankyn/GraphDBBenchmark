package com.silvertower.app.bench.utils;

import java.io.Serializable;

public class Port implements Serializable {
	private static final long serialVersionUID = 2504860562256001431L;
	private int port;
	
	public Port(String s) {
		if (!checkPort(s)) throw new NumberFormatException();
		else this.port = Integer.parseInt(s);
	}
	
	public static boolean checkPort(String port) {
		int portN = 0;
		try {
			portN = Integer.parseInt(port);
		} catch (NumberFormatException e) {
			return false;
		}
		if (!(portN >= 0 && portN <= 65535)) return false;
		else return true;
	}
	
	public int toInt() {
		return port;
	}
}
