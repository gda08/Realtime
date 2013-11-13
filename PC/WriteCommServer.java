import java.io.IOException;
import java.io.OutputStream;

import javax.microedition.io.StreamConnection;

import SimEnvironment.AnalogSource;


public class WriteCommServer extends Thread {
	
	private StreamConnection mConnection;
	private OutputStream outputStream;
	
	private AnalogSource analogInPos;
    private AnalogSource analogInAng;

	public WriteCommServer(StreamConnection connection, BeamAndBall beam) {
		mConnection = connection;
		analogInPos = beam.getSource(0);
        analogInAng = beam.getSource(1);
		try {
			outputStream = mConnection.openOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		while (true) {
			sendPos();
			sendAng();
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void sendPos(){
		String s = "POS,"+analogInPos.get();
    	try {
			outputStream.write(s.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void sendAng(){
		String s = "ANG,"+analogInAng.get();
    	try {
			outputStream.write(s.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
