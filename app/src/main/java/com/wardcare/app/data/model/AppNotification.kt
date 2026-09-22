package com.wardcare.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class AppNotification(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val subtitle: String? = null,
    val type: String = "info", // overdue, appointment, handover, info
    val timestamp: Long = System.currentTimeMillis()
)
