package com.cube9.gmarket.Account.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
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
import com.cube9.gmarket.Account.Adapter.StateSpinnerAdapter;
import com.cube9.gmarket.Cart.Activity.MyCartActivity;
import com.cube9.gmarket.HelperClass.CustomUtils;
import com.cube9.gmarket.HelperClass.GMarketApplication;
import com.cube9.gmarket.HelperClass.SharedPrefManager;
import com.cube9.gmarket.R;
import com.cube9.gmarket.WebUrls.WebUrls;
import com.cube9.gmarket.nointernet.NoInternetActivity;
import com.cube9.gmarket.receivers.NetworkChangeReceiver;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateAddressActivity extends AppCompatActivity {
    EditText et_first_name, et_last_name, et_company, et_telephone, et_street_two,et_street_one, et_city, et_zip;
    SearchableSpinner sp_country;
            Spinner sp_states;
    Button btn_save_address;
    ProgressBar p_bar;
    Toolbar toolbar;
    List<String> list_cities;
    TextView tv_title,tv_cart_count;
    ImageView iv_back,iv_search,iv_cart;
    List<String> country_list,state_list,state_code,state_id_list;
    List<String> country_code;
    Context context;
    String state_name="",country_name="",default_shipping="";
    public static String COUNTRY_HINT="";
    CheckBox cb_shipping,cb_billing;
    String state_id="",country_code_id="";
    boolean check=false;
    String address_id="";
    String state="";
    TextInputLayout tl_city;
    LinearLayout ll_cities;
    Spinner sp_cities;
    String region_id="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_address);
        initView();
         onClick();

    }
    public void initView() {
        context=UpdateAddressActivity.this;
        COUNTRY_HINT=getResources().getString(R.string.select_country);
        list_cities = new ArrayList<String>();
        list_cities.add(0,getResources().getString(R.string.select_city));

        toolbar=(Toolbar)findViewById(R.id.toolbar);
        tl_city=(TextInputLayout) findViewById(R.id.tl_city);
        ll_cities=(LinearLayout) findViewById(R.id.ll_cities);
        iv_back=(ImageView)toolbar.findViewById(R.id.iv_back);
        iv_search=(ImageView)toolbar.findViewById(R.id.iv_search);
        iv_cart=(ImageView)toolbar.findViewById(R.id.iv_cart);
        tv_title=(TextView) toolbar.findViewById(R.id.tv_title);
        tv_cart_count=(TextView) toolbar.findViewById(R.id.tv_cart_count);
        et_first_name = (EditText)findViewById(R.id.et_first_name);
        et_last_name = (EditText)findViewById(R.id.et_last_name);
        et_company = (EditText)findViewById(R.id.et_company);
        et_telephone = (EditText)findViewById(R.id.et_telephone);
        et_street_two = (EditText)findViewById(R.id.et_street_two);
        et_street_one = (EditText)findViewById(R.id.et_street_one);
        et_city = (EditText)findViewById(R.id.et_city);
        et_zip = (EditText)findViewById(R.id.et_zip);
        sp_country = (SearchableSpinner)findViewById(R.id.sp_country);
        sp_states = (Spinner) findViewById(R.id.sp_states);
        sp_cities = (Spinner) findViewById(R.id.sp_cities);
        btn_save_address = (Button)findViewById(R.id.btn_save_address);
        p_bar = (ProgressBar)findViewById(R.id.p_bar);
        cb_shipping = (CheckBox) findViewById(R.id.cb_shipping);
        cb_billing = (CheckBox) findViewById(R.id.cb_billing);
        country_list = new ArrayList<String>();
        country_code = new ArrayList<String>();
        state_list = new ArrayList<String>();
        state_code = new ArrayList<String>();
        state_id_list = new ArrayList<String>();
        tv_title.setText(getResources().getString(R.string.update_address));
        Intent i=getIntent();
        address_id=i.getStringExtra("address_id");
        list_cities.clear();
        state_list.add(0,getResources().getString(R.string.select_state));
        list_cities.add(0,getResources().getString(R.string.select_city));

        StateSpinnerAdapter adapter = new StateSpinnerAdapter(context,state_list);
        sp_states.setAdapter(adapter);
        ArrayAdapter<String> adapter1 =
                new ArrayAdapter<String>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, list_cities);
        adapter1.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
        sp_cities.setAdapter(adapter1);
    }//initViewClose

    @Override
    protected void onResume() {
        super.onResume();
        iv_search.setVisibility(View.GONE);
        iv_cart.setVisibility(View.GONE);
        tv_cart_count.setVisibility(View.GONE);
        if (NetworkChangeReceiver.isOnline(UpdateAddressActivity.this))
            getAddressDetails();
        else
        {
            startActivity(new Intent(UpdateAddressActivity.this, NoInternetActivity.class));

        }

    }

    @Override
    protected void onPause() {
        super.onPause();
      /*  iv_search.setVisibility(View.VISIBLE);
        iv_cart.setVisibility(View.VISIBLE);*/
    }

    public void onClick()
    {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final int[] iCurrentSelection = {sp_country.getSelectedItemPosition()};
        sp_country
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0,
                                               View arg1, int arg2, long arg3) {
                        ((TextView) arg1).setTextColor(Color.BLACK);
                        if(iCurrentSelection[0] == arg2){
                            return; //do nothing
                        }
                        if (country_list.get(arg2).equals(getResources().getString(R.string.select_country)))
                        {
                            country_code_id="";
                            //  CustomUtils.showToast(getResources().getString(R.string.select_country_error),context);
                            return;
                        }
                        else
                        {

                            country_code_id=country_code.get(arg2);
                            state_list.clear();
                            state_code.clear();
                            list_cities.clear();
                            sp_cities.setAdapter(null);
                            et_city.setText("");
                            tl_city.setVisibility(View.VISIBLE);
                            ll_cities.setVisibility(View.GONE);
                            getStateList(country_code.get(arg2),state);
                        }

                        iCurrentSelection[0] = arg2;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {

                    }

                });


        sp_states
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0,
                                               View arg1, int arg2, long arg3) {


                        if (state_list.get(arg2).equals(getResources().getString(R.string.select_state)))
                        {
                            state_name="";
                            //  CustomUtils.showToast(getResources().getString(R.string.select_country_error),context);
                            return;
                        }
                        else
                        {
                            state_name=state_code.get(arg2);
                            region_id=state_name;
                            et_city.setText("");
                            list_cities.clear();
                            GetCities();
                        }


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {

                    }

                });

        sp_cities
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0,
                                               View arg1, int arg2, long arg3) {

                        ((TextView) arg1).setTextColor(Color.BLACK);
                        if (list_cities.get(arg2).equals(getResources().getString(R.string.select_city)))
                        {

                            //  CustomUtils.showToast(getResources().getString(R.string.select_country_error),context);
                            return;
                        }
                        else
                        {

                            et_city.setText(list_cities.get(arg2));
                        }


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {

                    }

                });
        iv_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(context,MyCartActivity.class));

            }
        });

        cb_shipping.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    default_shipping="0";


                }

            }


        });
        cb_billing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    default_shipping="1";

                }

            }


        });

        btn_save_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isValidateFirstName(et_first_name.getText().toString().trim()))
                {
                    et_first_name.setError(getResources().getString(R.string.first_name_error));
                    return;
                }

                if (!isValidateLastName(et_last_name.getText().toString().trim()))
                {
                    et_last_name.setError(getResources().getString(R.string.last_name_error));
                    return;
                }
                if (!isValidMobile(et_telephone.getText().toString().trim()))
                {
                    et_telephone.setError(getResources().getString(R.string.mobile_error));
                    return;
                }
                if (et_street_one.getText().toString().length()==0)
                {
                    et_street_one.setError(getResources().getString(R.string.enter_street_name));
                    return;
                }

                if (et_street_two.getText().toString().length()==0)
                {
                    et_street_two.setError(getResources().getString(R.string.enter_street_name));
                    return;
                }
                if (et_city.getText().toString().length()==0)
                {
                    et_city.setError(getResources().getString(R.string.enter_city));
                    return;
                }
                if (country_code_id.length()==0)
                {
                    CustomUtils.showToast(getResources().getString(R.string.select_country),context);
                    return;
                }
                if (state_name.length()==0)
                {
                    CustomUtils.showToast(getResources().getString(R.string.select_state),context);
                    Toast.makeText(context, getResources().getString(R.string.select_city_error), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (et_city.getText().toString().trim().length()==0)
                {
                    et_city.setError(getResources().getString(R.string.enter_city));
                    Toast.makeText(context, ""+getResources().getString(R.string.enter_city), Toast.LENGTH_SHORT).show();
                    return;
                }
                UpdateAddress();
            }
        });


    }

    private boolean isValidMobile(String phone) {
        boolean check=false;
        if(!Pattern.matches("[a-zA-Z]+", phone)) {
            if(phone.length() < 8 || phone.length() > 15) {
                // if(phone.length() != 10) {
                check = false;
                et_telephone.setError(getResources().getString(R.string.not_valid_mobile_no));
            } else {
                check = true;
            }
        } else {
            check=false;
        }
        return check;
    }
    public  boolean isValidateFirstName( String firstName )
    {
        Pattern p = Pattern.compile("[^A-Za-z]");
        Matcher m = p.matcher(firstName);
        // boolean b = m.matches();
        boolean b = m.find();
        if (b == true)
            return check=false;
        else
            return check=true;

    } // end method validateFirstName

    // validate last name
    public  boolean isValidateLastName( String lastName )
    {
        Pattern p = Pattern.compile("[^A-Za-z]");
        Matcher m = p.matcher(lastName);
        // boolean b = m.matches();
        boolean b = m.find();
        if (b == true)
            return check=false;
        else
            return check=true;
    }

   public void getAddressDetails()
   {
       p_bar.setVisibility(View.VISIBLE);
       StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.GET_SINGLE_ADDRESS,
               new Response.Listener<String>() {
                   @Override
                   public void onResponse(String response) {
                       p_bar.setVisibility(View.GONE);
                       try {

                           JSONObject Jsonobj = new JSONObject(response);
                           String status = Jsonobj.getString("status");

                           if (status.equals("1")) {
                               String message=Jsonobj.getString("message");
                               JSONArray address=Jsonobj.getJSONArray("address");
                               for (int i=0;i<address.length();i++)
                               {
                                   JSONObject j1=address.getJSONObject(i);
                                   String address_id=j1.getString("address_id");
                                   String fname=j1.getString("fname");
                                   String lname=j1.getString("lname");
                                   String contact=j1.getString("contact");

                                   JSONArray street=j1.getJSONArray("street");
                                   String street_address1="",street_address2="";
                                   for(int j=0;j<street.length();j++)
                                   {
                                       street_address1=""+street.get(0);
                                       street_address2=""+street.get(1);

                                   }
                                   String city=j1.getString("city");
                                   String zip=j1.getString("zip");
                                    state=j1.getString("state");
                                   String country=j1.getString("country");
                                   String is_default_billing=j1.getString("is_default_billing");
                                   String is_default_shipping=j1.getString("is_default_shipping");
                                   setData(address_id,fname,lname,contact,street_address1,street_address2,city,zip,state,country,is_default_billing,is_default_shipping);
                               }

                               }


                           else {

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
                       //Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                   }
               }) {
           @Override
           protected Map<String, String> getParams() throws AuthFailureError {
               Map<String, String> params = new HashMap<>();
               params.put("address_id",address_id);
               params.put("language",new SharedPrefManager(UpdateAddressActivity.this).getLanguage());
               return params;
           }
       };
       stringRequest.setRetryPolicy(new DefaultRetryPolicy(
               9000,
               0,
               DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
       GMarketApplication.getInstance(context).addToRequestQueue(stringRequest);
   }//getAddressDetails
    public void GetCountries(final String country_name) {
        p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.GET_COUNTRIES,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(String response) {
                        p_bar.setVisibility(View.GONE);
                        try {

                            JSONObject Jsonobj = new JSONObject(response);
                            String status = Jsonobj.getString("status");

                            if (status.equals("1")) {
                                country_list.clear();
                                country_code.clear();
                                String name,code;
                                JSONArray country = Jsonobj.getJSONArray("country");
                                for (int i = 0; i < country.length(); i++) {
                                    JSONObject j1 = country.getJSONObject(i);

                                    name = j1.getString("name");
                                    code = j1.getString("code");
                                    country_list.add(name);
                                    country_code.add(code);
                                }





                                ArrayAdapter<String> adapter =
                                        new ArrayAdapter<String>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, country_list);
                                adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
                                sp_country.setAdapter(adapter);

                             //   sp_country.onSearchableItemClicked(adapter.getItem(adapter.getPosition(country_name)),0);


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
                       // Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("language",new SharedPrefManager(UpdateAddressActivity.this).getLanguage());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GMarketApplication.getInstance(context).addToRequestQueue(stringRequest);
    }//GetCountryClose
    private int getIndex(String myString){

        for (int i=0;i<country_code.size();i++){
            if (country_code.get(i).equalsIgnoreCase(myString)){
                Toast.makeText(context, ""+i, Toast.LENGTH_SHORT).show();
                return i;
            }
        }

        return 0;
    }

    public void getStateList(final String code, final String selected_state)
    {
        p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.GET_STATES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        p_bar.setVisibility(View.GONE);
                        try {
                            state_list.clear();
                            JSONObject Jsonobj = new JSONObject(response);
                            String status = Jsonobj.getString("status");

                            if (status.equals("1")) {
                                String name,code;
                                JSONArray State = Jsonobj.getJSONArray("State");
                                for (int i = 0; i < State.length(); i++) {
                                    JSONObject j1 = State.getJSONObject(i);

                                    name = j1.getString("name");
                                    code = j1.getString("id");


                                    state_list.add(name);
                                    state_code.add(code);


                                }
                                state_list.add(0,getResources().getString(R.string.select_state));
                                state_code.add(0,"0");



                                StateSpinnerAdapter adapter = new StateSpinnerAdapter(context,state_list);
                                sp_states.setAdapter(adapter);
                               // if (state_list.size()>0)
                           //     sp_states.setSelection(getStateListIndex(selected_state));

                            } else {
                                tl_city.setVisibility(View.VISIBLE);
                                et_city.setText("");
                                ll_cities.setVisibility(View.GONE);
                                state_code.add(0,"0");
                                state_list.clear();
                                state_code.clear();
                                list_cities.clear();
                                state_list.add(0,getResources().getString(R.string.select_state));
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
                        //Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("country_code",code);
                params.put("language",new SharedPrefManager(UpdateAddressActivity.this).getLanguage());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GMarketApplication.getInstance(context).addToRequestQueue(stringRequest);
    }//GetStateClose



    public void GetCities() {
       // Toast.makeText(context, region_id, Toast.LENGTH_SHORT).show();
        p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.GET_CITIES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        p_bar.setVisibility(View.GONE);
                        try {

                            JSONObject Jsonobj = new JSONObject(response);

                            Log.i("responseeee",""+Jsonobj.toString());
                            String status = Jsonobj.getString("status");

                            if (status.equals("1")) {

                                ll_cities.setVisibility(View.VISIBLE);
                                tl_city.setVisibility(View.GONE);
                                JSONArray City = Jsonobj.getJSONArray("City");
                                for (int i = 0; i < City.length(); i++) {
                                    JSONObject j1 = City.getJSONObject(i);

                                    String id = j1.getString("id");
                                    String name = j1.getString("name");
                                    list_cities.add(name);
                                }
                                list_cities.add(0,getResources().getString(R.string.select_city));
                                // country_list.add(0,getResources().getString(R.string.select_country));
                                //country_code.add(0,"0");
                                // CountrySpinnerAdapter adapter = new CountrySpinnerAdapter(context,country_list);
                                //sp_country.setAdapter(adapter);

                                // Applying the adapter to our spinner
                                ArrayAdapter<String> adapter =
                                        new ArrayAdapter<String>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, list_cities);
                                adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
                                sp_cities.setAdapter(adapter);
                            } else {
                               /* ll_cities.setVisibility(View.GONE);
                                et_city.setText("");
                                tl_city.setVisibility(View.VISIBLE);*/
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

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("region_id",region_id);
                params.put("language",new SharedPrefManager(UpdateAddressActivity.this).getLanguage());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GMarketApplication.getInstance(context).addToRequestQueue(stringRequest);
    }//GetCountryClose


    private int getStateListIndex(String myString){

        for (int i=0;i<state_list.size();i++){
            if (state_list.get(i).equalsIgnoreCase(myString)){
                return i;
            }
        }

        return 0;
    }

    public void setData(String address_id, String fname, String lname, String contact, String street_address,String street_address2, String city, String zip, String state, String country, String is_default_billing, String isDefaultShpping)
    {
        et_first_name.setText(fname);
        et_last_name.setText(lname);
        et_telephone.setText(contact);
        et_street_one.setText(street_address);
        et_street_two.setText(street_address2);
        et_city.setText("");
        et_zip.setText(zip);
        GetCountries(country);

         //getStateList(country,state);

         if (isDefaultShpping.equals("1"))
         {
             cb_shipping.setChecked(true);
         }
         if  (is_default_billing.equals("1"))
         {
             cb_billing.setChecked(true);
         }
          if (!is_default_billing.equals("1") && !isDefaultShpping.equals("1"))
         {
             cb_billing.setChecked(false);
             cb_shipping.setChecked(false);
         }


    }

    public void UpdateAddress()
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
                                openAlertDialog();

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
                       // Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id",new SharedPrefManager(context).GetCustomerId());
                params.put("fname",et_first_name.getText().toString());
                params.put("lname",et_last_name.getText().toString());
                params.put("contact",et_telephone.getText().toString());
                params.put("street1",et_street_one.getText().toString());
                params.put("street2",et_street_two.getText().toString());
                params.put("city",et_city.getText().toString());
                params.put("zip",et_zip.getText().toString());
                params.put("state",state_name);
                params.put("country",country_code_id);
                params.put("address_id",address_id);
                params.put("language",new SharedPrefManager(UpdateAddressActivity.this).getLanguage());
                if (cb_billing.isChecked() && cb_shipping.isChecked())
                {
                    params.put("default_billing","1");
                    params.put("default_shipping","1");
                }
                if (cb_shipping.isChecked() && !cb_billing.isChecked())
                {
                    params.put("default_shipping","1");
                    params.put("default_billing","0");
                }

                if (!cb_shipping.isChecked() && cb_billing.isChecked())
                {
                    params.put("default_shipping","0");
                    params.put("default_billing","1");
                }
                if (!cb_shipping.isChecked() && !cb_billing.isChecked())
                {
                    params.put("default_shipping","0");
                    params.put("default_billing","0");
                }


                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GMarketApplication.getInstance(context).addToRequestQueue(stringRequest);
    }
    public void openAlertDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(getResources().getString(R.string.address_updated))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });

        AlertDialog alert = builder.create();
        alert.setTitle(getResources().getString(R.string.success));
        alert.show();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
    }//
}
