package com.guanghuiz.exchangecard.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guanghui on 28/2/16.
 */
public class LogInSQLiteHelper {

    private SQLiteDatabase database;
    private MySQLiteHelper mySQLiteHelper;


    public LogInSQLiteHelper(Context context){
        mySQLiteHelper = new MySQLiteHelper(context);

        database = mySQLiteHelper.getWritableDatabase();
    }

    public void open() throws SQLException {
        database = mySQLiteHelper.getWritableDatabase();
    }

    public void close(){

        mySQLiteHelper.close();
    }

    public void insert(String username, String salt, String login, long login_time, long expireTime){
        // store the token into database

        SQLiteDatabase db = mySQLiteHelper.getWritableDatabase();

        if(null != username && null != salt && null!= login
                && 0!= login_time && 0!= expireTime) {

            ContentValues values = new ContentValues();
            values.put(LogInTable.COLUMN_USERNAME, username);
            values.put(LogInTable.COLUMN_SALT, salt);

            values.put(LogInTable.COLUMN_LOGIN, login);

            values.put(LogInTable.COLUMN_LOGIN_TIME, login_time);
            values.put(LogInTable.COLUMN_EXPIRE, expireTime);

            db.insert(LogInTable.TABLE_NAME_LOGINS,
                    null, values);

            db.close();
            this.close();
        }

    }

    public List<String> listAllLogins(){
        List<String> results = new ArrayList<>();
        SQLiteDatabase db = mySQLiteHelper.getReadableDatabase();

        Cursor cursor = db.query(LogInTable.TABLE_NAME_LOGINS, new String[]{
            LogInTable.COLUMN_USERNAME, LogInTable.COLUMN_EXPIRE, LogInTable.COLUMN_LOGIN},
                null, null, null, null, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()&& cursor.getCount()>0){
            results.add(cursorToData(cursor));
            cursor.moveToNext();
        }

        cursor.close();
        db.close();
        this.close();

        return results;

    }

    public String cursorToData(Cursor cursor){
        return cursor.getString(cursor.getColumnIndexOrThrow(LogInTable.COLUMN_USERNAME)) +
                " " + cursor.getString(cursor.getColumnIndexOrThrow(LogInTable.COLUMN_EXPIRE)) +
               " "+ cursor.getString(cursor.getColumnIndexOrThrow(LogInTable.COLUMN_LOGIN_TIME));


    }

    public Boolean isLoginExpire(){
        // get the current time in seconds
        long now_in_seconds = System.currentTimeMillis()/1000;
        SQLiteDatabase db = mySQLiteHelper.getReadableDatabase();

        //        String LATEST_LOGIN_SQL = "SELECT * FROM "+ LogInTable.TABLE_NAME_LOGINS + " WHERE "+
        //                LogInTable.COLUMN_EXPIRE +" >= " + now_in_seconds +
        //                " ORDER BY "+LogInTable.COLUMN_LOGIN_TIME+ " DESC LIMIT 1;";
        //
        //        Cursor cursor = db.rawQuery(LATEST_LOGIN_SQL, null);


        String[] tableColumns = new String[]{LogInTable.COLUMN_EXPIRE};
        String whereClause = LogInTable.COLUMN_EXPIRE +">=?";
        String[] whereArgs = new String[]{""+now_in_seconds};

        Cursor cursor = db.query(LogInTable.TABLE_NAME_LOGINS,
                tableColumns, whereClause, whereArgs,
                null, null, null);

        cursor.moveToFirst();

        if (cursor.getCount()<=0){
            return false;
        }

        int expire = cursor.getInt(cursor.getColumnIndex(LogInTable.COLUMN_EXPIRE));

        // Log.i("LOG IN ", "now in seconds:" + now_in_seconds +", expire in seconds:"+expire);

        db.close();
        this.close();


        if(expire >= now_in_seconds){
            return true;
        }

        return false;

    }
}
