package com.coquankedian.bigtime.ui.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.coquankedian.bigtime.R
import com.coquankedian.bigtime.data.model.Category
import com.coquankedian.bigtime.data.model.Event
import com.coquankedian.bigtime.data.repository.AppRepository
import com.coquankedian.bigtime.databinding.DialogAddEditEventBinding
import com.coquankedian.bigtime.ui.CategoryViewModel
import com.coquankedian.bigtime.ui.CategoryViewModelFactory
import com.coquankedian.bigtime.ui.EventViewModel
import com.coquankedian.bigtime.ui.EventViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class AddEditEventDialog : DialogFragment() {

    private var _binding: DialogAddEditEventBinding? = null
    private val binding get() = _binding!!

    private var selectedDate: Date = Date()
    private var selectedCategoryId: Long = 1L // Default category
    private var selectedCardBackgroundColor: Int = android.graphics.Color.WHITE
    private var selectedTextColor: Int = android.graphics.Color.BLACK
    private var selectedIconResId: Int = android.R.drawable.ic_menu_my_calendar
    private var editingEvent: Event? = null

    // TODO: Use proper dependency injection
    private val eventViewModel: EventViewModel by lazy {
        val database = com.coquankedian.bigtime.data.database.AppDatabase.getDatabase(requireContext(), kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO))
        val repository = com.coquankedian.bigtime.data.repository.AppRepository(database.eventDao(), database.categoryDao())
        com.coquankedian.bigtime.ui.EventViewModel(repository)
    }

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
        _binding = DialogAddEditEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Check if we're editing an existing event
        editingEvent = arguments?.getSerializable("event") as? com.coquankedian.bigtime.data.model.Event

        setupUI()
        setupDatePicker()
        setupCategorySpinner()
        setupButtons()
        loadEventDataIfEditing()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun setupUI() {
        binding.tvDialogTitle.text = if (editingEvent == null) "添加新事件" else "编辑事件"
    }

    private fun setupDatePicker() {
        updateDateDisplay()

        binding.btnSelectDate.setOnClickListener {
            val datePicker = DatePickerDialog.newInstance()
            datePicker.setOnDateSelectedListener { year, month, dayOfMonth ->
                val calendar = Calendar.getInstance()
                calendar.set(year, month, dayOfMonth)
                selectedDate = calendar.time
                updateDateDisplay()
            }
            datePicker.show(childFragmentManager, "DatePicker")
        }
    }

    private fun updateDateDisplay() {
        val dateFormat = SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault())
        binding.tvSelectedDate.text = dateFormat.format(selectedDate)
    }

    private fun setupCategorySpinner() {
        categoryViewModel.allCategories.observe(viewLifecycleOwner) { categories ->
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categories.map { it.name }
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerCategory.adapter = adapter

            // Set default selection
            val defaultIndex = categories.indexOfFirst { it.id == selectedCategoryId }
            if (defaultIndex >= 0) {
                binding.spinnerCategory.setSelection(defaultIndex)
            }
        }
    }

    private fun setupButtons() {
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnSave.setOnClickListener {
            saveEvent()
        }

        binding.btnSelectTheme.setOnClickListener {
            showThemePicker()
        }
    }

    private fun loadEventDataIfEditing() {
        editingEvent?.let { event ->
            binding.etEventTitle.setText(event.title)
            binding.etEventDescription.setText(event.description)
            selectedDate = event.date
            selectedCategoryId = event.categoryId
            selectedCardBackgroundColor = event.cardBackgroundColor
            selectedTextColor = event.textColor
            event.iconResId?.let { selectedIconResId = it }
            updateDateDisplay()
        }
    }

    private fun showThemePicker() {
        val themePicker = ThemePickerDialog.newInstance()
        themePicker.setOnThemeSelectedListener { cardBackgroundColor, textColor, iconResId ->
            selectedCardBackgroundColor = cardBackgroundColor
            selectedTextColor = textColor
            selectedIconResId = iconResId
        }
        themePicker.show(childFragmentManager, "ThemePickerDialog")
    }

    private fun saveEvent() {
        val title = binding.etEventTitle.text.toString().trim()
        val description = binding.etEventDescription.text.toString().trim()

        if (title.isEmpty()) {
            Toast.makeText(requireContext(), "请输入事件标题", Toast.LENGTH_SHORT).show()
            return
        }

        // Get selected category
        val selectedPosition = binding.spinnerCategory.selectedItemPosition
        categoryViewModel.allCategories.value?.let { categories ->
            if (selectedPosition < categories.size) {
                selectedCategoryId = categories[selectedPosition].id
            }
        }

        val event = Event(
            id = editingEvent?.id ?: 0,
            title = title,
            description = description,
            date = selectedDate,
            categoryId = selectedCategoryId,
            cardBackgroundColor = selectedCardBackgroundColor,
            textColor = selectedTextColor,
            iconResId = selectedIconResId
        )

        if (editingEvent == null) {
            eventViewModel.insertEvent(event)
            Toast.makeText(requireContext(), "事件已添加", Toast.LENGTH_SHORT).show()
        } else {
            eventViewModel.updateEvent(event)
            Toast.makeText(requireContext(), "事件已更新", Toast.LENGTH_SHORT).show()
        }

        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(event: Event? = null) = AddEditEventDialog().apply {
            arguments = Bundle().apply {
                event?.let { putSerializable("event", it as java.io.Serializable) }
            }
        }
    }
}
