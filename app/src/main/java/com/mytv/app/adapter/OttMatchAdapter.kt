package com.mytv.app.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mytv.app.R
import com.mytv.app.model.Channel

data class OttMatchItem(
    val id: String,
    val teamA: String,
    val teamB: String,
    val time: String,
    val champ: String,
    val channel: Channel? = null
)

class OttMatchAdapter(
    private var matches: List<OttMatchItem>,
    private val onMatchClick: (OttMatchItem) -> Unit
) : RecyclerView.Adapter<OttMatchAdapter.MatchViewHolder>() {

    class MatchViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtChamp: TextView = view.findViewById(R.id.txt_match_champ)
        val txtStatus: TextView = view.findViewById(R.id.txt_match_status)
        val txtTeamA: TextView = view.findViewById(R.id.txt_match_team_a)
        val txtTeamB: TextView = view.findViewById(R.id.txt_match_team_b)
        val viewFocusRing: View = view.findViewById(R.id.view_match_focus_ring)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ott_match_card, parent, false)
        return MatchViewHolder(view)
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        val match = matches[position]

        holder.txtChamp.text = match.champ
        holder.txtTeamA.text = match.teamA
        holder.txtTeamB.text = match.teamB
        holder.txtStatus.text = match.time

        if (match.time == "AO VIVO" || match.time.contains("VIVO", ignoreCase = true)) {
            holder.txtStatus.setBackgroundResource(R.drawable.badge_live)
            holder.txtStatus.setTextColor(Color.WHITE)
        } else {
            holder.txtStatus.setBackgroundResource(R.drawable.badge_hd)
            holder.txtStatus.setTextColor(Color.parseColor("#CBD5E1"))
        }

        // Foco e Escala Suave D-Pad
        holder.itemView.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                holder.itemView.animate()
                    .scaleX(1.05f)
                    .scaleY(1.05f)
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
            onMatchClick(match)
        }
    }

    override fun getItemCount(): Int = matches.size

    fun updateMatches(newMatches: List<OttMatchItem>) {
        this.matches = newMatches
        notifyDataSetChanged()
    }
}
