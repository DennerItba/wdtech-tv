package com.mytv.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mytv.app.R
import com.mytv.app.model.VodEpisode
import com.mytv.app.model.VodSeason

class SeasonAdapter(
    private var seasons: List<VodSeason> = emptyList(),
    private val onSeasonSelected: (VodSeason) -> Unit
) : RecyclerView.Adapter<SeasonAdapter.SeasonViewHolder>() {

    private var selectedIndex = 0

    fun updateSeasons(newSeasons: List<VodSeason>) {
        seasons = newSeasons
        selectedIndex = 0
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeasonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_series_season, parent, false)
        return SeasonViewHolder(view)
    }

    override fun onBindViewHolder(holder: SeasonViewHolder, position: Int) {
        val season = seasons[position]
        holder.bind(season, position == selectedIndex)
    }

    override fun getItemCount(): Int = seasons.size

    inner class SeasonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtSeason: TextView = itemView.findViewById(R.id.txt_season_tab)

        fun bind(season: VodSeason, isSelected: Boolean) {
            txtSeason.text = season.title
            txtSeason.isSelected = isSelected

            txtSeason.setOnClickListener {
                val prev = selectedIndex
                selectedIndex = bindingAdapterPosition
                notifyItemChanged(prev)
                notifyItemChanged(selectedIndex)
                onSeasonSelected(season)
            }

            txtSeason.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    val prev = selectedIndex
                    selectedIndex = bindingAdapterPosition
                    notifyItemChanged(prev)
                    notifyItemChanged(selectedIndex)
                    onSeasonSelected(season)
                }
            }
        }
    }
}

class EpisodeAdapter(
    private var episodes: List<VodEpisode> = emptyList(),
    private val onEpisodeClicked: (VodEpisode) -> Unit
) : RecyclerView.Adapter<EpisodeAdapter.EpisodeViewHolder>() {

    fun updateEpisodes(newEpisodes: List<VodEpisode>) {
        episodes = newEpisodes
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_series_episode, parent, false)
        return EpisodeViewHolder(view)
    }

    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        val episode = episodes[position]
        holder.bind(episode)
    }

    override fun getItemCount(): Int = episodes.size

    inner class EpisodeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtTitle: TextView = itemView.findViewById(R.id.txt_ep_title)
        private val txtDuration: TextView = itemView.findViewById(R.id.txt_ep_duration)

        fun bind(episode: VodEpisode) {
            txtTitle.text = episode.getDisplayName()
            txtDuration.text = episode.duration?.takeIf { it.isNotEmpty() } ?: "FHD"

            itemView.setOnClickListener {
                onEpisodeClicked(episode)
            }
        }
    }
}
