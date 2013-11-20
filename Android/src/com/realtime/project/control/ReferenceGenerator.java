package com.realtime.project.control;

public class ReferenceGenerator extends Thread {
	
	private double period;
	private double ref;	

	public ReferenceGenerator(double pos) {
		ref = pos;
	}
	
	public synchronized void setRef(double pos) {
		ref = pos;
	}
	
	public synchronized double getRef() {
		return ref;
	}
	
	public synchronized void setPeriod(double period) {
		this.period = period;
	}
	
	public synchronized double getPeriod() {
		return period;
	}

}

