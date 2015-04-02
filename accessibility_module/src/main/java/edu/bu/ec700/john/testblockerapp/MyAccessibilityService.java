package edu.bu.ec700.john.testblockerapp;

import android.accessibilityservice.AccessibilityService;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;
import android.widget.AbsoluteLayout;

import android.widget.Toast;

import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;


public class MyAccessibilityService extends AccessibilityService {

    static final String TAG = "MyAccessibilityService";
    private OverlayView overlayView;
    boolean triggered = false;
    HashMap<Integer, OverlayView> overlays = new HashMap<>();

    private String getEventType(AccessibilityEvent event) {
        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                return "TYPE_NOTIFICATION_STATE_CHANGED";
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                return "TYPE_VIEW_CLICKED";
            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                return "TYPE_VIEW_FOCUSED";
            case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED:
                return "TYPE_VIEW_LONG_CLICKED";
            case AccessibilityEvent.TYPE_VIEW_SELECTED:
                return "TYPE_VIEW_SELECTED";
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                return "TYPE_WINDOW_STATE_CHANGED";
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                return "TYPE_VIEW_TEXT_CHANGED";
            case AccessibilityEvent.TYPE_WINDOWS_CHANGED:
                return "TYPE_WINDOWS_CHANGED";
        }
        return new Integer(event.getEventType()).toString();
    }

    private String getEventText(AccessibilityEvent event) {
        StringBuilder sb = new StringBuilder();
        for (CharSequence s : event.getText()) {
            sb.append(s);
        }
        return sb.toString();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        ArrayList<Integer> outs = new ArrayList<>();
        List<AccessibilityWindowInfo> ws = getWindows();
        Log.v(TAG, new Integer(ws.size()).toString() + " windows");
        for (AccessibilityWindowInfo w : ws) {
            Log.v(TAG, w.getClass().toString());
            WalkNode(w.getRoot(), outs);
        }
        Iterator it = overlays.entrySet().iterator();
        ArrayList<Integer> toremove = new ArrayList<>();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            if (!outs.contains(pair.getKey())) {
                overlays.get(pair.getKey()).destory();
                toremove.add((Integer)pair.getKey());
            }
        }
        for (Integer i : toremove) {
            overlays.remove(i);
        }

        Log.v(TAG, String.format(
                "onAccessibilityEvent: [type] %s [class] %s [package] %s [time] %s [text] %s",
                getEventType(event), event.getClassName(), event.getPackageName(),
                event.getEventTime(), getEventText(event)));
        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED || event.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED) {
            AccessibilityNodeInfo node = event.getSource();
            if (node == null) {
                return;

            }
            //Log.v(TAG, "null passed1");
            if (getEventText(event).toLowerCase().contains("gogoeggrolls")) {
                Toast.makeText(this, "Not allowed!", Toast.LENGTH_LONG).show();
                Log.v(TAG, getEventText(event).replaceAll("(?i)secret", "******"));

                AccessibilityNodeInfo node2 = event.getSource();
                if (node2 == null) {
                    return;

                }
                //Log.v(TAG, "null passed2");
                Bundle b = new Bundle();
                b.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, getEventText(event).replaceAll("(?i)secret", "******"));
                //node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, b);
                Rect outbounds = new Rect();
                node.getBoundsInScreen(outbounds);
                Log.v(TAG, new Integer(outbounds.left).toString());
                Log.v(TAG, new Integer(outbounds.top).toString());
                if (overlayView == null) {
                    overlayView = new OverlayView(this, outbounds);
                }


            }


        }
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            if (overlayView != null) {
                overlayView.destory();
                overlayView = null;
            }


        }

        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOWS_CHANGED) {
/*
            AccessibilityWindowInfo node = event.getSource().getWindow();
            if (node == null) {
                return;
            }


*/

            //Log.v(TAG, "null passed!");
        }
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {

            AccessibilityNodeInfo n = event.getSource();
            if (n == null) {
                return;
            }
            //WalkNode(n);
        }
    }

    private void WalkNode(AccessibilityNodeInfo root, ArrayList<Integer> outs) {
        if (root == null) {
            return;
        }
        if (root.getText() != null) {
            Log.v(TAG, root.getText().toString());
            if (root.getText().toString().toLowerCase().contains("secret")) {
                Rect outbounds = new Rect();
                root.getBoundsInScreen(outbounds);
                if (overlays.containsKey(root.hashCode())) {

                    Rect b = overlays.get(root.hashCode()).getbounds();
                    if (!b.equals(outbounds)) {
                        OverlayView old = overlays.get(root.hashCode());
                        overlays.put(root.hashCode(), new OverlayView(this, outbounds));
                        old.destory();

                    }

                } else {
                    overlays.put(root.hashCode(), new OverlayView(this, outbounds));
                }
                outs.add(root.hashCode());


            }
        }

        int i = 0;
        while (i < root.getChildCount()) {
            AccessibilityNodeInfo child = root.getChild(i);
            WalkNode(child, outs);
            i = i + 1;
        }
    }

    @Override
    public void onInterrupt() {
        Log.v(TAG, "onInterrupt");
    }


    @Override
    protected void onServiceConnected() {
        Toast.makeText(this, "Service Connected", Toast.LENGTH_LONG).show();
        super.onServiceConnected();
        Log.v(TAG, "onServiceConnected");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The service is starting, due to a call to startService()
        Toast.makeText(this, "Service Started!", Toast.LENGTH_LONG).show();
        return START_STICKY;
        // For time consuming an long tasks you can launch a new thread here...

    }

    @Override
    public void onCreate() {
        //Toast.makeText(this, "The new Service was Created", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();

    }


}


class OverlayView extends AbsoluteLayout {

    protected WindowManager.LayoutParams layoutParams;

    private Rect bounds;

    public Rect getbounds() {
        return bounds;
    }
    public OverlayView(MyAccessibilityService service, Rect bounds) {
        super(service);

        this.bounds = bounds;

        load();
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public void updatebounds(Rect bounds) {
        Log.v("MyAccessibilityService", "updating bounds");

    }

    private void setupLayoutParams() {
        layoutParams = new WindowManager.LayoutParams(bounds.width(), bounds.height(), bounds.left, bounds.top-getStatusBarHeight(),
                WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
    }

    private void inflateView() {
        // Inflates the layout resource, sets up the LayoutParams and adds the
        // View to the WindowManager service.

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.overlay, this);
        this.setBackgroundColor(0);

    }

    protected void addView() {
        setupLayoutParams();


        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.addView(this, layoutParams);

        super.setVisibility(View.GONE);

    }

    protected void load() {
        inflateView();
        addView();
        setVisibility(View.VISIBLE);

        Log.v("MyAccessibilityService", new Integer(super.getLeft()).toString());
        Log.v("MyAccessibilityService", new Integer(super.getTop()).toString());
    }

    public void destory() {
        ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).removeView(this);
    }

    protected void hide() {
        // Set visibility, but bypass onVisibilityToChange()
        super.setVisibility(View.GONE);
    }

    protected void show() {
        // Set visibility, but bypass onVisibilityToChange()

        super.setVisibility(View.VISIBLE);
    }


}