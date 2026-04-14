package com.findmeahometeam.reskiume.data.util.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.findmeahometeam.reskiume.MainActivity
import com.findmeahometeam.reskiume.R
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.ui.util.fcm.MessagingServiceViewModel
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.koin.android.ext.android.inject

class MessagingService() : FirebaseMessagingService() {

    private val rescueEventsNotificationType = "rescueEvent"
    private val rescueEventChannelId = "rescue_events"

    private val messagingServiceViewModel: MessagingServiceViewModel by inject()
    private val log: Log by inject()

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        log.d("MessagingService", "onNewToken: $token")
    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()
        log.d("MessagingService", "onDeletedMessages called")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        messagingServiceViewModel.retrieveActivistId { activistId ->

            if (activistId.isBlank()) {
                log.d(
                    "MessagingService",
                    "onMessageReceived: No activist ID found, skipping notification"
                )
                return@retrieveActivistId
            }

            val notificationType = message.data["notificationType"] ?: ""

            if (notificationType == rescueEventsNotificationType) {

                val creatorId = message.data["creatorId"] ?: ""
                if (creatorId == activistId) {
                    log.d(
                        "MessagingService",
                        "onMessageReceived: Creator ID match with activist ID, skipping notification"
                    )
                    return@retrieveActivistId
                }
                val title = getString(R.string.notification_rescue_event_title)
                val body = getString(R.string.notification_rescue_event_body)
                val deeplink = message.data["deeplink"] ?: ""
                val channelName = getString(R.string.notification_rescue_event_channel)

                sendNotification(
                    title = title,
                    body = body,
                    deeplink = deeplink,
                    channelId = rescueEventChannelId,
                    channelName = channelName
                )
            }
        }
    }

    private fun sendNotification(
        title: String,
        body: String,
        deeplink: String,
        channelId: String,
        channelName: String
    ) {
        val requestCode = 0
        val intent = if (deeplink.isBlank()) {
            Intent(this, MainActivity::class.java)
        } else {
            Intent(Intent.ACTION_VIEW, deeplink.toUri())
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.reskiume)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT,
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationId = 0
        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}
