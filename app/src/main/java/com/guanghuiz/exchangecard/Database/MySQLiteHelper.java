package com.guanghuiz.exchangecard.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.guanghuiz.exchangecard.UI.MainActivity;

/**
 * Created by Guanghui on 28/2/16.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "exchangecard.db";
    public static final int DATABASE_VERSION = 1;


    public MySQLiteHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(UserTable.DATABASE_CREATE);
        db.execSQL(LogInTable.DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MainActivity.TAG,
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + UserTable.TABLE_NAME_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + LogInTable.TABLE_NAME_LOGINS);
        onCreate(db);
    }

}
