package com.wardcare.app.data.repository

import com.wardcare.app.data.local.MedicationDao
import com.wardcare.app.data.model.MedicationLog
import com.wardcare.app.data.remote.ApiService
import kotlinx.coroutines.flow.Flow

class MedicationRepository(
    private val medicationDao: MedicationDao,
    private val apiService: ApiService
) {
    val allLogs: Flow<List<MedicationLog>> = medicationDao.getAllLogs()
    val pendingCount: Flow<Int> = medicationDao.getPendingCount()

    suspend fun addMedicationLog(log: MedicationLog) {
        medicationDao.insertLog(log)
    }

    suspend fun syncPendingLogs() {
        val pendingLogs = medicationDao.getPendingLogs()
        if (pendingLogs.isEmpty()) return

        // Batch of up to 20 as per requirement
        pendingLogs.chunked(20).forEach { batch ->
            try {
                val response = apiService.syncMedicationLogs(batch)
                if (response.isSuccessful && response.body()?.success == true) {
                    val syncedUuids = response.body()?.data ?: emptyList()
                    syncedUuids.forEach { uuid ->
                        medicationDao.markAsSynced(uuid)
                    }
                }
            } catch (e: Exception) {
                // Network error, stay queued
            }
        }
    }
}
