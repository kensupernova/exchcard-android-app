package com.guanghuiz.exchangecard.UI;

import android.app.ProgressDialog;
import android.content.Intent;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;


import java.security.NoSuchAlgorithmException;

import com.guanghuiz.exchangecard.R;
import com.guanghuiz.exchangecard.SendReceiveApi.ServiceApi;
import com.guanghuiz.exchangecard.SendReceiveApi.ServiceGenerator;
import com.guanghuiz.exchangecard.Utils.SessionManager;
import com.guanghuiz.exchangecard.Utils.Utils;
import com.guanghuiz.exchangecard.Database.UserSQLiteHelper;
import com.guanghuiz.exchangecard.Database.model.ServerAuthResponse;
import com.guanghuiz.exchangecard.Database.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    public static int THIS_ACTIVITY_INDEX = 1;
    public static final int requestCode_Signup = 12;

    //  edittext username, password
    private EditText editText_username;
    private EditText editText_password;

    private Button btn_login;
    private Button btn_jump_to_user_signup;
    private LinearLayout root;

    private ProgressDialog progressDialog;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(LoginActivity.this);

        root = (LinearLayout) findViewById(R.id.layout_login_container);


        // set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_log_in);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        // get the backward button on the toolbar
        ImageButton imageButton = (ImageButton) toolbar.findViewById(R.id.imagebutton_login_backward);
        imageButton.setOnClickListener(backward_click);

        // log in button
        btn_login = (Button) findViewById(R.id.btn_userlogin);
        btn_login.setOnClickListener(ocl_login);

        // sign up button
        btn_jump_to_user_signup = (Button) findViewById(R.id.btn_jump_to_user_signup);
        btn_jump_to_user_signup.setOnClickListener(ocl_jump_to_signup);


        // set the edittext password and username
        editText_password = (EditText) findViewById(R.id.edittext_password);
        editText_username = (EditText) findViewById(R.id.edittext_username);

        // show pass word as it is
        // editText_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        // hide the password show as black dots
        editText_password.setTransformationMethod(PasswordTransformationMethod.getInstance());


    }

    //    @Override
    //    public void onBackPressed() {
    //
    //        super.onBackPressed();
    //
    //    }

    // onclick listener for image button
    OnClickListener backward_click = new OnClickListener() {
        @Override
        public void onClick(View v) {
            jump_to_mainactivity(1, false, "", -1);
        }
    };

    // onclick listener for log in buton
    OnClickListener ocl_login = new OnClickListener() {
        @Override
        public void onClick(View v) {

            // set root layout disabled
            // root.setEnabled(false);

            String username = String.valueOf(editText_username.getText());
            String password = String.valueOf(editText_password.getText());

            try {
                password = Utils.getMD5(password);
                Log.i("Log in activity", password);
            }
            catch (NoSuchAlgorithmException E){
                Toast.makeText(LoginActivity.this, "User Auth Information is wrong!", Toast.LENGTH_SHORT).show();
                jump_to_mainactivity(1, false, username, -1);
            }

            // authenticate the MD5 hashed password with the one stored in sqlitedatabase
            UserSQLiteHelper helper = new UserSQLiteHelper(getApplicationContext());

            // enthenticate the user and pasword locally firstly
            if(null==username || username.isEmpty()){
                Toast.makeText(LoginActivity.this, "Username is empty", Toast.LENGTH_SHORT).show();
            } else if (null==password || password.isEmpty()){
                Toast.makeText(LoginActivity.this, "Password is empty", Toast.LENGTH_SHORT).show();
            }

            if(null!=username && !username.isEmpty()
                    &&null!=password && !password.isEmpty()) {
                if (helper.enthenticate(username, password)) {

                    Log.i("exchangecard", "local log in success");

                } else {

                    Log.i("exchangecard", "Local Log in Fail! Try Server");
                }

                // try to enthenticate remotely on server
                // this is the usually case when local db had been cleaned up
                // check internet available first
                if (!Utils.isInternetAvailable(getApplicationContext())) {
                    // if internet is not available , jump back
                    Toast.makeText(getApplicationContext(), R.string.internet_is_not_connected,
                            Toast.LENGTH_SHORT).show();
                } else {

                    authFromServer(username, password);
                }
            }


        }

    };

    private void authFromServer(String username, final String password){
        // when button is clicked, show progress dialog
        progressDialog = ProgressDialog.show(LoginActivity.this, "", getResources().getString(R.string.please_wait), true );

        // use async task to auth from server
        //        AuthenticateFromServerTask task = new AuthenticateFromServerTask(this);
        //        task.execute(new User(username, password));

        Log.i("exchangecard", "Server log in!");

        ServiceApi api = ServiceGenerator.createService(ServiceApi.class);
        Call<ServerAuthResponse> call = api.authUser(new User(username, password));

        call.enqueue(new Callback<ServerAuthResponse>() {
            @Override
            public void onResponse(Call<ServerAuthResponse> call, Response<ServerAuthResponse> response) {
                progressDialog.cancel();

            if (response.isSuccessful()) {
                // tasks available
                int status_code = response.code();
                if (status_code== 200){

                    // get the response reinformation
                    ServerAuthResponse responseData = response.body();
                    // Log.i(MainActivity.TAG, "responseData: " +responseData);

                    // if isauth true, set the logintoken locally
                    if(responseData.isauth) {
                        Log.i(MainActivity.TAG, "auth successfully from server");
                        Log.i(MainActivity.TAG, "Set Log In Token");
                        // set Log In TO App Token
                        //  Utils.setLoginToken(getApplicationContext(), responseData.username);
                        // update the mailaddress of logged in user
                        Utils.setUserMailAddress(getApplicationContext(),
                                responseData.profile_id,
                                responseData.username,
                                password,
                                responseData.getMailAddress());

                        // create Session Manager to Store the Log Information

                        sessionManager.createdLoginSession(responseData.username, responseData.profile_id);

                        // jump mainactivity
                         jump_to_mainactivity(2, true, responseData.username, responseData.profile_id);


                    } else{
                        Log.i(MainActivity.TAG, "auth failed from server");
                        sessionManager.logoutProfile();
                        jump_to_mainactivity(1, false, "", -1);

                    }

                } else if (status_code == 400){
                    sessionManager.logoutProfile();
                    jump_to_mainactivity(1, false, "", -1);
                    Log.i(MainActivity.TAG, "Bad Request, Already Existed!");

                } else if (status_code == 403){
                    sessionManager.logoutProfile();
                    jump_to_mainactivity(1, false, "", -1);
                    Log.i(MainActivity.TAG, "Access Denied!");

                } else if (status_code == 405) {
                    sessionManager.logoutProfile();
                    jump_to_mainactivity(1, false, "", -1);
                    Log.i(MainActivity.TAG, "Method is not allowed!");

                }else {
                    sessionManager.logoutProfile();
                    jump_to_mainactivity(1, false, "", -1);
                    Log.i(MainActivity.TAG, "Auth User Address: "+"failed!");

                }



            } else {
                // error response, no access to resource?
                Log.i(MainActivity.TAG, "Auth User Address: "+"failed!");
                sessionManager.logoutProfile();
                jump_to_mainactivity(1, false, "", -1);
            }
        }

        @Override
        public void onFailure(Call<ServerAuthResponse> call, Throwable t) {
            Log.i(MainActivity.TAG, "Auth User Address: "+"failed!");
            sessionManager.logoutProfile();
            jump_to_mainactivity(1, false, "", -1);

        }
    });
    }

    // sign up click listener
    OnClickListener ocl_jump_to_signup = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // jump to signup activity
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            Bundle bundle = new Bundle();
            intent.putExtras(bundle);
            //启动Activity
            startActivity(intent);
            //startActivityForResult(intent, requestCode_Signup);

        }
    };

    // method for jump to mainactivity
    public void jump_to_mainactivity(int item_index_when_back, boolean log_in_or_not, String current_username, int curent_id){
        progressDialog.cancel();
        // show toast on register information
        if(log_in_or_not) {
            Toast.makeText(this, "Log in succeed!", Toast.LENGTH_SHORT).show();
        } else{
            Toast.makeText(this, "Log in failed!", Toast.LENGTH_SHORT).show();
        }

        //创建Intent对象，传入源Activity和目的Activity的类对象
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);

        //If you want to close existing activity stack regardless of
        // what's in there and create new root, correct set of flags is the following:
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // put activity index into bundle
        Bundle bundle = new Bundle();
        bundle.putInt("JUMP_FROM_ACTIVITY", THIS_ACTIVITY_INDEX);
        bundle.putInt("ITEM_INDEX_WHEN_BACK", item_index_when_back);
        bundle.putBoolean("LOG_IN_OR_NOT", log_in_or_not);
        bundle.putString("CURRENT_USER_NAME", current_username);
        bundle.putInt("CURRENT_PROFILE_ID", curent_id);
        intent.putExtras(bundle);

        //启动Activity
        startActivity(intent);
    }



    //
    //    @Override
    //    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    //        // Check which request we're responding to
    //        if (requestCode == requestCode_Signup) {
    //            // Make sure the request was successful
    //            if (resultCode == Activity.RESULT_OK) {
    //                // Do something with the contact here (bigger example below)
    //                Bundle bundle = data.getExtras();
    //                String username = bundle.getString("CURRENT_USER");
    //
    //                Intent result = new Intent();
    //                Bundle abundle = new Bundle();
    //                abundle.putString("CURRENT_USER",  username);
    //                result.putExtras(bundle);
    //                setResult(Activity.RESULT_OK, result);
    //                finish();
    //
    //            }
    //        }
    //    }
}

