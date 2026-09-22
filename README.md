WardCare

WardCare is a staff-facing Android app for tracking patient appointments and medication administration on a hospital ward or small practice. It is built for nurses and doctors to log doses given, missed, or delayed, keep an eye on upcoming appointments, and review patient details — it is explicitly not a clinical decision-support tool (no dosage calculation or drug-interaction checking).

This repository contains Part 2 (Build) of the project: the native Android client, built in Kotlin.

Team
Tiyase Ntesheng — ST10445279
Mabelebele Maselelo — ST10436039
Features
Authentication — login and registration screens, with a Google SSO entry point. Passwords are hashed with BCrypt before being handled by the app.
Home dashboard — quick overview for the signed-in staff member.
Patient list — patients on the ward, sorted by bed number.
Patient detail & history — patient info (age, gender, medical history, allergies, blood type) and a log of medication history for that patient.
Log medication — record a dose as given, missed, or delayed, with a timestamp.
Notifications — in-app list for things like overdue doses, upcoming appointments, and shift handover summaries.
Settings & profile — update staff profile details (name, ward, role) and preferences (language, notifications).
Multi-language support — English, isiZulu, and Afrikaans string resources.
Offline-first storage — patient, medication log, and notification data is persisted locally with Room, so the app works without a live connection to the ward Wi-Fi.
Tech stack
Language: Kotlin
Architecture: Fragments + ViewModel, View Binding, Navigation Component
Local persistence: Room (SQLite)
Networking: Retrofit2 + OkHttp (Gson converter)
Concurrency: Kotlin Coroutines
Security: BCrypt password hashing (at.favre.lib:bcrypt), AndroidX Security Crypto dependency for encrypted storage
Min SDK: 24 · Target/Compile SDK: 35

Project structure
app/src/main/java/com/wardcare/app/
├── data/
│   ├── local/        # Room database, DAOs, UserManager (SharedPreferences)
│   ├── model/         # Data models (Patient, MedicationLog, AppNotification)
│   ├── remote/        # Retrofit ApiClient / ApiService
│   └── repository/    # Repositories bridging local data and the API
├── notifications/      # NotificationHelper
├── security/          # PasswordHasher (BCrypt)
└── ui/
    ├── auth/           # Splash, Login, Register, AuthViewModel
    ├── dashboard/      # Home dashboard, Log medication
    ├── notifications/  # Notifications list
    ├── patients/       # Patient list, detail, history
    └── settings/       # Settings, user profile
Current state / known limitations

This build focuses on the Android client and its local data layer:

Login and registration currently run against local, simulated auth (AuthViewModel) rather than a live backend — this is where a hosted authentication API will be wired in for the final PoE.
The database seeds a small set of demo patients and notifications on first launch so the app is fully explorable without a backend connection.
One sync endpoint (POST /api/medication-logs/batch) is scaffolded in ApiService for pushing queued offline medication logs to a server; the base URL is currently a placeholder.
Google SSO is a UI entry point pending full OAuth2 integration.
Getting started
Clone the repository and open it in Android Studio (Ladybug or newer recommended).
Let Gradle sync — the project uses AGP 8.7.2 and Kotlin 2.0.21.
Run on an emulator or device with API level 24+.
