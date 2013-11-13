package com.realtime.project.gui;

import com.realtime.project.R;
import com.realtime.project.Str;
import com.realtime.project.R.id;
import com.realtime.project.R.layout;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class PIParams extends AbstractActivity {

    private TextView txtK, txtServerState, txtBTState;;

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
        String s = "PI" + ","
                + txtK.getText().toString() + " ";
        sendToService(Str.SEND_TO_SERVER, s);
    }

}
