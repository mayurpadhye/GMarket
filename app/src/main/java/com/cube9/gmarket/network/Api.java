package com.cube9.gmarket.network;

import com.cube9.gmarket.Home.ModelClass.ApiResponse;
import com.cube9.gmarket.Home.ModelClass.StackApiResponse;
import com.cube9.gmarket.WebUrls.WebUrls;
import com.google.gson.JsonElement;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface Api {

    @FormUrlEncoded
    @POST("/product.php")
    void getAllProducts(@Field("size") int size,
                              @Field("pageno") int pageno,
                              @Field("user_id") String user_id,
                              @Field("language") String language, Callback<Response> callback);

    @FormUrlEncoded
    @POST("/bestdeal.php")
    void getTopOffers(@Field("language") String size,
                        @Field("user_id") String pageno, Callback<Response> callback);

    @GET("product.php")
    Call<StackApiResponse> getProductList(@Query("size") int page, @Query("pageno") int pagesize,@Query("language") String language);

    @FormUrlEncoded
    @POST("/main_cat_details.php")
    void getSubCategory(@Field("cat_id") String cat_id,
                        @Field("language") String language, Callback<Response> callback);

    @FormUrlEncoded
    @POST("/all_latest.php")
    void getCatWiseProducts(
                        @Field("language") String language, Callback<Response> callback);

}


