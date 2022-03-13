package com.example.sheedah;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RecentTransactionDb extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "recentTransaction.db";

    //command for initaiting and creating database table layout
    private static final String SQL_CREATE_ENTRIES ="CREATE TABLE " + SheedahLocalDatabaseContract.RecentTransactions.TABLE_NAME + " (" +
            SheedahLocalDatabaseContract.RecentTransactions._ID + " INTEGER PRIMARY KEY," +
            SheedahLocalDatabaseContract.RecentTransactions.COLUMN_NAME_1 + " TEXT," +
            SheedahLocalDatabaseContract.RecentTransactions.COLUMN_NAME_2+ " TEXT," +
            SheedahLocalDatabaseContract.RecentTransactions.COLUMN_NAME_3+ " TEXT," +
            SheedahLocalDatabaseContract.RecentTransactions.COLUMN_NAME_4+ " TEXT,"+
            SheedahLocalDatabaseContract.RecentTransactions.COLUMN_NAME_5+ " TEXT)";

    //command for deleting database table content on upgrading database
    private static final String SQL_DELETE_ENTRIES ="DROP TABLE IF EXISTS " + SheedahLocalDatabaseContract.RecentTransactions.TABLE_NAME;


    public RecentTransactionDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for recent transaction performed by an individual saler, so its upgrade policy is
        // to simply discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}

