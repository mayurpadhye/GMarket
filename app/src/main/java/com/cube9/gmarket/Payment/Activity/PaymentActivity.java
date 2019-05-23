package com.cube9.gmarket.Payment.Activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cube9.gmarket.HelperClass.CustomUtils;
import com.cube9.gmarket.HelperClass.GMarketApplication;
import com.cube9.gmarket.HelperClass.SharedPrefManager;
import com.cube9.gmarket.Orders.Activities.OrderSuccessActivity;
import com.cube9.gmarket.Payment.ModelClass.StorePickUpPojo;
import com.cube9.gmarket.Payment.adapter.StorePickupAdapter;
import com.cube9.gmarket.R;
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

public class PaymentActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView tv_title;
    ImageView iv_back,iv_search,iv_cart;
    TextView tv_cart_count;
    Button btn_place_order;
    TextView tv_final_price,tv_payable_amount,tv_delivery,tv_price;
    RadioButton rb_cash_on_delivery,rb_atm_debit;
    ProgressBar p_bar;
    String address_id="",intent_from="";
    String product_id="";
    String total_qty="";
    LinearLayout ll_shipping_method;
    RadioGroup rg_shipping_method;
    String shipping_method="",shipping_method_title="";
    List<StorePickUpPojo>storePickUpPojoList;
    RadioGroup.LayoutParams rprms;
    String address="",price="";
    ListView lv_open_store_pickup;
    Dialog dialog;
    TextView tv_shipping_method_title;
    String grand_total="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        initView();
        onClick();
        getShippingMethod();

    }


    public void getShippingMethod()
    {
        p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.GET_SHIPPING_METHOD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        p_bar.setVisibility(View.GONE);
                        try {
                            storePickUpPojoList.clear();
                            JSONObject jsonObject=new JSONObject(response);
                            String status=jsonObject.getString("status");
                            if (status.equals("1"))
                            {

                                JSONArray shipMethods=jsonObject.getJSONArray("shipMethods");
                                for (int i=0;i<shipMethods.length();i++)
                                {
                                    JSONObject j1=shipMethods.getJSONObject(i);
                                    final String name=j1.getString("name");
                                    String title=j1.getString("title");
                                    RadioButton radioButton = new RadioButton(PaymentActivity.this);
                                    radioButton.setText(" "+title);
                                    radioButton.setId(i);
                                    rprms= new RadioGroup.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    JSONArray store_list=j1.getJSONArray("store_list");
                                    for (int k=0;k<store_list.length();k++)
                                    {

                                        JSONObject j2=store_list.getJSONObject(k);
                                        String title1=j2.getString("title");
                                        String price=j2.getString("price");
                                        String address=j2.getString("address");
                                        storePickUpPojoList.add(new StorePickUpPojo(title1,price,address));

                                    }

                                    radioButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            if (name.equals("tablerate"))
                                            {
                                                address="";
                                                price="";
                                                shipping_method="tablerate";
                                            }
                                            else if(name.equals("storepickupmodule"))
                                            {

                                               openStorePickUpDialog();
                                            }

                                        }
                                    });
                                   // rg_shipping_method.addView(layout1);
                                    rg_shipping_method.addView(radioButton, rprms);
                                }

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
                        //  Toast.makeText(PaymentActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("language",new SharedPrefManager(PaymentActivity.this).getLanguage());


                return params;
            }
        };

        GMarketApplication.getInstance(PaymentActivity.this).addToRequestQueue(stringRequest);

    }//getShippingMethodClose

    public  void openStorePickUpDialog()
    {
       dialog =new Dialog(PaymentActivity.this);
        dialog.setContentView(R.layout.dialog_open_store_pick_up);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
         lv_open_store_pickup=(ListView)dialog.findViewById(R.id.lv_open_store_pickup);
        ProgressBar p_bar=(ProgressBar) dialog.findViewById(R.id.p_bar);
        StorePickupAdapter adapter=new StorePickupAdapter(PaymentActivity.this,storePickUpPojoList,"");
        lv_open_store_pickup.setAdapter(adapter);

        lv_open_store_pickup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                String item = storePickUpPojoList.get(position).getAddress();

                Toast.makeText(PaymentActivity.this, item, Toast.LENGTH_SHORT).show();

            }
        });


       
        dialog.show();
    }
