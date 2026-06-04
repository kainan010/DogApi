# 🐕 WhatsUpDog

A modern Android app that lets you discover, explore, and save adorable dog photos — powered by the [Dog CEO API](https://dog.ceo/dog-api/).

Built with **Jetpack Compose**, **Material 3**, **Clean Architecture**, **Koin DI**, and **Room** — following **SOLID** principles.

---

## ✨ Features

| Feature | Description |
|---|---|
| 🏠 **Random Dog** | Discover random dog photos with a single tap or swipe |
| 👆 **Swipe Navigation** | Tinder-like swipe left/right to explore dogs with history |
| 🐕 **Breed Explorer** | Browse all breeds with real-time search filtering |
| 🖼️ **Breed Gallery** | View a staggered grid of photos for each breed |
| ❤️ **Favorites** | Save your favorite dogs locally with Room database |
| 🌙 **Dark Mode** | Beautiful dark theme with deep navy surfaces |
| ✨ **Animations** | Smooth transitions, shimmer loading, spring animations |
| 🎨 **Material 3** | Dynamic color support (Android 12+) with warm amber palette |

---

## 🏗️ Architecture

The project follows **Clean Architecture** with **MVVM** pattern, organized into three layers:

```
┌──────────────────────────────────────────────────┐
│                 Presentation                      │
│  ┌──────────┐  ┌──────────┐  ┌───────────────┐  │
│  │ Screens  │  │Components│  │  ViewModels   │  │
│  │ (Compose)│  │          │  │  (StateFlow)  │  │
│  └──────────┘  └──────────┘  └───────────────┘  │
├──────────────────────────────────────────────────┤
│                    Domain                         │
│  ┌──────────┐  ┌──────────┐  ┌───────────────┐  │
│  │  Models  │  │Use Cases │  │  Repositories │  │
│  │          │  │          │  │  (Interfaces)  │  │
│  └──────────┘  └──────────┘  └───────────────┘  │
├──────────────────────────────────────────────────┤
│                     Data                          │
│  ┌──────────┐  ┌──────────┐  ┌───────────────┐  │
│  │ Retrofit │  │   Room   │  │  Repositories │  │
│  │  (API)   │  │   (DB)   │  │   (Impl)      │  │
│  └──────────┘  └──────────┘  └───────────────┘  │
└──────────────────────────────────────────────────┘
```

### SOLID Principles Applied

| Principle | Implementation |
|---|---|
| **S** — Single Responsibility | Each class has one job (UseCase, Repository, ViewModel) |
| **O** — Open/Closed | Sealed interfaces for UI states, extensible via new implementations |
| **L** — Liskov Substitution | Repository implementations are interchangeable with their interfaces |
| **I** — Interface Segregation | Small, focused interfaces (DogRepository, FavoriteRepository) |
| **D** — Dependency Inversion | ViewModels depend on UseCases (abstractions), not concrete Repositories |

---

## 🛠️ Tech Stack

| Category | Technology |
|---|---|
| **Language** | Kotlin 2.0.21 |
| **UI Framework** | Jetpack Compose + Material 3 |
| **Architecture** | MVVM + Clean Architecture |
| **Dependency Injection** | Koin 4.0 |
| **Networking** | Retrofit 2.11 + OkHttp 4.12 |
| **Serialization** | Kotlinx Serialization 1.7.3 |
| **Image Loading** | Coil 3.0.4 (Compose native) |
| **Local Database** | Room 2.6.1 |
| **Navigation** | Navigation Compose 2.8.5 |
| **State Management** | StateFlow + collectAsStateWithLifecycle |
| **Build System** | Gradle 8.11.1 + AGP 8.7.3 (Kotlin DSL) |
| **Testing** | JUnit 4 + MockK + Turbine |
| **Min SDK** | 29 (Android 10) |
| **Target SDK** | 35 (Android 15) |

---

## 📁 Project Structure

```
app/src/main/java/com/naniak/whatsupdog/
├── MainActivity.kt
├── WhatsUpDogApp.kt                          # Application (Koin init)
├── di/
│   └── AppModule.kt                          # 5 Koin modules
├── data/
│   ├── local/                                # Room database
│   │   ├── AppDatabase.kt
│   │   ├── FavoriteDao.kt
│   │   └── FavoriteEntity.kt
│   ├── remote/                               # Retrofit API
│   │   ├── DogApiService.kt
│   │   ├── RetrofitClient.kt
│   │   └── dto/
│   │       ├── BreedImagesDto.kt
│   │       ├── BreedListDto.kt
│   │       └── DogRandomDto.kt
│   └── repository/                           # Repository implementations
│       ├── DogRepositoryImpl.kt
│       └── FavoriteRepositoryImpl.kt
├── domain/
│   ├── model/                                # Domain models
│   │   ├── Breed.kt
│   │   ├── DogImage.kt
│   │   └── FavoriteDog.kt
│   ├── repository/                           # Repository interfaces
│   │   ├── DogRepository.kt
│   │   └── FavoriteRepository.kt
│   └── usecase/                              # Business logic
│       ├── GetAllBreedsUseCase.kt
│       ├── GetBreedImagesUseCase.kt
│       ├── GetFavoritesUseCase.kt
│       ├── GetRandomDogUseCase.kt
│       ├── IsFavoriteUseCase.kt
│       └── ToggleFavoriteUseCase.kt
└── presentation/
    ├── components/                           # Reusable UI components
    │   ├── DogImageCard.kt
    │   ├── ErrorView.kt
    │   └── ShimmerEffect.kt
    ├── navigation/                           # Compose Navigation
    │   ├── AppNavigation.kt
    │   └── Screen.kt
    ├── screens/
    │   ├── home/                             # Random dog + swipe
    │   ├── breeds/                           # Breed list + search
    │   ├── gallery/                          # Breed image gallery
    │   └── favorites/                        # Saved favorites
    └── theme/                                # Material 3 theme
        ├── Color.kt
        ├── Theme.kt
        └── Type.kt
```

---

## 🧪 Testing

**58 unit tests** across 12 test files covering:

- ✅ All 6 Use Cases
- ✅ Both Repository implementations
- ✅ All 4 ViewModels

```bash
./gradlew test
```

**Testing tools:** JUnit 4, MockK (mocking), Turbine (Flow testing), Coroutines Test

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Ladybug (2024.2) or later
- JDK 17+
- Android SDK 35

### Build & Run

```bash
# Clone the repository
git clone https://github.com/kainan010/DogApi.git

# Open in Android Studio and sync Gradle
# Run on emulator or device (API 29+)
```

---

## 🌐 API

This app uses the free [Dog CEO API](https://dog.ceo/dog-api/):

| Endpoint | Description |
|---|---|
| `GET /breeds/image/random` | Random dog image |
| `GET /breeds/list/all` | All breeds with sub-breeds |
| `GET /breed/{breed}/images` | All images for a breed |

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).

---

<p align="center">
  Made with ❤️ and 🐾 by <a href="https://github.com/kainan010">kainan010</a>
</p>
