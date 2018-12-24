package com.taxialeairy.provider.FCM;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.taxialeairy.provider.Activity.MainActivity;
import com.taxialeairy.provider.Utilities.Utilities;

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    private static final String TAG = "MyFirebaseMsgService";
    Utilities utils = new Utilities();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData() != null) {
            utils.print(TAG, "From: " + remoteMessage.getFrom());
            utils.print(TAG, "Notification Message Body: " + remoteMessage.getData());
            //Calling method to generate notification
            sendNotification(remoteMessage.getData().get("message"));
        } else {
            utils.print(TAG, "FCM Notification failed");
        }
    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(String messageBody) {

        if (!Utilities.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            utils.print(TAG, "foreground");
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("push", true);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);


            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                AudioAttributes attributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build();

                String CHANNEL_ID = "my_channel_02";
                CharSequence name = "my_channel";
                String Description = "This is my channel";
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                mChannel.setDescription(Description);
                mChannel.enableLights(true);
                mChannel.setLightColor(Color.RED);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                mChannel.setShowBadge(false);
                mChannel.setSound(Settings.System.DEFAULT_NOTIFICATION_URI, attributes);
                notificationManager.createNotificationChannel(mChannel);
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "my_channel_02")
                    .setContentTitle(getString(com.taxialeairy.provider.R.string.app_name))
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setContentIntent(pendingIntent);
            builder.setSmallIcon(getNotificationIcon(builder), 1);

            notificationManager.notify(0, builder.build());


        } else {
            utils.print(TAG, "background");
            // app is in background, show the notification in notification tray
            if (messageBody.equalsIgnoreCase("New Incoming Ride")) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                        PendingIntent.FLAG_ONE_SHOT);


                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                    AudioAttributes attributes = new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                            .build();
                    String CHANNEL_ID = "my_channel_03";
                    CharSequence name = "my_channel";
                    String Description = "This is my channel";
                    int importance = NotificationManager.IMPORTANCE_HIGH;
                    NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                    mChannel.setDescription(Description);
                    mChannel.enableLights(true);
                    mChannel.setLightColor(Color.RED);
                    mChannel.setShowBadge(false);
                    mChannel.enableVibration(true);
                    mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                    mChannel.setSound(Settings.System.DEFAULT_NOTIFICATION_URI, attributes);
                    notificationManager.createNotificationChannel(mChannel);
                }

                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "my_channel_03")
                        .setContentTitle(getString(com.taxialeairy.provider.R.string.app_name))
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                        //.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + com.taxialeairy.provider.R.raw.alert_tone))
                        .setContentIntent(pendingIntent);
                builder.setSmallIcon(getNotificationIcon(builder), 1);

                notificationManager.notify(0, builder.build());
            } else {
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("push", true);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                        PendingIntent.FLAG_ONE_SHOT);


//                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
//                        .setContentTitle(getString(com.taxialeairy.provider.R.string.app_name))
//                        .setContentText(messageBody)
//                        .setAutoCancel(true)
//                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
//                        .setContentIntent(pendingIntent);

//                notificationBuilder.setSmallIcon(getNotificationIcon(notificationBuilder), 1);


                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    AudioAttributes attributes = new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                            .build();

                    String CHANNEL_ID = "my_channel_03";
                    CharSequence name = "my_channel";
                    String Description = "This is my channel";
                    int importance = NotificationManager.IMPORTANCE_HIGH;
                    NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                    mChannel.setDescription(Description);
                    mChannel.enableLights(true);
                    mChannel.setLightColor(Color.RED);
                    mChannel.enableVibration(true);
                    mChannel.setShowBadge(false);
                    mChannel.setSound(Settings.System.DEFAULT_NOTIFICATION_URI, attributes);
                    notificationManager.createNotificationChannel(mChannel);
                }

                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "my_channel_03")
                        .setContentTitle(getString(com.taxialeairy.provider.R.string.app_name))
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                        .setContentIntent(pendingIntent);
                builder.setSmallIcon(getNotificationIcon(builder), 1);

                notificationManager.notify(0, builder.build());
            }
        }


    }

    private int getNotificationIcon(NotificationCompat.Builder notificationBuilder) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setColor(ContextCompat.getColor(getApplicationContext(), com.taxialeairy.provider.R.color.colorPrimary));
            return com.taxialeairy.provider.R.drawable.notification_white;
        } else {
            return com.taxialeairy.provider.R.mipmap.ic_launcher;
        }
    }

}
