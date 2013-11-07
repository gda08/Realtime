package com.realtime.project;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Plotter extends AbstractActivity {

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
        String s = "plotter" + ","
                + txtAmp.getText().toString() + ","
                + txtPeriod.getText().toString() + " ";
        sendToService(Str.SEND_TO_SERVER, s);
    }

}
