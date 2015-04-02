package edu.bu.ec700.john.obscuremodule;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.graphics.Rect;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class OverlayService extends AccessibilityService {

    static final String TAG = "ObscureModule";  // debug tag
    HashMap<Integer, OverlayView> overlays = new HashMap<>();  // keep track of overlays
    SensitiveInfoRecognizer recognizer = new SensitiveInfoRecognizer();

    private String getEventText(AccessibilityEvent event) {
        StringBuilder sb = new StringBuilder();
        for (CharSequence s : event.getText()) {
            sb.append(s);
        }
        return sb.toString();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        List<Integer> visible_nodes = new ArrayList<>();
        List<AccessibilityWindowInfo> ws = getWindows();

        // Walk the GUI tree.
        for (AccessibilityWindowInfo w : ws) {
            WalkNode(w.getRoot(), visible_nodes);
        }

        // Remove stale overlays.
        Iterator it = overlays.entrySet().iterator();
        ArrayList<Integer> to_be_removed = new ArrayList<>();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            if (!visible_nodes.contains(pair.getKey())) {
                overlays.get(pair.getKey()).destroy();
                to_be_removed.add((Integer)pair.getKey());
            }
        }
        for (Integer i : to_be_removed) {
            overlays.remove(i);
        }

    }

    private void WalkNode(AccessibilityNodeInfo root, List<Integer> out_nodes) {

        if (root == null) {
            return;
        }

        if (root.getText() != null) {

            if (recognizer != null && recognizer.isNodeSensitive(root)) {
                // Sensitive info found; obscure...

                Rect outbounds = new Rect();
                root.getBoundsInScreen(outbounds);

                if (overlays.containsKey(root.hashCode())) {
                    Rect b = overlays.get(root.hashCode()).getBounds();

                    if (!b.equals(outbounds)) {
                        // Position on screen has changed
                        OverlayView old = overlays.get(root.hashCode());
                        overlays.put(root.hashCode(), new OverlayView(this, outbounds));
                        old.destroy();

                    }
                } else {
                    // New overlay.
                    overlays.put(root.hashCode(), new OverlayView(this, outbounds));
                }
                out_nodes.add(root.hashCode());
            }
        }

        // Recurse
        int i = 0;
        while (i < root.getChildCount()) {
            AccessibilityNodeInfo child = root.getChild(i);
            WalkNode(child, out_nodes);
            i = i + 1;
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
        return START_STICKY;
    }

    @Override
    public void onInterrupt() {

    }

}