package com.realtime.project.control;

//PID class to be written by you
public class PID {

	// Private attribute containing a reference to the PIDParameters
	// currently used
	private PIDParameters p;
	private double D;
	// Additional attributes
	private double e;
	private double ad;
	private double bd;
	private double y;
	private double yOld;
	private double I;
	private double v;

	// Constructor
	public PID(String name) {
		p = new PIDParameters();
		p.Beta = 1.0;
		p.H = 0.1;
		p.integratorOn = false;
		p.K = -0.1;
		p.Ti = 0.0;
		p.Tr = 10.0;
		this.e = 0;
		this.I = 0;
		p.N = 7;
		p.Td = 1.7;
		this.v = 0;
		//		new PIDGUI(this, p, name);
		setParameters(p);

	}

	// Calculates the control signal v. Called from BeamAndBallRegul.
	public synchronized double calculateOutput(double newY, double yref) {
		y=newY;
		e = yref - newY;
		D = ad * D - bd * (newY - yOld);
		v = p.K * (p.Beta * yref - newY) + I + D;
		return v;
	}

	// Updates the controller state. Should use tracking-based anti-windup
	// Called from BeamAndBallRegul.
	public synchronized void updateState(double u) {
		if(p.integratorOn)
			I = I + (p.K * p.H / p.Ti) * e + (p.H / p.Tr) * (u - v);
		yOld = y;
	}

	// Returns the sampling interval expressed as a long. Explicit type casting
	// needed
	public synchronized long getHMillis() {
		return (long) (p.H * 1000.0);
	}

	// Sets the PIDParameters. Called from PIDGUI. Must clone newParameters.
	public synchronized void setParameters(PIDParameters newParameters) {
		p = (PIDParameters) newParameters.clone();
		if (!p.integratorOn) {
			I = 0.0;
		}
		ad = p.Td / (p.Td + p.N * p.H);
		bd = p.K * ad * p.N;
	}

	// Sets the I-part of the controller to 0.
	// For example needed when changing controller mode.
	public synchronized void reset() {
		I = 0;
	}

	// Returns the current PIDParameters.
	public synchronized PIDParameters getParameters() {
		return (PIDParameters)p.clone();
	}

}
