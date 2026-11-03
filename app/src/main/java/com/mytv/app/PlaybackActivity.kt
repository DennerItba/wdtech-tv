package com.mytv.app

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.mytv.app.adapter.PlaybackChannelAdapter
import com.mytv.app.adapter.PlaybackDaysAdapter
import com.mytv.app.adapter.PlaybackProgramsAdapter
import com.mytv.app.data.ChannelRepository
import com.mytv.app.data.EpgRepository
import com.mytv.app.data.PlaybackRepository
import com.mytv.app.model.Channel
import com.mytv.app.model.ChannelCategory
import com.mytv.app.model.EpgProgram
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class PlaybackActivity : AppCompatActivity() {

    private lateinit var rvChannels: RecyclerView
    private lateinit var rvDays: RecyclerView
    private lateinit var rvPrograms: RecyclerView

    private lateinit var channelAdapter: PlaybackChannelAdapter
    private lateinit var daysAdapter: PlaybackDaysAdapter
    private lateinit var programsAdapter: PlaybackProgramsAdapter

    private lateinit var txtCategoryTitle: TextView
    private lateinit var txtDetailDuration: TextView
    private lateinit var txtDetailTitle: TextView
    private lateinit var txtDetailDescription: TextView
    private lateinit var imgDetailLogo: ImageView
    private lateinit var txtBreadcrumb: TextView
    private lateinit var txtMonth: TextView
    private lateinit var txtChannelCount: TextView
    private lateinit var btnClose: Button
    private lateinit var btnReload: Button
    private lateinit var layoutCategoryHeader: LinearLayout

    private var currentChannel: Channel? = null
    private var allPlaybackChannels: List<Channel> = emptyList()
    private var categories: List<ChannelCategory> = emptyList()
    private var currentCategoryIndex: Int = 0
    private var currentDayOffset: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playback)

        rvChannels = findViewById(R.id.rv_playback_channels)
        rvDays = findViewById(R.id.rv_playback_days)
        rvPrograms = findViewById(R.id.rv_playback_programs)

        txtCategoryTitle = findViewById(R.id.txt_playback_category_title)
        txtDetailDuration = findViewById(R.id.txt_detail_duration)
        txtDetailTitle = findViewById(R.id.txt_detail_title)
        txtDetailDescription = findViewById(R.id.txt_detail_description)
        imgDetailLogo = findViewById(R.id.img_playback_detail_logo)
        txtBreadcrumb = findViewById(R.id.txt_playback_breadcrumb)
        txtMonth = findViewById(R.id.txt_playback_month)
        txtChannelCount = findViewById(R.id.txt_playback_channel_count)
        btnClose = findViewById(R.id.btn_playback_close)
        btnReload = findViewById(R.id.btn_playback_reload)
        layoutCategoryHeader = findViewById(R.id.layout_playback_category_header)

        btnClose.setOnClickListener { finish() }
        btnReload.setOnClickListener {
            loadProgramsForCurrentDay()
            Toast.makeText(this, "🔄 Guia de programação atualizado!", Toast.LENGTH_SHORT).show()
        }
        layoutCategoryHeader.setOnClickListener {
            cycleNextCategory()
        }

        // Mês atual em maiúsculas (ex: OUT ou NOV)
        val cal = Calendar.getInstance(TimeZone.getTimeZone("America/Sao_Paulo"))
        val monthFormat = SimpleDateFormat("MMM", Locale("pt", "BR")).apply {
            timeZone = TimeZone.getTimeZone("America/Sao_Paulo")
        }
        txtMonth.text = monthFormat.format(cal.time).replace(".", "").uppercase(Locale("pt", "BR"))

        // Carrega a lista de categorias e canais disponíveis para Playback
        loadPlaybackChannels()
        setupDaysColumn()
        setupProgramsColumn()
        setupChannelsColumn()

        // Seleciona o canal inicial e dia Hoje
        val initialChannel = currentChannel ?: allPlaybackChannels.firstOrNull()
        if (initialChannel != null) {
            selectChannel(initialChannel)
        }

        // Foco inicial garantido na coluna de canais
        rvChannels.post {
            val vh = rvChannels.findViewHolderForAdapterPosition(channelAdapter.getSelectedPosition())
            vh?.itemView?.requestFocus()
        }
    }

    private fun loadPlaybackChannels() {
        val extraChannel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(EXTRA_SELECTED_CHANNEL, Channel::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(EXTRA_SELECTED_CHANNEL) as? Channel
        }

        val extraList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(EXTRA_CHANNEL_LIST, ArrayList::class.java) as? ArrayList<Channel>
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(EXTRA_CHANNEL_LIST) as? ArrayList<Channel>
        }

        // Categorias completas para alternar facilmente com o controle remoto
        val repoCats = ChannelRepository.getCategories().filter { !it.isLocked }
        val allCat = ChannelCategory(
            id = "cat_all",
            name = "Todos os Canais",
            isLocked = false,
            channels = repoCats.flatMap { it.channels }
        )
        this.categories = listOf(allCat) + repoCats

        val baseChannels = if (!extraList.isNullOrEmpty()) {
            extraList
        } else {
            repoCats.flatMap { it.channels }
        }

        // Enriquece canais com EPG e Playback
        val enrichedList = baseChannels.map { EpgRepository.enrichChannel(it) }

        this.allPlaybackChannels = enrichedList
        this.currentChannel = extraChannel?.let { EpgRepository.enrichChannel(it) } ?: allPlaybackChannels.firstOrNull()

        // Sincroniza categoria inicial
        currentCategoryIndex = categories.indexOfFirst { cat ->
            cat.channels.any { it.id == currentChannel?.id }
        }.coerceAtLeast(0)

        val catName = categories.getOrNull(currentCategoryIndex)?.name ?: currentChannel?.category?.ifEmpty { "CANAIS ABERTOS" } ?: "CANAIS ABERTOS"
        txtCategoryTitle.text = catName.uppercase()

        val initialIdx = allPlaybackChannels.indexOfFirst { it.id == currentChannel?.id }.coerceAtLeast(0)
        txtChannelCount.text = "${initialIdx + 1}/${allPlaybackChannels.size}"
    }

    private fun cycleNextCategory() {
        if (categories.isEmpty()) return
        val nextIdx = (currentCategoryIndex + 1) % categories.size
        switchCategory(nextIdx)
    }

    private fun cyclePrevCategory() {
        if (categories.isEmpty()) return
        val prevIdx = if (currentCategoryIndex - 1 < 0) categories.size - 1 else currentCategoryIndex - 1
        switchCategory(prevIdx)
    }

    private fun switchCategory(categoryIdx: Int) {
        currentCategoryIndex = categoryIdx.coerceIn(0, categories.size - 1)
        val selectedCat = categories[currentCategoryIndex]
        txtCategoryTitle.text = selectedCat.name.uppercase()

        val enriched = selectedCat.channels.map { EpgRepository.enrichChannel(it) }
        this.allPlaybackChannels = enriched

        val targetCh = enriched.firstOrNull()
        channelAdapter.updateChannels(enriched, targetCh?.id)
        if (targetCh != null) {
            selectChannel(targetCh)
        }
        rvChannels.scrollToPosition(0)
    }

    private fun setupChannelsColumn() {
        rvChannels.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        channelAdapter = PlaybackChannelAdapter(
            channels = allPlaybackChannels,
            onChannelSelected = { channel ->
                selectChannel(channel)
            },
            onNavigateToDays = {
                focusDays()
            },
            onNavigateLeftToCategory = {
                cyclePrevCategory()
            },
            onNavigateUpToClose = {
                layoutCategoryHeader.requestFocus()
            }
        )
        channelAdapter.updateChannels(allPlaybackChannels, currentChannel?.id)
        rvChannels.adapter = channelAdapter

        val initialIdx = allPlaybackChannels.indexOfFirst { it.id == currentChannel?.id }.coerceAtLeast(0)
        if (initialIdx > 0) {
            rvChannels.scrollToPosition(initialIdx)
        }
    }

    private fun setupDaysColumn() {
        rvDays.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        daysAdapter = PlaybackDaysAdapter(
            onDaySelected = { dayOffset ->
                this.currentDayOffset = dayOffset
                loadProgramsForCurrentDay()
            },
            onNavigateToChannels = {
                focusChannels()
            },
            onNavigateToPrograms = {
                focusPrograms()
            }
        )
        rvDays.adapter = daysAdapter
    }

    private fun setupProgramsColumn() {
        rvPrograms.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        programsAdapter = PlaybackProgramsAdapter(
            programs = emptyList(),
            onProgramFocused = { program ->
                updateProgramDetails(program)
            },
            onProgramClicked = { program ->
                playProgram(program)
            },
            onNavigateToDays = {
                focusDays()
            }
        )
        rvPrograms.adapter = programsAdapter
    }

    private fun selectChannel(channel: Channel) {
        this.currentChannel = channel
        val catName = categories.getOrNull(currentCategoryIndex)?.name ?: channel.category.ifEmpty { "CANAIS ABERTOS" }
        txtCategoryTitle.text = catName.uppercase()

        val currentIdx = allPlaybackChannels.indexOfFirst { it.id == channel.id }
        val displayIdx = if (currentIdx >= 0) currentIdx + 1 else 1
        txtChannelCount.text = "$displayIdx/${allPlaybackChannels.size}"

        txtBreadcrumb.text = "${channel.number} • ${channel.title} (Guia 7 Dias Gravados)"

        if (!channel.logoUrl.isNullOrEmpty()) {
            imgDetailLogo.load(channel.logoUrl) {
                crossfade(false)
                placeholder(R.drawable.logo)
                error(R.drawable.logo)
            }
        } else {
            imgDetailLogo.setImageResource(R.drawable.logo)
        }

        loadProgramsForCurrentDay()
    }

    private fun loadProgramsForCurrentDay() {
        val channel = currentChannel ?: return
        val programs = PlaybackRepository.getProgramHistory(channel, currentDayOffset)
        programsAdapter.updatePrograms(programs)

        val now = System.currentTimeMillis()
        var targetIdx = programs.indexOfFirst { it.startTimestamp <= now && now < it.endTimestamp }
        if (targetIdx < 0) {
            targetIdx = if (currentDayOffset == 0) {
                programs.indexOfLast { it.endTimestamp <= now }.coerceAtLeast(0)
            } else {
                0
            }
        }

        if (targetIdx in programs.indices) {
            rvPrograms.scrollToPosition(targetIdx)
            updateProgramDetails(programs[targetIdx])
        } else {
            val firstProg = programs.firstOrNull()
            if (firstProg != null) {
                updateProgramDetails(firstProg)
            } else {
                txtDetailDuration.text = "🕒 Sem gravações"
                txtDetailTitle.text = "Nenhum programa registrado para esta data"
                txtDetailDescription.text = "A transmissão deste canal não possui registros no guia deste dia."
            }
        }
    }

    private fun updateProgramDetails(program: EpgProgram) {
        txtDetailDuration.text = "🕒 Duração: ${program.startTime} - ${program.endTime}"
        txtDetailTitle.text = "📺 Programa: ${program.title}"
        txtDetailDescription.text = program.synopsis.ifEmpty {
            "Transmissão gravada na íntegra. Disponível para assistir do início pelo Playback na nuvem."
        }
    }

    private fun playProgram(program: EpgProgram) {
        val channel = currentChannel ?: return
        val now = System.currentTimeMillis()

        if (program.startTimestamp > now) {
            Toast.makeText(this, "⏰ Lembrete agendado para ${program.title} às ${program.startTime}", Toast.LENGTH_SHORT).show()
            return
        }

        val isLive = (program.startTimestamp <= now && now < program.endTimestamp)
        val offsetSeconds = if (isLive) 0L else ((now - program.startTimestamp) / 1000).coerceAtLeast(60)

        val msg = if (isLive) "🔴 Assistindo ao vivo: ${program.title}" else "▶ Reproduzindo: ${program.title}"
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

        val intent = Intent(this, PlayerActivity::class.java).apply {
            putExtra(PlayerActivity.EXTRA_CHANNEL, channel)
            putExtra(PlayerActivity.EXTRA_CHANNEL_LIST, ArrayList(allPlaybackChannels))
            putExtra(PlayerActivity.EXTRA_CHANNEL_INDEX, allPlaybackChannels.indexOfFirst { it.id == channel.id }.coerceAtLeast(0))
            putExtra(PlayerActivity.EXTRA_PLAYBACK_OFFSET, offsetSeconds)
            putExtra(PlayerActivity.EXTRA_PLAYBACK_TITLE, program.title)
        }
        startActivity(intent)
    }

    private fun focusChannels() {
        val pos = channelAdapter.getSelectedPosition().coerceAtLeast(0)
        rvChannels.scrollToPosition(pos)
        rvChannels.post {
            val vh = rvChannels.findViewHolderForAdapterPosition(pos)
            vh?.itemView?.requestFocus()
                ?: rvChannels.layoutManager?.findViewByPosition(pos)?.requestFocus()
        }
    }

    private fun focusDays() {
        val pos = currentDayOffset.coerceIn(0, 6)
        rvDays.scrollToPosition(pos)
        rvDays.post {
            val vh = rvDays.findViewHolderForAdapterPosition(pos)
            vh?.itemView?.requestFocus()
                ?: rvDays.layoutManager?.findViewByPosition(pos)?.requestFocus()
        }
    }

    private fun focusPrograms() {
        val channel = currentChannel ?: return
        val programs = PlaybackRepository.getProgramHistory(channel, currentDayOffset)
        val now = System.currentTimeMillis()
        var targetIdx = programs.indexOfFirst { it.startTimestamp <= now && now < it.endTimestamp }
        if (targetIdx < 0) {
            targetIdx = if (currentDayOffset == 0) programs.indexOfLast { it.endTimestamp <= now }.coerceAtLeast(0) else 0
        }
        val safePos = targetIdx.coerceIn(0, (programs.size - 1).coerceAtLeast(0))
        rvPrograms.scrollToPosition(safePos)
        rvPrograms.post {
            val vh = rvPrograms.findViewHolderForAdapterPosition(safePos)
            vh?.itemView?.requestFocus()
                ?: rvPrograms.layoutManager?.findViewByPosition(safePos)?.requestFocus()
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_BACK) {
            if (rvPrograms.hasFocus()) {
                focusDays()
                return true
            } else if (rvDays.hasFocus()) {
                focusChannels()
                return true
            } else {
                finish()
                return true
            }
        }
        return super.dispatchKeyEvent(event)
    }

    companion object {
        const val EXTRA_SELECTED_CHANNEL = "extra_selected_channel"
        const val EXTRA_CHANNEL_LIST = "extra_channel_list"
    }
}
