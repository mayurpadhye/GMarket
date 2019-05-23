package com.cube9.gmarket.Products.Activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cube9.gmarket.Account.Activity.AddNewAddressActivity;
import com.cube9.gmarket.Cart.Activity.MyCartActivity;
import com.cube9.gmarket.Delivery.Activity.BuyNowActivity;
import com.cube9.gmarket.Delivery.Activity.DeliveryDetailsActivity;
import com.cube9.gmarket.HelperClass.CustomUtils;
import com.cube9.gmarket.HelperClass.GMarketApplication;
import com.cube9.gmarket.HelperClass.SharedPrefManager;
import com.cube9.gmarket.Login.Activity.LoginDetailsActivity;
import com.cube9.gmarket.Products.Adapter.RecommendedProductsAdapter;
import com.cube9.gmarket.Products.ModelClasses.RecommendedPojo;
import com.cube9.gmarket.R;
import com.cube9.gmarket.SearchProducts.MasterSearchActivity;
import com.cube9.gmarket.SearchProducts.SearchProductsActivity;
import com.cube9.gmarket.WebUrls.WebUrls;
import com.cube9.gmarket.nointernet.NoInternetActivity;
import com.cube9.gmarket.receivers.NetworkChangeReceiver;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.XMLReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDetailsNewActivity extends AppCompatActivity {
    Button btn_out_of_stock;
    Dialog dailog;
    Button btn_continue,btn_make_payment;
    RecyclerView rv_product_images;
    public static String product_id = "";
    public static String category_id = "";
    List<String> product_images_list;
    JSONArray conf_prod;
    ShimmerFrameLayout mShimmerViewContainer;
    String product_url = "";
    RatingBar ratingBar;
    ScrollView sv_product_details;
    Toolbar toolbar;
    ProgressBar p_bar;
    TextView tv_title, tv_cart_count;
    ImageView iv_back, iv_search, iv_cart;
    RelativeLayout rl_main;
    SparkButton spark_button;
    TextView tv_product_name, tv_special_price_msg, tv_discounted_pice, tv_original_price, tv_discount, tv_product_details, tv_rating, tv_share, tv_all_reviews;
    ImageView iv_products;
    TextView tv_avg_rating;
    LinearLayout ll_addtocart_layout;
    JSONArray images = null;
    LinearLayout ll_review;
    CardView cv_customer_review;
    RecyclerView rv_recommended_products;
    RecommendedProductsAdapter adapter;
    List<RecommendedPojo> recommendedPojoList;
    Button btn_add_to_cart, btn_buy_now;
    CardView cv_size, cv_color;
    TextView tv_size, tv_color;

    @Override
    protected void onResume() {
        super.onResume();

      //  rl_main.setVisibility(View.GONE);


        if (new SharedPrefManager(ProductDetailsNewActivity.this).IsLogin()) {
            tv_cart_count.setText(new SharedPrefManager(ProductDetailsNewActivity.this).getCartCount());
            tv_cart_count.setVisibility(View.VISIBLE);
        } else {
            tv_cart_count.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details_new);
        initView();
        onClick();
        if (NetworkChangeReceiver.isOnline(ProductDetailsNewActivity.this)) {
            getProductDetails();
            //CheckStock(product_id,"1");
        } else {
            startActivity(new Intent(ProductDetailsNewActivity.this, NoInternetActivity.class));

        }
    }


    private void initView() {
        dailog=new Dialog(this);
        dailog.setContentView(R.layout.dialog_continue_shopping);
        btn_continue=dailog.findViewById(R.id.btn_continue);
        btn_make_payment=dailog.findViewById(R.id.btn_make_payment);
        rl_main = findViewById(R.id.rl_main);
        p_bar = findViewById(R.id.p_bar);
        cv_size = findViewById(R.id.cv_size);
        cv_color = findViewById(R.id.cv_color);
        tv_size = findViewById(R.id.tv_size);
        tv_color = findViewById(R.id.tv_color);
        p_bar = (ProgressBar) findViewById(R.id.p_bar);
        p_bar.setVisibility(View.GONE);
        btn_out_of_stock = (Button) findViewById(R.id.btn_out_of_stock);
        btn_add_to_cart = (Button) findViewById(R.id.btn_add_to_cart);
        sv_product_details = (ScrollView) findViewById(R.id.sv_product_details);
        btn_buy_now = (Button) findViewById(R.id.btn_buy_now);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        iv_back = (ImageView) toolbar.findViewById(R.id.iv_back);
        iv_search = (ImageView) toolbar.findViewById(R.id.iv_search);
        iv_cart = (ImageView) toolbar.findViewById(R.id.iv_cart);
        tv_title = (TextView) toolbar.findViewById(R.id.tv_title);
        tv_cart_count = (TextView) toolbar.findViewById(R.id.tv_cart_count);
        spark_button = (SparkButton) findViewById(R.id.spark_button);
        mShimmerViewContainer = (ShimmerFrameLayout) findViewById(R.id.shimmer_view_container);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        recommendedPojoList = new ArrayList<RecommendedPojo>();
        tv_product_name = (TextView) findViewById(R.id.tv_product_name);
        tv_avg_rating = (TextView) findViewById(R.id.tv_avg_rating);
        ll_addtocart_layout = (LinearLayout) findViewById(R.id.ll_addtocart_layout);
        tv_special_price_msg = (TextView) findViewById(R.id.tv_special_price_msg);
        tv_discounted_pice = (TextView) findViewById(R.id.tv_discounted_price);
        tv_original_price = (TextView) findViewById(R.id.tv_actual_price);
        tv_discount = (TextView) findViewById(R.id.tv_discount);
        tv_product_details = (TextView) findViewById(R.id.tv_product_details);
        tv_rating = (TextView) findViewById(R.id.tv_rating);
        tv_share = (TextView) findViewById(R.id.tv_share);
        tv_all_reviews = (TextView) findViewById(R.id.tv_all_reviews);
        rv_product_images = (RecyclerView) findViewById(R.id.rv_product_images);

        rv_recommended_products = (RecyclerView) findViewById(R.id.rv_recommended_products);
        spark_button = (SparkButton) findViewById(R.id.spark_button);
        iv_products = (ImageView) findViewById(R.id.iv_products);
        ll_review = (LinearLayout) findViewById(R.id.ll_review);
        cv_customer_review = (CardView) findViewById(R.id.cv_customer_review);
        product_images_list = new ArrayList<String>();
        Intent i = getIntent();
        product_id = i.getStringExtra("product_id");
        category_id = i.getStringExtra("category_id");

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_product_images.setLayoutManager(layoutManager);
        if (product_id == null) {
            Uri data = getIntent().getData();
            if (data != null) {
                product_id = data.getQueryParameter("product_id");
                category_id = data.getQueryParameter("category_id");

            }
        }

        GridLayoutManager gridLayoutManager = new GridLayoutManager(ProductDetailsNewActivity.this, 1);
        gridLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_recommended_products.setLayoutManager(gridLayoutManager);


    }//initViewClose

    public void getProductDetails() {
        p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.PRODUCT_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            p_bar.setVisibility(View.GONE);
                            Log.d("ProductDetails", "" + response.toString());
                            JSONObject Jsonobj = new JSONObject(response);
                            String status = Jsonobj.getString("status");

                            if (status.equals("1")) {
                                product_images_list.clear();

                                JSONObject products = Jsonobj.getJSONObject("products");
                                String product_id = products.getString("product_id");
                                String product_name = products.getString("product_name");
                                String product_price = products.getString("product_price");
                                String product_special_price = products.getString("product_special_price");
                                String product_image = products.getString("product_image");
                                String product_discount = products.getString("product_discount");
                                String product_description = products.getString("product_description");
                                String quantity_available = products.getString("quantity_available");
                                String is_in_stock = products.getString("is_in_stock");
                                String delivery_time = products.getString("delivery_time");
                                String rating_average = products.getString("rating_average");
                                String reviews_count = products.getString("reviews_count");
                                String size = products.getString("size");
                                String color = products.getString("color");

                                JSONArray reviewlist = Jsonobj.getJSONArray("reviewlist");
                                if (reviewlist.length() == 0) {
                                    cv_customer_review.setVisibility(View.GONE);
                                } else {
                                    cv_customer_review.setVisibility(View.VISIBLE);
                                }
                                ll_review.removeAllViews();
                                for (int m = 0; m < reviewlist.length(); m++) {
                                    View layout1 = LayoutInflater.from(ProductDetailsNewActivity.this).inflate(R.layout.row_customer_review, ll_review, false);
                                    TextView tv_title = layout1.findViewById(R.id.tv_title);
                                    TextView tv_created_on = layout1.findViewById(R.id.tv_created_on);
                                    TextView tv_user_name = layout1.findViewById(R.id.tv_user_name);
                                    TextView tv_review = layout1.findViewById(R.id.tv_review);
                                    RatingBar ratingBar = layout1.findViewById(R.id.ratingBar);


                                    JSONObject j1 = reviewlist.getJSONObject(m);
                                    String title = j1.getString("title");
                                    String details = j1.getString("details");
                                    String user_nickname = j1.getString("user_nickname");
                                    String created_date = j1.getString("created_date");
                                    String avgrating = j1.getString("avgrating");

                                    tv_title.setText(title);
                                    tv_review.setText(details);
                                    tv_created_on.setText(created_date);
                                    tv_user_name.setText(user_nickname);
                                    ratingBar.setRating(Float.parseFloat(avgrating));
                                    ll_review.addView(layout1);
                                }


                                images = products.getJSONArray("images");
                                product_url = products.getString("product_url");
                                conf_prod = Jsonobj.getJSONArray("conf_prod");
                                String wishlist_flag = products.getString("wishlist_flag");

                                JSONArray recommended_product = Jsonobj.getJSONArray("recommended_product");
                                recommendedPojoList.clear();
                                for (int n = 0; n < recommended_product.length(); n++) {
                                    RecommendedPojo recommendedPojo = new RecommendedPojo();
                                    JSONObject jsonObject = recommended_product.getJSONObject(n);
                                    String product_id1 = jsonObject.getString("product_id");
                                    String product_name1 = jsonObject.getString("product_name");
                                    String product_price1 = jsonObject.getString("product_price");
                                    String product_special_price1 = jsonObject.getString("product_special_price");
                                    String product_image1 = jsonObject.getString("product_image");
                                    String product_discount1 = jsonObject.getString("product_discount");
                                    String product_desc = jsonObject.getString("product_desc");
                                    String wishlist_flag1 = jsonObject.getString("wishlist_flag");
                                    String quantity_available1 = jsonObject.getString("quantity_available");
                                    String category_id = jsonObject.getString("category_id");
                                    String category_name = jsonObject.getString("category_name");
                                    String rating = jsonObject.getString("rating");
                                    String review_count = jsonObject.getString("review_count");

                                    recommendedPojo.setProduct_id(product_id1);
                                    recommendedPojo.setProduct_name(product_name1);
                                    recommendedPojo.setProduct_price(product_price1);
                                    recommendedPojo.setProduct_special_price(product_special_price1);
                                    recommendedPojo.setProduct_image(product_image1);
                                    recommendedPojo.setProduct_discount(product_discount1);
                                    recommendedPojo.setProduct_desc(product_desc);
                                    recommendedPojo.setWishlist_flag(wishlist_flag1);
                                    recommendedPojo.setQuantity_available(quantity_available1);
                                    recommendedPojo.setCategory_id(category_id);
                                    recommendedPojo.setRating(rating);
                                    recommendedPojo.setReview(review_count);
                                    recommendedPojoList.add(recommendedPojo);
                                }

                                adapter = new RecommendedProductsAdapter(recommendedPojoList, ProductDetailsNewActivity.this);
                                rv_recommended_products.setAdapter(adapter);

                                if (wishlist_flag.equals("1"))
                                    spark_button.setChecked(true);
                                else
                                    spark_button.setChecked(false);
                                for (int i = 0; i < images.length(); i++) {

                                    String imag = String.valueOf(images.get(i));
                                    product_images_list.add(imag);

                                }

                                ProductImagesAdapter productImagesAdapter = new ProductImagesAdapter();
                                rv_product_images.setAdapter(productImagesAdapter);
                                int config_size = conf_prod.length();
                                setProductDetailsData(product_id, product_name, product_price, product_special_price, product_image, product_discount, product_description, quantity_available, is_in_stock, config_size, delivery_time, rating_average, reviews_count, size, color);


                            } else {
                                p_bar.setVisibility(View.GONE);
                            }
                            rl_main.setVisibility(View.VISIBLE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            p_bar.setVisibility(View.GONE);
                            Log.i("json_logggggg", "" + e.toString());
                            Toast.makeText(ProductDetailsNewActivity.this, "" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        p_bar.setVisibility(View.GONE);
                        //  Toast.makeText(ProductDetailsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("language", new SharedPrefManager(ProductDetailsNewActivity.this).getLanguage());
                params.put("product_id", product_id);
                params.put("user_id", new SharedPrefManager(ProductDetailsNewActivity.this).GetCustomerId());
                params.put("category_id", category_id);


                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GMarketApplication.getInstance(ProductDetailsNewActivity.this).addToRequestQueue(stringRequest);
    }

    public void setProductDetailsData(String product_id, String product_name, String product_price, String product_special_price, String product_image, String product_discount, String product_description, String quantity_available, String is_in_stock, int config_size, String delivery_time, String rating_average, String reviews_count, String size, String color) {
        tv_product_name.setText(product_name);
        tv_discounted_pice.setText(product_special_price + " FCFA");
        tv_original_price.setText(product_price);
        tv_discount.setText("" + product_discount + " % OFF");
        if (product_special_price.equals("null")) {
            tv_discounted_pice.setVisibility(View.GONE);
            tv_original_price.setVisibility(View.VISIBLE);
        } else {
            tv_discounted_pice.setVisibility(View.VISIBLE);
            tv_original_price.setVisibility(View.VISIBLE);
        }
        if (product_discount.equals("null")) {
            tv_discount.setVisibility(View.GONE);
        }
        if (!product_price.equals("null")) {

            //   tv_original_price.setText("FCFA " + new DecimalFormat("##.##").format(Double.parseDouble(product_price)));
            tv_original_price.setText(product_price + " FCFA");
            tv_original_price.setPaintFlags(0);
        }
        if (!product_special_price.equals("null")) {
            // tv_discounted_pice.setText("FCFA " + new DecimalFormat("##.##").format(Double.parseDouble(product_special_price)));
            tv_discounted_pice.setText(product_special_price + " FCFA");
            tv_original_price.setPaintFlags(tv_original_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        }
        //   Glide.with(ProductDetailsActivity.this).load(product_image).into(iv_products);

      /*  Glide.with(ProductDetailsActivity.this).load(product_image)
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(iv_products);*/
        Picasso.with(ProductDetailsNewActivity.this).load(product_image).networkPolicy(NetworkPolicy.NO_CACHE).into(iv_products);

        if (is_in_stock.equals("1")) {
            btn_out_of_stock.setVisibility(View.GONE);
            ll_addtocart_layout.setVisibility(View.VISIBLE);


        } else {
            if (config_size > 0) {
                btn_out_of_stock.setVisibility(View.GONE);
                ll_addtocart_layout.setVisibility(View.VISIBLE);
            } else {
                ll_addtocart_layout.setVisibility(View.GONE);
                btn_out_of_stock.setVisibility(View.VISIBLE);
            }

        }
        cv_color.setVisibility(View.GONE);
        cv_size.setVisibility(View.GONE);
        if (!size.equalsIgnoreCase("No")) {
            cv_size.setVisibility(View.VISIBLE);
            tv_size.setText(size);
        }
        if (!color.equalsIgnoreCase("No")) {
            cv_color.setVisibility(View.VISIBLE);
            tv_color.setText(color);
        }

        tv_rating.setText(rating_average);
        tv_all_reviews.setText(reviews_count + " " + getResources().getString(R.string.reviews));
        tv_product_details.setText(Html.fromHtml(product_description, null, new UlTagHandler()));
        ratingBar.setRating(Float.parseFloat(rating_average));
        tv_avg_rating.setText(rating_average + "/5");
    }


    public class UlTagHandler implements Html.TagHandler {
        @Override
        public void handleTag(boolean opening, String tag, Editable output,
                              XMLReader xmlReader) {
            if (tag.equals("ul") && !opening) output.append("\n\n");
            if (tag.equals("li") && opening) output.append("\n\t•");
        }
    }

    public class ProductImagesAdapter extends RecyclerView.Adapter<ProductImagesAdapter.MyViewHolder> {
        public class MyViewHolder extends RecyclerView.ViewHolder {

            ImageView iv_product_image;

            public MyViewHolder(View view) {
                super(view);

                iv_product_image = (ImageView) view.findViewById(R.id.iv_product_image);


            }
        }

        @Override
        public ProductImagesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_product_images, parent, false);

            return new ProductImagesAdapter.MyViewHolder(itemView);
        }

        @SuppressLint("NewApi")
        @Override
        public void onBindViewHolder(@NonNull final ProductImagesAdapter.MyViewHolder holder, final int position) {
            // Glide.with(getApplicationContext()).load(product_images_list.get(position)).into(holder.iv_product_image);

            /*Glide.with(ProductDetailsActivity.this).load(product_images_list.get(position))
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.iv_product_image);*/
            Picasso.with(ProductDetailsNewActivity.this).load(product_images_list.get(position)).placeholder(getResources().getDrawable(R.drawable.ic_g_market_logo)).networkPolicy(NetworkPolicy.NO_CACHE).into(holder.iv_product_image);

            holder.iv_product_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Glide.with(ProductDetailsActivity.this).load(product_images_list.get(position)).into(iv_products);


                   /* Glide.with(ProductDetailsActivity.this).load(product_images_list.get(position))
                            .thumbnail(0.5f)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(iv_products);*/

                    Picasso.with(ProductDetailsNewActivity.this).load(product_images_list.get(position)).networkPolicy(NetworkPolicy.NO_CACHE).into(iv_products);
                }
            });

        }


        @Override
        public int getItemCount() {
            return product_images_list.size();
        }
    }

    public void onClick() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dailog.dismiss();
            }
        });

        btn_make_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dailog.dismiss();
                startActivity(new Intent(ProductDetailsNewActivity.this,MyCartActivity.class));

            }
        });
        btn_add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!new SharedPrefManager(ProductDetailsNewActivity.this).IsLogin()) {
                    Intent i = new Intent(ProductDetailsNewActivity.this, LoginDetailsActivity.class);
                    i.putExtra("intent_from", "cart");
                    i.putExtra("product_id", product_id);
                    startActivityForResult(i, 1);
                } else {
                    if (conf_prod.length() > 0) {
                        Intent i = new Intent(ProductDetailsNewActivity.this, VarientProductActivity.class);
                        i.putExtra("product_id", product_id);
                        i.putExtra("using_from", "cart");
                        startActivity(i);
                    } else {
                        AddtoCart("cart");
                    }
                }

            }
        });
        btn_buy_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAllProductsForDelivery();

            }
        });

        iv_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!new SharedPrefManager(ProductDetailsNewActivity.this).IsLogin()) {
                    Intent i = new Intent(ProductDetailsNewActivity.this, LoginDetailsActivity.class);
                    i.putExtra("intent_from", "product_details");
                    startActivityForResult(i, 1);
                } else {
                    startActivity(new Intent(ProductDetailsNewActivity.this, MyCartActivity.class));
                }
            }
        });

        spark_button.setEventListener(new SparkEventListener() {
            @Override
            public void onEvent(ImageView button, boolean buttonState) {

                if (!new SharedPrefManager(ProductDetailsNewActivity.this).IsLogin()) {
                    Intent i = new Intent(ProductDetailsNewActivity.this, LoginDetailsActivity.class);
                    i.putExtra("intent_from", "product_details_wishList");
                    i.putExtra("product_id", product_id);
                    startActivityForResult(i, 1);

                } else {
                    if (buttonState) {
                        AddToWishList(product_id);
                    } else {

                        RemoveFromWishList(product_id);

                    }
                }

            }

            @Override
            public void onEventAnimationEnd(ImageView button, boolean buttonState) {

            }

            @Override
            public void onEventAnimationStart(ImageView button, boolean buttonState) {

            }
        });

        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProductDetailsNewActivity.this, MasterSearchActivity.class));
            }
        });

        tv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, product_url + "?product_id=" + product_id + "&category_id=" + category_id);
                Intent intent = Intent.createChooser(shareIntent, "Share");
                startActivity(intent);
            }
        });

        iv_products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductDetailsNewActivity.this, ProductImageZoomActivity.class);
                intent.putExtra("jsonArray", images.toString());
                startActivity(intent);
            }
        });

    }//onClickClose

    public void getAllProductsForDelivery() {
        p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.GET_PRODUCT_FOR_DELIVERY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        p_bar.setVisibility(View.GONE);
                        try {

                            JSONObject Jsonobj = new JSONObject(response);
                            Log.i("Prodt", "" + Jsonobj.toString());
                            String status = Jsonobj.getString("status");
                            if (status.equals("1")) {

                                JSONArray products = Jsonobj.getJSONArray("products");
                                for (int i = 0; i < products.length(); i++) {
                                    JSONObject j1 = products.getJSONObject(i);
                                    String product_id = j1.getString("product_id");
                                    String product_name = j1.getString("product_name");
                                    String product_price = j1.getString("product_price");
                                    String product_special_price = j1.getString("product_special_price");
                                    String product_image = j1.getString("product_image");
                                    String product_discount = j1.getString("product_discount");
                                    String selected_quantity = j1.getString("selected_quantity");
                                    String quantity_available = j1.getString("quantity_available");

                                }
                                if (!Jsonobj.isNull("default_shipping_address")) {
                                    JSONArray default_shipping_address = Jsonobj.getJSONArray("default_shipping_address");
                                    for (int k = 0; k < default_shipping_address.length(); k++) {
                                        JSONObject address = default_shipping_address.getJSONObject(k);
                                        String address_fname = address.getString("address_fname");
                                        // address_id=address.getString("address_id");
                                        String address_lname = address.getString("address_lname");
                                        String telephone = address.getString("telephone");
                                        JSONArray street = address.getJSONArray("street");
                                        String street1 = "";
                                        String street2 = "";
                                        for (int i = 0; i < street.length(); i++) {
                                            street1 = "" + street.get(0);
                                            street2 = "" + street.get(1);
                                        }
                                        String city = address.getString("city");
                                        String zip = address.getString("zip");
                                        String state = address.getString("state");
                                        String country = address.getString("country");

                                    }
                                    if (default_shipping_address.length() == 0) {
                                        if (!new SharedPrefManager(ProductDetailsNewActivity.this).IsLogin()) {

                                            Intent i = new Intent(ProductDetailsNewActivity.this, LoginDetailsActivity.class);
                                            i.putExtra("intent_from", "buy_now");
                                            i.putExtra("product_id", product_id);
                                            startActivityForResult(i, 1);
                                        } else {
                                            Intent i = new Intent(ProductDetailsNewActivity.this, AddNewAddressActivity.class);
                                            i.putExtra("intent_from", "delivery_detail_buy_now");
                                            startActivityForResult(i, 1);
                                        }

                                    } else {
                                        if (!new SharedPrefManager(ProductDetailsNewActivity.this).IsLogin()) {

                                            Intent i = new Intent(ProductDetailsNewActivity.this, LoginDetailsActivity.class);
                                            i.putExtra("intent_from", "buy_now");
                                            i.putExtra("product_id", product_id);
                                            startActivityForResult(i, 1);
                                        } else {
                                            if (conf_prod.length() > 0) {
                                                Intent i = new Intent(ProductDetailsNewActivity.this, VarientProductActivity.class);
                                                i.putExtra("product_id", product_id);
                                                i.putExtra("using_from", "buy_now");
                                                startActivity(i);
                                            } else {
                                                //  AddtoCart("buy_now");

                                                CheckStock(product_id, "1");
                                                //       sfsdf

                                            }

                                        }
                                    }


                                }


                            } else {
                                if (status.equals("0")) {
                                    if (!new SharedPrefManager(ProductDetailsNewActivity.this).IsLogin()) {

                                        Intent i = new Intent(ProductDetailsNewActivity.this, LoginDetailsActivity.class);
                                        i.putExtra("intent_from", "buy_now");
                                        i.putExtra("product_id", product_id);
                                        startActivityForResult(i, 1);
                                    } else {
                                        if (!Jsonobj.isNull("default_shipping_address")) {
                                            JSONArray default_shipping_address = Jsonobj.getJSONArray("default_shipping_address");
                                            for (int k = 0; k < default_shipping_address.length(); k++) {
                                                JSONObject address = default_shipping_address.getJSONObject(k);
                                                String address_fname = address.getString("address_fname");

                                                String address_lname = address.getString("address_lname");
                                                String telephone = address.getString("telephone");
                                                JSONArray street = address.getJSONArray("street");
                                                String street1 = "";
                                                String street2 = "";
                                                for (int i = 0; i < street.length(); i++) {
                                                    street1 = "" + street.get(0);
                                                    street2 = "" + street.get(1);
                                                }
                                                String city = address.getString("city");
                                                String zip = address.getString("zip");
                                                String state = address.getString("state");
                                                String country = address.getString("country");

                                            }
                                            if (default_shipping_address.length() > 0) {
                                                if (conf_prod.length() > 0) {
                                                    Intent i = new Intent(ProductDetailsNewActivity.this, VarientProductActivity.class);
                                                    i.putExtra("product_id", product_id);
                                                    i.putExtra("using_from", "buy_now");
                                                    startActivity(i);
                                                    //CheckStock(product_id,"1");
                                                } else {
                                                    CheckStock(product_id, "1");
                                                }
                                            }
                                            if (default_shipping_address.length() == 0) {
                                                Intent i = new Intent(ProductDetailsNewActivity.this, AddNewAddressActivity.class);
                                                i.putExtra("intent_from", "delivery_detail_buy_now");
                                                startActivityForResult(i, 1);
                                            }
                                        }
                                    }

                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.i("buytttt", e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        p_bar.setVisibility(View.GONE);
                        //  Toast.makeText(BuyNowActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("language", new SharedPrefManager(ProductDetailsNewActivity.this).getLanguage());
                params.put("user_id", new SharedPrefManager(ProductDetailsNewActivity.this).GetCustomerId());

                params.put("language", new SharedPrefManager(ProductDetailsNewActivity.this).getLanguage());

                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GMarketApplication.getInstance(ProductDetailsNewActivity.this).addToRequestQueue(stringRequest);
    }


    public void CheckStock(final String product_id, final String qty) {
        p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.CHECK_STOCK,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        p_bar.setVisibility(View.GONE);
                        try {

                            JSONObject Jsonobj = new JSONObject(response);
                            String status = Jsonobj.getString("status");

                            if (status.equals("1")) {

                                Intent i = new Intent(ProductDetailsNewActivity.this, BuyNowActivity.class);
                                i.putExtra("product_id", product_id);
                                startActivity(i);
                                //  tv_final_total.setText("FCFA "+(final_price*p_qty));
                            } else {

                                CustomUtils.showOKAlertDialog(ProductDetailsNewActivity.this, getResources().getString(R.string.quantity_not_available), getResources().getString(R.string.quantity_not_available_error));
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        p_bar.setVisibility(View.GONE);
                        //  Toast.makeText(BuyNowActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("language", new SharedPrefManager(ProductDetailsNewActivity.this).getLanguage());
                params.put("product_id", product_id);
                params.put("qty", qty);


                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GMarketApplication.getInstance(ProductDetailsNewActivity.this).addToRequestQueue(stringRequest);
    }


    private void AddToWishList(final String product_id) {
        p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.ADD_TO_WISHLIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        p_bar.setVisibility(View.GONE);
                        try {

                            JSONObject Jsonobj = new JSONObject(response);
                            String status = Jsonobj.getString("status");

                            if (status.equals("1")) {
                                CustomUtils.showToast(Jsonobj.getString("message"), ProductDetailsNewActivity.this);
                                //items.setWishlist_flag("1");

                                spark_button.setChecked(true);

                            } else {
                                CustomUtils.showToast(Jsonobj.getString("message"), ProductDetailsNewActivity.this);
                                spark_button.setChecked(false);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        p_bar.setVisibility(View.GONE);
                        // Toast.makeText(ProductDetailsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("language", new SharedPrefManager(ProductDetailsNewActivity.this).getLanguage());
                params.put("product_id", product_id);
                params.put("user_id", new SharedPrefManager(ProductDetailsNewActivity.this).GetCustomerId());


                return params;
            }
        };

        GMarketApplication.getInstance(ProductDetailsNewActivity.this).addToRequestQueue(stringRequest);

    }//AddtoWishListClose

    public void RemoveFromWishList(final String product_id) {
        p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.REMOVE_FROM_WISHLIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        p_bar.setVisibility(View.GONE);
                        try {

                            JSONObject Jsonobj = new JSONObject(response);
                            String status = Jsonobj.getString("status");

                            if (status.equals("1")) {
                                CustomUtils.showToast(getResources().getString(R.string.remove_from_wishlist), ProductDetailsNewActivity.this);
                                spark_button.setChecked(false);

                            } else {
                                CustomUtils.showToast(getResources().getString(R.string.remove_from_wishlist_error), ProductDetailsNewActivity.this);
                                spark_button.setChecked(true);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        p_bar.setVisibility(View.GONE);
                        // Toast.makeText(ProductDetailsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("language", new SharedPrefManager(ProductDetailsNewActivity.this).getLanguage());
                params.put("product_id", product_id);
                params.put("user_id", new SharedPrefManager(ProductDetailsNewActivity.this).GetCustomerId());


                return params;
            }
        };

        GMarketApplication.getInstance(ProductDetailsNewActivity.this).addToRequestQueue(stringRequest);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String intent_from = data.getStringExtra("intent_from");
                String product_id = data.getStringExtra("product_id");
                if (intent_from.equals("buy_now")) {
                   /* Intent i=new Intent(ProductDetailsNewActivity.this, BuyNowActivity.class);
                    i.putExtra("product_id",product_id);
                    startActivity(i);*/
                    if (conf_prod.length() > 0) {
                        Intent i = new Intent(ProductDetailsNewActivity.this, VarientProductActivity.class);
                        i.putExtra("product_id", product_id);
                        i.putExtra("using_from", "buy_now");
                        startActivity(i);

                    } else {
                        CheckStock(product_id, "1");
                    }

                } else if (intent_from.equals("cart")) {
                    if (conf_prod.length() > 0) {
                        Intent i = new Intent(ProductDetailsNewActivity.this, VarientProductActivity.class);
                        i.putExtra("product_id", product_id);
                        i.putExtra("using_from", "cart");
                        startActivity(i);
                    } else {
                        AddtoCart("cart");
                    }
                } else if (intent_from.equals("product_details")) {
                    startActivity(new Intent(ProductDetailsNewActivity.this, MyCartActivity.class));
                } else if (intent_from.equals("product_details_wishList")) {
                    AddToWishList(product_id);

                }
            }
        }
    }

    public void AddtoCart(final String order_by) {
        p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.ADD_TO_CART,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        p_bar.setVisibility(View.GONE);
                        try {

                            JSONObject Jsonobj = new JSONObject(response);
                            String status = Jsonobj.getString("status");
                            String message = Jsonobj.getString("message");
                            if (status.equals("1")) {
                                String my_cart_count = Jsonobj.getString("my_cart_count");
                                tv_cart_count.setText(my_cart_count);
                                new SharedPrefManager(ProductDetailsNewActivity.this).setCartCount(my_cart_count);
                                dailog.show();
                                CustomUtils.showToast(message, ProductDetailsNewActivity.this);
                                if (order_by.equals("buy_now")) {
                                    startActivity(new Intent(ProductDetailsNewActivity.this, DeliveryDetailsActivity.class));
                                }

                            } else {
                                CustomUtils.showToast(message, ProductDetailsNewActivity.this);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.i("add_to_cart_exception", e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        p_bar.setVisibility(View.GONE);
                        //    Toast.makeText(ProductDetailsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("language", new SharedPrefManager(ProductDetailsNewActivity.this).getLanguage());
                params.put("product_id", product_id);
                params.put("user_id", new SharedPrefManager(ProductDetailsNewActivity.this).GetCustomerId());


                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GMarketApplication.getInstance(ProductDetailsNewActivity.this).addToRequestQueue(stringRequest);
    }

    public void LoadRecommendedProduct(String product_id1, String product_name) {
        rl_main.setVisibility(View.GONE);
        mShimmerViewContainer.startShimmerAnimation();
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        product_id = product_id1;
        sv_product_details.fullScroll(ScrollView.FOCUS_UP);
        getProductDetails();
    }
}
