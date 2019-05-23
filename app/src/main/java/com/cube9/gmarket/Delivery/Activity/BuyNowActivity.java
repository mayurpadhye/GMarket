package com.cube9.gmarket.Delivery.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cube9.gmarket.Account.Activity.AllAddressActivity;
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
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuyNowActivity extends AppCompatActivity {
String product_id="";
    TextView tv_name,tv_address,tv_pincode,tv_state_city;
    Button btn_change_address;
    TextView tv_product_name,tv_seller,tv_price,tv_final_total,tv_discounted_pice,tv_discount;
    Button btn_continue;
    ImageView iv_product_image;
    public static String address_id="";
    ProgressBar p_bar;
    static String final_price="0";
    Dialog dialog;
    LinearLayout ll_address_details;
    List<StorePickUpPojo> storePickUpPojoList;
    CardView cv_continue;
    String title="";
    RadioGroup.LayoutParams rprms;
    //--------Toolbar--------
    Toolbar toolbar;
    TextView tv_title,tv_cart_count;
    ImageView iv_back,iv_search,iv_cart;
    Spinner sp_qty;
    TextView tv_qty;
    String address="",price="";
    String shipping_method="";
    static  int p_qty=1;
    String from="home_delivery";
    RadioGroup rg_shipping_method;
    private final String[] quantityValues = new String[] { "1", "2", "3", "4",
            "5","more" };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_now);
        initView();
        getShippingMethod(rg_shipping_method);
        onClick();
      //  getAllProductsForDelivery();

    }//onCreateClose

    private void onClick() {
        btn_change_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(BuyNowActivity.this,AllAddressActivity.class);
                i.putExtra("intent_from","delivery_detail");
                startActivityForResult(i, 1);
            }
        });
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* BottomSheetDialog dialog=new BottomSheetDialog(BuyNowActivity.this);
                dialog.setContentView(R.layout.bootm_sheet_store_pickup);
                RadioGroup rg_shipping_method=dialog.findViewById(R.id.rg_shipping_method);
                dialog.show();
                getShippingMethod(rg_shipping_method);*/
               if (from.equals("home_delivery"))
               {
                   Intent i=new Intent(BuyNowActivity.this, PaymentActivity.class);
                   i.putExtra("address_id",address_id);
                   i.putExtra("intent_from","buy_now_activity");
                   i.putExtra("product_id",product_id);
                   i.putExtra("total_qty",""+p_qty);
                   i.putExtra("address",address);
                   i.putExtra("price",price);
                   i.putExtra("shipping_method",shipping_method);
                   i.putExtra("shipping_method_title",getResources().getString(R.string.home_delivery));
                   startActivity(i);
               }
               else if (from.equals(""))
               {

                   Toast.makeText(BuyNowActivity.this, ""+getResources().getString(R.string.select_shipping_method), Toast.LENGTH_SHORT).show();

                  // return;
               }


            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        sp_qty
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0,
                                               View arg1, int arg2, long arg3) {
                        if (quantityValues[arg2].equals("more"))
                        {
                            openMoreQuantityDialog();
                        }
                        else
                        {
                            CheckStock(product_id,quantityValues[arg2]);
                            /*tv_qty.setText("Qty "+quantityValues[arg2]);
                            p_qty=Integer.parseInt(quantityValues[arg2]);
                            tv_qty.setText("Qty "+p_qty);
                            tv_final_total.setText(""+final_price*p_qty);*/
                        }


                     //   tv_final_total.setText(""+(Integer.parseInt(tv_final_total.getText().toString())*Integer.parseInt(quantityValues[arg2])));

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        if (sp_qty.getSelectedItem().equals("more"))
                        {
                            openMoreQuantityDialog();
                        }
                    }

                });

    }//onClickClose
    @Override
    protected void onResume() {
        super.onResume();
        iv_cart.setVisibility(View.GONE);
        iv_search.setVisibility(View.GONE);
        tv_cart_count.setVisibility(View.GONE);
        if (NetworkChangeReceiver.isOnline(BuyNowActivity.this)) {
            getAllProductsForDelivery();
            from="home_delivery";

        }
        else
        {
            startActivity(new Intent(BuyNowActivity.this, NoInternetActivity.class));

        }
    }
    public void initView()
    {
        storePickUpPojoList=new ArrayList<StorePickUpPojo>();
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        iv_back=(ImageView)toolbar.findViewById(R.id.iv_back);
        tv_qty=(TextView)findViewById(R.id.tv_qty);
        ll_address_details=(LinearLayout) findViewById(R.id.ll_address_details);
        cv_continue=(CardView) findViewById(R.id.cv_continue);
        iv_search=(ImageView)toolbar.findViewById(R.id.iv_search);
        iv_cart=(ImageView)toolbar.findViewById(R.id.iv_cart);
        tv_title=(TextView) toolbar.findViewById(R.id.tv_title);
        tv_cart_count=(TextView) toolbar.findViewById(R.id.tv_cart_count);
        tv_name=(TextView) findViewById(R.id.tv_name);
        tv_address=(TextView) findViewById(R.id.tv_address);
        tv_pincode=(TextView) findViewById(R.id.tv_pincode);
        tv_state_city=(TextView) findViewById(R.id.tv_state_city);
        tv_product_name=(TextView) findViewById(R.id.tv_product_name);
        tv_seller=(TextView) findViewById(R.id.tv_seller);
        tv_price=(TextView) findViewById(R.id.tv_original_price);
        tv_discounted_pice=(TextView) findViewById(R.id.tv_discounted_pice);
        tv_discount=(TextView) findViewById(R.id.tv_discount);
        tv_final_total=(TextView) findViewById(R.id.tv_final_total);
        btn_change_address=(Button)findViewById(R.id.btn_change_address);
        btn_continue=(Button)findViewById(R.id.btn_continue);
        iv_product_image=(ImageView)findViewById(R.id.product_image);
        p_bar=(ProgressBar)findViewById(R.id.p_bar);
        sp_qty=(Spinner) findViewById(R.id.sp_qty);
        ArrayAdapter<String> quantityAdapter = new ArrayAdapter<String>(
                BuyNowActivity.this, android.R.layout.simple_spinner_item,
                quantityValues);
        sp_qty.setAdapter(quantityAdapter);
        //sp_qty.setSelection(p_qty);
        Intent i=getIntent();
        product_id=i.getStringExtra("product_id");
        tv_title.setText(getResources().getString(R.string.delivery));
         rg_shipping_method=findViewById(R.id.rg_shipping_method);


    }//initView

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
                                        getProductDetails(product_id);
                                    }
                                    if (default_shipping_address.length()==0)
                                    {
                                        CustomUtils.showToast(getResources().getString(R.string.please_add_shipping_address),BuyNowActivity.this);
                                        finish();
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
                                                    getProductDetails(product_id);
                                                }
                                        if (default_shipping_address.length()==0)
                                        {
                                            CustomUtils.showToast(getResources().getString(R.string.please_add_shipping_address),BuyNowActivity.this);
                                            finish();
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
                params.put("language",new SharedPrefManager(BuyNowActivity.this).getLanguage());
                params.put("user_id",new SharedPrefManager(BuyNowActivity.this).GetCustomerId());


                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GMarketApplication.getInstance(BuyNowActivity.this).addToRequestQueue(stringRequest);
    }

 public void getProductDetails(final String product_id)
 {
     p_bar.setVisibility(View.VISIBLE);
     StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.PRODUCT_DETAILS,
             new Response.Listener<String>() {
                 @Override
                 public void onResponse(String response) {
                     p_bar.setVisibility(View.GONE);
                     try {

                         JSONObject Jsonobj = new JSONObject(response);
                         String status = Jsonobj.getString("status");

                         if (status.equals("1")) {
                             JSONObject products = Jsonobj.getJSONObject("products");
                             String product_id = products.getString("product_id");
                             String product_name = products.getString("product_name");
                             String product_price = products.getString("product_price");
                             String product_special_price = products.getString("product_special_price");
                             String product_image = products.getString("product_image");
                             String product_discount = products.getString("product_discount");
                             String product_description = products.getString("product_description");
                             String quantity_available = products.getString("quantity_available");
                             JSONArray images = products.getJSONArray("images");

                             setProductDetailsData(product_id, product_name, product_price, product_special_price, product_image, product_discount, product_description, quantity_available);
                             ll_address_details.setVisibility(View.VISIBLE);
                             cv_continue.setVisibility(View.VISIBLE);
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
             params.put("language", new SharedPrefManager(BuyNowActivity.this).getLanguage());
             params.put("product_id", product_id);
             params.put("user_id", new SharedPrefManager(BuyNowActivity.this).GetCustomerId());


             return params;
         }
     };
     stringRequest.setRetryPolicy(new DefaultRetryPolicy(
             9000,
             0,
             DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
     GMarketApplication.getInstance(BuyNowActivity.this).addToRequestQueue(stringRequest);
 }
    public void setAddressDetails(String address_fname, String address_lname, String telephone, String street1, String street2, String city, String zip, String state, String country)
    {
        tv_name.setText(address_fname+" "+address_lname);
        tv_address.setText(street1+", "+street2);
        tv_pincode.setText(zip);
        tv_state_city.setText(city+", "+state+", "+country);

    }//setAddressDetalClose

    public void setProductDetailsData(String product_id, String product_name, String product_price, String product_special_price, String product_image, String product_discount, String product_description, String quantity_available) {
        tv_product_name.setText(product_name);
        /*tv_discounted_pice.setText("FCFA " + product_special_price);
        tv_original_price.setText(product_price);
        tv_discount.setText("" + product_discount + " % OFF");*/
        /*if (product_special_price.equals("null")) {
            tv_price.setVisibility(View.GONE);
        }
        if (product_discount.equals("null")) {
            tv_discount.setVisibility(View.GONE);
        }*/
        if (product_special_price.equals("null")) {
            tv_discounted_pice.setVisibility(View.GONE);
        }
        if (product_discount.equals("null")) {
            tv_discount.setVisibility(View.GONE);
        }
        if (!product_price.equals("null")) {

          //  tv_price.setText("FCFA " + new DecimalFormat("##.##").format(Double.parseDouble(product_price)));
            tv_price.setText(product_price+" FCFA");

        }
        if (!product_special_price.equals("null")) {
            //tv_discounted_pice.setText("FCFA " + new DecimalFormat("##.##").format(Double.parseDouble(product_special_price)));
            tv_discounted_pice.setText( product_special_price+" FCFA");
            tv_price.setPaintFlags( tv_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        }


        if(!product_special_price.equals("null"))
        {
         //   tv_final_total.setText("FCFA " + new DecimalFormat("##.##").format(Double.parseDouble(product_special_price)*p_qty));
           //final_price= Double.parseDouble(new DecimalFormat("##.##").format(Double.parseDouble(product_special_price)));
           final_price= product_special_price;
        }

       else
        {
       //     tv_final_total.setText("FCFA " + new DecimalFormat("##.##").format(Double.parseDouble(product_price)*p_qty));
           // final_price= Double.parseDouble(new DecimalFormat("##.##").format(Double.parseDouble(product_price)));
            final_price=product_price;
        }


        /* if (!product_special_price.equals("null")) {
            tv_discounted_pice.setText("FCFA " + new DecimalFormat("##.##").format(Double.parseDouble(product_special_price)));

        }*/
        //Glide.with(BuyNowActivity.this).load(product_image).into(iv_product_image);


       /* Glide.with(BuyNowActivity.this).load(product_image)
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(iv_product_image);*/
        Picasso.with(BuyNowActivity.this).load(product_image).networkPolicy(NetworkPolicy.NO_CACHE).into(iv_product_image);
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


    public void openMoreQuantityDialog()
    {
        final Dialog dialog=new Dialog(BuyNowActivity.this);
        dialog.setContentView(R.layout.dialog_add_more_qty);
        final EditText et_quantity=(EditText)dialog.findViewById(R.id.et_quantity);
        TextView tv_cancel=(TextView)dialog.findViewById(R.id.tv_cancel);
        TextView tv_save=(TextView)dialog.findViewById(R.id.tv_save);
        dialog.show();
        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_quantity.getText().toString().trim().length()==0)
                {
                    et_quantity.setError(getResources().getString(R.string.enter_qty_error));
                    return;
                }
                else
                {
                  CheckStock(product_id,et_quantity.getText().toString());

                  //  UpdateCart(item.getProduct_id(),position,et_quantity.getText().toString(),tv_qty,item,arg0,more_qty);
                    dialog.dismiss();
                }
            }
        });

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        // return Integer.parseInt(et_quantity.getText().toString());
    }


    public void CheckStock(final String product_id, final String qty)
    {
        p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.CHECK_STOCK,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject Jsonobj = new JSONObject(response);
                            String status = Jsonobj.getString("status");

                            if (status.equals("1")) {
                                p_qty=Integer.parseInt(qty);
                                tv_qty.setText("Qty "+p_qty);
                              //  tv_final_total.setText("FCFA "+(final_price*p_qty));
                            }

                            else
                            {
                                CustomUtils.showOKAlertDialog(BuyNowActivity.this,getResources().getString(R.string.quantity_not_available),getResources().getString(R.string.quantity_not_available_error));
                            }
                            p_bar.setVisibility(View.GONE);

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
                params.put("language", new SharedPrefManager(BuyNowActivity.this).getLanguage());
                params.put("product_id", product_id);
                params.put("qty", qty);


                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GMarketApplication.getInstance(BuyNowActivity.this).addToRequestQueue(stringRequest);
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
                                    View layout1 = LayoutInflater.from(BuyNowActivity.this).inflate(R.layout.row_shipping_method, rg_shipping_method, false);
                                    JSONObject j1=shipMethods.getJSONObject(i);
                                    final String name=j1.getString("name");
                                    title=j1.getString("title");
                                    RadioButton radioButton = new RadioButton(BuyNowActivity.this);
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
                                    rb_shipping.setChecked(false);
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

                                               /* Intent i=new Intent(BuyNowActivity.this, PaymentActivity.class);
                                                i.putExtra("address_id",address_id);
                                                i.putExtra("intent_from","buy_now_activity");
                                                i.putExtra("product_id",product_id);
                                                i.putExtra("total_qty",""+p_qty);

                                                i.putExtra("address",address);
                                                i.putExtra("price",price);
                                                i.putExtra("shipping_method",shipping_method);
                                                i.putExtra("shipping_method_title",getResources().getString(R.string.home_delivery));
                                                startActivity(i);*/
                                               // dialog.dismiss();
                                            }
                                            else if(name.equals("storepickupmodule"))
                                            {
                                            //    from="store_pickup";
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
                                Intent i=new Intent(BuyNowActivity.this, PaymentActivity.class);
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

                params.put("language",new SharedPrefManager(BuyNowActivity.this).getLanguage());

                return params;
            }
        };

        GMarketApplication.getInstance(BuyNowActivity.this).addToRequestQueue(stringRequest);

    }//getShippingMethodClose
    public  void openStorePickUpDialog()
    {
        dialog =new Dialog(BuyNowActivity.this);
        dialog.setContentView(R.layout.dialog_open_store_pick_up);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        ListView lv_open_store_pickup=(ListView)dialog.findViewById(R.id.lv_open_store_pickup);
        ProgressBar p_bar=(ProgressBar) dialog.findViewById(R.id.p_bar);
        StorePickupAdapter adapter=new StorePickupAdapter(BuyNowActivity.this,storePickUpPojoList,"buy");
        lv_open_store_pickup.setAdapter(adapter);
        dialog.show();
    }
    public void getStorePIckUpDetails(int position)
    {
        address=storePickUpPojoList.get(position).getAddress();
        price=storePickUpPojoList.get(position).getPrice();
        shipping_method="storepickupmodule";
        Intent i=new Intent(BuyNowActivity.this, PaymentActivity.class);
        i.putExtra("address_id",address_id);
        i.putExtra("intent_from","buy_now_activity");
        i.putExtra("product_id",product_id);
        i.putExtra("total_qty",""+p_qty);

        i.putExtra("address",address);
        i.putExtra("price",price);
        i.putExtra("shipping_method",shipping_method);
        i.putExtra("shipping_method_title",getResources().getString(R.string.store_pick_up));
        startActivity(i);
        dialog.dismiss();
    }
}
