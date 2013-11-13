package com.realtime.project.control;

import java.awt.*;

public class ReferenceGenerator{

	private int period;
	private double ref;

	public ReferenceGenerator(double pos) {
		ref = pos;
	}
	public synchronized void setRef(double pos){
		ref = pos;
	}
	public synchronized double getRef() 
	{
		return ref;
	}

}

