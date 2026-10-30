package com.mytv.app.adapter

import android.graphics.Color
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mytv.app.R
import com.mytv.app.model.EpgProgram

class PlaybackProgramsAdapter(
    private var programs: List<EpgProgram>,
    private val onProgramFocused: (EpgProgram) -> Unit,
    private val onProgramClicked: (EpgProgram) -> Unit,
    private val onNavigateToDays: () -> Unit
) : RecyclerView.Adapter<PlaybackProgramsAdapter.ViewHolder>() {

    private var selectedPosition = 0

    fun updatePrograms(newPrograms: List<EpgProgram>) {
        programs = newPrograms
        selectedPosition = 0
        notifyDataSetChanged()
    }

    fun getSelectedProgram(): EpgProgram? {
        return if (selectedPosition in programs.indices) programs[selectedPosition] else programs.firstOrNull()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_playback_program, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val program = programs[position]
        val now = System.currentTimeMillis()

        holder.txtTime.text = "${program.startTime} - ${program.endTime}"
        holder.txtTitle.text = program.title

        val isLiveNow = (program.startTimestamp <= now && now < program.endTimestamp)
        val isPast = (program.endTimestamp <= now)

        if (isLiveNow) {
            holder.txtStatusIcon.visibility = View.VISIBLE
            holder.txtActionBtn.text = "Reproduzindo"
            holder.txtActionBtn.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.bg_action_reproduzindo)
            holder.txtActionBtn.setTextColor(Color.parseColor("#04172E"))
        } else if (isPast) {
            holder.txtStatusIcon.visibility = View.INVISIBLE
            holder.txtActionBtn.text = "Reprodução"
            holder.txtActionBtn.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.bg_action_reproducao)
            holder.txtActionBtn.setTextColor(Color.WHITE)
        } else {
            holder.txtStatusIcon.visibility = View.INVISIBLE
            holder.txtActionBtn.text = "Lembrete"
            holder.txtActionBtn.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.bg_action_lembrete)
            holder.txtActionBtn.setTextColor(Color.parseColor("#8FAAC9"))
        }

        val isSelected = (position == selectedPosition)
        holder.itemView.isSelected = isSelected

        holder.itemView.setOnFocusChangeListener { view, hasFocus ->
            val pos = holder.bindingAdapterPosition
            if (hasFocus) {
                holder.txtTitle.setTextColor(Color.parseColor("#00E5FF"))
                holder.itemView.isSelected = true
                view.scaleX = 1.01f
                view.scaleY = 1.01f
                if (pos != RecyclerView.NO_POSITION && pos < programs.size) {
                    val prev = selectedPosition
                    selectedPosition = pos
                    if (prev != pos && prev in programs.indices) {
                        notifyItemChanged(prev)
                    }
                    onProgramFocused(programs[pos])
                }
            } else {
                view.scaleX = 1.0f
                view.scaleY = 1.0f
                holder.itemView.isSelected = (pos == selectedPosition)
                holder.txtTitle.setTextColor(if (pos == selectedPosition) Color.WHITE else Color.parseColor("#E6EDF8"))
            }
        }

        holder.itemView.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                val pos = holder.bindingAdapterPosition
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_LEFT -> {
                        onNavigateToDays()
                        return@setOnKeyListener true
                    }
                    KeyEvent.KEYCODE_DPAD_CENTER,
                    KeyEvent.KEYCODE_ENTER,
                    KeyEvent.KEYCODE_NUMPAD_ENTER -> {
                        if (pos != RecyclerView.NO_POSITION && pos < programs.size) {
                            onProgramClicked(programs[pos])
                            return@setOnKeyListener true
                        }
                    }
                }
            }
            false
        }

        holder.itemView.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION && pos < programs.size) {
                onProgramClicked(programs[pos])
            }
        }
    }

    override fun getItemCount(): Int = programs.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtStatusIcon: TextView = view.findViewById(R.id.txt_prog_status_icon)
        val txtTime: TextView = view.findViewById(R.id.txt_prog_time)
        val txtTitle: TextView = view.findViewById(R.id.txt_prog_title)
        val txtActionBtn: TextView = view.findViewById(R.id.txt_prog_action_btn)
    }
}
