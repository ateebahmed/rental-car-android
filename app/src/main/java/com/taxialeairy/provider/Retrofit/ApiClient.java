package com.taxialeairy.provider.Retrofit;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ApiClient {

    public static final String BASE_URL = "http://app.aleairytaxi.com/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(40, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS).build();
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .client(okHttpClient)
                    .build();
        }
        return retrofit;
    }
}
