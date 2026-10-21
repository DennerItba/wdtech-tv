package com.mytv.app.presenter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.leanback.widget.Presenter
import coil.load
import com.mytv.app.R
import com.mytv.app.model.Channel

class CardPresenter : Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_channel_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val channel = item as? Channel ?: return
        val view = viewHolder.view

        val txtNumber = view.findViewById<TextView>(R.id.txt_channel_number)
        val imgLogo = view.findViewById<ImageView>(R.id.img_channel_logo)
        val txtFallback = view.findViewById<TextView>(R.id.txt_logo_fallback)
        val txtTitle = view.findViewById<TextView>(R.id.txt_channel_title)
        val txtCategory = view.findViewById<TextView>(R.id.txt_channel_category)

        txtTitle.text = channel.title
        txtCategory.text = "${channel.category ?: "Ao Vivo"} • FHD 1080p"
        
        // Número sequencial ou ID do canal
        val num = channel.id.filter { it.isDigit() }.take(2)
        txtNumber.text = if (num.isNotEmpty()) "CH $num" else "TV"

        if (!channel.logoUrl.isNullOrEmpty()) {
            txtFallback.visibility = View.GONE
            imgLogo.visibility = View.VISIBLE
            imgLogo.load(channel.logoUrl) {
                crossfade(true)
                listener(
                    onError = { _, _ ->
                        imgLogo.visibility = View.GONE
                        txtFallback.visibility = View.VISIBLE
                        txtFallback.text = channel.title.take(16).uppercase()
                    },
                    onSuccess = { _, _ ->
                        imgLogo.visibility = View.VISIBLE
                        txtFallback.visibility = View.GONE
                    }
                )
            }
        } else {
            imgLogo.visibility = View.GONE
            txtFallback.visibility = View.VISIBLE
            txtFallback.text = channel.title.take(16).uppercase()
        }

        // Animação de pop-out e zoom no foco do controle remoto
        view.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                v.animate().scaleX(1.10f).scaleY(1.10f).setDuration(120).start()
                v.elevation = 16f
            } else {
                v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(120).start()
                v.elevation = 0f
            }
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val view = viewHolder.view
        val imgLogo = view.findViewById<ImageView>(R.id.img_channel_logo)
        imgLogo.setImageDrawable(null)
    }
}
