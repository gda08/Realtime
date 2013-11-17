package com.realtime.project.gui;

import com.realtime.project.R;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PlotterGUI extends AbstractActivity {

	public static final String SEND_TO_SERVER = "s5";
	
    private TextView txtAmp, txtPeriod, txtServerState, txtBTState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plotter);
        initGUI();
    }

    private void initGUI() {
        txtServerState = (TextView)findViewById(R.id.txtServerStatePlotter);
        txtBTState = (TextView)findViewById(R.id.txtBTStatePlotter);
        txtAmp = (TextView)findViewById(R.id.txtAmp);
        txtPeriod = (TextView)findViewById(R.id.txtPeriod);

        super.setBtStateTextView(txtBTState);
        super.setServerTextView(txtServerState);

        ((Button)findViewById(R.id.btnUpdate)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePlotter();
            }
        });
    }

    public void updatePlotter() {
        String s = "123";
        sendToService(SEND_TO_SERVER, s);
    }

}
