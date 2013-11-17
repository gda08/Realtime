package com.realtime.project.control;

import java.io.IOException;
import java.io.OutputStream;

public class BeamAndBallRegul extends Thread {
	
	private static final int MODE_OFF = 0;
    private static final int MODE_BEAM = 1;
    private static final int MODE_BALL = 2;
    
    private static int MAX = 10;
	private static int MIN = -10;

	private double positionIn, angleIn;
	private int priority;

	private OutputStream outStream;

	private int mode;
	private boolean doRun;

	private PID outerPIDcontroller;
	private PI innerPIcontroller;
	private ReferenceGenerator refGen;
	private long starttime;
	//private Semaphore mutex; // used for synchronization at shut-down
	private double y, yref, u;

	public BeamAndBallRegul(ReferenceGenerator ref, int pri, OutputStream outStream) {
		this.refGen = ref;
		this.outStream = outStream;
		outerPIDcontroller = new PID("PID");
		innerPIcontroller= new PI("PI");
		//mutex = new Semaphore(1);
		setPriority(pri);
		priority = pri;
		mode = MODE_OFF;
		doRun = true;
		y = yref = u = 0;
	}

	public synchronized void setPosition(double position) {
		this.positionIn = position;
	}

	public synchronized void setAngle(double angle) {
		this.angleIn = angle;
	}

	public synchronized void setInnerParameters(PIParameters p) {
		innerPIcontroller.setParameters((PIParameters) p.clone());
	}

	public synchronized PIParameters getInnerParameters() {
		return innerPIcontroller.getParameters();
	}

	public synchronized void setOuterParameters(PIDParameters p) {
		outerPIDcontroller.setParameters((PIDParameters)p.clone());
	}

	public synchronized PIDParameters getOuterParameters(){
		return outerPIDcontroller.getParameters();
	}

	public synchronized void setOFFmode() {
		mode = MODE_OFF;
	}

	public synchronized void setBEAMmode() {
		mode = MODE_BEAM;
	}

	public synchronized void setBALLmode() {
		mode = MODE_BALL;
	}

	public synchronized void stopRun() {
		doRun = false;
	}

	private double limit(double u, double umin, double umax) {
		if (u < umin) {
			u = umin;
		} else if (u > umax) {
			u = umax;
		}
		return u;
	}

	public void run() {

		long duration;
		starttime = System.currentTimeMillis();

		setPriority(priority);
		//mutex.take();
		while (doRun) {
			yref = refGen.getRef();
			switch (mode) {
			case MODE_OFF: {
				synchronized (outerPIDcontroller) {
					outerPIDcontroller.reset();
				}
				synchronized (innerPIcontroller) {
					innerPIcontroller.reset();
				}
				u = 0;
				sendToPC(u);
				// TODO: UPDATE PLOTTER USING yref, y and u
				break;
			}
			case MODE_BEAM: {
				y = angleIn;
				synchronized (innerPIcontroller) {
					u = innerPIcontroller.calculateOutput(y, yref);
					u = limit(u, MIN, MAX);
					sendToPC(u);
					innerPIcontroller.updateState(u);
				}
				// TODO: UPDATE PLOTTER USING yref, y and u
				break;
			}
			case MODE_BALL: {
				double out1, out2 = 0;
				synchronized (innerPIcontroller) {
					out1 = innerPIcontroller.calculateOutput(positionIn, yref);
					out1 = limit(out1, MIN, MAX);
					innerPIcontroller.updateState(out1);
				}
				synchronized (outerPIDcontroller) {
					out2 = outerPIDcontroller.calculateOutput(angleIn, out1);
					out2 = limit(out2, MIN, MAX);
					sendToPC(out2);
					outerPIDcontroller.updateState(out2);
				}
				// TODO: UPDATE PLOTTER USING yref, y and u
				break;
			}
			default: {
				System.out.println("Error: Illegal mode.");
				break;
			}
			}
			// sleep
			starttime = starttime + innerPIcontroller.getHMillis();
			duration = starttime - System.currentTimeMillis();
			if (duration > 0) {
				try {
					sleep(duration);
				} catch (InterruptedException x) {
				}
			}
		}
		//mutex.give();
	}

	private void sendToPC(double controlSignal) {
		String controlSignalString = "CONTROL_SIGNAL," + controlSignal;
		byte[] buffer = controlSignalString.getBytes();
		try {
			outStream.write(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
