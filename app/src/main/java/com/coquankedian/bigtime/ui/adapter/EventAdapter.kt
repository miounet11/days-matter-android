package com.coquankedian.bigtime.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.coquankedian.bigtime.data.model.Event
import com.coquankedian.bigtime.databinding.ItemEventBinding
import java.text.SimpleDateFormat
import java.util.*

class EventAdapter(
    private val onEventClick: (Event) -> Unit,
    private val onEventLongClick: (Event) -> Unit = {},
    private val getCategoryName: (Long) -> String = { "分类" }
) : ListAdapter<Event, EventAdapter.EventViewHolder>(EventDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class EventViewHolder(
        private val binding: ItemEventBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onEventClick(getItem(position))
                }
            }

            binding.root.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onEventLongClick(getItem(position))
                    true // Consume the long click
                } else {
                    false
                }
            }
        }

        fun bind(event: Event) {
            binding.apply {
                tvEventTitle.text = event.title
                tvEventDescription.text = event.description.takeIf { it.isNotEmpty() } ?: "无描述"
                tvCountdown.text = event.countdownText

                // Format date
                val dateFormat = SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault())
                tvEventDate.text = dateFormat.format(event.date)

                // Set category name
                tvCategory.text = getCategoryName(event.categoryId)

                // Handle pin and lock icons
                ivPin.visibility = if (event.isPinned) android.view.View.VISIBLE else android.view.View.GONE
                ivLock.visibility = if (event.isLocked) android.view.View.VISIBLE else android.view.View.GONE

                // Set colors based on countdown status
                tvCountdown.setTextColor(event.statusColor)

                // Apply custom colors
                root.setCardBackgroundColor(event.cardBackgroundColor)
                tvEventTitle.setTextColor(event.textColor)
                tvEventDescription.setTextColor(event.textColor)
                tvEventDate.setTextColor(event.textColor)

                // Set category chip background color using theme colors
                val context = root.context
                val categoryColor = when (event.categoryId) {
                    1L -> android.graphics.Color.parseColor("#FF6B9D") // Anniversary
                    2L -> android.graphics.Color.parseColor("#4ECDC4") // Work
                    3L -> android.graphics.Color.parseColor("#45B7D1") // Life
                    else -> android.graphics.Color.parseColor("#9C27B0") // Custom
                }

                // Create a background drawable with the category color
                val backgroundDrawable = android.graphics.drawable.GradientDrawable().apply {
                    setColor(categoryColor)
                    cornerRadius = 16f
                }
                tvCategory.background = backgroundDrawable

                // Set custom icon if available
                event.iconResId?.let { iconResId ->
                    try {
                        ivEventIcon.setImageResource(iconResId)
                    } catch (e: Exception) {
                        ivEventIcon.setImageResource(android.R.drawable.ic_menu_my_calendar)
                    }
                }
            }
        }
    }
}

class EventDiffCallback : DiffUtil.ItemCallback<Event>() {
    override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
        return oldItem == newItem
    }
}
