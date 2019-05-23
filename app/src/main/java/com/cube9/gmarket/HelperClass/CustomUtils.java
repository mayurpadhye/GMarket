package com.cube9.gmarket.HelperClass;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.cube9.gmarket.R;

public class CustomUtils {


    public static final String ENGLISH="1";
    public static final String ENGLISH_CODE="en";
    public static final String FRENCH="2";
    public static final String FRENCH_CODE="fr";


    public static void showToast(String msg, Context context){

        Toast toast= Toast.makeText(context,msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }//showToastClose

    public static boolean isNetworkAvailable(Context context) {
        if(context != null) {

            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netinfo = cm.getActiveNetworkInfo();

            if (netinfo != null && netinfo.isConnectedOrConnecting()) {
                NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

                return (mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting());
            } else
                return false;
        } else return false;
    }

    public static void hideKeyboard(View view, Context context) {
        InputMethodManager inputMethodManager =(InputMethodManager)context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    public static void showOKAlertDialog(Context context,String title,String message)
    {
        android.support.v7.app.AlertDialog.Builder dialogBuilder = new android.support.v7.app.AlertDialog.Builder(context,R.style.AppCompatAlertDialogStyle);
        dialogBuilder.setTitle(title);
        dialogBuilder.setCancelable(false);


        dialogBuilder.setMessage(message);
        dialogBuilder.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();

            }
        });
        final android.support.v7.app.AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }
    public static void openAlertDialog(Context context,String messgae)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(messgae)
                .setCancelable(false)
                .setPositiveButton(context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();

                    }
                });

        AlertDialog alert = builder.create();
        alert.setTitle(context.getResources().getString(R.string.success));
        alert.show();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
    }//
}
