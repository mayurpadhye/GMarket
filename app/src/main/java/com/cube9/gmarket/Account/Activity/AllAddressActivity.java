package com.cube9.gmarket.Account.Activity;

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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cube9.gmarket.Account.Adapter.AllAddressesAdapter;
import com.cube9.gmarket.Account.ModelClass.AllAddressPojo;
import com.cube9.gmarket.HelperClass.GMarketApplication;
import com.cube9.gmarket.HelperClass.SharedPrefManager;
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

public class AllAddressActivity extends AppCompatActivity {
  RecyclerView rv_addresses;
  ProgressBar p_bar;
  TextView tv_no_address_found;
    Toolbar toolbar;
    TextView tv_title,tv_cart_count;
    ImageView iv_back,iv_search,iv_cart;
    TextView tv_add_new_address;
    List<AllAddressPojo> allAddressPojoList;
    String intent_from="";
    Button btn_deliver_to_address;
    Context context;
    AllAddressesAdapter allAddressesAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_address);
        initView();

    }//onCreateClose

    @Override
    protected void onResume() {
        super.onResume();
        iv_search.setVisibility(View.GONE);
        iv_cart.setVisibility(View.GONE);
        tv_cart_count.setVisibility(View.GONE);

        if (NetworkChangeReceiver.isOnline(AllAddressActivity.this))
            GetAllAddresses();
        else
        {
            startActivity(new Intent(AllAddressActivity.this, NoInternetActivity.class));

        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        /*iv_search.setVisibility(View.VISIBLE);
        iv_cart.setVisibility(View.VISIBLE);*/
    }

    public void initView()
    {
        context=this;
        allAddressPojoList=new ArrayList<AllAddressPojo>();
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        iv_back=(ImageView)toolbar.findViewById(R.id.iv_back);
        iv_search=(ImageView)toolbar.findViewById(R.id.iv_search);
        iv_cart=(ImageView)toolbar.findViewById(R.id.iv_cart);
        tv_title=(TextView) toolbar.findViewById(R.id.tv_title);
        tv_cart_count=(TextView) toolbar.findViewById(R.id.tv_cart_count);
        tv_title.setText(getResources().getString(R.string.all_address));
        rv_addresses=(RecyclerView)findViewById(R.id.rv_addresses);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rv_addresses.setLayoutManager(mLayoutManager);
        rv_addresses.addItemDecoration(new DividerItemDecoration(this, 0));
        p_bar=(ProgressBar) findViewById(R.id.p_bar);
        tv_no_address_found=(TextView)findViewById(R.id.tv_no_address_found);
        tv_add_new_address=(TextView)findViewById(R.id.tv_add_new_address);
        btn_deliver_to_address=(Button)findViewById(R.id.btn_deliver_to_address);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent i=getIntent();
        intent_from=i.getStringExtra("intent_from");
        if (intent_from.equals("my_account_activity"))
        {
            btn_deliver_to_address.setVisibility(View.VISIBLE);
            btn_deliver_to_address.setText(getResources().getString(R.string.add_new_address));
            tv_add_new_address.setVisibility(View.GONE);
        }
        else
        {
            btn_deliver_to_address.setVisibility(View.VISIBLE);
            tv_add_new_address.setVisibility(View.VISIBLE);
            btn_deliver_to_address.setText(getResources().getString(R.string.deliver_to_this_address));
        }

        tv_add_new_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(AllAddressActivity.this,AddNewAddressActivity.class);
                i.putExtra("intent_from","delivery_detail");

                startActivityForResult(i, 1);
            }
        });
        btn_deliver_to_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intent_from.equals("my_account_activity"))
                {
                    Intent i=new Intent(AllAddressActivity.this,AddNewAddressActivity.class);
                   startActivity(i);
                }
                else
                {
                    int position=allAddressesAdapter.getSelectedMenuPosition();
                    UpdateAddress(allAddressPojoList.get(position).getFname(),allAddressPojoList.get(position).getLname(),allAddressPojoList.get(position).getTelephone(),allAddressPojoList.get(position).getStreet1(),allAddressPojoList.get(position).getStreet2(),allAddressPojoList.get(position).getCity(),allAddressPojoList.get(position).getZip(),allAddressPojoList.get(position).getState_id(),allAddressPojoList.get(position).getCountry_code(),allAddressPojoList.get(position).getAddress_id());
                }
            }
        });
    }//initViewClose
    public void UpdateAddress(final String fname, final String lname, final String mobile, final String street1, final String street2, final String city, final String zip, final String state, final String country_id, final String address_id)
    {
        p_bar.setVisibility(View.VISIBLE);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.UPDATE_ADDRESS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        p_bar.setVisibility(View.GONE);
                        try {

                            JSONObject Jsonobj = new JSONObject(response);
                            String status = Jsonobj.getString("status");

                            if (status.equals("1")) {
                                String message=Jsonobj.getString("message");
                                //openAlertDialog();
                                Intent intent = new Intent();
                                intent.putExtra("intent_from", "delivery_detail" );
                                setResult(RESULT_OK, intent);
                                finish();

                            } else {

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
                     //   Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id",new SharedPrefManager(context).GetCustomerId());
                params.put("fname",fname);
                params.put("lname",lname);
                params.put("contact",mobile);
                params.put("street1",street1);
                params.put("street2",street2);
                params.put("city",city);
                params.put("zip",zip);
                params.put("state",state);
                params.put("country",country_id);
                params.put("address_id",address_id);
                params.put("default_billing","1");
                params.put("default_shipping","1");
                params.put("language",new SharedPrefManager(AllAddressActivity.this).getLanguage());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GMarketApplication.getInstance(context).addToRequestQueue(stringRequest);
    }
    public void GetAllAddresses()
    {
        p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.GET_ALL_ADDRESSES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        p_bar.setVisibility(View.GONE);
                        try {

                            JSONObject Jsonobj = new JSONObject(response);
                            String status=Jsonobj.getString("status");

                            if (status.equals("1"))
                            {
                                allAddressPojoList.clear();
                                JSONArray alldata=Jsonobj.getJSONArray("alldata");
                                for (int i=0;i<alldata.length();i++)
                                {
                                    JSONObject j1=alldata.getJSONObject(i);
                                    String address_id=j1.getString("address_id");
                                    String fname=j1.getString("fname");
                                    String lname=j1.getString("lname");
                                    String telephone=j1.getString("telephone");
                                    String street_name="";
                                    JSONArray street=j1.getJSONArray("street");
                                    String street1="",street2="";
                                    for (int j=0;j<street.length();j++)
                                    {
                                      street_name=street_name+" "+street.get(j);
                                        street1=""+street.get(0);
                                        if (j==1)
                                        street2=""+street.get(1);
                                    }
                                    String city=j1.getString("city");
                                    String zip=j1.getString("zip");
                                    String state=j1.getString("state");
                                    String company=j1.getString("company");
                                    String country=j1.getString("country");
                                    String fax=j1.getString("fax");
                                    String is_billing=j1.getString("is_billing");
                                    String is_shipping=j1.getString("is_shipping");
                                    String state_id=j1.getString("state_id");
                                    String country_code=j1.getString("country_code");
                                    String checked="";
                                    if (i==0)
                                    {
                                         checked="1";
                                    }
                                    else
                                    {
                                         checked="0";
                                    }



                                    allAddressPojoList.add(new AllAddressPojo(address_id,fname,lname,telephone,street_name,city,zip,state,company,country,fax,is_billing,is_shipping,intent_from,street1,street2,checked,state_id,country_code));

                                }//forClose
                                if (alldata.length()>0)
                                {
                                    tv_no_address_found.setVisibility(View.GONE);
                                     allAddressesAdapter=new AllAddressesAdapter(context,allAddressPojoList,p_bar,tv_no_address_found,btn_deliver_to_address,AllAddressActivity.this);
                                    rv_addresses.setAdapter(allAddressesAdapter);
                                }
                                else
                                {
                                    tv_no_address_found.setVisibility(View.VISIBLE);
                                    btn_deliver_to_address.setVisibility(View.VISIBLE);

                                }

                            }
                            else {
                                tv_no_address_found.setVisibility(View.VISIBLE);
                                btn_deliver_to_address.setVisibility(View.VISIBLE);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.i("address_exception",e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        p_bar.setVisibility(View.GONE);
                     //   Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("language",new SharedPrefManager(context).getLanguage());
                params.put("user_id",new SharedPrefManager(context).GetCustomerId());
                params.put("language",new SharedPrefManager(AllAddressActivity.this).getLanguage());

                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GMarketApplication.getInstance(context).addToRequestQueue(stringRequest);
    }//getAllAddressClose

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                String intent_from = data.getStringExtra("intent_from");
                if (intent_from.equals("delivery_detail"))
                {
                    finish();
                }

            }
        }
    }

}
