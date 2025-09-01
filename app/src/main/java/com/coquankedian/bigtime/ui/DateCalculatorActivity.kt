package com.coquankedian.bigtime.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.coquankedian.bigtime.databinding.ActivityDateCalculatorBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class DateCalculatorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDateCalculatorBinding
    private var startDate: Date = Date()
    private var endDate: Date = Date()
    private val dateFormat = SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDateCalculatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupDatePickers()
        calculateDifference()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "日期计算器"
    }

    private fun setupDatePickers() {
        updateDateDisplays()

        binding.btnSelectStartDate.setOnClickListener {
            showDatePickerDialog(true)
        }

        binding.btnSelectEndDate.setOnClickListener {
            showDatePickerDialog(false)
        }

        binding.btnSwapDates.setOnClickListener {
            swapDates()
        }

        binding.btnToday.setOnClickListener {
            setToday()
        }
    }

    private fun showDatePickerDialog(isStartDate: Boolean) {
        val calendar = Calendar.getInstance()
        val currentDate = if (isStartDate) startDate else endDate
        calendar.time = currentDate

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val newCalendar = Calendar.getInstance()
                newCalendar.set(year, month, dayOfMonth)
                
                if (isStartDate) {
                    startDate = newCalendar.time
                } else {
                    endDate = newCalendar.time
                }
                
                updateDateDisplays()
                calculateDifference()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }

    private fun updateDateDisplays() {
        binding.tvStartDate.text = dateFormat.format(startDate)
        binding.tvEndDate.text = dateFormat.format(endDate)
    }

    private fun swapDates() {
        val temp = startDate
        startDate = endDate
        endDate = temp
        updateDateDisplays()
        calculateDifference()
    }

    private fun setToday() {
        val today = Date()
        startDate = today
        endDate = today
        updateDateDisplays()
        calculateDifference()
    }

    private fun calculateDifference() {
        val diffInMillis = endDate.time - startDate.time
        val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis)
        val absDiffInDays = kotlin.math.abs(diffInDays)

        // Calculate years, months, weeks, and days
        val calendar1 = Calendar.getInstance().apply { time = startDate }
        val calendar2 = Calendar.getInstance().apply { time = endDate }

        val years = kotlin.math.abs(calendar2.get(Calendar.YEAR) - calendar1.get(Calendar.YEAR))
        val months = kotlin.math.abs(calendar2.get(Calendar.MONTH) - calendar1.get(Calendar.MONTH))
        val weeks = absDiffInDays / 7
        val remainingDays = absDiffInDays % 7

        // Update result displays
        binding.tvTotalDays.text = "${absDiffInDays}天"
        binding.tvTotalWeeks.text = "${weeks}周 ${remainingDays}天"
        binding.tvTotalHours.text = "${absDiffInDays * 24}小时"
        binding.tvTotalMinutes.text = "${absDiffInDays * 24 * 60}分钟"

        // Calculate approximate months and years
        val approximateMonths = absDiffInDays / 30.44 // Average days per month
        val approximateYears = absDiffInDays / 365.25 // Average days per year

        binding.tvApproximateMonths.text = String.format("约%.1f个月", approximateMonths)
        binding.tvApproximateYears.text = String.format("约%.1f年", approximateYears)

        // Show direction
        val direction = when {
            diffInDays > 0 -> "从起始日期到结束日期"
            diffInDays < 0 -> "从结束日期到起始日期"
            else -> "相同日期"
        }
        binding.tvDirection.text = direction

        // Special dates calculation
        calculateSpecialDates()
    }

    private fun calculateSpecialDates() {
        val specialDatesText = StringBuilder()
        val calendar = Calendar.getInstance()
        
        // Add days to start date to get special milestones
        val milestones = listOf(100, 365, 1000, 3650) // 100 days, 1 year, 1000 days, 10 years
        
        calendar.time = startDate
        specialDatesText.append("特殊里程碑:\n")
        
        milestones.forEach { days ->
            calendar.time = startDate
            calendar.add(Calendar.DAY_OF_YEAR, days)
            val milestoneDate = calendar.time
            
            when (days) {
                100 -> specialDatesText.append("100天后: ${dateFormat.format(milestoneDate)}\n")
                365 -> specialDatesText.append("1年后: ${dateFormat.format(milestoneDate)}\n")
                1000 -> specialDatesText.append("1000天后: ${dateFormat.format(milestoneDate)}\n")
                3650 -> specialDatesText.append("10年后: ${dateFormat.format(milestoneDate)}\n")
            }
        }
        
        binding.tvSpecialDates.text = specialDatesText.toString()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}