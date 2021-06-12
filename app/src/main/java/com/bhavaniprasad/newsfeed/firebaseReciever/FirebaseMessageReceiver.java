package com.bhavaniprasad.newsfeed.firebaseReciever;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.bhavaniprasad.newsfeed.MainActivity;
import com.bhavaniprasad.newsfeed.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FirebaseMessageReceiver extends FirebaseMessagingService {
    private static final String TAG = FirebaseMessagingService.class.getSimpleName();
    int UNIQUE_INT_PER_CALL=0;
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("token", "Token: " + token);
    }

    /**
     * method to receive the incoming push notifications
     *
     * @param remoteMessage contains notification and data payload
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        //check if push notification has notification payload or not
        if (remoteMessage.getNotification() != null) {
            Map<String, String> data = remoteMessage.getData();
            String jobType = data.get("type");



            //get the title and body
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();

            Log.d(TAG, "Notification Title: " + title + " - Body: " + body);

            //show notification
            showNotification(title, body);
        }

        //check if push notification has data payload or not
        if(remoteMessage.getData().size()>0){
            for (Map.Entry<String, String> entry : remoteMessage.getData().entrySet()) {
                showNotification(entry.getKey(),entry.getValue());
            }
        }


    }

    /***
     *
     * @param title Notification title
     * @param message Notification message
     */
    public void showNotification(String title, String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Key"+UNIQUE_INT_PER_CALL, message);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("Key"+UNIQUE_INT_PER_CALL, message);  // Saving string

        String channel_id = "notification_channel";
        PendingIntent pendingIntent = PendingIntent.getActivity(this, UNIQUE_INT_PER_CALL, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        UNIQUE_INT_PER_CALL++;
        NotificationCompat.Builder builder
                = new NotificationCompat
                .Builder(getApplicationContext(),
                channel_id)
                .setSmallIcon(R.drawable.offlineimage)
                .setAutoCancel(true)
                .setVibrate(new long[]{1000, 1000, 1000,
                        1000, 1000})
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent);


        builder = builder.setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.drawable.border1);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channel_id, "web_app",NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        notificationManager.notify(UNIQUE_INT_PER_CALL, builder.build());
    }




}
