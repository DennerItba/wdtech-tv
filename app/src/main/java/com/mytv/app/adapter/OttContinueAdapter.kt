package com.mytv.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.mytv.app.R
import com.mytv.app.data.WatchProgress

class OttContinueAdapter(
    private var items: List<WatchProgress>,
    private val onItemClick: (WatchProgress) -> Unit
) : RecyclerView.Adapter<OttContinueAdapter.ContinueViewHolder>() {

    class ContinueViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgThumb: ImageView = view.findViewById(R.id.img_continue_thumb)
        val txtCategory: TextView = view.findViewById(R.id.txt_continue_category)
        val txtTitle: TextView = view.findViewById(R.id.txt_continue_title)
        val progressBar: ProgressBar = view.findViewById(R.id.pb_continue_progress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContinueViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ott_continue_card, parent, false)
        return ContinueViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContinueViewHolder, position: Int) {
        val item = items[position]

        holder.txtTitle.text = item.title
        holder.txtCategory.text = "${item.category} • ${item.progressPercent}% assistido"
        holder.progressBar.progress = item.progressPercent

        if (!item.coverImage.isNullOrEmpty()) {
            holder.imgThumb.load(item.coverImage) {
                crossfade(true)
                placeholder(R.drawable.tv_bg)
                error(R.drawable.tv_bg)
            }
        } else {
            holder.imgThumb.setImageResource(R.drawable.tv_bg)
        }

        // Foco e Escala Suave D-Pad
        holder.itemView.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                holder.itemView.animate()
                    .scaleX(1.08f)
                    .scaleY(1.08f)
                    .translationZ(8f)
                    .setDuration(160)
                    .start()
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
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<WatchProgress>) {
        this.items = newItems
        notifyDataSetChanged()
    }
}
