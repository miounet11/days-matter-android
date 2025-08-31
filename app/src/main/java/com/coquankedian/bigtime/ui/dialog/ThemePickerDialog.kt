package com.coquankedian.bigtime.ui.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import com.coquankedian.bigtime.R
import com.coquankedian.bigtime.databinding.DialogThemePickerBinding

class ThemePickerDialog : DialogFragment() {

    private var _binding: DialogThemePickerBinding? = null
    private val binding get() = _binding!!

    private var onThemeSelected: ((cardBackgroundColor: Int, textColor: Int, iconResId: Int) -> Unit)? = null

    // Predefined themes
    private val themes = listOf(
        Theme("经典白", Color.WHITE, Color.BLACK, android.R.drawable.ic_menu_my_calendar),
        Theme("深色", Color.parseColor("#1E1E1E"), Color.WHITE, android.R.drawable.ic_menu_my_calendar),
        Theme("浪漫粉", Color.parseColor("#FFF0F5"), Color.parseColor("#FF1493"), android.R.drawable.ic_menu_my_calendar),
        Theme("海洋蓝", Color.parseColor("#E3F2FD"), Color.parseColor("#1976D2"), android.R.drawable.ic_menu_my_calendar),
        Theme("森林绿", Color.parseColor("#E8F5E8"), Color.parseColor("#388E3C"), android.R.drawable.ic_menu_my_calendar),
        Theme("阳光橙", Color.parseColor("#FFF3E0"), Color.parseColor("#F57C00"), android.R.drawable.ic_menu_my_calendar),
        Theme("紫罗兰", Color.parseColor("#F3E5F5"), Color.parseColor("#7B1FA2"), android.R.drawable.ic_menu_my_calendar),
        Theme("简约灰", Color.parseColor("#F5F5F5"), Color.parseColor("#424242"), android.R.drawable.ic_menu_my_calendar)
    )

    private val icons = listOf(
        android.R.drawable.ic_menu_my_calendar,
        android.R.drawable.ic_menu_agenda,
        android.R.drawable.ic_menu_compass,
        android.R.drawable.ic_menu_camera,
        android.R.drawable.ic_menu_gallery,
        android.R.drawable.ic_menu_help,
        android.R.drawable.ic_menu_info_details,
        android.R.drawable.ic_menu_manage
    )

    data class Theme(
        val name: String,
        val cardBackgroundColor: Int,
        val textColor: Int,
        val iconResId: Int
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogThemePickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupThemeGrid()
        setupIconGrid()
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

    private fun setupThemeGrid() {
        binding.themeGrid.removeAllViews()

        themes.forEach { theme ->
            val themeView = createThemePreview(theme)
            binding.themeGrid.addView(themeView)
        }
    }

    private fun setupIconGrid() {
        binding.iconGrid.removeAllViews()

        icons.forEach { iconResId ->
            val iconView = createIconPreview(iconResId)
            binding.iconGrid.addView(iconView)
        }
    }

    private fun createThemePreview(theme: Theme): View {
        val context = requireContext()
        return LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(0, 120, 1f)
            orientation = LinearLayout.VERTICAL
            setPadding(8, 8, 8, 8)

            // Card preview
            val cardView = View(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    80
                )
                setBackgroundColor(theme.cardBackgroundColor)
                setPadding(4, 4, 4, 4)

                val borderView = View(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                    )
                    setBackgroundColor(Color.LTGRAY)
                    setPadding(1, 1, 1, 1)

                    val contentView = View(context).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT
                        )
                        setBackgroundColor(theme.cardBackgroundColor)
                    }
                    addView(contentView)
                }
                addView(borderView)
            }

            // Theme name
            val nameView = android.widget.TextView(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                text = theme.name
                textSize = 12f
                gravity = android.view.Gravity.CENTER
                setPadding(4, 4, 4, 4)
            }

            addView(cardView)
            addView(nameView)

            setOnClickListener {
                // Update selected theme
                selectedCardBackgroundColor = theme.cardBackgroundColor
                selectedTextColor = theme.textColor
                selectedIconResId = theme.iconResId
                updatePreview()
            }
        }
    }

    private fun createIconPreview(iconResId: Int): View {
        val context = requireContext()
        return LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(60, 60)
            orientation = LinearLayout.VERTICAL
            gravity = android.view.Gravity.CENTER
            setPadding(4, 4, 4, 4)
            setBackgroundResource(R.drawable.category_chip_background)

            val imageView = android.widget.ImageView(context).apply {
                layoutParams = LinearLayout.LayoutParams(32, 32)
                setImageResource(iconResId)
                scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
            }

            addView(imageView)

            setOnClickListener {
                selectedIconResId = iconResId
                updatePreview()
            }
        }
    }

    private fun setupButtons() {
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnApply.setOnClickListener {
            onThemeSelected?.invoke(selectedCardBackgroundColor, selectedTextColor, selectedIconResId)
            dismiss()
        }
    }

    private fun updatePreview() {
        binding.previewCard.setCardBackgroundColor(selectedCardBackgroundColor)
        binding.previewTitle.setTextColor(selectedTextColor)
        binding.previewDescription.setTextColor(selectedTextColor)
        binding.previewDate.setTextColor(selectedTextColor)
        binding.previewIcon.setImageResource(selectedIconResId)
    }

    private var selectedCardBackgroundColor = Color.WHITE
    private var selectedTextColor = Color.BLACK
    private var selectedIconResId = android.R.drawable.ic_menu_my_calendar

    fun setOnThemeSelectedListener(listener: (cardBackgroundColor: Int, textColor: Int, iconResId: Int) -> Unit) {
        onThemeSelected = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = ThemePickerDialog()
    }
}
