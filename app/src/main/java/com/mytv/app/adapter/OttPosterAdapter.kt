package com.mytv.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.mytv.app.R
import com.mytv.app.model.VodItem

class OttPosterAdapter(
    private var items: List<VodItem>,
    private val onItemFocus: (VodItem) -> Unit,
    private val onItemClick: (VodItem) -> Unit,
    private val onNavigateUpToTabs: (() -> Unit)? = null
) : RecyclerView.Adapter<OttPosterAdapter.PosterViewHolder>() {

    class PosterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgCover: ImageView = view.findViewById(R.id.img_poster_cover)
        val txtTag: TextView = view.findViewById(R.id.txt_poster_tag)
        val txtRating: TextView = view.findViewById(R.id.txt_poster_rating)
        val txtTitle: TextView = view.findViewById(R.id.txt_poster_title)
        val viewFocusRing: View = view.findViewById(R.id.view_poster_focus_ring)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PosterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ott_poster_card, parent, false)
        return PosterViewHolder(view)
    }

    override fun onBindViewHolder(holder: PosterViewHolder, position: Int) {
        val item = items[position]

        holder.txtTitle.text = item.name

        // Micro-badge discreto estilo Netflix
        holder.txtTag.text = if (item.isSeries) "SÉRIE" else (if (item.name.contains("4K", ignoreCase = true)) "4K" else "FHD")
        holder.txtRating.text = item.getFormattedRating()

        if (!item.posterUrl.isNullOrEmpty()) {
            holder.imgCover.load(item.posterUrl) {
                crossfade(true)
                placeholder(R.drawable.logo)
                error(R.drawable.logo)
            }
        } else {
            holder.imgCover.setImageResource(R.drawable.logo)
        }

        // Foco e Escala Suave D-Pad estilo TV Box Premium
        holder.itemView.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                holder.itemView.animate()
                    .scaleX(1.06f)
                    .scaleY(1.06f)
                    .translationZ(6f)
                    .setDuration(140)
                    .start()
                holder.viewFocusRing.visibility = View.VISIBLE
                onItemFocus(item)
            } else {
                holder.itemView.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .translationZ(0f)
                    .setDuration(140)
                    .start()
                holder.viewFocusRing.visibility = View.GONE
            }
        }

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }

        holder.itemView.setOnKeyListener { _, keyCode, event ->
            if (event.action == android.view.KeyEvent.ACTION_DOWN) {
                if (keyCode == android.view.KeyEvent.KEYCODE_DPAD_UP && position < 5) {
                    onNavigateUpToTabs?.invoke()
                    return@setOnKeyListener true
                }
            }
            false
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<VodItem>) {
        this.items = newItems
        notifyDataSetChanged()
    }
}
