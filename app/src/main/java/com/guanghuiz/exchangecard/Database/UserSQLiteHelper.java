package com.guanghuiz.exchangecard.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.guanghuiz.exchangecard.Database.model.MailAddress;
import com.guanghuiz.exchangecard.Database.model.User;

/**
 * Created by Guanghui on 28/2/16.
 */
public class UserSQLiteHelper {

    private SQLiteDatabase database;
    private MySQLiteHelper mySQLiteHelper;


    public UserSQLiteHelper(Context context){
        mySQLiteHelper = new MySQLiteHelper(context);
        database = mySQLiteHelper.getWritableDatabase();
    }

    public void open() throws SQLException {
        database = mySQLiteHelper.getWritableDatabase();
    }

    public void close(){

        mySQLiteHelper.close();
    }


    public long insert( String username, String userpassword_h, String email,
                       String name, String address, String postcode){
        SQLiteDatabase db = mySQLiteHelper.getWritableDatabase();

        // store the information to content values
        ContentValues values = new ContentValues();
        // not profile id
        values.put(UserTable.COLUMN_USERNAME, username);


        values.put(UserTable.COLUMN_PASSWORD_HASHED, userpassword_h);
        values.put(UserTable.COLUMN_EMAIL, email);

        values.put(UserTable.COLUMN_NAME, name);
        values.put(UserTable.COLUMN_ADDRESS, address);
        values.put(UserTable.COLUMN_POSTCODE, postcode);

        // insert the content values into db
        long insertID = db.insert(UserTable.TABLE_NAME_USERS, null, values);

        db.close();
        this.close();
        return insertID;
    }

    public long insert(int profile_id, String username, String userpassword_h, String email,
                       String name, String address, String postcode){
        SQLiteDatabase db = mySQLiteHelper.getWritableDatabase();

        // store the information to content values
        ContentValues values = new ContentValues();
        values.put(UserTable.COLUMN_PROFILE_ID, profile_id);
        values.put(UserTable.COLUMN_USERNAME, username);


        values.put(UserTable.COLUMN_PASSWORD_HASHED, userpassword_h);
        values.put(UserTable.COLUMN_EMAIL, email);

        values.put(UserTable.COLUMN_NAME, name);
        values.put(UserTable.COLUMN_ADDRESS, address);
        values.put(UserTable.COLUMN_POSTCODE, postcode);

        // insert the content values into db
        long insertID = db.insert(UserTable.TABLE_NAME_USERS, null, values);

        db.close();
        this.close();
        return insertID;
    }

    public boolean enthenticate(String username, String password_h){
        SQLiteDatabase db = mySQLiteHelper.getReadableDatabase();

        // get the data with the username
        Cursor c = db.rawQuery("SELECT password_hashed FROM users WHERE username = '" +username +"'", null);

        if( null == c ){
            return false;
        } if (c.getCount()<=0){
            return false;
        }

        c.moveToFirst();
        // get the hashed pasword
        String valid_pw = c.getString(c.getColumnIndexOrThrow(UserTable.COLUMN_PASSWORD_HASHED));

        this.close();
        // compare the hashed password with
        return valid_pw.equals(password_h);


    }

    public String getHashedPassword(String username){
        SQLiteDatabase db = mySQLiteHelper.getReadableDatabase();

        String[] tableColumns = new String[]{UserTable.COLUMN_USERNAME,
                UserTable.COLUMN_PASSWORD_HASHED};
        String whereClause = UserTable.COLUMN_USERNAME +" = ?";
        String[] whereArgs = new String[]{username};
        String orderBy = UserTable.COLUMN_USERNAME;

        Cursor cursor = db.query(UserTable.TABLE_NAME_USERS,
                new String[]{UserTable.COLUMN_USERNAME, UserTable.COLUMN_PASSWORD_HASHED},
                UserTable.COLUMN_USERNAME +" = ?", new String[]{username}, null, null, null);

        cursor.moveToFirst();

        if(null == cursor){
            return null;
        }

        if(cursor.getCount()<=0){
            return null;
        }



        String pw = cursor.getString(
                cursor.getColumnIndexOrThrow(UserTable.COLUMN_PASSWORD_HASHED));
        db.close();
        this.close();
        return pw;
    }

