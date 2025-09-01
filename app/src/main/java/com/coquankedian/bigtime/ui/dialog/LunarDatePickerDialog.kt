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
import com.coquankedian.bigtime.databinding.DialogLunarDatePickerBinding
import com.coquankedian.bigtime.utils.LunarCalendar
import java.util.*

class LunarDatePickerDialog : DialogFragment() {

    private var _binding: DialogLunarDatePickerBinding? = null
    private val binding get() = _binding!!

    private var listener: ((date: Date) -> Unit)? = null
    private var selectedLunarYear = 2024
    private var selectedLunarMonth = 1
    private var selectedLunarDay = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogLunarDatePickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpinners()
        setupButtons()
        setDefaultDate()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun setupSpinners() {
        // Year spinner (2020-2030)
        val years = (2020..2030).toList()
        val yearAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            years.map { "${it}年" }
        )
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerLunarYear.adapter = yearAdapter

        // Month spinner
        val months = (1..12).toList()
        val monthAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            months.map { LunarCalendar.getLunarMonthName(it) }
        )
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerLunarMonth.adapter = monthAdapter

        // Day spinner
        val days = (1..30).toList()
        val dayAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            days.map { LunarCalendar.getLunarDayName(it) }
        )
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerLunarDay.adapter = dayAdapter
    }

    private fun setupButtons() {
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnConfirm.setOnClickListener {
            selectedLunarYear = (2020..2030).toList()[binding.spinnerLunarYear.selectedItemPosition]
            selectedLunarMonth = binding.spinnerLunarMonth.selectedItemPosition + 1
            selectedLunarDay = binding.spinnerLunarDay.selectedItemPosition + 1

            try {
                val solarDate = LunarCalendar.lunarToSolar(selectedLunarYear, selectedLunarMonth, selectedLunarDay)
                listener?.invoke(solarDate)
                dismiss()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "日期转换失败", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnToday.setOnClickListener {
            setTodayLunarDate()
        }
    }

    private fun setDefaultDate() {
        val currentDate = Date()
        val lunarDate = LunarCalendar.solarToLunar(currentDate)
        
        selectedLunarYear = lunarDate.year
        selectedLunarMonth = lunarDate.month
        selectedLunarDay = lunarDate.day
        
        updateSpinnerSelections()
    }

    private fun setTodayLunarDate() {
        val today = Date()
        val lunarDate = LunarCalendar.solarToLunar(today)
        
        selectedLunarYear = lunarDate.year
        selectedLunarMonth = lunarDate.month
        selectedLunarDay = lunarDate.day
        
        updateSpinnerSelections()
    }

    private fun updateSpinnerSelections() {
        val yearIndex = selectedLunarYear - 2020
        if (yearIndex in 0..10) {
            binding.spinnerLunarYear.setSelection(yearIndex)
        }
        
        if (selectedLunarMonth in 1..12) {
            binding.spinnerLunarMonth.setSelection(selectedLunarMonth - 1)
        }
        
        if (selectedLunarDay in 1..30) {
            binding.spinnerLunarDay.setSelection(selectedLunarDay - 1)
        }
    }

    fun setOnDateSelectedListener(listener: (date: Date) -> Unit) {
        this.listener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = LunarDatePickerDialog()
    }
}