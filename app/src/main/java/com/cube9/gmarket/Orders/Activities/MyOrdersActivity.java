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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.cube9.gmarket.Home.Activities.HomeActivity;
import com.cube9.gmarket.Orders.Adapters.MyOrderListAdapter;
import com.cube9.gmarket.Orders.ModelClass.OrderListPojo;
import com.cube9.gmarket.R;
import com.cube9.gmarket.SearchProducts.MasterSearchActivity;
import com.cube9.gmarket.SearchProducts.SearchProductsActivity;
import com.cube9.gmarket.WebUrls.WebUrls;
import com.cube9.gmarket.nointernet.NoInternetActivity;
import com.cube9.gmarket.receivers.NetworkChangeReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyOrdersActivity extends AppCompatActivity {
RecyclerView rv_my_orders;
TextView tv_no_item;
ProgressBar p_bar;
Context context;
    List<OrderListPojo>orderListPojoList;
    Toolbar toolbar;
    TextView tv_title,tv_cart_count;
    ImageView iv_back,iv_search,iv_cart;
    Button btn_start_shopping;
    LinearLayout ll_no_orders;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);
        initView();

        onClick();
        tv_title.setText(getResources().getString(R.string.my_orders));
        if (new SharedPrefManager(MyOrdersActivity.this).IsLogin())
        {
            tv_cart_count.setText(new SharedPrefManager(MyOrdersActivity.this).getCartCount());
        }
        else
        {
            tv_cart_count.setVisibility(View.GONE);
        }

    }//onCtreateClose

    @Override
    protected void onResume() {
        super.onResume();
        if (NetworkChangeReceiver.isOnline(MyOrdersActivity.this))
            GetAllOrders();
        else
        {
            startActivity(new Intent(MyOrdersActivity.this, NoInternetActivity.class));

        }
        if (new SharedPrefManager(MyOrdersActivity.this).IsLogin())
        {
            tv_cart_count.setText(new SharedPrefManager(MyOrdersActivity.this).getCartCount());
        }
        else
        {
            tv_cart_count.setVisibility(View.GONE);
        }

    }

    public void onClick()
    {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                startActivity(new Intent(MyOrdersActivity.this, MasterSearchActivity.class));
            }
        });
        btn_start_shopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MyOrdersActivity.this, HomeActivity.class);
                startActivity(i);
                finish();
            }
        });

    }//onClickClose

    public void initView()
    {
        context=this;
        btn_start_shopping=(Button)findViewById(R.id.btn_start_shopping);
        ll_no_orders=(LinearLayout) findViewById(R.id.ll_no_orders);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        iv_back=(ImageView)toolbar.findViewById(R.id.iv_back);
        iv_search=(ImageView)toolbar.findViewById(R.id.iv_search);
        iv_cart=(ImageView)toolbar.findViewById(R.id.iv_cart);
        tv_title=(TextView) toolbar.findViewById(R.id.tv_title);
        tv_cart_count=(TextView) toolbar.findViewById(R.id.tv_cart_count);
        rv_my_orders=(RecyclerView)findViewById(R.id.rv_my_orders);
        tv_no_item=(TextView) findViewById(R.id.tv_no_item);
        p_bar=(ProgressBar) findViewById(R.id.p_bar);
        orderListPojoList=new ArrayList<OrderListPojo>();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rv_my_orders.setLayoutManager(mLayoutManager);
        rv_my_orders.addItemDecoration(new DividerItemDecoration(this, 0));
    }//initViewClose

    public void GetAllOrders()
    {
        p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.GET_ALL_ORDERS,
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
                                orderListPojoList.clear();
                                JSONArray order=Jsonobj.getJSONArray("order");
                                for (int i=0;i<order.length();i++)
                                {
                                    JSONObject j1=order.getJSONObject(i);
                                   String entity_id=j1.getString("entity_id");
                                   String state=j1.getString("state");
                                   String order_status=j1.getString("status");
                                   String customer_id=j1.getString("customer_id");
                                   String base_discount_amount=j1.getString("base_discount_amount");
                                   String base_discount_canceled=j1.getString("base_discount_canceled");
                                   String base_grand_total=j1.getString("base_grand_total");
                                   String base_shipping_amount=j1.getString("base_shipping_amount");
                                   String base_shipping_tax_amount=j1.getString("base_shipping_tax_amount");
                                   String base_subtotal=j1.getString("base_subtotal");
                                   String discount_amount=j1.getString("discount_amount");
                                   String grand_total=j1.getString("grand_total");
                                   String shipping_amount=j1.getString("shipping_amount");
                                   String total_qty_ordered=j1.getString("total_qty_ordered");
                                   String created_at=j1.getString("created_at");
                                   String increment_id=j1.getString("increment_id");
                                   String customer_firstname=j1.getString("customer_firstname");
                                   String customer_lastname=j1.getString("customer_lastname");
                                   String customer_name=customer_firstname+" "+customer_lastname;

                                    DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                    DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
                                    String inputDateStr=created_at;
                                    Date date = null;
                                    try {
                                        date = inputFormat.parse(inputDateStr);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    String outputDateStr = outputFormat.format(date);
                                   orderListPojoList.add(new OrderListPojo(increment_id,customer_name,outputDateStr,grand_total,order_status));
                                }
                                if (order.length()>0)
                                {

                                    MyOrderListAdapter orderListAdapter=new MyOrderListAdapter(orderListPojoList,context);
                                    rv_my_orders.setAdapter(orderListAdapter);
                                    ll_no_orders.setVisibility(View.GONE);
                                }
                                else
                                {

                                    rv_my_orders.setAdapter(null);
                                    ll_no_orders.setVisibility(View.VISIBLE);
                                }


                            }
                            else
                            {
                                ll_no_orders.setVisibility(View.VISIBLE);
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
                params.put("user_id",new SharedPrefManager(MyOrdersActivity.this).GetCustomerId());
                params.put("language",new SharedPrefManager(MyOrdersActivity.this).getLanguage());


                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GMarketApplication.getInstance(MyOrdersActivity.this                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          ).addToRequestQueue(stringRequest);
    }
}
