package com.guanghuiz.exchangecard.Tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.guanghuiz.exchangecard.SendReceiveApi.ServiceApi;
import com.guanghuiz.exchangecard.Database.model.MailAddress;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Guanghui on 17/2/16.
 */
public class SendAddressToServerTask extends AsyncTask<MailAddress, Void, Void> {

    Context context;
    // public static WeakReference<MainActivity> mainRef;
    public SendAddressToServerTask(Context context){
        // mainRef = new WeakReference<MainActivity>(activity);
        this.context = context;
    }
    protected Void doInBackground(MailAddress... address) {

        MailAddress addr = address[0];
        // create retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServiceApi.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Log.i("exchangecard", "retrofit");

        ServiceApi api = retrofit.create(ServiceApi.class);

        MailAddress mailAddress = new MailAddress("张光辉", "四川省三台县","621116");

        Call<MailAddress> call = api.addAddress(mailAddress);

        call.enqueue(new Callback<MailAddress>() {
            @Override
            public void onResponse(Call<MailAddress> call, Response<MailAddress> response) {
                int statusCode = response.code();
                MailAddress mailAddressBack = response.body();
                Log.i("exchangecard", "state code :" + statusCode);
                Log.i("exchangecard", "add address successfully!");
                Toast.makeText(context, "Address has been send to sever", Toast.LENGTH_SHORT).show();
                if (mailAddressBack != null) {
                    Log.i("exchangecard", mailAddressBack + " !");
                } else{
                    Log.i("exchangecard","NULL!");
                }
            }

            @Override
            public void onFailure(Call<MailAddress> call, Throwable t) {
                Log.i("exchangecard", "add address failed!");
            }
        });


        return null;
    }

    protected void onProgressUpdate(Void... progress) {

    }

    protected void onPostExecute(Void result) {

    }
}
