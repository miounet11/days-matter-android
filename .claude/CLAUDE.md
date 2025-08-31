# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

> Think carefully and implement the most concise solution that changes as little code as possible.

## USE SUB-AGENTS FOR CONTEXT OPTIMIZATION

### 1. Always use the file-analyzer sub-agent when asked to read files.
The file-analyzer agent is an expert in extracting and summarizing critical information from files, particularly log files and verbose outputs. It provides concise, actionable summaries that preserve essential information while dramatically reducing context usage.

### 2. Always use the code-analyzer sub-agent when asked to search code, analyze code, research bugs, or trace logic flow.

The code-analyzer agent is an expert in code analysis, logic tracing, and vulnerability detection. It provides concise, actionable summaries that preserve essential information while dramatically reducing context usage.

### 3. Always use the test-runner sub-agent to run tests and analyze the test results.

Using the test-runner agent ensures:

- Full test output is captured for debugging
- Main conversation stays clean and focused
- Context usage is optimized
- All issues are properly surfaced
- No approval dialogs interrupt the workflow

## Philosophy

### Error Handling

- **Fail fast** for critical configuration (missing text model)
- **Log and continue** for optional features (extraction model)
- **Graceful degradation** when external services unavailable
- **User-friendly messages** through resilience layer

### Testing

- Always use the test-runner agent to execute tests.
- Do not use mock services for anything ever.
- Do not move on to the next test until the current test is complete.
- If the test fails, consider checking if the test is structured correctly before deciding we need to refactor the codebase.
- Tests to be verbose so we can use them for debugging.


## Tone and Behavior

- Criticism is welcome. Please tell me when I am wrong or mistaken, or even when you think I might be wrong or mistaken.
- Please tell me if there is a better approach than the one I am taking.
- Please tell me if there is a relevant standard or convention that I appear to be unaware of.
- Be skeptical.
- Be concise.
- Short summaries are OK, but don't give an extended breakdown unless we are working through the details of a plan.
- Do not flatter, and do not give compliments unless I am specifically asking for your judgement.
- Occasional pleasantries are fine.
- Feel free to ask many questions. If you are in doubt of my intent, don't guess. Ask.

## ABSOLUTE RULES:

- NO PARTIAL IMPLEMENTATION
- NO SIMPLIFICATION : no "//This is simplified stuff for now, complete implementation would blablabla"
- NO CODE DUPLICATION : check existing codebase to reuse functions and constants Read files before writing new functions. Use common sense function name to find them easily.
- NO DEAD CODE : either use or delete from codebase completely
- IMPLEMENT TEST FOR EVERY FUNCTIONS
- NO CHEATER TESTS : test must be accurate, reflect real usage and be designed to reveal flaws. No useless tests! Design tests to be verbose so we can use them for debuging.
- NO INCONSISTENT NAMING - read existing codebase naming patterns.
- NO OVER-ENGINEERING - Don't add unnecessary abstractions, factory patterns, or middleware when simple functions would work. Don't think "enterprise" when you need "working"
- NO MIXED CONCERNS - Don't put validation logic inside API handlers, database queries inside UI components, etc. instead of proper separation
- NO RESOURCE LEAKS - Don't forget to close database connections, clear timeouts, remove event listeners, or clean up file handles

## Android Day Counter App - Project Specifics

### Development Commands

```bash
# Build the app
./gradlew build

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Install on connected device/emulator
./gradlew installDebug

# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Clean build
./gradlew clean

# Run specific test class
./gradlew test --tests "com.coquankedian.bigtime.*TestClassName"

# Check dependencies
./gradlew dependencies
```

### Architecture Overview

**MVVM Architecture with Repository Pattern**

```
UI Layer (View)
├── MainActivity (Single Activity)
├── Fragments (EventList, Category, Archived)
├── Dialogs (AddEditEvent, DatePicker, etc.)
└── Adapters (EventAdapter, CategoryAdapter)
        ↕ ViewBinding & LiveData
Presentation Layer (ViewModel)
├── EventViewModel
└── CategoryViewModel
        ↕ LiveData/Flow
Data Layer (Model)
├── Repository (AppRepository)
├── DAOs (EventDao, CategoryDao)
├── Database (AppDatabase - Room)
└── Entities (Event, Category)
```

**Key Architectural Decisions:**
- **Single Activity Architecture**: MainActivity hosts all fragments via ViewPager2
- **Repository Pattern**: AppRepository is the single source of truth for data
- **Reactive Data Flow**: Room → Flow → LiveData → UI with automatic updates
- **ViewModelFactory**: Custom factory for dependency injection to ViewModels
- **Coroutines**: All async operations use Kotlin coroutines with viewModelScope

### Project Structure

```
app/src/main/
├── java/com/coquankedian/bigtime/
│   ├── MainActivity.kt              # Entry point, hosts ViewPager2
│   ├── data/
│   │   ├── database/
│   │   │   ├── AppDatabase.kt      # Room database configuration
│   │   │   └── Converters.kt       # Type converters for Date
│   │   ├── dao/                    # Database access objects
│   │   ├── model/                  # Entity classes
│   │   └── repository/             # Data repository
│   ├── ui/
│   │   ├── adapter/                # RecyclerView adapters
│   │   ├── dialog/                 # Dialog fragments
│   │   ├── *Fragment.kt            # Main UI fragments
│   │   └── *ViewModel.kt           # ViewModels
│   └── widget/                     # App widget implementation
├── res/
│   ├── layout/                     # XML layouts
│   ├── values/                     # Resources (strings, colors, themes)
│   └── values-night/               # Dark theme resources
└── AndroidManifest.xml
```

### Key Dependencies & Versions

- **Target SDK**: 36 (Android 15)
- **Min SDK**: 24 (Android 7.0)
- **Kotlin JVM Target**: 11
- **Room Database**: For local data persistence with Flow support
- **ViewPager2**: Tab navigation between Event List, Categories, and Archived
- **Material Design Components**: UI components and theming
- **Kotlin Coroutines**: Async operations with lifecycle-aware scopes
- **WorkManager**: Background tasks for reminders/notifications
- **ViewBinding/DataBinding**: Type-safe view references

### Database Schema

**Events Table:**
- id (Primary Key)
- title, description
- date (Date with TypeConverter)
- categoryId (Foreign Key)
- isCountingUp, isArchived, isPinned
- color, repeatType, isPrivate
- lastNotified, createdAt

**Categories Table:**
- id (Primary Key)
- name, color, icon
- isDefault

### Testing Strategy

- **Unit Tests**: Test ViewModels, Repository, and business logic
- **Instrumented Tests**: Test Room database operations and DAOs
- **UI Tests**: Use Espresso for fragment and dialog testing
- Place tests in `app/src/test/` (unit) and `app/src/androidTest/` (instrumented)

### Common Development Tasks

**Adding a new feature:**
1. Update data model if needed (Entity classes)
2. Update DAOs with new queries
3. Update Repository with new methods
4. Update or create ViewModel
5. Create/update UI components (Fragment/Dialog)
6. Add to MainActivity's ViewPager if needed

**Modifying database schema:**
1. Update Entity classes
2. Increment database version in AppDatabase
3. Add migration in AppDatabase.kt
4. Update DAOs if needed

**Working with dialogs:**
- All dialogs extend DialogFragment
- Use ViewBinding for view references
- Pass data via Bundle arguments
- Communicate back via interface callbacks or SharedViewModel
