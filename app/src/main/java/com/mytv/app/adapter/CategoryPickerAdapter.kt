package com.mytv.app.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mytv.app.R
import com.mytv.app.model.ChannelCategory

class CategoryPickerAdapter(
    private val categories: List<ChannelCategory>,
    private val currentSelectedId: String?,
    private val onCategorySelected: (ChannelCategory) -> Unit
) : RecyclerView.Adapter<CategoryPickerAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val indicator: View = view.findViewById(R.id.view_picker_indicator)
        val icon: ImageView = view.findViewById(R.id.img_picker_icon)
        val title: TextView = view.findViewById(R.id.txt_picker_category_name)
        val count: TextView = view.findViewById(R.id.txt_picker_category_count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_picker, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cat = categories[position]
        holder.title.text = cat.name
        holder.count.text = cat.channels.size.toString()
        holder.icon.setImageResource(cat.iconRes)

        val isSelected = cat.id == currentSelectedId
        holder.indicator.visibility = if (isSelected) View.VISIBLE else View.GONE
        if (isSelected) {
            holder.title.setTextColor(Color.parseColor("#00E5FF"))
        } else {
            holder.title.setTextColor(Color.parseColor("#FFFFFF"))
        }

        holder.itemView.setOnClickListener {
            onCategorySelected(cat)
        }

        holder.itemView.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                view.animate().scaleX(1.03f).scaleY(1.03f).setDuration(120).start()
                holder.title.setTextColor(Color.parseColor("#00E5FF"))
            } else {
                view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(120).start()
                if (cat.id == currentSelectedId) {
                    holder.title.setTextColor(Color.parseColor("#00E5FF"))
                } else {
                    holder.title.setTextColor(Color.parseColor("#FFFFFF"))
                }
            }
        }
    }

    override fun getItemCount(): Int = categories.size
}
