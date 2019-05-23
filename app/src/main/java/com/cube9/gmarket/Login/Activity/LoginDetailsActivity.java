package com.cube9.gmarket.Login.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;
import com.cube9.gmarket.Login.Adapter.LoginViewPagerAdapter;
import com.cube9.gmarket.Login.Fragments.SignInFragment;
import com.cube9.gmarket.Login.Fragments.SignUpFragment;
import com.cube9.gmarket.R;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import io.fabric.sdk.android.Fabric;

public class LoginDetailsActivity extends AppCompatActivity {
    ViewPager login_viewpager;
    TabLayout login_tabs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_login_details);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);


        initView();
    }
    public void initView()
    {
        login_viewpager=(ViewPager)findViewById(R.id.login_viewpager);
        login_tabs=(TabLayout)findViewById(R.id.login_tabs);

        LoginViewPagerAdapter adapter = new LoginViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new SignInFragment(), getResources().getString(R.string.login));
        adapter.addFragment(new SignUpFragment(), getResources().getString(R.string.sign_up));
        login_viewpager.setAdapter(adapter);
        login_tabs.setupWithViewPager(login_viewpager);
    }//initView Close

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            //System.out.println("@#@");
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

}
