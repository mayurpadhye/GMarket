package com.cube9.gmarket.WishList.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cube9.gmarket.Cart.Activity.MyCartActivity;
import com.cube9.gmarket.HelperClass.GMarketApplication;
import com.cube9.gmarket.HelperClass.SharedPrefManager;
import com.cube9.gmarket.Home.Activities.HomeActivity;
import com.cube9.gmarket.R;
import com.cube9.gmarket.SearchProducts.MasterSearchActivity;
import com.cube9.gmarket.SearchProducts.SearchProductsActivity;
import com.cube9.gmarket.WebUrls.WebUrls;
import com.cube9.gmarket.WishList.Adapter.WishListAdapter;
import com.cube9.gmarket.WishList.ModelClass.WishListPojo;
import com.cube9.gmarket.nointernet.NoInternetActivity;
import com.cube9.gmarket.receivers.NetworkChangeReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WishListActivity extends AppCompatActivity {
RecyclerView rv_wishlist;
ProgressBar p_bar;
List<WishListPojo> wishListPojoList;
    Toolbar toolbar;
    TextView tv_title,tv_cart_count;
    ImageView iv_back,iv_search,iv_cart;
    LinearLayout ll_empty_wishlist;
    Button btn_continue_shopping;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_list);
        initView();
        onClick();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (NetworkChangeReceiver.isOnline(WishListActivity.this))
            getWishList();
        else
        {
            startActivity(new Intent(WishListActivity.this, NoInternetActivity.class));

        }
        if (new SharedPrefManager(WishListActivity.this).IsLogin())
        {
            tv_cart_count.setText(new SharedPrefManager(WishListActivity.this).getCartCount());
        }
    }

    public void initView()
    {
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        iv_back=(ImageView)toolbar.findViewById(R.id.iv_back);
        iv_search=(ImageView)toolbar.findViewById(R.id.iv_search);
        iv_cart=(ImageView)toolbar.findViewById(R.id.iv_cart);
        tv_title=(TextView) toolbar.findViewById(R.id.tv_title);
        tv_cart_count=(TextView) toolbar.findViewById(R.id.tv_cart_count);
        rv_wishlist=(RecyclerView)findViewById(R.id.rv_wishlist);
        p_bar=(ProgressBar)findViewById(R.id.p_bar);
        btn_continue_shopping=(Button) findViewById(R.id.btn_continue_shopping);
        ll_empty_wishlist=(LinearLayout) findViewById(R.id.ll_empty_wishlist);
        wishListPojoList=new ArrayList<WishListPojo>();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rv_wishlist.setLayoutManager(mLayoutManager);
        rv_wishlist.addItemDecoration(new DividerItemDecoration(this, 1));
        tv_title.setText(getResources().getString(R.string.my_wishlist));



    }//initViewClose

    public void onClick()
    {
        iv_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WishListActivity.this,MyCartActivity.class));}
         });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WishListActivity.this, MasterSearchActivity.class));
            }
        });

        btn_continue_shopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(WishListActivity.this, HomeActivity.class);
                startActivity(i);
                finish();
            }
        });
    }//onClickClose
    public void getWishList()
    {
        p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.GET_ALL_WISHLIST_PRODUCTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        p_bar.setVisibility(View.GONE);
                        try {
                            wishListPojoList.clear();
                            JSONObject Jsonobj = new JSONObject(response);
                            String status=Jsonobj.getString("status");
                            if (status.equals("1"))
                            {
                                int k=0;
                                JSONArray products=Jsonobj.getJSONArray("products");
                                for (int i=0;i<products.length();i++)
                                {
                                    k=k+1;
                                    JSONObject j1=products.getJSONObject(i);
                                    String product_id=j1.getString("product_id");
                                    String product_name=j1.getString("product_name");
                                    String product_price=j1.getString("product_price");
                                    String product_special_price=j1.getString("product_special_price");
                                    String product_image=j1.getString("product_image");
                                    String product_discount=j1.getString("product_discount");
                                    String is_in_stock=j1.getString("is_in_stock");
                                    JSONArray config_prod=j1.getJSONArray("config_prod");
                                    boolean isConfigurable;
                                    if (config_prod.length()>0)
                                    {
                                        isConfigurable=true;
                                    }
                                    else
                                    {
                                        isConfigurable=false;
                                    }

                                    wishListPojoList.add(new WishListPojo(product_id,product_name,product_price,product_special_price,product_image,product_discount,isConfigurable,is_in_stock));
                                }
                                WishListAdapter wishListAdapter=new WishListAdapter(wishListPojoList,WishListActivity.this,p_bar,tv_cart_count,ll_empty_wishlist,tv_title);
                                rv_wishlist.setAdapter(wishListAdapter);
                                ll_empty_wishlist.setVisibility(View.GONE);
                                tv_title.setText(getResources().getString(R.string.my_wishlist)+" "+"("+k+")");
                            }
                            else
                            {
                                ll_empty_wishlist.setVisibility(View.VISIBLE);
                                tv_title.setText(getResources().getString(R.string.my_wishlist)+" (0)");
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
                     //   Toast.makeText(WishListActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("language",new SharedPrefManager(WishListActivity.this).getLanguage());
                params.put("user_id",new SharedPrefManager(WishListActivity.this).GetCustomerId());


                return params;
            }
        };

        GMarketApplication.getInstance(WishListActivity.this).addToRequestQueue(stringRequest);
    }
}
