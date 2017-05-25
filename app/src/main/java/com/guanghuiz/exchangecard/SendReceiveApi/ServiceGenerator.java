package com.guanghuiz.exchangecard.SendReceiveApi;

import android.util.Base64;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit服务的创建。
 * @author  Guanghui
 * @time 10/3/16
 */
public class ServiceGenerator {

    /**
     * create retrofitBuilder object
     */
    private static Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
            .baseUrl(ServiceApi.ENDPOINT)
            .addConverterFactory(GsonConverterFactory.create());

    /**
     * create Httpclient.builder
     */
    private static OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

    public ServiceGenerator(){

    }

    public static <S> S createService(Class<S> serviceClass) {
        return createService(serviceClass, null, null);
    }


    public static <S> S createService(Class<S> serviceClass,
                                  String username, String password){
        /** if the username and password are not null,
        add to the interceptors of the client */
        if(username!=null && password!=null){

            /** 创建密码信息 */
            String credentials = username + ":" + password;
            final String auth_string =
                    "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);


            /** define the interceptor, add authentication headers, username, password */
            clientBuilder.addInterceptor(new Interceptor() {
                @Override
                public okhttp3.Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();

                    Request.Builder requestBuilder = original.newBuilder()
                            .header("Authorization", auth_string) // add authorization information
                            .header("Accept", "application/json") // the data type when receive response
                            .method(original.method(), original.body());

                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            });

        }

        // get the HttpClient from client builder
        OkHttpClient client = clientBuilder.build();

        Retrofit retrofit = retrofitBuilder.client(client).build();

        return retrofit.create(serviceClass);
    }

    public static <S> S createServiceForPostMultiPart(Class<S> serviceClass,
                                      String username, String password){
        // if the username and password are not null,
        // add to the interceptors of the client
        if(username!=null && password!=null){

            String credentials = username + ":" + password;
            final String auth_string =
                    "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);


            // define the interceptor, add authentication headers, username, password
            clientBuilder.addInterceptor(new Interceptor() {
                @Override
                public okhttp3.Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();

                    Request.Builder requestBuilder = original.newBuilder()
                            .header("Authorization", auth_string)
                            .header("content-type", "multipart/form-data") // the data type when sending request
                            .method(original.method(), original.body());

                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            });

        }

        // get the HttpClient from client builder
        OkHttpClient client = clientBuilder.build();

        Retrofit retrofit = retrofitBuilder.client(client).build();

        return retrofit.create(serviceClass);
    }


}
