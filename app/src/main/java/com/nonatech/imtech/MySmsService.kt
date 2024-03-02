package com.nonatech.imtech


import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*

@SuppressLint("UseCompatLoadingForDrawables")
class MyService : Service() {
    private companion object {
        // Declare a variable to store the job
        private var incrementJob: Job? = null
        const val CHANNEL_ID = "ForegroundServiceChannel"
        const val NOTIFICATION_ID = 1
    }

    private val notificationManager by lazy {
        getSystemService(NotificationManager::class.java)
    }

    private val notificationBuilder by lazy {
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Zain BH")
            .setContentText("Your Request is in process. Please wait...")
            .setSmallIcon(R.drawable.app_logo)
    }

    private var count = 0

    override fun onCreate() {
        Log.i("TAG", "onCreate:  from service")
        super.onCreate()




//        showNotification(this)


//        updateNotification()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

//        startIncrementing()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, notificationBuilder.build())
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopIncrementing()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(serviceChannel)
        }
    }

    private fun stopIncrementing() {
        // Cancel the Coroutine to stop incrementing
        incrementJob?.cancel()
    }

    private fun updateNotification() {
        notificationBuilder.setContentText(count.toString())
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }
}
