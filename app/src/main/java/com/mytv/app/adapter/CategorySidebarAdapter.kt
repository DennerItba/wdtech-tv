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
    private val onCategoryClicked: (ChannelCategory) -> Unit
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
        holder.txtCount.text = cat.channels.size.toString()
        holder.imgIcon.setImageResource(if (cat.iconRes != 0) cat.iconRes else R.drawable.outline_live_tv_24)

        val isCurrentSelected = (position == selectedIndex)
        holder.itemView.isSelected = isCurrentSelected

        if (isCurrentSelected) {
            holder.txtName.setTextColor(Color.parseColor("#00E5FF"))
        } else {
            holder.txtName.setTextColor(Color.parseColor("#E6EDF8"))
        }

        // Foco pelo controle remoto
        holder.itemView.setOnFocusChangeListener { _, hasFocus ->
            val pos = holder.bindingAdapterPosition
            if (hasFocus) {
                holder.txtName.setTextColor(Color.parseColor("#00E5FF"))
                if (pos != RecyclerView.NO_POSITION && pos < categories.size) {
                    val oldPos = selectedIndex
                    selectedIndex = pos
                    if (oldPos != pos) {
                        notifyItemChanged(oldPos)
                        notifyItemChanged(pos)
                    }
                    onCategoryFocused(categories[pos])
                }
            } else {
                if (pos != selectedIndex) {
                    holder.txtName.setTextColor(Color.parseColor("#E6EDF8"))
                }
            }
        }

        // Navegação precisa pelo D-Pad do controle
        holder.itemView.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                val pos = holder.bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION && pos < categories.size) {
                    when (keyCode) {
                        // Clicar OK ou D-Pad Direita: abre a categoria e joga o foco diretamente no canal 1
                        KeyEvent.KEYCODE_DPAD_RIGHT,
                        KeyEvent.KEYCODE_DPAD_CENTER,
                        KeyEvent.KEYCODE_ENTER,
                        KeyEvent.KEYCODE_NUMPAD_ENTER -> {
                            selectedIndex = pos
                            onCategoryClicked(categories[pos])
                            return@setOnKeyListener true
                        }
                        // D-Pad Cima no primeiro item: não escapa para fora do painel
                        KeyEvent.KEYCODE_DPAD_UP -> {
                            if (pos == 0) {
                                return@setOnKeyListener true
                            }
                        }
                        // D-Pad Baixo no último item: não escapa
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
