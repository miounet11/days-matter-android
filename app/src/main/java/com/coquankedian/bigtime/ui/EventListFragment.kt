package com.coquankedian.bigtime.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.core.view.ViewCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.lifecycle.LiveData
import com.coquankedian.bigtime.R
import com.coquankedian.bigtime.data.model.Event
import com.coquankedian.bigtime.data.repository.AppRepository
import com.coquankedian.bigtime.databinding.FragmentEventListBinding
import com.coquankedian.bigtime.ui.adapter.EventDaysMatterAdapter
import com.coquankedian.bigtime.ui.EventViewModel
import com.coquankedian.bigtime.ui.EventViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class EventListFragment : Fragment() {

    private var _binding: com.coquankedian.bigtime.databinding.FragmentEventListBinding? = null
    private val binding get() = _binding!!

    private lateinit var eventAdapter: EventDaysMatterAdapter
    private var isGridView = false
    private var currentSearchQuery: String? = null
    private var isSearching = false

    private val eventViewModel: EventViewModel by viewModels {
        val database = com.coquankedian.bigtime.data.database.AppDatabase.getDatabase(
            requireContext(),
            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO)
        )
        val repository = com.coquankedian.bigtime.data.repository.AppRepository(
            database.eventDao(),
            database.categoryDao()
        )
        EventViewModelFactory(repository, requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSwipeRefresh()
        observeEvents()
    }

    private fun setupRecyclerView() {
        eventAdapter = com.coquankedian.bigtime.ui.adapter.EventDaysMatterAdapter(
            onEventClick = { event ->
                showEventDetails(event)
            },
            onEventLongClick = { event ->
                showEventMenu(event)
            }
        )

        binding.recyclerViewEvents.apply {
            layoutManager = if (isGridView) {
                GridLayoutManager(requireContext(), 2)
            } else {
                LinearLayoutManager(requireContext())
            }
            adapter = eventAdapter
        }
    }
    
    fun setViewMode(gridView: Boolean) {
        isGridView = gridView
        binding.recyclerViewEvents.layoutManager = if (isGridView) {
            GridLayoutManager(requireContext(), 2)
        } else {
            LinearLayoutManager(requireContext())
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            // Refresh data
            observeEvents()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun observeEvents() {
        // Observe filtered events instead of all events
        eventViewModel.filteredEvents.observe(viewLifecycleOwner) { events ->
            updateEventList(events)
        }
        
        // Also observe pinned events for the top card
        eventViewModel.pinnedEvents.observe(viewLifecycleOwner) { pinnedEvents ->
            updatePinnedEventCard(pinnedEvents.firstOrNull())
        }
    }
    
    fun filterByCategory(categoryId: Long?, pinnedOnly: Boolean) {
        if (isSearching) return // Don't change filter during search
        
        if (pinnedOnly) {
            // Show only pinned events
            eventViewModel.pinnedEvents.observe(viewLifecycleOwner) { events ->
                updateEventList(events)
            }
        } else {
            // Apply category filter
            eventViewModel.setCategoryFilter(categoryId)
        }
    }
    
    fun searchEvents(query: String) {
        currentSearchQuery = query
        isSearching = true
        
        eventViewModel.searchEvents(query).observe(viewLifecycleOwner) { events ->
            updateEventList(events)
            // Hide pinned event card during search
            binding.pinnedEventCard.root.visibility = android.view.View.GONE
        }
    }
    
    fun clearSearch() {
        currentSearchQuery = null
        isSearching = false
        
        // Return to normal event observation
        observeEvents()
    }
    
    private fun updatePinnedEventCard(event: Event?) {
        if (event != null && event.isPinned) {
            // Show pinned event card
            binding.pinnedEventCard.root.visibility = android.view.View.VISIBLE
            
            // Update pinned event card content
            binding.pinnedEventCard.tvPinnedTitle.text = event.title
            binding.pinnedEventCard.tvPinnedDaysNumber.text = Math.abs(event.daysUntil).toString()
            
            // Update days label based on whether it's future or past
            val daysLabel = when {
                event.daysUntil > 0 -> "天后"
                event.daysUntil < 0 -> "天前"
                else -> "今天"
            }
            binding.pinnedEventCard.tvPinnedDaysLabel.text = daysLabel
            
            // Format and display date
            val dateFormat = java.text.SimpleDateFormat("yyyy年MM月dd日", java.util.Locale.CHINA)
            binding.pinnedEventCard.tvPinnedDate.text = dateFormat.format(event.date)
            
            // Show description if available
            if (!event.description.isNullOrEmpty()) {
                binding.pinnedEventCard.tvPinnedDescription.visibility = android.view.View.VISIBLE
                binding.pinnedEventCard.tvPinnedDescription.text = event.description
            } else {
                binding.pinnedEventCard.tvPinnedDescription.visibility = android.view.View.GONE
            }
            
            // Set category name
            binding.pinnedEventCard.tvPinnedCategory.text = getCategoryName(event.categoryId)
            
            // Set card click listener
            binding.pinnedEventCard.root.setOnClickListener {
                showEventDetails(event)
            }
        } else {
            // Hide pinned event card if no pinned event
            binding.pinnedEventCard.root.visibility = android.view.View.GONE
        }
    }

    private fun updateEventList(events: List<Event>) {
        eventAdapter.submitList(events)

        if (events.isEmpty()) {
            binding.recyclerViewEvents.visibility = android.view.View.GONE
            binding.layoutEmptyState.root.visibility = android.view.View.VISIBLE
        } else {
            binding.recyclerViewEvents.visibility = android.view.View.VISIBLE
            binding.layoutEmptyState.root.visibility = android.view.View.GONE
        }
    }

    private fun showEventDetails(event: Event) {
        // Open full screen event detail activity
        val intent = android.content.Intent(requireContext(), EventDetailActivity::class.java).apply {
            putExtra(EventDetailActivity.EXTRA_EVENT_ID, event.id)
        }
        startActivity(intent)
    }

    private fun showEventMenu(event: Event) {
        val dialog = com.coquankedian.bigtime.ui.dialog.EventMenuDialog.newInstance(event)
        dialog.show(childFragmentManager, "EventMenuDialog")
    }

    private fun getCategoryName(categoryId: Long): String {
        // TODO: Implement proper category lookup
        // For now, return default names based on ID
        return when (categoryId) {
            1L -> "纪念日"
            2L -> "工作"
            3L -> "生活"
            else -> "自定义"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = EventListFragment()
    }
}
