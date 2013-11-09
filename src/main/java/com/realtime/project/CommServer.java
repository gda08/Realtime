package com.realtime.project;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.InputStream;
import java.util.ArrayList;

import javax.microedition.io.StreamConnection;

public class CommServer {
	private StreamConnection mConnection;
	private static final int EXIT_CMD = -1;
	private BeamAndBallRegul regul;
	// private ArrayList<DBContainer> PIDParameters = new
	// ArrayList<DBContainer>();
	// private ArrayList<DBContainer> PIParameters = new
	// ArrayList<DBContainer>();
	PIDParameters pidParameters;
	PIParameters piParameters;

	public CommServer(StreamConnection connection, BeamAndBallRegul regul) {
		mConnection = connection;
		this.regul = regul;
		pidParameters = new PIDParameters();
		piParameters = new PIParameters();
	}

	/*
	 * Send in strings. To set PID send first value 1 To set PI, send first
	 * value 2 To set Amplitude first value 3 To set Period, first value 4
	 */
	public void run() {

		try {
			// prepare to receive data
			InputStream inputStream = mConnection.openInputStream();
			System.out.println("waiting for input");
			while (true) {
				String data = "";
				int command = inputStream.read();
				while ((char) command != '*') {
					data += (char) command;
				}
				String sp[] = data.split(",");
				for (int i = 0; i < sp.length - 1; i++) {
					String spTemp[] = sp[i + 1].split(" ");
					if (sp[0].compareTo("PID") == 0) {
						updatePID(spTemp);
					} else if (sp[0].compareTo("PI") == 0) {
						updatePI(spTemp);
					}
				}
				// processCommand(command);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updatePID(String[] spTemp) {
		int i = 0;
		while (i < spTemp.length) {
			switch (spTemp[i]) {
			case "K":
				pidParameters.K = Integer.parseInt(spTemp[i + 1]);
				break;
			case "Ti":
				pidParameters.Ti = Integer.parseInt(spTemp[i + 1]);
				break;
			case "Tr":
				pidParameters.Tr = Integer.parseInt(spTemp[i + 1]);
				break;
			case "Td":
				pidParameters.Td = Integer.parseInt(spTemp[i + 1]);
				break;
			case "N":
				pidParameters.N = Integer.parseInt(spTemp[i + 1]);
				break;
			case "Beta":
				pidParameters.Beta = Integer.parseInt(spTemp[i + 1]);
				break;
			case "H":
				pidParameters.H = Integer.parseInt(spTemp[i + 1]);
				break;
			}
			i = i + 2;
		}
	}

	private void updatePI(String[] spTemp) {
		int i = 0;
		while (i < spTemp.length) {
			switch (spTemp[i]) {
			case "K":
				piParameters.K = Integer.parseInt(spTemp[i + 1]);
				break;
			case "Ti":
				piParameters.Ti = Integer.parseInt(spTemp[i + 1]);
				break;
			case "Tr":
				piParameters.Tr = Integer.parseInt(spTemp[i + 1]);
				break;
			case "Beta":
				piParameters.Beta = Integer.parseInt(spTemp[i + 1]);
				break;
			case "H":
				piParameters.H = Integer.parseInt(spTemp[i + 1]);
				break;
			}
			i = i + 2;
		}
	}
}
