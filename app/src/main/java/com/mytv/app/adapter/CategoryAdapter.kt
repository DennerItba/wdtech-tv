package com.mytv.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mytv.app.R
import com.mytv.app.model.Channel
import com.mytv.app.model.ChannelCategory

class CategoryAdapter(
    private val categories: List<ChannelCategory>,
    private val onChannelFocused: (Channel) -> Unit,
    private val onChannelClicked: (Channel) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtTitle: TextView = view.findViewById(R.id.txt_category_title)
        val rvChannels: RecyclerView = view.findViewById(R.id.rv_category_channels)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_row, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.txtTitle.text = category.name

        holder.rvChannels.layoutManager = LinearLayoutManager(
            holder.itemView.context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        holder.rvChannels.adapter = ChannelAdapter(
            channels = category.channels,
            onChannelFocused = onChannelFocused,
            onChannelClicked = onChannelClicked
        )
    }

    override fun getItemCount(): Int = categories.size
}
