---
created: 2025-08-31T13:03:35Z
last_updated: 2025-08-31T13:03:35Z
version: 1.0
author: Claude Code PM System
---

# Technical Context

## Technology Stack

### Core Technologies
- **Language**: Kotlin
- **Platform**: Android Native
- **Build System**: Gradle 8.x with Kotlin DSL
- **IDE**: Android Studio

### Android Configuration
- **Min SDK**: 24 (Android 7.0 Nougat)
- **Target SDK**: 36 (Android 15)
- **Compile SDK**: 36
- **JVM Target**: Java 11
- **Kotlin JVM Target**: 11

## Dependencies

### AndroidX Core
- `androidx.core:core-ktx` - Kotlin extensions for Android
- `androidx.appcompat:appcompat` - Backward compatibility
- `androidx.activity:activity` - Activity components
- `androidx.constraintlayout:constraintlayout` - Constraint layouts

### UI Components
- `com.google.android.material:material` - Material Design components
- `androidx.viewpager2:viewpager2` - Tab navigation
- `androidx.recyclerview:recyclerview` - List displays
- `androidx.cardview:cardview` - Card views
- `androidx.swiperefreshlayout:swiperefreshlayout` - Pull to refresh
- `androidx.appcompat:appcompat-resources` - Resource compatibility

### Architecture Components
- `androidx.lifecycle:lifecycle-viewmodel-ktx` - ViewModel with coroutines
- `androidx.lifecycle:lifecycle-livedata-ktx` - LiveData with coroutines
- `androidx.lifecycle:lifecycle-runtime-ktx` - Lifecycle runtime with coroutines

### Data Persistence
- `androidx.room:room-runtime` - Room database
- `androidx.room:room-ktx` - Room Kotlin extensions
- `androidx.room:room-compiler` - Room annotation processor (kapt)

### Background Processing
- `androidx.work:work-runtime-ktx` - WorkManager for background tasks
- `kotlinx.coroutines:kotlinx-coroutines-android` - Coroutines for Android

### Security
- `androidx.security:security-crypto` - Encrypted preferences

### Image Loading
- `io.coil-kt:coil` - Image loading library

### Testing Dependencies
- `junit:junit` - Unit testing
- `androidx.test.ext:junit` - AndroidX JUnit extensions
- `androidx.test.espresso:espresso-core` - UI testing

## Build Configuration

### Gradle Plugins
- `com.android.application` - Android application plugin
- `org.jetbrains.kotlin.android` - Kotlin Android plugin
- `kotlin-kapt` - Kotlin annotation processing

### Build Features
- **View Binding**: Enabled for type-safe view references
- **Data Binding**: Enabled for data binding support
- **Minification**: Disabled in release builds

### Build Types
- **Debug**: Development builds with debugging enabled
- **Release**: Production builds without minification

## Development Tools

### Project Management
- **Version Control**: Git
- **Repository**: GitHub (https://github.com/miounet11/days-matter-android.git)
- **CI/CD**: Not configured

### Code Quality
- **Linting**: Android Lint (default configuration)
- **ProGuard**: Rules defined but minification disabled
- **Testing**: JUnit 4 + Espresso (minimal implementation)

## Database Technology

### Room Database Setup
- **Version**: 1
- **Entities**: Event, Category, Notebook (planned)
- **Type Converters**: Date conversion support
- **Migration Strategy**: Not implemented (risk for updates)

### Database Features
- Kotlin Flow support for reactive queries
- Coroutines integration
- Foreign key constraints
- Default data population

## Architectural Libraries

### Dependency Injection
- **Current**: Manual ViewModelFactory
- **Future Consideration**: Dagger/Hilt for proper DI

### Navigation
- **Current**: ViewPager2 with fragments
- **Alternative**: Navigation Component (not used)

## Network & API
- **Current Status**: No network layer implemented
- **Future Requirements**: Cloud sync, weather API integration

## Widget Technology
- **App Widget**: Basic implementation with RemoteViews
- **Update Strategy**: Manual updates via service

## Security Considerations
- **Data Encryption**: androidx.security available but not implemented
- **Password Protection**: Planned but not implemented
- **Secure Storage**: Not currently using encrypted preferences

## Performance Tools
- **Image Caching**: Coil library with default configuration
- **Database Optimization**: Room with Flow for efficient queries
- **Memory Management**: ViewBinding to avoid memory leaks

## Development Environment

### Required Tools
- Android Studio Arctic Fox or newer
- Android SDK 36
- Kotlin 1.9.x
- Gradle 8.x

### Environment Setup
```bash
# Build the project
./gradlew build

# Install on device
./gradlew installDebug

# Run tests
./gradlew test
./gradlew connectedAndroidTest
```

## Third-Party Services

### Current Integrations
- None active

### Planned Integrations
- iCity cloud sync service
- Weather API for event suggestions
- Lunar calendar API
- Historical events API

## Version History

### Current Version
- **Version Code**: 2
- **Version Name**: 1.0.1
- **Release Date**: Recent

### Version Compatibility
- Supports Android 7.0 (API 24) and above
- Covers ~98% of active Android devices

## Technical Debt

### Known Issues
- No database migration strategy
- Hard-coded strings instead of resources
- Widget coroutine lifecycle management
- Missing comprehensive error handling

### Improvement Areas
- Add proper dependency injection
- Implement database migrations
- Add network layer for cloud features
- Improve test coverage
- Add analytics integration