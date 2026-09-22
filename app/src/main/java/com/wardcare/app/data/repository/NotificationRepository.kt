package com.wardcare.app.data.repository

import com.wardcare.app.data.local.NotificationDao
import com.wardcare.app.data.model.AppNotification
import kotlinx.coroutines.flow.Flow

class NotificationRepository(private val notificationDao: NotificationDao) {

    val allNotifications: Flow<List<AppNotification>> = notificationDao.getAllNotifications()

    suspend fun addNotification(notification: AppNotification) {
        notificationDao.insertNotification(notification)
    }
}
