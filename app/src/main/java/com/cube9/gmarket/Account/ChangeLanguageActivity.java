package com.cube9.gmarket.Account;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.cube9.gmarket.HelperClass.CustomUtils;
import com.cube9.gmarket.HelperClass.SharedPrefManager;
import com.cube9.gmarket.R;

import java.util.Locale;

public class ChangeLanguageActivity extends AppCompatActivity {
    AppCompatSpinner spinner;
    SharedPrefManager sharedPreferences;
    Locale LOCALE;
    Configuration CONFIG;
    boolean start = true;
    Toolbar toolbar;
    ImageView iv_back,iv_search,iv_cart;
    TextView tv_title,tv_cart_count,tv_select_lang;
    @Override
    protected void onResume() {
        super.onResume();
        tv_cart_count.setVisibility(View.GONE);
        iv_cart.setVisibility(View.GONE);
        iv_search.setVisibility(View.GONE);
        tv_select_lang.setText(getResources().getString(R.string.select_language));
        tv_title.setText(getResources().getString(R.string.change_language));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_language);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        iv_back = (ImageView) toolbar.findViewById(R.id.iv_back);
        iv_search = (ImageView) toolbar.findViewById(R.id.iv_search);
        iv_cart = (ImageView) toolbar.findViewById(R.id.iv_cart);
        tv_title = (TextView) toolbar.findViewById(R.id.tv_title);
        tv_cart_count = (TextView) toolbar.findViewById(R.id.tv_cart_count);

        tv_select_lang=(TextView)findViewById(R.id.tv_select_lang);
        start = true;
        sharedPreferences = new SharedPrefManager(this);
        String language = sharedPreferences.getAppLanguageCode();
        if(language.isEmpty())
            language= CustomUtils.FRENCH_CODE;

        String languageToLoad  =language; // your language
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());



        sharedPreferences = new SharedPrefManager(getApplicationContext());

        //CustomUtils.showLog("LANGUAGE ",sharedPreferences.getAppLanguage());
      iv_back.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              finish();
          }
      });

        spinner = findViewById(R.id.spinner_language);


       if (sharedPreferences.getLanguage().equals(CustomUtils.FRENCH)) {
            spinner.setSelected(true);
            spinner.setSelection(1);
        } else {
            spinner.setSelected(true);
            spinner.setSelection(0);
        }
        Log.i("strtValue",""+start);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

                if (!start) {
                    switch (position) {
                        case 0:

                            sharedPreferences.setLanguage(CustomUtils.ENGLISH);
                            sharedPreferences.setAppLanguageCode(CustomUtils.ENGLISH_CODE);
                            LOCALE = new Locale(CustomUtils.ENGLISH);
                            Locale.setDefault(LOCALE);
                            CONFIG = new Configuration();
                            CONFIG.locale = LOCALE;
                            CONFIG.setLocale(LOCALE);
                            getApplicationContext().createConfigurationContext(CONFIG);
                            getResources().updateConfiguration(CONFIG, getResources().getDisplayMetrics());
                            onConfigurationChanged(CONFIG);
                           /* start=true;
                            recreate();*/

                            Intent intent = getIntent();
                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            finish();
                            startActivity(intent);
                            break;

                        case 1:
                            sharedPreferences.setLanguage(CustomUtils.FRENCH);
                            sharedPreferences.setAppLanguageCode(CustomUtils.FRENCH_CODE);
                            LOCALE = new Locale(CustomUtils.FRENCH);
                            Locale.setDefault(LOCALE);
                            CONFIG = new Configuration();
                            CONFIG.locale = LOCALE;
                            CONFIG.setLocale(LOCALE);
                            getApplicationContext().createConfigurationContext(CONFIG);
                            getResources().updateConfiguration(CONFIG, getResources().getDisplayMetrics());
                            onConfigurationChanged(CONFIG);
                           /* start=true;
                           recreate();*/
                            Intent intent1 = getIntent();
                            intent1.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            finish();
                            startActivity(intent1);
                            break;
                    }
                } else {
                    start = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
