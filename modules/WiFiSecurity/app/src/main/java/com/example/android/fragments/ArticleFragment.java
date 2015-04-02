/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.content.Context;

import java.util.List;

import javax.crypto.Mac;

public class ArticleFragment extends Fragment {
    final static String ARG_POSITION = "position";
    int mCurrentPosition = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {

        // If activity recreated (such as from screen rotate), restore
        // the previous article selection set by onSaveInstanceState().
        // This is primarily necessary when in the two-pane layout.
        if (savedInstanceState != null) {
            mCurrentPosition = savedInstanceState.getInt(ARG_POSITION);
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.article_view, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        // During startup, check if there are arguments passed to the fragment.
        // onStart is a good place to do this because the layout has already been
        // applied to the fragment at this point so we can safely call the method
        // below that sets the article text.
        Bundle args = getArguments();
        if (args != null) {
            // Set article based on argument passed in
            updateArticleView(args.getInt(ARG_POSITION));
        } else if (mCurrentPosition != -1) {
            // Set article based on saved instance state defined during onCreateView
            updateArticleView(mCurrentPosition);
        }
    }


    public void updateArticleView(int position) {
        TextView article = (TextView) getActivity().findViewById(R.id.article);
        // article.setText(Ipsum.Articles[position]);
        // article.setText("here you go");
        Context ctxt = getActivity().getApplicationContext();

        article.setText(checkWifi(ctxt));
        mCurrentPosition = position;
    }

    public static String checkWifi(Context context) {

        DBHelper myDBHelper = new DBHelper(context);

        SQLiteDatabase myDB = myDBHelper.getWritableDatabase();


        // Print out the Connected WiFi Information
        String printout = "";
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String SSID = "SSID:\n" + wifiInfo.getSSID() + "\n";
        String BSSID = "BSSID:\n" + wifiInfo.getBSSID() + "\n";
        String IPAddress = "IP Address:\n" + wifiInfo.getIpAddress() + "\n";
        String LinkSpeed = "Link Speed:\n" + wifiInfo.getLinkSpeed() + "Mbps \n";
        String MacAddress = "Mac Address:\n" + wifiInfo.getMacAddress() + "\n";
        String NetworkId = "Network ID:\n" + wifiInfo.getNetworkId() + "\n";
        String Rssi = "Rssi:\n" + wifiInfo.getRssi() + "\n";
        // String description = "Description:\n" + wifiInfo.toString() + "\n";

        SupplicantState suppState = wifiInfo.getSupplicantState();
        String networkInfo = "Supplicant State:\n" + suppState + "\n";


        // to get the Frequency
        String Frequency = "";
        int Freq = 0;
        String Capabilities = "";
        List<ScanResult> wifiList= wifiManager.getScanResults();
        for (int i = 0; i < wifiList.size(); i++) {
            String a = wifiList.get(i).BSSID;
            String b = wifiInfo.getBSSID();
            if (a.equals(b)) {
                Frequency = "Frequency:\n" + wifiList.get(i).frequency + "\n";
                Freq = wifiList.get(i).frequency;
                Capabilities = "Capabilities:\n" + wifiList.get(i).capabilities + "\n\n\n";
                break;
            }
        }


        // String Frequency = "Frequency: " + wifiInfo.getFrequency() + "\n";

//        printout = printout + SSID + BSSID + IPAddress + LinkSpeed + MacAddress
//                + NetworkId + Rssi + networkInfo + Frequency + Capabilities;

        String Rating = "";
        if (Capabilities.contains("WPA2")) {
            Rating = "WPA2: 80\n\n" +
                    "Hi The Security Score of the WiFi you are connecting to is : 80 points.";
        } else if (Capabilities.contains("WPA")) {
            Rating = "WPA: 60\n\n" +
                    "Hi The Security Score of the WiFi you are connecting to is : 60 points.\n" +
                    "Please do not use any service related to Finance while you are connecting this WiFi.";
        } else {
            Rating = "WEP: 20\n\n" +
                    "Hi The Security Score of the WiFi you are connecting to is : 20 points.\n" +
                    "Please try to avoid using this WiFi.";
        }
        printout = printout + Rating + "\n\n\n\n";

        printout = printout + "Current LinkSpeed:\n" + wifiInfo.getLinkSpeed() + "\n"
                            + "Current Rssi:\n" + wifiInfo.getRssi() + "\n"
                            + "Current Frequency:\n" + Freq + "\n\n\n";



        // Read from Database
        String[] projection = {
                myDBHelper.WIFI_COLUMN_SSID,
                myDBHelper.WIFI_COLUMN_BSSID,
                myDBHelper.WIFI_COLUMN_LINKSPEED,
                myDBHelper.WIFI_COLUMN_Rssi,
                myDBHelper.WIFI_COLUMN_FREQUENCY
        };

        String selection = myDBHelper.WIFI_COLUMN_SSID + "=?" + " and "
                         + myDBHelper.WIFI_COLUMN_BSSID + "=?";

        String[] selectionArgs = {
                wifiInfo.getSSID(),
                wifiInfo.getBSSID()
        };

        Cursor c = myDB.query(
                myDBHelper.WIFI_TABLE_NAME,     // table name
                projection,                     // columns
                selection,                      // which rows to return
                selectionArgs,
                null,                           // how to group rows
                null,                           // which row groups to include
                null,
                null                            // limit the numbers
        );

        c.moveToFirst();
        int EXY = 0;
        int EX = 0;
        int EY = 0;
        int EX2 = 0;
        int EY2 = 0;
        double correlation = 0;

        while (!c.isAfterLast()) {
            int current_LinkSpeed = c.getInt(c.getColumnIndex(myDBHelper.WIFI_COLUMN_LINKSPEED));
            int current_Rssi = c.getInt(c.getColumnIndex(myDBHelper.WIFI_COLUMN_Rssi));
            int current_Freq = c.getInt(c.getColumnIndex(myDBHelper.WIFI_COLUMN_FREQUENCY));

            EXY = EXY + current_LinkSpeed * current_Rssi;
            EX = EX + current_LinkSpeed;
            EY = EY + current_Rssi;
            EX2 = EX2 + current_LinkSpeed * current_LinkSpeed;
            EY2 = EY2 + current_Rssi * current_Rssi;

//            System.out.println("LinkSpeed:  " + c.getInt(c.getColumnIndex(myDBHelper.WIFI_COLUMN_LINKSPEED)));
//            System.out.println("Rssi:\t\t" + c.getInt(c.getColumnIndex(myDBHelper.WIFI_COLUMN_Rssi)));
//            System.out.println("Frequency:  " + c.getInt(c.getColumnIndex(myDBHelper.WIFI_COLUMN_FREQUENCY)));
//            System.out.println();

            c.moveToNext();
        }
        int N = c.getCount();


        if (N != 0) {
//            float LinkSpeed_Average = EX / N;
//            float Rssi_Average = EY / N;
//            // float Freq_Average = Freq_Sum / rowNum;
//
//            printout = printout + "History LinkSpeed:\n" + LinkSpeed_Average + "\n"
//                                + "History Rssi:\n" + Rssi_Average + "\n";
//                               // + "History Frequency:\n" + Freq_Average + "\n";
//                                //+ "Database Row Number: " + rowNum + "\n";

            correlation = (N * EXY - EX * EY) / Math.sqrt((N * EX2 - EX * EX) * (N * EY2 - EY * EY));
//
            // New Correlation
            N = N + 1;
            EXY += wifiInfo.getLinkSpeed() * wifiInfo.getRssi();
            EX += wifiInfo.getLinkSpeed();
            EY += wifiInfo.getRssi();
            EX2 += wifiInfo.getLinkSpeed() * wifiInfo.getLinkSpeed();
            EY2 += wifiInfo.getRssi() * wifiInfo.getRssi();

            double correlation2 = (N * EXY - EX * EY) / Math.sqrt((N * EX2 - EX * EX) * (N * EY2 - EY * EY));

            if ((correlation2 - correlation)  > 0.5 || (correlation - correlation2)  > 0.5) {
                printout = printout + "Warning:\n"  + "WiFi may have security problems";
            } else {
                printout = printout + "Safe WiFi Connection.";
            }




        } else {
            printout = printout + "No History Result";
        }



        // Write into Database
        ContentValues values = new ContentValues();

        values.put(myDBHelper.WIFI_COLUMN_SSID, wifiInfo.getSSID());
        values.put(myDBHelper.WIFI_COLUMN_BSSID, wifiInfo.getBSSID());
        values.put(myDBHelper.WIFI_COLUMN_LINKSPEED, wifiInfo.getLinkSpeed());
        values.put(myDBHelper.WIFI_COLUMN_Rssi, wifiInfo.getRssi());
        values.put(myDBHelper.WIFI_COLUMN_FREQUENCY, Freq);
        myDB.insert(myDBHelper.WIFI_TABLE_NAME, null, values);
        myDB.close();

        return printout;
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the current article selection in case we need to recreate the fragment
        outState.putInt(ARG_POSITION, mCurrentPosition);
    }


}