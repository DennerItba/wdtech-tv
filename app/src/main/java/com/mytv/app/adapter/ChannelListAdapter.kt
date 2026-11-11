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

class ChannelListAdapter(
    private var channels: List<Channel>,
    private val onChannelSelected: (Channel) -> Unit,
    private val onChannelClicked: (Channel) -> Unit,
    private val onNavigateToCategories: () -> Unit,
    private val onNavigateToPlayer: () -> Unit,
    private val onNavigateUpToTabs: (() -> Unit)? = null
) : RecyclerView.Adapter<ChannelListAdapter.ChannelViewHolder>() {

    private var selectedIndex = 0

    class ChannelViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgLogo: ImageView = view.findViewById(R.id.img_row_logo)
        val txtNumber: TextView = view.findViewById(R.id.txt_row_number)
        val txtTitle: TextView = view.findViewById(R.id.txt_row_title)
        val txtEpg: TextView = view.findViewById(R.id.txt_row_epg)
        val txtBadge: TextView = view.findViewById(R.id.txt_row_badge)
        val txtPlayback: TextView = view.findViewById(R.id.txt_row_playback)
        val pbProgress: android.widget.ProgressBar = view.findViewById(R.id.pb_row_progress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_channel_list_row, parent, false)
        return ChannelViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
        val channel = channels[position]

        // Numeração padronizada em 3 dígitos (ex: 001, 002, 015)
        val num = channel.number.toIntOrNull() ?: (position + 1)
        holder.txtNumber.text = String.format("%03d", num)
        holder.txtTitle.text = channel.title
        holder.txtBadge.text = channel.quality
        holder.txtPlayback.visibility = if (channel.hasPlayback) View.VISIBLE else View.GONE

        // Barra de progresso para Continue Assistindo
        if (channel.progressPercent > 0) {
            holder.pbProgress.visibility = View.VISIBLE
            holder.pbProgress.progress = channel.progressPercent
        } else {
            holder.pbProgress.visibility = View.GONE
        }

        val epg = channel.currentProgram
        if (epg != null) {
            holder.txtEpg.text = "▶ ${epg.title} (${epg.startTime})"
        } else {
            holder.txtEpg.text = "▶ Transmissão Ao Vivo (${channel.quality})"
        }

        if (!channel.logoUrl.isNullOrEmpty()) {
            holder.imgLogo.load(channel.logoUrl) {
                size(96, 72)
                crossfade(false)
                placeholder(R.drawable.logo)
                error(R.drawable.logo)
            }
        } else {
            holder.imgLogo.setImageResource(R.drawable.logo)
        }

        val isCurrentSelected = (position == selectedIndex)
        holder.itemView.isSelected = isCurrentSelected

        if (holder.itemView.isFocused) {
            holder.txtTitle.setTextColor(Color.parseColor("#00E5FF"))
            holder.txtNumber.setTextColor(Color.WHITE)
            holder.itemView.scaleX = 1.05f
            holder.itemView.scaleY = 1.05f
            holder.itemView.elevation = 8f
        } else {
            holder.itemView.scaleX = 1.0f
            holder.itemView.scaleY = 1.0f
            holder.itemView.elevation = 0f
            if (isCurrentSelected) {
                holder.txtTitle.setTextColor(Color.WHITE)
                holder.txtNumber.setTextColor(Color.parseColor("#00E5FF"))
            } else {
                holder.txtTitle.setTextColor(Color.parseColor("#E6EDF8"))
                holder.txtNumber.setTextColor(Color.parseColor("#00E5FF"))
            }
        }

        // Foco e seleção ultra nítida e segura com o controle remoto (Escala suave 1.05x)
        holder.itemView.setOnFocusChangeListener { view, hasFocus ->
            val pos = holder.bindingAdapterPosition
            if (hasFocus) {
                holder.txtTitle.setTextColor(Color.parseColor("#00E5FF"))
                holder.txtNumber.setTextColor(Color.WHITE)
                holder.itemView.isSelected = true
                view.animate().scaleX(1.05f).scaleY(1.05f).setDuration(120).start()
                view.elevation = 8f
                if (pos != RecyclerView.NO_POSITION && pos < channels.size) {
                    selectedIndex = pos
                    onChannelSelected(channels[pos])
                }
            } else {
                view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(120).start()
                view.elevation = 0f
                holder.itemView.isSelected = false
                holder.txtTitle.setTextColor(Color.parseColor("#E6EDF8"))
                holder.txtNumber.setTextColor(Color.parseColor("#00E5FF"))
            }
        }

        // Navegação D-Pad precisa: transições fluidas e estáveis
        holder.itemView.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                val pos = holder.bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION && pos < channels.size) {
                    when (keyCode) {
                        // Seta para Esquerda: volta diretamente para as Categorias
                        KeyEvent.KEYCODE_DPAD_LEFT -> {
                            onNavigateToCategories()
                            return@setOnKeyListener true
                        }
                        // Seta para Direita: move foco para a área do Player / Tela Cheia
                        KeyEvent.KEYCODE_DPAD_RIGHT -> {
                            onNavigateToPlayer()
                            return@setOnKeyListener true
                        }
                        // Seta para Cima no primeiro canal: sobe para as abas superiores
                        KeyEvent.KEYCODE_DPAD_UP -> {
                            if (pos == 0) {
                                onNavigateUpToTabs?.invoke()
                                return@setOnKeyListener true
                            }
                        }
                        // Seta para Baixo no último canal: NÃO deixa escapar para fora
                        KeyEvent.KEYCODE_DPAD_DOWN -> {
                            if (pos == channels.size - 1) {
                                return@setOnKeyListener true
                            }
                        }
                        // Clicar OK: abre em Tela Cheia
                        KeyEvent.KEYCODE_DPAD_CENTER,
                        KeyEvent.KEYCODE_ENTER,
                        KeyEvent.KEYCODE_NUMPAD_ENTER -> {
                            onChannelClicked(channels[pos])
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
                onChannelClicked(channels[pos])
            }
        }
    }

    override fun getItemCount(): Int = channels.size

    fun getSelectedPosition(): Int = selectedIndex

    fun getChannels(): List<Channel> = channels

    fun updateChannels(newChannels: List<Channel>) {
        this.channels = newChannels
        this.selectedIndex = 0
        notifyDataSetChanged()
    }
}
