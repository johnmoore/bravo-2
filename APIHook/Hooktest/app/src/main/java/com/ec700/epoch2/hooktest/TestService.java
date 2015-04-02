package com.ec700.epoch2.hooktest;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.widget.Toast;

import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class TestService extends IntentService {

    public TestService() {
        super("TestService");
    }

    final Handler handler = new Handler();

    @Override
    protected void onHandleIntent(Intent intent) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                getLastKnownLocation();
                Location lastKnownLocation = getLastKnownLocation();
                if(lastKnownLocation!=null)
                    Toast.makeText(TestService.this, "service longitude:" + lastKnownLocation.getLongitude() + "\tlatitude:" + lastKnownLocation.getLatitude() + ";", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(TestService.this,"service location is null;",Toast.LENGTH_LONG).show();
            }
        };
        handler.postDelayed(runnable,10000);
    }

    private Location getLastKnownLocation() {
        LocationManager mLocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

}
