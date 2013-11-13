package com.realtime.project.gui;

import com.realtime.project.R;
import com.realtime.project.Str;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class PIDParams extends AbstractActivity {

    private TextView txtK, txtServerState, txtBTState;

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
        String s = "PID" + ","
                + txtK.getText().toString() + " ";
        sendToService(Str.SEND_TO_SERVER, s);
    }

}
