package com.realtime.project;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.IBinder;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.realtime.project.control.BeamAndBallRegul;
import com.realtime.project.control.PIDParameters;
import com.realtime.project.control.PIParameters;
import com.realtime.project.control.ReferenceGenerator;

public class CommService extends Service {

	// För att läsa mer om vad UUID är för något: http://en.wikipedia.org/wiki/Bluetooth#Technical_informations
	private static final UUID 	MY_UUID = UUID.fromString("04c6093b-0000-1000-8000-00805f9b34fb");
    private static final String NAME = "BT_SERVICE";
    private static final String CONNECT_TO_SERVER = "s3";
    private static final String DISCONNECT_FROM_SERVER = "s4";
    private static final String SEND_TO_SERVER = "s5";    
    private static final String SERVER_STATE_s = "s7";
    private static final String TOAST = "s8";
    private static final String BT_STATE_s = "s9";
    private static final String READ_DATA_s = "s10";
    private static final String CHECK_BT_STATE_s = "s13";    
    private static final String UPDATE_PI_PARAMS = "s26";
    private static final String UPDATE_PID_PARAMS = "27";    
    private static final String MODE_OFF_s = "s28";
    private static final String MODE_BEAM_s = "s29";
    private static final String MODE_BALL_s = "s30";
    private static final int 	SERVER_STATE_DISCONNECTED = 13;
    private static final int 	SERVER_STATE_CONNECTING = 14;
    private static final int 	SERVER_STATE_CONNECTED = 15;
    private static final int 	BT_STATE_ENABLED = 16;
    private static final int 	BT_STATE_DISABLED = 17;    


    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice pairedDevice;
    private List<String> pairedDevices;

    private AcceptThread acceptThread;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;
    private int serverState;
    
    private BeamAndBallRegul regul;
    private PIDParameters pidParams;
    private PIParameters piParams;

    @Override
    public void onCreate() {
        super.onCreate();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        pairedDevices = new ArrayList<String>();
        serverState = SERVER_STATE_DISCONNECTED;
        broadcastBTState();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    // Starts the regul. The regul sends the control signal via the OutputStream
    private synchronized void startRegul(OutputStream outStream){
    	regul = new BeamAndBallRegul(new ReferenceGenerator(0), 8, outStream);
        regul.start();
        
        if (mode.equals(MODE_OFF_s)) regul.setOFFmode();
        else if (mode.equals(MODE_BEAM_s)) regul.setBEAMmode();
        else if (mode.equals(MODE_BALL_s)) regul.setBALLmode();
        
        pidParams = regul.getOuterParameters();
        piParams = regul.getInnerParameters();
    }
    
    // Sends the position and the angle to regul
    private synchronized void sendDataToRegul(String data, int bytes) {
    	Intent i = new Intent(READ_DATA_s);
        i.putExtra(READ_DATA_s, bytes);
        sendBroadcast(i);
        //toastPrint(data);
        String key = data.split(",", -1)[0];
        String value = data.split(",", -1)[1];
        if (key.equals("POS")) {
        	regul.setPosition(Double.valueOf(value));
        } else if (key.equals("ANG")) {
        	regul.setAngle(Double.valueOf(value));
        }
    }

    private synchronized void readPairedDevices() {
        if (bluetoothAdapter == null) {
            toastPrint("BT not supported");
            return;
        }
        Set<BluetoothDevice> mPairedDevices = bluetoothAdapter.getBondedDevices();
        if (mPairedDevices.size() > 0) {
            for (BluetoothDevice device : mPairedDevices) {
                if (!pairedDevices.contains(device.getName())) {
                    pairedDevices.add(device.getName());
                    pairedDevice = device;
                }
            }
            toastPrint("Paired with: " + pairedDevice.getName());
        } else {
            toastPrint("No paired devices found");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (action.equals(SEND_TO_SERVER)) {
            if (!bluetoothAdapter.isEnabled()) {
                toastPrint("Cannot connect. BT is disabled");
                return -1;
            }
            String data = intent.getStringExtra(SEND_TO_SERVER);
            byte[] send = data.getBytes();
            write(send);
        } else  if (action.equals(CONNECT_TO_SERVER)) {
            if (!bluetoothAdapter.isEnabled()) {
                toastPrint("Cannot connect. BT is disabled");
                return -1;
            }
            readPairedDevices();
            connect(pairedDevice);
        } else  if (action.equals(DISCONNECT_FROM_SERVER)) {
            stop();
        } else if (action.equals(CHECK_BT_STATE_s)) {
            broadcastBTState();
        } else if (action.equals(UPDATE_PI_PARAMS)) {
        	String allParams = intent.getStringExtra(UPDATE_PI_PARAMS);
        	String[] params = allParams.split(",", -1);
        	piParams.K = Double.valueOf(params[0]);
        	piParams.Ti = Double.valueOf(params[1]);
        	piParams.Tr = Double.valueOf(params[2]);
        	piParams.Beta = Double.valueOf(params[3]);
        	piParams.H = Double.valueOf(params[4]);
        	piParams.integratorOn = Boolean.valueOf(params[5]);
        	regul.setInnerParameters(piParams);
        } else if (action.equals(UPDATE_PID_PARAMS)) {
        	String allParams = intent.getStringExtra(UPDATE_PID_PARAMS);
        	String[] params = allParams.split(",", -1);
        	pidParams.K = Double.valueOf(params[0]);
        	pidParams.Ti = Double.valueOf(params[1]);
        	pidParams.Td = Double.valueOf(params[2]);
        	pidParams.Tr = Double.valueOf(params[3]);
        	pidParams.N = Double.valueOf(params[4]);
        	pidParams.Beta = Double.valueOf(params[5]);
        	pidParams.H = Double.valueOf(params[6]);
        	pidParams.integratorOn = Boolean.valueOf(params[7]);
        	regul.setOuterParameters(pidParams);
        } else if (action.equals(MODE_OFF_s)) {
        	mode = MODE_OFF_s;
        	if (regul!=null) regul.setOFFmode();
        } else if (action.equals(MODE_BALL_s)) {
        	mode = MODE_BALL_s;
        	if (regul!=null) regul.setBALLmode();
        } else if (action.equals(MODE_BEAM_s)) {
        	mode = MODE_BALL_s;
        	if (regul!=null) regul.setBEAMmode();
        }
        return Service.START_NOT_STICKY;
    }
    
    private String mode = MODE_OFF_s;

    private synchronized void setState(int state) {
        serverState = state;
        Intent i = new Intent(SERVER_STATE_s);
        i.putExtra(SERVER_STATE_s, state);
        sendBroadcast(i);

    }

    private synchronized void start() {
        stop();
        if (acceptThread == null) {
            acceptThread = new AcceptThread();
            acceptThread.start();
        }
    }

    private synchronized void connect(BluetoothDevice device) {
        if (serverState == SERVER_STATE_CONNECTING) {
            if (connectThread != null) {
                connectThread.cancel();
                connectThread = null;
            }
        }
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }
        connectThread = new ConnectThread(device);
        connectThread.start();
        setState(SERVER_STATE_CONNECTING);
    }

    private synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        stop();
        connectedThread = new ConnectedThread(socket);
        connectedThread.start();
        setState(SERVER_STATE_CONNECTED);
    }

