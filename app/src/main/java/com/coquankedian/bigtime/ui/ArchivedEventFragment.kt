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
import com.coquankedian.bigtime.databinding.FragmentArchivedEventBinding
import com.coquankedian.bigtime.ui.adapter.EventAdapter
import com.coquankedian.bigtime.ui.EventViewModel
import com.coquankedian.bigtime.ui.EventViewModelFactory
import kotlinx.coroutines.launch

class ArchivedEventFragment : Fragment() {

    private var _binding: FragmentArchivedEventBinding? = null
    private val binding get() = _binding!!

    private lateinit var eventAdapter: EventAdapter

    // TODO: Use proper dependency injection
    private val eventViewModel: EventViewModel by lazy {
        val database = com.coquankedian.bigtime.data.database.AppDatabase.getDatabase(requireContext(), kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO))
        val repository = com.coquankedian.bigtime.data.repository.AppRepository(database.eventDao(), database.categoryDao())
        com.coquankedian.bigtime.ui.EventViewModel(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArchivedEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSwipeRefresh()
        observeArchivedEvents()
    }

    private fun setupRecyclerView() {
        eventAdapter = com.coquankedian.bigtime.ui.adapter.EventAdapter(
            onEventClick = { event ->
                showArchivedEventOptions(event)
            },
            onEventLongClick = { event ->
                showEventMenu(event)
            }
        )

        binding.recyclerViewArchivedEvents.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
            adapter = eventAdapter
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            observeArchivedEvents()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun observeArchivedEvents() {
        eventViewModel.archivedEvents.observe(viewLifecycleOwner) { events ->
            updateEventList(events)
        }
    }

    private fun updateEventList(events: List<Event>) {
        eventAdapter.submitList(events)

        if (events.isEmpty()) {
            binding.recyclerViewArchivedEvents.visibility = android.view.View.GONE
            binding.layoutEmptyState.root.visibility = android.view.View.VISIBLE
        } else {
            binding.recyclerViewArchivedEvents.visibility = android.view.View.VISIBLE
            binding.layoutEmptyState.root.visibility = android.view.View.GONE
        }
    }

    private fun showArchivedEventOptions(event: Event) {
        // For archived events, clicking shows restore options
        showEventMenu(event)
    }

    private fun showEventMenu(event: Event) {
        val dialog = com.coquankedian.bigtime.ui.dialog.EventMenuDialog.newInstance(event)
        dialog.show(childFragmentManager, "EventMenuDialog")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = ArchivedEventFragment()
    }
}
