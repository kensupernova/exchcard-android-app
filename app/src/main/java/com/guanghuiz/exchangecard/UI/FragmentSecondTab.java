package com.guanghuiz.exchangecard.UI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.guanghuiz.exchangecard.R;
import com.guanghuiz.exchangecard.SendReceiveApi.ServiceApi;
import com.guanghuiz.exchangecard.SendReceiveApi.ServiceGenerator;
import com.guanghuiz.exchangecard.Utils.SessionManager;
import com.guanghuiz.exchangecard.Utils.Updateable;
import com.guanghuiz.exchangecard.Utils.Utils;
import com.guanghuiz.exchangecard.Database.UserSQLiteHelper;
import com.guanghuiz.exchangecard.Database.model.CardCount;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Guanghui on 16/2/16.
 */
public class FragmentSecondTab extends Fragment implements Updateable {
    public static final String CURRENT_USER = "current_user";
    public static final String LOG_IN = "log_in";
    public static final String CURRENT_PROFILE = "profile_id";
    private static final String TAG = "SecondTab";


    private String current_username;
    private int current_profile_id;
    private Boolean log_in_or_not;

    private static final int signin_request_code = 123;
    private static final int setting_request_code = 124;
    private static final int profile_request_code = 12;

    private TextView textView_is_log_in;
    private TextView textView_username;
    private ImageView imageButton_head;

    private Button btn_sent_cards_arrived;
    private Button btn_sent_cards_travelling;

    private Button btn_received_cards_arrived;
    private Button btn_received_cards_travelling;

    private CardCount cardCount;

    private ViewGroup fragment_second_tab_container;

    private String prefs_of_user;

    private SessionManager sessionManager;
    private Context this_context;


    public FragmentSecondTab() {
    }

