WardCare

WardCare is a staff-facing Android app for tracking patient appointments and medication administration on a hospital ward or small practice. It is built for nurses and doctors to log doses given, missed, or delayed, keep an eye on upcoming appointments, and review patient details — it is explicitly not a clinical decision-support tool (no dosage calculation or drug-interaction checking).

This repository contains Part 2 (Build) of the project: the native Android client, built in Kotlin.

Team
Tiyase Ntesheng — ST10445279
Mabelebele Maselelo — ST10436039

1. Purpose of the app

Problem statement. Paper-based patient and medication tracking on a busy ward is slow and error-prone, nurses already carry a high workload, and ward Wi-Fi is often unreliable — a purely online tool would fail exactly when it's needed most.

Purpose & vision. WardCare is a digital bedside assistant: it gives staff a fast way to check on patients, log medication administration, and see ward notifications, with an offline-first design so logging a dose never depends on having a live connection.

Target users & value. Nurses, doctors, and ward supervisors. The value proposition is uptime that doesn't depend on Wi-Fi, no lost medication logs while offline, and a UI available in English, isiZulu, and Afrikaans.

2. Design considerations

Architecture. MVVM + Repository pattern: Fragments observe a ViewModel, which exposes state via StateFlow/Flow; each Repository (e.g. MedicationRepository, PatientRepository) is the single source of truth, backed by Room locally and Retrofit for the network.

Offline-first sync. A medication log is always written to the local Room database first, flagged isSyncPending = true. Pending logs are sent to the API in batches of up to 20 (pendingLogs.chunked(20)); each log is flipped to synced only once the server confirms it, so a dropped connection never loses data — it just stays queued.

UI/UX. Material 3 components (MaterialCardView, MaterialAlertDialogBuilder) on a calm clinical blue palette (colorPrimary = #1976D2), with status badges for Given/Missed/Delayed doses.

Security. Passwords are hashed with BCrypt (PasswordHasher, cost factor 10) before being handled by the app. Session/profile state is kept via UserManager (SharedPreferences).

Localization. Language isn't just static string resources — SettingsFragment switches the app's locale at runtime via AppCompatDelegate.setApplicationLocales, so English, isiZulu, and Afrikaans can be swapped without restarting the app.

3. Use of GitHub

Version control. The project is tracked with modular, descriptive commits (e.g. separating the initial build, dependency additions, and logging instrumentation into distinct commits) so history reflects how the app was actually built.

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
No CI/CD pipeline yet — a GitHub Actions workflow to auto-build a debug APK is planned but not yet added.
Getting started
Clone the repository and open it in Android Studio (Ladybug or newer recommended).
Let Gradle sync — the project uses AGP 8.7.2 and Kotlin 2.0.21.
Run on an emulator or device with API level 24+.

