package com.realtime.project.control;

import java.io.IOException;
import java.io.OutputStream;
import android.util.Log;

public class BeamAndBallRegul extends Thread {

	private double positionIn, angleIn;
	
	private OutputStream outStream;
	
	private PID outer;
	private PI inner;
	private ReferenceGenerator refGen;
	private double uMin = -10.0;
	private double uMax = 10.0;
	
	public BeamAndBallRegul(ReferenceGenerator ref, int pri, OutputStream outStream) {
		// 	analogInPos = beam.getSource(0);
		// 	analogInAng = beam.getSource(1);
	// 		analogOut = beam.getSink(0);
	// 		analogRef = beam.getSink(1);
		this.refGen = ref;
		this.outStream = outStream;
		outer = new PID("PID");
		inner= new PI("PI");
		setPriority(pri);
	}

	private double limit(double u, double umin, double umax) {
		if (u < umin) {
			u = umin;
		} else if (u > umax) {
			u = umax;
		}
		return u;
	}
	
	public synchronized void setPosition(double position) {
		this.positionIn = position;
	}
	
	public synchronized void setAngle(double angle) {
		this.angleIn = angle;
	}
	
	public synchronized void setInnerParameters(PIParameters p) {
		inner.setParameters((PIParameters) p.clone());
	}

	public synchronized PIParameters getInnerParameters() {
		return inner.getParameters();
	}

	public synchronized void setOuterParameters(PIDParameters p) {
		outer.setParameters((PIDParameters)p.clone());
		Log.d("REGUL", "PID: " + p.K + " " + p.Ti + " " + p.integratorOn);
	}

	public synchronized PIDParameters getOuterParameters(){
		return outer.getParameters();
	}
	
	public synchronized void stopRun() {
		doRun = false;
	}
	
	private boolean doRun = true;
	
	public void run() {
		long t = System.currentTimeMillis();
		while (doRun) {			
			double ref = refGen.getRef();
			double u;
			synchronized (outer) {
				u = limit(outer.calculateOutput(positionIn, ref), uMin, uMax);
				outer.updateState(u);
			}
			double u2 = 100;
			synchronized (inner) {
				u2 = limit(inner.calculateOutput(angleIn, u), uMin, uMax);
				//analogOut.set(u2);
				// SEND TO COMOUTER u2
				String controlSignal = "CON," + u2;
				byte[] buffer = controlSignal.getBytes();
				try {
					outStream.write(buffer);
				} catch (IOException e) {
					e.printStackTrace();
				}
				inner.updateState(u2);
			}
			//analogRef.set(ref);
			long duration;
			t = t + inner.getHMillis();
			
			//update plot
			String temp = "" + u2;
			updatePlot(ref, positionIn);
			duration = t - System.currentTimeMillis();
			if (duration > 0) {
				try {
					sleep(duration);
				} catch (InterruptedException x) {
				}
			}
		}
	}
	public String updatePlot(double ref, double position){
		String out = Double.toString(ref);
		out += ",";
		out += Double.toString(position);
		out += "*";
		return out;
	}
}
