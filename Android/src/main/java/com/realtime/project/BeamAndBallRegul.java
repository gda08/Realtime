package com.realtime.project;

import java.io.IOException;

import SimEnvironment.AnalogSink;
import SimEnvironment.AnalogSource;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

public class BeamAndBallRegul extends Thread {
	// Constructor
	// IO interface declarations
	// 	private AnalogSource analogInPos;
	// 	private AnalogSource analogInAng;
	// 	private AnalogSink analogOut;
	// 	private AnalogSink analogRef;
	private final BluetoothSocket socket;
	private final OutputStream outStream;
	private PID controller;
	private PI controller2;
	private ReferenceGenerator refGen;
	private double uMin = -10.0;
	private double uMax = 10.0;
	private CommService com;
	public BeamAndBallRegul(ReferenceGenerator ref, BeamAndBall beam, int pri, BluetoothSocket socket) {
		// 	analogInPos = beam.getSource(0);
		// 	analogInAng = beam.getSource(1);
	// 		analogOut = beam.getSink(0);
	// 		analogRef = beam.getSink(1);
		this.refGen = ref;
		controller = new PID("PID");
		controller2= new PI("PI");
		setPriority(pri);
		com = new CommService();
		this.socket = socket
		outStream = null;
		  try {
			  outStream = socket.getOutputStream();
          } catch (IOException e) {
          }
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
		long t = System.currentTimeMillis();
		double y = 0;
		double y1 = 0;
		while (true) {
			byte[] b=new byte[100];
			String s = inputStream.read(b).toString();
			String sp[] = data.split(",");
			
			if(sp[0].equals("POS")){
				y = Double.parseDouble(sp[1]);
			}
			if(sp[0].equals("ANG")){
				y1 = Double.parseDouble(sp[1]);
			}
			byte[] b=new byte[100];
			s = inputStream.read(b).toString();
			sp[] = data.split(",");
			if(sp[0].equals("POS")){
				y = Double.parseDouble(sp[1]);
			}
			if(sp[0].equals("ANG")){
				y1 = Double.parseDouble(sp[1]);
			}
			
			double ref = refGen.getRef();
			double u;
			synchronized (controller) {
				u = limit(controller.calculateOutput(y, ref), uMin, uMax);
				controller.updateState(u);

			}
			double u2;
			synchronized (controller2) {
				u2 = limit(controller2.calculateOutput(y1, u), uMin, uMax);
				analogOut.set(u2);
				controller2.updateState(u2);
			}
			analogRef.set(ref);
			long duration;
			t = t + controller2.getHMillis();
			
			//update plot
			String temp = "" + u2;
			com.write(temp.getBytes());
			updatePlot(ref, y);
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