    public MailAddress getMailAddress(String username){

        SQLiteDatabase db = mySQLiteHelper.getReadableDatabase();

        String[] tableColumns = new String[]{UserTable.COLUMN_USERNAME,
                UserTable.COLUMN_NAME, UserTable.COLUMN_ADDRESS,
                UserTable.COLUMN_POSTCODE};
        String whereClause = UserTable.COLUMN_USERNAME +" = ?";
        String[] whereArgs = new String[]{username};
        String orderBy = UserTable.COLUMN_USERNAME;

        Cursor cursor = db.query(UserTable.TABLE_NAME_USERS,
                tableColumns,
                whereClause,
                whereArgs, null, null, orderBy);

        cursor.moveToFirst();


        if(cursor.getCount()<=0){
            return null;
        }

        MailAddress address;
        address = new MailAddress(
                cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COLUMN_ADDRESS)),
                cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COLUMN_POSTCODE))
        );

        db.close();
        this.close();
        return address;

    }

    public int getProfileId(String username){
        SQLiteDatabase db = mySQLiteHelper.getReadableDatabase();

        String[] tableColumns = new String[]{UserTable.COLUMN_PROFILE_ID};
        String whereClause = UserTable.COLUMN_USERNAME +" = ?";
        String[] whereArgs = new String[]{username};

        Cursor cursor = db.query(UserTable.TABLE_NAME_USERS,
                tableColumns, whereClause, whereArgs,
                null, null, null);

        cursor.moveToFirst();

        // if null, return 0
        if(cursor.getCount()<=0){
            return -1;
        }

        int profile_id;

        try {
            profile_id = cursor.getInt(cursor.getColumnIndexOrThrow(UserTable.COLUMN_PROFILE_ID));

        } catch(Exception e){
            return -1;
        }

        db.close();
        this.close();
        return profile_id;

    }

    // get the user object by username
    public User getUser(String username){
        SQLiteDatabase db = mySQLiteHelper.getReadableDatabase();

        String[] tableColumns = new String[]{
                UserTable.COLUMN_USERNAME,
                UserTable.COLUMN_PASSWORD_HASHED,
                UserTable.COLUMN_EMAIL};
        String whereClause = UserTable.COLUMN_USERNAME +" = ?";
        String[] whereArgs = new String[]{username};
        String orderBy = UserTable.COLUMN_USERNAME;

        Cursor cursor = db.query(UserTable.TABLE_NAME_USERS,
                tableColumns,
                whereClause, whereArgs, null, null, orderBy);

        cursor.moveToFirst();

        if(cursor.getCount()<=0){
            return null;
        }


        User user = new User(
                cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COLUMN_USERNAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COLUMN_PASSWORD_HASHED))
        );

        return user;
    }

    public boolean updateorinsertMailAddress(int profile_id,
                                             String username,
                                             String pw,
                                             MailAddress mailAddress){
        Log.i("User SQLITE", "update or insert mail address....");
        SQLiteDatabase db = mySQLiteHelper.getWritableDatabase();

        String[] tableColumns = new String[]{
                UserTable.COLUMN_USERNAME,
                UserTable.COLUMN_PASSWORD_HASHED,
                UserTable.COLUMN_EMAIL};

        String whereClause = UserTable.COLUMN_USERNAME +" = ?";
        String[] whereArgs = new String[]{username};

        try{
            ContentValues values =new ContentValues();
            values.put(UserTable.COLUMN_PROFILE_ID, profile_id);
            values.put(UserTable.COLUMN_NAME, mailAddress.getName());
            values.put(UserTable.COLUMN_ADDRESS, mailAddress.getAddress());
            values.put(UserTable.COLUMN_POSTCODE, mailAddress.getPostcode());

            // check whether the mailaddres of the user already exist or not,
            // if not, insert int
            Cursor cursor = db.query(UserTable.TABLE_NAME_USERS, tableColumns, whereClause, whereArgs, null, null, null);
            if(cursor.getCount()<=0){
                values.put(UserTable.COLUMN_USERNAME, username);
                values.put(UserTable.COLUMN_PASSWORD_HASHED, pw);
                values.put(UserTable.COLUMN_EMAIL, username);
                db.insert(UserTable.TABLE_NAME_USERS, null, values);
                Log.i("User SQLITE", "insert mail address");
            } else {

                db.update(UserTable.TABLE_NAME_USERS, values, whereClause, whereArgs);
                Log.i("User SQLITE", "update mail address");
            }

            // try to get
            Log.i("User SQLITE", ""+this.getMailAddress(username));

        } catch (Exception e){
            db.close();
            return false;
        }

        db.close();

        return true;

    }
}
