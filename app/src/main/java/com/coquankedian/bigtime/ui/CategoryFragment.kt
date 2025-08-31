package com.coquankedian.bigtime.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.core.view.ViewCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.coquankedian.bigtime.R
import com.coquankedian.bigtime.data.model.Category
import com.coquankedian.bigtime.data.repository.AppRepository
import com.coquankedian.bigtime.databinding.FragmentCategoryBinding
import com.coquankedian.bigtime.ui.adapter.CategoryAdapter
import com.coquankedian.bigtime.ui.CategoryViewModel
import com.coquankedian.bigtime.ui.CategoryViewModelFactory
import kotlinx.coroutines.launch

class CategoryFragment : Fragment() {

    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var categoryAdapter: CategoryAdapter

    // TODO: Use proper dependency injection
    private val categoryViewModel: CategoryViewModel by lazy {
        val database = com.coquankedian.bigtime.data.database.AppDatabase.getDatabase(requireContext(), kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO))
        val repository = com.coquankedian.bigtime.data.repository.AppRepository(database.eventDao(), database.categoryDao())
        com.coquankedian.bigtime.ui.CategoryViewModel(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupFab()
        observeCategories()
    }

    private fun setupRecyclerView() {
        categoryAdapter = CategoryAdapter { category ->
            // Handle category click - show events in this category
            showEventsInCategory(category)
        }

        binding.recyclerViewCategories.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = categoryAdapter
        }
    }

    private fun observeCategories() {
        categoryViewModel.allCategories.observe(viewLifecycleOwner) { categories ->
            categoryAdapter.submitList(categories)
        }
    }

    private fun setupFab() {
        binding.fabAddCategory.setOnClickListener {
            showAddCategoryDialog()
        }
    }

    private fun showEventsInCategory(category: Category) {
        // TODO: Navigate to events in this category
    }

    private fun showAddCategoryDialog() {
        val dialog = com.coquankedian.bigtime.ui.dialog.AddCategoryDialog.newInstance()
        dialog.show(childFragmentManager, "AddCategoryDialog")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = CategoryFragment()
    }
}
