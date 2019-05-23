package com.cube9.gmarket.nointernet;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cube9.gmarket.HelperClass.CustomUtils;
import com.cube9.gmarket.R;
import com.cube9.gmarket.receivers.NetworkChangeReceiver;

public class NoInternetActivity extends AppCompatActivity {
Button btn_no_internet;
    NetworkChangeReceiver mNetworkReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);
        btn_no_internet=(Button)findViewById(R.id.btn_no_internet);
        mNetworkReceiver = new NetworkChangeReceiver();
        registerNetworkBroadcastForNougat();
        btn_no_internet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkChangeReceiver.isOnline(NoInternetActivity.this))
                {
                    finish();
                }
                else
                {
                    CustomUtils.showToast(getResources().getString(R.string.no_internet),NoInternetActivity.this);
                }
            }
        });
    }
    private void registerNetworkBroadcastForNougat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
           registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
          registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    protected void unregisterNetworkChanges() {
        try {
           unregisterReceiver(mNetworkReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterNetworkChanges();
    }

    @Override
    public void onBackPressed() {


        if (NetworkChangeReceiver.isOnline(NoInternetActivity.this))
        {
           finish();
        }
        else
        {
            Toast.makeText(this, getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();

            finishAffinity();
        }
    }

    public static void FinishTisActivity()
    {
      //  NoInternetActivity.finish();

    }
}
