package com.cube9.gmarket.Orders.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cube9.gmarket.Cart.Activity.MyCartActivity;
import com.cube9.gmarket.HelperClass.GMarketApplication;
import com.cube9.gmarket.HelperClass.SharedPrefManager;
import com.cube9.gmarket.Orders.Adapters.OrderDetailsAdapter;
import com.cube9.gmarket.Orders.ModelClass.OrderDetailPojo;
import com.cube9.gmarket.R;
import com.cube9.gmarket.SearchProducts.MasterSearchActivity;
import com.cube9.gmarket.SearchProducts.SearchProductsActivity;
import com.cube9.gmarket.WebUrls.WebUrls;
import com.cube9.gmarket.nointernet.NoInternetActivity;
import com.cube9.gmarket.receivers.NetworkChangeReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderDetailsActivity extends AppCompatActivity {
ProgressBar p_bar;
RecyclerView rv_order_details;
String order_id="";
List<OrderDetailPojo> orderDetailPojoList;
Context context;
    Toolbar toolbar;
    TextView tv_title,tv_cart_count;
    ImageView iv_back,iv_search,iv_cart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        initView();

        onClick();
        if (new SharedPrefManager(OrderDetailsActivity.this).IsLogin())
        {
            tv_cart_count.setText(new SharedPrefManager(OrderDetailsActivity.this).getCartCount());
        }
        else
        {
            tv_cart_count.setVisibility(View.GONE);
        }

    }//onCreateClose

    @Override
    protected void onResume() {
        super.onResume();

        if (NetworkChangeReceiver.isOnline(OrderDetailsActivity.this))
            GetOrderDetails();
        else
        {
            startActivity(new Intent(OrderDetailsActivity.this, NoInternetActivity.class));

        }
    }

    public void onClick()
    {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("order_id", order_id );

                setResult(RESULT_OK, intent);
                finish();
            }
        });
        iv_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(context,MyCartActivity.class));

            }
        });
        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OrderDetailsActivity.this, MasterSearchActivity.class));
            }
        });

    }//onClickClose

    private void initView() {
        p_bar=(ProgressBar)findViewById(R.id.p_bar);
        rv_order_details=(RecyclerView) findViewById(R.id.rv_order_details);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        iv_back=(ImageView)toolbar.findViewById(R.id.iv_back);
        iv_search=(ImageView)toolbar.findViewById(R.id.iv_search);
        iv_cart=(ImageView)toolbar.findViewById(R.id.iv_cart);
        tv_title=(TextView) toolbar.findViewById(R.id.tv_title);
        tv_cart_count=(TextView) toolbar.findViewById(R.id.tv_cart_count);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rv_order_details.setLayoutManager(mLayoutManager);
        rv_order_details.addItemDecoration(new DividerItemDecoration(this, 0));
        orderDetailPojoList=new ArrayList<OrderDetailPojo>();
        context=this;
        Intent i=getIntent();
        order_id=i.getStringExtra("order_id");
        tv_title.setText(getResources().getString(R.string.order_details));

    }//initViewClose

    public void GetOrderDetails()
    {
        p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.GET_ORDER_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        p_bar.setVisibility(View.GONE);
                        try {

                            //converting response to json object
                            JSONObject Jsonobj = new JSONObject(response);
                            String status=Jsonobj.getString("status");

                            if (status.equals("1"))
                            {
                                orderDetailPojoList.clear();
                                JSONArray order_list=Jsonobj.getJSONArray("order_list");
                                for (int i=0;i<order_list.length();i++)
                                {
                                    JSONObject j1=order_list.getJSONObject(i);
                                    String product_id=j1.getString("product_id");
                                    String name=j1.getString("name");
                                    String product_type=j1.getString("product_type");
                                    String sku=j1.getString("sku");
                                    String description=j1.getString("description");
                                    String price=j1.getString("price");
                                    String Image=j1.getString("Image");
                                    String rating=j1.getString("rating");
                                  orderDetailPojoList.add(new OrderDetailPojo(product_id,name,product_type,sku,description,price,Image,rating));
                                }
                                OrderDetailsAdapter adapter=new OrderDetailsAdapter(orderDetailPojoList,context);
                                rv_order_details.setAdapter(adapter);



                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.i("thisssssssss",""+e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        p_bar.setVisibility(View.GONE);
                        //  Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("order_id",order_id);
                params.put("user_id",new SharedPrefManager(context).GetCustomerId());

                params.put("language",new SharedPrefManager(OrderDetailsActivity.this).getLanguage());

                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GMarketApplication.getInstance(OrderDetailsActivity.this                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          ).addToRequestQueue(stringRequest);
    }//getOrderDetailClose


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        intent.putExtra("order_id", order_id );

     setResult(RESULT_OK, intent);
         finish();
    }
}

