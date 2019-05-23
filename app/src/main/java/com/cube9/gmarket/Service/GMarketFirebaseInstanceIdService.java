package com.cube9.gmarket.Service;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class GMarketFirebaseInstanceIdService  extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        //Displaying token on logcat
        Log.i("GCM ID",refreshedToken);

    }

}
