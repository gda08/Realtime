package com.realtime.project.gui;

import com.realtime.project.R;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class PIParamsGUI extends AbstractActivity {
	
	private static final String UPDATE_PI_PARAMS = "s26";
	
    private TextView txtK, txtTi, txtTr, txtBeta, txtH;
    private TextView txtServerState, txtBTState;
    private CheckBox boxIntegratorOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pi);
        initGUI();
    }

    private void initGUI() {
        txtServerState = (TextView)findViewById(R.id.txtServerStatePi);
        txtBTState = (TextView)findViewById(R.id.txtBTStatePi);
        txtK = (TextView)findViewById(R.id.txtPiK);
        txtTi = (TextView)findViewById(R.id.txtPiTi);
        txtTr = (TextView)findViewById(R.id.txtPiTr);
        txtBeta = (TextView)findViewById(R.id.txtPiBeta);
        txtH = (TextView)findViewById(R.id.txtPiH);
        boxIntegratorOn = (CheckBox)findViewById(R.id.chckBoxPiIntegratorOn);

        super.setBtStateTextView(txtBTState);
        super.setServerTextView(txtServerState);

        ((Button)findViewById(R.id.btnPiUpdate)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePI();
            }
        });
    }
    
    public void updatePI() {
    	String params = txtK.getText().toString() + "," + 
    			txtTi.getText().toString() + "," +
    			txtTr.getText().toString() + "," +
    			txtBeta.getText().toString() + "," +
    			txtH.getText().toString() + "," +
    			boxIntegratorOn.isChecked();
    	sendToService(UPDATE_PI_PARAMS, params);
    }

}
