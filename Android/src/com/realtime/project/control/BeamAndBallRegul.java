package com.realtime.project.control;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

import android.os.Environment;

import com.realtime.project.CommService;

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
	private long starttime;
	//private Semaphore mutex; // used for synchronization at shut-down
	private double y, yref, u;

	private CommService commService;
	
	public BeamAndBallRegul(int pri, OutputStream outStream, CommService commService) {
		this.outStream = outStream;
		this.commService = commService;
		outerPIDcontroller = new PID("PID");
		innerPIcontroller= new PI("PI");
		//mutex = new Semaphore(1);
		setPriority(pri);
		priority = pri;
		mode = MODE_OFF;
		doRun = true;
		y = yref = u = 0;
		initWriters();
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

	public synchronized void setRef(double ref) {
		yref = ref;
		try {
			y_out.append("----------------\n");
			yref_out.append("----------------\n");
			u_out.append("----------------\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
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
			//yref = refGen.getRef();
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
				commService.updatePlotter(0, 0, 0);
				if (closeWriters)
					closeWriters();
				break;
			}
			case MODE_BEAM: {
				y = angleIn;
				yref = 0;
				synchronized (innerPIcontroller) {
					u = innerPIcontroller.calculateOutput(y, yref);
					u = limit(u, MIN, MAX);
					sendToPC(u);
					innerPIcontroller.updateState(u);
				}
				commService.updatePlotter(u, y, yref);
				break;
			}
			case MODE_BALL: {
				closeWriters = true;
				double out1, out2 = 0;
				y = positionIn;
				synchronized (outerPIDcontroller) {
					out1 = outerPIDcontroller.calculateOutput(y, yref);
					out1 = limit(out1, MIN, MAX);
					outerPIDcontroller.updateState(out1);
				}
				synchronized (innerPIcontroller) {
					out2 = innerPIcontroller.calculateOutput(angleIn, out1);
					out2 = limit(out2, MIN, MAX);
					sendToPC(out2);
					innerPIcontroller.updateState(out2);
				}
				commService.updatePlotter(out2, y, yref);
				write_to_file(y, yref, out2);
				break;
			}
			default: {
				System.out.println("Error: Illegal mode.");
				break;
			}
			}
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
	
	private static final String APP_FILES =
    		Environment.getExternalStorageDirectory().getPath() + "/Android/data/bb";
    
    private static final String Y_FILE = APP_FILES + "/y.txt";
    private static final String YREF_FILE = APP_FILES + "/yref.txt";
    private static final String U_FILE = APP_FILES + "/u.txt";
    
    private boolean closeWriters = false;
    
    private BufferedWriter y_out, yref_out, u_out;
    
    private void write_to_file(double y, double yref, double u) {
    	try {
			y_out.append(String.valueOf(y) + "\n");
			yref_out.append(String.valueOf(yref) + "\n");
			u_out.append(String.valueOf(u) + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    private void initWriters() {
    	try {
			FileWriter y_stream = new FileWriter(Y_FILE);
			FileWriter yref_stream = new FileWriter(YREF_FILE);
			FileWriter u_stream = new FileWriter(U_FILE);
			y_out = new BufferedWriter(y_stream);
			yref_out = new BufferedWriter(yref_stream);
			u_out = new BufferedWriter(u_stream);
			y_out.append("----------------\n");
			yref_out.append("----------------\n");
			u_out.append("----------------\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    private void closeWriters() {
    	try {
			y_out.close();
			yref_out.close();
			u_out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
