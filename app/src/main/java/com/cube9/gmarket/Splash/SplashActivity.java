package com.cube9.gmarket.Splash;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.cube9.gmarket.HelperClass.CustomUtils;
import com.cube9.gmarket.HelperClass.SharedPrefManager;
import com.cube9.gmarket.Home.Activities.HomeActivity;
import com.cube9.gmarket.Login.Activity.LoginDetailsActivity;
import com.cube9.gmarket.R;
import com.cube9.gmarket.nointernet.NoInternetActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class SplashActivity extends AppCompatActivity {
    Locale LOCALE;
    Configuration CONFIG;
    Locale locale;
    private static int SPLASH_TIME_OUT = 3000;
    SharedPrefManager sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ImageView iv_splash=findViewById(R.id.iv_splash);
        loadAppInLanguage();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getHash();

       // loadAppInLanguage();
    }

    public void getHash() {

        MessageDigest md = null;
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash = ", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }


    }



    public void loadAppInLanguage() {

        sharedPreferences = new SharedPrefManager(SplashActivity.this);
        if(sharedPreferences.getLanguage().isEmpty()) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                String languageToLoad  = CustomUtils.FRENCH_CODE; // your language
                Locale locale = new Locale(languageToLoad);
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getBaseContext().getResources().updateConfiguration(config,
                        getBaseContext().getResources().getDisplayMetrics());
                sharedPreferences.setLanguage(CustomUtils.FRENCH);
                sharedPreferences.setAppLanguageCode(CustomUtils.FRENCH_CODE);
                LOCALE = new Locale(CustomUtils.FRENCH_CODE);
                Locale.setDefault(LOCALE);
                CONFIG = new Configuration();
                CONFIG.locale = LOCALE;
                CONFIG.setLocale(LOCALE);
                getResources().updateConfiguration(CONFIG, getResources().getDisplayMetrics());

            } else {
                String languageToLoad  = CustomUtils.FRENCH_CODE; // your language
                Locale locale = new Locale(languageToLoad);
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getBaseContext().getResources().updateConfiguration(config,
                        getBaseContext().getResources().getDisplayMetrics());
                //noinspection deprecation
                locale = Resources.getSystem().getConfiguration().locale;
                sharedPreferences.setLanguage(CustomUtils.FRENCH);
                sharedPreferences.setAppLanguageCode(CustomUtils.FRENCH);
                LOCALE = new Locale(CustomUtils.FRENCH_CODE);
                Locale.setDefault(LOCALE);
                CONFIG = new Configuration();
                CONFIG.locale = LOCALE;
                CONFIG.setLocale(LOCALE);
                getResources().updateConfiguration(CONFIG, getResources().getDisplayMetrics());
            }
        }
        else
        {
         /*   LOCALE = new Locale(sharedPreferences.getAppLanguage());
            Locale.setDefault(LOCALE);
            CONFIG = new Configuration();
            CONFIG.locale = LOCALE;
            CONFIG.setLocale(LOCALE);
            getResources().updateConfiguration(CONFIG, getResources().getDisplayMetrics());*/


            LOCALE = new Locale(sharedPreferences.getAppLanguageCode());
            Locale.setDefault(LOCALE);
            CONFIG = new Configuration();
            CONFIG.locale = LOCALE;
            CONFIG.setLocale(LOCALE);
            getApplicationContext().createConfigurationContext(CONFIG);
            getResources().updateConfiguration(CONFIG,getResources().getDisplayMetrics());
            onConfigurationChanged(CONFIG);
        }

        if(CustomUtils.isNetworkAvailable(this)) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {

                    if (new SharedPrefManager(SplashActivity.this).IsLogin()) {
                        Intent i = new Intent(SplashActivity.this, HomeActivity.class);
                        startActivity(i);
                        finish();
                    }
                    else
                    {
                        Intent i = new Intent(SplashActivity.this, LoginDetailsActivity.class);
                        startActivity(i);
                        finish();
                    }
                }
            }, SPLASH_TIME_OUT);

        }
        else {
           startActivity(new Intent(SplashActivity.this, NoInternetActivity.class));

        }
    }
}
