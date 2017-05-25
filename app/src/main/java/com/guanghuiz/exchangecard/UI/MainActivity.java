package com.guanghuiz.exchangecard.UI;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.widget.ImageButton;

import com.guanghuiz.exchangecard.R;
import com.guanghuiz.exchangecard.Utils.MyPagerAdapter;
import com.guanghuiz.exchangecard.Utils.SessionManager;
import com.guanghuiz.exchangecard.Database.LogInSQLiteHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "exchangecard";

    public static final String PREFS_NAME = "ProfilePrefsFile";
    //    public static final String key_current_log_in_username = "key_current_log_in_username";
    //    public static final String key_current_log_in_profile_id ="key_current_log_in_profile_id";
    //    public static final String key_current_is_log_in ="key_current_is_log_in";

    public static SessionManager sessionManager;


    //viewpager and view adaper
    ViewPager mViewPager;
    MyPagerAdapter mPagerAdapter;

    private List<Fragment> fragments;
    private TabLayout tabLayout;
    private static String current_username;
    private static int current_profile_id;
    private static boolean log_in_or_not;

    public static String POSITION = "POSITION";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);

        // set the toolbar to act as action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_send_card);
        setSupportActionBar(toolbar);

        mViewPager = (ViewPager) findViewById(R.id.pager);

        // tablayout are connected with view pager
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        // check the latest encrypted login
        // update in preferences

        /*
        // current profile information are stored in preference
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        current_username = settings.getString(MainActivity.key_current_log_in_username, "none");
        current_profile_id = settings.getInt(MainActivity.key_current_log_in_profile_id, -1);
        log_in_or_not = settings.getBoolean(MainActivity.key_current_is_log_in, false);
        */

        // clean up sessions
        sessionManager.sessionCleanUp();
        // update log in information by checking whether expire
        is_login_expire_db(this);

        current_username = sessionManager.getUserName();
        current_profile_id = sessionManager.getProfileId();
        log_in_or_not = sessionManager.isLoggedIn();

        //        Log.i(TAG, "Main Activity "+"current user name:" + current_username);
        //        Log.i(TAG, "Main Activity "+"current profile id:" + current_profile_id);
        //        Log.i(TAG, "Main Activity "+"log in or not:" + log_in_or_not);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        // each page has one fragment
        fragments = getFragments(current_username, current_profile_id, log_in_or_not);

        // create dapters
        String[ ]tabs = {
                getResources().getString(R.string.tab_1_title),
                getResources().getString(R.string.tab_2_title)
        };
        mPagerAdapter = new MyPagerAdapter(tabs, getSupportFragmentManager(), fragments);

        // set adapters to viewpager
        mViewPager.setAdapter(mPagerAdapter);

        // refresh the view pager
        mPagerAdapter.getItemPosition(0);

        // set view pager to the tablayout
        tabLayout.setupWithViewPager(mViewPager);


        // jump to the item on the viewpager
        if (bundle!= null){
            int jump_from_activity_index = bundle.getInt("JUMP_FROM_ACTIVITY");

            if (jump_from_activity_index == LoginActivity.THIS_ACTIVITY_INDEX
                    || jump_from_activity_index == SignUpActivity.THIS_ACTIVITY_INDEX
                    || jump_from_activity_index ==SettingsActivity2.THIS_ACTIVITY_INDEX
                    || jump_from_activity_index == ProfileActivity.THIS_ACTIVITY_INDEX) {
                // get the item index from bundle
                int item_index = bundle.getInt("ITEM_INDEX_WHEN_BACK");
                mViewPager.setCurrentItem(item_index);
            }
        }

        // cross button on the tool bar
        ImageButton btn_cross = (ImageButton) findViewById(R.id.btn_cross_on_toolbar);
        if(null!= btn_cross) {
            btn_cross.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    action_cross();
                }
            });
        }

    }

    /*
     *This method is called more frequent than onCreate()
     */
    @Override
    protected void onResume(){
        super.onResume();

        // Log.i("Main Activity", "on resume");

    }

    @Override
    protected void onStop(){
        super.onStop();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(POSITION, tabLayout.getSelectedTabPosition());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mViewPager.setCurrentItem(savedInstanceState.getInt(POSITION));
    }

    private  List<Fragment> getFragments(String username, int profile_id, Boolean islogin){
        // Log.i("main activity", "generate new fragments");
        List<Fragment> flist = new ArrayList<>();

        flist.add(FragmentFirstTab.newInstance("Send or receive"));
        flist.add(FragmentSecondTab.newInstance(username, profile_id, islogin));

        return flist;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_cross:
                action_cross();
                break;
            default:
                break;


        }

        return super.onOptionsItemSelected(item);
    }

    // check whether login information is still valid
    public static boolean is_login_expire_db(Context context){
        // check the token stored into database
        LogInSQLiteHelper loginhelper = new LogInSQLiteHelper(context);
        // Log.i(TAG, loginhelper.listAllLogins().toString());

        Boolean isLoginExpire = loginhelper.isLoginExpire();

        /*
        // store the log in information in preference

        SharedPreferences sharedPreferences = context.getSharedPreferences(MainActivity.PREFS_NAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(MainActivity.key_current_is_log_in, isLogin);
        editor.commit();
        */

        // using SessionManager To manage
        // it could expire, or have not logged in
        sessionManager.updateLogIn(isLoginExpire);

        return isLoginExpire;

    }

    public static void action_cross(){

    }


}
