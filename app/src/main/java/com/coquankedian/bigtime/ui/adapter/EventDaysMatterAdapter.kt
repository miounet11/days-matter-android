package com.coquankedian.bigtime.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.coquankedian.bigtime.R
import com.coquankedian.bigtime.data.model.Event
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class EventDaysMatterAdapter(
    private val onEventClick: (Event) -> Unit,
    private val onEventLongClick: (Event) -> Unit = {}
) : ListAdapter<Event, EventDaysMatterAdapter.EventViewHolder>(EventDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event_days_matter, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val colorBar: View = itemView.findViewById(R.id.colorBar)
        private val tvEventTitle: TextView = itemView.findViewById(R.id.tvEventTitle)
        private val tvTargetDate: TextView = itemView.findViewById(R.id.tvTargetDate)
        private val tvDaysCount: TextView = itemView.findViewById(R.id.tvDaysCount)
        private val tvDaysLabel: TextView = itemView.findViewById(R.id.tvDaysLabel)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onEventClick(getItem(position))
                }
            }

            itemView.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onEventLongClick(getItem(position))
                    true
                } else {
                    false
                }
            }
        }

        fun bind(event: Event) {
            // Calculate days difference
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time

            val eventDate = Date(event.date.time).apply {
                val cal = Calendar.getInstance()
                cal.time = this
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                time = cal.timeInMillis
            }

            val diffInMillis = eventDate.time - today.time
            val daysCount = TimeUnit.MILLISECONDS.toDays(diffInMillis)
            
            // Set event title with status
            val titlePrefix = when {
                daysCount > 0 -> "${event.title} 还有"
                daysCount < 0 -> "${event.title} 已经"
                else -> "${event.title} 就是今天"
            }
            tvEventTitle.text = titlePrefix

            // Format target date
            val dateFormat = SimpleDateFormat("yyyy-MM-dd EEEE", Locale.CHINA)
            tvTargetDate.text = "目标日: ${dateFormat.format(event.date)}"

            // Set days count
            tvDaysCount.text = abs(daysCount).toString()

            // Set days label based on status
            val context = itemView.context
            when {
                daysCount > 0 -> {
                    tvDaysLabel.text = "天"
                    tvDaysLabel.setBackgroundResource(R.drawable.days_label_background)
                }
                daysCount < 0 -> {
                    tvDaysLabel.text = "天"
                    tvDaysLabel.setBackgroundResource(R.drawable.days_label_background)
                }
                else -> {
                    tvDaysLabel.text = "今天"
                    tvDaysLabel.setBackgroundResource(R.drawable.days_label_background)
                }
            }

            // Set color bar based on category
            val categoryColor = when (event.categoryId) {
                1L -> Color.parseColor("#E74C3C") // Anniversary/纪念日
                2L -> Color.parseColor("#4A90E2") // Work/工作
                3L -> Color.parseColor("#FF8C42") // Life/生活
                else -> Color.parseColor("#9B59B6") // Custom/自定义
            }
            colorBar.setBackgroundColor(categoryColor)

            // Apply countdown status color to days count
            val daysColor = when {
                daysCount == 0L -> context.getColor(R.color.countdown_today)
                daysCount in 1..7 -> context.getColor(R.color.countdown_soon)
                daysCount > 7 -> context.getColor(R.color.countdown_future)
                else -> context.getColor(R.color.countdown_past)
            }
            tvDaysCount.setTextColor(daysColor)
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
}