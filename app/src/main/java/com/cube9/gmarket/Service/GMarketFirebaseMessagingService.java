package com.cube9.gmarket.Service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.cube9.gmarket.HelperClass.SharedPrefManager;
import com.cube9.gmarket.Home.Activities.HomeActivity;
import com.cube9.gmarket.Login.Activity.LoginDetailsActivity;
import com.cube9.gmarket.MainActivity;
import com.cube9.gmarket.R;
import com.cube9.gmarket.Splash.SplashActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;

public class GMarketFirebaseMessagingService extends FirebaseMessagingService{
    SharedPrefManager sharedPreferences;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        sharedPreferences = new SharedPrefManager(getApplicationContext());

        Log.i("Notification Body", remoteMessage.getNotification().getBody()+"");
        if(remoteMessage.getNotification()!=null) {
            Log.i("Notification Title ", remoteMessage.getNotification().getTitle());
            Log.i("Notification Body", remoteMessage.getNotification().getBody());
        }
        if(remoteMessage.getData()!=null) {
            Log.i("Notification Data", remoteMessage.getData().toString());//+sharedPreferences.getIsLogin()

            sendNotification(remoteMessage.getNotification().getTitle(),
                    remoteMessage.getNotification().getBody(),
                    remoteMessage.getData(),
                    Integer.parseInt(remoteMessage.getData().get("flag")));
        }
    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(String title, String messageBody, Map<String,String> data, int flag) {
        Intent intent;
        NotificationManager notificationManager;
        PendingIntent pendingIntent;
        Uri defaultSoundUri;
        NotificationCompat.Builder notificationBuilder;

        switch (flag) {
            case 0: // Account Blocked
                if (isAppInForeground() && sharedPreferences.IsLogin())
                {
                    sharedPreferences.SetisLogin(false);

                    HomeActivity.getInstance().finishAffinity();
                    startActivity(new Intent(getApplicationContext(), LoginDetailsActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
                else {
                    sharedPreferences.SetisLogin(false);
                    intent = new Intent(this, SplashActivity.class);
                    intent.putExtra("flag", data.get("flag"));
                    intent.putExtra("message", data.get("msg"));
                    pendingIntent = PendingIntent.getActivity(this, 0, intent,
                            PendingIntent.FLAG_ONE_SHOT);

                    defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    notificationBuilder = new NotificationCompat.Builder(this)
                            .setContentTitle(title)
                            .setContentText(messageBody)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setContentIntent(pendingIntent);
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        notificationBuilder.setSmallIcon(R.drawable.logo);
                        notificationBuilder.setColor(ContextCompat.getColor(this, R.color.colorPrimary));
                    } else {
                        notificationBuilder.setSmallIcon(R.drawable.logo);
                    }
                    notificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(0, notificationBuilder.build());
                }
                break;

            case 1: // Approved By Admin
                if(sharedPreferences.IsLogin()) {
                    intent = new Intent(this, Notification.class);
                    intent.putExtra("flag", data.get("flag"));
                    intent.putExtra("message", data.get("msg"));
                    pendingIntent = PendingIntent.getActivity(this, 0, intent,
                            PendingIntent.FLAG_ONE_SHOT);

                    defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    notificationBuilder = new NotificationCompat.Builder(this)
                            .setContentTitle(title)
                            .setContentText(messageBody)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setContentIntent(pendingIntent);
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        notificationBuilder.setSmallIcon(R.drawable.logo);
                        notificationBuilder.setColor(ContextCompat.getColor(this, R.color.colorPrimary));
                    } else {
                        notificationBuilder.setSmallIcon(R.drawable.logo);
                    }
                    notificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(0, notificationBuilder.build());
                }
                else
                {
                    intent = new Intent(this, SplashActivity.class);
                    intent.putExtra("flag", data.get("flag"));
                    intent.putExtra("message", data.get("msg"));
                    pendingIntent = PendingIntent.getActivity(this, 0, intent,
                            PendingIntent.FLAG_ONE_SHOT);

                    defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    notificationBuilder = new NotificationCompat.Builder(this)
                            .setContentTitle(title)
                            .setContentText(messageBody)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setContentIntent(pendingIntent);
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        notificationBuilder.setSmallIcon(R.drawable.logo);
                        notificationBuilder.setColor(ContextCompat.getColor(this, R.color.colorPrimary));
                    } else {
                        notificationBuilder.setSmallIcon(R.drawable.logo);
                    }
                    notificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(0, notificationBuilder.build());
                }
                break;

            case 2: // promo code notification
                if(sharedPreferences.IsLogin()) {
                    intent = new Intent(this, MainActivity.class);
                    intent.putExtra("flag", data.get("flag"));
                    intent.putExtra("message", data.get("msg"));
                    intent.putExtra("offer_name", data.get("offer_name"));
                    intent.putExtra("expiry_date", data.get("expiry_date"));
                    intent.putExtra("code", data.get("code"));
                    pendingIntent = PendingIntent.getActivity(this, 0, intent,
                            PendingIntent.FLAG_ONE_SHOT);

                    defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    notificationBuilder = new NotificationCompat.Builder(this)
                            .setContentTitle(title)
                            .setContentText(messageBody)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setContentIntent(pendingIntent);
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        notificationBuilder.setSmallIcon(R.drawable.logo);
                        notificationBuilder.setColor(ContextCompat.getColor(this, R.color.colorPrimary));
                    } else {
                        notificationBuilder.setSmallIcon(R.drawable.logo);
                    }
                    notificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(0, notificationBuilder.build());
                }

                break;

        }
    }

    public boolean isAppInForeground() {
        ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(appProcessInfo);
        return (appProcessInfo.importance == IMPORTANCE_FOREGROUND || appProcessInfo.importance == IMPORTANCE_VISIBLE);
    }
}
