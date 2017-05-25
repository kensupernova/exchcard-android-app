package com.guanghuiz.exchangecard.Database;

/**
 * Created by Guanghui on 8/3/16.
 */
public class UserTable {

    public static final String TABLE_NAME_USERS = "users";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PROFILE_ID = "profile_id";

    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD_HASHED = "password_hashed";
    public static final String COLUMN_EMAIL = "email";

    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_POSTCODE = "postcode";

    public static final String DATABASE_CREATE = "CREATE TABLE "
            + TABLE_NAME_USERS +"("+COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_PROFILE_ID+" INTEGER NULL,"
            + COLUMN_USERNAME +" TEXT NOT NULL, "
            + COLUMN_PASSWORD_HASHED +" TEXT NOT NULL, "
            + COLUMN_EMAIL +" TEXT NOT NULL, "
            + COLUMN_NAME +" TEXT NOT NULL, "
            + COLUMN_ADDRESS + " TEXT NOT NULL, "
            + COLUMN_POSTCODE + " TEXT NOT NULL" +");";
}
