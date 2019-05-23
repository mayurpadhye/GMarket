package com.cube9.gmarket.Products.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cube9.gmarket.Account.Adapter.CountrySpinnerAdapter;
import com.cube9.gmarket.Delivery.Activity.BuyNowActivity;
import com.cube9.gmarket.HelperClass.CustomUtils;
import com.cube9.gmarket.HelperClass.GMarketApplication;
import com.cube9.gmarket.HelperClass.SharedPrefManager;
import com.cube9.gmarket.Products.ModelClasses.VarientProductsPojo;
import com.cube9.gmarket.Products.ModelClasses.VarientProductsSizePojo;
import com.cube9.gmarket.R;
import com.cube9.gmarket.WebUrls.WebUrls;
import com.cube9.gmarket.nointernet.NoInternetActivity;
import com.cube9.gmarket.receivers.NetworkChangeReceiver;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VarientProductActivity extends AppCompatActivity {
    Context context;
    Intent i;
    String product_id="",using_from="";
    ProgressBar p_bar;
    ImageView iv_product;
    TextView tv_product_name,tv_discounted_pice,tv_original_price,tv_discount,tv_rating,tv_color;
    RecyclerView rv_product_images;
    RecyclerView rv_sizes;
    CardView cv_continue;
    Button btn_out_of_stock;
    Toolbar toolbar;
    TextView tv_title,tv_cart_count;
    ImageView iv_back,iv_search,iv_cart;
    List<VarientProductsPojo> varientProductsPojoList;
    List<VarientProductsSizePojo> varientProductsSizePojoList;
    Button btn_continue;
    Spinner sp_color,sp_sizes;
    String color_id="",size_id="";
    String is_in_stock="1";
    TextView tv_size;
    List<String> list_color_id;
    List<String> list_color_names;
    List<String> list_size_names;
    List<String> list_size_ids;
    List<String> list_size_ids_temp;
    List<String> list_size_names_temp;
    LinearLayout ll_shape;

    int color_list_flag=1;
    int size_list_flag=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_varient_product);
        initView();
        onClick();
        if (NetworkChangeReceiver.isOnline(VarientProductActivity.this))
        {
            getProductDetails();
        }
        else
        {
            startActivity(new Intent(VarientProductActivity.this, NoInternetActivity.class));
        }

    }//onCreateClose

    @Override
    protected void onResume() {
        super.onResume();
        iv_cart.setVisibility(View.GONE);
        iv_search.setVisibility(View.GONE);
        tv_cart_count.setVisibility(View.GONE);

        if (NetworkChangeReceiver.isOnline(VarientProductActivity.this))
        {

        }
        else
        {
            startActivity(new Intent(VarientProductActivity.this, NoInternetActivity.class));
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
       /* iv_cart.setVisibility(View.VISIBLE);
        iv_search.setVisibility(View.VISIBLE);*/
    }

    public void initView()
    {
        context=this;
        i=getIntent();
        list_color_id=new ArrayList<String>();
        list_color_names=new ArrayList<String>();
        list_size_ids=new ArrayList<String>();
        list_size_names_temp=new ArrayList<String>();
        list_size_ids_temp=new ArrayList<String>();
        list_size_names=new ArrayList<String>();
        cv_continue=(CardView)findViewById(R.id.cv_continue);
        btn_out_of_stock=(Button) findViewById(R.id.btn_out_of_stock);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        iv_back=(ImageView)toolbar.findViewById(R.id.iv_back);
        iv_search=(ImageView)toolbar.findViewById(R.id.iv_search);
        iv_cart=(ImageView)toolbar.findViewById(R.id.iv_cart);
        tv_title=(TextView) toolbar.findViewById(R.id.tv_title);
        tv_cart_count=(TextView) toolbar.findViewById(R.id.tv_cart_count);
        product_id=i.getStringExtra("product_id");
        using_from=i.getStringExtra("using_from");
        p_bar=(ProgressBar)findViewById(R.id.p_bar);
        rv_product_images=(RecyclerView)findViewById(R.id.rv_product_images);
        rv_sizes=(RecyclerView)findViewById(R.id.rv_sizes);
        iv_product=(ImageView) findViewById(R.id.iv_product_image);
        tv_product_name=(TextView) findViewById(R.id.tv_product_name);
        tv_discounted_pice=(TextView) findViewById(R.id.tv_discounted_pice);
        tv_original_price=(TextView) findViewById(R.id.tv_original_price);
        tv_discount=(TextView) findViewById(R.id.tv_discount);
        tv_rating=(TextView) findViewById(R.id.tv_rating);
        tv_color=(TextView) findViewById(R.id.tv_color);
        btn_continue=(Button)findViewById(R.id.btn_continue);
        sp_color=(Spinner) findViewById(R.id.sp_color);
        tv_size=(TextView) findViewById(R.id.tv_size);
        sp_sizes=(Spinner) findViewById(R.id.sp_sizes);
        ll_shape=(LinearLayout)findViewById(R.id.ll_shape);
        varientProductsPojoList=new ArrayList<VarientProductsPojo>();
        varientProductsSizePojoList=new ArrayList<VarientProductsSizePojo>();
        LinearLayoutManager layoutManager= new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_product_images.setLayoutManager(layoutManager);
        tv_title.setText(getResources().getString(R.string.select_varient));


    }//initViewClose

    public void onClick()
    {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (color_list_flag==1 && size_list_flag==1)
                {
                    if (sp_color.getSelectedItem().toString().equals(getResources().getString(R.string.select_color)))
                    {
                        if (varientProductsSizePojoList.size()>0)
                        {
                            CustomUtils.showToast(getResources().getString(R.string.select_color_and_size),VarientProductActivity.this);
                        }
                        else
                        {
                            CustomUtils.showToast(getResources().getString(R.string.select_color),VarientProductActivity.this);
                        }

                    }

                    else{

                        for (int i=0;i<varientProductsPojoList.size();i++)
                        {
                            if (varientProductsPojoList.get(i).getColor_id().equals(color_id) && varientProductsPojoList.get(i).getSize_id().equals(size_id))
                            {
                                product_id=varientProductsPojoList.get(i).getId();
                                is_in_stock=varientProductsPojoList.get(i).getIs_in_stock();


                            }
                            else  if (varientProductsPojoList.get(i).getColor_id().equals(color_id) && size_id.equals(""))
                            {
                                product_id=varientProductsPojoList.get(i).getId();
                                is_in_stock=varientProductsPojoList.get(i).getIs_in_stock();
                            }
                        }



                        if (using_from.equals("cart")) {
                            if (is_in_stock.equals("1"))

                                AddToCart(using_from);
                            else
                                CustomUtils.showToast(getResources().getString(R.string.product_not_available),VarientProductActivity.this);
                        }
                        else{

                            Intent i=new Intent(VarientProductActivity.this, BuyNowActivity.class);
                            i.putExtra("product_id",product_id);
                            startActivity(i);
                        }
                    }
                }

                if (!product_id.equals(""))
                {
                    if (using_from.equals("cart")) {
                        if (is_in_stock.equals("1"))
                            AddToCart(using_from);
                        else
                            CustomUtils.showToast(getResources().getString(R.string.product_not_available),VarientProductActivity.this);
                    }
                    else{

                        Intent i=new Intent(VarientProductActivity.this, BuyNowActivity.class);
                        i.putExtra("product_id",product_id);
                        startActivity(i);
                    }
                }
                else
                {
                    Toast.makeText(context, getResources().getString(R.string.select_varient), Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (color_list_flag==1) {
            sp_color.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    if (color_list_flag==1&&size_list_flag==1)
                    {

                        // your code here



                        if (!list_color_names.get(position).equals(getResources().getString(R.string.select_color)))
                        {
                            color_id=list_color_id.get(position);
                            list_size_ids.clear();
                            list_size_names_temp.clear();
                            for (int i = 0; i < varientProductsPojoList.size(); i++) {

                                if ((varientProductsPojoList.get(i).getColor_id()).equals(""+list_color_id.get(position))) {
                                    {
                                        // Toast.makeText(context, varientProductsPojoList.get(i).getSize_id(), Toast.LENGTH_SHORT).show();
                                        list_size_ids.add(  varientProductsPojoList.get(i).getSize_id());


                                    }
                                }
                            }
                            if (varientProductsSizePojoList.size()>0)
                            {
                                for (int j=0;j<list_size_ids.size();j++)
                                {

                                    for(int k=0;k<varientProductsSizePojoList.size();k++) {
                                        if (varientProductsSizePojoList.get(k).getSize_id().equals(list_size_ids.get(j))) {
                                            list_size_names_temp.add(varientProductsSizePojoList.get(k).getSize_name());

                                        }
                                    }

                                }
                                ll_shape.setVisibility(View.VISIBLE);
                                tv_size.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                ll_shape.setVisibility(View.GONE);
                                tv_size.setVisibility(View.GONE);

                                for (int i=0;i<varientProductsPojoList.size();i++)
                                {
                                    if (varientProductsPojoList.get(i).getColor_id().equals(color_id))
                                    {
                                        product_id=varientProductsPojoList.get(i).getId();
                                        is_in_stock=varientProductsPojoList.get(i).getIs_in_stock();

                                        if (is_in_stock.equals("1"))
                                        {
                                            cv_continue.setVisibility(View.VISIBLE);
                                            btn_out_of_stock.setVisibility(View.GONE);
                                        }
                                        else
                                        {
                                            cv_continue.setVisibility(View.GONE);
                                            btn_out_of_stock.setVisibility(View.VISIBLE);
                                        }

                                    }

                                }
                            }

                            CountrySpinnerAdapter adapter = new CountrySpinnerAdapter(context,list_size_names_temp);
                            sp_sizes.setAdapter(adapter);


                            for (int i=0;i<varientProductsPojoList.size();i++)
                            {
                                if (varientProductsPojoList.get(i).getColor_id().equals(color_id) && varientProductsPojoList.get(i).getSize_id().equals(size_id))
                                {
                                    //  Glide.with(VarientProductActivity.this).load(varientProductsPojoList.get(i).getImage()).into(iv_product);
                           /* Glide.with(VarientProductActivity.this).load(varientProductsPojoList.get(i).getImage())
                                    .thumbnail(0.5f)
                                    .crossFade()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(iv_product);*/
                                    Picasso.with(context).load(varientProductsPojoList.get(i).getImage()).placeholder(context.getResources().getDrawable(R.drawable.ic_g_market_logo)).networkPolicy(NetworkPolicy.NO_CACHE).into(iv_product);
                                }
                                else  if (varientProductsPojoList.get(i).getColor_id().equals(color_id) && size_id.equals(""))
                                {
                                    //Glide.with(VarientProductActivity.this).load(varientProductsPojoList.get(i).getImage()).into(iv_product);


                           /* Glide.with(VarientProductActivity.this).load(varientProductsPojoList.get(i).getImage())
                                    .thumbnail(0.5f)
                                    .crossFade()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(iv_product);*/

                                    Picasso.with(context).load(varientProductsPojoList.get(i).getImage()).placeholder(getResources().getDrawable(R.drawable.ic_g_market_logo)).networkPolicy(NetworkPolicy.NO_CACHE).into(iv_product);
                                }
                            }



                        }
                        else
                        {
                            ll_shape.setVisibility(View.GONE);
                            tv_size.setVisibility(View.GONE);
                        }

                    }
                    else if (color_list_flag==1 && size_list_flag==0) {
                        if (!list_color_names.get(position).equals(getResources().getString(R.string.select_color))) {
                            color_id=list_color_id.get(position);
                            sp_color.setVisibility(View.VISIBLE);
                            ll_shape.setVisibility(View.GONE);
                            for (int k = 0; k < varientProductsPojoList.size(); k++) {
                                if (varientProductsPojoList.get(k).getColor_id().equals(color_id)) {
                                 //   Toast.makeText(context, "" + varientProductsPojoList.get(k).getProduct_id() + " " + varientProductsPojoList.get(position).getColor_name(), Toast.LENGTH_SHORT).show();
                                    product_id = varientProductsPojoList.get(k).getId();
                                    is_in_stock = varientProductsPojoList.get(k).getIs_in_stock();
                                    if (is_in_stock.equals("1")) {
                                        cv_continue.setVisibility(View.VISIBLE);
                                        btn_out_of_stock.setVisibility(View.GONE);
                                    } else {
                                        cv_continue.setVisibility(View.GONE);
                                        btn_out_of_stock.setVisibility(View.VISIBLE);
                                    }

                                }
                            }
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });

        }

        sp_sizes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                size_id=list_size_ids.get(position);
                if (color_list_flag==1&&size_list_flag==1)
                {

                    size_id=list_size_ids.get(position);

                    for (int i=0;i<varientProductsPojoList.size();i++)
                    {
                        if (varientProductsPojoList.get(i).getColor_id().equals(color_id) && varientProductsPojoList.get(i).getSize_id().equals(size_id))
                        {
                            //  Glide.with(VarientProductActivity.this).load(varientProductsPojoList.get(i).getImage()).into(iv_product);

                        /*Glide.with(VarientProductActivity.this).load(varientProductsPojoList.get(i).getImage())
                                .thumbnail(0.5f)
                                .crossFade()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(iv_product);*/
                            Picasso.with(context).load(varientProductsPojoList.get(i).getImage()).placeholder(getResources().getDrawable(R.drawable.ic_g_market_logo)).networkPolicy(NetworkPolicy.NO_CACHE).into(iv_product);
                        }
                        else  if (varientProductsPojoList.get(i).getColor_id().equals(color_id) && size_id.equals(""))
                        {
                            //   Glide.with(VarientProductActivity.this).load(varientProductsPojoList.get(i).getImage()).into(iv_product);

                        /*Glide.with(VarientProductActivity.this).load(varientProductsPojoList.get(position).getImage())
                                .thumbnail(0.5f)
                                .crossFade()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(iv_product);*/

                            Picasso.with(context).load(varientProductsPojoList.get(position).getImage()).placeholder(getResources().getDrawable(R.drawable.ic_g_market_logo)).networkPolicy(NetworkPolicy.NO_CACHE).into(iv_product);

                        }
                    }

                    for (int i=0;i<varientProductsPojoList.size();i++)
                    {
                        if (varientProductsPojoList.get(i).getColor_id().equals(color_id) && varientProductsPojoList.get(i).getSize_id().equals(size_id))
                        {
                            product_id=varientProductsPojoList.get(i).getId();
                            is_in_stock=varientProductsPojoList.get(i).getIs_in_stock();

                            if (is_in_stock.equals("1"))
                            {
                                cv_continue.setVisibility(View.VISIBLE);
                                btn_out_of_stock.setVisibility(View.GONE);
                            }
                            else
                            {
                                cv_continue.setVisibility(View.GONE);
                                btn_out_of_stock.setVisibility(View.VISIBLE);
                            }

                        }
                        else  if (varientProductsPojoList.get(i).getColor_id().equals(color_id) && size_id.equals(""))
                        {
                            product_id=varientProductsPojoList.get(i).getId();
                            is_in_stock=varientProductsPojoList.get(i).getIs_in_stock();
                            if (is_in_stock.equals("1"))
                            {
                                cv_continue.setVisibility(View.VISIBLE);
                                btn_out_of_stock.setVisibility(View.GONE);
                            }
                            else
                            {
                                cv_continue.setVisibility(View.GONE);
                                btn_out_of_stock.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                }
                else if (color_list_flag==0&& size_list_flag==1)
                {
                    for (int k=0;k<varientProductsPojoList.size();k++)
                    {
                        if (varientProductsPojoList.get(k).getSize_id().equals(size_id))
                        {
                         //   Toast.makeText(context, ""+varientProductsPojoList.get(k).getProduct_id()+" "+varientProductsPojoList.get(position).getName(), Toast.LENGTH_SHORT).show();
                            product_id=varientProductsPojoList.get(k).getId();
                            is_in_stock=varientProductsPojoList.get(k).getIs_in_stock();
                            if (is_in_stock.equals("1"))
                            {
                                cv_continue.setVisibility(View.VISIBLE);
                                btn_out_of_stock.setVisibility(View.GONE);
                            }
                            else
                            {
                                cv_continue.setVisibility(View.GONE);
                                btn_out_of_stock.setVisibility(View.VISIBLE);
                            }

                        }
                    }
                    product_id=varientProductsPojoList.get(sp_sizes.getSelectedItemPosition()).getId();
                    Toast.makeText(context, ""+product_id, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

    }//onClickClose

    private void AddToCart(final String using_from) {

        p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.ADD_TO_CART,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        p_bar.setVisibility(View.GONE);
                        try {

                            JSONObject Jsonobj = new JSONObject(response);
                            String status=Jsonobj.getString("status");

                            if (status.equals("1"))
                            {
                                String my_cart_count=Jsonobj.getString("my_cart_count");
                                new SharedPrefManager(VarientProductActivity.this).setCartCount(my_cart_count);
                                String message=Jsonobj.getString("message");
                                CustomUtils.showToast(message,VarientProductActivity.this);
                                if (using_from.equals("cart"))
                                {
                                    finish();
                                    //startActivity(new Intent(ProductDetailsActivity.this, DeliveryDetailsActivity.class));
                                }


                            }
                            else
                            {
                                CustomUtils.showToast(getResources().getString(R.string.unable_to_add_to_cart),VarientProductActivity.this);
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
                        // Toast.makeText(VarientProductActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("language",new SharedPrefManager(VarientProductActivity.this).getLanguage());
                params.put("product_id",product_id);
                params.put("user_id",new SharedPrefManager(VarientProductActivity.this).GetCustomerId());


                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GMarketApplication.getInstance(VarientProductActivity.this).addToRequestQueue(stringRequest);
    }//AddtoCart

    public void getProductDetails()
    {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.PRODUCT_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        p_bar.setVisibility(View.GONE);
                        try {

                            JSONObject Jsonobj = new JSONObject(response);
                            String status=Jsonobj.getString("status");
                            Log.i("resssss",""+Jsonobj.toString());
                            if (status.equals("1"))
                            {
                                varientProductsPojoList.clear();
                                list_color_names.clear();
                                list_color_id.clear();
                                list_size_ids.clear();
                                list_size_names_temp.clear();
                                varientProductsSizePojoList.clear();
                                JSONObject products=Jsonobj.getJSONObject("products");
                                String product_id=products.getString("product_id");
                                String product_name=products.getString("product_name");
                                tv_product_name.setText(product_name);
                                String product_price=products.getString("product_price");
                                tv_original_price.setText(product_price);
                                String product_special_price=products.getString("product_special_price");
                                tv_discounted_pice.setText(product_special_price);
                                String product_image=products.getString("product_image");
                                Picasso.with(context).load(product_image).placeholder(getResources().getDrawable(R.drawable.ic_g_market_logo)).networkPolicy(NetworkPolicy.NO_CACHE).into(iv_product);
                                String product_discount=products.getString("product_discount");
                                tv_discount.setText(product_discount);
                                String product_description=products.getString("product_description");
                                String quantity_available=products.getString("quantity_available");
                                JSONArray images=products.getJSONArray("images");
                                String product_url=products.getString("product_url");
                                JSONArray conf_prod=Jsonobj.getJSONArray("conf_prod");
                                if (product_special_price.equals("null"))
                                {
                                    tv_discounted_pice.setVisibility(View.GONE);
                                }
                                if (product_discount.equals("null"))
                                {
                                    tv_discount.setVisibility(View.GONE);
                                }
                                if (!product_price.equals("null"))
                                {

                                    //  tv_original_price.setText("FCFA "+new DecimalFormat("##.##").format(Double.parseDouble(product_price)));
                                    tv_original_price.setText(product_price+" FCFA");

                                }
                                if ( !product_special_price.equals("null"))
                                {
                                    //   tv_discounted_pice.setText("FCFA "+new DecimalFormat("##.##").format(Double.parseDouble(product_special_price)));
                                    tv_discounted_pice.setText(product_special_price+" FCFA");

                                }

                                JSONObject varient=Jsonobj.getJSONObject("varient");
                                if (varient.has("Product Color"))
                                {
                                    JSONArray Color=varient.getJSONArray("Product Color");
                                    for (int i=0;i<Color.length();i++)
                                    {
                                        JSONObject j1=Color.getJSONObject(i);
                                        String id =j1.getString("id");
                                        String name =j1.getString("name");
                                        list_color_id.add(id);
                                        list_color_names.add(name);
                                    }
                                    list_color_names.add(0,getResources().getString(R.string.select_color));
                                    list_color_id.add(0,"0");
                                    CountrySpinnerAdapter adapter = new CountrySpinnerAdapter(context,list_color_names);
                                    sp_color.setAdapter(adapter);
                                }

                                if (varient.has("Size"))
                                {
                                    JSONArray Product_Size=varient.getJSONArray("Size");
                                    for (int i=0;i<Product_Size.length();i++)
                                    {
                                        JSONObject j1=Product_Size.getJSONObject(i);
                                        String size_id=j1.getString("id");
                                        String size_name=j1.getString("name");
                                        list_size_names.add(size_name);
                                        varientProductsSizePojoList.add(new VarientProductsSizePojo(size_id,size_name));

                                        list_size_ids.add(size_id);
                                    }


                                }
                                if (varient.has("Tailles"))
                                {
                                    JSONArray Product_Size=varient.getJSONArray("Tailles");
                                    for (int i=0;i<Product_Size.length();i++)
                                    {
                                        JSONObject j1=Product_Size.getJSONObject(i);
                                        String size_id=j1.getString("id");
                                        String size_name=j1.getString("name");
                                        list_size_names.add(size_name);
                                        varientProductsSizePojoList.add(new VarientProductsSizePojo(size_id,size_name));

                                        list_size_ids.add(size_id);

                                    }


                                }

                                if (!varient.has("Product Color") && varient.has("Size"))
                                {
                                    color_list_flag=0;
                                    size_list_flag=1;
                                    sp_color.setVisibility(View.GONE);
                                    tv_color.setVisibility(View.INVISIBLE);
                                    tv_size.setVisibility(View.VISIBLE);
                                    ll_shape.setVisibility(View.VISIBLE);
                                    CountrySpinnerAdapter adapter = new CountrySpinnerAdapter(context,list_size_names);
                                    sp_sizes.setAdapter(adapter);
                                }
                                else if(varient.has("Product Color") && !varient.has("Size"))
                                {
                                    color_list_flag=1;
                                    size_list_flag=0;
                                    sp_color.setVisibility(View.VISIBLE);
                                    ll_shape.setVisibility(View.GONE);
                                    tv_size.setVisibility(View.GONE);
                                    CountrySpinnerAdapter adapter = new CountrySpinnerAdapter(context,list_color_names);
                                    sp_color.setAdapter(adapter);
                                }

                                else if(!varient.has("Product Color") && varient.has("Size"))
                                {
                                    color_list_flag=0;
                                    size_list_flag=1;
                                    sp_color.setVisibility(View.GONE);
                                    tv_color.setVisibility(View.GONE);
                                    ll_shape.setVisibility(View.VISIBLE);
                                    tv_size.setVisibility(View.VISIBLE);
                                    CountrySpinnerAdapter adapter = new CountrySpinnerAdapter(context,list_size_names);
                                    sp_sizes.setAdapter(adapter);
                                }
                                else if(!varient.has("Product Color") && varient.has("Tailles"))
                                {
                                    color_list_flag=0;
                                    size_list_flag=1;
                                    sp_color.setVisibility(View.GONE);
                                    tv_color.setVisibility(View.GONE);
                                    ll_shape.setVisibility(View.VISIBLE);
                                    tv_size.setVisibility(View.VISIBLE);
                                    CountrySpinnerAdapter adapter = new CountrySpinnerAdapter(context,list_size_names);
                                    sp_sizes.setAdapter(adapter);
                                }

                                for (int i=0;i<conf_prod.length();i++)
                                {
                                    JSONObject j1=conf_prod.getJSONObject(i);
                                    String id=j1.getString("id");
                                    String name=j1.getString("name");
                                    String price=j1.getString("price");
                                    String color_id=j1.getString("color_id");
                                    String color_name=j1.getString("color_name");
                                    String discount=j1.getString("discount");
                                    String description=j1.getString("description");
                                    String available_qty=j1.getString("available_qty");
                                    String image=j1.getString("image");
                                    String size_id=j1.getString("size_id");
                                    String is_in_stock=j1.getString("is_in_stock");
                                    boolean selected=false;
                                    varientProductsPojoList.add(new VarientProductsPojo(product_id,id,name,price,color_id,color_name,discount,description,available_qty,image,selected,size_id,is_in_stock));
                                }

                               /* VarientProductAdapter varientProductAdapter=new VarientProductAdapter();
                                rv_product_images.setAdapter(varientProductAdapter);*/
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                btn_continue.setEnabled(true);
                            }
                            else
                            {
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                btn_continue.setEnabled(false);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            btn_continue.setEnabled(false);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        p_bar.setVisibility(View.GONE);
                        btn_continue.setEnabled(false);
                        // Toast.makeText(VarientProductActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("language",new SharedPrefManager(VarientProductActivity.this).getLanguage());
                params.put("product_id",product_id);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GMarketApplication.getInstance(VarientProductActivity.this).addToRequestQueue(stringRequest);
    }//productDetailsClose


    public class VarientProductAdapter extends  RecyclerView.Adapter<VarientProductAdapter.MyViewHolder>  {
        private SparseBooleanArray selectedItems = new SparseBooleanArray();
        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
            ImageView iv_product_image;
            TextView tv_color_name;


            public MyViewHolder(View view) {
                super(view);
                iv_product_image = (ImageView) view.findViewById(R.id.img_temp);
                tv_color_name = (TextView) view.findViewById(R.id.tv_color_name);
                view.setOnClickListener(this);
            }


            @Override
            public void onClick(View v) {
                switch (v.getId()) {

                    default:

                        for(int i=0;i<varientProductsPojoList.size();i++)
                        {
                            varientProductsPojoList.get(i).setSetSelected(false);
                        }
                        if (varientProductsPojoList.get(this.getAdapterPosition()).getPrice().equals("null"))
                        {
                            tv_discounted_pice.setVisibility(View.GONE);
                        }

                        if (varientProductsPojoList.get(this.getAdapterPosition()).getPrice()==null)
                        {

                        }
                        else {// tv_original_price.setText("FCFA "+new DecimalFormat("##.##").format(Double.parseDouble(varientProductsPojoList.get(this.getAdapterPosition()).getPrice())));
                            tv_original_price.setText(varientProductsPojoList.get(this.getAdapterPosition()).getPrice()+" FCFA");
                        }
                        product_id=varientProductsPojoList.get(this.getAdapterPosition()).getId();
                        //Glide.with(context).load(varientProductsPojoList.get(this.getAdapterPosition()).getImage()).into(iv_product);
                       /* Glide.with(VarientProductActivity.this).load(varientProductsPojoList.get(this.getAdapterPosition()).getImage())
                                .thumbnail(0.5f)
                                .crossFade()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(iv_product);*/
                        Picasso.with(context).load(varientProductsPojoList.get(this.getAdapterPosition()).getImage()).placeholder(getResources().getDrawable(R.drawable.ic_g_market_logo)).networkPolicy(NetworkPolicy.NO_CACHE).into(iv_product);
                        varientProductsPojoList.get(this.getAdapterPosition()).setSetSelected(true);
                        varientProductsPojoList.get(this.getAdapterPosition()).getProduct_id();
                        notifyItemChanged(this.getAdapterPosition());
                        notifyDataSetChanged();

                        break;

                }

            }
        }
        @Override
        public VarientProductAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_varient_product, parent, false);

            return new VarientProductAdapter.MyViewHolder(itemView);
        }

        @SuppressLint("NewApi")
        @Override
        public void onBindViewHolder(@NonNull final VarientProductAdapter.MyViewHolder holder, final int position) {
            final VarientProductsPojo items=varientProductsPojoList.get(position);
            //  Glide.with(context).load(varientProductsPojoList.get(position).getImage()).into(holder.iv_product_image);


         /*   Glide.with(VarientProductActivity.this).load(varientProductsPojoList.get(position).getImage())
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(iv_product);*/
            Picasso.with(context).load(varientProductsPojoList.get(position).getImage()).placeholder(getResources().getDrawable(R.drawable.ic_g_market_logo)).networkPolicy(NetworkPolicy.NO_CACHE).into(iv_product);
            holder.tv_color_name.setText(items.getColor_name());
           /*  holder.rl_sub_product.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {


                     product_id=items.getId();
                  //   getProductDetails();
                 }
             });*/

            if(varientProductsPojoList.get(position).isSetSelected())
            {
                holder.iv_product_image.setBackground(context.getResources().getDrawable(R.drawable.cell_shape_colored));
            }
            else
            {
                holder.iv_product_image.setBackground(null);
            }


        }


        @Override
        public int getItemCount() {
            return varientProductsPojoList.size();
        }



    }






}
