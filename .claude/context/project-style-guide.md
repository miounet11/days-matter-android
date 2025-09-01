---
created: 2025-08-31T13:03:35Z
last_updated: 2025-08-31T13:03:35Z
version: 1.0
author: Claude Code PM System
---

# Project Style Guide

## Code Style

### Kotlin Conventions

#### Naming Conventions
```kotlin
// Classes: PascalCase
class EventViewModel
class AddEditEventDialog

// Functions: camelCase
fun calculateDaysRemaining()
fun updateEventStatus()

// Variables: camelCase
val eventTitle: String
var isArchived: Boolean

// Constants: UPPER_SNAKE_CASE
const val DATABASE_NAME = "app_database"
const val DEFAULT_CATEGORY_ID = 1L

// Packages: lowercase
package com.coquankedian.bigtime.data.model
```

#### File Organization
```kotlin
// Standard order within Kotlin files:
// 1. Package declaration
package com.coquankedian.bigtime.ui

// 2. Import statements (alphabetically sorted)
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.coquankedian.bigtime.data.model.Event

// 3. Class declaration
class EventListFragment : Fragment() {
    // 4. Companion object
    companion object {
        const val TAG = "EventListFragment"
    }
    
    // 5. Properties (grouped by visibility)
    private lateinit var viewModel: EventViewModel
    private var adapter: EventAdapter? = null
    
    // 6. Lifecycle methods (in lifecycle order)
    override fun onCreate(savedInstanceState: Bundle?) { }
    override fun onCreateView(...) { }
    override fun onViewCreated(...) { }
    
    // 7. Public methods
    fun refreshEvents() { }
    
    // 8. Private methods
    private fun setupRecyclerView() { }
    
    // 9. Inner classes/interfaces
    interface OnEventClickListener { }
}
```

#### Code Formatting
```kotlin
// Function parameters: one per line if many
fun createEvent(
    title: String,
    date: Date,
    description: String,
    categoryId: Long
): Event {
    // Implementation
}

// Lambda expressions
events.filter { it.isActive }
    .map { it.title }
    .forEach { println(it) }

// When expressions
when (event.type) {
    EventType.COUNTDOWN -> handleCountdown()
    EventType.COUNTUP -> handleCountup()
    else -> handleDefault()
}
```

### XML Layout Conventions

#### File Naming
```
activity_*.xml      // Activities
fragment_*.xml      // Fragments
dialog_*.xml        // Dialogs
item_*.xml          // RecyclerView items
layout_*.xml        // Included layouts
widget_*.xml        // App widgets
```

#### ID Naming
```xml
<!-- View type prefix + description -->
<TextView android:id="@+id/tvEventTitle" />
<Button android:id="@+id/btnSave" />
<EditText android:id="@+id/etDescription" />
<ImageView android:id="@+id/ivIcon" />
<RecyclerView android:id="@+id/rvEvents" />
```

#### Resource Organization
```xml
<!-- Strings: use descriptive keys -->
<string name="event_title_hint">Enter event title</string>
<string name="button_save">Save</string>
<string name="error_empty_title">Title cannot be empty</string>

<!-- Colors: semantic naming -->
<color name="primary">@color/blue_500</color>
<color name="primary_dark">@color/blue_700</color>
<color name="accent">@color/orange_500</color>
<color name="text_primary">@color/gray_900</color>

<!-- Dimensions: use standard sizes -->
<dimen name="margin_small">8dp</dimen>
<dimen name="margin_medium">16dp</dimen>
<dimen name="margin_large">24dp</dimen>
```

## Architecture Guidelines

### MVVM Pattern Rules
```kotlin
// ViewModels should not reference Views
class EventViewModel : ViewModel() {
    // ❌ Wrong
    // private lateinit var textView: TextView
    
    // ✅ Correct
    private val _eventTitle = MutableLiveData<String>()
    val eventTitle: LiveData<String> = _eventTitle
}

// Views observe ViewModel state
class EventFragment : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.events.observe(viewLifecycleOwner) { events ->
            adapter.submitList(events)
        }
    }
}
```

### Repository Pattern
```kotlin
// Repository as single source of truth
class AppRepository(
    private val eventDao: EventDao,
    private val categoryDao: CategoryDao
) {
    // Expose data as Flow/LiveData
    fun getAllEvents(): Flow<List<Event>> = eventDao.getAllEvents()
    
    // Suspend functions for operations
    suspend fun insertEvent(event: Event) {
        eventDao.insert(event)
    }
}
```

## UI/UX Guidelines

### Material Design Principles
- Follow Material Design 3 guidelines
- Use Material components wherever possible
- Maintain consistent elevation and shadows
- Apply proper ripple effects and transitions

### Color Scheme
```xml
<!-- Light Theme -->
<color name="primary">#2196F3</color>       <!-- Blue -->
<color name="primary_variant">#1976D2</color>
<color name="secondary">#FF9800</color>     <!-- Orange -->
<color name="background">#FFFFFF</color>
<color name="surface">#F5F5F5</color>
<color name="error">#F44336</color>

<!-- Dark Theme -->
<color name="primary_dark">#1E88E5</color>
<color name="secondary_dark">#FFB74D</color>
<color name="background_dark">#121212</color>
<color name="surface_dark">#1E1E1E</color>
```

