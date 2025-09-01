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
import com.coquankedian.bigtime.utils.LunarCalendar
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
    private var selectedRepeatType: String = "NONE"
    private var reminderEnabled: Boolean = false
    private var reminderMinutes: Int = 1440 // Default 1 day before
    private var isLunarCalendar: Boolean = false
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
        setupRepeatSpinner()
        setupReminderControls()
        setupCalendarTypeControls()
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
            if (isLunarCalendar) {
                val lunarDatePicker = LunarDatePickerDialog.newInstance()
                lunarDatePicker.setOnDateSelectedListener { date ->
                    selectedDate = date
                    updateDateDisplay()
                }
                lunarDatePicker.show(childFragmentManager, "LunarDatePicker")
            } else {
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
    }

    private fun updateDateDisplay() {
        if (isLunarCalendar) {
            val lunarDate = LunarCalendar.solarToLunar(selectedDate)
            binding.tvSelectedDate.text = LunarCalendar.formatLunarDate(lunarDate)
        } else {
            val dateFormat = SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault())
            binding.tvSelectedDate.text = dateFormat.format(selectedDate)
        }
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

    private fun setupRepeatSpinner() {
        val repeatOptions = arrayOf("不重复", "每日", "每周", "每月", "每年")
        val repeatValues = arrayOf("NONE", "DAILY", "WEEKLY", "MONTHLY", "YEARLY")
        
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            repeatOptions
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerRepeat.adapter = adapter
        
        val defaultIndex = repeatValues.indexOf(selectedRepeatType)
        if (defaultIndex >= 0) {
            binding.spinnerRepeat.setSelection(defaultIndex)
        }
    }

    private fun setupReminderControls() {
        val reminderOptions = arrayOf("提前1小时", "提前1天", "提前1周", "提前1月")
        val reminderValues = arrayOf(60, 1440, 10080, 43200) // minutes
        
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            reminderOptions
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerReminderTime.adapter = adapter
        
        binding.switchReminder.isChecked = reminderEnabled
        binding.spinnerReminderTime.isEnabled = reminderEnabled
        
        val defaultIndex = reminderValues.indexOf(reminderMinutes)
        if (defaultIndex >= 0) {
            binding.spinnerReminderTime.setSelection(defaultIndex)
        }
        
        binding.switchReminder.setOnCheckedChangeListener { _, isChecked ->
            reminderEnabled = isChecked
            binding.spinnerReminderTime.isEnabled = isChecked
        }
    }

    private fun setupCalendarTypeControls() {
        binding.rbSolarCalendar.isChecked = !isLunarCalendar
        binding.rbLunarCalendar.isChecked = isLunarCalendar
        
        binding.rgCalendarType.setOnCheckedChangeListener { _, checkedId ->
            isLunarCalendar = checkedId == R.id.rbLunarCalendar
            updateDateDisplay() // Update date display based on calendar type
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
            selectedRepeatType = event.repeatType
            reminderEnabled = event.reminderEnabled
            reminderMinutes = event.reminderMinutes
            isLunarCalendar = event.isLunarCalendar
            
            // Update UI with loaded values
            updateDateDisplay()
            setupRepeatSpinner()
            setupReminderControls()
            setupCalendarTypeControls()
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

        // Get selected repeat type
        val repeatOptions = arrayOf("NONE", "DAILY", "WEEKLY", "MONTHLY", "YEARLY")
        val repeatPosition = binding.spinnerRepeat.selectedItemPosition
        if (repeatPosition in repeatOptions.indices) {
            selectedRepeatType = repeatOptions[repeatPosition]
        }

        // Get selected reminder time
        if (reminderEnabled) {
            val reminderValues = arrayOf(60, 1440, 10080, 43200) // minutes
            val reminderPosition = binding.spinnerReminderTime.selectedItemPosition
            if (reminderPosition in reminderValues.indices) {
                reminderMinutes = reminderValues[reminderPosition]
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
            iconResId = selectedIconResId,
            repeatType = selectedRepeatType,
            reminderEnabled = reminderEnabled,
            reminderMinutes = reminderMinutes,
            isLunarCalendar = isLunarCalendar
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
