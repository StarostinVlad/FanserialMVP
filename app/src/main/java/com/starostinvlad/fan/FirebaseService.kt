package com.starostinvlad.fan

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.squareup.picasso.Picasso
import com.starostinvlad.fan.GsonModels.Episode
import com.starostinvlad.fan.VideoScreen.VideoActivity
import java.io.IOException
import java.util.*
import kotlin.Throws

class FirebaseService : FirebaseMessagingService() {
    private val TAG: String = javaClass.getSimpleName()
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = Random().nextInt()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotifiacationChannel(notificationManager)
        }
        var bitmap: Bitmap? = null
        try {
            if (remoteMessage.notification!!.imageUrl != null) bitmap = Picasso.get()
                    .load(remoteMessage.notification!!.imageUrl)
                    .transform(BlurTransformation(this))
                    .placeholder(R.color.colorPrimary)
                    .get()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val intent = Intent(this, VideoActivity::class.java)
        if (remoteMessage.data.size > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
            val episode = Episode()
            episode.name = remoteMessage.data["NAME"]
            episode.url = remoteMessage.data["HREF"]
            intent.putExtra(getString(R.string.episode_extra), episode)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(remoteMessage.notification!!.title)
                .setContentText(remoteMessage.notification!!.body)
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(bitmap)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()
        notificationManager.notify(notificationId, notification)
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun createNotifiacationChannel(notificationManager: NotificationManager) {
        val channelName = "my_channel"
        val channel = NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH)
        channel.lightColor = Color.GREEN
        channel.enableLights(true)
        channel.description = "description"
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        private const val CHANNEL_ID = "my_channel"
    }
}