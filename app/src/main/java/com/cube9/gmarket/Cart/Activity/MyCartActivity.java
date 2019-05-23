package com.cube9.gmarket.Cart.Activity;

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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cube9.gmarket.Account.Activity.AddNewAddressActivity;
import com.cube9.gmarket.Cart.Adapter.MyCartAdapter;
import com.cube9.gmarket.Delivery.Activity.DeliveryDetailsActivity;
import com.cube9.gmarket.Delivery.ModelClass.CartProductPojo;
import com.cube9.gmarket.HelperClass.GMarketApplication;
import com.cube9.gmarket.HelperClass.SharedPrefManager;
import com.cube9.gmarket.Home.Activities.HomeActivity;
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

import static com.cube9.gmarket.HelperClass.CustomUtils.showOKAlertDialog;

public class MyCartActivity extends AppCompatActivity {
    RecyclerView rv_cart;
    ProgressBar p_bar;
    Context context;
    Toolbar toolbar;
    TextView tv_title,tv_cart_count;
    ImageView iv_back,iv_search,iv_cart;
    List<Integer> listProductQuantity;
    List<CartProductPojo> cartProductPojoList;
    TextView tv_final_total,tv_empty_cart;
    Button btn_continue,btn_shop_now;
    RelativeLayout rl_continue,rl_no_items;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cart);
        initView();
        onClick();


    }


    public void initView()
    {
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        iv_back=(ImageView)toolbar.findViewById(R.id.iv_back);
        iv_search=(ImageView)toolbar.findViewById(R.id.iv_search);
        iv_cart=(ImageView)toolbar.findViewById(R.id.iv_cart);
        tv_title=(TextView) toolbar.findViewById(R.id.tv_title);
        tv_cart_count=(TextView) toolbar.findViewById(R.id.tv_cart_count);
        tv_empty_cart=(TextView) findViewById(R.id.tv_empty_cart);
        tv_final_total=(TextView)findViewById(R.id.tv_final_total);
        btn_continue=(Button)findViewById(R.id.btn_continue);
        cartProductPojoList=new ArrayList<CartProductPojo>();
        context=MyCartActivity.this;
        listProductQuantity=new ArrayList<Integer>();
        rv_cart=(RecyclerView)findViewById(R.id.rv_cart);
        p_bar=(ProgressBar)findViewById(R.id.p_bar);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rv_cart.setLayoutManager(mLayoutManager);
        rv_cart.addItemDecoration(new DividerItemDecoration(this, 0));
        rl_continue=(RelativeLayout)findViewById(R.id.rl_continue);
        rl_no_items=(RelativeLayout)findViewById(R.id.rl_no_items);
        btn_shop_now=(Button)findViewById(R.id.btn_shop_now);
        tv_title.setText(getResources().getString(R.string.my_cart));



    }//initViewClose

    public void onClick()
    {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_shop_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MyCartActivity.this, HomeActivity.class);
                startActivity(i);
                finish();
            }
        });
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i=0;i<cartProductPojoList.size();i++)
                {
                    if (cartProductPojoList.get(i).getIs_in_stock().equals("0"))
                    {
                        showOKAlertDialog(MyCartActivity.this,getResources().getString(R.string.out_of_stock),getResources().getString(R.string.product_out_of_stock_error));
                        return;
                    }
                }
                getAllProductsForDelivery();

            }
        });
    }//onClickClose

    @Override
    protected void onResume() {
        super.onResume();
        iv_cart.setVisibility(View.GONE);
        iv_search.setVisibility(View.GONE);
        tv_cart_count.setVisibility(View.GONE);
        if (NetworkChangeReceiver.isOnline(MyCartActivity.this))
            GetMyCartProducts();
        else
        {
            startActivity(new Intent(MyCartActivity.this, NoInternetActivity.class));

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
       /* iv_cart.setVisibility(View.VISIBLE);
        iv_search.setVisibility(View.VISIBLE);
        tv_cart_count.setVisibility(View.VISIBLE);*/
    }

    public void GetMyCartProducts()
    {
        p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.GET_ALL_CART_PRODUCTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        p_bar.setVisibility(View.GONE);
                        try {
                            cartProductPojoList.clear();
                            JSONObject Jsonobj = new JSONObject(response);
                            String status=Jsonobj.getString("status");
                            Log.i("cart_details",""+Jsonobj.toString());
                            if (status.equals("1"))
                            {
                                cartProductPojoList.clear();
                             String total_amount=Jsonobj.getString("total_amount");
                           //  tv_final_total.setText("FCFA "+total_amount);
                            //    tv_final_total.setText("FCFA " + new DecimalFormat("##.##").format(Double.parseDouble(total_amount)));
                                tv_final_total.setText(total_amount+" FCFA");
                                JSONArray products=Jsonobj.getJSONArray("products");
                                int k=0;
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
                                    String selected_quantity=j1.getString("selected_quantity");
                                    String quantity_available   =j1.getString("quantity_available");
                                    String is_in_stock   =j1.getString("is_in_stock");
                                    cartProductPojoList.add(new CartProductPojo(product_name,product_special_price,product_image,product_price,product_discount,product_id,selected_quantity,quantity_available,is_in_stock));
                                }
                                  if (products.length()>0)
                                  {
                                      rl_continue.setVisibility(View.VISIBLE);
                                      MyCartAdapter myCartAdapter=new MyCartAdapter(cartProductPojoList,MyCartActivity.this,p_bar,tv_final_total,rl_continue,rl_no_items,tv_title);
                                      rv_cart.setAdapter(myCartAdapter);
                                      tv_title.setText(getResources().getString(R.string.my_cart)+" "+"("+k+")");
                                  }
                                  else
                                  {
                                      rl_continue.setVisibility(View.GONE);
                                      tv_title.setText(getResources().getString(R.string.my_cart)+" (0)");
                                      new SharedPrefManager(MyCartActivity.this).setCartCount("0");
                                  }



                            }
                            else
                            {
                                rl_continue.setVisibility(View.GONE);
                                rl_no_items.setVisibility(View.VISIBLE);
                                tv_title.setText(getResources().getString(R.string.my_cart)+" (0)");
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, ""+e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        p_bar.setVisibility(View.GONE);
                    //    Toast.makeText(MyCartActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("language",new SharedPrefManager(MyCartActivity.this).getLanguage());
                params.put("user_id",new SharedPrefManager(MyCartActivity.this).GetCustomerId());


                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GMarketApplication.getInstance(MyCartActivity.this).addToRequestQueue(stringRequest);
    }//MyCartActivityClose


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

                            Log.i("Price_details",""+Jsonobj.toString());
                            String status=Jsonobj.getString("status");
                            String total_amount=Jsonobj.getString("total_amount");
                         //   tv_final_total.setText("FCFA "+total_amount);
                            String final_total = total_amount.replaceAll(",","");
                         //   tv_final_total.setText("FCFA " + new DecimalFormat("##.##").format(Double.parseDouble(final_total)));
                            tv_final_total.setText(final_total+" FCFA");
                            if (status.equals("1"))
                            {
                                cartProductPojoList.clear();
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
                                    String is_in_stock=j1.getString("is_in_stock");
                                    cartProductPojoList.add(new CartProductPojo(product_name,product_special_price,product_image,product_price,product_discount,product_id,selected_quantity,quantity_available,is_in_stock));
                                }
                                if (!Jsonobj.isNull("default_shipping_address"))
                                {


                                    JSONArray default_shipping_address=Jsonobj.getJSONArray("default_shipping_address");

                                   for (int i=0;i<default_shipping_address.length();i++)
                                   {
                                       JSONObject address=default_shipping_address.getJSONObject(i);
                                       String address_fname=address.getString("address_fname");
                                       String address_lname=address.getString("address_lname");
                                       String telephone=address.getString("telephone");
                                       JSONArray street=address.getJSONArray("street");
                                       String street1="";
                                       String street2="";
                                       for (int k=0;k<street.length();k++)
                                       {
                                           street1=""+street.get(0);
                                           street2=""+street.get(1);
                                       }
                                       String city=address.getString("city");
                                       String zip=address.getString("zip");
                                       String state=address.getString("state");
                                       String country=address.getString("country");
                                       startActivity(new Intent(MyCartActivity.this, DeliveryDetailsActivity.class));

                                   }
                                   if (default_shipping_address.length()==0)
                                   {
                                       Intent i=new Intent(MyCartActivity.this, AddNewAddressActivity.class);
                                       i.putExtra("intent_from","delivery_detail");
                                       startActivityForResult(i, 1);
                                   }
                                     }
                                else
                                {

                                }



                            }


                        } catch (JSONException e) {
                            Log.i("ProductListJson",""+e.toString());
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        p_bar.setVisibility(View.GONE);
                       // Toast.makeText(MyCartActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("language",new SharedPrefManager(MyCartActivity.this).getLanguage());
                params.put("user_id",new SharedPrefManager(MyCartActivity.this).GetCustomerId());


                return params;
            }
        };

        GMarketApplication.getInstance(MyCartActivity.this).addToRequestQueue(stringRequest);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                String intent_from = data.getStringExtra("intent_from");
                if (intent_from.equals("delivery_detail"))
                {
                    startActivity(new Intent(MyCartActivity.this, DeliveryDetailsActivity.class));
                }

            }
        }
    }

    public void setActivityInteraction()
    {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
    public void enableActivityInteraction()
    {

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}
