---
created: 2025-08-31T13:03:35Z
last_updated: 2025-08-31T13:03:35Z
version: 1.0
author: Claude Code PM System
---

# Project Structure

## Root Directory Layout

```
bigtime/
├── .claude/                    # Claude AI context and project management
│   ├── agents/                 # AI agent configurations
│   ├── commands/               # Custom commands for project management
│   ├── context/                # Project context documentation (this directory)
│   ├── rules/                  # Development rules and patterns
│   └── scripts/                # Automation scripts
├── .gradle/                    # Gradle build cache
├── .idea/                      # Android Studio project settings
├── app/                        # Main application module
├── gradle/                     # Gradle wrapper files
├── build.gradle.kts            # Root build configuration
├── gradle.properties           # Gradle properties
├── settings.gradle.kts         # Project settings
├── CLAUDE.md                   # Claude AI instructions
├── Days_Matter_UI_Documentation.md  # UI design documentation
└── FEATURE_CHECKLIST.md        # Feature implementation tracking
```

## Application Module Structure

```
app/
├── build.gradle.kts            # Module build configuration
├── proguard-rules.pro          # ProGuard rules
└── src/
    ├── main/
    │   ├── AndroidManifest.xml # App manifest
    │   ├── java/com/coquankedian/bigtime/
    │   │   ├── MainActivity.kt # Main activity with navigation
    │   │   ├── data/           # Data layer
    │   │   │   ├── database/   # Room database
    │   │   │   │   ├── AppDatabase.kt
    │   │   │   │   └── Converters.kt
    │   │   │   ├── dao/        # Data access objects
    │   │   │   │   ├── EventDao.kt
    │   │   │   │   └── CategoryDao.kt
    │   │   │   ├── model/      # Entity classes
    │   │   │   │   ├── Event.kt
    │   │   │   │   ├── Category.kt
    │   │   │   │   └── Notebook.kt
    │   │   │   └── repository/ # Repository pattern
    │   │   │       └── AppRepository.kt
    │   │   ├── ui/             # UI layer
    │   │   │   ├── adapter/    # RecyclerView adapters
    │   │   │   │   ├── EventDaysMatterAdapter.kt
    │   │   │   │   ├── EventAdapter.kt
    │   │   │   │   └── CategoryAdapter.kt
    │   │   │   ├── dialog/     # Dialog fragments
    │   │   │   │   ├── AddEditEventDialog.kt
    │   │   │   │   ├── DatePickerDialog.kt
    │   │   │   │   └── EventMenuDialog.kt
    │   │   │   ├── EventListFragment.kt
    │   │   │   ├── CategoryFragment.kt
    │   │   │   ├── ArchivedEventFragment.kt
    │   │   │   ├── EventDetailActivity.kt
    │   │   │   ├── SettingsActivity.kt
    │   │   │   ├── EventViewModel.kt
    │   │   │   └── CategoryViewModel.kt
    │   │   └── widget/         # App widget
    │   │       ├── EventWidgetProvider.kt
    │   │       └── EventWidgetService.kt
    │   └── res/                # Resources
    │       ├── drawable/       # Vector drawables and shapes
    │       ├── layout/         # XML layouts
    │       │   ├── activity_*.xml
    │       │   ├── fragment_*.xml
    │       │   ├── dialog_*.xml
    │       │   └── item_*.xml
    │       ├── menu/           # Menu resources
    │       ├── mipmap-*/       # App icons
    │       ├── values/         # Values resources
    │       │   ├── colors.xml
    │       │   ├── strings.xml
    │       │   ├── themes.xml
    │       │   └── styles.xml
    │       ├── values-night/   # Dark theme resources
    │       └── xml/            # XML configurations
    │           └── file_paths.xml
    ├── test/                   # Unit tests
    │   └── java/com/coquankedian/bigtime/
    │       └── ExampleUnitTest.kt
    └── androidTest/            # Instrumented tests
        └── java/com/coquankedian/bigtime/
            └── ExampleInstrumentedTest.kt
```

## Key File Patterns

### Data Layer Files
- **Entities**: `data/model/*Entity.kt` - Data models with Room annotations
- **DAOs**: `data/dao/*Dao.kt` - Database access interfaces
- **Database**: `data/database/AppDatabase.kt` - Room database singleton
- **Repository**: `data/repository/*Repository.kt` - Data source abstraction

### UI Layer Files  
- **ViewModels**: `ui/*ViewModel.kt` - Business logic and state management
- **Fragments**: `ui/*Fragment.kt` - UI components
- **Activities**: `ui/*Activity.kt` - Activity containers
- **Adapters**: `ui/adapter/*Adapter.kt` - RecyclerView adapters
- **Dialogs**: `ui/dialog/*Dialog.kt` - Dialog fragments

### Resource Files
- **Layouts**: `res/layout/*.xml` - UI layouts
- **Drawables**: `res/drawable/*.xml` - Vector graphics and shapes
- **Values**: `res/values/*.xml` - Colors, strings, dimensions, styles
- **Menus**: `res/menu/*.xml` - Navigation and option menus

## Module Organization

### Package Structure
```
com.coquankedian.bigtime/
├── data/                # Data layer (Model)
├── ui/                  # Presentation layer (View + ViewModel)
├── widget/              # App widget components
└── MainActivity.kt      # Application entry point
```

### Layer Responsibilities
- **Data Layer**: Database, DAOs, repositories, models
- **UI Layer**: Activities, fragments, ViewModels, adapters
- **Widget Layer**: Home screen widget functionality

## Build Variants

Currently using single variant:
- **Debug**: Development builds with debugging enabled
- **Release**: Production builds (minification disabled)

## Asset Organization

### Image Resources
- App icons: `res/mipmap-*/`
- Vector drawables: `res/drawable/ic_*.xml`
- Backgrounds: `res/drawable/bg_*.xml`
- Shapes: `res/drawable/*_background.xml`

### String Resources
- App strings: `res/values/strings.xml`
- Currently has hard-coded Chinese strings that need extraction

## Configuration Files

### Project Configuration
- `build.gradle.kts` - Root project build script
- `app/build.gradle.kts` - App module build script
- `gradle.properties` - Gradle configuration
- `local.properties` - Local SDK path (not in VCS)

### Android Configuration
- `AndroidManifest.xml` - App permissions and components
- `proguard-rules.pro` - Code obfuscation rules (unused currently)

## Development Files

### Documentation
- `CLAUDE.md` - AI assistant instructions
- `Days_Matter_UI_Documentation.md` - UI design specs
- `FEATURE_CHECKLIST.md` - Feature implementation status
- `.claude/context/` - Project context documentation

### Version Control
- `.gitignore` - Git ignore patterns
- `.git/` - Git repository data