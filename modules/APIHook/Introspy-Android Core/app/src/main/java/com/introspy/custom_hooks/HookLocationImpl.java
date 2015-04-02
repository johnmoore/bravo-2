package com.introspy.custom_hooks;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.ec700.epoch2.introspyservice.IRemoteService;
import com.introspy.core.ApplicationConfig;
import com.introspy.core.IntroHook;
import com.introspy.core.Main;

import java.io.IOException;

// The detail implementation of location API hook
class intro_LOCATION_LASTKNOW extends IntroHook {

    @Override
    public void execute(Object... args) {
        super.execute(args);
    }

    // Change the return value of API call
    @Override
    protected Object _hookInvoke(Object... args) throws Throwable {
        // get the original return
        Location lastLocation = (Location) _old.invoke(_resources, args);
        if (Main.service == null) {
            // Try to connect to service first
            RemoteServiceConnection connection = new RemoteServiceConnection();
            Intent intent = new Intent();
            intent.setClassName("com.ec700.epoch2.introspyservice", "com.ec700.epoch2.introspyservice.RemoteService");

            Main.context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        }
        // Ask remote service to check if that operation is allowed or not
        if (Main.service != null && !Main.service.isAllow(1,ApplicationConfig.getPackageName(),ApplicationConfig.getDataDir())) {
            // if refused, then set it to meaninglessness value
            if (lastLocation == null) {
                lastLocation = new Location(((String) args[0]));
            }
            lastLocation.setLongitude(0.0);
            lastLocation.setLatitude(0.0);

        }
        return lastLocation;
    }

    private static class RemoteServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName component, IBinder binder) {
            Main.service = IRemoteService.Stub.asInterface(binder);
        }

        @Override
        public void onServiceDisconnected(ComponentName component) {
        }
    }
}
