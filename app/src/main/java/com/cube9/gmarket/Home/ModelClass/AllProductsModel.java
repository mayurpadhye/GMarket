package com.cube9.gmarket.Home.ModelClass;

import android.util.Log;

import com.cube9.gmarket.WebUrls.WebUrls;
import com.cube9.gmarket.network.Api;
import com.cube9.gmarket.network.RetrofitClient;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class AllProductsModel {
    public String product_id, product_type, product_name, product_price, product_special_price, product_image, product_discount, product_desc, wishlist_flag;
    String review_count, rating, delivery_time, quantity_available, is_in_stock;
   public static List<AllProductsModel> movies = new ArrayList<>();
    public AllProductsModel() {

    }

    public AllProductsModel(String product_id, String product_type, String product_name, String product_price, String product_special_price, String product_image, String product_discount, String product_desc, String wishlist_flag, String review_count, String rating, String delivery_time, String quantity_available, String is_in_stock) {
        this.product_id = product_id;
        this.product_type = product_type;
        this.product_name = product_name;
        this.product_price = product_price;
        this.product_special_price = product_special_price;
        this.product_image = product_image;
        this.product_discount = product_discount;
        this.product_desc = product_desc;
        this.wishlist_flag = wishlist_flag;
        this.review_count = review_count;
        this.rating = rating;
        this.delivery_time = delivery_time;
        this.quantity_available = quantity_available;
        this.is_in_stock = is_in_stock;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_type() {
        return product_type;
    }

    public void setProduct_type(String product_type) {
        this.product_type = product_type;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_price() {
        return product_price;
    }

    public void setProduct_price(String product_price) {
        this.product_price = product_price;
    }

    public String getProduct_special_price() {
        return product_special_price;
    }

    public void setProduct_special_price(String product_special_price) {
        this.product_special_price = product_special_price;
    }

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    public String getProduct_discount() {
        return product_discount;
    }

    public void setProduct_discount(String product_discount) {
        this.product_discount = product_discount;
    }

    public String getProduct_desc() {
        return product_desc;
    }

    public void setProduct_desc(String product_desc) {
        this.product_desc = product_desc;
    }

    public String getWishlist_flag() {
        return wishlist_flag;
    }

    public void setWishlist_flag(String wishlist_flag) {
        this.wishlist_flag = wishlist_flag;
    }

    public String getReview_count() {
        return review_count;
    }

    public void setReview_count(String review_count) {
        this.review_count = review_count;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getDelivery_time() {
        return delivery_time;
    }

    public void setDelivery_time(String delivery_time) {
        this.delivery_time = delivery_time;
    }

    public String getQuantity_available() {
        return quantity_available;
    }

    public void setQuantity_available(String quantity_available) {
        this.quantity_available = quantity_available;
    }

    public String getIs_in_stock() {
        return is_in_stock;
    }

    public void setIs_in_stock(String is_in_stock) {
        this.is_in_stock = is_in_stock;
    }


    /**
     * Creating 10 dummy content for list.
     *
     * @param itemCount
     * @return
     */
    public static List<AllProductsModel> createMovies(final int itemCount) {


        RetrofitClient retrofitClient = new RetrofitClient();
        Api service = retrofitClient.getAPIClient(WebUrls.DOMAIN_URL);
        service.getAllProducts(10, itemCount, "2", "1",
                new Callback<Response>() {
                    @Override
                    public void success(Response result, Response response) {
                        try {

Log.i("errrrrrrrrrrrr",String.valueOf(response.getBody()));
                            String bodyString = new String(((TypedByteArray) response.getBody()).getBytes());
                            JSONObject jsonObject = new JSONObject(bodyString);

                            String cart_counter = jsonObject.getString("cart_counter");
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");

                            JSONArray productinfo = jsonObject.getJSONArray("productinfo");
                            for (int i = 0; i < productinfo.length(); i++) {
                                JSONObject j1 = productinfo.getJSONObject(i);
                                String product_id = j1.getString("product_id");
                                String product_type = j1.getString("product_type");
                                String product_name = j1.getString("product_name");
                                String product_price = j1.getString("product_price");
                                String product_special_price = j1.getString("product_special_price");
                                String product_image = j1.getString("product_special_price");
                                String product_discount = j1.getString("product_discount");
                                String product_desc = j1.getString("product_desc");
                                String wishlist_flag = j1.getString("wishlist_flag");
                                String review_count = j1.getString("review_count");
                                String rating = j1.getString("rating");
                                String delivery_time = j1.getString("delivery_time");
                                String quantity_available = j1.getString("quantity_available");
                                String is_in_stock = j1.getString("is_in_stock");

                                AllProductsModel movie = new AllProductsModel(product_id, product_type, product_name, product_price, product_special_price, product_image,
                                        product_discount, product_desc, wishlist_flag, review_count, rating, delivery_time, quantity_available, is_in_stock + (itemCount == 0 ?
                                        (itemCount + 1 + i) : (itemCount + i)));
                                movies.add(movie);
                            }
                        } catch (JSONException je) {

                            Log.i("JSONNNNNNNN", "" + je.toString());
                            je.printStackTrace();

                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {

                        Log.i("JSONNNNNNNN", "" + error.getMessage());
                        error.printStackTrace();
                    }
                });



        return movies;


    }
}

