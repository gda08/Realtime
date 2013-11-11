package com.realtime.project.gui;

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

public class CommService extends Service {

    private static final String NAME = "BT_SERVICE";

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice pairedDevice;
    private List<String> pairedDevices;

    private AcceptThread acceptThread;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;
    private int serverState;

    @Override
    public void onCreate() {
        super.onCreate();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        pairedDevices = new ArrayList<String>();
        serverState = Str.SERVER_STATE_DISCONNECTED;
        broadcastBTState();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
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
        if (action.equals(Str.SEND_TO_SERVER)) {
            if (!bluetoothAdapter.isEnabled()) {
                toastPrint("Cannot connect. BT is disabled");
                return -1;
            }
            String data = intent.getStringExtra(Str.SEND_TO_SERVER);
            byte[] send = data.getBytes();
            write(send);
        } else  if (action.equals(Str.CONNECT_TO_SERVER)) {
            if (!bluetoothAdapter.isEnabled()) {
                toastPrint("Cannot connect. BT is disabled");
                return -1;
            }
            readPairedDevices();
            connect(pairedDevice);
        } else  if (action.equals(Str.DISCONNECT_FROM_SERVER)) {
            stop();
        } else if (action.equals(Str.CHECK_BT_STATE_s)) {
            broadcastBTState();
        }
        return Service.START_NOT_STICKY;
    }

    private synchronized void setState(int state) {
        serverState = state;
        Intent i = new Intent(Str.SERVER_STATE_s);
        i.putExtra(Str.SERVER_STATE_s, state);
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
        if (serverState == Str.SERVER_STATE_CONNECTING) {
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
        setState(Str.SERVER_STATE_CONNECTING);
    }

    private synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        stop();
        connectedThread = new ConnectedThread(socket);
        connectedThread.start();
        setState(Str.SERVER_STATE_CONNECTED);
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
        setState(Str.SERVER_STATE_DISCONNECTED);
    }

    private void write(byte[] out) {
        ConnectedThread r;
        synchronized (this) {
            if (serverState != Str.SERVER_STATE_CONNECTED) {
                toastPrint("Cannot send. Server disconnected");
                return;
            }
            r = connectedThread;
        }
        r.write(out);
    }

    private void toastPrint(String message) {
        Intent i = new Intent(Str.TOAST);
        i.putExtra(Str.TOAST, message);
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
        Intent i = new Intent(Str.BT_STATE_s);
        if (bluetoothAdapter.isEnabled()) {
            i.putExtra(Str.BT_STATE_s, Str.BT_STATE_ENABLED);
        } else {
            i.putExtra(Str.BT_STATE_s, Str.BT_STATE_DISABLED);
        }
        sendBroadcast(i);
    }

    /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
    private class AcceptThread extends Thread {
        // The local server socket
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;

            // Create a new listening server socket
            try {
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, Str.MY_UUID);
            } catch (IOException e) {
            }
            mmServerSocket = tmp;
        }

        public void run() {
            setName("AcceptThread");
            BluetoothSocket socket = null;
            // Listen to the server socket if we're not connected
            while (serverState != Str.SERVER_STATE_CONNECTED) {
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    break;
                }

                // If a connection was accepted
                if (socket != null) {
                    synchronized (CommService.this) {
                        switch (serverState) {
                            case Str.SERVER_STATE_DISCONNECTED:
                            case Str.SERVER_STATE_CONNECTING:
                                connected(socket, socket.getRemoteDevice());
                                break;
                            case Str.SERVER_STATE_CONNECTED:
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
                tmp = device.createRfcommSocketToServiceRecord(Str.MY_UUID);
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

            // Reset the ConnectThread because we're done
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

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    String data = new String(buffer);
                    Intent i = new Intent(Str.READ_DATA_s);
                    i.putExtra(Str.READ_DATA_s, bytes);
                    sendBroadcast(i);
                    toastPrint(data);
                } catch (IOException e) {
                    connectionLost();
                    // Start the service over to restart listening mode
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
