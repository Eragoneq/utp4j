package com.utp4j.data;

public class MicroSecondsTimeStamp {

	private static long initDateMillis = System.currentTimeMillis();
	private static long startNs = System.nanoTime();
	
	public long timeStamp() {
		
		long currentNs = System.nanoTime();
		long deltaMs = (currentNs - startNs)/1000;
		
		return initDateMillis*1000 + deltaMs;
	}
	
	public long getBegin() {
		return (initDateMillis * 1000);
	}
	
	public long getStartNs() {
		return startNs;
	}
	
	public long getInitDateMs() {
		return initDateMillis;
	}

}
