package com.guanghuiz.exchangecard.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.guanghuiz.exchangecard.SendReceiveApi.ServiceApi;
import com.guanghuiz.exchangecard.SendReceiveApi.ServiceGenerator;
import com.guanghuiz.exchangecard.UI.LoginActivity;
import com.guanghuiz.exchangecard.UI.MainActivity;
import com.guanghuiz.exchangecard.Utils.SessionManager;
import com.guanghuiz.exchangecard.Database.model.ServerAuthResponse;
import com.guanghuiz.exchangecard.Database.model.User;

import java.io.IOException;
import java.lang.ref.WeakReference;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Guanghui on 10/3/16.
 */
public class AuthenticateFromServerTask extends AsyncTask<User, Void, Response> {
    private WeakReference<LoginActivity> weakReference;
    private SessionManager sessionManager;

    public AuthenticateFromServerTask(LoginActivity activity){
        sessionManager = new SessionManager(activity);
        this.weakReference = new WeakReference<LoginActivity>(activity);
    }
    @Override
    protected Response doInBackground(User... params) {
        User user = params[0];

        ServiceApi api = ServiceGenerator.createService(ServiceApi.class);

        Call<ServerAuthResponse> call = api.authUser(user);

        try {
            Response response = call.execute();

            return response;
        } catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }

    protected void onProgressUpdate(Void... progress) {

    }

    protected void onPostExecute(Response result) {
        int status_code = result.code();

        Log.i(MainActivity.TAG, "Status Code is " + status_code);

        if (status_code== 200){
            Log.i(MainActivity.TAG, "auth successfully");

            // get the response reinformation
            ServerAuthResponse responseData = (ServerAuthResponse) result.body();
            Log.i(MainActivity.TAG, "responseData: " +result);
            Log.i(MainActivity.TAG, "responseData: " +responseData);

            // if isauth true, set the logintoken locally
            if(responseData.isauth) {
                Log.i(MainActivity.TAG, "Set Log In Token");
                // set Log In TO App Token
                // Utils.login_database(weakReference.get(), responseData.username);

                /*
                // set current user in preference
                SharedPreferences settings = weakReference.get().
                        getSharedPreferences(MainActivity.PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(MainActivity.key_current_log_in_username,
                        responseData.username);
                editor.putInt(MainActivity.key_current_log_in_profile_id,
                        responseData.profile_id);
                editor.commit();
                */
                sessionManager = new SessionManager(weakReference.get());
                sessionManager.createdLoginSession(responseData.username, responseData.profile_id);


                // jump mainactivity
                weakReference.get().jump_to_mainactivity(2, true, responseData.username, responseData.profile_id);
            } else{

                sessionManager.logoutProfile();
                weakReference.get().jump_to_mainactivity(1, false, "", -1);
            }

        } else if (status_code == 400){
            sessionManager.logoutProfile();
            Log.i(MainActivity.TAG, "Bad Request, Already Existed!");
        } else if (status_code == 403){
            sessionManager.logoutProfile();
            Log.i(MainActivity.TAG, "Access Denied!");
        } else if (status_code == 405) {
            sessionManager.logoutProfile();
            Log.i(MainActivity.TAG, "Method is not allowed!");
        }else {
            sessionManager.logoutProfile();
            Log.i(MainActivity.TAG, "Auth User Address: "+"failed!, "
                    +"status code: " +status_code);
        }



    }
}
