package com.mytv.app.adapter

import android.graphics.Color
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mytv.app.R
import com.mytv.app.model.EpgProgram

class PlaybackHistoryAdapter(
    private var programs: List<EpgProgram>,
    private val onProgramClicked: (EpgProgram) -> Unit
) : RecyclerView.Adapter<PlaybackHistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtTime: TextView = view.findViewById(R.id.txt_history_time)
        val txtTitle: TextView = view.findViewById(R.id.txt_history_title)
        val txtSynopsis: TextView = view.findViewById(R.id.txt_history_synopsis)
        val txtPlayAction: TextView = view.findViewById(R.id.txt_history_play_action)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_playback_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val program = programs[position]

        holder.txtTime.text = "${program.startTime} - ${program.endTime}"
        holder.txtTitle.text = program.title
        holder.txtSynopsis.text = program.synopsis.ifEmpty { "Gravação completa disponível." }

        holder.itemView.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                holder.txtTitle.setTextColor(Color.parseColor("#00E5FF"))
                holder.txtPlayAction.setBackgroundResource(R.drawable.badge_live)
                holder.txtPlayAction.setTextColor(Color.WHITE)
            } else {
                holder.txtTitle.setTextColor(Color.WHITE)
                holder.txtPlayAction.setBackgroundResource(R.drawable.badge_playback)
                holder.txtPlayAction.setTextColor(Color.parseColor("#00E5FF"))
            }
        }

        holder.itemView.setOnClickListener {
            onProgramClicked(program)
        }

        holder.itemView.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_CENTER,
                    KeyEvent.KEYCODE_ENTER,
                    KeyEvent.KEYCODE_NUMPAD_ENTER -> {
                        onProgramClicked(program)
                        return@setOnKeyListener true
                    }
                }
            }
            false
        }
    }

    override fun getItemCount(): Int = programs.size

    fun updateData(newPrograms: List<EpgProgram>) {
        this.programs = newPrograms
        notifyDataSetChanged()
    }
}