    /**
     * Stop all threads
     */
    private synchronized void stop() {
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }
        if (acceptThread != null) {
            acceptThread.cancel();
            acceptThread = null;
        }
        setState(SERVER_STATE_DISCONNECTED);
        if (regul != null) regul.stopRun();
    }

    private void write(byte[] out) {
        ConnectedThread r;
        synchronized (this) {
            if (serverState != SERVER_STATE_CONNECTED) {
                toastPrint("Cannot send. Server disconnected");
                return;
            }
            r = connectedThread;
        }
        r.write(out);
    }

    private void toastPrint(String message) {
        Intent i = new Intent(TOAST);
        i.putExtra(TOAST, message);
        sendBroadcast(i);
    }

    private void connectionFailed() {
        toastPrint("Unable to connect server");
        CommService.this.start();
    }

    private void connectionLost() {
        toastPrint("Server connection was lost");
        stop();
        CommService.this.start();
    }

    private void broadcastBTState() {
        Intent i = new Intent(BT_STATE_s);
        if (bluetoothAdapter.isEnabled()) {
            i.putExtra(BT_STATE_s, BT_STATE_ENABLED);
        } else {
            i.putExtra(BT_STATE_s, BT_STATE_DISABLED);
        }
        sendBroadcast(i);
    }

    /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
    private class AcceptThread extends Thread {

        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            try {
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (IOException e) {
            }
            mmServerSocket = tmp;
        }

        public void run() {
            setName("AcceptThread");
            BluetoothSocket socket = null;
            while (serverState != SERVER_STATE_CONNECTED) {
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    break;
                }
                if (socket != null) {
                    synchronized (CommService.this) {
                        switch (serverState) {
                            case SERVER_STATE_DISCONNECTED:
                            case SERVER_STATE_CONNECTING:
                                connected(socket, socket.getRemoteDevice());
                                break;
                            case SERVER_STATE_CONNECTED:
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                }
                                break;
                        }
                    }
                }
            }
        }

        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
            }
        }
    }


    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {
    	
        private BluetoothSocket mmSocket;
        private BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
            }
            mmSocket = tmp;
        }

        public void run() {
            bluetoothAdapter.cancelDiscovery();
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
            } catch (IOException e) {
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                }
                connectionFailed();
                return;
            }
            synchronized (CommService.this) {
                connectThread = null;
            }
            connected(mmSocket, mmDevice);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
    	
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
            startRegul(mmOutStream);
            
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    String data = new String(buffer);
                    sendDataToRegul(data, bytes);
                } catch (IOException e) {
                    connectionLost();
                    CommService.this.start();
                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         * @param buffer  The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);
            } catch (IOException e) {
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }




}
