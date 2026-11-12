package com.mytv.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.mytv.app.R
import com.mytv.app.model.Channel

class OttChannelCardAdapter(
    private var channels: List<Channel>,
    private val defaultGradientRes: Int = R.drawable.bg_channel_gradient_blue,
    private val onChannelClick: (Channel) -> Unit,
    private val onChannelFocus: ((Channel) -> Unit)? = null
) : RecyclerView.Adapter<OttChannelCardAdapter.OttChannelViewHolder>() {

    class OttChannelViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val viewGradient: View = view.findViewById(R.id.view_card_gradient)
        val txtNumber: TextView = view.findViewById(R.id.txt_card_number)
        val txtQuality: TextView = view.findViewById(R.id.txt_card_quality)
        val imgLogo: ImageView = view.findViewById(R.id.img_card_logo)
        val txtTitle: TextView = view.findViewById(R.id.txt_card_title)
        val txtDesc: TextView = view.findViewById(R.id.txt_card_desc)
        val viewFocusRing: View = view.findViewById(R.id.view_card_focus_ring)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OttChannelViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ott_channel_card, parent, false)
        return OttChannelViewHolder(view)
    }

    override fun onBindViewHolder(holder: OttChannelViewHolder, position: Int) {
        val channel = channels[position]

        // Formatação do número e título
        val numStr = channel.number.ifEmpty { String.format("%03d", position + 1) }
        holder.txtNumber.text = numStr
        holder.txtTitle.text = channel.title
        holder.txtQuality.text = channel.quality.ifEmpty { "FHD" }

        // Descrição inteligente ou EPG
        val descText = if (!channel.description.isNullOrEmpty()) {
            channel.description
        } else {
            when {
                channel.title.contains("Fazenda", true) || channel.category.contains("Fazenda", true) -> "Câmera Ao Vivo 24 Horas"
                channel.title.contains("Premiere", true) || channel.category.contains("Premiere", true) -> "Futebol Ao Vivo • Brasileirão & Estaduais"
                channel.title.contains("SporTV", true) -> "O Canal Campeão • Transmissão HD"
                channel.title.contains("ESPN", true) -> "Ligas Internacionais & Champions League"
                channel.title.contains("Globo", true) -> "Jornalismo, Novelas e Shows"
                channel.title.contains("SBT", true) -> "Auditório e Entretenimento"
                channel.title.contains("Record", true) -> "Jornalismo e Variedades"
                channel.title.contains("Band", true) -> "Esportes e Notícias"
                channel.title.contains("Telecine", true) -> "Grandes Sucessos do Cinema"
                channel.title.contains("HBO", true) -> "Séries Originais e Superproduções"
                channel.title.contains("Discovery", true) -> "Documentários, Ciência e Vida Selvagem"
                channel.category.contains("Kids", true) || channel.category.contains("Infantil", true) -> "Desenhos e Séries Infantis"
                channel.category.contains("24h", true) || channel.category.contains("24 Horas", true) -> "Episódios Clássicos 24 Horas"
                channel.category.contains("4K", true) || channel.category.contains("H.265", true) -> "Transmissão Ultra HD de Máxima Resolução"
                channel.category.contains("Música", true) -> "Clipes, Shows e Sucessos 24h"
                else -> channel.category.ifEmpty { "Transmissão Digital Ao Vivo" }
            }
        }
        holder.txtDesc.text = descText

        // Gradiente temático por categoria
        val gradRes = when {
            channel.title.contains("Fazenda", true) || channel.category.contains("Fazenda", true) -> R.drawable.bg_channel_gradient_orange
            channel.title.contains("Premiere", true) || channel.category.contains("Premiere", true) -> R.drawable.bg_channel_gradient_green
            channel.title.contains("Telecine", true) || channel.category.contains("Telecine", true) -> R.drawable.bg_channel_gradient_red
            channel.title.contains("HBO", true) || channel.category.contains("HBO", true) -> R.drawable.bg_channel_gradient_purple
            channel.title.contains("SporTV", true) || channel.title.contains("ESPN", true) || channel.category.contains("Esport", true) -> R.drawable.bg_channel_gradient_green
            channel.category.contains("Música", true) -> R.drawable.bg_channel_gradient_purple
            channel.category.contains("Kids", true) || channel.category.contains("Infantil", true) -> R.drawable.bg_channel_gradient_orange
            channel.category.contains("4K", true) || channel.category.contains("H.265", true) -> R.drawable.bg_channel_gradient_dark
            channel.title.contains("Discovery", true) || channel.category.contains("Discovery", true) -> R.drawable.bg_channel_gradient_blue
            channel.title.contains("SBT", true) || channel.title.contains("Record", true) -> R.drawable.bg_channel_gradient_orange
            else -> defaultGradientRes
        }
        holder.viewGradient.setBackgroundResource(gradRes)

        // Carregamento de logotipo via Coil
        if (!channel.logoUrl.isNullOrEmpty()) {
            holder.imgLogo.visibility = View.VISIBLE
            holder.imgLogo.load(channel.logoUrl) {
                crossfade(true)
                placeholder(R.drawable.badge_hd)
                error(R.drawable.badge_hd)
            }
        } else {
            holder.imgLogo.visibility = View.INVISIBLE
        }

        // Foco e Escala Suave D-Pad (Android TV)
        holder.itemView.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                holder.itemView.animate()
                    .scaleX(1.06f)
                    .scaleY(1.06f)
                    .translationZ(8f)
                    .setDuration(160)
                    .start()
                onChannelFocus?.invoke(channel)
            } else {
                holder.itemView.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .translationZ(0f)
                    .setDuration(160)
                    .start()
            }
        }

        holder.itemView.setOnClickListener {
            onChannelClick(channel)
        }
    }

    override fun getItemCount(): Int = channels.size

    fun updateChannels(newChannels: List<Channel>) {
        this.channels = newChannels
        notifyDataSetChanged()
    }

    fun getChannels(): List<Channel> = channels
}
