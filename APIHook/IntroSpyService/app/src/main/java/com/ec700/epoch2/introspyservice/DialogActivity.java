package com.ec700.epoch2.introspyservice;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

// THe dialog to get user's response
// Refuse defaultly after 10 seconds
public class DialogActivity extends Activity {
    Button confirmButton;
    Button refuseButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        confirmButton = (Button)findViewById(R.id.ok_btn_id);
        refuseButton = (Button)findViewById(R.id.cancel_btn_id);
        setListener();
        // Hide after some seconds
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                sendBackResponse(false);
            }
        };

        handler.postDelayed(runnable, 10000);
    }

    private void setListener(){
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBackResponse(true);
            }
        });
        refuseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBackResponse(false);
            }
        });
    }

    private void sendBackResponse(boolean isAllow){
        Intent i = new Intent("RECEIVE_RESPONSE_FROM_ACTIVITY");
        i.putExtra("isAllow",isAllow);
        sendBroadcast(i);
        finish();
    }
}
