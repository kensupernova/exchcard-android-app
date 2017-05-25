package com.guanghuiz.exchangecard.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.guanghuiz.exchangecard.UI.LoginActivity;
import com.guanghuiz.exchangecard.UI.SettingsActivity2;

import java.util.HashMap;

/**
 * Created by Guanghui on 21/3/16.
 */
public class SessionManager {

    public static final String PREFS_NAME= "SessionPrefsFile";
    private static String prefs_of_user;

    //shared perferences
    SharedPreferences pref;

    // editor for shared preferences
    SharedPreferences.Editor editor;

    //Context
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // shared preferences keys
    private static final String KEY_IS_LOGIN ="IsLogIn";

    public static final String KEY_USER_NAME = "user_name";

    public static final String KEY_PROFILE_ID = "profile_id";

    public static final String KEY_CURRENT_TAB = "current_tab";

    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREFS_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createdLoginSession(String user_name, int profile_id){
        editor.putBoolean(KEY_IS_LOGIN, true);
        editor.putString(KEY_USER_NAME, user_name);
        editor.putInt(KEY_PROFILE_ID, profile_id);

        // stope log in token with expire time in the database
        Utils.login_database(_context, user_name);
        // commit changes
        editor.commit();
    }

    public int getProfileId(){
        // isLogInOrRedirect();
        return pref.getInt(KEY_PROFILE_ID, -1);
    }

    public String getUserName(){
        // isLogInOrRedirect();
        return pref.getString(KEY_USER_NAME, null);
    }

    /**
     * username stamp is username string without @ .
     * @return
     */

    public String getUserNameStamp(){

        String username= pref.getString(KEY_USER_NAME, null);
        String usernamestamp = username.replace("@", "_");
        usernamestamp = usernamestamp.replace(".", "_");

        return usernamestamp;
    }


    public void isLogInOrRedirect(){
        if(!this.isLoggedIn()){
            // if user is not logged in redirect him to log in activity
            Intent i = new Intent(_context, LoginActivity.class);

            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


            // Staring Login Activity
            _context.startActivity(i);
        }
    }


    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGIN, false);
    }

    // log out user

    public void logoutProfile(){
        // log out in the database
        Utils.logout_database(_context);
        // clean up in session manager
        editor.clear();
        editor.commit();
    }

    public void sessionCleanUp(){
        if(null == getUserName() || -1 ==getProfileId()
                || !isLoggedIn()){
            this.logoutProfile();
        }
    }

    public void redirectToLoginActivity(){
        Intent i = new Intent(_context, LoginActivity.class);

        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


        // Staring Login Activity
        _context.startActivity(i);
    }

    public void updateLogIn(Boolean isLogin){

        // if log in is false, log out
        if(!isLogin){
            this.logoutProfile();
        }

    }

    public void putCurrentTab(int index){
        editor.putInt(KEY_CURRENT_TAB, index);
    }

    public int getCurrentTAB(int index){
        return pref.getInt(KEY_CURRENT_TAB, 0);
    }

    public String getUserPrefsName(){
        if(isLoggedIn()) {
            return "prefs_of_user_" + this.getUserNameStamp();
        }

        return null;
    }



}
