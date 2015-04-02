package com.ec700.epoch2.introspyservice;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;

public class RemoteService extends Service {
    private boolean blocking = false;
    private boolean result = true;
    private int type;
    private String packageName;
    private String dataDir;
    private String currentClassName;
    private String currentPackageName;

    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Intent intent = new Intent(RemoteService.this, DialogActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    };

    final Thread thread = new Thread() {

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (blocking) {
                    handler.sendEmptyMessage(1);
                }
            }
        }
    };

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            result = intent.getBooleanExtra("isAllow", false);
            blocking = false;
            // store user's preference at here
            SharedPreferences sharedPref = getSharedPreferences(packageName + currentClassName + type, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("hasRecord", true);
            editor.putBoolean("result", result);
            editor.commit();
        }
    };

    private int askForRule() {
        // implement rules, return 1 if allow, 0 for refuse
        if (currentPackageName!=null && !currentPackageName.equals( packageName)) {
            // probably run in the background
            writeToFile("detect background API call; ");
            return 0;
        }
        SharedPreferences sharedPref = getSharedPreferences(packageName + currentClassName + type, Context.MODE_PRIVATE);
        boolean hasRecord = sharedPref.getBoolean("hasRecord", false);
        if (hasRecord) {
            boolean result = sharedPref.getBoolean("result", false);
            writeToFile("detect user preference; ");
            if (result) {
                return 1;
            } else {
                return 0;
            }
        }
        return -1;
    }

    private void setCurrentState(int type, String packageName, String dataDir) {
        this.type = type;
        this.packageName = packageName;
        this.dataDir = dataDir;
        ActivityManager am = (ActivityManager) RemoteService.this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        this.currentClassName = taskInfo.get(0).topActivity.getClassName();
        this.currentPackageName = taskInfo.get(0).topActivity.getPackageName();
        writeToFile("type:"+type+", package name: "+packageName+", dataDir: "+dataDir+", currentClassName: "+currentClassName+", currentPackageName: "+currentPackageName+"\n");
    }

    private IBinder mBinder = new IRemoteService.Stub() {

        @Override
        public boolean isAllow(int type, String packageName, String dataDir) throws RemoteException {
            setCurrentState(type, packageName, dataDir);
            int ruleResult = askForRule();
            if (ruleResult < 0) {
                blocking = true;
                while (blocking) ;
            } else if (ruleResult == 0) {
                result = false;
            } else {
                result = true;
            }
            writeToFile(" result:"+result+"\n");
            return result;
        }
    };

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        thread.start();
        IntentFilter filter = new IntentFilter();
        filter.addAction("RECEIVE_RESPONSE_FROM_ACTIVITY"); //further more
        registerReceiver(receiver, filter);
        return mBinder;
    }

    private void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("log.txt", Context.MODE_APPEND));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
        }
    }


    private String readFromFile() {

        String ret = "";

        try {
            InputStream inputStream = openFileInput("log.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (IOException e) {
        }

        return ret;
    }

}