package com.wardcare.app.data.remote

import com.wardcare.app.data.model.MedicationLog
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class SyncResponse(
    val success: Boolean,
    val data: List<String>?, // List of successfully synced UUIDs
    val error: String?
)

interface ApiService {
    @POST("api/medication-logs/batch")
    suspend fun syncMedicationLogs(
        @Body logs: List<MedicationLog>
    ): Response<SyncResponse>
}
