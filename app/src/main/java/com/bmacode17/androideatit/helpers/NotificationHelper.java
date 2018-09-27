package com.bmacode17.androideatit.helpers;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

import com.bmacode17.androideatit.R;

/**
 * Created by User on 26-Sep-18.
 */

public class NotificationHelper extends ContextWrapper {
    
    private static final String BASEL_CHANNEL_ID = "com.bmacode17.androideatit.Basel";
    private static final String BASEL_CHANNEL_NAME = "Eat It";
    
    private NotificationManager manager;
    
    public NotificationHelper(Context base) {
        super(base);
        
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)  // works only if API is 26 or higher (OREO Version)
            createChannel();
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {

        NotificationChannel baselChanel = new NotificationChannel(BASEL_CHANNEL_ID,
                BASEL_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        baselChanel.enableLights(false);
        baselChanel.enableVibration(true);
        baselChanel.setLockscreenVisibility(android.app.Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(baselChanel);

    }

    public NotificationManager getManager() {

        if(manager == null)
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public android.app.Notification.Builder getEatItChannelNotification(String title, String body, PendingIntent contentIntent,
                                                                        Uri soundUri){
        return new android.app.Notification.Builder(getApplicationContext(),BASEL_CHANNEL_ID)
                .setContentIntent(contentIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_shopping_cart_black_24dp)
                .setSound(soundUri)
                .setAutoCancel(false);
    }
}
