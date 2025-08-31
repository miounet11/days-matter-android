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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.coquankedian.bigtime.R
import com.coquankedian.bigtime.data.model.Event
import com.coquankedian.bigtime.data.repository.AppRepository
import com.coquankedian.bigtime.databinding.FragmentEventListBinding
import com.coquankedian.bigtime.ui.adapter.EventAdapter
import com.coquankedian.bigtime.ui.EventViewModel
import com.coquankedian.bigtime.ui.EventViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class EventListFragment : Fragment() {

    private var _binding: com.coquankedian.bigtime.databinding.FragmentEventListBinding? = null
    private val binding get() = _binding!!

    private lateinit var eventAdapter: EventAdapter

    // TODO: Use proper dependency injection
    private val eventViewModel: EventViewModel by lazy {
        // For now, create a simple implementation
        val database = com.coquankedian.bigtime.data.database.AppDatabase.getDatabase(requireContext(), kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO))
        val repository = com.coquankedian.bigtime.data.repository.AppRepository(database.eventDao(), database.categoryDao())
        com.coquankedian.bigtime.ui.EventViewModel(repository)
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
        eventAdapter = com.coquankedian.bigtime.ui.adapter.EventAdapter(
            onEventClick = { event ->
                showEventDetails(event)
            },
            onEventLongClick = { event ->
                showEventMenu(event)
            },
            getCategoryName = { categoryId ->
                getCategoryName(categoryId)
            }
        )

        binding.recyclerViewEvents.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
            adapter = eventAdapter
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
        eventViewModel.allEvents.observe(viewLifecycleOwner) { events ->
            updateEventList(events)
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
        val dialog = com.coquankedian.bigtime.ui.dialog.AddEditEventDialog.newInstance(event)
        dialog.show(childFragmentManager, "EditEventDialog")
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
