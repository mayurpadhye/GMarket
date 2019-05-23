package com.cube9.gmarket.Login.Fragments;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
import com.cube9.gmarket.HelperClass.CustomUtils;
import com.cube9.gmarket.HelperClass.GMarketApplication;
import com.cube9.gmarket.HelperClass.SharedPrefManager;
import com.cube9.gmarket.Home.Activities.HomeActivity;
import com.cube9.gmarket.R;
import com.cube9.gmarket.WebUrls.WebUrls;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;
import static com.facebook.login.widget.ProfilePictureView.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment {
   View v;
   EditText et_username,et_password;
   Button btn_sign_in;
   RelativeLayout rl_main;
   CheckBox cb_remember_me;

    View.OnClickListener mOnClickListener;
    Dialog ForgetPasswordDialog;
    TextView tv_forgot_pass;
    ProgressBar p_bar;
    Intent i;
    String intent_from="",product_id="";
    private static final String EMAIL = "email";
    LoginButton loginButton;
    CallbackManager callbackManager;
    SignInButton btn_google_sign_in;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 007;
    public SignInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        v= inflater.inflate(R.layout.fragment_sign_in, container, false);
        initView(v);
        initGoogleSignIN();
        facebookLogin();
        onClick();
        return v;
    }//onCreateViewClose

    public void initView(View v)
    {
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
            intent_from="";
            product_id="";
            e.printStackTrace();
        }

        btn_google_sign_in = (SignInButton) v.findViewById(R.id.btn_google_sign_in);
        et_username=(EditText)v.findViewById(R.id.et_username);
        et_password=(EditText)v.findViewById(R.id.et_password);
        btn_sign_in=(Button) v.findViewById(R.id.btn_sign_in);
        rl_main=(RelativeLayout) v.findViewById(R.id.rl_main);
        tv_forgot_pass=(TextView) v.findViewById(R.id.tv_forgot_pass);
        p_bar=(ProgressBar)v.findViewById(R.id.p_bar);
        cb_remember_me=(CheckBox) v.findViewById(R.id.cb_remember_me);
        if (new SharedPrefManager(getActivity()).IsPassRemeber())
         {
             cb_remember_me.setChecked(true);
             et_username.setText(new SharedPrefManager(getActivity()).getMobileNumber());
         }

        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        loginButton = (LoginButton) v.findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(EMAIL));
        callbackManager = CallbackManager.Factory.create();
     /* boolean isLoggedIn = accessToken != null && !accessToken.isExpired();


        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));*/
    }//initViewClose

    public void onClick()
    {
        btn_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CustomUtils.isNetworkAvailable(getActivity()))
                SubmitForm();
                else
                    Snackbar.make(rl_main, ""+getResources().getString(R.string.no_internet), Snackbar.LENGTH_LONG)
                            .setActionTextColor(Color.RED)
                            .show();

            }
        });
        btn_google_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleSignIn();
            }
        });
        tv_forgot_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForgetPasswordDialog();
            }
        });
    }//onClickClose

    public void initGoogleSignIN()
    {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity(), new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Customizing G+ button
        btn_google_sign_in.setSize(SignInButton.SIZE_STANDARD);
        btn_google_sign_in.setScopes(gso.getScopeArray());
    }//GoogleSignInClose

    private void GoogleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void GooglesignOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        updateUI(false);
                    }
                });
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            Log.e(TAG, "display name: " + acct.getDisplayName());

            String personName = acct.getDisplayName();
