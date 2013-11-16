import java.io.IOException;
import java.io.OutputStream;

import javax.microedition.io.StreamConnection;

import SimEnvironment.AnalogSource;


public class WriteCommServer extends Thread {
	
	private StreamConnection mConnection;
	private OutputStream outputStream;
	
	private AnalogSource analogInPos;
    	private AnalogSource analogInAng;
    
	private boolean doRun = true;

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
		long t = System.currentTimeMillis();
		while (true) {
			synchronized(this){ //syncronizes sendPos and sendAng. Sync is handeled here to prevent unnecessary delay from seperate sync.
				sendPos();
				sendAng();
			}
			long duration;
                        t = t + 3000;
                        duration = t - System.currentTimeMillis();
                        if(duration>0){
				try {
					sleep(duration);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
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
