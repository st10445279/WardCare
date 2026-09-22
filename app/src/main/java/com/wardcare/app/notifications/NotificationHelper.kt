package com.wardcare.app.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.wardcare.app.R
import com.wardcare.app.data.local.AppDatabase
import com.wardcare.app.data.model.AppNotification
import com.wardcare.app.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object NotificationHelper {

    private const val CHANNEL_ID = "wardcare_reminders_channel"
    private const val CHANNEL_NAME = "WardCare Reminders"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders for medication due and patient appointments"
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendMedicationDueReminder(
        context: Context,
        patientName: String,
        bedNumber: String,
        medicationName: String
    ) {
        createNotificationChannel(context)

        val title = "Dose overdue: $bedNumber, $medicationName"
        val message = "Medication $medicationName is due now for $patientName at $bedNumber."

        showSystemNotification(context, title, message, 101)
        saveNotificationToDb(context, title, "overdue")
    }

    fun sendAppointmentNotification(
        context: Context,
        patientName: String,
        bedNumber: String,
        appointmentTime: String
    ) {
        createNotificationChannel(context)

        val title = "Appointment in 15 min: $bedNumber"
        val message = "Upcoming appointment at $appointmentTime for $patientName ($bedNumber)."

        showSystemNotification(context, title, message, 102)
        saveNotificationToDb(context, title, "appointment")
    }

    fun sendHandoverNotification(context: Context, summaryText: String = "Shift handover summary ready") {
        createNotificationChannel(context)

        val title = summaryText
        val message = "Shift handover summary for your ward is ready for review."

        showSystemNotification(context, title, message, 103)
        saveNotificationToDb(context, title, "handover")
    }

    private fun showSystemNotification(
        context: Context,
        title: String,
        message: String,
        notificationId: Int
    ) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_nav_notifications)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, builder.build())
    }

    private fun saveNotificationToDb(context: Context, title: String, type: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val database = AppDatabase.getDatabase(context)
            database.notificationDao().insertNotification(
                AppNotification(
                    title = title,
                    type = type
                )
            )
        }
    }
}
