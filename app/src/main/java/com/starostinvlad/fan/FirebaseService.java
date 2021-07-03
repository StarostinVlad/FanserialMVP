package com.starostinvlad.fan;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;
import com.starostinvlad.fan.GsonModels.Episode;
import com.starostinvlad.fan.VideoScreen.VideoActivity;

import java.io.IOException;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class FirebaseService extends FirebaseMessagingService {
    private static final String CHANNEL_ID = "my_channel";
    private final String TAG = getClass().getSimpleName();

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationId = new Random().nextInt();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotifiacationChannel(notificationManager);
        }

        Bitmap bitmap = null;
        try {
            if (remoteMessage.getNotification().getImageUrl() != null)
                bitmap = Picasso.get()
                        .load(remoteMessage.getNotification().getImageUrl())
                        .transform(new BlurTransformation(this))
                        .placeholder(R.color.colorPrimary)
                        .get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(this, VideoActivity.class);
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            Episode episode = new Episode();
            episode.setName(remoteMessage.getData().get("NAME"));
            episode.setUrl(remoteMessage.getData().get("HREF"));
            intent.putExtra(getString(R.string.episode_extra), episode);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(bitmap)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();

        notificationManager.notify(notificationId, notification);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void createNotifiacationChannel(NotificationManager notificationManager) {
        String channelName = "my_channel";
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH);
        channel.setLightColor(Color.GREEN);
        channel.enableLights(true);
        channel.setDescription("description");
        notificationManager.createNotificationChannel(channel);

    }
}
