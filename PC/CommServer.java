package PC;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.microedition.io.StreamConnection;

import SimEnvironment.AnalogSink;
import SimEnvironment.AnalogSource;

public class CommServer extends Thread{
	private StreamConnection mConnection;
	private static final int EXIT_CMD = -1;
//	private ReferenceGenerator ref;
	private AnalogSource analogInPos;
    private AnalogSource analogInAng;
    private AnalogSink analogOut;
    private AnalogSink analogRef;
    private InputStream inputStream;
    private OutputStream outputStream;
    private double regulSignal;
    // private ArrayList<DBContainer> PIDParameters = new
	// ArrayList<DBContainer>();
	// private ArrayList<DBContainer> PIParameters = new
	// ArrayList<DBContainer>();
//	PIDParameters pidParameters;
//	PIParameters piParameters;

	public CommServer(StreamConnection connection, BeamAndBall beam) {
		analogInPos = beam.getSource(0);
        analogInAng = beam.getSource(1);
        analogOut = beam.getSink(0);
        analogRef = beam.getSink(1);
		mConnection = connection;
//		pidParameters = new PIDParameters();
//		piParameters = new PIParameters();
		regulSignal = 0;
		try {
			inputStream = mConnection.openInputStream();
			outputStream = mConnection.openOutputStream();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * Send in strings. To set PID send first value 1 To set PI, send first
	 * value 2 To set Amplitude first value 3 To set Period, first value 4
	 */
	public void run() {
		try {
			// prepare to receive data
			
			System.out.println("waiting for input");
			while (true) {
				byte[] signal = getRegulSignal();
				if(signal!=null){
					regulSignal = Double.parseDouble(signal.toString());
				}
				analogOut.set(regulSignal);
				sendPos();
				sendAng();
				System.out.print(regulSignal);
				sleep(10);
				// processCommand(command);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	private void sendPos(){
		String s = "POS,"+analogInPos.get();
    	try {
			outputStream.write(s.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void sendAng(){
		String s = "ANG,"+analogInAng.get();
    	try {
			outputStream.write(s.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private byte[] getRegulSignal(){
		byte[] b=new byte[100];
		try {
			inputStream.read(b);
			return b;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
//	private void updatePID(String[] spTemp) {
//		int i = 0;
//		while (i < spTemp.length) {
//			switch (spTemp[i]) {
//			case "K":
//				pidParameters.K = Integer.parseInt(spTemp[i + 1]);
//				break;
//			case "Ti":
//				pidParameters.Ti = Integer.parseInt(spTemp[i + 1]);
//				break;
//			case "Tr":
//				pidParameters.Tr = Integer.parseInt(spTemp[i + 1]);
//				break;
//			case "Td":
//				pidParameters.Td = Integer.parseInt(spTemp[i + 1]);
//				break;
//			case "N":
//				pidParameters.N = Integer.parseInt(spTemp[i + 1]);
//				break;
//			case "Beta":
//				pidParameters.Beta = Integer.parseInt(spTemp[i + 1]);
//				break;
//			case "H":
//				pidParameters.H = Integer.parseInt(spTemp[i + 1]);
//				break;
//			}
//			i = i + 2;
//		}
//	}

//	private void updatePI(String[] spTemp) {
//		int i = 0;
//		while (i < spTemp.length) {
//			switch (spTemp[i]) {
//			case "K":
//				piParameters.K = Integer.parseInt(spTemp[i + 1]);
//				break;
//			case "Ti":
//				piParameters.Ti = Integer.parseInt(spTemp[i + 1]);
//				break;
//			case "Tr":
//				piParameters.Tr = Integer.parseInt(spTemp[i + 1]);
//				break;
//			case "Beta":
//				piParameters.Beta = Integer.parseInt(spTemp[i + 1]);
//				break;
//			case "H":
//				piParameters.H = Integer.parseInt(spTemp[i + 1]);
//				break;
//			}
//			i = i + 2;
//		}
//	}
}
