package com.mytv.app.adapter

import android.graphics.Color
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mytv.app.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

data class PlaybackDayItem(
    val dayOffset: Int,
    val dayNumber: String,
    val weekday: String,
    val isToday: Boolean
)

class PlaybackDaysAdapter(
    private val onDaySelected: (Int) -> Unit,
    private val onNavigateToChannels: () -> Unit,
    private val onNavigateToPrograms: () -> Unit
) : RecyclerView.Adapter<PlaybackDaysAdapter.ViewHolder>() {

    private val days = mutableListOf<PlaybackDayItem>()
    private var selectedPosition = 0

    init {
        generateDays()
    }

    private fun generateDays() {
        days.clear()
        val cal = Calendar.getInstance(TimeZone.getTimeZone("America/Sao_Paulo"))
        val dayNumFormat = SimpleDateFormat("dd", Locale("pt", "BR")).apply {
            timeZone = TimeZone.getTimeZone("America/Sao_Paulo")
        }
        val weekdayFormat = SimpleDateFormat("EEE", Locale("pt", "BR")).apply {
            timeZone = TimeZone.getTimeZone("America/Sao_Paulo")
        }

        for (i in 0 until 7) {
            val c = cal.clone() as Calendar
            c.add(Calendar.DAY_OF_YEAR, -i)
            val numStr = if (i == 0) "Hoje" else dayNumFormat.format(c.time)
            val weekStr = weekdayFormat.format(c.time).replace(".", "").uppercase(Locale("pt", "BR"))
            days.add(PlaybackDayItem(i, numStr, weekStr, i == 0))
        }
    }

    fun getSelectedOffset(): Int = days.getOrNull(selectedPosition)?.dayOffset ?: 0

    fun setSelectedOffset(offset: Int) {
        val idx = days.indexOfFirst { it.dayOffset == offset }
        if (idx >= 0 && idx != selectedPosition) {
            val prev = selectedPosition
            selectedPosition = idx
            notifyItemChanged(prev)
            notifyItemChanged(selectedPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_playback_day, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = days[position]
        holder.txtNumber.text = item.dayNumber
        holder.txtWeekday.text = item.weekday

        val isSelected = (position == selectedPosition)
        holder.itemView.isSelected = isSelected
        holder.txtIndicator.visibility = if (isSelected) View.VISIBLE else View.INVISIBLE

        holder.itemView.setOnFocusChangeListener { _, hasFocus ->
            val pos = holder.bindingAdapterPosition
            if (hasFocus) {
                holder.txtNumber.setTextColor(Color.WHITE)
                holder.txtWeekday.setTextColor(Color.parseColor("#00E5FF"))
                holder.itemView.isSelected = true
                if (pos != RecyclerView.NO_POSITION && pos < days.size) {
                    val prev = selectedPosition
                    selectedPosition = pos
                    if (prev != pos && prev in days.indices) {
                        notifyItemChanged(prev)
                    }
                    holder.txtIndicator.visibility = View.VISIBLE
                    onDaySelected(days[pos].dayOffset)
                }
            } else {
                holder.itemView.isSelected = (pos == selectedPosition)
                holder.txtIndicator.visibility = if (pos == selectedPosition) View.VISIBLE else View.INVISIBLE
                holder.txtNumber.setTextColor(if (pos == selectedPosition) Color.WHITE else Color.parseColor("#D0DFF2"))
                holder.txtWeekday.setTextColor(Color.parseColor("#00E5FF"))
            }
        }

        holder.itemView.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                val pos = holder.bindingAdapterPosition
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_LEFT -> {
                        onNavigateToChannels()
                        return@setOnKeyListener true
                    }
                    KeyEvent.KEYCODE_DPAD_RIGHT,
                    KeyEvent.KEYCODE_DPAD_CENTER,
                    KeyEvent.KEYCODE_ENTER,
                    KeyEvent.KEYCODE_NUMPAD_ENTER -> {
                        if (pos != RecyclerView.NO_POSITION && pos < days.size) {
                            selectedPosition = pos
                            onDaySelected(days[pos].dayOffset)
                            onNavigateToPrograms()
                            return@setOnKeyListener true
                        }
                    }
                }
            }
            false
        }

        holder.itemView.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION && pos < days.size) {
                selectedPosition = pos
                onDaySelected(days[pos].dayOffset)
                onNavigateToPrograms()
            }
        }
    }

    override fun getItemCount(): Int = days.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtNumber: TextView = view.findViewById(R.id.txt_day_number)
        val txtWeekday: TextView = view.findViewById(R.id.txt_day_weekday)
        val txtIndicator: TextView = view.findViewById(R.id.txt_day_indicator)
    }
}
