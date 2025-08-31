package com.coquankedian.bigtime.ui.dialog

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class DatePickerDialog : DialogFragment(), DatePickerDialog.OnDateSetListener {

    private var listener: ((year: Int, month: Int, dayOfMonth: Int) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(requireContext(), this, year, month, day)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        listener?.invoke(year, month, dayOfMonth)
    }

    fun setOnDateSelectedListener(listener: (year: Int, month: Int, dayOfMonth: Int) -> Unit) {
        this.listener = listener
    }

    companion object {
        fun newInstance() = DatePickerDialog()
    }
}