### Typography
```xml
<!-- Text styles following Material Design -->
<style name="TextAppearance.BigTime.Headline">
    <item name="android:textSize">24sp</item>
    <item name="android:fontFamily">@font/roboto_medium</item>
</style>

<style name="TextAppearance.BigTime.Body">
    <item name="android:textSize">16sp</item>
    <item name="android:fontFamily">@font/roboto_regular</item>
</style>
```

### Spacing & Layout
- Use 8dp grid system
- Standard margins: 8dp, 16dp, 24dp, 32dp
- Card elevation: 2dp-8dp
- Touch targets: minimum 48dp

## Database Conventions

### Entity Design
```kotlin
@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "title")
    val title: String,
    
    @ColumnInfo(name = "event_date")
    val date: Date,
    
    @ColumnInfo(name = "category_id")
    val categoryId: Long,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Date = Date()
)
```

### DAO Methods
```kotlin
@Dao
interface EventDao {
    // Queries return Flow for reactivity
    @Query("SELECT * FROM events ORDER BY event_date")
    fun getAllEvents(): Flow<List<Event>>
    
    // Operations are suspend functions
    @Insert
    suspend fun insert(event: Event)
    
    @Update
    suspend fun update(event: Event)
    
    @Delete
    suspend fun delete(event: Event)
}
```

## Testing Conventions

### Test File Naming
```
// Unit tests
EventViewModelTest.kt
AppRepositoryTest.kt

// Instrumented tests
EventDaoTest.kt
MainActivityTest.kt
```

### Test Structure
```kotlin
class EventViewModelTest {
    @Before
    fun setup() {
        // Setup test environment
    }
    
    @Test
    fun `when event is created then it appears in list`() {
        // Given
        val event = createTestEvent()
        
        // When
        viewModel.createEvent(event)
        
        // Then
        assertTrue(viewModel.events.value?.contains(event) == true)
    }
    
    @After
    fun tearDown() {
        // Cleanup
    }
}
```

## Documentation Standards

### Code Comments
```kotlin
/**
 * Calculates the number of days between the event date and today.
 * 
 * @param eventDate The date of the event
 * @return Number of days (positive for future, negative for past)
 */
fun calculateDaysDifference(eventDate: Date): Int {
    // Implementation
}

// Use single-line comments for clarification
val daysDiff = calculateDaysDifference(event.date) // Can be negative
```

### TODO Comments
```kotlin
// TODO: Implement error handling
// FIXME: Memory leak in adapter
// NOTE: This requires API 26+
```

## Git Conventions

### Commit Messages
```
feat: Add event deletion functionality
fix: Resolve crash when creating event without title
refactor: Extract event validation logic
docs: Update README with build instructions
test: Add unit tests for EventViewModel
style: Format code according to style guide
```

### Branch Naming
```
feature/add-reminder-notifications
bugfix/category-filter-not-working
refactor/mvvm-architecture
release/v1.2.0
```

## Error Handling

### Exception Handling
```kotlin
// Use Result pattern for operations that can fail
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}

// Handle exceptions gracefully
viewModelScope.launch {
    try {
        val events = repository.getEvents()
        _uiState.value = UiState.Success(events)
    } catch (e: Exception) {
        _uiState.value = UiState.Error(e.message)
        Log.e(TAG, "Error loading events", e)
    }
}
```

### User Feedback
```kotlin
// Provide meaningful error messages
when (error) {
    is NetworkError -> showSnackbar("No internet connection")
    is DatabaseError -> showSnackbar("Failed to save event")
    else -> showSnackbar("Something went wrong")
}
```

## Performance Guidelines

### Memory Management
- Use ViewBinding instead of findViewById
- Clear references in onDestroyView
- Avoid memory leaks with WeakReference
- Use efficient data structures

### Database Optimization
- Add indexes for frequently queried columns
- Use transactions for bulk operations
- Limit query results when possible
- Cache frequently accessed data

### UI Performance
- Use RecyclerView for lists
- Implement DiffUtil for efficient updates
- Load images asynchronously
- Avoid nested layouts when possible

## Accessibility

### Content Descriptions
```xml
<ImageButton
    android:id="@+id/btnDelete"
    android:contentDescription="@string/delete_event"
    android:importantForAccessibility="yes" />
```

### Touch Targets
- Minimum 48dp x 48dp for interactive elements
- Adequate spacing between clickable items
- Clear visual feedback for interactions

## Localization

### String Resources
```xml
<!-- Always use string resources -->
<!-- ❌ Wrong -->
android:text="Save"

<!-- ✅ Correct -->
android:text="@string/button_save"
```

### Date Formatting
```kotlin
// Use locale-aware formatting
val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
val formattedDate = dateFormat.format(event.date)
```

## Security Guidelines

### Data Protection
- Never log sensitive information
- Validate all user inputs
- Use parameterized queries
- Implement proper authentication (when needed)

### Permissions
- Request minimum necessary permissions
- Check permissions at runtime
- Provide rationale for permission requests
- Handle permission denial gracefully