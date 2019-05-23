package com.cube9.gmarket.Login.Fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.cube9.gmarket.Home.Activities.HomeActivity;
import com.cube9.gmarket.Login.Activity.LoginDetailsActivity;
import com.cube9.gmarket.R;
import com.cube9.gmarket.WebUrls.WebUrls;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {
View v;
ProgressBar p_bar;
Button btn_sign_up;
EditText et_cpassword,et_password,et_mobile_no,et_email_id,et_last_name,et_first_name;
boolean check=false;
RelativeLayout rl_main;
    String intent_from="",product_id="";
    Intent i;
    private static final String EMAIL = "email";
    LoginButton loginButton;
    CallbackManager callbackManager;
    View.OnClickListener mOnClickListener;

    public SignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v= inflater.inflate(R.layout.fragment_sign_up, container, false);
        initView(v);
        facebookLogin();
        onClick();

       return v;
    }//onCreateViewClose
    public void onClick()
    {
        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CustomUtils.isNetworkAvailable(getActivity()))
              submitForm();
                else
                    CustomUtils.showToast(getResources().getString(R.string.no_internet),getActivity());
            }
        });
    }//onClickClose

    public void submitForm()
    {
      //  SignUp();
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

            return;
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
        if (!isValidMail(et_email_id.getText().toString().trim()))
        {
            if (et_email_id.getText().toString().trim().length()==0)
            {
                et_email_id.setError(getResources().getString(R.string.enter_email_id));
            }
            else
            {
                et_email_id.setError(getResources().getString(R.string.email_error));
            }

            return;
        }

        if (!isValidMobile(et_mobile_no.getText().toString().trim()))
        {
            if (et_mobile_no.getText().toString().trim().length()==0)
            {
                et_mobile_no.setError(getResources().getString(R.string.enter_mobile_no));
            }
            else
            {
                et_mobile_no.setError(getResources().getString(R.string.mobile_error));
            }

            return;
        }
        if (!validatePassword(et_password.getText().toString().trim()))
        {
            et_password.setError(getResources().getString(R.string.password_error));
            return;
        }
        if (!validateConfirmPassword(et_cpassword.getText().toString().trim()))
        {
            if (et_cpassword.getText().toString().trim().length()==0)
            {
                et_cpassword.setError(getResources().getString(R.string.enter_confirm_pass));
            }
            else
            et_cpassword.setError(getResources().getString(R.string.Cpassword_error));
            return;
        }
       SignUp();
    }//submitFormClose

    public void facebookLogin()
    {

        loginButton.setReadPermissions("public_profile,email");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                getFacebookUserDetails(loginResult);
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
    }//facebookLoginClose
    protected void getFacebookUserDetails(LoginResult loginResult) {
        GraphRequest data_request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject json_object,
                            GraphResponse response) {

                        Log.i("facebook_details",json_object.toString());
                        try {
                            String fb_id=json_object.getString("id");
                            String name=json_object.getString("name");
                            String[] name_string = name.split(" ");

                            String f_name="";
                            String l_name="";
                            f_name=name_string[0];
                            int name_string_len=name_string.length-1;
                            l_name=name_string[name_string_len];
                            String email="";
                            if (json_object.has("email"))
                            {
                                email=json_object.getString("email");
                            }
                            else
                            {
                                LoginManager.getInstance().logOut();
                                Snackbar.make(rl_main, ""+getResources().getString(R.string.error_fatching_email), Snackbar.LENGTH_LONG)
                                        .setActionTextColor(Color.RED)
                                        .show();
                                return;
                            }

                            loginToApp(f_name,l_name,fb_id,name,email);

                        }

                        catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                });
        Bundle permission_param = new Bundle();
        permission_param.putString("fields", "id,name,email,picture.width(120).height(120)");
        data_request.setParameters(permission_param);
        data_request.executeAsync();

    }

    private void loginToApp(final String f_name, final String l_name, String fb_id, String name, final String email) {
        p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.FACEBOOK_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        p_bar.setVisibility(View.GONE);
                        LoginManager.getInstance().logOut();
                        try {

                            //converting response to json object
                            JSONObject Jsonobj = new JSONObject(response);
                            String status=Jsonobj.getString("status");
                            if (status.equals("1"))
                            {
                                String user_id=Jsonobj.getString("user_id");
                                String fname=Jsonobj.getString("fname");
                                String lname=Jsonobj.getString("lname");
                                String message=Jsonobj.getString("message");
                                String cart_counter=Jsonobj.getString("cart_counter");
                                SharedPrefManager sharedPrefManager= new SharedPrefManager(getActivity());
                                sharedPrefManager.setUserFirstName(fname);
                                sharedPrefManager.setUserLastName(lname);
                                sharedPrefManager.SetCustomerId(user_id);
                                sharedPrefManager.setMobileNumber("");
                                sharedPrefManager.setPassword(et_password.getText().toString());
                                sharedPrefManager.SetisLogin(true);
                                sharedPrefManager.setIsFbLogin(true);
                                sharedPrefManager.setCartCount(cart_counter);
                                startActivity(new Intent(getActivity(),HomeActivity.class));
                                getActivity().finish();
                            }
                            else
                            {
                                Snackbar.make(rl_main, ""+Jsonobj.getString("message"), Snackbar.LENGTH_LONG)
                                        .setAction(getResources().getString(R.string.retry), mOnClickListener)
                                        .setActionTextColor(Color.RED)
                                        .show();

                                mOnClickListener = new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {


                                    }
                                };
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
                        LoginManager.getInstance().logOut();
                        // Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
              //  params.put("mobilenum", et_username.getText().toString().trim());
                params.put("fname", f_name);
                params.put("lname", l_name);
                params.put("email", email);
                params.put("language",new SharedPrefManager(getActivity()).getLanguage());
                params.put("password","1234567");

                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GMarketApplication.getInstance(getActivity()).addToRequestQueue(stringRequest);
      //  sdsdsd

    }
    public void SignUp()
    {
        p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.SIGN_UP,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        p_bar.setVisibility(View.GONE);
                        try {
                            //converting response to json object
                            JSONObject Jsonobj = new JSONObject(response);
                            Log.i("sigggg",""+Jsonobj.toString());
                            String status=Jsonobj.getString("status");
                            if (status.equals("1"))
                            {
                                SharedPrefManager sharedPrefManager= new SharedPrefManager(getActivity());
                                String message=Jsonobj.getString("message");
                                String customer_id=Jsonobj.getString("Customer_id");
                                sharedPrefManager.SetCustomerId(customer_id);
                                sharedPrefManager.setMobileNumber(et_mobile_no.getText().toString());
                                sharedPrefManager.setPassword(et_password.getText().toString());
                                sharedPrefManager.setEmail(et_email_id.getText().toString());
                                sharedPrefManager.SetPassRemeber(false);
                                RegistrationSuccessDialog();
                            }
                            else
                            {
                                Snackbar.make(rl_main, ""+Jsonobj.getString("message"), Snackbar.LENGTH_LONG)
                                    .setAction(getResources().getString(R.string.retry), mOnClickListener)
                                    .setActionTextColor(Color.RED)
                                    .show();
                                mOnClickListener = new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        submitForm();
                                    }
                                };
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
                       // Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("fname", et_first_name.getText().toString().trim());
                params.put("lname", et_last_name.getText().toString().trim());
                params.put("email", et_email_id.getText().toString().trim());
                params.put("password", et_password.getText().toString().trim());
                params.put("mobilenum", et_mobile_no.getText().toString().trim());
                params.put("language",new SharedPrefManager(getActivity()).getLanguage());

                Log.i("fname", et_first_name.getText().toString().trim());
                Log.i("lname", et_last_name.getText().toString().trim());
                Log.i("email", et_email_id.getText().toString().trim());
                Log.i("password", et_password.getText().toString().trim());
                Log.i("mobilenum", et_mobile_no.getText().toString().trim());
                Log.i("language",new SharedPrefManager(getActivity()).getLanguage());

                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GMarketApplication.getInstance(getActivity()).addToRequestQueue(stringRequest);


    }//signUpClose

    public void initView(View v)
    {
        loginButton = (LoginButton) v.findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(EMAIL));
        callbackManager = CallbackManager.Factory.create();
        try {
            i=getActivity().getIntent();
            if(i.hasExtra("intent_from")) {
                intent_from = i.getStringExtra("intent_from");
                product_id = i.getStringExtra("product_id"); //Do first time stuff here

            } else {
                //Do stuff with intent data here
                intent_from="";
                product_id="";
            }


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        p_bar=(ProgressBar)v.findViewById(R.id.p_bar);
        et_cpassword=(EditText) v.findViewById(R.id.et_cpassword);
        et_password=(EditText) v.findViewById(R.id.et_password);
        et_mobile_no=(EditText) v.findViewById(R.id.et_mobile_no);
        et_email_id=(EditText) v.findViewById(R.id.et_email_id);
        et_last_name=(EditText) v.findViewById(R.id.et_last_name);
        et_first_name=(EditText) v.findViewById(R.id.et_first_name);
        btn_sign_up=(Button) v.findViewById(R.id.btn_sign_up);
        rl_main=(RelativeLayout)v.findViewById(R.id.rl_main);
    }//initViewClose

    private boolean isValidMail(String email) {
        boolean check;
        Pattern p;
        Matcher m;

        String EMAIL_STRING = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        p = Pattern.compile(EMAIL_STRING);

        m = p.matcher(email);
        check = m.matches();

        if(!check) {
            et_email_id.setError("Not Valid Email");
        }
        return check;
    }
    private boolean isValidMobile(String phone) {
        boolean check=false;
        if(!Pattern.matches("[a-zA-Z]+", phone)) {
            if(phone.length() < 8 || phone.length() > 15) {
                // if(phone.length() != 10) {
                check = false;
                et_mobile_no.setError("Not Valid Number");
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

    public boolean validatePassword( String password)
    {
        if (password.length()>=6)
        {
            return true;
        }
        else
       return false;
    }

    public boolean validateConfirmPassword( String Cpassword)
    {
        if (Cpassword.length()>0 && Cpassword.equals(et_password.getText().toString()))
        {
            return true;
        }
        else
            return false;
    }

    public void RegistrationSuccessDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getResources().getString(R.string.registration_success))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (intent_from.equals(""))
                        {
                            startActivity(new Intent(getActivity(), LoginDetailsActivity.class));
                            getActivity().finish();
                        }
                        else
                        {
                            Intent intent=new Intent(getActivity(),LoginDetailsActivity.class);
                            intent.putExtra("intent_from",  intent_from);
                            intent.putExtra("product_id",  product_id);
                            startActivity(intent);
                            getActivity().finish();

                        }

                    }
                });

         AlertDialog alert = builder.create();
         alert.setTitle(getResources().getString(R.string.success));
         alert.show();
    }

}
