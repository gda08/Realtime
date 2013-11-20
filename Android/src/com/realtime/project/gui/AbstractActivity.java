package com.realtime.project.gui;

import com.realtime.project.CommService;
import com.realtime.project.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


public abstract class AbstractActivity extends Activity {

	private static final String CONNECT_TO_SERVER = "s3";
	private static final String SERVER_STATE_s = "s7";
    private static final String BT_STATE_s = "s9";
    private static final String SCAN_DEVICES_s = "s11";
    private static final String MAKE_DISCOVERABLE_s = "s12";
    private static final String CHECK_BT_STATE_s = "s13";    
    private static final String MODE_OFF_s = "s28";
    private static final String MODE_BEAM_s = "s29";
    private static final String MODE_BALL_s = "s30";
    private static final int 	ENABLE_BT_int = 6;
    private static final int 	MAKE_DISCOVERABLE = 8;
    private static final int 	SERVER_STATE_DISCONNECTED = 13;
    private static final int 	SERVER_STATE_CONNECTING = 14;
    private static final int 	SERVER_STATE_CONNECTED = 15;
    private static final int 	BT_STATE_ENABLED = 16;
    private static final int 	BT_STATE_DISABLED = 17;    
    private static final int 	MODE_OFF = 0;
    private static final int 	MODE_BEAM = 1;
    private static final int 	MODE_BALL = 2;
	
    private TextView btState, serverState;
    private BroadcastReceiver myReceiver;
    
    private AlertDialog regulModeDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver();
        createActionDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sendToService(CHECK_BT_STATE_s);
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
                sendToService(MAKE_DISCOVERABLE_s);
                return true;
            case R.id.start_scan:
                sendToService(SCAN_DEVICES_s);
                return true;
            case R.id.connect_to_server:
                sendToService(CONNECT_TO_SERVER);
                return true;
            case R.id.regul_mode:
            	regulModeDialog.show();
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
        filter.addAction(SERVER_STATE_s);
        filter.addAction(BT_STATE_s);
        registerReceiver(myReceiver, filter);
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (action.equals(SERVER_STATE_s)) {
            updateServerState(intent.getIntExtra(SERVER_STATE_s, 0));
        } else if (action.equals(BT_STATE_s)) {
            updateBTState(intent.getIntExtra(BT_STATE_s, 0));
        }
    }

    private void enableBT() {
        updateBTState(BT_STATE_ENABLED);
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, ENABLE_BT_int);
    }

    private void disableBT() {
        updateBTState(BT_STATE_DISABLED);
        BluetoothAdapter.getDefaultAdapter().disable();
        sendToService(BT_STATE_s, BT_STATE_DISABLED);
    }

    private void updateBTState(int state) {
        switch (state) {
            case BT_STATE_ENABLED:
                btState.setText("Enabled");
                btState.setTextColor(getResources().getColor(R.color.GREEN));
                break;
            case BT_STATE_DISABLED:
                btState.setText("Disabled");
                btState.setTextColor(getResources().getColor(R.color.RED));
                break;
        }
    }

    private void updateServerState(int state) {
        switch (state) {
            case SERVER_STATE_CONNECTED:
                serverState.setText("Connected");
                serverState.setTextColor(getResources().getColor(R.color.GREEN));
                break;
            case SERVER_STATE_CONNECTING:
                serverState.setText("Connecting");
                serverState.setTextColor(getResources().getColor(R.color.ORAGNE));
                break;
            case SERVER_STATE_DISCONNECTED:
                serverState.setText("Disconnected");
                serverState.setTextColor(getResources().getColor(R.color.RED));
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MAKE_DISCOVERABLE) {
            if (resultCode != 0) {
                Toast.makeText(getApplicationContext(), "Device discoverable for 120 seconds", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == ENABLE_BT_int) {
            if (resultCode != 0) {
                sendToService(BT_STATE_s, BT_STATE_ENABLED);
            } else {
                sendToService(BT_STATE_s, BT_STATE_DISABLED);
            }
        }
    }
    
    private void createActionDialog() {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("REGUL MODE");
        CharSequence[] c = {"Mode: OFF", "Mode: BEAM", "Mode: BALL"};
        builder.setItems(c, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
                case MODE_OFF:
                	sendToService(MODE_OFF_s);
                	break;
                case MODE_BEAM:
                	sendToService(MODE_BEAM_s);
                	break;
                case MODE_BALL:
                	sendToService(MODE_BALL_s);
                	break;
				}
			}
        	
        });
        regulModeDialog = builder.create();
    }

}
