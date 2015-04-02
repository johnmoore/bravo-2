package edu.bu.ec700.john.obscuremodule;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;

// Based on http://www.jawsware.mobi/code_OverlayView/
class OverlayView extends AbsoluteLayout {

    protected WindowManager.LayoutParams layoutParams;

    private Rect bounds;

    public Rect getBounds() {
        return bounds;
    }

    public OverlayView(OverlayService service, Rect bounds) {
        super(service);
        this.bounds = bounds;

        load();
    }

    // Cited: http://stackoverflow.com/questions/3407256/height-of-status-bar-in-android
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void setupLayoutParams() {
        layoutParams = new WindowManager.LayoutParams(bounds.width(), bounds.height(), bounds.left, bounds.top - getStatusBarHeight(),
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
    }

    public void destroy() {
        ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).removeView(this);
    }

    protected void hide() {
        super.setVisibility(View.GONE);
    }

    protected void show() {
        super.setVisibility(View.VISIBLE);
    }


}