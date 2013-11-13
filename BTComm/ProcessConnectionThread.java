/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bluetoothcomm;

import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.StreamConnection;
import javax.swing.JTextArea;

/**
 *
 * @author Hani
 */
public class ProcessConnectionThread implements Runnable {

    private StreamConnection mConnection;
    // Constant that indicate command from devices
    private static final int EXIT_CMD = -1;
    private boolean listen = true;
    
    private BluetoothCommView btview;

    public ProcessConnectionThread(StreamConnection connection, BluetoothCommView btview) {
        mConnection = connection;
        this.btview = btview;
    }
    
    @Override
    public void run() {
        try {
            // prepare to receive data
            InputStream inputStream = mConnection.openInputStream();
            btview.printToConsole("waiting for input");
            while (listen) {
                int command = inputStream.read();
                if (command == EXIT_CMD) {
                    btview.printToConsole("finish process");
                    break;
                }
                processCommand(command);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String data = "";

    /**
     * Process the command from client
     * @param command the command code
     */
    private void processCommand(int command) {
        if ((char) command == ' ') {
            String sp[] = data.split(",", -1);
            String key = sp[0];
            if (key.equals("a")) {
                btview.setSlider(Integer.valueOf(sp[1]));
            } else if (key.equals("plotter")) {
                btview.selectSquare(Boolean.valueOf(sp[1]));
                btview.setAmplitude(sp[2]);
                btview.setPeriod(sp[3]);
                btview.selectManual(Boolean.valueOf(sp[4]));
                btview.setSlider(Integer.valueOf(sp[5]));
            } else if (key.equals("PI")) {
                btview.setPiData(sp[0]);
                btview.setPiK(sp[1]);
                btview.setPiTi(sp[2]);
                btview.setPiTr(sp[3]);
                btview.setPiBeta(sp[4]);
                btview.setPiH(sp[5]);
            } else if (key.equals("PID")) {
                btview.setPidData(sp[0]);
                btview.setPidK(sp[1]);
                btview.setPidTi(sp[2]);
                btview.setPidTr(sp[3]);
                btview.setPidTd(sp[4]);
                btview.setPidN(sp[5]);
                btview.setPidBeta(sp[6]);
                btview.setPidH(sp[7]);
            }
            data = "";
        } else {
            data += (char) command;
        }
    }
}
