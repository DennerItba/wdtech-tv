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
import com.mytv.app.model.ChannelCategory

class CategorySidebarAdapter(
    private var categories: List<ChannelCategory>,
    private val onCategoryFocused: (ChannelCategory) -> Unit,
    private val onCategoryClicked: (ChannelCategory) -> Unit,
    private val onNavigateRightToContent: (() -> Unit)? = null,
    private val onNavigateUpToTabs: (() -> Unit)? = null
) : RecyclerView.Adapter<CategorySidebarAdapter.CategoryViewHolder>() {

    private var selectedIndex = 0

    class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val viewIndicator: View = view.findViewById(R.id.view_indicator)
        val imgIcon: ImageView = view.findViewById(R.id.img_category_icon)
        val txtName: TextView = view.findViewById(R.id.txt_category_name)
        val txtCount: TextView = view.findViewById(R.id.txt_category_count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sidebar_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val cat = categories[position]
        holder.txtName.text = if (cat.isLocked) "🔒 " + cat.name else cat.name
        holder.imgIcon.setImageResource(if (cat.iconRes != 0) cat.iconRes else R.drawable.outline_live_tv_24)

        if (cat.id == "cat_settings" || cat.id == "cat_ajustes") {
            holder.txtCount.visibility = View.GONE
            holder.imgIcon.setColorFilter(Color.parseColor("#00E5FF"))
        } else if (cat.channels.isEmpty()) {
            holder.txtCount.visibility = View.GONE
            holder.imgIcon.clearColorFilter()
        } else {
            holder.txtCount.visibility = View.VISIBLE
            holder.txtCount.text = cat.channels.size.toString()
            holder.txtCount.setTextColor(Color.parseColor("#00E5FF"))
            holder.imgIcon.clearColorFilter()
        }

        val isCurrentSelected = (position == selectedIndex)
        holder.itemView.isSelected = isCurrentSelected
        holder.viewIndicator.visibility = if (isCurrentSelected) View.VISIBLE else View.INVISIBLE

        if (isCurrentSelected) {
            holder.txtName.setTextColor(Color.parseColor("#00E5FF"))
        } else {
            holder.txtName.setTextColor(Color.parseColor("#E6EDF8"))
        }

        // Foco visual suave: ao mover o cursor pelo menu principal, apenas destaca visualmente
        // e atualiza a coluna de canais SEM roubar o foco da navegação!
        holder.itemView.setOnFocusChangeListener { _, hasFocus ->
            val pos = holder.bindingAdapterPosition
            if (hasFocus) {
                holder.txtName.setTextColor(Color.parseColor("#00E5FF"))
                holder.viewIndicator.visibility = View.VISIBLE
                if (pos != RecyclerView.NO_POSITION && pos < categories.size) {
                    val oldIndex = selectedIndex
                    selectedIndex = pos
                    notifyItemChanged(oldIndex)
                    notifyItemChanged(selectedIndex)
                    onCategoryFocused(categories[pos])
                }
            } else {
                val isSel = (pos == selectedIndex)
                holder.viewIndicator.visibility = if (isSel) View.VISIBLE else View.INVISIBLE
                holder.txtName.setTextColor(if (isSel) Color.parseColor("#00E5FF") else Color.parseColor("#E6EDF8"))
            }
        }

        holder.itemView.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION && pos < categories.size) {
                val oldIndex = selectedIndex
                selectedIndex = pos
                notifyItemChanged(oldIndex)
                notifyItemChanged(selectedIndex)
                onCategoryClicked(categories[pos])
            }
        }

        holder.itemView.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                val pos = holder.bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION && pos < categories.size) {
                    when (keyCode) {
                        KeyEvent.KEYCODE_DPAD_RIGHT -> {
                            val oldIndex = selectedIndex
                            selectedIndex = pos
                            notifyItemChanged(oldIndex)
                            notifyItemChanged(selectedIndex)
                            onCategoryClicked(categories[pos])
                            onNavigateRightToContent?.invoke()
                            return@setOnKeyListener true
                        }
                        KeyEvent.KEYCODE_DPAD_CENTER,
                        KeyEvent.KEYCODE_ENTER,
                        KeyEvent.KEYCODE_NUMPAD_ENTER -> {
                            val oldIndex = selectedIndex
                            selectedIndex = pos
                            notifyItemChanged(oldIndex)
                            notifyItemChanged(selectedIndex)
                            onCategoryClicked(categories[pos])
                            onNavigateRightToContent?.invoke()
                            return@setOnKeyListener true
                        }
                        KeyEvent.KEYCODE_DPAD_UP -> {
                            if (pos == 0) {
                                return@setOnKeyListener true
                            }
                        }
                        KeyEvent.KEYCODE_DPAD_DOWN -> {
                            if (pos == categories.size - 1) {
                                return@setOnKeyListener true
                            }
                        }
                    }
                }
            }
            false
        }
    }

    override fun getItemCount(): Int = categories.size

    fun updateCategories(newCategories: List<ChannelCategory>) {
        this.categories = newCategories
        if (selectedIndex >= newCategories.size) {
            selectedIndex = 0
        }
        notifyDataSetChanged()
    }

    fun getSelectedPosition(): Int = selectedIndex

    fun setSelectedCategory(category: ChannelCategory) {
        val index = categories.indexOfFirst { it.id == category.id }
        if (index != -1 && index != selectedIndex) {
            val old = selectedIndex
            selectedIndex = index
            notifyItemChanged(old)
            notifyItemChanged(selectedIndex)
        }
    }
}
