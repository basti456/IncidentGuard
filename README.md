# IncidentGuard — Kotlin Multiplatform (KMP) Offline-First Field Inspection Engine

**IncidentGuard** is an enterprise-ready, offline-first field inspection and bug-reporting mobile application built using **Compose Multiplatform (KMP)** targeting **Android** and **iOS**.

---

## 📱 Screenshots & Application Demo

| Field Reports & Interactive Map | Report Incident Form | Severity Map Pins |
| :---: | :---: | :---: |
| *Incident List Screen showing Google Maps pins & scrollable cards* | *Form validation, severity selector & location pin drop* | *Red, Orange, Yellow, Green severity pins & Blue current location* |
| `docs/screenshots/incident_list.png` | `docs/screenshots/create_incident.png` | `docs/screenshots/map_pins.png` |

---

## 🏗️ System Architecture & Data Pipeline

IncidentGuard follows strict **Clean Architecture** principles and a **Local-First Ground Truth** data strategy. The user interface **never** waits for network requests; every action persists instantly to the local database, streaming updates to the UI in real time.

```
shared/src/commonMain/kotlin/com/ekagra/incidentguard/
  ├── domain/             --> Clean Domain Layer (Pure Kotlin)
  │   ├── model/         --> Incident, WeatherInfo, Severity, Status
  │   ├── repository/    --> IncidentRepository interface
  │   ├── usecase/       --> GetIncidentUseCase, CreateIncidentUseCase, SyncIncidentsUseCase
  │   └── util/          --> Resource<T>
  ├── data/               --> Data Persistence & Networking
  │   ├── local/         --> SQLDelight DB (IncidentEntity.sq), IncidentLocalDataSourceImpl
  │   ├── remote/        --> Ktor REST Client (Open-Meteo), NativeSyncEngine (Firebase)
  │   └── repository/    --> IncidentRepositoryImpl (Offline-first coordinator)
  ├── presentation/       --> ViewModels & Compose UI
  │   ├── incident_list/  --> IncidentListScreen, IncidentListViewModel, IncidentListUiState
  │   ├── incident_create/--> CreateIncidentScreen, CreateIncidentViewModel, CreateIncidentUiState
  │   ├── components/    --> IncidentMapView (Google Maps bridge), PhotoPicker
  │   └── navigation/    --> NavGraph (Type-Safe Navigation Compose)
  └── di/                 --> Koin Dependency Injection
      └── appModule()    --> platformModule, databaseModule, networkModule, repositoryModule, useCaseModule, viewModelModule
```

---

## ✨ Key Features

- ⚡ **Offline-First Ground Truth Persistence**: Saves field reports instantly (0ms latency) to local **SQLDelight** SQLite database (`isSynced = false`).
- 🔄 **Non-Blocking Background Cloud Sync**: Asynchronously pushes queued local records to **Firebase Cloud Firestore** and **Firebase Storage** when online, updating local status to `isSynced = true`.
- 🌤️ **Real-Time Weather Metadata**: Queries **Open-Meteo REST API** via **Ktor Client 3.x** and `kotlinx.serialization` to enrich field reports with temperature and weather condition at exact GPS coordinates.
- 🗺️ **Interactive Cross-Platform Maps**: Native **Google Maps** integration (`GoogleMap` Compose on Android / `UIKitView` embedding `GMSMapView` on iOS) with severity color-coded markers (Red, Orange, Yellow, Green, Blue).
- 📶 **Real-Time Flaky-Network Observer**: Listens to connectivity changes via `callbackFlow` with 1s debouncing and `NET_CAPABILITY_VALIDATED` checks, automatically triggering sync upon internet restoration.
- 📍 **Native GPS Location Auto-Detection**: Integrates Google Play Services `FusedLocationProviderClient` (Android) and Apple `CoreLocation` `CLLocationManager` (iOS) to auto-center field map pins.
- 🎨 **Declarative Material 3 UI & Type-Safe Navigation**: Built with Compose Multiplatform, Material 3, and `@Serializable` Type-Safe Navigation Compose routes (`ScreenRoute`).

---

## 🛠️ Technology Stack

| Layer | Technology |
| :--- | :--- |
| **Language** | Kotlin 2.1+ / Swift 5+ |
| **Cross-Platform Framework** | Compose Multiplatform (KMP) |
| **UI & Design System** | Material 3, Navigation Compose (Type-Safe `@Serializable` Routes) |
| **Local Database** | SQLDelight 2.x (AndroidSqliteDriver & NativeSqliteDriver) |
| **External Networking** | Ktor Client 3.x, `kotlinx.serialization` (Open-Meteo REST API) |
| **Cloud Backend & Sync** | Native Firebase BoM (Android) & Firebase Swift SDK (iOS) via `expect`/`actual` |
| **Interactive Mapping** | Google Maps Compose (Android) & `UIKitView` GMSMapView (iOS) |
| **Dependency Injection** | Koin 4.x (`koinConfiguration { modules(appModule()) }`) |
| **Concurrency & Reactive** | Kotlin Coroutines, `StateFlow`, `callbackFlow` |

---

## 🚀 Building & Running

### Android Setup
1. Add your Google Maps Android API Key to `local.properties`:
   ```properties
   MAPS_API_KEY=AIzaSy...your_key_here...
   ```
2. Run debug build:
   ```bash
   ./gradlew :androidApp:assembleDebug
   ```

### iOS Setup
1. Add your Google Maps iOS API Key to `local.properties`:
   ```properties
   MAPS_API_KEY_IOS=AIzaSy...your_key_here...
   ```
2. Open `iosApp/iosApp.xcodeproj` in **Xcode**.
3. Press **`Cmd + R`** to run on iOS Simulator or Device.
