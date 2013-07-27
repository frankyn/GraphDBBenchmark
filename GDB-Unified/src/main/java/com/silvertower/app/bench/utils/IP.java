package com.silvertower.app.bench.utils;

public class IP {
	private String address;
	
	public IP(String s) throws NumberFormatException {
		if (!checkIp(s)) throw new NumberFormatException();
		else this.address = s;
	}
	
	public static boolean checkIp(String ip) {
		String[] parts = ip.split("\\.");
		if (parts.length != 4) return false;
		for (String partS: parts) {
			int part = 0;
			try {
				part = Integer.parseInt(partS);
			} catch (NumberFormatException e) {
				return false;
			}
			if (!(part >= 0 && part <= 255)) return false;
		}
		return true;
    }
	
	public String toString() {
		return address;
	}
}
