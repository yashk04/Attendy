package com.google.firebase.samples.apps.mlkit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.samples.apps.mlkit.others.SharedPref;

public class SettingsActivity extends AppCompatActivity {

    EditText mEtIp,mEtPort;
    Button button;
    private boolean flag = true;
    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mEtIp = findViewById(R.id.et_ip);
        mEtPort = findViewById(R.id.et_port_no);
        button = findViewById(R.id.btn_change_ip);
        sharedPref = new SharedPref(getApplicationContext());
        if( sharedPref.getIP()!= null && sharedPref.getPort() != null)
        {
            mEtIp.setText(sharedPref.getIP());
            mEtPort.setText(sharedPref.getPort());
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag = true;
                if(mEtIp.getText().toString().length() == 0)
                {
                    flag = false;
                    mEtIp.setError("Please enter IP");
                }
                if(mEtPort.getText().toString().length() == 0)
                {
                    flag = false;
                    mEtPort.setError("Please enter PORT");
                }
                if(flag)
                {
                    sharedPref.setIp(mEtIp.getText().toString());
                    sharedPref.setPort(mEtPort.getText().toString());
                    finish();
                }

            }
        });
    }
}
