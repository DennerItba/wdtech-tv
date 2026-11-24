package com.mytv.app.adapter

import android.graphics.Color
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mytv.app.R
import com.mytv.app.model.SettingItem

class SettingListAdapter(
    private var items: List<SettingItem>,
    private val onSettingFocused: (SettingItem) -> Unit,
    private val onSettingClicked: (SettingItem) -> Unit,
    private val onNavigateToCategories: () -> Unit,
    private val onNavigateToPreview: () -> Unit,
    private val onNavigateUpToTabs: (() -> Unit)? = null
) : RecyclerView.Adapter<SettingListAdapter.SettingViewHolder>() {

    private var selectedIndex = 0

    class SettingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgIcon: ImageView = view.findViewById(R.id.img_setting_icon)
        val txtTitle: TextView = view.findViewById(R.id.txt_setting_title)
        val txtSubtitle: TextView = view.findViewById(R.id.txt_setting_subtitle)
        val txtBadge: TextView = view.findViewById(R.id.txt_setting_badge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_setting_list_row, parent, false)
        return SettingViewHolder(view)
    }

    override fun onBindViewHolder(holder: SettingViewHolder, position: Int) {
        val item = items[position]
        holder.txtTitle.text = item.title
        holder.txtSubtitle.text = item.subtitle
        holder.txtBadge.text = item.badgeText
        holder.imgIcon.setImageResource(item.iconRes)

        val isSelected = (position == selectedIndex)
        holder.itemView.isSelected = isSelected

        holder.itemView.setOnFocusChangeListener { _, hasFocus ->
            val pos = holder.bindingAdapterPosition
            if (hasFocus) {
                holder.txtTitle.setTextColor(Color.parseColor("#00E5FF"))
                holder.itemView.isSelected = true
                if (pos != RecyclerView.NO_POSITION && pos < items.size) {
                    selectedIndex = pos
                    onSettingFocused(items[pos])
                }
            } else {
                holder.itemView.isSelected = false
                holder.txtTitle.setTextColor(Color.parseColor("#FFFFFF"))
            }
        }

        holder.itemView.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                val pos = holder.bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION && pos < items.size) {
                    when (keyCode) {
                        KeyEvent.KEYCODE_DPAD_UP -> {
                            if (pos == 0) {
                                onNavigateUpToTabs?.invoke()
                                return@setOnKeyListener true
                            }
                        }
                        KeyEvent.KEYCODE_DPAD_CENTER,
                        KeyEvent.KEYCODE_ENTER,
                        KeyEvent.KEYCODE_NUMPAD_ENTER -> {
                            selectedIndex = pos
                            onSettingClicked(items[pos])
                            return@setOnKeyListener true
                        }
                        KeyEvent.KEYCODE_DPAD_LEFT -> {
                            onNavigateToCategories()
                            return@setOnKeyListener true
                        }
                        KeyEvent.KEYCODE_DPAD_RIGHT -> {
                            onNavigateToPreview()
                            return@setOnKeyListener true
                        }
                    }
                }
            }
            false
        }

        holder.itemView.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION && pos < items.size) {
                selectedIndex = pos
                onSettingClicked(items[pos])
            }
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<SettingItem>) {
        this.items = newItems
        notifyDataSetChanged()
    }

    fun getItems(): List<SettingItem> = items
}
