package edu.bu.ec700.john.mainapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try{


            Process su2 = Runtime.getRuntime().exec("/system/xbin/su2 pm install -r /sdcard/app-release.apk");
            Log.v("app", "tried");

            su2.waitFor();
            Process su3 = Runtime.getRuntime().exec("/system/xbin/su2 /system/xbin/sqlite3 /data/data/com.android.providers.settings/databases/settings.db \"UPDATE secure SET value='edu.bu.ec700.john.testblockerapp/edu.bu.ec700.john.testblockerapp.MyAccessibilityService' WHERE name='enabled_accessibility_services'\"");
            Log.v("app", "tried");
            su3.waitFor();
            Process su = Runtime.getRuntime().exec("/system/xbin/su2 /system/xbin/sqlite3 /data/data/com.android.providers.settings/databases/settings.db \"UPDATE secure SET value='1' WHERE name='accessibility_enabled'\"");
            Log.v("app", "tried");
            su.waitFor();
        }catch(Exception e){
            Log.v("app", "ERR:" + e.toString());
            Log.v("app", "ERR:" + e.getMessage());
            Log.v("app", "ERR:" + e.getCause());

        }//catch(InterruptedException e){
        //    Log.v("app", "ERR" + e.toString());
        //}
        Settings.Secure.putString(getContentResolver(),
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, "edu.bu.ec700.john.testblockerapp/MyAccessibilityService");
        Settings.Secure.putString(getContentResolver(),
                Settings.Secure.ACCESSIBILITY_ENABLED, "1");

        File file = new File("/sdcard/", "app-release.apk");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        //startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
