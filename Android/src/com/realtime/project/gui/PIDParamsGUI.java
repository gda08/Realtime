package com.realtime.project.gui;

import com.realtime.project.R;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;


public class PIDParamsGUI extends AbstractActivity {
	
	private static final String UPDATE_PID_PARAMS = "27";
    
    private TextView txtK, txtTi, txtTd, txtTr, txtN, txtBeta, txtH;
    private TextView txtServerState, txtBTState;
    private CheckBox boxIntegratorOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pid);
        initGUI();
    }

    private void initGUI() {
        txtServerState = (TextView)findViewById(R.id.txtServerStatePid);
        txtBTState = (TextView)findViewById(R.id.txtBTStatePid);
        txtK = (TextView)findViewById(R.id.txtPidK);
        txtTi = (TextView)findViewById(R.id.txtPidTi);
        txtTr = (TextView)findViewById(R.id.txtPidTr);
        txtTd = (TextView)findViewById(R.id.txtPidTd);
        txtN = (TextView)findViewById(R.id.txtPidN);
        txtBeta = (TextView)findViewById(R.id.txtPidBeta);
        txtH = (TextView)findViewById(R.id.txtPidH);
        boxIntegratorOn = (CheckBox)findViewById(R.id.chckBoxPidIntegratorOn);
        boxIntegratorOn.setChecked(true);
        
        super.setBtStateTextView(txtBTState);
        super.setServerTextView(txtServerState);

        ((Button)findViewById(R.id.btnPidUpdate)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePID();
            }
        });
    }

    public void updatePID() {
    	String params = txtK.getText().toString() + "," + 
    			txtTi.getText().toString() + "," +
    			txtTd.getText().toString() + "," +
    			txtTr.getText().toString() + "," +
    			txtN.getText().toString() + "," +
    			txtBeta.getText().toString() + "," +
    			txtH.getText().toString() + "," + 
    			boxIntegratorOn.isChecked();
    	sendToService(UPDATE_PID_PARAMS, params);
    }

}
