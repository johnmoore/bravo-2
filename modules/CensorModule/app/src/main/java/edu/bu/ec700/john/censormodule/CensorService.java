package edu.bu.ec700.john.censormodule;

import android.accessibilityservice.AccessibilityService;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;


public class CensorService extends AccessibilityService implements BravoDefenseInterface {

    static final String TAG = "CensorModule";  // debug tag
    SensitiveInfoRecognizer recognizer = new SensitiveInfoRecognizer();
    private boolean enabled = true;

    public static final int ACTION_NEW_FILTER_STRING = 1;
    public static final int ACTION_NEW_FILTER_PATTERN = 2;
    public static final int ACTION_ENABLE = 3;
    public static final int ACTION_DISABLE = 4;

    private String getEventText(AccessibilityEvent event) {
        StringBuilder sb = new StringBuilder();
        for (CharSequence s : event.getText()) {
            sb.append(s);
        }
        return sb.toString();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (!enabled) {
            return;
        }

        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED ||
                event.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED) {
            AccessibilityNodeInfo node = event.getSource();
            if (node == null) {
                return;

            }

            if (recognizer.isNodeSensitive(node)) {
                Toast.makeText(this, "Not allowed!", Toast.LENGTH_LONG).show();

                Bundle b = new Bundle();
                b.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                        recognizer.sanitize(node));
                node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, b);

            }
        }
    }

    @Override
    protected void onServiceConnected() {
        Log.v(TAG, "onServiceConnected");
        recognizer.addSensitivePattern("^(?!000|666)[0-8][0-9]{2}-(?!00)[0-9]{2}-(?!0000)[0-9]{4}$");
        recognizer.addSensitiveString("confidential");
        recognizer.addSensitiveString("internal only");
        recognizer.addSensitiveString("company secret");
        recognizer.addSensitiveString("jane doe");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IntentFilter intentFilter = new IntentFilter("edu.bu.ec700.john.action.obscuremodule");
        BroadcastReceiver receiver = new Receiver(this);
        this.registerReceiver(receiver , intentFilter);
        Log.v(TAG, "connected");

        return START_STICKY;
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void enable() {
        Log.v(TAG, "Enabling");
        enabled = true;
    }

    @Override
    public void disable() {
        Log.v(TAG, "Disabling");
        enabled = false;
    }

}