package com.example.android.fragments;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
* Created by D_shaw on 3/26/15.
*/


// Defines table name and column names for single table
public class DBHelper extends SQLiteOpenHelper implements BaseColumns{


    // Defines table contents
        public static final String DATABASE_NAME = "WiFiDB.db";
        public static final String WIFI_TABLE_NAME = "WiFiInfoTable";
        public static final String WIFI_COLUMN_SSID = "SSID";
        public static final String WIFI_COLUMN_BSSID = "BSSID";
        public static final String WIFI_COLUMN_LINKSPEED = "LinkSpeed";
        public static final String WIFI_COLUMN_Rssi = "Rssi";
        public static final String WIFI_COLUMN_FREQUENCY = "Frequency";

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + WIFI_TABLE_NAME + " ("
            + DBHelper._ID + " INTEGER PRIMARY KEY,"
            + WIFI_COLUMN_SSID + " TEXT,"
            + WIFI_COLUMN_BSSID + " TEXT,"
            + WIFI_COLUMN_LINKSPEED + " INTEGER,"
            + WIFI_COLUMN_Rssi + " INTEGER,"
            + WIFI_COLUMN_FREQUENCY + " INTEGER" + ")";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + WIFI_TABLE_NAME;


    public static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // downgrade policy
    }
}

