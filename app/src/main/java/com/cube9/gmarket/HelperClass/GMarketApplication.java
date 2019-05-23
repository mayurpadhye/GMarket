package com.cube9.gmarket.HelperClass;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatDelegate;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.cube9.gmarket.R;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

@ReportsCrashes(formUri = "",
        mailTo = "mayurpadhye12@gmail.com",
        mode = ReportingInteractionMode.SILENT,
        resToastText = R.string.crash_toast_text)

public class GMarketApplication extends Application {
    private static GMarketApplication mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    @Override
    public void onCreate() {
        super.onCreate();

        //mInstance = this;
        ACRA.init(this);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

    }
public  GMarketApplication()
{}
    private GMarketApplication(Context context) {
        this.mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized GMarketApplication getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new GMarketApplication(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}

