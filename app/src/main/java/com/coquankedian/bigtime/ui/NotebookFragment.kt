package com.coquankedian.bigtime.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.GridLayoutManager
import com.coquankedian.bigtime.databinding.FragmentNotebookBinding
import com.coquankedian.bigtime.ui.adapter.NotebookAdapter

class NotebookFragment : Fragment() {

    private var _binding: FragmentNotebookBinding? = null
    private val binding get() = _binding!!

    private lateinit var notebookAdapter: NotebookAdapter
    private var isGridView = true

    private val notebookViewModel: NotebookViewModel by viewModels {
        val database = com.coquankedian.bigtime.data.database.AppDatabase.getDatabase(
            requireContext(),
            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO)
        )
        val repository = com.coquankedian.bigtime.data.repository.AppRepository(
            database.eventDao(),
            database.categoryDao(),
            database.notebookDao()
        )
        NotebookViewModelFactory(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotebookBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSwipeRefresh()
        setupFab()
        observeNotebooks()
    }

    private fun setupRecyclerView() {
        notebookAdapter = NotebookAdapter(
            onNotebookClick = { notebook ->
                openNotebook(notebook)
            },
            onNotebookLongClick = { notebook ->
                showNotebookMenu(notebook)
            },
            onNotebookEdit = { notebook ->
                editNotebook(notebook)
            }
        )

        binding.recyclerViewNotebooks.apply {
            layoutManager = if (isGridView) {
                GridLayoutManager(requireContext(), 2)
            } else {
                LinearLayoutManager(requireContext())
            }
            adapter = notebookAdapter
        }
    }

    fun setViewMode(gridView: Boolean) {
        isGridView = gridView
        binding.recyclerViewNotebooks.layoutManager = if (isGridView) {
            GridLayoutManager(requireContext(), 2)
        } else {
            LinearLayoutManager(requireContext())
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            observeNotebooks()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun setupFab() {
        binding.fabAddNotebook.setOnClickListener {
            showAddNotebookDialog()
        }
    }

    private fun observeNotebooks() {
        notebookViewModel.allNotebooks.observe(viewLifecycleOwner) { notebooks ->
            updateNotebookList(notebooks)
        }
    }

    private fun updateNotebookList(notebooks: List<com.coquankedian.bigtime.data.model.Notebook>) {
        notebookAdapter.submitList(notebooks)

        if (notebooks.isEmpty()) {
            binding.recyclerViewNotebooks.visibility = View.GONE
            binding.layoutEmptyState.root.visibility = View.VISIBLE
        } else {
            binding.recyclerViewNotebooks.visibility = View.VISIBLE
            binding.layoutEmptyState.root.visibility = View.GONE
        }
    }

    private fun openNotebook(notebook: com.coquankedian.bigtime.data.model.Notebook) {
        // Navigate to events filtered by notebook
        val activity = requireActivity()
        if (activity is com.coquankedian.bigtime.MainActivity) {
            // Switch to events tab and filter by notebook
            activity.filterEventsByNotebook(notebook.id)
        }
    }

    private fun showNotebookMenu(notebook: com.coquankedian.bigtime.data.model.Notebook) {
        val dialog = com.coquankedian.bigtime.ui.dialog.NotebookMenuDialog.newInstance(notebook)
        dialog.show(childFragmentManager, "NotebookMenuDialog")
    }

    private fun editNotebook(notebook: com.coquankedian.bigtime.data.model.Notebook) {
        val dialog = com.coquankedian.bigtime.ui.dialog.AddEditNotebookDialog.newInstance(notebook)
        dialog.show(childFragmentManager, "AddEditNotebookDialog")
    }

    private fun showAddNotebookDialog() {
        val dialog = com.coquankedian.bigtime.ui.dialog.AddEditNotebookDialog.newInstance()
        dialog.show(childFragmentManager, "AddEditNotebookDialog")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = NotebookFragment()
    }
}