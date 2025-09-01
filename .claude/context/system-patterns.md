---
created: 2025-08-31T13:03:35Z
last_updated: 2025-08-31T13:03:35Z
version: 1.0
author: Claude Code PM System
---

# System Patterns

## Architectural Pattern

### MVVM with Repository Pattern

```
┌─────────────────────────────────────────────┐
│                   View Layer                 │
│         (Activities, Fragments, Adapters)    │
└─────────────────┬───────────────────────────┘
                  │ ViewBinding/DataBinding
                  │ LiveData Observation
┌─────────────────▼───────────────────────────┐
│              ViewModel Layer                 │
│     (EventViewModel, CategoryViewModel)      │
└─────────────────┬───────────────────────────┘
                  │ LiveData/Flow
                  │ Coroutines
┌─────────────────▼───────────────────────────┐
│             Repository Layer                 │
│              (AppRepository)                 │
└─────────────────┬───────────────────────────┘
                  │ Suspend Functions
                  │ Flow Emissions
┌─────────────────▼───────────────────────────┐
│               Data Layer                     │
│         (Room Database, DAOs)                │
└──────────────────────────────────────────────┘
```

### Key Principles
- **Separation of Concerns**: Each layer has distinct responsibilities
- **Unidirectional Data Flow**: Data flows up, events flow down
- **Reactive Programming**: Using Flow and LiveData for state management
- **Lifecycle Awareness**: ViewModels survive configuration changes

## Design Patterns

### 1. Singleton Pattern
**Implementation**: Database and Repository
```kotlin
@Database(...)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                // Create database
            }
        }
    }
}
```

### 2. Factory Pattern
**Implementation**: ViewModelFactory
```kotlin
class ViewModelFactory(private val repository: AppRepository) : 
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Create appropriate ViewModel with dependencies
    }
}
```

### 3. Observer Pattern
**Implementation**: LiveData/Flow observation
- ViewModels expose LiveData/StateFlow
- Views observe and react to state changes
- Automatic lifecycle management

### 4. Repository Pattern
**Implementation**: AppRepository
- Single source of truth for data
- Abstracts data sources from ViewModels
- Handles data operations and caching logic

### 5. Adapter Pattern
**Implementation**: RecyclerView Adapters
- EventDaysMatterAdapter for Days Matter style cards
- EventAdapter for standard list items
- CategoryAdapter for category management

## Data Flow Patterns

### Event Creation Flow
```
User Input → Dialog → ViewModel → Repository → DAO → Database
                ↓
            UI Update ← LiveData ← Flow ← Database Trigger
```

### Category Filtering Flow
```
Drawer Selection → MainActivity → EventViewModel → Filter Logic
                            ↓
                    Filtered LiveData → Fragment → Adapter Update
```

### Widget Update Flow
```
Database Change → BroadcastReceiver → WidgetService → RemoteViews
                                                ↓
                                          Widget Update
```

## Async Patterns

### Coroutines Usage
- **ViewModelScope**: For ViewModel operations
- **lifecycleScope**: For Fragment/Activity operations
- **GlobalScope**: Avoided (anti-pattern)
- **suspend functions**: For database operations

### Flow Patterns
```kotlin
// Database to UI flow
dao.getAllEvents()
    .flowOn(Dispatchers.IO)
    .map { events -> events.filter { it.categoryId == selectedCategory } }
    .asLiveData()
```

## UI Patterns

### Single Activity Architecture
- MainActivity hosts all screens
- Fragments for different features
- ViewPager2 for tab navigation
- Navigation Drawer for category filtering

### Dialog Pattern
- DialogFragment for all dialogs
- Bundle arguments for data passing
- Interface callbacks for communication
- Consistent styling and behavior

### List Display Pattern
- RecyclerView with ViewHolder pattern
- DiffUtil for efficient updates (planned)
- ViewBinding in ViewHolders
- Click listeners via lambdas

## State Management

### ViewModel State
```kotlin
class EventViewModel : ViewModel() {
    private val _events = MutableLiveData<List<Event>>()
    val events: LiveData<List<Event>> = _events
    
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
}
```

### UI State Pattern
- Sealed classes for state representation
- Loading, Success, Error states
- Single source of truth in ViewModel

## Error Handling Patterns

### Current Implementation
- Basic try-catch in coroutines
- Toast messages for user feedback
- Log statements for debugging

### Recommended Pattern
```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()
}
```

## Database Patterns

### Room Configuration
- TypeConverters for Date objects
- Foreign key constraints
- Indices for performance
- Pre-populated database callback

### DAO Patterns
```kotlin
@Dao
interface EventDao {
    @Query("SELECT * FROM events")
    fun getAllEvents(): Flow<List<Event>>
    
    @Insert
    suspend fun insert(event: Event)
    
    @Update
    suspend fun update(event: Event)
    
    @Delete
    suspend fun delete(event: Event)
}
```

## Resource Management

### Context Usage
- Application context for singletons
- Activity context for UI operations
- Avoiding context leaks in ViewModels

### Memory Management
- ViewBinding to avoid findViewById
- Weak references where appropriate
- Proper lifecycle observation

## Testing Patterns

### Recommended Structure
- Unit tests for ViewModels and Repository
- Instrumented tests for DAOs
- UI tests for critical user journeys
- Mock repository for testing

## Security Patterns

### Data Protection
- Room database encryption (planned)
- Secure preferences for sensitive data (planned)
- Input validation at entry points

## Performance Patterns

### Database Optimization
- Indexed columns for queries
- Flow for reactive updates
- Batch operations where possible

### UI Optimization
- ViewHolder pattern in RecyclerViews
- Image caching with Coil
- Lazy loading for large lists

## Communication Patterns

### Fragment to Fragment
- Shared ViewModel approach
- Activity as mediator
- Event bus (avoided - anti-pattern)

### Service Communication
- BroadcastReceiver for widget updates
- WorkManager for background tasks
- Bound services (not currently used)

## Code Organization Patterns

### Package by Feature
```
ui/
  ├── event/
  ├── category/
  ├── settings/
  └── widget/
```

### Naming Conventions
- Activities: `*Activity`
- Fragments: `*Fragment`
- ViewModels: `*ViewModel`
- Adapters: `*Adapter`
- Dialogs: `*Dialog`

## Anti-Patterns to Avoid

### Currently Present
- Hard-coded strings in code
- Missing error handling
- No dependency injection framework
- Direct database access from UI

### Being Avoided
- God activities/fragments
- Callback hell
- Memory leaks
- Blocking main thread