    public static FragmentSecondTab newInstance(String current_username, int profile_id, boolean log_in_or_not) {
        FragmentSecondTab fragment = new FragmentSecondTab();
        Bundle args = new Bundle();
        args.putString(CURRENT_USER, current_username);
        args.putBoolean(LOG_IN, log_in_or_not);
        args.putInt(CURRENT_PROFILE, profile_id);
        // add bundle data to fragment
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // set session manager
        sessionManager = new SessionManager(this.getActivity());
        this_context = this.getActivity();
        prefs_of_user = sessionManager.getUserPrefsName();

        // Log.i(MainActivity.TAG, "fragment profile created");

        Bundle arg = getArguments();

        // get username, profile id, is log in from intent arguments
        current_username = arg.getString(CURRENT_USER);
        current_profile_id = arg.getInt(CURRENT_PROFILE);
        log_in_or_not = arg.getBoolean(LOG_IN);

        // then add the data to the fragment view
        View rootView = inflater.inflate(R.layout.fragment_page_second_tab, container, false);

        // set the head icon
        imageButton_head =(ImageView) rootView.findViewById(R.id.imageButton_avatar);
        imageButton_head.setOnClickListener(login_click);

        // setting button
        Button btn_setting = (Button) rootView.findViewById(R.id.btn_setting);
        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(FragmentSecondTab.this.getActivity(), SettingsActivity2.class);
                startActivityForResult(intent, setting_request_code);
            }
        });

        textView_username = (TextView) rootView.findViewById(R.id.textView_username);

        // get textview is log in
        textView_is_log_in = (TextView) rootView.findViewById(R.id.textView_is_log_in);
        if(log_in_or_not){
            textView_is_log_in.setText(R.string.already_loged_in);
            textView_username.setText(current_username);
            // when logged in already, set imageButton_head unclickable
            // later on, when click, other actions

            // if log in, set avatar photo if possible, the avatar photo path is in
            // internal app path
            //String prefs_of_user ="prefs_of_user_"+
            //        new SessionManager(this.getActivity()).getUserName();

            if(null !=prefs_of_user) {

                SharedPreferences settings = this.getActivity().getSharedPreferences(prefs_of_user, 0);
                String avatar_path = settings.getString("key_avatar_path", "");

                setAvatarPhoto(avatar_path);
            }

            imageButton_head.setClickable(false);
        } else{
            textView_is_log_in.setText(R.string.not_log_in);
            textView_username.setText(getResources().getText(R.string.empty_string));
        }

        // cards count views

        btn_sent_cards_arrived= (Button) rootView.findViewById(R.id.btn_sent_cards_arrived_value);
        btn_sent_cards_travelling= (Button) rootView.findViewById(R.id.btn_sent_cards_travelling_value);

        btn_received_cards_arrived= (Button) rootView.findViewById(R.id.btn_received_cards_arrived_value);
        btn_received_cards_travelling= (Button) rootView.findViewById(R.id.btn_received_cards_travelling_value);

        // set views from prefs, if return false
        if(btn_sent_cards_arrived != null && btn_sent_cards_travelling != null
                && btn_received_cards_arrived!= null && btn_received_cards_travelling!= null) {

            // if logged in, set card cound views
            if (log_in_or_not) {
                if(!setCardCountViewsFromPref()) {
                    getCardsCountFromServer(current_username, current_profile_id);
                }
            }
        }


        // set onclick listener for text view personal profile
        TextView textview_personal_profile = (TextView) rootView.findViewById(R.id.textview_personal_profile);
        textview_personal_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jump_to_profile_activity();
            }
        });


        fragment_second_tab_container = (ViewGroup) rootView;
        return rootView;


    }

    /**
     * OnClickListener for starting log in activity
     */
    View.OnClickListener login_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // jump to LoginActivity
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            // startActivity(intent);
            startActivityForResult(intent, signin_request_code);

        }
    };

    @Override
    public void update() {

    }

    // get result from log in activity
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == signin_request_code) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK) {
                // Do something with the contact here (bigger example below)
                Bundle bundle = data.getExtras();
                String username = bundle.getString("CURRENT_USER");
                textView_is_log_in.setText(R.string.already_loged_in);
                textView_username.setText(username);
                imageButton_head.setClickable(false);

                // set card count views after successfully log in
                setCardCountViewsFromPref();

                // every time, successfully log in, download the lastest card counts from server
                getCardsCountFromServer(current_username, current_profile_id);


            }
        }else if(requestCode == profile_request_code){
            if(resultCode == Activity.RESULT_OK){

            }
        }
    }

    public CardCount getCardsCountFromServer(String current_username, int current_profile_id){

        Log.i("SecontTag", "get cards from server, downloading!");

        final CardCount[] counts = {null};
        if(current_profile_id != -1 && MainActivity.is_login_expire_db(getActivity())){
            UserSQLiteHelper helper = new UserSQLiteHelper(getActivity());

            ServiceApi api = ServiceGenerator.createService(ServiceApi.class,
                    current_username, helper.getHashedPassword(current_username));

            Call<CardCount> call = api.getCardCount(current_profile_id);

            call.enqueue(new Callback<CardCount>() {
                @Override
                public void onResponse(Call<CardCount> call, Response<CardCount> response) {
                    int code = response.code();
                    CardCount cardCount = response.body();
                    if (cardCount!=null){
                        Log.i(MainActivity.TAG, "cardscount from server, "+cardCount.toString());
                        // saveCardCount2Prefs(cardCount);
                        counts[0] = cardCount;
                        setCardCountViews(counts[0]);
                        //// save card count of the profiler into preferences files
                        saveCardCount2Prefs(counts[0]);

                    }

                }

                @Override
                public void onFailure(Call<CardCount> call, Throwable t) {
                    Log.i(MainActivity.TAG, "getcardscount, all cards: " + call.toString());
                    Log.i(MainActivity.TAG, "getcardscount, all cards: "+ t.toString());

                }
            });
        }


        return counts[0];

    }


    public void saveCardCount2Prefs(CardCount cardCount){
        if(cardCount!=null && null != prefs_of_user) {

            prefs_of_user = sessionManager.getUserPrefsName();
            Log.i(TAG, "saving card count to pref: " +prefs_of_user);
            if(null!= prefs_of_user) {

                SharedPreferences settings = this_context.getSharedPreferences(prefs_of_user, 0);
                SharedPreferences.Editor editor = settings.edit();

                editor.putInt("key_sent_cards_total", cardCount.sent_cards.total);
                editor.putInt("key_sent_cards_arrived", cardCount.sent_cards.arrived);
                editor.putInt("key_sent_cards_travelling", cardCount.sent_cards.travelling);

                editor.putInt("key_received_cards_total", cardCount.received_cards.total);
                editor.putInt("key_received_cards_arrived", cardCount.received_cards.arrived);
                editor.putInt("key_received_cards_travelling", cardCount.received_cards.travelling);

                // Commit the edits!!!!!!!!!!!!
                editor.commit();
            }
        }

    }

    /**
     * set views from data stored from Pref
     */
    public boolean setCardCountViewsFromPref(){

        prefs_of_user = sessionManager.getUserPrefsName();

        Log.i(TAG, "set cards from prefs file "+prefs_of_user);
        if(null != prefs_of_user) {

            SharedPreferences settings = this_context.getSharedPreferences(prefs_of_user, 0);

            int sent_cards_arrived = settings.getInt("key_sent_cards_arrived", 0);
            int sent_cards_travelling = settings.getInt("key_sent_cards_travelling", 0);
            int received_cards_arrived = settings.getInt("key_received_cards_arrived", 0);
            int received_cards_travelling = settings.getInt("key_received_cards_travelling", 0);

            Log.i(TAG, "card counts from prefs: " + sent_cards_arrived + "," +
                    sent_cards_travelling + "," +
                    received_cards_arrived + "," +
                    received_cards_travelling + ",");

            btn_sent_cards_arrived.setText("" + sent_cards_arrived);
            btn_sent_cards_travelling.setText("" + sent_cards_travelling);

            btn_received_cards_arrived.setText("" + received_cards_arrived);
            btn_received_cards_travelling.setText("" + received_cards_travelling);
        }

        return false;

    }

    public void setCardCountViews(CardCount cardCount){

        btn_sent_cards_arrived.setText(""+cardCount.getSentCards().arrived);
        btn_sent_cards_travelling.setText(""+cardCount.getSentCards().travelling);

        btn_received_cards_arrived.setText(""+cardCount.getReceivedCards().arrived);
        btn_received_cards_travelling.setText(""+cardCount.getReceivedCards().travelling);

    }





    /**
     * jump from main activity to Profile Activity, which will help edit avatar photo
     * describe one self, education, city, etc.
     * upload address and password is in setting activity 2
     */
    public void jump_to_profile_activity(){
        SessionManager sessionManager = new SessionManager(this.getActivity());
        if(sessionManager.isLoggedIn()) {
            Intent intent = new Intent(this.getActivity(), ProfileActivity.class);
            this.startActivityForResult(intent, profile_request_code);
        }else{
            Utils.showAlertRedirectLogIn(sessionManager, this.getActivity());
        }
    }


    /**
     *
     * @param avatar_path
     */
    private void setAvatarPhoto(String avatar_path){
        Log.i(TAG, "SETTING AVATAR PHOTO " + avatar_path +", absolute path");

        if(""!=avatar_path && !avatar_path.isEmpty()) {


            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds =false;
            bmOptions.inSampleSize = 1;
            Bitmap bitmap = BitmapFactory.decodeFile(avatar_path, bmOptions);
            imageButton_head.setImageBitmap(bitmap);
        }
    }

}