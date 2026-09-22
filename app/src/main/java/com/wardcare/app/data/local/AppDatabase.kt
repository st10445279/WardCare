package com.wardcare.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.wardcare.app.data.model.AppNotification
import com.wardcare.app.data.model.MedicationLog
import com.wardcare.app.data.model.Patient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [MedicationLog::class, Patient::class, AppNotification::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun medicationDao(): MedicationDao
    abstract fun patientDao(): PatientDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "wardcare_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database)
                    }
                }
            }

            private suspend fun populateInitialData(database: AppDatabase) {
                val patientDao = database.patientDao()
                val notificationDao = database.notificationDao()

                if (patientDao.getPatientCount() == 0) {
                    patientDao.insertPatients(
                        listOf(
                            Patient(
                                id = "patient-1",
                                patientId = "P-101",
                                name = "J. Nkosi",
                                bedNumber = "Bed 4",
                                age = 42,
                                gender = "Male",
                                medicalHistory = "Hypertension, Asthma",
                                allergies = "Penicillin",
                                bloodType = "A+",
                                nextAppointment = "14:00",
                                medSchedule = "Amoxicillin 8h"
                            ),
                            Patient(
                                id = "patient-2",
                                patientId = "P-102",
                                name = "T. Mokoena",
                                bedNumber = "Bed 5",
                                age = 38,
                                gender = "Female",
                                medicalHistory = "Post-op Recovery",
                                allergies = "None",
                                bloodType = "O+",
                                nextAppointment = "15:30",
                                medSchedule = "Paracetamol 500mg 6h"
                            ),
                            Patient(
                                id = "patient-3",
                                patientId = "P-103",
                                name = "A. van Wyk",
                                bedNumber = "Bed 6",
                                age = 55,
                                gender = "Male",
                                medicalHistory = "Type 2 Diabetes",
                                allergies = "Sulfa drugs",
                                bloodType = "B-",
                                nextAppointment = "16:00",
                                medSchedule = "Insulin 12h"
                            )
                        )
                    )
                }

                if (notificationDao.getNotificationCount() == 0) {
                    notificationDao.insertNotifications(
                        listOf(
                            AppNotification(
                                title = "Dose overdue: Bed 4, Amoxicillin",
                                type = "overdue"
                            ),
                            AppNotification(
                                title = "Appointment in 15 min: Bed 6",
                                type = "appointment"
                            ),
                            AppNotification(
                                title = "Shift handover summary ready",
                                type = "handover"
                            )
                        )
                    )
                }
            }
        }
    }
}
