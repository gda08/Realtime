
import java.io.IOException;
import java.io.OutputStream;

import javax.microedition.io.StreamConnection;

import se.lth.control.realtime.AnalogIn;
import se.lth.control.realtime.IOChannelException;

public class WriteCommServer extends Thread {

	private StreamConnection mConnection;
	private OutputStream outputStream;

	private AnalogIn analogInPos;
	private AnalogIn analogInAng;

	public WriteCommServer(StreamConnection connection) {
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
			s = analogInPos.get() + "," + analogInAng.get();
			System.out.println(s);
			outputStream.write(s.getBytes());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

}
