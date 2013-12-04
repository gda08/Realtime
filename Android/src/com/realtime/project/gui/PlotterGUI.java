package com.realtime.project.gui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.realtime.project.R;

public class PlotterGUI extends AbstractActivity {

	private static final String UPDATE_POSITION_REFERENCE = "s50";
	
    private TextView txtPosRef, txtServerState, txtBTState,
    				 txtU, txtY, txtYref;
    
    private LinearLayout plotterLayout;
    
    private Plotter plotter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plotter);
        registerReceiverX();
        initGUI();        
    }
    
    @Override
    protected void onDestroy() {
    	super.onStop();
    	try {
    		if (myReceiverX != null)
        		unregisterReceiver(myReceiverX);
    	} catch (Exception e) {
    		
    	}
    }

    private void initGUI() {
        txtServerState = (TextView)findViewById(R.id.txtServerStatePlotter);
        txtBTState = (TextView)findViewById(R.id.txtBTStatePlotter);
        txtPosRef = (TextView)findViewById(R.id.txtPosRef);
        txtU = (TextView)findViewById(R.id.txtU);
        txtY = (TextView)findViewById(R.id.txtY);
        txtYref = (TextView)findViewById(R.id.txtYref);
        
        plotterLayout = (LinearLayout)findViewById(R.id.plotterLayout);
        plotterLayout.setBackgroundColor(Color.WHITE);

        plotter = new Plotter(getApplicationContext());
        
        plotterLayout.addView(plotter);
        plotter.invalidate();
        
        super.setBtStateTextView(txtBTState);
        super.setServerTextView(txtServerState);

        ((Button)findViewById(R.id.btnUpdate)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateReferencePosition();
                if (!switchRef) {
                	Thread t = new Thread(refs);
                	t.start();
                	switchRef = true;
                }
            }
        });        
    }
    
    private void updateReferencePosition() {
        String s = txtPosRef.getText().toString();
        sendToService(UPDATE_POSITION_REFERENCE, s);
        ref = Integer.parseInt(s);
    }
    
    private boolean switchRef = false;
    int ref = 0;
    
    private Runnable refs = new Runnable() {
		@Override
		public void run() {
			while (true) {
				if (switchRef) {
					txtPosRef.post(new Runnable() {
						@Override
						public void run() {
							ref *= -1;
							sendToService(UPDATE_POSITION_REFERENCE, String.valueOf(ref));
						}
					});
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	};
    
    private BroadcastReceiver myReceiverX;
    private static final String UPDATE_PLOTTER = "s551";
    
    private void registerReceiverX() {
        myReceiverX = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, final Intent intent) {
                handleIntent(intent);
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(UPDATE_PLOTTER);
        registerReceiver(myReceiverX, filter);
    } 
    
    private int time = 0;
    
    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (action.equals(UPDATE_PLOTTER)) {
        	float u = (float) intent.getDoubleExtra(UPDATE_PLOTTER+"u", 0);
        	float y = (float) intent.getDoubleExtra(UPDATE_PLOTTER+"y", 0);
        	float yref = (float) intent.getDoubleExtra(UPDATE_PLOTTER+"yref", 0);
        	
        	txtYref.setText("Yref:" + yref + "  ");
        	try {
        		txtU.setText("U:" + String.valueOf(u).substring(0, 7) + "  ");
            	txtY.setText("Y:" + String.valueOf(y).substring(0, 7) + "  ");
        	} catch (Exception e) {
        		
        	}
        
        	if (time>=100) {
        		time--;
        	}
        	
        	plotter.addU(new PointF(time, u));
        	plotter.addY(new PointF(time, y));
        	plotter.addYref(new PointF(time, yref));
			
        	plotter.invalidate();
			
        	time++;
        }
    }

}
