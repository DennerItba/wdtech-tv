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
        holder.txtName.text = if (cat.isLocked) "🔒 " + cat.name.uppercase() else cat.name.uppercase()
        holder.imgIcon.setImageResource(if (cat.iconRes != 0) cat.iconRes else R.drawable.outline_live_tv_24)

        if (cat.id == "cat_settings") {
            holder.txtCount.visibility = View.VISIBLE
            holder.txtCount.text = "CONFIG"
            holder.txtCount.setTextColor(Color.parseColor("#FFD700"))
            holder.imgIcon.setColorFilter(Color.parseColor("#00E5FF"))
        } else if (cat.channels.isEmpty() && (cat.id.startsWith("cat_playback") || cat.id.startsWith("cat_movies") || cat.id.startsWith("cat_series") || cat.id.startsWith("cat_back_live"))) {
            holder.txtCount.visibility = View.GONE
            holder.txtCount.setTextColor(Color.parseColor("#00E5FF"))
            holder.imgIcon.clearColorFilter()
        } else {
            holder.txtCount.visibility = View.VISIBLE
            holder.txtCount.text = cat.channels.size.toString()
            holder.txtCount.setTextColor(Color.parseColor("#00E5FF"))
            holder.imgIcon.clearColorFilter()
        }

        val isCurrentSelected = (position == selectedIndex)
        holder.itemView.isSelected = isCurrentSelected

        if (isCurrentSelected) {
            holder.txtName.setTextColor(Color.parseColor("#00E5FF"))
        } else {
            holder.txtName.setTextColor(Color.parseColor("#E6EDF8"))
        }

        // Foco visual suave: ao mover o cursor pelo menu principal, apenas destaca visualmente
        // NÃO abre o submenu nem troca de tela até que o usuário pressione OK / ENTER!
        holder.itemView.setOnFocusChangeListener { _, hasFocus ->
            val pos = holder.bindingAdapterPosition
            if (hasFocus) {
                holder.txtName.setTextColor(Color.parseColor("#00E5FF"))
                holder.itemView.isSelected = true
                if (pos != RecyclerView.NO_POSITION && pos < categories.size) {
                    selectedIndex = pos
                    onCategoryFocused(categories[pos])
                }
            } else {
                holder.itemView.isSelected = false
                holder.txtName.setTextColor(Color.parseColor("#E6EDF8"))
            }
        }

        // Navegação precisa pelo D-Pad do controle:
        // O submenu SÓ abre quando for feito o CLIQUE (ENTER / DPAD_CENTER)!
        // Seta para Direita apenas move o cursor para o conteúdo ao lado.
        holder.itemView.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                val pos = holder.bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION && pos < categories.size) {
                    when (keyCode) {
                        KeyEvent.KEYCODE_DPAD_CENTER,
                        KeyEvent.KEYCODE_ENTER,
                        KeyEvent.KEYCODE_NUMPAD_ENTER -> {
                            selectedIndex = pos
                            onCategoryClicked(categories[pos])
                            return@setOnKeyListener true
                        }
                        KeyEvent.KEYCODE_DPAD_RIGHT -> {
                            onNavigateRightToContent?.invoke()
                            return@setOnKeyListener true
                        }
                    }
                }
            }
            false
        }

        holder.itemView.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION && pos < categories.size) {
                selectedIndex = pos
                onCategoryClicked(categories[pos])
            }
        }
    }

    override fun getItemCount(): Int = categories.size

    fun getSelectedPosition(): Int = selectedIndex

    fun updateCategories(newCategories: List<ChannelCategory>) {
        this.categories = newCategories
        if (this.selectedIndex >= newCategories.size) {
            this.selectedIndex = 0
        }
        notifyDataSetChanged()
    }
}
