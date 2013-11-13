/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bluetoothcomm;

import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import javax.swing.JTextArea;

/**
 *
 * @author Hani
 */
public class WaitThread implements Runnable {
    
    private BluetoothCommView btview;
    
    public WaitThread(BluetoothCommView btview) {
        this.btview = btview;
    }

    @Override
    public void run() {
        waitForConnection();
    }

    private void waitForConnection() {
        // retrieve the local Bluetooth device object
        LocalDevice local = null;

        StreamConnectionNotifier notifier;
        StreamConnection connection = null;

        // setup the server to listen for connection
        try {
            local = LocalDevice.getLocalDevice();
            local.setDiscoverable(DiscoveryAgent.GIAC);

            UUID uuid = new UUID(80087355); // "04c6093b-0000-1000-8000-00805f9b34fb"
            String url = "btspp://localhost:" + uuid.toString() + ";name=RemoteBluetooth";
            notifier = (StreamConnectionNotifier) Connector.open(url);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        // waiting for connection
        while (true) {
            try {
                //System.out.println("waiting for connection...");
                btview.printToConsole("waiting for connection...");
                connection = notifier.acceptAndOpen();

                ProcessConnectionThread pct = new ProcessConnectionThread(connection, btview);
                btview.setStreamConn(connection);
                Thread processThread = new Thread(pct);
                processThread.start();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