//            String personPhotoUrl = acct.getPhotoUrl().toString();
            String email = acct.getEmail();

            Log.e(TAG, "Name: " + personName + ", email: " + email
                    + ", Image: ");

           /* txtName.setText(personName);
            txtEmail.setText(email);
            Glide.with(getApplicationContext()).load(personPhotoUrl)
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgProfilePic);*/

            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }


    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.

            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {

                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }
    private void updateUI(boolean isSignedIn) {
        if (isSignedIn) {
           GooglesignOut();

        } else {


        }
    }

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
                        try {
                            LoginManager.getInstance().logOut();
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
                                LoginManager.getInstance().logOut();
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
                        LoginManager.getInstance().logOut();
                        p_bar.setVisibility(View.GONE);
                        // Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
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
        //sdsdsd

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       //callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
       /*if (requestCode == RC_SIGN_IN) {
           GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
           handleSignInResult(result);
       }*/
       callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void ForgetPasswordDialog()
    {
        ForgetPasswordDialog=new Dialog(getActivity());
        ForgetPasswordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ForgetPasswordDialog.setContentView(R.layout.forgot_password_dialog);
        final EditText et_email=(EditText)ForgetPasswordDialog.findViewById(R.id.et_email);
        Button btn_cancel=(Button)ForgetPasswordDialog.findViewById(R.id.btn_cancel);
        Button btn_ok=(Button)ForgetPasswordDialog.findViewById(R.id.btn_ok);
        ForgetPasswordDialog.show();

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isValidMail(et_email.getText().toString().trim(),et_email))
                {
                    if (et_email.getText().toString().trim().length()==0)
                    {
                        et_email.setError(getResources().getString(R.string.enter_email_id));
                    }
                    else
                    {
                        et_email.setError(getResources().getString(R.string.email_error));
                    }

                }

                else
                {   if (CustomUtils.isNetworkAvailable(getActivity()))
                    SendPasswordResetLink(et_email.getText().toString().trim());
                else
                    CustomUtils.showToast(getResources().getString(R.string.no_internet),getActivity());
                 }
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForgetPasswordDialog.dismiss();
            }
        });

    }


    public void SubmitForm()
    {

        if (!isValidMobile(et_username.getText().toString().trim()))
        {

            if (et_username.getText().toString().trim().length()==0)
            {
                et_username.setError(getResources().getString(R.string.enter_mobile_error));
                Snackbar.make(rl_main, ""+getResources().getString(R.string.enter_mobile_error), Snackbar.LENGTH_LONG)
                        .setAction(getResources().getString(R.string.retry), mOnClickListener)
                        .setActionTextColor(Color.RED)
                        .show();
            }
            else
            {
                et_username.setError(getResources().getString(R.string.mobile_error));
                Snackbar.make(rl_main, ""+getResources().getString(R.string.mobile_error), Snackbar.LENGTH_LONG)
                        .setAction(getResources().getString(R.string.retry), mOnClickListener)
                        .setActionTextColor(Color.RED)
                        .show();
            }



            mOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SubmitForm();
                }
            };

            return;
        }
        if (!validatePassword(et_password.getText().toString().trim()))
        {
            et_password.setError(getResources().getString(R.string.password_error));
            Snackbar.make(rl_main, ""+getResources().getString(R.string.password_error), Snackbar.LENGTH_LONG)
                    .setAction(getResources().getString(R.string.retry), mOnClickListener)
                    .setActionTextColor(Color.RED)
                    .show();

            mOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SubmitForm();
                }
            };
            return;
        }
        Login();
    }
    private boolean isValidMobile(String phone) {
        boolean check=false;
        if(!Pattern.matches("[a-zA-Z]+", phone)) {
            if(phone.length() < 8 || phone.length() > 15) {
                // if(phone.length() != 10) {
                check = false;
                et_username.setError("Not Valid Number");
            } else {
                check = true;
            }
        } else {
            check=false;
        }
        return check;
    }

    public boolean validatePassword( String password)
    {
        if (password.length()>0)
        {
            return true;
        }
        else
            return false;
    }

    private boolean isValidMail(String email,EditText et_email) {
        boolean check;
        Pattern p;
        Matcher m;

        String EMAIL_STRING = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        p = Pattern.compile(EMAIL_STRING);

        m = p.matcher(email);
        check = m.matches();

        if(!check) {
            et_email.setError("Not Valid Email");
        }
        return check;
    }
    public void Login()
    {
        p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        p_bar.setVisibility(View.GONE);
                        try {

                            //converting response to json object
                            JSONObject Jsonobj = new JSONObject(response);
                            String status=Jsonobj.getString("status");
                            if (status.equals("1"))
                            {
                                String cart_counter=Jsonobj.getString("cart_counter");

                                SharedPrefManager sharedPrefManager= new SharedPrefManager(getActivity());
                                String message=Jsonobj.getString("message");
                                String customer_id=Jsonobj.getString("user_id");
                                String fname=Jsonobj.getString("fname");
                                String lname=Jsonobj.getString("lname");
                                sharedPrefManager.setUserFirstName(fname);
                                sharedPrefManager.setUserLastName(lname);
                                sharedPrefManager.SetCustomerId(customer_id);
                                sharedPrefManager.setMobileNumber(et_username.getText().toString());
                                sharedPrefManager.setPassword(et_password.getText().toString());
                                sharedPrefManager.SetisLogin(true);
                                sharedPrefManager.setCartCount(cart_counter);

                                if (cb_remember_me.isChecked())
                                sharedPrefManager.SetPassRemeber(true);
                                else
                                    sharedPrefManager.SetPassRemeber(false);
                                  if (!intent_from.equals(""))
                                  {
                                      if (intent_from.equals("change_password"))
                                      {
                                          startActivity(new Intent(getActivity(), HomeActivity.class));
                                          getActivity().finish();

                                      }

                                      else
                                      {
                                          Intent intent = new Intent();
                                          intent.putExtra("intent_from",  intent_from);
                                          intent.putExtra("product_id",  product_id);
                                          getActivity().setResult(RESULT_OK, intent);
                                          getActivity().finish();
                                      }
                                  }

                               else
                                {
                                    startActivity(new Intent(getActivity(), HomeActivity.class));
                                    getActivity().finish();
                                }

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
                       // Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("mobilenum", et_username.getText().toString().trim());
                params.put("password", et_password.getText().toString().trim());
                params.put("language",new SharedPrefManager(getActivity()).getLanguage());

                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GMarketApplication.getInstance(getActivity()).addToRequestQueue(stringRequest);


    }

    public void SendPasswordResetLink(final String email)
    {
        p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.FORGOT_PASSWORD,
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
                                Snackbar.make(rl_main, ""+message, Snackbar.LENGTH_LONG)
                                        .setActionTextColor(Color.RED)
                                        .show();
                                ForgetPasswordDialog.dismiss();
                            }
                            else
                            {
                                Snackbar.make(rl_main, ""+Jsonobj.getString("message"), Snackbar.LENGTH_LONG)
                                        .setActionTextColor(Color.RED)
                                        .show();
                                ForgetPasswordDialog.dismiss();
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
                      //  Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("language",new SharedPrefManager(getActivity()).getLanguage());

                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GMarketApplication.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }



}
