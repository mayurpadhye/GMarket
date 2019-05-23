package com.cube9.gmarket.Home.ModelClass;

import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;
import android.util.Log;

import com.cube9.gmarket.network.RetrofitClient2;

import java.util.List;

import retrofit.mime.TypedByteArray;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemDataSource extends PageKeyedDataSource<Integer, productinfo> {

    //the size of a page that we want
    public static final int PAGE_SIZE = 50;
    //we will start from the first page which is 1
    private static final int FIRST_PAGE = 1;
    private static final int MAX_PAGE = 10;
    //we need to fetch from stackoverflow
    private static final String SITE_NAME = "stackoverflow";
    //this will be called once to load the initial data

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull final LoadInitialCallback<Integer, productinfo> callback) {
        RetrofitClient2.getInstance()
                .getApi().getProductList(FIRST_PAGE, PAGE_SIZE,"2")
                .enqueue(new Callback<StackApiResponse>() {
                    @Override
                    public void onResponse(Call<StackApiResponse> call, Response<StackApiResponse> response) {
                        if (response.body() != null) {
                        //    List<productinfo> item  = response.body();
                            Log.i("ffffffffffffffff",""+response.body().productinfo);
                            callback.onResult(response.body().productinfo, null, FIRST_PAGE + 1);
                        }
                    }

                    @Override
                    public void onFailure(Call<StackApiResponse> call, Throwable t) {

                    }
                });
    }

    //this will load the previous page
    @Override
    public void loadBefore(@NonNull final LoadParams<Integer> params, @NonNull final LoadCallback<Integer, productinfo> callback) {
        RetrofitClient2.getInstance()
                .getApi().getProductList(params.key, PAGE_SIZE,"2")
                .enqueue(new Callback<StackApiResponse>() {
                    @Override
                    public void onResponse(Call<StackApiResponse> call, Response<StackApiResponse> response) {

                        //if the current page is greater than one
                        //we are decrementing the page number
                        //else there is no previous page
                        Integer adjacentKey = (params.key > 1) ? params.key - 1 : null;
                        if (response.body() != null) {

                            //passing the loaded data
                            //and the previous page key
                            callback.onResult(response.body().productinfo, adjacentKey);
                        }
                    }

                    @Override
                    public void onFailure(Call<StackApiResponse> call, Throwable t) {

                    }
                });
    }

    //this will load the next page
    @Override
    public void loadAfter(@NonNull final LoadParams<Integer> params, @NonNull final LoadCallback<Integer, productinfo> callback) {
        RetrofitClient2.getInstance()
                .getApi()
                .getProductList(params.key, PAGE_SIZE,"2")
                .enqueue(new Callback<StackApiResponse>() {
                    @Override
                    public void onResponse(Call<StackApiResponse> call, Response<StackApiResponse> response) {

                        if (response.body() != null) {
                            //if the response has next page
                            //incrementing the next page number

                            Integer key=null;
                           if (params.key!=10) {
                                key = params.key + 1;//response.body().has_more ? params.key + 1 : null;
                           }
                            //passing the loaded data and next page value
                            callback.onResult(response.body().productinfo, key);
                        }
                    }

                    @Override
                    public void onFailure(Call<StackApiResponse> call, Throwable t) {

                    }
                });
    }
}