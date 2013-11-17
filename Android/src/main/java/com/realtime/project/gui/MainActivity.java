package com.realtime.project.gui;

import com.realtime.project.R;

import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.Toast;


/**
 * CREATE THREE TABS. EACH TAB CONTAINS ONE ACTIVITY.
 */

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity {

	private static final String TOAST = "s8";
	
    private BroadcastReceiver myReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        TabHost tabHost = getTabHost();

        TabHost.TabSpec plotterspec = tabHost.newTabSpec("Plotter");
        plotterspec.setIndicator("Plotter");
        Intent plotterIntent = new Intent(this, PlotterGUI.class);
        plotterspec.setContent(plotterIntent);

        TabHost.TabSpec pidspec = tabHost.newTabSpec("PID");
        pidspec.setIndicator("PID");
        Intent pidIntent = new Intent(this, PIDParamsGUI.class);
        pidspec.setContent(pidIntent);

        TabHost.TabSpec pispec = tabHost.newTabSpec("PI");
        pispec.setIndicator("PI");
        Intent piIntent = new Intent(this, PIParamsGUI.class);
        pispec.setContent(piIntent);

        tabHost.addTab(plotterspec);
        tabHost.addTab(pidspec);
        tabHost.addTab(pispec);

        tabHost.setCurrentTab(1);   // calls onCreate() of the pid activity
        tabHost.setCurrentTab(2);   // calls onCreate() of the pi activity
        tabHost.setCurrentTab(0);   // calls onCreate() of the plotter activity

        registerReceiver();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(myReceiver);
        } catch (Exception e){

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
        filter.addAction(TOAST);
        registerReceiver(myReceiver, filter);
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (action.equals(TOAST)) {
            Toast.makeText(getApplicationContext(), intent.getStringExtra(TOAST), Toast.LENGTH_SHORT).show();
        }
    }

}
