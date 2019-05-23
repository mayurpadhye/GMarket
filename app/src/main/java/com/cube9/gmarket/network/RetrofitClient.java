package com.cube9.gmarket.network;

import com.cube9.gmarket.WebUrls.WebUrls;
import com.squareup.okhttp.OkHttpClient;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = WebUrls.DOMAIN_URL;
    private static RetrofitClient mInstance;
    private Retrofit retrofit;


    public Api getAPIClient(String url)
    {
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {


            }
        };


        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(url)

                .setRequestInterceptor(requestInterceptor)
                .setClient(new OkClient(new OkHttpClient()))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        return adapter.create(Api.class);
    }
}
