package com.cube9.gmarket.Products.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.cube9.gmarket.HelperClass.GMarketApplication;
import com.cube9.gmarket.HelperClass.SharedPrefManager;
import com.cube9.gmarket.Products.Adapter.FilterExpandableMenuAdapter;
import com.cube9.gmarket.Products.ModelClasses.FilterDetailsPojo;
import com.cube9.gmarket.R;
import com.cube9.gmarket.WebUrls.WebUrls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductFilterActivity extends AppCompatActivity {
Toolbar toolbar;
ImageView iv_back,iv_search,iv_cart;
TextView tv_title,tv_cart_count;
double MIN,MAX;
    private static float MIN_SET=0,MAX_SET=0;
TextView textMax,textMin;
    HashMap listChildMenu;
    List  listMenuHeader ;
    CrystalRangeSeekbar rangeSeekBar;
    String intent_from="";
    Button btn_clear_filter,btn_apply_filter;
    float filter_max_price=0,filter_min_price=0;
    ExpandableListView filter_expandable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_filter);
      initView();
        getFilterDetails();
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Intent i=getIntent();
        MIN=i.getDoubleExtra("min_price",0);
        MAX=i.getDoubleExtra("max_price",0);
        filter_min_price=i.getFloatExtra("filter_min_price",0);
        filter_max_price=i.getFloatExtra("filter_max_price",0);
        intent_from=i.getStringExtra("product_filter");
        rangeSeekBar=(CrystalRangeSeekbar)findViewById(R.id.seekBar_price2);


        rangeSeekBar.setMinValue((float) MIN);
        rangeSeekBar.setMaxValue((float) MAX);

        // set listener
        rangeSeekBar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                MIN_SET=minValue.floatValue();
                MAX_SET=maxValue.floatValue();
                textMin.setText(String.valueOf(minValue));
                textMax.setText(String.valueOf(maxValue));
            }
        });

        btn_apply_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_clear_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


// get range seekbar container

    }




    private void initView() {
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        iv_back=(ImageView)toolbar.findViewById(R.id.iv_back);
        iv_search=(ImageView)toolbar.findViewById(R.id.iv_search);
        iv_cart=(ImageView)toolbar.findViewById(R.id.iv_cart);
        tv_title=(TextView) toolbar.findViewById(R.id.tv_title);
        textMin=(TextView) findViewById(R.id.textMin);
        textMax=(TextView) findViewById(R.id.textMax);
        tv_cart_count=(TextView) toolbar.findViewById(R.id.tv_cart_count);
        tv_title.setText(getResources().getString(R.string.filter_by));
        btn_clear_filter=(Button)findViewById(R.id.btn_clear_filter);
        btn_apply_filter=(Button)findViewById(R.id.btn_apply_filter);
        filter_expandable=(ExpandableListView) findViewById(R.id.filter_expandable);


    }//initViewClose

    @Override
    protected void onResume() {
        super.onResume();

        iv_search.setVisibility(View.GONE);
        iv_cart.setVisibility(View.GONE);

        tv_cart_count.setVisibility(View.GONE);
    }

    @Override
    public void finish() {
        Intent i= getIntent();
        if(MIN_SET>0 && MAX_SET>0) {
            i.putExtra("intent_from","product_filter");
            i.putExtra("min", MIN_SET);
            i.putExtra("max", MAX_SET);

            setResult(RESULT_OK, i);
        }
        else {
            i.putExtra("intent_from","no_value");
            setResult(RESULT_CANCELED, i);
        }
        super.finish();
    }


    public void getFilterDetails()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.FILTER_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {


                            JSONObject Jsonobj = new JSONObject(response);
                            String status=Jsonobj.getString("status");

                            if (status.equals("1"))
                            {
                                listChildMenu = new HashMap<>();
                                listMenuHeader = new ArrayList<>();
                                JSONObject filter_category=Jsonobj.getJSONObject("filter_category");


                                for(int f=0;f<filter_category.names().length();f++) {
                                    Log.i("Json keys ", filter_category.names().get(f) + "");
                                    JSONArray jsonArrayColor=filter_category.getJSONArray(filter_category.names().getString(f));
                                    if (jsonArrayColor.length() > 0) {

                                        ArrayList<FilterDetailsPojo> listSub = new ArrayList<>();

                                        for (int j = 0; j < jsonArrayColor.length(); j++) {

                                            JSONObject jObjSC = jsonArrayColor.getJSONObject(j);
                                            FilterDetailsPojo menuData = new FilterDetailsPojo();
                                            menuData.setId(jObjSC.getString("id"));
                                            menuData.setName(jObjSC.getString("name"));
                                            menuData.setSelected(false);
                                            listSub.add(menuData);
                                        }
                                        listMenuHeader.add(filter_category.names().getString(f));
                                        listChildMenu.put(filter_category.names().getString(f), listSub);
                                    }

                                }




                                FilterExpandableMenuAdapter   menuAdapter = new FilterExpandableMenuAdapter(ProductFilterActivity.this,
                                        listMenuHeader, listChildMenu, filter_expandable);
                                filter_expandable.setAdapter(menuAdapter);

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        // Toast.makeText(HomeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("language",new SharedPrefManager(ProductFilterActivity.this).getLanguage());

                params.put("category_id","3");


                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GMarketApplication.getInstance(ProductFilterActivity.this).addToRequestQueue(stringRequest);

    }//getFilterDetailsClose
}
