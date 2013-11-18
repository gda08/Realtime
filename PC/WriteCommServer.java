
import java.io.IOException;
import java.io.OutputStream;

import javax.microedition.io.StreamConnection;

import se.lth.control.realtime.AnalogIn;
import se.lth.control.realtime.IOChannelException;

import SimEnvironment.AnalogSource;

public class WriteCommServer extends Thread {

	private StreamConnection mConnection;
	private OutputStream outputStream;

	private AnalogIn analogInPos;
	private AnalogIn analogInAng;

	private boolean doRun = true;

	public WriteCommServer(StreamConnection connection, BeamAndBall beam) {
		mConnection = connection;
		try {
			analogInPos = new AnalogIn(1);
			analogInAng = new AnalogIn(0);
		} catch (IOChannelException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			outputStream = mConnection.openOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (true) {
			synchronized (this) {
				if (outputStream != null) {
					sendPosAndAng();
//					sendAng();
				}
			}
			try {
				sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private synchronized void  sendPosAndAng() {
		String s;
		try {
			String tempPos = "" + analogInPos.get();
			String tempAng = "" + analogInAng.get();
//			if(tempPos.contains("E")){
//				tempPos = "0";
//			}
//			if(tempAng.contains("E")){
//				tempPos = "0";
//			}
			s = tempPos;
			s += "," + tempAng;
			System.out.println(s);
			outputStream.write(s.getBytes());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private void sendAng() {
		String s;
		try {
			s = "ANG," + analogInAng.get();
			System.out.println(s);
			outputStream.write(s.getBytes());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

}
