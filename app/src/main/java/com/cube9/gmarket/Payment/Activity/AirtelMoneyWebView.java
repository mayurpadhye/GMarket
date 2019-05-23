package com.cube9.gmarket.Payment.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cube9.gmarket.HelperClass.CustomUtils;
import com.cube9.gmarket.HelperClass.SharedPrefManager;
import com.cube9.gmarket.Home.Activities.HomeActivity;
import com.cube9.gmarket.Orders.Activities.OrderPaymentFailedActivity;
import com.cube9.gmarket.Orders.Activities.OrderSuccessActivity;
import com.cube9.gmarket.R;
import com.cube9.gmarket.WebUrls.WebUrls;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class AirtelMoneyWebView extends AppCompatActivity {

    WebView webView;
    TextView tv_title,tv_cart_count;
    ImageView iv_back,iv_search,iv_cart;
Toolbar toolbar;
    String   order_id="";
    String language_id="1",paymentUrl=WebUrls.MAKE_PAYMENT,payCode="",postData="";
    ProgressBar progressBar;
AlertDialog dialog;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:{
                   webViewGoBack();
                }break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airtel_money_web_view);


        webView = findViewById(R.id.wv_payment);
        toolbar=findViewById(R.id.toolbar);
        iv_back=findViewById(R.id.iv_back);
        iv_search=findViewById(R.id.iv_search);
        iv_cart=findViewById(R.id.iv_cart);
        tv_title=findViewById(R.id.tv_title);
        tv_cart_count=findViewById(R.id.tv_cart_count);
        iv_cart.setVisibility(View.GONE);
        iv_search.setVisibility(View.GONE);
        tv_cart_count.setVisibility(View.GONE);
        tv_title.setText(getResources().getString(R.string.payment));
        language_id="1";


        postData="";
       // sharedPreferences = new CustomSharedPreferences(getActivity().getApplicationContext());

        webView.setWebViewClient(new CustomWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);

        CookieManager cookieManager = CookieManager.getInstance();
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {

            cookieManager.setAcceptThirdPartyCookies( webView, false );
            cookieManager.removeAllCookies(null);
            cookieManager.removeSessionCookies(null);
        }
        else
        {
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
        }

        webView.clearHistory();
        webView.clearCache(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setAppCacheEnabled(false);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setLoadWithOverviewMode(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setVerticalScrollBarEnabled(false);
        progressBar =findViewById(R.id.loading_progress);
       progressBar.setVisibility(View.GONE);
        webView.getSettings().setUseWideViewPort(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

      /*  webView.setOnKeyListener(new View.OnKeyListener(){

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_BACK:
                            if (webView.canGoBack()) {
                                webView.goBack();
                            } else {


                            }
                            return true;
                    }

                }
                return super.onKeyDown(keyCode, event);



            }

        });*/

       iv_back .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomUtils.hideKeyboard(v, getApplicationContext());
              showAlertMessageDialogLeave();
            }
        });

        Intent i=getIntent();

        order_id=i.getStringExtra("order_id");
     String   amount=i.getStringExtra("amount");
     String   user_id=new SharedPrefManager(AirtelMoneyWebView.this).GetCustomerId();
        try {
            postData = "order_id=" + URLEncoder.encode(order_id, "UTF-8")+ "&amount=" + URLEncoder.encode(amount, "UTF-8")+ "&user_id=" + URLEncoder.encode(user_id, "UTF-8");;
            Log.i("LoadData",postData);
            webView.postUrl(paymentUrl, postData.getBytes());

            Log.i("POSTURL",postData);
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                      //  CustomUtils.hideKeyboard(v, AirtelMoneyWebView.this);
                        showAlertMessageDialogLeave();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }
    private class CustomWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
          //  if (Uri.parse(url).getHost().equals(url)) {
                if (url.contains(WebUrls.PAYMENT_SUCCESS)) {
                    view.loadUrl(url);
                   startActivity(new Intent(AirtelMoneyWebView.this, OrderSuccessActivity.class));
                   finish();
                } else if (url.contains(WebUrls.PAYMENT_FAILURE)) {
                    view.loadUrl(url);
                    startActivity(new Intent(AirtelMoneyWebView.this, OrderPaymentFailedActivity.class));
                    finish();
                } else {
                    view.loadUrl(url);
                }

                view.loadUrl(url);
                return true;

        }

}

    private void webViewGoBack(){
        webView.goBack();
    }

    public void showAlertMessageDialogLeave() {

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_2_button, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(dialogView);

        TextView tv_message = dialogView.findViewById(R.id.txt_message);
        tv_message.setText(getString(R.string.payment_cancel));
        Button button_save = dialogView.findViewById(R.id.btn_ok);
        button_save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                CustomUtils.hideKeyboard(view, getApplicationContext());

                Intent i=new Intent(AirtelMoneyWebView.this,HomeActivity.class);
                startActivity(i);
                finishAffinity();
                dialog.dismiss();
            }
        });

        Button button_cancel = dialogView.findViewById(R.id.btn_cancel);
        button_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                CustomUtils.hideKeyboard(view, getApplicationContext());
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);
    }
    }
