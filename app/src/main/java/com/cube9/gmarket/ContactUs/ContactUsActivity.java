package com.cube9.gmarket.ContactUs;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.cube9.gmarket.R;
import com.cube9.gmarket.WebUrls.WebUrls;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContactUsActivity extends AppCompatActivity {
EditText et_name,et_email,et_telephone,et_comment;
Button btn_submit;
ProgressBar p_bar;
    Toolbar toolbar;
    TextView tv_title;
    ImageView iv_back,iv_search,iv_cart;
    TextView tv_cart_count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        initView();
        onClick();
    }
    protected void onResume() {
        super.onResume();
        iv_cart.setVisibility(View.GONE);
        iv_search.setVisibility(View.GONE);
        tv_cart_count.setVisibility(View.GONE);

    }

    private void onClick() {
btn_submit.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if (CustomUtils.isNetworkAvailable(ContactUsActivity.this))
            SubmitForm();
    }
});

iv_back.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        finish();
    }
});

    }

    private void SubmitForm() {

        if (isValidateFirstName(et_name.getText().toString().trim())==false)
        {
            if (et_name.getText().toString().trim().length()==0)
            {
                et_name.setError(getResources().getString(R.string.enter_name));
            }
            else
            {
                et_name.setError(getResources().getString(R.string.invalid_name));
            }

            return;
        }

        if (!isValidMail(et_email.getText().toString().trim()))
        {
            if (et_email.getText().toString().trim().length()==0)
            {
                et_email.setError(getResources().getString(R.string.enter_email_id));
            }
            else
            {
                et_email.setError(getResources().getString(R.string.email_error));
            }

            return;
        }

        if (!isValidMobile(et_telephone.getText().toString().trim()))
        {
            if (et_telephone.getText().toString().trim().length()==0)
            {
                et_telephone.setError(getResources().getString(R.string.enter_mobile_no));
            }
            else
            {
                et_telephone.setError(getResources().getString(R.string.mobile_error));
            }

            return;
        }

        if (et_comment.getText().toString().trim().length()==0)
        {
            et_comment.setError(getResources().getString(R.string.enter_comment));
            return;
        }
        SubmitDetails();
    }//SubmitForm
    public  boolean isValidateFirstName( String firstName )
    {
        if (firstName.trim().equals(""))
            return  false;
        else
            return firstName.matches("[a-zA-Z ]*");

    } // end method validateFirstName


    private boolean isValidMobile(String phone) {
        boolean check=false;
        if(!Pattern.matches("[a-zA-Z]+", phone)) {
            if(phone.length() < 9 || phone.length() > 15) {
                // if(phone.length() != 10) {
                check = false;
                et_telephone.setError("Not Valid Number");
            } else {
                check = true;
            }
        } else {
            check=false;
        }
        return check;
    }


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
            et_email.setError("Not Valid Email");
        }
        return check;
    }
    public void initView()
    {
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        iv_back=(ImageView)toolbar.findViewById(R.id.iv_back);
        iv_search=(ImageView)toolbar.findViewById(R.id.iv_search);
        iv_cart=(ImageView)toolbar.findViewById(R.id.iv_cart);
        tv_title=(TextView) toolbar.findViewById(R.id.tv_title);
        tv_cart_count=(TextView) toolbar.findViewById(R.id.tv_cart_count);
        et_name=(EditText)findViewById(R.id.et_name);
        et_email=(EditText)findViewById(R.id.et_email);
        et_telephone=(EditText)findViewById(R.id.et_telephone);
        et_comment=(EditText)findViewById(R.id.et_comment);
        btn_submit=(Button) findViewById(R.id.btn_submit);
        p_bar=(ProgressBar) findViewById(R.id.p_bar);
        tv_title.setText(getResources().getString(R.string.contact_us));
    }//initViewClose


    public void SubmitDetails()
    {
        p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.CONTACT_US,
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
                                String message=Jsonobj.getString("message");
                                CustomUtils.showOKAlertDialog(ContactUsActivity.this,getResources().getString(R.string.success),getResources().getString(R.string.request_submitted));
                                et_comment.setText("");
                                et_email.setText("");
                                et_name.setText("");
                                et_telephone.setText("");

                            }
                            else
                            {
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
                params.put("email", et_email.getText().toString().trim());
                params.put("name", et_name.getText().toString().trim());
                params.put("telephone", et_telephone.getText().toString().trim());
                params.put("comment", et_comment.getText().toString().trim());
                params.put("language",new SharedPrefManager(ContactUsActivity.this).getLanguage());

                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GMarketApplication.getInstance(ContactUsActivity.this).addToRequestQueue(stringRequest);


    }//submitDetailsClose
}
