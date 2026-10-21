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

class ChannelAdapter(
    private val channels: List<Channel>,
    private val onChannelFocused: (Channel) -> Unit,
    private val onChannelClicked: (Channel) -> Unit
) : RecyclerView.Adapter<ChannelAdapter.ChannelViewHolder>() {

    class ChannelViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtNumber: TextView = view.findViewById(R.id.txt_channel_number)
        val imgLogo: ImageView = view.findViewById(R.id.img_channel_logo)
        val txtFallback: TextView = view.findViewById(R.id.txt_logo_fallback)
        val txtTitle: TextView = view.findViewById(R.id.txt_channel_title)
        val txtCategory: TextView = view.findViewById(R.id.txt_channel_category)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_channel_card, parent, false)
        return ChannelViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
        val channel = channels[position]

        holder.txtNumber.text = "CH ${channel.number}"
        holder.txtTitle.text = channel.title
        holder.txtCategory.text = "${channel.category} • FHD 1080p"

        if (!channel.logoUrl.isNullOrEmpty()) {
            holder.txtFallback.visibility = View.GONE
            holder.imgLogo.visibility = View.VISIBLE
            holder.imgLogo.load(channel.logoUrl) {
                crossfade(true)
                listener(
                    onError = { _, _ ->
                        holder.imgLogo.visibility = View.GONE
                        holder.txtFallback.visibility = View.VISIBLE
                        holder.txtFallback.text = channel.title.take(16).uppercase()
                    },
                    onSuccess = { _, _ ->
                        holder.imgLogo.visibility = View.VISIBLE
                        holder.txtFallback.visibility = View.GONE
                    }
                )
            }
        } else {
            holder.imgLogo.visibility = View.GONE
            holder.txtFallback.visibility = View.VISIBLE
            holder.txtFallback.text = channel.title.take(16).uppercase()
        }

        // Animação e destaque para o controle remoto (DPAD)
        holder.itemView.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                v.animate().scaleX(1.09f).scaleY(1.09f).setDuration(120).start()
                v.elevation = 16f
                onChannelFocused(channel)
            } else {
                v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(120).start()
                v.elevation = 0f
            }
        }

        holder.itemView.setOnClickListener {
            onChannelClicked(channel)
        }
    }

    override fun getItemCount(): Int = channels.size
}
