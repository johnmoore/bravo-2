package edu.bu.ec700.john.obscuremodule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Receiver extends BroadcastReceiver {

    OverlayService owner;

    public Receiver(OverlayService owner) {
        super();
        this.owner = owner;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int opcode=intent.getIntExtra("type", -1);
        String s;

        switch(opcode) {
            case OverlayService.ACTION_NEW_FILTER_STRING:
                // New string to filter.
                s = intent.getStringExtra("filterstring");
                if (s != null) {
                    this.owner.recognizer.addSensitiveString(s);
                }
                Log.v(owner.TAG, "Adding new filter string: " + s);
                break;
            case OverlayService.ACTION_NEW_FILTER_PATTERN:
                // New pattern to filter.
                s = intent.getStringExtra("patternstring");
                if (s != null) {
                    this.owner.recognizer.addSensitivePattern(s);
                }
                Log.v(owner.TAG, "Adding new filter pattern: " + s);
                break;
            case OverlayService.ACTION_ENABLE:
                owner.enable();
                break;

            case OverlayService.ACTION_DISABLE:
                owner.disable();
                break;

            default:
                Log.v(owner.TAG, "Unknown command received");
        }
    }
}