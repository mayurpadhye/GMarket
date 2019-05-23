package com.cube9.gmarket.Account.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cube9.gmarket.Cart.Activity.MyCartActivity;
import com.cube9.gmarket.HelperClass.CustomUtils;
import com.cube9.gmarket.HelperClass.GMarketApplication;
import com.cube9.gmarket.HelperClass.SharedPrefManager;
import com.cube9.gmarket.Login.Activity.LoginDetailsActivity;
import com.cube9.gmarket.R;
import com.cube9.gmarket.SearchProducts.MasterSearchActivity;
import com.cube9.gmarket.SearchProducts.SearchProductsActivity;
import com.cube9.gmarket.WebUrls.WebUrls;
import com.cube9.gmarket.nointernet.NoInternetActivity;
import com.cube9.gmarket.receivers.NetworkChangeReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MyAccountActivity extends AppCompatActivity {
    TextView tv_first_name,tv_last_name,tv_email,tv_mobile_number,tv_shipping_address,tv_billing_address,tv_change_language;
    ImageView iv_edit_contact,iv_edit_mobile,iv_edit_password,iv_edit_address_book;
    ProgressBar p_bar;
    Context context;
    Toolbar toolbar;
    TextView tv_title;
    ImageView iv_back,iv_search,iv_cart;
    TextView tv_cart_count;
    LinearLayout ll_change_pass;
    boolean check=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        initView();

        onClick();
    }//onCreateClose

    @Override
    protected void onResume() {
        super.onResume();


        if (new SharedPrefManager(MyAccountActivity.this).IsLogin())
        {
            tv_cart_count.setText(new SharedPrefManager(MyAccountActivity.this).getCartCount());
        }

        if (NetworkChangeReceiver.isOnline(MyAccountActivity.this))
            getUserData();
        else
        {
            startActivity(new Intent(MyAccountActivity.this, NoInternetActivity.class));

        }

    }

    public void onClick()
    {
        iv_edit_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenChangePasswordDialog();
            }
        });
        iv_edit_address_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(context,AllAddressActivity.class);
                i.putExtra("intent_from","my_account_activity");
                startActivity(i);




            }
        });

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

        iv_edit_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenPersonalDetailDialog();
            }
        });

        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyAccountActivity.this, MasterSearchActivity.class));
            }
        });

    }//onClickClose

    public void OpenPersonalDetailDialog()
    {
        final Dialog dialog=new Dialog(context);
        dialog.setContentView(R.layout.dialog_update_personal_details);
        final EditText et_first_name=(EditText)dialog.findViewById(R.id.et_first_name);
        final EditText et_last_name=(EditText)dialog.findViewById(R.id.et_last_name);
        et_first_name.setText(new SharedPrefManager(MyAccountActivity.this).getUserFirstName());
        et_last_name.setText(new SharedPrefManager(MyAccountActivity.this).getUserLastName());

        Button btn_update=(Button)dialog.findViewById(R.id.btn_update);
        dialog.show();

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isValidateFirstName(et_first_name.getText().toString().trim())==false)
                {
                    if (et_first_name.getText().toString().trim().length()==0)
                    {
                        et_first_name.setError(getResources().getString(R.string.enter_first_name));
                    }
                    else
                    {
                        et_first_name.setError(getResources().getString(R.string.first_name_error));
                    }
                }
                if (isValidateLastName(et_last_name.getText().toString().trim())==false)
                {
                    if (et_last_name.getText().toString().trim().length()==0)
                    {
                        et_last_name.setError(getResources().getString(R.string.enter_last_name));
                    }
                    else
                        et_last_name.setError(getResources().getString(R.string.last_name_error));
                    return;
                }
                UpdatePersonalDetails(et_first_name.getText().toString(),et_last_name.getText().toString(),tv_email.getText().toString(),dialog);
            }
        });

    }//


    public  void OpenChangePasswordDialog()
    {
        final Dialog dialog=new Dialog(context);
        dialog.setContentView(R.layout.dialog_change_password);
        final EditText et_current_pass=(EditText)dialog.findViewById(R.id.et_current_pass);
        final EditText et_new_password=(EditText)dialog.findViewById(R.id.et_new_password);
        final EditText et_confirm_pass=(EditText)dialog.findViewById(R.id.et_confirm_pass);
        Button btn_change_pass=(Button)dialog.findViewById(R.id.btn_change_pass);
        dialog.show();
        btn_change_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_current_pass.getText().toString().trim().length()==0)
                {
                    et_current_pass.setError(getResources().getString(R.string.current_password_error));
                    return;
                }
                if (et_new_password.getText().toString().trim().length() < 6) {
                    et_new_password.setError(getResources().getString(R.string.new_password__length_error));
                    return;
                }

                if (!et_new_password.getText().toString().equals(et_confirm_pass.getText().toString()))
                {
                    et_confirm_pass.setError(getResources().getString(R.string.password_not_matched));
                    return;
                }
                changePassword(et_current_pass.getText().toString(),et_new_password.getText().toString(),dialog,et_current_pass);


            }
        });
    }

    public void initView()
    {
        context=MyAccountActivity.this;
        ll_change_pass=findViewById(R.id.ll_change_pass);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        iv_back=(ImageView)toolbar.findViewById(R.id.iv_back);
        iv_search=(ImageView)toolbar.findViewById(R.id.iv_search);
        iv_cart=(ImageView)toolbar.findViewById(R.id.iv_cart);
        tv_title=(TextView) toolbar.findViewById(R.id.tv_title);
        tv_change_language=(TextView) toolbar.findViewById(R.id.tv_change_language);
        tv_cart_count=(TextView) toolbar.findViewById(R.id.tv_cart_count);
        p_bar=(ProgressBar)findViewById(R.id.p_bar);
        iv_edit_contact=(ImageView)findViewById(R.id.iv_edit_contact);
        iv_edit_mobile=(ImageView)findViewById(R.id.iv_edit_mobile);
        iv_edit_password=(ImageView)findViewById(R.id.iv_edit_password);
        iv_edit_address_book=(ImageView)findViewById(R.id.iv_edit_address_book);
        tv_first_name=(TextView)findViewById(R.id.tv_first_name);
        tv_last_name=(TextView)findViewById(R.id.tv_last_name);
        tv_email=(TextView)findViewById(R.id.tv_email);
        tv_mobile_number=(TextView)findViewById(R.id.tv_mobile_number);
        tv_first_name=(TextView)findViewById(R.id.tv_first_name);
        tv_shipping_address=(TextView)findViewById(R.id.tv_shipping_address);
        tv_billing_address=(TextView)findViewById(R.id.tv_billing_address);
         tv_title.setText(getResources().getString(R.string.my_account));

         if (new SharedPrefManager(context).IsFbLogin())
         {
             ll_change_pass.setVisibility(View.GONE);
         }
         else
         {
             ll_change_pass.setVisibility(View.VISIBLE);
         }
    }//initViewClose

    public  boolean isValidateFirstName( String firstName )
    {
        if (firstName.trim().equals(""))
            return  false;
        else
            return firstName.matches("[a-zA-Z]*");

    } // end method validateFirstName

    // validate last name
    public  boolean isValidateLastName( String lastName )
    {
        if (lastName.trim().equals(""))
            return  false;
        else
            return lastName.matches("[a-zA-Z]*");
    }





    public void getUserData()
    {

        p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.GET_USER_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        p_bar.setVisibility(View.GONE);
                        try {

                            JSONObject Jsonobj = new JSONObject(response);
                            String status=Jsonobj.getString("status");

                            if (status.equals("1"))
                            {
                                JSONObject customer_details=Jsonobj.getJSONObject("user_details");
                                String website_id=customer_details.getString("website_id");
                                String entity_id=customer_details.getString("entity_id");
                                String entity_type_id=customer_details.getString("entity_type_id");
                                String attribute_set_id=customer_details.getString("attribute_set_id");
                                String email=customer_details.getString("email");
                                String group_id=customer_details.getString("group_id");
                                String increment_id=customer_details.getString("increment_id");
                                String store_id=customer_details.getString("store_id");
                                String created_at=customer_details.getString("created_at");
                                String updated_at=customer_details.getString("updated_at");
                                String is_active=customer_details.getString("is_active");
                                String disable_auto_group_change=customer_details.getString("disable_auto_group_change");
                                String created_in=customer_details.getString("created_in");
                                String firstname=customer_details.getString("firstname");
                                String lastname=customer_details.getString("lastname");
                               new SharedPrefManager(MyAccountActivity.this).setUserFirstName(firstname);
                                new SharedPrefManager(MyAccountActivity.this).setUserLastName(lastname);
                                String mobilenum="";
                                if (customer_details.has("mobilenum")) {
                                     mobilenum = customer_details.getString("mobilenum");
                                }
                                JSONObject shippping_address = null;
                                if (!Jsonobj.isNull("shippping_address"))
                                {

                                    shippping_address=Jsonobj.getJSONObject("shippping_address");
                                }
                                JSONObject billing_address=null;
                                if (!Jsonobj.isNull("billing_address"))
                                {
                                    billing_address=Jsonobj.getJSONObject("billing_address");
                                }


                                setData(firstname,lastname,email,shippping_address,billing_address,Jsonobj,mobilenum);

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
                params.put("language",new SharedPrefManager(context).getLanguage());
                params.put("user_id",new SharedPrefManager(context).GetCustomerId());


                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GMarketApplication.getInstance(context).addToRequestQueue(stringRequest);
    }//getUserDataClose

    private void setData(String firstname, String lastname, String email, JSONObject shippping_address, JSONObject billing_address, JSONObject jsonObject, String mobilenum)
    {
        tv_email.setText(email);
        tv_first_name.setText(firstname);
        tv_last_name.setText(lastname);
        tv_mobile_number.setText(getResources().getString(R.string.country_code)+" "+mobilenum);

        if (jsonObject.isNull("shippping_address"))
        {
            tv_shipping_address.setText(getResources().getString(R.string.shipping_address_not_found));
        }
        else
        {
            try {
                String address_fname=shippping_address.getString("address_fname");
                String address_lname=shippping_address.getString("address_lname");
                String telephone=shippping_address.getString("telephone");
                String city=shippping_address.getString("city");
                String zip=shippping_address.getString("zip");
                String state=shippping_address.getString("state");
                String country=shippping_address.getString("country");
                JSONArray street=shippping_address.getJSONArray("street");
                String street_address="";
                for (int i=0;i<street.length();i++)
                {
                    street_address=street_address+" "+street.get(i);
                }

                tv_shipping_address.setText(" "+address_fname+" "+address_lname+" \n"+" "+telephone+"\n"+street_address + ", " + city + ", " + zip + "\n " + state + ", " + " "+country);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        if (jsonObject.isNull("billing_address"))
        {
            tv_billing_address.setText(getResources().getString(R.string.billing_address_not_found));
        }
        else {
            try {
                String address_fname = billing_address.getString("address_fname");
                String address_lname = billing_address.getString("address_lname");
                String telephone = billing_address.getString("telephone");
                String city = billing_address.getString("city");
                String zip = billing_address.getString("zip");
                String state = billing_address.getString("state");
                String country = billing_address.getString("country");
                JSONArray street = billing_address.getJSONArray("street");
                String street_address = "";
                for (int i = 0; i < street.length(); i++) {
                    street_address = street_address + " " + street.get(i);
                }

                tv_billing_address.setText(" "+address_fname+" "+address_lname+" \n"+" "+telephone+"\n"+street_address + ", " + city + ", " + zip + "\n " + state + ", " + " "+country);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }//setDataClose

    public void changePassword(final String current_password, final String new_password, final Dialog dialog, final EditText et_current_pass)
    {

        p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.CHANGE_PASSWORD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        p_bar.setVisibility(View.GONE);
                        try {

                            JSONObject Jsonobj = new JSONObject(response);
                            String status=Jsonobj.getString("status");
                            if (status.equals("1"))
                            {
                                String message=Jsonobj.getString("message");
                                CustomUtils.showToast(message,context);
                                new SharedPrefManager(context).Logout();
                                dialog.dismiss();
                                openAlertDialog(getResources().getString(R.string.password_change_success),"password");
                            }
                            else
                            {
                                String message=Jsonobj.getString("message");
                                CustomUtils.showToast(message,context);
                                et_current_pass.setError(message);

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
                     //   Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("language",new SharedPrefManager(context).getLanguage());
                params.put("user_id",new SharedPrefManager(context).GetCustomerId());
                params.put("current_password",current_password);
                params.put("new_password",new_password);


                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GMarketApplication.getInstance(context).addToRequestQueue(stringRequest);

    }//ChangePassword


    public void UpdatePersonalDetails(final String fname, final String lname, final String email, final Dialog dialog)
    {
        p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.UPDATE_USER_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        p_bar.setVisibility(View.GONE);
                        try {

                            JSONObject Jsonobj = new JSONObject(response);

                            Log.i("OUTPUT",""+response.toString());
                            String status=Jsonobj.getString("status");
                            if (status.equals("1"))
                            {
                                getUserData();
                                String message=Jsonobj.getString("message");

                                dialog.dismiss();
                                openAlertDialog(getResources().getString(R.string.user_details_updated),"user_details");
                            }
                            else
                            {
                                String message=Jsonobj.getString("message");
                                CustomUtils.showToast(message,context);


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
                       ////Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("language",new SharedPrefManager(context).getLanguage());
                params.put("user_id",new SharedPrefManager(context).GetCustomerId());
                params.put("fname",fname);
                params.put("lname",lname);
                params.put("email",email);


                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GMarketApplication.getInstance(context).addToRequestQueue(stringRequest);

    }//updatePersonalDetailsClose


    public void openAlertDialog(String msg, final String update_from)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (update_from.equals("password"))
                        {
                            Intent intent=new Intent(context,LoginDetailsActivity.class);
                            intent.putExtra("intent_from","change_password");
                            startActivity(intent);
                            finish();
                        }
                        else
                        {

                            dialog.dismiss();

                        }


                    }
                });

        AlertDialog alert = builder.create();
        alert.setTitle(getResources().getString(R.string.success));
        alert.show();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
    }//


}
