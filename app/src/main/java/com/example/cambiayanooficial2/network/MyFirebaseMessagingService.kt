package com.example.cambiayanooficial2.network

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.cambiayanooficial2.R
import com.example.cambiayanooficial2.ui.main.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

const val channelId = "notifications_channel"
const val channelName = "com.example.cambiayanooficial2"

class MyFirebaseMessagingService : FirebaseMessagingService() {

    // Método que se invoca cuando llega una notificación push
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Si la notificación contiene una notificación (título y cuerpo)
        remoteMessage.notification?.let {
            // Generar y mostrar la notificación en la barra de estado
            generateNotification(it.title ?: "Notificación", it.body ?: "Mensaje de notificación")
        }

        // Si la notificación contiene datos adicionales
        remoteMessage.data.isNotEmpty().let {
            val customData = remoteMessage.data["customKey"]
            // Aquí puedes hacer algo con los datos personalizados
        }
    }

    @SuppressLint("RemoteViewLayout")
    private fun getRemoteView(title: String, message: String): RemoteViews {
        val remoteView = RemoteViews(packageName, R.layout.notification)

        remoteView.setTextViewText(R.id.title, title)
        remoteView.setTextViewText(R.id.message, message)
        remoteView.setImageViewResource(R.id.app_logo, R.drawable.ic_notifications)

        return remoteView
    }

    // Método para generar la notificación y mostrarla
    private fun generateNotification(title: String, message: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        // Configuración de la notificación
        val builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_notifications)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .setContent(getRemoteView(title, message))

        // Obtener el NotificationManager
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear el canal de notificación para Android 8.0 y superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        // Mostrar la notificación
        notificationManager.notify(0, builder.build())
    }

    // Método invocado cuando se genera un nuevo token de FCM
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Aquí puedes enviar el token al servidor para almacenarlo
        sendTokenToServer(token)
    }

    // Método para enviar el token al servidor
    private fun sendTokenToServer(token: String) {
        // Aquí puedes hacer una solicitud HTTP para enviar el token al backend
    }
}
