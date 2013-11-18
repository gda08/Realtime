
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.StreamConnection;

import se.lth.control.realtime.AnalogOut;
import se.lth.control.realtime.IOChannelException;


public class ReadCommServer extends Thread{
	
	private StreamConnection mConnection;
	private AnalogOut analogOut;
    private InputStream inputStream;
    private double regulSignal;

	public ReadCommServer(StreamConnection connection) {
        try {
			analogOut = new AnalogOut(0);
		} catch (IOChannelException e1) {
			e1.printStackTrace();
		}
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
					if (key.equals("CONTROL_SIGNAL")) {
						try {
							regulSignal = Double.parseDouble(value);
						} catch (Exception e) {
							regulSignal = 0;
						}
					}
				}
				System.out.println("CONTROL_SIGNAL: " + regulSignal);
				analogOut.set(regulSignal);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private byte[] getRegulSignal(){
		byte[] b=new byte[500];
		try {
			inputStream.read(b);
			return b;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
