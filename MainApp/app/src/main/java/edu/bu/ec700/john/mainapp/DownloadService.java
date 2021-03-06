package edu.bu.ec700.john.mainapp;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

// Cited: http://stackoverflow.com/questions/19304329/send-broadcast-from-one-apk-package-to-another-apk-package
public class DownloadService extends IntentService {
    public static final int UPDATE_PROGRESS = 8344;
    public DownloadService() {
        super("DownloadService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        String urlToDownload = intent.getStringExtra("url");
        String apkname = intent.getStringExtra("apkname");
        String servicetostart = intent.getStringExtra("servicetostart");
        String config = intent.getStringExtra("config");
        ResultReceiver receiver = (ResultReceiver) intent.getParcelableExtra("receiver");
        try {
            URL url = new URL(urlToDownload);
            URLConnection connection = url.openConnection();
            connection.connect();
            // this will be useful so that you can show a typical 0-100% progress bar
            int fileLength = connection.getContentLength();

            // download the file
            InputStream input = new BufferedInputStream(connection.getInputStream());
            OutputStream output = new FileOutputStream("/sdcard/" + apkname);

            byte data[] = new byte[1024];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                Bundle resultData = new Bundle();
                resultData.putInt("progress" ,(int) (total * 100 / fileLength));
                receiver.send(UPDATE_PROGRESS, resultData);
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.v("MainApp", "IO ERR:" + e.toString() + e.getCause() + e.getMessage());
        }

        Bundle resultData = new Bundle();
        resultData.putInt("progress" ,100);
        resultData.putString("apkname" ,apkname);
        resultData.putString("servicetostart" ,servicetostart);
        resultData.putString("config", config);
        Log.v("MainApp", "100%");
        receiver.send(UPDATE_PROGRESS, resultData);
    }
}