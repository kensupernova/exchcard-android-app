package com.guanghuiz.exchangecard.Tasks;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.guanghuiz.exchangecard.SendReceiveApi.ServiceApi;
import com.guanghuiz.exchangecard.SendReceiveApi.ServiceGenerator;
import com.guanghuiz.exchangecard.UI.MainActivity;
import com.guanghuiz.exchangecard.Utils.SessionManager;
import com.guanghuiz.exchangecard.Database.UserSQLiteHelper;
import com.guanghuiz.exchangecard.UI.SendToCardActivity;
import com.guanghuiz.exchangecard.Database.model.RecipientForCard;

import java.lang.ref.WeakReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Guanghui on 17/2/16.
 */
public class ReceiveAddressFromServerTask extends AsyncTask<String, Void, Void> {
    public WeakReference<ProgressBar> progressBarWeakReference;
    public WeakReference<SendToCardActivity> weakReference;

    public ReceiveAddressFromServerTask(ProgressBar progressBar, SendToCardActivity activity){
        this.progressBarWeakReference = new WeakReference<ProgressBar>(progressBar);
        this.weakReference = new WeakReference<SendToCardActivity>(activity);

    }
    protected Void doInBackground(String... msg) {
        /*
        // get the current user name in perference

        SharedPreferences settings =weakReference.get()
                .getSharedPreferences(MainActivity.PREFS_NAME, 0);
        final String current_log_in_username=
                settings.getString(MainActivity.key_current_log_in_username, "null");
        */

        SessionManager sessionManager =new SessionManager(weakReference.get());
        final String current_log_in_username= sessionManager.getUserName();
        if(null==current_log_in_username){
            // cancel this task first
            this.cancel(true);
            sessionManager.redirectToLoginActivity();
            // sessionManager.isLogInOrRedirect();

        }

            // get the password by username
            UserSQLiteHelper helper = new UserSQLiteHelper(weakReference.get());
            final String pw = helper.getHashedPassword(current_log_in_username);

            Log.i(MainActivity.TAG + ":" + this.toString(),
                    current_log_in_username + ":" + pw);

            // if pw is wrong, need to log in again
            if (pw == null) {
                this.cancel(true);
            }


        Log.i("exchangecard", "executing receiveaddressfromservertask");


        // method 2
        ServiceApi api = ServiceGenerator.createService(ServiceApi.class,
                current_log_in_username, pw);
        Call<RecipientForCard> call = api.getRecipientForCard();

        // establish asnyc call and execute
        call.enqueue(new Callback<RecipientForCard>() {
            @Override
            public void onResponse(Call<RecipientForCard> call, Response<RecipientForCard> response) {
                progressBarWeakReference.get().setVisibility(View.GONE);
                int statusCode = response.code();
                RecipientForCard recipientForCard = response.body();
                // Log.i("exchangecard", "status code :" + statusCode);
                Log.i("exchangecard", "receive address succeed :" + recipientForCard);

                if (recipientForCard != null) {
                    weakReference.get().updateTextView(recipientForCard);
                } else{
                    weakReference.get().updateTextView(null);

                }
            }

            @Override
            public void onFailure(Call<RecipientForCard> call, Throwable t) {
                progressBarWeakReference.get().setVisibility(View.GONE);
                // Log error here since request failed
                Log.i("exchangecard", "response body: failed " + call);
                Log.i("exchangecard", "response body: failed " + t.toString());
            }
        });

        return null;

    }
    protected void onProgressUpdate(Void... progress) {

    }

    protected void onPostExecute(Void result) {

    }
}
