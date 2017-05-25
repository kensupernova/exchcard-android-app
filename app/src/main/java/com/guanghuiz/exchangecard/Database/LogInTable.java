package com.guanghuiz.exchangecard.Database;

/**
 * Created by Guanghui on 8/3/16.
 */
public class LogInTable {

    public static final String DATABASE_NAME = MySQLiteHelper.DATABASE_NAME;
    public static final int DATABASE_VERSION = MySQLiteHelper.DATABASE_VERSION;

    public static final String TABLE_NAME_LOGINS = "encryptlogins";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_USERNAME = "username";

    public static final String COLUMN_LOGIN = "login";
    public static final String COLUMN_SALT = "salt";
    // logged in time in seconds
    public static final String COLUMN_LOGIN_TIME = "login_time";
    public static final String COLUMN_EXPIRE = "expire";

    public static final String DATABASE_CREATE = "CREATE TABLE "
            + TABLE_NAME_LOGINS +"("+COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USERNAME + " TEXT NOT NULL, "
            + COLUMN_SALT + " TEXT NOT NULL, "
            + COLUMN_LOGIN + " TEXT NOT NULL, "
            + COLUMN_LOGIN_TIME + " BIGINTEGER DEFAULT 0, "
            + COLUMN_EXPIRE + " BIGINTEGER DEFAULT 0 " + ");";
}
