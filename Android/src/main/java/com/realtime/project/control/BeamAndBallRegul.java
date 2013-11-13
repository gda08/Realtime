package com.realtime.project.control;

import java.io.IOException;

import com.realtime.project.CommService;

import SimEnvironment.AnalogSink;
import SimEnvironment.AnalogSource;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;

public class BeamAndBallRegul extends Thread {

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
	
	public synchronized void setPosition(double position) {
		this.position = position;
	}
	
	public synchronized void setAngle(double angle) {
		this.angle = angle;
	}

	private double position, angle;
	
	public void run() {
		long t = System.currentTimeMillis();
		while (true) {			
			double ref = refGen.getRef();
			double u;
			synchronized (controller) {
				u = limit(controller.calculateOutput(position, ref), uMin, uMax);
				controller.updateState(u);
			}
			double u2;
			synchronized (controller2) {
				u2 = limit(controller2.calculateOutput(angle, u), uMin, uMax);
				//analogOut.set(u2);
				// SEND TO COMOUTER u2
				controller2.updateState(u2);
			}
			//analogRef.set(ref);
			long duration;
			t = t + controller2.getHMillis();
			
			//update plot
			String temp = "" + u2;
			updatePlot(ref, position);
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
