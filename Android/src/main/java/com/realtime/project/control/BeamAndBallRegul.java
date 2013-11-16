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
		this.refGen = ref;
		this.outStream = outStream;
		outer = new PID("PID");
		inner= new PI("PI");
		setPriority(pri);
	}
	//synchronization is handled in the run method. 
	//This method shall not be synchronized, otherwise 
	//the inner and the outer controller will interfere with eachother.
	private double limit(double u, double umin, double umax) { 
		if (u < umin) {
			u = umin;
		} else if (u > umax) {
			u = umax;
		}
		return u;
	}
	//not used in the run method, Synchronized for external usage.
	public synchronized void setPosition(double position) {
		this.positionIn = position;
	}
	//not used in the run method, Synchronized for external usage.
	public synchronized void setAngle(double angle) {
		this.angleIn = angle;
	}
	//not used in the run method, Synchronized for external usage.
	public synchronized void setInnerParameters(PIParameters p) {
		inner.setParameters((PIParameters) p.clone());
	}
	//not used in the run method, Synchronized for external usage.
	public synchronized PIParameters getInnerParameters() {
		return inner.getParameters();
	}
	//not used in the run method, Synchronized for external usage.
	public synchronized void setOuterParameters(PIDParameters p) {
		outer.setParameters((PIDParameters)p.clone());
		Log.d("REGUL", "PID: " + p.K + " " + p.Ti + " " + p.integratorOn);
	}
	//not used in the run method, Synchronized for external usage.
	public synchronized PIDParameters getOuterParameters(){
		return outer.getParameters();
	}
	//not used in the run method, Synchronized for external usage.
	public synchronized void stopRun() { 
		doRun = false;
	}
	
	private boolean doRun = true;
	
	public void run() {
		long t = System.currentTimeMillis();
		while (doRun) {			
			double ref = refGen.getRef();
			double u;
			synchronized (outer) { //limit and update state synchronized to outer controller
				u = limit(outer.calculateOutput(positionIn, ref), uMin, uMax);
				outer.updateState(u);
			}
			double u2 = 100;
			synchronized (inner) { //limit, update state and outSteam synchronized to inner controller
				u2 = limit(inner.calculateOutput(angleIn, u), uMin, uMax);
				String controlSignal = "CON," + u2;
				byte[] buffer = controlSignal.getBytes();
				try {
					outStream.write(buffer);
				} catch (IOException e) {
					e.printStackTrace();
				}
				inner.updateState(u2);
			}
			long duration;
			t = t + inner.getHMillis();
			//update plot
			String temp = "" + u2;
			updatePlot(ref, positionIn);
			duration = t - System.currentTimeMillis(); //duration calculated here to minimize calculation errors due to delay
			if (duration > 0) {
				try {
					sleep(duration);
				} catch (InterruptedException x) {
				}
			}
		}
	}
	public String updatePlot(double ref, double position){ //shall not be synchronized, because it will interfere with the regulators.
		String out = Double.toString(ref);
		out += ",";
		out += Double.toString(position);
		out += "*";
		return out;
	}
}
