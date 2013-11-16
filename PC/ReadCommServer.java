import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.StreamConnection;

import SimEnvironment.AnalogSink;

public class ReadCommServer extends Thread{
	
	private StreamConnection mConnection;
	private AnalogSink analogOut;
	private InputStream inputStream;
	private double regulSignal;

	public ReadCommServer(StreamConnection connection, BeamAndBall beam) {
        analogOut = beam.getSink(0);
		mConnection = connection;
		regulSignal = 0;
		try {
			inputStream = mConnection.openInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Send in strings. To set PID send first value 1 To set PI, send first
	 * value 2 To set Amplitude first value 3 To set Period, first value 4
	 */
	public void run() {
		try {
			System.out.println("waiting for input");
			while (true) {
				byte[] signal = getRegulSignal();
				if(signal!=null){
					String s = new String(signal);
					String key = s.split(",", -1)[0];
					String value = s.split(",", -1)[1];
					if (key.equals("CON")) {
						try {
							regulSignal = Double.parseDouble(value);
						} catch (Exception e) {
							regulSignal = 0;
						}
					}
				}
				synchronized(this){
					analogOut.set(regulSignal); //moved to minimize delay
				}
				System.out.println("Control: " + regulSignal);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private byte[] getRegulSignal(){
		byte[] b=new byte[100];
		try {
			inputStream.read(b);
			return b;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
