package com.rent24.driver.components.job.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.rent24.driver.R
import com.rent24.driver.components.home.HomeActivity

private val TAG = JobActivateWorker::class.java.name
private const val CHANNEL_ID = "Job Notification"

class JobActivateWorker(private val context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        Log.d(TAG, "Worker started")
        createNotification()
        Log.d(TAG, "Worker finished")
        return Result.success()
    }

    private fun createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(
                    NotificationChannel(
                        CHANNEL_ID, "Job Notification Service",
                        NotificationManager.IMPORTANCE_HIGH).apply {
                        description = "Rent24 Job Alert"
                    })
        }

        with(NotificationManagerCompat.from(context)) {
            notify(0, NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_toolbar_logo)
                .setContentTitle("Job Activated")
                .setContentText(inputData.getString("message"))
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_STATUS)
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(context, 0,
                    Intent(context, HomeActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        putExtra("notification", 12312)
                        putExtra("type", inputData.getInt("type", -1))
                        putExtra("pickup", DoubleArray(2).apply {
                            val data = inputData.keyValueMap["pickup"] as Array<*>
                            this[0] = data[0] as Double
                            this[1] = data[1] as Double
                        })
                        action = Intent.ACTION_MAIN
                        addCategory(Intent.CATEGORY_LAUNCHER)
                    }, PendingIntent.FLAG_UPDATE_CURRENT)).build())
        }
    }
}