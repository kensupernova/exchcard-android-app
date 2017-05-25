package com.guanghuiz.exchangecard.Database;

/**
 * Created by Guanghui on 13/3/16.
 */
public class CardCountTable {

    public static final String TABLE_NAME_USERS = "card_counts";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_CARD_NAME = "card_name";

    public static final String COLUMN_SENDER_ID = "sender_id";

    public static final String COLUMN_RECIPIENT_ID= "recipient_profile_id";
    public static final String COLUMN_SENT_TIME ="sent_time";

    public static final String DATABASE_CREATE = "CREATE TABLE "
            + TABLE_NAME_USERS +"("+ COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_CARD_NAME +" TEXT NOT NULL, "
            + COLUMN_RECIPIENT_ID +" INTEGER NOT NULL, "
            + COLUMN_SENDER_ID +" INTEGER NOT NULL ,"
            + COLUMN_SENT_TIME +" BIGINTEGER NOT NULL "
            +");";
}
