package com.mytv.app.adapter

import android.graphics.Color
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.mytv.app.R
import com.mytv.app.model.Channel

class PlaybackChannelAdapter(
    private var channels: List<Channel>,
    private val onChannelSelected: (Channel) -> Unit,
    private val onNavigateToDays: () -> Unit,
    private val onNavigateLeftToCategory: (() -> Unit)? = null,
    private val onNavigateUpToClose: (() -> Unit)? = null
) : RecyclerView.Adapter<PlaybackChannelAdapter.ViewHolder>() {

    private var selectedPosition = 0

    fun updateChannels(newChannels: List<Channel>, initialChannelId: String? = null) {
        channels = newChannels
        if (initialChannelId != null) {
            val idx = newChannels.indexOfFirst { it.id == initialChannelId }
            selectedPosition = if (idx >= 0) idx else 0
        } else {
            selectedPosition = 0
        }
        notifyDataSetChanged()
    }

    fun getSelectedChannel(): Channel? {
        return if (selectedPosition in channels.indices) channels[selectedPosition] else channels.firstOrNull()
    }

    fun getSelectedPosition(): Int = selectedPosition

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_playback_channel, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val channel = channels[position]
        holder.txtNumber.text = channel.number
        holder.txtName.text = channel.title

        if (!channel.logoUrl.isNullOrEmpty()) {
            holder.imgLogo.load(channel.logoUrl) {
                crossfade(false)
                placeholder(R.drawable.logo)
                error(R.drawable.logo)
            }
        } else {
            holder.imgLogo.setImageResource(R.drawable.logo)
        }

        val hasCatchup = channel.hasPlayback || com.mytv.app.data.PlaybackRepository.hasPlayback(channel)
        holder.txtClock.visibility = if (hasCatchup) View.VISIBLE else View.INVISIBLE

        val isSelected = (position == selectedPosition)
        holder.itemView.isSelected = isSelected

        holder.itemView.setOnFocusChangeListener { view, hasFocus ->
            val pos = holder.bindingAdapterPosition
            if (hasFocus) {
                holder.txtName.setTextColor(Color.parseColor("#00E5FF"))
                holder.txtNumber.setTextColor(Color.WHITE)
                holder.itemView.isSelected = true
                view.scaleX = 1.02f
                view.scaleY = 1.02f
                if (pos != RecyclerView.NO_POSITION && pos < channels.size) {
                    val prev = selectedPosition
                    selectedPosition = pos
                    if (prev != pos && prev in channels.indices) {
                        notifyItemChanged(prev)
                    }
                    onChannelSelected(channels[pos])
                }
            } else {
                view.scaleX = 1.0f
                view.scaleY = 1.0f
                holder.itemView.isSelected = (pos == selectedPosition)
                holder.txtName.setTextColor(if (pos == selectedPosition) Color.WHITE else Color.parseColor("#E6EDF8"))
                holder.txtNumber.setTextColor(Color.parseColor("#00E5FF"))
            }
        }

        holder.itemView.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                val pos = holder.bindingAdapterPosition
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_LEFT -> {
                        onNavigateLeftToCategory?.invoke()
                        return@setOnKeyListener true
                    }
                    KeyEvent.KEYCODE_DPAD_RIGHT,
                    KeyEvent.KEYCODE_DPAD_CENTER,
                    KeyEvent.KEYCODE_ENTER,
                    KeyEvent.KEYCODE_NUMPAD_ENTER -> {
                        if (pos != RecyclerView.NO_POSITION && pos < channels.size) {
                            selectedPosition = pos
                            onChannelSelected(channels[pos])
                            onNavigateToDays()
                            return@setOnKeyListener true
                        }
                    }
                    KeyEvent.KEYCODE_DPAD_UP -> {
                        if (pos == 0) {
                            onNavigateUpToClose?.invoke()
                            return@setOnKeyListener true
                        }
                    }
                }
            }
            false
        }

        holder.itemView.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION && pos < channels.size) {
                selectedPosition = pos
                onChannelSelected(channels[pos])
                onNavigateToDays()
            }
        }
    }

    override fun getItemCount(): Int = channels.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgLogo: ImageView = view.findViewById(R.id.img_playback_ch_logo)
        val txtNumber: TextView = view.findViewById(R.id.txt_playback_ch_number)
        val txtName: TextView = view.findViewById(R.id.txt_playback_ch_name)
        val txtClock: TextView = view.findViewById(R.id.txt_playback_ch_clock)
    }
}
