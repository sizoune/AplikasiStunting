package com.kominfotabalong.simasganteng.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.kominfotabalong.simasganteng.R
import timber.log.Timber

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.notification?.let { notif ->
            Timber.tag("notifPayload").d("Message Notification Body: ${notif.body}")
            notif.body?.let { body ->
                showNotification(
                    notif.title ?: "SIMASGANTENG Notif",
                    body,
                    1,
                )
            }
        }
    }

    override fun onNewToken(token: String) {
        Timber.tag("FCMToken").d("Refreshed token: $token")

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val logoutWorkRequest: WorkRequest =
            OneTimeWorkRequestBuilder<LogoutWorker>()
                .setInputData(Data.Builder().putString(LogoutWorker.FCM_TOKEN, token).build())
                .setConstraints(constraints)
                .addTag("FCMWorker")
                .build()
        WorkManager
            .getInstance(this)
            .enqueue(logoutWorkRequest)
    }

    private fun showNotification(
        title: String,
        message: String,
        notifId: Int,
    ) {
        val CHANNEL_ID = getString(R.string.default_notification_channel_id)
        val CHANNEL_NAME = "simasganteng_channel"
        val notificationManagerCompat =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setSmallIcon(R.drawable.logo)
//            .setLargeIcon(generateBitmapFromVectorDrawable(this, R.drawable.ic_megaphone_notif))
            .setContentText(message)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
//            .setSound(alarmSound)
            .setAutoCancel(true)
//            .setContentIntent(resultPendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
//            val attribute = AudioAttributes.Builder()
//                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
//                .build()
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)
//            channel.setSound(alarmSound, attribute)
            builder.setChannelId(CHANNEL_ID)
            notificationManagerCompat.createNotificationChannel(channel)
        }

        val notification = builder.build()
        notificationManagerCompat.notify(notifId, notification)
    }
}