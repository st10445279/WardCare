package com.wardcare.app.data.repository

import com.wardcare.app.data.local.PatientDao
import com.wardcare.app.data.model.Patient
import kotlinx.coroutines.flow.Flow

class PatientRepository(private val patientDao: PatientDao) {

    val allPatients: Flow<List<Patient>> = patientDao.getAllPatients()

    fun searchPatients(query: String): Flow<List<Patient>> {
        return if (query.isBlank()) {
            patientDao.getAllPatients()
        } else {
            patientDao.searchPatients(query)
        }
    }

    fun getPatientById(id: String): Flow<Patient?> {
        return patientDao.getPatientById(id)
    }

    suspend fun addPatient(patient: Patient) {
        patientDao.insertPatient(patient)
    }
}
