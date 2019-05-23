package com.cube9.gmarket.network;

import com.cube9.gmarket.WebUrls.WebUrls;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient2 {
    private static final String BASE_URL = WebUrls.BASE_URL;
    private static RetrofitClient2 mInstance;
    private Retrofit retrofit;


    private RetrofitClient2() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized RetrofitClient2 getInstance() {
        if (mInstance == null) {
            mInstance = new RetrofitClient2();
        }
        return mInstance;
    }

    public Api getApi() {
        return retrofit.create(Api.class);
    }
}
