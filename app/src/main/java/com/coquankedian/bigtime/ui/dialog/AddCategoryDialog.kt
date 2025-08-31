package com.coquankedian.bigtime.ui.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.coquankedian.bigtime.R
import com.coquankedian.bigtime.data.model.Category
import com.coquankedian.bigtime.data.repository.AppRepository
import com.coquankedian.bigtime.databinding.DialogAddCategoryBinding
import com.coquankedian.bigtime.ui.CategoryViewModel
import com.coquankedian.bigtime.ui.CategoryViewModelFactory

class AddCategoryDialog : DialogFragment() {

    private var _binding: DialogAddCategoryBinding? = null
    private val binding get() = _binding!!

    private var selectedColor: Int = Color.parseColor("#FF6B9D") // Default color
    private var selectedIconResId: Int = android.R.drawable.ic_menu_my_calendar // Default icon

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
        _binding = DialogAddCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupColorPicker()
        setupIconPicker()
        setupButtons()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun setupColorPicker() {
        val colors = listOf(
            Color.parseColor("#FF6B9D"), // Pink
            Color.parseColor("#4ECDC4"), // Teal
            Color.parseColor("#45B7D1"), // Blue
            Color.parseColor("#96CEB4"), // Green
            Color.parseColor("#FFEAA7"), // Yellow
            Color.parseColor("#DDA0DD"), // Plum
            Color.parseColor("#98D8C8"), // Mint
            Color.parseColor("#F7DC6F"), // Light Yellow
            Color.parseColor("#BB8FCE"), // Light Purple
        )

        binding.colorPicker.removeAllViews()
        colors.forEach { color ->
            val colorView = View(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(50, 50)
                setBackgroundColor(color)
                setPadding(4, 4, 4, 4)
                setOnClickListener {
                    selectedColor = color
                    updateSelectedColorPreview()
                }
            }
            binding.colorPicker.addView(colorView)
        }

        updateSelectedColorPreview()
    }

    private fun setupIconPicker() {
        val icons = listOf(
            android.R.drawable.ic_menu_my_calendar,
            android.R.drawable.ic_menu_agenda,
            android.R.drawable.ic_menu_compass,
            android.R.drawable.ic_menu_camera,
            android.R.drawable.ic_menu_gallery,
            android.R.drawable.ic_menu_help,
            android.R.drawable.ic_menu_info_details,
            android.R.drawable.ic_menu_manage,
            android.R.drawable.ic_menu_preferences
        )

        binding.iconPicker.removeAllViews()
        icons.forEach { iconResId ->
            val iconView = View(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(50, 50)
                setBackgroundResource(R.drawable.category_chip_background)
                setOnClickListener {
                    selectedIconResId = iconResId
                    updateSelectedIconPreview()
                }
            }
            binding.iconPicker.addView(iconView)
        }

        updateSelectedIconPreview()
    }

    private fun updateSelectedColorPreview() {
        binding.colorPreview.setBackgroundColor(selectedColor)
    }

    private fun updateSelectedIconPreview() {
        binding.iconPreview.setImageResource(selectedIconResId)
    }

    private fun setupButtons() {
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnSave.setOnClickListener {
            saveCategory()
        }
    }

    private fun saveCategory() {
        val categoryName = binding.etCategoryName.text.toString().trim()

        if (categoryName.isEmpty()) {
            Toast.makeText(requireContext(), "请输入分类名称", Toast.LENGTH_SHORT).show()
            return
        }

        val newCategory = Category(
            name = categoryName,
            color = selectedColor,
            iconResId = selectedIconResId,
            isDefault = false,
            isCustom = true
        )

        categoryViewModel.insertCategory(newCategory)
        Toast.makeText(requireContext(), "分类已添加", Toast.LENGTH_SHORT).show()
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = AddCategoryDialog()
    }
}
