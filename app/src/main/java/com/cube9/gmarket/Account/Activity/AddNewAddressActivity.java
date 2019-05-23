package com.cube9.gmarket.Account.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

public class AddNewAddressActivity extends AppCompatActivity {
    EditText et_first_name, et_last_name, et_company, et_telephone, et_street_two,et_street_one, et_city, et_zip;
    SearchableSpinner sp_country;
    List<String> list_cities;
    Spinner sp_states;
    Button btn_save_address;
    ProgressBar p_bar;
    Toolbar toolbar;
    TextView tv_title,tv_cart_count;
    ImageView iv_back,iv_search,iv_cart;
    List<String> country_list,state_list,state_code,state_id_list;
    List<String> country_code;
    Context context;
    String state_name="",country_name="",default_shipping="";
    public static String COUNTRY_HINT="";
    CheckBox cb_shipping,cb_billing;
    String state_id="",country_code_id="";
    String   intent_from="";
    LinearLayout ll_cities;
    TextInputLayout tl_city;
    String region_id="";
    Spinner sp_cities;
    boolean check=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_address);
        initView();
        onClick();


        final int[] iCurrentSelection = {sp_country.getSelectedItemPosition()};
        sp_country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0,
                                               View arg1, int arg2, long arg3) {

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
                            ((TextView) arg1).setTextColor(Color.BLACK); //Change selected text color
                            country_code_id=country_code.get(arg2);
                            state_list.clear();
                            state_code.clear();
                            list_cities.clear();
                            sp_cities.setAdapter(null);
                            et_city.setText("");
                            tl_city.setVisibility(View.VISIBLE);
                            ll_cities.setVisibility(View.GONE);
                            getStateList(country_code.get(arg2));
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
                          //  ((TextView) arg1).setTextColor(Color.BLACK);
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
                            ((TextView) arg1).setTextColor(Color.BLACK);
                            et_city.setText(list_cities.get(arg2));
                        }


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {

                    }

                });
    }//onCreateClose


    @Override
    protected void onResume() {
        super.onResume();
        iv_search.setVisibility(View.GONE);
        iv_cart.setVisibility(View.GONE);
        tv_cart_count.setVisibility(View.GONE);
        if (NetworkChangeReceiver.isOnline(AddNewAddressActivity.this))
        {
            GetCountries();
            try {
                Intent i=getIntent();

                if(i.hasExtra("intent_from")) {
                    intent_from = i.getStringExtra("intent_from");


                } else {
                    //Do stuff with intent data here
                    intent_from="";

                }

            }
            catch (Exception e)
            {
                intent_from="";

                e.printStackTrace();
            }


            if (intent_from.equals("delivery_detail") || intent_from.equals("delivery_detail_buy_now"))
            {
                cb_shipping.setChecked(true);
                cb_billing.setChecked(true);
                cb_shipping.setClickable(false);
                cb_shipping.setEnabled(false);
                cb_billing.setEnabled(false);
                cb_billing.setClickable(false);
                default_shipping="2";
            }
            else
            {
                cb_shipping.setClickable(true);
                cb_shipping.setEnabled(true);
                cb_billing.setEnabled(true);
                cb_billing.setClickable(true);
            }
        }

        else
        {
            startActivity(new Intent(AddNewAddressActivity.this, NoInternetActivity.class));

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
                if (NetworkChangeReceiver.isOnline(AddNewAddressActivity.this)) {


                    if (!isValidateFirstName(et_first_name.getText().toString().trim())) {
                        et_first_name.setError(getResources().getString(R.string.first_name_error));
                        return;
                    }

                    if (!isValidateLastName(et_last_name.getText().toString().trim())) {
                        et_last_name.setError(getResources().getString(R.string.last_name_error));
                        return;
                    }
                    if (!isValidMobile(et_telephone.getText().toString().trim())) {
                        et_telephone.setError(getResources().getString(R.string.mobile_error));
                        return;
                    }
                    if (et_street_one.getText().toString().length() == 0) {
                        et_street_one.setError(getResources().getString(R.string.enter_street_name));
                        return;
                    }

                    if (et_street_two.getText().toString().length() == 0) {
                        et_street_two.setError(getResources().getString(R.string.enter_street_name));
                        return;
                    }
                   /* if (et_city.getText().toString().length() == 0) {
                        et_city.setError(getResources().getString(R.string.enter_city));
                        return;
                    }*/
                    if (country_code_id.length() == 0) {
                        CustomUtils.showToast(getResources().getString(R.string.select_country), context);
                        return;
                    }
                    if (state_name.length() == 0) {
                        CustomUtils.showToast(getResources().getString(R.string.select_state), context);
                        return;
                    }
                   if (list_cities.size()>0) {
                       if (sp_cities.getSelectedItem().toString().equals(getResources().getString(R.string.select_city)) || et_city.getText().toString().equals(null)) {
                           et_city.setError(getResources().getString(R.string.enter_city));
                           return;
                       }
                   }

                    AddNewAddress();
                }
                else
                {
                    startActivity(new Intent(AddNewAddressActivity.this, NoInternetActivity.class));

                }
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

    public void initView() {
        context=AddNewAddressActivity.this;
        COUNTRY_HINT=getResources().getString(R.string.select_country);
        ll_cities=(LinearLayout)findViewById(R.id.ll_cities);
        tl_city=(TextInputLayout) findViewById(R.id.tl_city);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
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
        sp_country = (SearchableSpinner) findViewById(R.id.sp_country);
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
        list_cities = new ArrayList<String>();
        tv_title.setText(getResources().getString(R.string.add_address));
        et_first_name.setText(new SharedPrefManager(AddNewAddressActivity.this).getUserFirstName());
        et_last_name.setText(new SharedPrefManager(AddNewAddressActivity.this).getUserLastName());
        et_telephone.setText(new SharedPrefManager(AddNewAddressActivity.this).getMobileNumber());
        state_list.add(0,getResources().getString(R.string.select_state));
        list_cities.add(0,getResources().getString(R.string.select_city));

        StateSpinnerAdapter adapter = new StateSpinnerAdapter(context,state_list);
        sp_states.setAdapter(adapter);
        ArrayAdapter<String> adapter1 =
                new ArrayAdapter<String>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, list_cities);
        adapter1.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
        sp_cities.setAdapter(adapter1);
    }//

    public void GetCountries() {
        p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.GET_COUNTRIES,
                new Response.Listener<String>() {
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
                               // country_list.add(0,getResources().getString(R.string.select_country));
                                //country_code.add(0,"0");
                               // CountrySpinnerAdapter adapter = new CountrySpinnerAdapter(context,country_list);
                                //sp_country.setAdapter(adapter);

                                // Applying the adapter to our spinner
                                ArrayAdapter<String> adapter =
                                        new ArrayAdapter<String>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, country_list);
                             //   adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
                                sp_country.setAdapter(adapter);
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

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("language",new SharedPrefManager(AddNewAddressActivity.this).getLanguage());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GMarketApplication.getInstance(context).addToRequestQueue(stringRequest);
    }//GetCountryClose

    public void getStateList(final String code)
    {
        p_bar.setVisibility(View.VISIBLE);
        list_cities.clear();
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
                                JSONArray country = Jsonobj.getJSONArray("State");
                                for (int i = 0; i < country.length(); i++) {
                                    JSONObject j1 = country.getJSONObject(i);

                                    name = j1.getString("name");
                                    code = j1.getString("id");

                                    state_list.add(name);
                                    state_code.add(code);


                                }
                                state_list.add(0,getResources().getString(R.string.select_state));
                                state_code.add(0,"0");



                                StateSpinnerAdapter adapter = new StateSpinnerAdapter(context,state_list);
                                sp_states.setAdapter(adapter);
                            } else {
                                tl_city.setVisibility(View.VISIBLE);
                                et_city.setText("");
                                ll_cities.setVisibility(View.GONE);
                                sp_cities.setSelection(0);
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

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("country_code",code);
                params.put("language",new SharedPrefManager(AddNewAddressActivity.this).getLanguage());
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

        p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.GET_CITIES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        p_bar.setVisibility(View.GONE);
                        try {
                            list_cities.clear();
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
                                         ll_cities.setVisibility(View.GONE);
                                         et_city.setText("");
                                         tl_city.setVisibility(View.VISIBLE);
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
                params.put("language",new SharedPrefManager(AddNewAddressActivity.this).getLanguage());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GMarketApplication.getInstance(context).addToRequestQueue(stringRequest);
    }

    public void AddNewAddress()
    {
        p_bar.setVisibility(View.VISIBLE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.ADD_NEW_ADDRESS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        p_bar.setVisibility(View.GONE);
                        try {

                            JSONObject Jsonobj = new JSONObject(response);
                            String status = Jsonobj.getString("status");

                            if (status.equals("1")) {
                                String message=Jsonobj.getString("message");

                                if (intent_from.equals("delivery_detail"))
                                {
                                     Intent intent = new Intent();
                                     intent.putExtra("intent_from", intent_from );
                                     setResult(RESULT_OK, intent);
                                     finish();
                                }
                                else
                                {
                                    openAlertDialog();
                                }


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
                      //  Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                params.put("language",new SharedPrefManager(AddNewAddressActivity.this).getLanguage());
                if (cb_billing.isChecked() && cb_shipping.isChecked())
                {
                    params.put("default_shipping","2");
                }
                else
                {
                    params.put("default_shipping",default_shipping);

                }



                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GMarketApplication.getInstance(context).addToRequestQueue(stringRequest);
    }//addNewAddressClose





    public void openAlertDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(getResources().getString(R.string.address_saved_successfully))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent();
                        intent.putExtra("intent_from",  intent_from);
                        setResult(RESULT_OK, intent);
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
