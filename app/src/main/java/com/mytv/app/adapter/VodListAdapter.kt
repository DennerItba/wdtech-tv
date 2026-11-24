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
import com.mytv.app.model.VodItem

class VodListAdapter(
    private var items: List<VodItem> = emptyList(),
    private val onItemSelected: (VodItem) -> Unit,
    private val onItemClicked: (VodItem) -> Unit,
    private val onNavigateToCategories: () -> Unit,
    private val onNavigateToDetails: () -> Unit,
    private val onNavigateUpToTabs: (() -> Unit)? = null
) : RecyclerView.Adapter<VodListAdapter.VodViewHolder>() {

    private var selectedPosition = 0

    fun updateItems(newItems: List<VodItem>) {
        items = newItems
        selectedPosition = 0
        notifyDataSetChanged()
    }

    fun getSelectedPosition(): Int = selectedPosition

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VodViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vod_list_row, parent, false)
        return VodViewHolder(view)
    }

    override fun onBindViewHolder(holder: VodViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, position == selectedPosition)
    }

    override fun getItemCount(): Int = items.size

    inner class VodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgPoster: ImageView = itemView.findViewById(R.id.img_vod_poster)
        private val txtTitle: TextView = itemView.findViewById(R.id.txt_vod_title)
        private val txtSubtitle: TextView = itemView.findViewById(R.id.txt_vod_subtitle)
        private val txtBadge: TextView = itemView.findViewById(R.id.txt_vod_badge)

        fun bind(item: VodItem, isSelected: Boolean) {
            txtTitle.text = item.name

            val ratingText = item.getFormattedRating()
            val extraText = when {
                !item.year.isNullOrEmpty() -> item.year
                !item.genre.isNullOrEmpty() -> item.genre
                item.isSeries -> "Série Completa"
                else -> item.categoryName.ifEmpty { "Filme" }
            }
            txtSubtitle.text = "$ratingText • $extraText"

            txtBadge.text = when {
                item.isSeries -> "SÉRIE"
                item.name.contains("4K", ignoreCase = true) -> "4K"
                else -> "FHD"
            }

            // Carrega Poster via Coil de forma segura
            if (!item.posterUrl.isNullOrEmpty()) {
                imgPoster.load(item.posterUrl) {
                    crossfade(true)
                    placeholder(R.drawable.logo)
                    error(R.drawable.logo)
                }
            } else {
                imgPoster.setImageResource(R.drawable.logo)
            }

            itemView.isSelected = isSelected

            // Foco 100% seguro sem chamar notifyItemChanged durante o layout pass!
            itemView.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    selectedPosition = bindingAdapterPosition
                    itemView.isSelected = true
                    txtTitle.setTextColor(Color.parseColor("#00E5FF"))
                    onItemSelected(item)
                } else {
                    itemView.isSelected = false
                    txtTitle.setTextColor(Color.parseColor("#E6EDF8"))
                }
            }

            itemView.setOnClickListener {
                onItemClicked(item)
            }

            itemView.setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN) {
                    when (keyCode) {
                        KeyEvent.KEYCODE_DPAD_UP -> {
                            if (bindingAdapterPosition == 0) {
                                onNavigateUpToTabs?.invoke()
                                true
                            } else false
                        }
                        KeyEvent.KEYCODE_DPAD_LEFT -> {
                            onNavigateToCategories()
                            true
                        }
                        KeyEvent.KEYCODE_DPAD_RIGHT -> {
                            onNavigateToDetails()
                            true
                        }
                        KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                            onItemClicked(item)
                            true
                        }
                        else -> false
                    }
                } else false
            }
        }
    }
}
