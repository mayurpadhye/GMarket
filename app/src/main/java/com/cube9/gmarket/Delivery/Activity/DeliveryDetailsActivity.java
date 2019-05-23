package com.cube9.gmarket.Delivery.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
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
import com.cube9.gmarket.Account.Activity.AllAddressActivity;
import com.cube9.gmarket.Delivery.Adapter.DeliveryProductAdapter;
import com.cube9.gmarket.Delivery.ModelClass.CartProductPojo;
import com.cube9.gmarket.HelperClass.CustomUtils;
import com.cube9.gmarket.HelperClass.GMarketApplication;
import com.cube9.gmarket.HelperClass.SharedPrefManager;
import com.cube9.gmarket.Payment.Activity.PaymentActivity;
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

public class DeliveryDetailsActivity extends AppCompatActivity {
ProgressBar p_bar;
ListView lv_order_details;
List<CartProductPojo> cartProductPojoList;
TextView tv_final_total;
    Button btn_continue;
   public static String address_id="";
    RadioGroup.LayoutParams rprms;
    String address="",price="";
    String from="home_delivery";
    String shipping_method="";
    Dialog dialog;
    List<StorePickUpPojo>storePickUpPojoList;
    RadioGroup rg_shipping_method;
 //---------header--------
    View header;
    TextView tv_name,tv_address,tv_pincode;
    Button btn_change_address;
    //--------header Close----------
   //--------Toolbar--------
     Toolbar toolbar;
    TextView tv_title,tv_state_city,tv_cart_count;
    ImageView iv_back,iv_search,iv_cart;
    String intent="",product_id="";
    Intent i;
     String title="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_details);
        initView();
        onClick();




    }//onCreateClose
    @Override
    protected void onResume() {
        super.onResume();
        iv_cart.setVisibility(View.GONE);
        iv_search.setVisibility(View.GONE);
        tv_cart_count.setVisibility(View.GONE);
        if (NetworkChangeReceiver.isOnline(DeliveryDetailsActivity.this)) {
            getAllProductsForDelivery();
            rg_shipping_method.removeAllViews();
            getShippingMethod(rg_shipping_method);
        }
        else
        {
            startActivity(new Intent(DeliveryDetailsActivity.this, NoInternetActivity.class));

        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }
    public void onClick()
    {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_change_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(DeliveryDetailsActivity.this,AllAddressActivity.class);
                i.putExtra("intent_from","delivery_detail");

                startActivityForResult(i, 1);
            }
        });
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
if (from.equals("home_delivery"))
{
    Intent i=new Intent(DeliveryDetailsActivity.this, PaymentActivity.class);
    i.putExtra("address_id",address_id);
    i.putExtra("address",address);
    i.putExtra("price",price);
    i.putExtra("shipping_method",shipping_method);
    i.putExtra("shipping_method_title",getResources().getString(R.string.home_delivery));
    startActivity(i);

}
else if (from.equals(""))
{
    Toast.makeText(DeliveryDetailsActivity.this, ""+getResources().getString(R.string.plese_select_shipping_method), Toast.LENGTH_SHORT).show();
    return;
}

               /* BottomSheetDialog dialog=new BottomSheetDialog(DeliveryDetailsActivity.this);
                dialog.setContentView(R.layout.bootm_sheet_store_pickup);
                RadioGroup rg_shipping_method=dialog.findViewById(R.id.rg_shipping_method);
                dialog.show();
                getShippingMethod(rg_shipping_method);*/
            }
        });
    }

    public void getShippingMethod( final RadioGroup rg_shipping_method)
    {
        p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.GET_SHIPPING_METHOD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        p_bar.setVisibility(View.GONE);
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            String status=jsonObject.getString("status");
                            if (status.equals("1"))
                            {
                                storePickUpPojoList.clear();
                                JSONArray shipMethods=jsonObject.getJSONArray("shipMethods");
                                for (int i=0;i<shipMethods.length();i++)
                                {
                                    View layout1 = LayoutInflater.from(DeliveryDetailsActivity.this).inflate(R.layout.row_shipping_method, rg_shipping_method, false);
                                    JSONObject j1=shipMethods.getJSONObject(i);
                                    final String name=j1.getString("name");
                                     title=j1.getString("title");
                                    RadioButton radioButton = new RadioButton(DeliveryDetailsActivity.this);
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


                                    final RadioButton rb_shipping=(RadioButton)layout1.findViewById(R.id.rb_shipping);
                                    rb_shipping.setId((1 * 2) + i);


                                    rb_shipping.setText(title);
                                    shipping_method="tablerate";
                                    from="home_delivery";
                                    radioButton.setChecked(true);
                                    radioButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            if (name.equals("tablerate"))
                                            {
                                                address="";
                                                price="";
                                                shipping_method="tablerate";

                                                from="home_delivery";
                                              /*  Intent i=new Intent(DeliveryDetailsActivity.this, PaymentActivity.class);
                                                i.putExtra("address_id",address_id);
                                                i.putExtra("address",address);
                                                i.putExtra("price",price);
                                                i.putExtra("shipping_method",shipping_method);
                                                i.putExtra("shipping_method_title",getResources().getString(R.string.home_delivery));
                                                startActivity(i);
                                                dialog.dismiss();*/
                                            }
                                            else if(name.equals("storepickupmodule"))
                                            {
                                                from="store_pickup";
                                              //  dialog.dismiss();
                                                openStorePickUpDialog();
                                            }

                                        }
                                    });
                                    // rg_shipping_method.addView(layout1);
                                    rg_shipping_method.addView(radioButton, rprms);
                                }

                            }
                            else
                            {
                                Intent i=new Intent(DeliveryDetailsActivity.this, PaymentActivity.class);
                                i.putExtra("address_id",address_id);
                                i.putExtra("address","");
                                i.putExtra("price","");
                                i.putExtra("shipping_method","tablerate");
                                i.putExtra("shipping_method_title",getResources().getString(R.string.home_delivery));
                                startActivity(i);
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
                params.put("language",new SharedPrefManager(DeliveryDetailsActivity.this).getLanguage());


                return params;
            }
        };

        GMarketApplication.getInstance(DeliveryDetailsActivity.this).addToRequestQueue(stringRequest);

    }//getShippingMethodClose
    public  void openStorePickUpDialog()
    {
        dialog =new Dialog(DeliveryDetailsActivity.this);
        dialog.setContentView(R.layout.dialog_open_store_pick_up);
dialog.setCancelable(false);
dialog.setCanceledOnTouchOutside(false);
        ListView  lv_open_store_pickup=(ListView)dialog.findViewById(R.id.lv_open_store_pickup);
        ProgressBar p_bar=(ProgressBar) dialog.findViewById(R.id.p_bar);
        StorePickupAdapter adapter=new StorePickupAdapter(DeliveryDetailsActivity.this,storePickUpPojoList,"");
        lv_open_store_pickup.setAdapter(adapter);


        dialog.show();
    }
    public void getStorePIckUpDetails(int position)
    {
        address=storePickUpPojoList.get(position).getAddress();
        price=storePickUpPojoList.get(position).getPrice();
        shipping_method="storepickupmodule";
        Intent i=new Intent(DeliveryDetailsActivity.this, PaymentActivity.class);
        i.putExtra("address_id",address_id);
        i.putExtra("address",address);
        i.putExtra("price",price);
        i.putExtra("shipping_method",shipping_method);
        i.putExtra("shipping_method_title","Store Pickup");
        startActivity(i);
        dialog.dismiss();
    }
    public void initView()
    {
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        iv_back=(ImageView)toolbar.findViewById(R.id.iv_back);
        iv_search=(ImageView)toolbar.findViewById(R.id.iv_search);
        iv_cart=(ImageView)toolbar.findViewById(R.id.iv_cart);
        tv_title=(TextView) toolbar.findViewById(R.id.tv_title);
        tv_cart_count=(TextView) toolbar.findViewById(R.id.tv_cart_count);
        p_bar=(ProgressBar)findViewById(R.id.p_bar);
        lv_order_details=(ListView) findViewById(R.id.lv_order_details);
        cartProductPojoList=new ArrayList<CartProductPojo>();
        storePickUpPojoList=new ArrayList<StorePickUpPojo>();
        tv_final_total=(TextView)findViewById(R.id.tv_final_total);
        btn_continue=(Button)findViewById(R.id.btn_continue);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        header = inflater.inflate(R.layout.header_delivery_address, null);
        tv_name=(TextView) header.findViewById(R.id.tv_name);
        rg_shipping_method=(RadioGroup) header.findViewById(R.id.rg_shipping_method);
        tv_address=(TextView) header.findViewById(R.id.tv_address);
        tv_pincode=(TextView) header.findViewById(R.id.tv_pincode);
        tv_state_city=(TextView) header.findViewById(R.id.tv_state_city);
        btn_change_address=(Button) header.findViewById(R.id.btn_change_address);
        lv_order_details.addHeaderView(header);
        tv_title.setText(getResources().getString(R.string.delivery));

    }//initViewClose



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
                            String status=Jsonobj.getString("status");
                            String total_amount=Jsonobj.getString("total_amount");
                            tv_final_total.setText(total_amount+" FCFA");
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
                                    for(int k=0;k<default_shipping_address.length();k++)
                                    {
                                        JSONObject address=default_shipping_address.getJSONObject(k);
                                        String address_fname=address.getString("address_fname");
                                        address_id=address.getString("address_id");
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
                                        setAddressDetails(address_fname,address_lname,telephone,street1,street2,city,zip,state,country);
                                       // getProductDetails(product_id);
                                    }
                                    if (default_shipping_address.length()==0)
                                    {
                                        CustomUtils.showToast(getResources().getString(R.string.please_add_shipping_address),DeliveryDetailsActivity.this);
                                        finish();
                                    }

                                }
                                else
                                {
                                  /*  Intent i=new Intent(DeliveryDetailsActivity.this, AddNewAddressActivity.class);
                                    i.putExtra("intent_form","delivery_add_address");
                                    startActivityForResult(i, 1);*/
                                }
                                DeliveryProductAdapter deliveryProductAdapter=new DeliveryProductAdapter(DeliveryDetailsActivity.this,cartProductPojoList);
                                lv_order_details.setAdapter(deliveryProductAdapter);


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
                       // Toast.makeText(DeliveryDetailsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("language",new SharedPrefManager(DeliveryDetailsActivity.this).getLanguage());
                params.put("user_id",new SharedPrefManager(DeliveryDetailsActivity.this).GetCustomerId());


                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GMarketApplication.getInstance(DeliveryDetailsActivity.this).addToRequestQueue(stringRequest);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                String intent_from = data.getStringExtra("intent_from");
                if (intent_from.equals("delivery_detail"))
                {
                    getAllProductsForDelivery();
                }

            }
        }
    }


    public void setAddressDetails(String address_fname, String address_lname, String telephone, String street1, String street2, String city, String zip, String state, String country)
    {
       tv_name.setText(address_fname+" "+address_lname);
       tv_address.setText(street1+", "+street2);
       tv_pincode.setText(zip);
       tv_state_city.setText(city+", "+state+", "+country);

    }//setAddressDetalClose


}
