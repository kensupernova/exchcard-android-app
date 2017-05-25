package com.guanghuiz.exchangecard.Tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


import com.guanghuiz.exchangecard.SendReceiveApi.ServiceApi;
import com.guanghuiz.exchangecard.UI.MainActivity;
import com.guanghuiz.exchangecard.Database.model.RegisterUserAddressResponseData;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Guanghui on 28/2/16.
 */
public class RegisterEncryptedInfoToServerTask extends AsyncTask<String, Void, Void> {
    private static Context context;

    public RegisterEncryptedInfoToServerTask(Context context){
        this.context = context;
    }
    @Override
    protected Void doInBackground(String... params) {
        // get the information needed to send
        String info_to_send = params[0];

        // send_by_httpurlconnection(info_to_send);
        register_by_retrofit(info_to_send);
        return null;
    }


    protected void onProgressUpdate(Void... progress) {

    }

    protected void onPostExecute(Void result) {

    }

    private void send_by_httpurlconnection(String info_to_send) {
        // Create a new HttpClient and Post Header
        HttpURLConnection urlConnection;
        URL url;

        try {
            // set up url connection
            url = new URL("http://exchangecard2.applinzi.com/api/register");
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setDoOutput(true);
            urlConnection.setChunkedStreamingMode(0);

            // make the string to send
            StringBuilder result = new StringBuilder();
            result.append(URLEncoder.encode("register", "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(info_to_send, "UTF-8"));

            // write the post into to server
            OutputStream os = new BufferedOutputStream(urlConnection.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "utf-8"));

            writer.write(result.toString());
            writer.flush();
            writer.close();
            os.close();

            // get the feed back
            Log.i(MainActivity.TAG, "" + urlConnection.getResponseCode());
            Log.i(MainActivity.TAG, "" + urlConnection.getResponseMessage());

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            Log.i("exchangecard", "register feedback " + sb.toString());

            urlConnection.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
    }

    private void register_by_retrofit(String information){

        // create retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServiceApi.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Log.i("exchangecard", "retrofit");

        ServiceApi api = retrofit.create(ServiceApi.class);

        Call<RegisterUserAddressResponseData> call = api.registerUserAsForm(information);

        call.enqueue(new Callback<RegisterUserAddressResponseData>() {
            @Override
            public void onResponse(Call<RegisterUserAddressResponseData> call, Response<RegisterUserAddressResponseData> response) {
                int statusCode = response.code();
                RegisterUserAddressResponseData result = response.body();

                if (result != null) {
                    // code 201 means created successfully!
                    Log.i("exchangecard", "state code :" + statusCode);
                    Log.i("exchangecard", result + " !");
                } else{
                    Log.i("exchangecard","result is null!");
                }
            }

            @Override
            public void onFailure(Call<RegisterUserAddressResponseData> call, Throwable t) {
                Log.i("exchangecard", "add address failed!");
            }
        });
    }

}
