package com.realtime.project;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public abstract class AbstractActivity extends Activity {

    private TextView btState, serverState;
    private BroadcastReceiver myReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sendToService(Str.CHECK_BT_STATE_s);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(myReceiver);
        } catch (Exception e){

        }
    }

    protected void sendToService(String action) {
        Intent i = new Intent(this, CommService.class);
        i.setAction(action);
        this.startService(i);
    }

    protected void sendToService(String action, int extra) {
        Intent i = new Intent(this, CommService.class);
        i.setAction(action);
        i.putExtra(action, extra);
        this.startService(i);
    }

    protected void sendToService(String action, String extra) {
        Intent i = new Intent(this, CommService.class);
        i.setAction(action);
        i.putExtra(action, extra);
        this.startService(i);
    }

    protected void setBtStateTextView(TextView btState) {
        this.btState = btState;
    }

    protected  void setServerTextView(TextView serverState) {
        this.serverState = serverState;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.enable_bt:
                enableBT();
                return true;
            case R.id.disable_bt:
                disableBT();
                return true;
            case R.id.set_discoverable:
                sendToService(Str.MAKE_DISCOVERABLE_s);
                return true;
            case R.id.start_scan:
                sendToService(Str.SCAN_DEVICES_s);
                return true;
            case R.id.connect_to_server:
                sendToService(Str.CONNECT_TO_SERVER);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void registerReceiver() {
        myReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, final Intent intent) {
                handleIntent(intent);
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Str.SERVER_STATE_s);
        filter.addAction(Str.BT_STATE_s);
        registerReceiver(myReceiver, filter);
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (action.equals(Str.SERVER_STATE_s)) {
            updateServerState(intent.getIntExtra(Str.SERVER_STATE_s, 0));
        } else if (action.equals(Str.BT_STATE_s)) {
            updateBTState(intent.getIntExtra(Str.BT_STATE_s, 0));
        }
    }

    private void enableBT() {
        updateBTState(Str.BT_STATE_ENABLED);
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, Str.ENABLE_BT_int);
    }

    private void disableBT() {
        updateBTState(Str.BT_STATE_DISABLED);
        BluetoothAdapter.getDefaultAdapter().disable();
        sendToService(Str.BT_STATE_s, Str.BT_STATE_DISABLED);
    }

    private void updateBTState(int state) {
        switch (state) {
            case Str.BT_STATE_ENABLED:
                btState.setText("Enabled");
                btState.setTextColor(getResources().getColor(R.color.GREEN));
                break;
            case Str.BT_STATE_DISABLED:
                btState.setText("Disabled");
                btState.setTextColor(getResources().getColor(R.color.RED));
                break;
        }
    }

    private void updateServerState(int state) {
        switch (state) {
            case Str.SERVER_STATE_CONNECTED:
                serverState.setText("Connected");
                serverState.setTextColor(getResources().getColor(R.color.GREEN));
                break;
            case Str.SERVER_STATE_CONNECTING:
                serverState.setText("Connecting");
                serverState.setTextColor(getResources().getColor(R.color.ORAGNE));
                break;
            case Str.SERVER_STATE_DISCONNECTED:
                serverState.setText("Disconnected");
                serverState.setTextColor(getResources().getColor(R.color.RED));
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Str.MAKE_DISCOVERABLE) {
            if (resultCode != 0) {
                Toast.makeText(getApplicationContext(), "Device discoverable for 120 seconds", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == Str.ENABLE_BT_int) {
            if (resultCode != 0) {
                sendToService(Str.BT_STATE_s, Str.BT_STATE_ENABLED);
            } else {
                sendToService(Str.BT_STATE_s, Str.BT_STATE_DISABLED);
            }
        }
    }

}
