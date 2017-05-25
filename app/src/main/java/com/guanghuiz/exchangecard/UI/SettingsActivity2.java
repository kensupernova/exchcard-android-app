package com.guanghuiz.exchangecard.UI;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
//import android.preference.PreferenceFragment;
//import android.support.v4.app.FragmentTransaction;
//import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.guanghuiz.exchangecard.R;
import com.guanghuiz.exchangecard.Utils.SessionManager;
import com.guanghuiz.exchangecard.Utils.Utils;

public class SettingsActivity2 extends AppCompatActivity {
    public static final int THIS_ACTIVITY_INDEX = 3;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    private FrameLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting2);

        // set the toolbar to act as action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_setting_2);
        setSupportActionBar(toolbar);
        // back to parent acitivity button, remove title first
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        layout = (FrameLayout) findViewById(R.id.activity_setting_framelayout);

        // image button on the toolbar
        ImageButton imageButton_back = (ImageButton) findViewById(R.id.imagebutton_logout_backward);
        if (null != imageButton_back) {
            imageButton_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }


        //add fragment
        if (null == savedInstanceState) {
            //FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            SettingFragment fragment = new SettingFragment();
            transaction.add(R.id.activity_setting_framelayout, fragment, "add");
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    private static void log_out_action(final Activity context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.are_sure_to_log_out);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // create and start task
                // send the address to server
                // Utils.logout_database(context);
                SessionManager sessionManager = new SessionManager(context);
                sessionManager.logoutProfile();

                // go back to main activity
                Intent intent = new Intent(context, MainActivity.class);

                // put activity index into bundle
                Bundle bundle = new Bundle();
                bundle.putInt("JUMP_FROM_ACTIVITY", THIS_ACTIVITY_INDEX);
                bundle.putInt("ITEM_INDEX_WHEN_BACK", 2);
                bundle.putBoolean("LOG_IN_OR_NOT", false);
                bundle.putString("CURRENT_USER_NAME", "");
                bundle.putInt("CURRENT_PROFILE_ID", -1);
                intent.putExtras(bundle);

                context.startActivity(intent);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
            }
        });

        builder.create().show();
    }



    public static class SettingFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 final Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_setting2, container, false);

            final SessionManager sessionManager = new SessionManager(getActivity());


            // button for log out
            Button btn_setting_logout = (Button) rootView.findViewById(R.id.btn_settings_log_out);
            if (null != btn_setting_logout) {
                btn_setting_logout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(sessionManager.isLoggedIn()){
                            log_out_action(getActivity());
                        } else{
                            // if not logged in, show dialog and jump to log in actvity
                            Utils.showAlertRedirectLogIn(sessionManager, getActivity());
                        }

                    }
                });
            }

            // button for editting address
            Button btn_setting_edit_addr = (Button) rootView.findViewById(R.id.btn_settings_edit_addr);
            if(null != btn_setting_edit_addr){
                btn_setting_edit_addr.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(sessionManager.isLoggedIn()){
                            Intent intent = new Intent(getActivity(), EditAddressActivity.class);
                            startActivity(intent);

                        } else{
                            // if not logged in, show dialog and jump to log in actvity
                            // showAlertRedirectLogIn(sessionManager, getActivity());
                            Utils.showAlertRedirectLogIn(sessionManager, getActivity());
                        }

                    }
                });
            }

            // button clicked for general

            //            Button btn_general = (Button) rootView.findViewById(R.id.btn_setting_general);
            //            if (null != btn_general) {
            //                btn_general.setOnClickListener(new View.OnClickListener() {
            //                    @Override
            //                    public void onClick(View v) {
            //                        Intent intent = new Intent(getActivity(), SettingsActivity.class);
            //                        startActivity(intent);
            //                    }
            //                });
            //            }

            // button click to reset password
            Button btn_reset_password = (Button) rootView.findViewById(R.id.btn_settings_passworkd);
            btn_reset_password.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(sessionManager.isLoggedIn()){
                        Intent intent = new Intent(getActivity(), ResetPasswordActivity.class);
                        startActivity(intent);

                    } else{
                        // if not logged in, show dialog and jump to log in actvity
                        Utils.showAlertRedirectLogIn(sessionManager, getActivity());
                    }
                }
            });

            return rootView;
        }


    }


}
