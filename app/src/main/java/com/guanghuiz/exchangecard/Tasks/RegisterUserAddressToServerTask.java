package com.guanghuiz.exchangecard.Tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.guanghuiz.exchangecard.SendReceiveApi.ServiceApi;
import com.guanghuiz.exchangecard.UI.MainActivity;
import com.guanghuiz.exchangecard.UI.SignUpActivity;
import com.guanghuiz.exchangecard.Utils.SessionManager;
import com.guanghuiz.exchangecard.Utils.Utils;
import com.guanghuiz.exchangecard.Database.model.RegisterUserAddressRequestData;
import com.guanghuiz.exchangecard.Database.model.RegisterUserAddressResponseData;

import java.io.IOException;
import java.lang.ref.WeakReference;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Guanghui on 28/2/16.
 */
public class RegisterUserAddressToServerTask extends AsyncTask<RegisterUserAddressRequestData, Void, Response> {
    private static Context context;
    private static WeakReference<SignUpActivity> activityWeakReference;

    public RegisterUserAddressToServerTask(Context context, SignUpActivity activity){
        this.context = context;
        this.activityWeakReference = new WeakReference<SignUpActivity>(activity);
    }
    @Override
    protected Response doInBackground(RegisterUserAddressRequestData... params) {
        // get the information needed to send
        RegisterUserAddressRequestData info_to_send = params[0];

        try {
            return register_by_retrofit(info_to_send);
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }


    protected void onProgressUpdate(Void... progress) {

    }

    protected void onPostExecute(Response response) {

        int status_code = response.code();

        Log.i(MainActivity.TAG, "Status Code is " + status_code);

        if (status_code== 201 || status_code== 200){
            Toast.makeText(context, "Register Successfully", Toast.LENGTH_SHORT).show();
            Log.i(MainActivity.TAG, "Registered From Server Successfully");

            // get the response reinformation

            RegisterUserAddressResponseData responseData = (RegisterUserAddressResponseData) response.body();
            Log.i(MainActivity.TAG, "responseData: " +response);
            Log.i(MainActivity.TAG, "responseData: " +responseData);

            // register new user to local
            Log.i(MainActivity.TAG, "Save to local database After Register");
            activityWeakReference.get().register_new_profile_to_local(responseData);

            Log.i(MainActivity.TAG, "Set Log In Token After Register");
            // set Log In to App Token
            // Utils.setLoginToken(context, responseData.username);

            // method 2, store in preferences, but not privately using session manager
            // set current user in preference
            /*
            SharedPreferences settings = activityWeakReference.get().
                    getSharedPreferences(MainActivity.PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();

            editor.putString(MainActivity.key_current_log_in_username,
                    responseData.username);
            editor.putInt(MainActivity.key_current_log_in_profile_id,
                    responseData.profile_id);
            // Commit the edits!
            editor.commit();
            */

            // use Session Manager To Create a New session
            SessionManager sessionManager = new SessionManager(activityWeakReference.get());
            sessionManager.createdLoginSession(responseData.username, responseData.profile_id);

            // jump back to main activity
            Utils.jump_to_mainactivity(activityWeakReference.get(),
                    SignUpActivity.THIS_ACTIVITY_INDEX, 2,
                    true, responseData.username, responseData.profile_id);


        } else if (status_code == 400){
            Log.i(MainActivity.TAG, "Bad Request, Already Existed!");
        } else if (status_code == 403){
            Log.i(MainActivity.TAG, "Access Denied!");
        } else if (status_code == 405) {
            Log.i(MainActivity.TAG, "Method is not allowed!");
        }else {
            Log.i(MainActivity.TAG, "Register User Address: "+"failed!, "
                +"status code: " +status_code);
        }


    }

    private Response register_by_retrofit(RegisterUserAddressRequestData information) throws IOException {
        Log.i(MainActivity.TAG, "register information "+information);
        // create retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServiceApi.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServiceApi api = retrofit.create(ServiceApi.class);

        // method 2, use form POSTrequest
        // Call<LoginServerResponse> call = api.registerUserAsForm(information);
        Call<RegisterUserAddressResponseData> call = api.registerUserAddressProfile(information);


        Response response = call.execute();

        return response;

    }

}
