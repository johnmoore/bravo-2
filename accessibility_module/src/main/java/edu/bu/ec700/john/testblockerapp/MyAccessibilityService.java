package edu.bu.ec700.john.testblockerapp;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MyAccessibilityService extends AccessibilityService {

    static final String TAG = "MyAccessibilityService";
    private SampleOverlayView overlayView;

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
        Log.v(TAG, String.format(
                "onAccessibilityEvent: [type] %s [class] %s [package] %s [time] %s [text] %s",
                getEventType(event), event.getClassName(), event.getPackageName(),
                event.getEventTime(), getEventText(event)));
        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED || event.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED) {
            if (getEventText(event).contains("secret")) {
                Toast.makeText(this, "Not allowed!", Toast.LENGTH_LONG).show();
                Log.v(TAG, getEventText(event).replaceAll("secret", "******"));
                if (overlayView == null) {
                    overlayView = new SampleOverlayView(this);
                }
                AccessibilityNodeInfo source = event.getSource();
                if (source == null) {
                    Log.v(TAG, "null reached");
                    return;
                }
                Log.v(TAG, "null passed");
                source.setText(getEventText(event).replaceAll("secret", "******"));
            }

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
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.flags = AccessibilityServiceInfo.DEFAULT;
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        setServiceInfo(info);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The service is starting, due to a call to startService()
        Log.v(TAG, "gogoeggrolls");
        Toast.makeText(this, " Service Started", Toast.LENGTH_LONG).show();
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

abstract class OverlayView extends RelativeLayout {

    protected WindowManager.LayoutParams layoutParams;

    private int layoutResId;
    private int notificationId = 0;

    public OverlayView(MyAccessibilityService service, int layoutResId, int notificationId) {
        super(service);

        this.layoutResId = layoutResId;
        this.notificationId = notificationId;

        this.setLongClickable(true);

        this.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                return onTouchEvent_LongPress();
            }
        });

        load();
    }


    public int getLayoutGravity() {
        // Override this to set a custom Gravity for the view.

        return Gravity.CENTER;
    }

    private void setupLayoutParams() {
        layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);

        layoutParams.gravity = getLayoutGravity();

        onSetupLayoutParams();

    }

    protected void onSetupLayoutParams() {
        // Override this to modify the initial LayoutParams. Be sure to call
        // super.setupLayoutParams() first.
    }

    private void inflateView() {
        // Inflates the layout resource, sets up the LayoutParams and adds the
        // View to the WindowManager service.

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(layoutResId, this);

        onInflateView();

    }

    protected void onInflateView() {
        // Override this to make calls to findViewById() to setup references to
        // the views that were inflated.
        // This is called automatically when the object is created right after
        // the resource is inflated.
    }

    public boolean isVisible() {
        // Override this method to control when the Overlay is visible without
        // destroying it.
        return true;
    }

    public void refreshLayout() {
        // Call this to force the updating of the view's layout.

        if (isVisible()) {
            removeAllViews();
            inflateView();

            onSetupLayoutParams();

            ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).updateViewLayout(this, layoutParams);

            refresh();
        }

    }

    protected void addView() {
        setupLayoutParams();

        ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).addView(this, layoutParams);

        super.setVisibility(View.GONE);
    }

    protected void load() {
        inflateView();
        addView();
        refresh();
    }

    protected void unload() {
        ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).removeView(this);

        removeAllViews();
    }

    protected void reload() {
        unload();

        load();
    }

    public void destory() {
        ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).removeView(this);
    }

    public void refresh() {
        // Call this to update the contents of the Overlay.

        if (!isVisible()) {
            setVisibility(View.GONE);
        } else {
            setVisibility(View.VISIBLE);

            refreshViews();
        }
    }

    protected void refreshViews() {
        // Override this method to refresh the views inside of the Overlay. Only
        // called when Overlay is visible.
    }

    protected boolean showNotificationHidden() {
        // Override this to configure the notification to remain even when the
        // overlay is invisible.
        return true;
    }

    protected boolean onVisibilityToChange(int visibility) {
        // Catch changes to the Overlay's visibility in order to animate

        return true;
    }

    protected View animationView() {
        return this;
    }

    protected void hide() {
        // Set visibility, but bypass onVisibilityToChange()
        super.setVisibility(View.GONE);
    }

    protected void show() {
        // Set visibility, but bypass onVisibilityToChange()

        super.setVisibility(View.VISIBLE);
    }



    protected int getLeftOnScreen() {
        int[] location = new int[2];

        getLocationOnScreen(location);

        return location[0];
    }

    protected int getTopOnScreen() {
        int[] location = new int[2];

        getLocationOnScreen(location);

        return location[1];
    }

    protected boolean isInside(View view, int x, int y) {
        // Use this to test if the X, Y coordinates of the MotionEvent are
        // inside of the View specified.

        int[] location = new int[2];

        view.getLocationOnScreen(location);

        if (x >= location[0]) {
            if (x <= location[0] + view.getWidth()) {
                if (y >= location[1]) {
                    if (y <= location[1] + view.getHeight()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    protected void onTouchEvent_Up(MotionEvent event) {

    }

    protected void onTouchEvent_Move(MotionEvent event) {

    }

    protected void onTouchEvent_Press(MotionEvent event) {

    }

    public boolean onTouchEvent_LongPress()
    {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {

            onTouchEvent_Press(event);

        } else if (event.getActionMasked() == MotionEvent.ACTION_UP) {

            onTouchEvent_Up(event);

        } else if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {

            onTouchEvent_Move(event);

        }

        return super.onTouchEvent(event);

    }

}
class SampleOverlayView extends OverlayView {

    private TextView info;


    public SampleOverlayView(MyAccessibilityService service) {
        super(service, R.layout.overlay, 1);
    }

    @Override
    protected void onInflateView() {
        info = (TextView) this.findViewById(R.id.textview_info);
    }

    @Override
    protected void refreshViews() {
        info.setText("WAITING\nWAITING");
    }

    @Override
    protected void onTouchEvent_Up(MotionEvent event) {
        info.setText("UP\nPOINTERS: " + event.getPointerCount());
    }

    @Override
    protected void onTouchEvent_Move(MotionEvent event) {
        info.setText("MOVE\nPOINTERS: " + event.getPointerCount());
    }

    @Override
    protected void onTouchEvent_Press(MotionEvent event) {
        info.setText("DOWN\nPOINTERS: " + event.getPointerCount());
    }

    @Override
    public boolean onTouchEvent_LongPress() {
        info.setText("LONG\nPRESS");

        return true;
    }


}