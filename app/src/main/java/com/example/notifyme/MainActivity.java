package com.example.notifyme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    public static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    public static final int NOTIFICATION_ID = 0;
    private static final String ACTION_UPDATE_NOTIFICATION =
            "com.example.android.notifyme.ACTION_UPDATE_NOTIFICATION";

    private NotificationManager notificationManager;
    Button btnCancel;
    Button btnUpdate;
    Button btnNotify;
    private NotificationReceiver notificationReceiver = new NotificationReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnNotify = findViewById(R.id.btnNotify);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnCancel = findViewById(R.id.btnCancel);

        createNotificationChannel();
        setNotificationButtonState(true, false, false);

        registerReceiver(notificationReceiver, new IntentFilter(ACTION_UPDATE_NOTIFICATION));

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(notificationReceiver);
        super.onDestroy();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnNotify:
                sendNotification();
                setNotificationButtonState(false, true, true);
                break;

            case R.id.btnUpdate:
                updateNotification();
                setNotificationButtonState(false, false, true);
                break;

            case R.id.btnCancel:
                cancelNotification();
                setNotificationButtonState(true, false, false);
                break;
        }
    }

    public void sendNotification() {
        Intent updateIntent = new Intent(ACTION_UPDATE_NOTIFICATION);
        PendingIntent updatePendingIntent = PendingIntent.getBroadcast
                (this, NOTIFICATION_ID, updateIntent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        notifyBuilder.addAction(R.drawable.ic_update, "Update Notification", updatePendingIntent);
        notificationManager.notify(NOTIFICATION_ID, notifyBuilder.build());

    }

    public void createNotificationChannel() {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID,
                    "Mascot Notification", NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification from Mascot");
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    public void updateNotification() {
        Bitmap androidImage = BitmapFactory.decodeResource(getResources(), R.drawable.mascot_1);
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        notifyBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(androidImage)
                .setBigContentTitle("Notification Updated!"));
        notificationManager.notify(NOTIFICATION_ID, notifyBuilder.build());


    }

    public void cancelNotification() {
        notificationManager.cancel(NOTIFICATION_ID);
    }

    private NotificationCompat.Builder getNotificationBuilder() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this,
                NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notifyBuilder =
                new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                        .setContentTitle("You've been notified!")
                        .setContentText("This is your notification text.")
                        .setSmallIcon(R.drawable.ic_android)
                        .setContentIntent(notificationPendingIntent)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setDefaults(NotificationCompat.DEFAULT_ALL);

        return notifyBuilder;
    }

    void setNotificationButtonState(
            Boolean isNotifyEnabled, Boolean isUpdateEnabled, Boolean isCancelEnabled) {

        btnNotify.setEnabled(isNotifyEnabled);
        btnUpdate.setEnabled(isUpdateEnabled);
        btnCancel.setEnabled(isCancelEnabled);
    }

    public class NotificationReceiver extends BroadcastReceiver {

        public NotificationReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            // Update the notification
            updateNotification();


        }
    }

}
