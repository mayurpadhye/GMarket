package com.cube9.gmarket.Products.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

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
import com.cube9.gmarket.R;
import com.cube9.gmarket.SearchProducts.MasterSearchActivity;
import com.cube9.gmarket.SearchProducts.SearchProductsActivity;
import com.cube9.gmarket.WebUrls.WebUrls;
import com.cube9.gmarket.nointernet.NoInternetActivity;
import com.cube9.gmarket.receivers.NetworkChangeReceiver;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDetailsActivity extends AppCompatActivity {
    ImageView iv_products;
    RecyclerView rv_product_images;
    TextView tv_product_name, tv_special_price_msg, tv_discounted_pice, tv_original_price, tv_discount;
    Button btn_add_to_cart, btn_buy_now;
    ProgressBar p_bar;
    JSONArray images=null;
    ScrollView sv_main;
    public  static String product_id = "";
    boolean isMoreImages = false;
    SparkButton spark_button;
    List<String> product_images_list;
    Toolbar toolbar;
    TextView tv_title, tv_cart_count;
    ImageView iv_back, iv_search, iv_cart;
    JSONArray conf_prod;
    TextView tv_share;
    String product_url="";
    Button btn_out_of_stock;
    LinearLayout ll_add_to_cart;;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        initView();
        onClick();


    }//onCreateClose

    @Override
    protected void onResume() {
        super.onResume();
        if (NetworkChangeReceiver.isOnline(ProductDetailsActivity.this))
            getProductDetails();
        else
        {
            startActivity(new Intent(ProductDetailsActivity.this, NoInternetActivity.class));

        }

        if (new SharedPrefManager(ProductDetailsActivity.this).IsLogin()) {
            tv_cart_count.setText(new SharedPrefManager(ProductDetailsActivity.this).getCartCount());
            tv_cart_count.setVisibility(View.VISIBLE);
        } else {
            tv_cart_count.setVisibility(View.GONE);
        }
    }

    public void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        iv_back = (ImageView) toolbar.findViewById(R.id.iv_back);
        iv_search = (ImageView) toolbar.findViewById(R.id.iv_search);
        iv_cart = (ImageView) toolbar.findViewById(R.id.iv_cart);
        tv_title = (TextView) toolbar.findViewById(R.id.tv_title);
        tv_cart_count = (TextView) toolbar.findViewById(R.id.tv_cart_count);
        spark_button = (SparkButton) findViewById(R.id.spark_button);
        product_images_list = new ArrayList<String>();
        iv_products = (ImageView) findViewById(R.id.iv_products);
        rv_product_images = (RecyclerView) findViewById(R.id.rv_product_images);
        tv_product_name = (TextView) findViewById(R.id.tv_product_name);
        tv_special_price_msg = (TextView) findViewById(R.id.tv_special_price_msg);
        tv_discounted_pice = (TextView) findViewById(R.id.tv_discounted_pice);
        tv_original_price = (TextView) findViewById(R.id.tv_original_price);
        tv_discount = (TextView) findViewById(R.id.tv_discount);
        btn_add_to_cart = (Button) findViewById(R.id.btn_add_to_cart);
        sv_main = (ScrollView) findViewById(R.id.sv_main);
        btn_buy_now = (Button) findViewById(R.id.btn_buy_now);
        p_bar = (ProgressBar) findViewById(R.id.p_bar);
        btn_out_of_stock = (Button) findViewById(R.id.btn_out_of_stock);
        ll_add_to_cart = (LinearLayout) findViewById(R.id.ll_add_to_cart);
        tv_share=(TextView)findViewById(R.id.tv_share);
        Intent i = getIntent();
        product_id = i.getStringExtra("product_id");
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_product_images.setLayoutManager(layoutManager);
        if(product_id==null) {
            Uri data = getIntent().getData();
            if(data !=null) {
                product_id = data.getQueryParameter("product_id");

            }
        }
    }//initViewClose

    public void onClick() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!new SharedPrefManager(ProductDetailsActivity.this).IsLogin()) {
                    Intent i = new Intent(ProductDetailsActivity.this, LoginDetailsActivity.class);
                    i.putExtra("intent_from", "cart");
                    i.putExtra("product_id", product_id);
                    startActivityForResult(i, 1);
                } else {
                    if (conf_prod.length() > 0) {
                        Intent i = new Intent(ProductDetailsActivity.this, VarientProductActivity.class);
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
                if (!new SharedPrefManager(ProductDetailsActivity.this).IsLogin()) {
                    Intent i = new Intent(ProductDetailsActivity.this, LoginDetailsActivity.class);
                    i.putExtra("intent_from", "product_details");
                    startActivityForResult(i, 1);
                } else {
                    startActivity(new Intent(ProductDetailsActivity.this, MyCartActivity.class));
                }
            }
        });

        spark_button.setEventListener(new SparkEventListener() {
            @Override
            public void onEvent(ImageView button, boolean buttonState) {

                if (!new SharedPrefManager(ProductDetailsActivity.this).IsLogin()) {
                    Intent i = new Intent(ProductDetailsActivity.this, LoginDetailsActivity.class);
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
                startActivity(new Intent(ProductDetailsActivity.this, MasterSearchActivity.class));
            }
        });

        tv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT,  product_url+"?product_id="+product_id);
                Intent intent = Intent.createChooser(shareIntent,"Share");
                startActivity(intent);
            }
        });

        iv_products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductDetailsActivity.this, ProductImageZoomActivity.class);
                intent.putExtra("jsonArray", images.toString());
                startActivity(intent);
            }
        });

    }//onClickClose
    public void getAllProductsForDelivery()
    {
        p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.GET_PRODUCT_FOR_DELIVERY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        p_bar.setVisibility(View.GONE);
                        try {

                            JSONObject Jsonobj = new JSONObject(response);
                            Log.i("Prodt",""+Jsonobj.toString());
                            String status=Jsonobj.getString("status");
                            if (status.equals("1"))
                            {

                                JSONArray products=Jsonobj.getJSONArray("products");
                                for (int i=0;i<products.length();i++)
                                {
                                    JSONObject j1=products.getJSONObject(i);
                                    String product_id=j1.getString("product_id");
                                    String product_name=j1.getString("product_name");
                                    String product_price=j1.getString("product_price");
                                    String product_special_price=j1.getString("product_special_price");
                                    String product_image=j1.getString("product_image");
                                    String product_discount=j1.getString("product_discount");
                                    String selected_quantity=j1.getString("selected_quantity");
                                    String quantity_available=j1.getString("quantity_available");
                                 //   Toast.makeText(ProductDetailsActivity.this, ""+quantity_available, Toast.LENGTH_SHORT).show();

                                }
                                if (!Jsonobj.isNull("default_shipping_address"))
                                {
                                    JSONArray default_shipping_address=Jsonobj.getJSONArray("default_shipping_address");
                                    for(int k=0;k<default_shipping_address.length();k++)
                                    {
                                        JSONObject address=default_shipping_address.getJSONObject(k);
                                        String address_fname=address.getString("address_fname");
                                       // address_id=address.getString("address_id");
                                        String address_lname=address.getString("address_lname");
                                        String telephone=address.getString("telephone");
                                        JSONArray street=address.getJSONArray("street");
                                        String street1="";
                                        String street2="";
                                        for (int i=0;i<street.length();i++)
                                        {
                                            street1=""+street.get(0);
                                            street2=""+street.get(1);
                                        }
                                        String city=address.getString("city");
                                        String zip=address.getString("zip");
                                        String state=address.getString("state");
                                        String country=address.getString("country");

                                    }
                                    if (default_shipping_address.length()==0)
                                    {
                                        Intent i=new Intent(ProductDetailsActivity.this,AddNewAddressActivity.class);
                                        i.putExtra("intent_from","delivery_detail_buy_now");
                                        startActivityForResult(i, 1);
                                    }
                                    else
                                    {
                                        if (!new SharedPrefManager(ProductDetailsActivity.this).IsLogin()) {

                                            Intent i = new Intent(ProductDetailsActivity.this, LoginDetailsActivity.class);
                                            i.putExtra("intent_from", "buy_now");
                                            i.putExtra("product_id", product_id);
                                            startActivityForResult(i, 1);
                                        } else {
                                            if (conf_prod.length() > 0) {
                                                Intent i = new Intent(ProductDetailsActivity.this, VarientProductActivity.class);
                                                i.putExtra("product_id", product_id);
                                                i.putExtra("using_from", "buy_now");
                                                startActivity(i);
                                            } else {
                                                //  AddtoCart("buy_now");
                                                Intent i=new Intent(ProductDetailsActivity.this, BuyNowActivity.class);
                                                i.putExtra("product_id",product_id);
                                                startActivity(i);

                                            }

                                        }
                                    }


                                }



                            }
                            else{
                                if (status.equals("0"))
                                {
                                    if (!Jsonobj.isNull("default_shipping_address"))
                                    {
                                        JSONArray default_shipping_address=Jsonobj.getJSONArray("default_shipping_address");
                                        for(int k=0;k<default_shipping_address.length();k++)
                                        {
                                            JSONObject address=default_shipping_address.getJSONObject(k);
                                            String address_fname=address.getString("address_fname");

                                            String address_lname=address.getString("address_lname");
                                            String telephone=address.getString("telephone");
                                            JSONArray street=address.getJSONArray("street");
                                            String street1="";
                                            String street2="";
                                            for (int i=0;i<street.length();i++)
                                            {
                                                street1=""+street.get(0);
                                                street2=""+street.get(1);
                                            }
                                            String city=address.getString("city");
                                            String zip=address.getString("zip");
                                            String state=address.getString("state");
                                            String country=address.getString("country");

                                        }
                                           if (default_shipping_address.length()>0)
                                           {
                                               Intent i=new Intent(ProductDetailsActivity.this, BuyNowActivity.class);
                                               i.putExtra("product_id",product_id);
                                               startActivity(i);
                                           }
                                        if (default_shipping_address.length()==0)
                                        {
                                            Intent i=new Intent(ProductDetailsActivity.this,AddNewAddressActivity.class);
                                            i.putExtra("intent_from","delivery_detail_buy_now");
                                            startActivityForResult(i, 1);
                                        }
                                    }
                                }
                            }



                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.i("buytttt",e.toString());
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
                params.put("language",new SharedPrefManager(ProductDetailsActivity.this).getLanguage());
                params.put("user_id",new SharedPrefManager(ProductDetailsActivity.this).GetCustomerId());

                params.put("language",new SharedPrefManager(ProductDetailsActivity.this).getLanguage());

                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GMarketApplication.getInstance(ProductDetailsActivity.this).addToRequestQueue(stringRequest);
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
                                CustomUtils.showToast(Jsonobj.getString("message"), ProductDetailsActivity.this);
                                //items.setWishlist_flag("1");

                                spark_button.setChecked(true);

                            } else {
                                CustomUtils.showToast(Jsonobj.getString("message"), ProductDetailsActivity.this);
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
                params.put("language", new SharedPrefManager(ProductDetailsActivity.this).getLanguage());
                params.put("product_id", product_id);
                params.put("user_id", new SharedPrefManager(ProductDetailsActivity.this).GetCustomerId());


                return params;
            }
        };

        GMarketApplication.getInstance(ProductDetailsActivity.this).addToRequestQueue(stringRequest);

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
                                CustomUtils.showToast(getResources().getString(R.string.remove_from_wishlist), ProductDetailsActivity.this);
                                spark_button.setChecked(false);

                            } else {
                                CustomUtils.showToast(getResources().getString(R.string.remove_from_wishlist_error), ProductDetailsActivity.this);
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
                params.put("language", new SharedPrefManager(ProductDetailsActivity.this).getLanguage());
                params.put("product_id", product_id);
                params.put("user_id", new SharedPrefManager(ProductDetailsActivity.this).GetCustomerId());


                return params;
            }
        };

        GMarketApplication.getInstance(ProductDetailsActivity.this).addToRequestQueue(stringRequest);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String intent_from = data.getStringExtra("intent_from");
                String product_id = data.getStringExtra("product_id");
                if (intent_from.equals("buy_now")) {
                    Intent i=new Intent(ProductDetailsActivity.this, BuyNowActivity.class);
                    i.putExtra("product_id",product_id);
                    startActivity(i);
                } else if (intent_from.equals("cart")) {
                    if (conf_prod.length() > 0) {
                        Intent i = new Intent(ProductDetailsActivity.this, VarientProductActivity.class);
                        i.putExtra("product_id", product_id);
                        i.putExtra("using_from", "cart");
                        startActivity(i);
                    } else {
                        AddtoCart("cart");
                    }
                } else if (intent_from.equals("product_details")) {
                    startActivity(new Intent(ProductDetailsActivity.this, MyCartActivity.class));
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
                                new SharedPrefManager(ProductDetailsActivity.this).setCartCount(my_cart_count);

                                CustomUtils.showToast(message, ProductDetailsActivity.this);
                                if (order_by.equals("buy_now")) {
                                    startActivity(new Intent(ProductDetailsActivity.this, DeliveryDetailsActivity.class));
                                }

                            } else {
                                CustomUtils.showToast(message, ProductDetailsActivity.this);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.i("add_to_cart_exception",e.toString());
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
                params.put("language", new SharedPrefManager(ProductDetailsActivity.this).getLanguage());
                params.put("product_id", product_id);
                params.put("user_id", new SharedPrefManager(ProductDetailsActivity.this).GetCustomerId());


                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GMarketApplication.getInstance(ProductDetailsActivity.this).addToRequestQueue(stringRequest);
    }

    public void getProductDetails() {
        p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.PRODUCT_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        p_bar.setVisibility(View.GONE);
                        try {
                            Log.d("ProductDetails",""+response.toString());
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
                                 images = products.getJSONArray("images");
                                 product_url = products.getString("product_url");
                                conf_prod = Jsonobj.getJSONArray("conf_prod");
                                String wishlist_flag = products.getString("wishlist_flag");
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
                                int config_size=conf_prod.length();
                                setProductDetailsData(product_id, product_name, product_price, product_special_price, product_image, product_discount, product_description, quantity_available,is_in_stock,config_size);
                                sv_main.setVisibility(View.VISIBLE);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.i("json_logggggg",""+e.toString());
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
                params.put("language", new SharedPrefManager(ProductDetailsActivity.this).getLanguage());
                params.put("product_id", product_id);
                params.put("user_id", new SharedPrefManager(ProductDetailsActivity.this).GetCustomerId());


                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GMarketApplication.getInstance(ProductDetailsActivity.this).addToRequestQueue(stringRequest);
    }//productDetailsClose

    public void setProductDetailsData(String product_id, String product_name, String product_price, String product_special_price, String product_image, String product_discount, String product_description, String quantity_available,String is_in_stock,int config_size) {
        tv_product_name.setText(product_name);
        tv_discounted_pice.setText( product_special_price+"FCFA " );
        tv_original_price.setText(product_price);
        tv_discount.setText("" + product_discount + " % OFF");
        if (product_special_price.equals("null")) {
            tv_discounted_pice.setVisibility(View.GONE);
        }
        if (product_discount.equals("null")) {
            tv_discount.setVisibility(View.GONE);
        }
        if (!product_price.equals("null")) {

         //   tv_original_price.setText("FCFA " + new DecimalFormat("##.##").format(Double.parseDouble(product_price)));
            tv_original_price.setText(product_price+" FCFA" );

        }
        if (!product_special_price.equals("null")) {
            tv_discounted_pice.setText( product_special_price+" FCFA");
            //tv_discounted_pice.setText("FCFA " + new DecimalFormat("##.##").format(Double.parseDouble(product_special_price)));
            tv_original_price.setPaintFlags( tv_original_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        }
     //   Glide.with(ProductDetailsActivity.this).load(product_image).into(iv_products);

      /*  Glide.with(ProductDetailsActivity.this).load(product_image)
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(iv_products);*/
        Picasso.with(ProductDetailsActivity.this).load(product_image).networkPolicy(NetworkPolicy.NO_CACHE).into(iv_products);

        if (is_in_stock.equals("1"))
        {
            btn_out_of_stock.setVisibility(View.GONE);
            ll_add_to_cart.setVisibility(View.VISIBLE);



        }
        else
        {
            if (config_size>0)
            {
                btn_out_of_stock.setVisibility(View.GONE);
                ll_add_to_cart.setVisibility(View.VISIBLE);
            }
            else
            {
                ll_add_to_cart.setVisibility(View.GONE);
                btn_out_of_stock.setVisibility(View.VISIBLE);
            }

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
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_product_images, parent, false);

            return new MyViewHolder(itemView);
        }

        @SuppressLint("NewApi")
        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
           // Glide.with(getApplicationContext()).load(product_images_list.get(position)).into(holder.iv_product_image);

            /*Glide.with(ProductDetailsActivity.this).load(product_images_list.get(position))
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.iv_product_image);*/
            Picasso.with(ProductDetailsActivity.this).load(product_images_list.get(position)).networkPolicy(NetworkPolicy.NO_CACHE).into(holder.iv_product_image);

            holder.iv_product_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Glide.with(ProductDetailsActivity.this).load(product_images_list.get(position)).into(iv_products);


                   /* Glide.with(ProductDetailsActivity.this).load(product_images_list.get(position))
                            .thumbnail(0.5f)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(iv_products);*/

                    Picasso.with(ProductDetailsActivity.this).load(product_images_list.get(position)).networkPolicy(NetworkPolicy.NO_CACHE).into(iv_products);
                }
            });

        }


        @Override
        public int getItemCount() {
            return product_images_list.size();
        }
    }
}
