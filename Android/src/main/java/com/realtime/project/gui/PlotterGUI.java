package com.realtime.project.gui;

import com.realtime.project.R;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PlotterGUI extends AbstractActivity {

	public static final String SEND_TO_SERVER = "s5";
	public static final String UPDATE_POSITION_REFERENCE = "s50";
	
    private TextView txtPosRef, txtServerState, txtBTState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plotter);
        initGUI();
    }

    private void initGUI() {
        txtServerState = (TextView)findViewById(R.id.txtServerStatePlotter);
        txtBTState = (TextView)findViewById(R.id.txtBTStatePlotter);
        txtPosRef = (TextView)findViewById(R.id.txtPosRef);

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
        String s = txtPosRef.getText().toString();
        sendToService(UPDATE_POSITION_REFERENCE, s);
    }

}