public void getStorePIckUpDetails(int position)
{
    address=storePickUpPojoList.get(position).getAddress();
    price=storePickUpPojoList.get(position).getPrice();
    shipping_method="storepickupmodule";
    dialog.dismiss();
}

    private void onClick() {

   iv_back.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View v) {
           finish();
       }
   });

        rg_shipping_method.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb && checkedId > -1) {

                }

            }
        });

   btn_place_order.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View v) {
           if (NetworkChangeReceiver.isOnline(PaymentActivity.this))
           {
               if (shipping_method.equals(""))
               {
                   CustomUtils.showToast(getResources().getString(R.string.select_shipping_method),getApplicationContext());
                  // Toast.makeText(PaymentActivity.this, , Toast.LENGTH_SHORT).show();
               }
               else {

                   if (intent_from.equals("buy_now_activity"))
                   {

                       placeBuyNowOrder();
                   }
                   else
                       PlaceOrder();

               }
       }
           else
               startActivity(new Intent(PaymentActivity.this,NoInternetActivity.class));

       }
   });
    }

    private void placeBuyNowOrder() {
        p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.PLACE_ORDER_BUY_NOW,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        p_bar.setVisibility(View.GONE);
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            String status=jsonObject.getString("status");
                            if (status.equals("1"))
                            {
                                String order_id=jsonObject.getString("order_id");
                                String message=jsonObject.getString("message");
                               if (rb_atm_debit.isChecked())
                               {
                                   Intent i= new Intent(PaymentActivity.this, AirtelMoneyWebView.class);
                                   i.putExtra("order_id",order_id);
                                   i.putExtra("amount",price);
                                   startActivityForResult(i,1);
                               }
                               else
                               {
                                   Intent i= new Intent(PaymentActivity.this, OrderSuccessActivity.class);
                                   i.putExtra("order_id",order_id);
                                   startActivity(i);
                               }


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
                      //  Toast.makeText(PaymentActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("language",new SharedPrefManager(PaymentActivity.this).getLanguage());
                params.put("user_id",new SharedPrefManager(PaymentActivity.this).GetCustomerId());
                params.put("billing_address_id",address_id);
                params.put("shipping_address_id",address_id);
                params.put("payment_method","1");
                params.put("product_id",product_id);
                params.put("qty",total_qty);
                params.put("shipping_method",shipping_method);
                params.put("address",address);
                params.put("price",price);
                params.put("language",new SharedPrefManager(PaymentActivity.this).getLanguage());

                return params;
            }
        };

        GMarketApplication.getInstance(PaymentActivity.this).addToRequestQueue(stringRequest);
    }//placeBuyNOwOrderclose


    public void getProductDetails(final String product_id)
    {
        p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.PRODUCT_BUY_NOW_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        p_bar.setVisibility(View.GONE);
                        try {
                            Log.i("ButNowRes",""+response.toString());
                            JSONObject jsonObject=new JSONObject(response);
                            String status=jsonObject.getString("status");
                            String price=jsonObject.getString("price");
                            String sub_total=jsonObject.getString("sub_total");
                            String delivery_charges=jsonObject.getString("delivery_charges");
                            String grand_total=jsonObject.getString("grand_total");
                            setProductDetailsData(sub_total,delivery_charges,grand_total);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        p_bar.setVisibility(View.GONE);
                       // Toast.makeText(PaymentActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("language", new SharedPrefManager(PaymentActivity.this).getLanguage());
                params.put("product_id", product_id);
                params.put("user_id", new SharedPrefManager(PaymentActivity.this).GetCustomerId());
                params.put("shipping_method", shipping_method);
                params.put("shipping_address_id", address_id);
                params.put("price", price);
                params.put("qty", total_qty);

                params.put("language",new SharedPrefManager(PaymentActivity.this).getLanguage());


                return params;
            }
        };

        GMarketApplication.getInstance(PaymentActivity.this).addToRequestQueue(stringRequest);
    }
    public void setProductDetailsData(String sub_total,String delivery_charge,String grand_total) {
        if (!sub_total.equals("null")) {

            //tv_price.setText("" + new DecimalFormat("##.##").format(Double.parseDouble(sub_total))+" "+getResources().getString(R.string.FCFA));
            tv_price.setText("" + sub_total+" "+getResources().getString(R.string.FCFA));

        }
     //   tv_price.setText("" + new DecimalFormat("##.##").format(Double.parseDouble(sub_total))+" "+getResources().getString(R.string.FCFA));
        tv_price.setText("" +sub_total+" "+getResources().getString(R.string.FCFA));

      //  tv_final_price.setText("" + new DecimalFormat("##.##").format(Double.parseDouble(grand_total))+" "+getResources().getString(R.string.FCFA));
        tv_final_price.setText("" + grand_total+" "+getResources().getString(R.string.FCFA));
        tv_payable_amount.setText("" +grand_total+" "+getResources().getString(R.string.FCFA));
    //    tv_payable_amount.setText("" + new DecimalFormat("##.##").format(Double.parseDouble(grand_total))+" "+getResources().getString(R.string.FCFA));
        tv_delivery.setText(""+delivery_charge+" "+getResources().getString(R.string.FCFA));



    }
    private void initView() {
        storePickUpPojoList=new ArrayList<StorePickUpPojo>();
        rg_shipping_method=(RadioGroup)findViewById(R.id.rg_shipping_method);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        iv_back=(ImageView)toolbar.findViewById(R.id.iv_back);
        iv_search=(ImageView)toolbar.findViewById(R.id.iv_search);
        iv_cart=(ImageView)toolbar.findViewById(R.id.iv_cart);
        tv_title=(TextView) toolbar.findViewById(R.id.tv_title);
        tv_cart_count=(TextView) toolbar.findViewById(R.id.tv_cart_count);
        btn_place_order=(Button)findViewById(R.id.btn_place_order);
        tv_final_price=(TextView)findViewById(R.id.tv_final_price);
        tv_payable_amount=(TextView)findViewById(R.id.tv_payable_amount);
        tv_delivery=(TextView)findViewById(R.id.tv_delivery);
        tv_shipping_method_title=(TextView)findViewById(R.id.tv_shipping_method_title);
        tv_price=(TextView)findViewById(R.id.tv_price);
        rb_cash_on_delivery=(RadioButton)findViewById(R.id.rb_cash_on_delivery);
        rb_atm_debit=(RadioButton)findViewById(R.id.rb_atm_debit);
        p_bar=(ProgressBar) findViewById(R.id.p_bar);
        ll_shipping_method=(LinearLayout)findViewById(R.id.ll_shipping_method);
        tv_title.setText(getResources().getString(R.string.payment));
        Intent i=getIntent();
        address_id=i.getStringExtra("address_id");
        address=i.getStringExtra("address");
        shipping_method=i.getStringExtra("shipping_method");
        Log.i("shipping",""+shipping_method);

        shipping_method_title=i.getStringExtra("shipping_method_title");
        tv_shipping_method_title.setText(shipping_method_title);
        price=i.getStringExtra("price");
        try {

            if(i.hasExtra("intent_from")) {
                intent_from = i.getStringExtra("intent_from");
                product_id = i.getStringExtra("product_id"); //Do first time stuff here
                if (intent_from.equals("buy_now_activity"))
                {
                    total_qty=i.getStringExtra("total_qty");
                }

            } else {
                //Do stuff with intent data here
                intent_from="";
                product_id="";
            }

        }
        catch (Exception e)
        {
            intent_from="";
            product_id="";
            e.printStackTrace();
        }

    }//initViewClose
    @Override
    protected void onResume() {
        super.onResume();
        iv_search.setVisibility(View.GONE);
        iv_cart.setVisibility(View.GONE);
        tv_cart_count.setVisibility(View.GONE);

        if (NetworkChangeReceiver.isOnline(PaymentActivity.this))
            if(intent_from.equals("buy_now_activity"))
            {
                getProductDetails(product_id);
            }
            else
            {
                PriceDetails();
            }
        else
        {
            startActivity(new Intent(PaymentActivity.this, NoInternetActivity.class));

        }


    }

    public void PlaceOrder()
    {

        p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.PLACE_ORDER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        p_bar.setVisibility(View.GONE);
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            Log.i("jsonskdlsk",""+jsonObject.toString());
                            String status=jsonObject.getString("status");
                            if (status.equals("1"))
                            {
                                String order_id=jsonObject.getString("order_id");
                                String message=jsonObject.getString("message");
                                new SharedPrefManager(PaymentActivity.this).setCartCount("0");

                                if (rb_atm_debit.isChecked())
                                {
                                    Intent i= new Intent(PaymentActivity.this, AirtelMoneyWebView.class);
                                    i.putExtra("order_id",order_id);
                                    i.putExtra("amount",grand_total);
                                    startActivity(i);
                                }
                                else
                                {
                                    Intent i= new Intent(PaymentActivity.this, OrderSuccessActivity.class);
                                    i.putExtra("order_id",order_id);
                                    //i.putExtra("amount",grand_total);
                                    startActivity(i);
                                }


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
                      //  Toast.makeText(PaymentActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("language",new SharedPrefManager(PaymentActivity.this).getLanguage());
                params.put("user_id",new SharedPrefManager(PaymentActivity.this).GetCustomerId());
                params.put("shipping_address_id",address_id);
                params.put("payment_method","1");
                params.put("shipping_method",shipping_method);
                params.put("address",address);
                params.put("price",price);

                params.put("language",new SharedPrefManager(PaymentActivity.this).getLanguage());

                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GMarketApplication.getInstance(PaymentActivity.this).addToRequestQueue(stringRequest);
    }//

    public void PriceDetails()
    {

        p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.PRICE_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        p_bar.setVisibility(View.GONE);
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            Log.i("jsonObject",""+jsonObject.toString());
                            String status=jsonObject.getString("status");
                            if (status.equals("1"))
                            {
                                String sub_total=jsonObject.getString("sub_total");
                                String delivery_charges=jsonObject.getString("delivery_charges");
                                 grand_total=jsonObject.getString("grand_total");

                                tv_price.setText("" + sub_total+" "+getResources().getString(R.string.FCFA));
                             //   tv_price.setText("" + new DecimalFormat("##.##").format(Double.parseDouble(sub_total))+" "+getResources().getString(R.string.FCFA));
                                tv_payable_amount.setText("" + grand_total+" "+getResources().getString(R.string.FCFA));
                             //   tv_payable_amount.setText("" + new DecimalFormat("##.##").format(Double.parseDouble(grand_total))+" "+getResources().getString(R.string.FCFA));
                                tv_final_price.setText("" +grand_total+" "+getResources().getString(R.string.FCFA));
                              //  tv_final_price.setText("" + new DecimalFormat("##.##").format(Double.parseDouble(grand_total))+" "+getResources().getString(R.string.FCFA));


                                if (delivery_charges.equals("0"))
                                tv_delivery.setText("Free");
                                else
                                    tv_delivery.setText(""+delivery_charges+" "+getResources().getString(R.string.FCFA));



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
                       // Toast.makeText(PaymentActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("language",new SharedPrefManager(PaymentActivity.this).getLanguage());
                params.put("user_id",new SharedPrefManager(PaymentActivity.this).GetCustomerId());
                params.put("billing_address_id",address_id);
                params.put("shipping_address_id",address_id);
                params.put("payment_method","1");
                params.put("shipping_method",shipping_method);
                params.put("price",price);


                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GMarketApplication.getInstance(PaymentActivity.this).addToRequestQueue(stringRequest);
    }//PriceDetailsClose
}
