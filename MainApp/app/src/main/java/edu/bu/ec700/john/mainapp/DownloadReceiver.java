package edu.bu.ec700.john.mainapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class DownloadReceiver extends ResultReceiver {

    DefenseManagerService owner;

    public DownloadReceiver(Handler handler, DefenseManagerService owner) {
        super(handler);
        this.owner = owner;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        super.onReceiveResult(resultCode, resultData);
        if (resultCode == DownloadService.UPDATE_PROGRESS) {
            int progress = resultData.getInt("progress");
            String apkname = resultData.getString("apkname");
            String servicetostart = resultData.getString("servicetostart");
            if (progress == 100) {
                if (apkname != null) {
                    owner.downloadDone(apkname, servicetostart);
                }
            }
        }
    }
}