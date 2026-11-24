package com.mytv.app.data

import com.mytv.app.model.Channel
import com.mytv.app.model.ChannelCategory
import com.mytv.app.model.MainSection
import com.mytv.app.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

object ChannelRepository {

    fun getCategories(): List<ChannelCategory> {
        return listOf(
            ChannelCategory(
                id = "cat_fazenda",
                name = "A Fazenda 24H (7 Câmeras)",
                isLocked = false,
                channels = listOf(
                    Channel(
                        id = "ch_fazenda_1",
                        number = "07",
                        title = "A Fazenda 24H - Camera 1 (Sede Principal)",
                        category = "A Fazenda 24H (7 Câmeras)",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5770051.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/pt/thumb/d/d7/Logotipo_de_A_Fazenda.png/320px-Logotipo_de_A_Fazenda.png",
                        description = "Camera principal ao vivo 24 horas por dia dentro da sede de A Fazenda com todos os peoes.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5770051.ts", "http://gelosalgados.site:80/live/59471019/60849037/5770051.m3u8")
                    ),
                    Channel(
                        id = "ch_fazenda_2",
                        number = "08",
                        title = "A Fazenda 24H - Camera 2 (Baia & Animais)",
                        category = "A Fazenda 24H (7 Câmeras)",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5770052.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/pt/thumb/d/d7/Logotipo_de_A_Fazenda.png/320px-Logotipo_de_A_Fazenda.png",
                        description = "Camera exclusiva na baia dos animais, rotina rural e conversas reservadas.",
                        quality = "HD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5770052.ts", "http://gelosalgados.site:80/live/59471019/60849037/5770052.m3u8")
                    ),
                    Channel(
                        id = "ch_fazenda_3",
                        number = "09",
                        title = "A Fazenda 24H - Camera 3 (Oficina & Varanda)",
                        category = "A Fazenda 24H (7 Câmeras)",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5770053.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/pt/thumb/d/d7/Logotipo_de_A_Fazenda.png/320px-Logotipo_de_A_Fazenda.png",
                        description = "Acompanhe as estrategias e reunioes secretas na area da varanda e oficina.",
                        quality = "HD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5770053.ts", "http://gelosalgados.site:80/live/59471019/60849037/5770053.m3u8")
                    ),
                    Channel(
                        id = "ch_fazenda_4",
                        number = "10",
                        title = "A Fazenda 24H - Camera 4 (Cozinha & Sala)",
                        category = "A Fazenda 24H (7 Câmeras)",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5770054.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/pt/thumb/d/d7/Logotipo_de_A_Fazenda.png/320px-Logotipo_de_A_Fazenda.png",
                        description = "Toda a movimentacao das refeicoes, discussoes na cozinha e momentos na sala de estar.",
                        quality = "HD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5770054.ts", "http://gelosalgados.site:80/live/59471019/60849037/5770054.m3u8")
                    ),
                    Channel(
                        id = "ch_fazenda_5",
                        number = "11",
                        title = "A Fazenda 24H - Camera 5 (Piscina & Academia)",
                        category = "A Fazenda 24H (7 Câmeras)",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5770055.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/pt/thumb/d/d7/Logotipo_de_A_Fazenda.png/320px-Logotipo_de_A_Fazenda.png",
                        description = "Area externa, treinos na academia, banho de sol na piscina e conversas quentes.",
                        quality = "HD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5770055.ts", "http://gelosalgados.site:80/live/59471019/60849037/5770055.m3u8")
                    ),
                    Channel(
                        id = "ch_fazenda_6",
                        number = "12",
                        title = "A Fazenda 24H - Camera 6 (Quartos & Espelho)",
                        category = "A Fazenda 24H (7 Câmeras)",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5770056.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/pt/thumb/d/d7/Logotipo_de_A_Fazenda.png/320px-Logotipo_de_A_Fazenda.png",
                        description = "Transmissao ao vivo dos quartos, pre-festas, fofocas e descanso dos peoes.",
                        quality = "HD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5770056.ts", "http://gelosalgados.site:80/live/59471019/60849037/5770056.m3u8")
                    ),
                    Channel(
                        id = "ch_fazenda_7",
                        number = "13",
                        title = "A Fazenda 24H - Camera 7 (Mosaico Multicameras)",
                        category = "A Fazenda 24H (7 Câmeras)",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5770057.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/b/b4/Logo_A_Fazenda_-_Atual.png",
                        description = "Visao panoramica mosaico transmitindo todos os cantos da fazenda simultaneamente.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5770057.ts", "http://gelosalgados.site:80/live/59471019/60849037/5770057.m3u8")
                    )

                )
            ),
                        ChannelCategory(
                id = "cat_24h",
                name = "Canais 24 Horas",
                isLocked = false,
                channels = listOf(
                    Channel(
                        id = "ch_24h_chaves",
                        number = "2401",
                        title = "24H Chaves",
                        category = "Canais 24 Horas",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5770144.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/1/14/Logo_Chaves.png/320px-Logo_Chaves.png",
                        description = "Transmissao oficial 24h sem intervalos com todos os episodios classicos da vila do Chaves.",
                        quality = "HD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5770144.ts", "http://gelosalgados.site:80/live/59471019/60849037/5770144.m3u8")
                    ),
                    Channel(
                        id = "ch_24h_chapolin",
                        number = "2402",
                        title = "24H Chapolin Colorado",
                        category = "Canais 24 Horas",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5770171.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/ca/Chapolin_Colorado_logo.png/320px-Chapolin_Colorado_logo.png",
                        description = "Nao contavam com a minha astucia! O heroi mais amado da TV 24 horas no ar.",
                        quality = "HD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5770171.ts", "http://gelosalgados.site:80/live/59471019/60849037/5770171.m3u8")
                    ),
                    Channel(
                        id = "ch_24h_simpsons",
                        number = "2403",
                        title = "24H Os Simpsons",
                        category = "Canais 24 Horas",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769977.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/02/The_Simpsons_Logo.svg/320px-The_Simpsons_Logo.svg.png",
                        description = "Todas as temporadas de Homer, Marge, Bart, Lisa e Maggie em Springfield 24h por dia.",
                        quality = "HD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769977.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769977.m3u8")
                    ),
                    Channel(
                        id = "ch_24h_chris",
                        number = "2404",
                        title = "24H Todo Mundo Odeia o Chris",
                        category = "Canais 24 Horas",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5770116.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2f/Everybody_Hates_Chris_title.png/320px-Everybody_Hates_Chris_title.png",
                        description = "Julius, Rochelle, Tonya, Drew e Greg nas aventuras mais engracadas de Nova York.",
                        quality = "HD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5770116.ts", "http://gelosalgados.site:80/live/59471019/60849037/5770116.m3u8")
                    ),
                    Channel(
                        id = "ch_24h_friends",
                        number = "2405",
                        title = "24H Friends",
                        category = "Canais 24 Horas",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5770161.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/bc/Friends_logo.svg/320px-Friends_logo.svg.png",
                        description = "Rachel, Monica, Phoebe, Joey, Chandler e Ross no Central Perk 24 horas por dia.",
                        quality = "HD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5770161.ts", "http://gelosalgados.site:80/live/59471019/60849037/5770161.m3u8")
                    ),
                    Channel(
                        id = "ch_24h_dois_homens",
                        number = "2406",
                        title = "24H Dois Homens e Meio",
                        category = "Canais 24 Horas",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5770132.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/5/52/Two_and_a_Half_Men_logo.svg/320px-Two_and_a_Half_Men_logo.svg.png",
                        description = "Charlie Harper, Alan e Jake na comedia de maior sucesso da TV 24h sem parar.",
                        quality = "HD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5770132.ts", "http://gelosalgados.site:80/live/59471019/60849037/5770132.m3u8")
                    ),
                    Channel(
                        id = "ch_24h_bob_esponja",
                        number = "2407",
                        title = "24H Bob Esponja",
                        category = "Canais 24 Horas",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769921.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/87/SpongeBob_SquarePants_logo.svg/320px-SpongeBob_SquarePants_logo.svg.png",
                        description = "Na Fenda do Biquini com Bob Esponja, Patrick Estrela, Lula Molusco e Sr. Sirigueijo 24h.",
                        quality = "HD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769921.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769921.m3u8")
                    ),
                    Channel(
                        id = "ch_24h_pica_pau",
                        number = "2408",
                        title = "24H Pica Pau",
                        category = "Canais 24 Horas",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5770029.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e0/Woody_Woodpecker_logo.png/320px-Woody_Woodpecker_logo.png",
                        description = "O passaro mais biruta dos desenhos animados em suas classicas trapalhadas 24 horas.",
                        quality = "HD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5770029.ts", "http://gelosalgados.site:80/live/59471019/60849037/5770029.m3u8")
                    ),
                    Channel(
                        id = "ch_24h_dragon_ball",
                        number = "2409",
                        title = "24H Dragon Ball Z",
                        category = "Canais 24 Horas",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5770038.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/22/Dragon_Ball_Z_logo.svg/320px-Dragon_Ball_Z_logo.svg.png",
                        description = "Goku, Vegeta, Gohan e os Guerreiros Z nas batalhas mais epicas do anime 24h por dia.",
                        quality = "HD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5770038.ts", "http://gelosalgados.site:80/live/59471019/60849037/5770038.m3u8")
                    )
                )
            ),
            ChannelCategory(
                id = "cat_jogos_dia",
                name = "⚽ Jogos do Dia (Ao Vivo)",
                isLocked = false,
                channels = dynamicJogosDoDia ?: getFallbackJogosDoDia()
            ),
            ChannelCategory(
                id = "cat_premiere",
                name = "⚽ Premiere FC (Todos os Jogos)",
                isLocked = false,
                channels = listOf(
                    Channel(
                        id = "prem_clubes",
                        number = "49",
                        title = "Premiere Clubes FHD",
                        category = "⚽ Premiere FC (Todos os Jogos)",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769291.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d7/Premiere_logo.svg/320px-Premiere_logo.svg.png",
                        description = "O melhor do futebol brasileiro 24 horas por dia com jogos ao vivo e programas exclusivos.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769291.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769291.m3u8")
                    ),
                    Channel(
                        id = "prem_2",
                        number = "50",
                        title = "Premiere 2 FHD",
                        category = "⚽ Premiere FC (Todos os Jogos)",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769297.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d7/Premiere_logo.svg/320px-Premiere_logo.svg.png",
                        description = "Transmissões ao vivo das Séries A e B do Brasileirão e campeonatos regionais.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769297.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769297.m3u8")
                    ),
                    Channel(
                        id = "prem_3",
                        number = "51",
                        title = "Premiere 3 FHD",
                        category = "⚽ Premiere FC (Todos os Jogos)",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769303.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d7/Premiere_logo.svg/320px-Premiere_logo.svg.png",
                        description = "Partidas simultâneas ao vivo e transmissão completa dos principais clubes do Brasil.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769303.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769303.m3u8")
                    ),
                    Channel(
                        id = "prem_4",
                        number = "52",
                        title = "Premiere 4 FHD",
                        category = "⚽ Premiere FC (Todos os Jogos)",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769309.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d7/Premiere_logo.svg/320px-Premiere_logo.svg.png",
                        description = "Futebol ao vivo em alta definição direto dos estádios de todo o Brasil.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769309.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769309.m3u8")
                    ),
                    Channel(
                        id = "prem_5",
                        number = "53",
                        title = "Premiere 5 FHD",
                        category = "⚽ Premiere FC (Todos os Jogos)",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769315.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d7/Premiere_logo.svg/320px-Premiere_logo.svg.png",
                        description = "Canal de transmissão direta de partidas simultâneas dos campeonatos nacionais.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769315.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769315.m3u8")
                    ),
                    Channel(
                        id = "prem_6",
                        number = "54",
                        title = "Premiere 6 FHD",
                        category = "⚽ Premiere FC (Todos os Jogos)",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769321.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d7/Premiere_logo.svg/320px-Premiere_logo.svg.png",
                        description = "Cobertura completa dos jogos da Série B e confrontos regionais.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769321.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769321.m3u8")
                    ),
                    Channel(
                        id = "prem_7",
                        number = "55",
                        title = "Premiere 7 FHD",
                        category = "⚽ Premiere FC (Todos os Jogos)",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769327.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d7/Premiere_logo.svg/320px-Premiere_logo.svg.png",
                        description = "Canal pay-per-view exclusivo para transmissões de mata-mata e decisões.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769327.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769327.m3u8")
                    ),
                    Channel(
                        id = "prem_8",
                        number = "56",
                        title = "Premiere 8 FHD",
                        category = "⚽ Premiere FC (Todos os Jogos)",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769332.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d7/Premiere_logo.svg/320px-Premiere_logo.svg.png",
                        description = "Transmissão em tempo real de partidas complementares da rodada de futebol.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769332.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769332.m3u8")
                    )
                )
            ),
            ChannelCategory(
                id = "cat_campeonatos",
                name = "🏆 Campeonatos & Estaduais",
                isLocked = false,
                channels = listOf(
                    Channel(
                        id = "sportynet_1",
                        number = "57",
                        title = "SportyNet 1 FHD",
                        category = "🏆 Campeonatos & Estaduais",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5770393.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/320px-Soccerball.svg.png",
                        description = "Transmissões ao vivo dos Campeonatos Estaduais, divisões de acesso e copas regionais.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5770393.ts", "http://gelosalgados.site:80/live/59471019/60849037/5770393.m3u8")
                    ),
                    Channel(
                        id = "sportynet_2",
                        number = "58",
                        title = "SportyNet 2 FHD",
                        category = "🏆 Campeonatos & Estaduais",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769415.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/320px-Soccerball.svg.png",
                        description = "Canal complementar com múltiplos jogos ao vivo simultâneos dos campeonatos estaduais.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769415.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769415.m3u8")
                    ),
                    Channel(
                        id = "dazn_1",
                        number = "59",
                        title = "DAZN 1 FHD",
                        category = "🏆 Campeonatos & Estaduais",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/caze-tv-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.pngdazn.png",
                        description = "O serviço global de esportes: Série C, futebol europeu, boxe e torneios mundiais.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5770364.m3u8", "http://64.31.23.207:80/live/59471019/60849037/5770364.ts")
                    ),
                    Channel(
                        id = "fcf_tv_1",
                        number = "60",
                        title = "FCF TV 1 FHD",
                        category = "🏆 Campeonatos & Estaduais",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5770281.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/320px-Soccerball.svg.png",
                        description = "Transmissão oficial das federações estaduais com jogos ao vivo e copas do Nordeste.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5770281.ts", "http://gelosalgados.site:80/live/59471019/60849037/5770281.m3u8")
                    ),
                    Channel(
                        id = "xsports_fhd",
                        number = "61",
                        title = "X Sports FHD",
                        category = "🏆 Campeonatos & Estaduais",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5770403.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/320px-Soccerball.svg.png",
                        description = "Esportes alternativos, campeonatos internacionais e partidas ao vivo.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5770403.ts", "http://gelosalgados.site:80/live/59471019/60849037/5770403.m3u8")
                    ),
                    Channel(
                        id = "bandsports_campeonatos",
                        number = "62",
                        title = "BandSports FHD",
                        category = "🏆 Campeonatos & Estaduais",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769465.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.pngbandsports.png",
                        description = "Futebol internacional, campeonatos de base, tênis, automobilismo e esportes olímpicos.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769465.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769465.m3u8")
                    ),
                    Channel(
                        id = "combate_campeonatos",
                        number = "63",
                        title = "Combate FHD",
                        category = "🏆 Campeonatos & Estaduais",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769164.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.pngcombate.png",
                        description = "O canal oficial das lutas: UFC, Boxe, Jiu-Jitsu e grandes torneios de artes marciais.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769164.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769164.m3u8")
                    )
                )
            ),
            ChannelCategory(
                id = "cat_esportes",
                name = "📺 SporTV & Canais Esportivos",
                isLocked = false,
                channels = listOf(
                    Channel(
                        id = "sportv_1",
                        number = "64",
                        title = "SporTV FHD",
                        category = "📺 SporTV & Canais Esportivos",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769428.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.pngsportv.png",
                        description = "O canal campeão: cobertura ao vivo dos maiores campeonatos esportivos do Brasil e do mundo.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769428.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769428.m3u8")
                    ),
                    Channel(
                        id = "sportv_2",
                        number = "65",
                        title = "SporTV 2 FHD",
                        category = "📺 SporTV & Canais Esportivos",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769415.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.pngsportv2.png",
                        description = "Vôlei, basquete, tênis, automobilismo e futebol ao vivo todos os dias.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769415.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769415.m3u8")
                    ),
                    Channel(
                        id = "sportv_3",
                        number = "66",
                        title = "SporTV 3 FHD",
                        category = "📺 SporTV & Canais Esportivos",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769421.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.pngsportv3.png",
                        description = "Esportes olímpicos, atletismo, natação e transmissão simultânea de grandes torneios.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769421.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769421.m3u8")
                    ),
                    Channel(
                        id = "sportv_4",
                        number = "67",
                        title = "SporTV 4 FHD",
                        category = "📺 SporTV & Canais Esportivos",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/caze-tv-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.pngsportv2.png",
                        description = "Canal de apoio para eventos simultâneos e rodadas decisivas em alta resolução.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769426.m3u8", "http://64.31.23.207:80/live/59471019/60849037/5769426.ts")
                    ),
                    Channel(
                        id = "bandsports_fhd",
                        number = "68",
                        title = "BandSports FHD",
                        category = "📺 SporTV & Canais Esportivos",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769465.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.pngbandsports.png",
                        description = "Futebol internacional, treinos de Fórmula 1, lutas e esportes radicais ao vivo.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769465.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769465.m3u8")
                    ),
                    Channel(
                        id = "combate_fhd",
                        number = "69",
                        title = "Combate FHD",
                        category = "📺 SporTV & Canais Esportivos",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769164.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.pngcombate.png",
                        description = "O canal oficial das artes marciais: UFC, Boxe, Jiu-Jitsu, Kickboxing e Jungle Fight 24H.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769164.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769164.m3u8")
                    )
                )
            ),
            ChannelCategory(
                id = "cat_espn",
                name = "🌐 Rede ESPN (Champions & Ligas)",
                isLocked = false,
                channels = listOf(
                    Channel(
                        id = "espn_fhd",
                        number = "70",
                        title = "ESPN FHD",
                        category = "🌐 Rede ESPN (Champions & Ligas)",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/espn-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.pngespn.png",
                        description = "O lider mundial em esportes: Premier League, LaLiga, Serie A italiana e NFL.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769472.m3u8", "http://64.31.23.207:80/live/59471019/60849037/5769472.ts")
                    ),
                    Channel(
                        id = "espn_2",
                        number = "71",
                        title = "ESPN 2 FHD",
                        category = "🌐 Rede ESPN (Champions & Ligas)",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/espn-2-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.pngespn2.png",
                        description = "Futebol europeu, NBA, beisebol da MLB, college football e programas esportivos de debate.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769620.m3u8", "http://64.31.23.207:80/live/59471019/60849037/5769620.ts")
                    ),
                    Channel(
                        id = "espn_3",
                        number = "72",
                        title = "ESPN 3 FHD",
                        category = "🌐 Rede ESPN (Champions & Ligas)",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/espn-3-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.pngespn.png",
                        description = "Tenis com Grand Slams ao vivo, ciclismo, esportes a motor e jogos internacionais.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769626.m3u8", "http://64.31.23.207:80/live/59471019/60849037/5769626.ts")
                    ),
                    Channel(
                        id = "espn_4",
                        number = "73",
                        title = "ESPN 4 FHD",
                        category = "🌐 Rede ESPN (Champions & Ligas)",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769631.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.pngfoxsports1.png",
                        description = "A casa da Copa Libertadores da América, Copa Sul-Americana e futebol sul-americano.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769631.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769631.m3u8")
                    ),
                    Channel(
                        id = "espn_5",
                        number = "74",
                        title = "ESPN 5 FHD",
                        category = "🌐 Rede ESPN (Champions & Ligas)",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/espn-5-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.pngfoxsports2.png",
                        description = "Cobertura multiplataforma e grandes eventos de esportes de combate e automobilismo.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769635.m3u8", "http://64.31.23.207:80/live/59471019/60849037/5769635.ts")
                    ),
                    Channel(
                        id = "espn_6",
                        number = "75",
                        title = "ESPN 6 FHD",
                        category = "🌐 Rede ESPN (Champions & Ligas)",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/espn-6-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.pngespnextra.png",
                        description = "Transmissoes simultaneas de rodadas europeias, campeonatos de rugby e esportes radicais.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769642.m3u8", "http://64.31.23.207:80/live/59471019/60849037/5769642.ts")
                    )
                )
            ),
            ChannelCategory(
                id = "cat_telecine",
                name = "Rede Telecine",
                isLocked = false,
                channels = listOf(
                    Channel(
                        id = "tc_premium",
                        number = "42",
                        title = "Telecine Premium HD",
                        category = "Rede Telecine",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769195.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/82/Telecine_Premium_logo_2019.svg/320px-Telecine_Premium_logo_2019.svg.png",
                        description = "Grandes sucessos de bilheteria e estreias exclusivas do cinema mundial em primeira mao.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769195.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769195.m3u8")
                    ),
                    Channel(
                        id = "tc_action",
                        number = "43",
                        title = "Telecine Action HD",
                        category = "Rede Telecine",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769171.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/ca/Telecine_Action_logo_2019.svg/320px-Telecine_Action_logo_2019.svg.png",
                        description = "Muita adrenalina, velocidade, acao e perseguicoes sem limite.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769171.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769171.m3u8")
                    ),
                    Channel(
                        id = "tc_touch",
                        number = "44",
                        title = "Telecine Touch HD",
                        category = "Rede Telecine",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769201.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/eb/Telecine_Touch_logo_2019.svg/320px-Telecine_Touch_logo_2019.svg.png",
                        description = "Emocao e historias marcantes: os maiores dramas e romances de todos os tempos.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769201.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769201.m3u8")
                    ),
                    Channel(
                        id = "tc_pipoca",
                        number = "45",
                        title = "Telecine Pipoca HD",
                        category = "Rede Telecine",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769189.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a2/Telecine_Pipoca_logo_2019.svg/320px-Telecine_Pipoca_logo_2019.svg.png",
                        description = "Os filmes mais divertidos do cinema nacional e internacional 100% dublados.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769189.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769189.m3u8")
                    ),
                    Channel(
                        id = "tc_fun",
                        number = "46",
                        title = "Telecine Fun HD",
                        category = "Rede Telecine",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769183.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/8d/Telecine_Fun_logo_2019.svg/320px-Telecine_Fun_logo_2019.svg.png",
                        description = "Comedias e animacoes para garantir boas risadas com a familia reunida.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769183.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769183.m3u8")
                    ),
                    Channel(
                        id = "tc_cult",
                        number = "47",
                        title = "Telecine Cult HD",
                        category = "Rede Telecine",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769177.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b2/Telecine_Cult_logo_2019.svg/320px-Telecine_Cult_logo_2019.svg.png",
                        description = "Obras-primas da historia da setima arte, cinema de autor e producoes premiadas.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769177.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769177.m3u8")
                    )
                )
            ),
            ChannelCategory(
                id = "cat_hbo",
                name = "Rede HBO",
                isLocked = false,
                channels = listOf(
                    Channel(
                        id = "hbo_main",
                        number = "48",
                        title = "HBO HD",
                        category = "Rede HBO",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769347.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/de/HBO_logo.svg/320px-HBO_logo.svg.png",
                        description = "Series consagradas, grandes documentarios e as maiores producoes mundiais.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769347.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769347.m3u8")
                    ),
                    Channel(
                        id = "hbo_2",
                        number = "49",
                        title = "HBO 2 HD",
                        category = "Rede HBO",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769336.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/4/4b/HBO2_logo_2014.svg/320px-HBO2_logo_2014.svg.png",
                        description = "Reprises dos melhores filmes e series em horarios alternativos dublados e legendados.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769336.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769336.m3u8")
                    ),
                    Channel(
                        id = "hbo_plus",
                        number = "50",
                        title = "HBO Plus HD",
                        category = "Rede HBO",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769357.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/be/HBO_Plus_logo_2019.svg/320px-HBO_Plus_logo_2019.svg.png",
                        description = "Conteudo jovem, comedia, acao e musica da franquia HBO.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769357.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769357.m3u8")
                    ),
                    Channel(
                        id = "hbo_family",
                        number = "51",
                        title = "HBO Family HD",
                        category = "Rede HBO",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769341.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/7/70/HBO_Family_2014.svg/320px-HBO_Family_2014.svg.png",
                        description = "Programacao voltada para criancas e jovens com animacoes e filmes familiares.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769341.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769341.m3u8")
                    ),
                    Channel(
                        id = "hbo_sig",
                        number = "52",
                        title = "HBO Signature HD",
                        category = "Rede HBO",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769371.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/9/91/HBO_Signature_logo_2014.svg/320px-HBO_Signature_logo_2014.svg.png",
                        description = "O acervo mais exclusivo e refinado das producoes originais e documentarios HBO.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769371.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769371.m3u8")
                    ),
                    Channel(
                        id = "hbo_mundi",
                        number = "53",
                        title = "HBO Mundi HD",
                        category = "Rede HBO",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769351.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/de/HBO_logo.svg/320px-HBO_logo.svg.png",
                        description = "Filmes internacionais e producoes independentes aclamadas pela critica.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769351.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769351.m3u8")
                    ),
                    Channel(
                        id = "hbo_pop",
                        number = "54",
                        title = "HBO Pop HD",
                        category = "Rede HBO",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769363.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/de/HBO_logo.svg/320px-HBO_logo.svg.png",
                        description = "Filmes leves, comedias romanticas e grandes sucessos da cultura pop.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769363.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769363.m3u8")
                    ),
                    Channel(
                        id = "hbo_xtreme",
                        number = "55",
                        title = "HBO Xtreme HD",
                        category = "Rede HBO",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769377.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/de/HBO_logo.svg/320px-HBO_logo.svg.png",
                        description = "Acao frenetica, suspense e terror para quem gosta de fortes emocoes.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769377.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769377.m3u8")
                    )
                )
            ),
            ChannelCategory(
                id = "cat_discovery",
                name = "Rede Discovery",
                isLocked = false,
                channels = listOf(
                    Channel(
                        id = "ch_discovery",
                        number = "56",
                        title = "Discovery Channel HD",
                        category = "Rede Discovery",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/discovery-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/discovery-channel-hd.png",
                        description = "O canal pioneiro em exploracao cientifica, misterios do planeta e aventuras humanas.",
                        quality = "HD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769469.m3u8", "http://64.31.23.207:80/live/59471019/60849037/5769469.ts")
                    ),
                    Channel(
                        id = "ch_discovery_kids",
                        number = "57",
                        title = "Discovery Kids HD",
                        category = "Rede Discovery",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5770298.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/07/Discovery_Kids_logo_%282016%29.svg/320px-Discovery_Kids_logo_%282016%29.svg.png",
                        description = "O canal lider para o publico infantil: Peppa Pig, O Show da Luna e Doki.",
                        quality = "HD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5770298.ts", "http://gelosalgados.site:80/live/59471019/60849037/5770298.m3u8")
                    ),
                    Channel(
                        id = "ch_discovery_hh",
                        number = "58",
                        title = "Discovery Home & Health HD",
                        category = "Rede Discovery",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/discovery-hh/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/discovery-hh-hd.png",
                        description = "Estilo de vida, reformas de imoveis, saude, culinaria e maternidade.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769470.m3u8", "http://64.31.23.207:80/live/59471019/60849037/5769470.ts")
                    ),
                    Channel(
                        id = "ch_discovery_turbo",
                        number = "59",
                        title = "Discovery Turbo HD",
                        category = "Rede Discovery",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/discovery-turbo-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/discovery-turbo-hd.png",
                        description = "Carros personalizados, restauracao de classicos e motores de alta performance.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769471.m3u8", "http://64.31.23.207:80/live/59471019/60849037/5769471.ts")
                    ),
                    Channel(
                        id = "disc_id",
                        number = "60",
                        title = "Discovery ID (Investigacao) HD",
                        category = "Rede Discovery",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5770295.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/cd/Investigation_Discovery_logo_%282020%29.svg/320px-Investigation_Discovery_logo_%282020%29.svg.png",
                        description = "Crimes reais, investigacoes policiais forenses e os misterios mais intrigantes.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5770295.ts", "http://gelosalgados.site:80/live/59471019/60849037/5770295.m3u8")
                    ),
                    Channel(
                        id = "disc_world",
                        number = "61",
                        title = "Discovery World HD",
                        category = "Rede Discovery",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5770310.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a2/Discovery_World_logo.svg/320px-Discovery_World_logo.svg.png",
                        description = "Documentarios deslumbrantes sobre culturas globais, paisagens e a historia da Terra.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5770310.ts", "http://gelosalgados.site:80/live/59471019/60849037/5770310.m3u8")
                    ),
                    Channel(
                        id = "disc_science",
                        number = "62",
                        title = "Discovery Science HD",
                        category = "Rede Discovery",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5770301.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/9/91/Discovery_Science_logo.svg/320px-Discovery_Science_logo.svg.png",
                        description = "Tecnologia de ponta, astrofisica, experimentos ousados e inovacoes do futuro.",
                        quality = "HD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5770301.ts", "http://gelosalgados.site:80/live/59471019/60849037/5770301.m3u8")
                    ),
                    Channel(
                        id = "disc_theater",
                        number = "63",
                        title = "Discovery Theater HD",
                        category = "Rede Discovery",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5770304.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/23/Discovery_Theater_logo.png/320px-Discovery_Theater_logo.png",
                        description = "Imagens cinematograficas em alta definicao da natureza e animais selvagens.",
                        quality = "HD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5770304.ts", "http://gelosalgados.site:80/live/59471019/60849037/5770304.m3u8")
                    ),
                    Channel(
                        id = "disc_animal",
                        number = "64",
                        title = "Animal Planet HD",
                        category = "Rede Discovery",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769462.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/20/Animal_Planet_logo_2018.svg/320px-Animal_Planet_logo_2018.svg.png",
                        description = "O reino animal de perto: resgate de fauna, predadores selvagens e vida dos pets.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769462.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769462.m3u8")
                    ),
                    Channel(
                        id = "disc_tlc",
                        number = "65",
                        title = "TLC HD",
                        category = "Rede Discovery",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/tlc-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/tlc-hd.png",
                        description = "Historias reais e inspiradoras: Muquiranas, Quilos Mortais, 90 Dias para Casar e Cake Boss.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769562.m3u8", "http://64.31.23.207:80/live/59471019/60849037/5769562.ts")
                    ),
                    Channel(
                        id = "disc_hgtv",
                        number = "66",
                        title = "HGTV HD",
                        category = "Rede Discovery",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5770510.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/hgtv.png",
                        description = "O canal mundial de reformas de casas, decoracao de interiores e compra e venda de imoveis.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5770510.ts", "http://gelosalgados.site:80/live/59471019/60849037/5770510.m3u8")
                    )
                )
            ),
            ChannelCategory(
                id = "cat_filmes",
                name = "Filmes & Séries",
                isLocked = false,
                channels = listOf(
                    Channel(
                        id = "ch_sony_channel",
                        number = "56",
                        title = "Canal Sony HD (Sony Channel)",
                        category = "Filmes & Séries",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5770480.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/60/Sony_Channel_logo_2019.svg/320px-Sony_Channel_logo_2019.svg.png",
                        description = "O lendario Canal Sony: Grey's Anatomy, The Good Doctor e as series medicas e policiais mais amadas.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5770480.ts", "http://gelosalgados.site:80/live/59471019/60849037/5770480.m3u8")
                    ),
                    Channel(
                        id = "ch_sony_movies",
                        number = "57",
                        title = "Sony Movies HD",
                        category = "Filmes & Séries",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769605.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/7/77/Sony_Movies_logo_2022.svg/320px-Sony_Movies_logo_2022.svg.png",
                        description = "O acervo completo dos lendarios estudios da Columbia Pictures e Sony Pictures.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769605.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769605.m3u8")
                    ),
                    Channel(
                        id = "warner_ch",
                        number = "58",
                        title = "Warner Channel HD",
                        category = "Filmes & Séries",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769598.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/7/72/Warner_Channel_2019_logo.svg/320px-Warner_Channel_2019_logo.svg.png",
                        description = "Universo DC, The Big Bang Theory, Friends e os maiores sucessos da Warner Bros.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769598.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769598.m3u8")
                    ),
                    Channel(
                        id = "megapix_ch",
                        number = "59",
                        title = "Megapix HD",
                        category = "Filmes & Séries",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769538.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f3/Megapix_logo_2020.svg/320px-Megapix_logo_2020.svg.png",
                        description = "O canal dos grandes campeoes de bilheteria dublados com sessao pipoca todo dia.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769538.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769538.m3u8")
                    ),
                    Channel(
                        id = "ch_universal_tv",
                        number = "60",
                        title = "Universal TV HD",
                        category = "Filmes & Séries",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5770549.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e0/Universal_TV_logo_2018.svg/320px-Universal_TV_logo_2018.svg.png",
                        description = "Franquia Chicago (Fire, P.D., Med), Law & Order e grandes investigacoes.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5770549.ts", "http://gelosalgados.site:80/live/59471019/60849037/5770549.m3u8")
                    ),
                    Channel(
                        id = "ch_studio_universal",
                        number = "61",
                        title = "Studio Universal HD",
                        category = "Filmes & Séries",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769480.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/1/1a/Studio_Universal_logo.svg/320px-Studio_Universal_logo.svg.png",
                        description = "Cinema hollywoodiano e classicos dos estúdios Universal Pictures.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769480.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769480.m3u8")
                    ),
                    Channel(
                        id = "ch_paramount",
                        number = "62",
                        title = "Paramount Network HD",
                        category = "Filmes & Séries",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5770425.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f2/Paramount_Network_2018.svg/320px-Paramount_Network_2018.svg.png",
                        description = "Yellowstone, Tulsa King e superproducoes consagradas da Paramount Pictures.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5770425.ts", "http://gelosalgados.site:80/live/59471019/60849037/5770425.m3u8")
                    ),
                    Channel(
                        id = "ch_cinemax",
                        number = "63",
                        title = "Cinemax HD",
                        category = "Filmes & Séries",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769532.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/1/14/Cinemax_logo_2011.svg/320px-Cinemax_logo_2011.svg.png",
                        description = "Filmes de sucesso, acao e suspense com a chancela do grupo HBO.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769532.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769532.m3u8")
                    ),
                    Channel(
                        id = "ch_tnt",
                        number = "64",
                        title = "TNT HD",
                        category = "Filmes & Séries",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/tnt-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b5/TNT_Logo_2016.svg/320px-TNT_Logo_2016.svg.png",
                        description = "Grandes premiacoes como o Oscar, superproducoes cinematograficas e esportes.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769568.m3u8", "http://64.31.23.207:80/live/59471019/60849037/5769568.ts")
                    ),
                    Channel(
                        id = "ch_space",
                        number = "65",
                        title = "Space HD",
                        category = "Filmes & Séries",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/space-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/36/Space_logo_2017.svg/320px-Space_logo_2017.svg.png",
                        description = "Especializado em acao, ficcao cientifica, suspense e filmes de luta.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769550.m3u8", "http://64.31.23.207:80/live/59471019/60849037/5769550.ts")
                    ),
                    Channel(
                        id = "ch_axn",
                        number = "66",
                        title = "AXN HD",
                        category = "Filmes & Séries",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769463.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b5/AXN_logo_2015.svg/320px-AXN_logo_2015.svg.png",
                        description = "As maiores series policiais e de investigacao forense do mundo: NCIS e Criminal Minds.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769463.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769463.m3u8")
                    ),
                    Channel(
                        id = "ch_284",
                        number = "67",
                        title = "TNT Novelas HD",
                        category = "Filmes & Séries",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/tnt-novelas-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/tnt-novelas.png",
                        description = "O canal dedicado aos grandes sucessos mundiais das telenovelas e melodramas.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769572.m3u8", "http://64.31.23.207:80/live/59471019/60849037/5769572.ts")
                    ),
                    Channel(
                        id = "ch_392",
                        number = "68",
                        title = "TV Classicos do Cinema",
                        category = "Filmes & Séries",
                        streamUrl = "https://live20.bozztv.com/giatvplayout7/giatv-208272/tracks-v1a1/mono.ts.m3u8?ROGERIOTORRES",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/tv-classicos-do-cinema.png",
                        description = "Classicos que marcaram geracoes na historia do cinema mundial.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_391",
                        number = "69",
                        title = "TV Sonata",
                        category = "Filmes & Séries",
                        streamUrl = "https://video04.logicahost.com.br/isonata/isonata/chunklist.m3u8?ROGERIOTORRES",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/tv-sonata.png",
                        description = "Cinema independente, producoes artisticas e entretenimento refinado.",
                        quality = "HD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769457.m3u8", "http://64.31.23.207:80/live/59471019/60849037/5769457.ts")
                    ),
                    Channel(
                        id = "ch_388",
                        number = "70",
                        title = "Top Mix Filmes",
                        category = "Filmes & Séries",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5378594.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/top-mix-filmes.png",
                        description = "Uma selecao diversificada com filmes de varios generos 24 horas por dia.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_385",
                        number = "71",
                        title = "Hallo! Series",
                        category = "Filmes & Séries",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769616.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/hallo-series.png",
                        description = "Maratonas das melhores series de comedia e drama da televisao.",
                        quality = "HD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769616.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769616.m3u8")
                    ),
                    Channel(
                        id = "ch_331",
                        number = "72",
                        title = "RunTime - Cinema",
                        category = "Filmes & Séries",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769611.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/runtime-cinema.png",
                        description = "Filmes aclamados com exibicao continua e alta definicao.",
                        quality = "HD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769611.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769611.m3u8")
                    ),
                    Channel(
                        id = "ch_332",
                        number = "73",
                        title = "RunTime - Terror",
                        category = "Filmes & Séries",
                        streamUrl = "https://stream.ads.ottera.tv/playlist.m3u8?network_id=4865",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/runtime-terror.png",
                        description = "O melhor do horror psicologico, sobrenatural e suspense horripilante.",
                        quality = "HD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769614.m3u8", "http://64.31.23.207:80/live/59471019/60849037/5769614.ts")
                    ),
                    Channel(
                        id = "ch_273",
                        number = "74",
                        title = "RunTime - Acao",
                        category = "Filmes & Séries",
                        streamUrl = "https://stream.ads.ottera.tv/playlist.m3u8?network_id=2552",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/runtime-acao.png",
                        description = "Tiros, explosoes e perseguicoes para os apaixonados por adrenalina.",
                        quality = "HD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769614.m3u8", "http://64.31.23.207:80/live/59471019/60849037/5769614.ts")
                    )
                )
            ),
            ChannelCategory(
                id = "cat_docs",
                name = "Documentários",
                isLocked = false,
                channels = listOf(
                    Channel(
                        id = "ch_history",
                        number = "75",
                        title = "History Channel HD",
                        category = "Documentários",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769544.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f5/History_Logo.svg/320px-History_Logo.svg.png",
                        description = "Trato Feito, Alienigenas do Passado, Forjado no Fogo e grandes acontecimentos historicos.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769544.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769544.m3u8")
                    ),
                    Channel(
                        id = "ch_history_2",
                        number = "76",
                        title = "History 2 (H2) HD",
                        category = "Documentários",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5770333.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6b/History2_logo.svg/320px-History2_logo.svg.png",
                        description = "Aprofundamento historico, grandes guerras, arqueologia e conspiracoes mundiais.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5770333.ts", "http://gelosalgados.site:80/live/59471019/60849037/5770333.m3u8")
                    ),
                    Channel(
                        id = "doc_natgeo",
                        number = "77",
                        title = "National Geographic (Nat Geo) HD",
                        category = "Documentários",
                        streamUrl = "https://cdn-ue1-prod.tsv2.amagi.tv/linear/outsidetv-outsidetvplus-oando/playlist.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6a/National_Geographic_Logo.svg/320px-National_Geographic_Logo.svg.png",
                        description = "Ciencia, exploracao geografica e preservacao do meio ambiente.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "doc_natgeowild",
                        number = "78",
                        title = "Nat Geo Wild HD",
                        category = "Documentários",
                        streamUrl = "https://rbmn-live.akamaized.net/hls/live/590964/BoRB-AT/master_6660.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/36/Nat_Geo_Wild_logo.svg/320px-Nat_Geo_Wild_logo.svg.png",
                        description = "O lado mais selvagem e intocado da natureza nos ecossistemas mais extremos.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "doc_curta",
                        number = "79",
                        title = "Curta! HD",
                        category = "Documentários",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5770482.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/curta.png",
                        description = "Cinema brasileiro, artes plasticas, musica, filosofia e literatura.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5770482.ts", "http://gelosalgados.site:80/live/59471019/60849037/5770482.m3u8")
                    ),
                    Channel(
                        id = "doc_arte1",
                        number = "80",
                        title = "Arte 1 HD",
                        category = "Documentários",
                        streamUrl = "http://45.162.64.114/ARTE1/index.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/arte-1.png",
                        description = "Danca, musica classica, teatro, fotografia e arquitetura.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5770473.m3u8", "http://64.31.23.207:80/live/59471019/60849037/5770473.ts")
                    )
                )
            ),
            ChannelCategory(
                id = "cat_variedades",
                name = "Variedades & Estilo",
                isLocked = false,
                channels = listOf(
                    Channel(
                        id = "var_multishow",
                        number = "88",
                        title = "Multishow HD",
                        category = "Variedades & Estilo",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5770521.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/1/15/Multishow_logo_2017.svg/320px-Multishow_logo_2017.svg.png",
                        description = "Shows ao vivo, humor com Vai Que Cola, festivais Rock in Rio e Lollapalooza.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5770521.ts", "http://gelosalgados.site:80/live/59471019/60849037/5770521.m3u8")
                    ),
                    Channel(
                        id = "var_gnt",
                        number = "89",
                        title = "GNT HD",
                        category = "Variedades & Estilo",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769475.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/ab/GNT_logo_2020.svg/320px-GNT_logo_2020.svg.png",
                        description = "Gastronomia, moda, decoracao, Papo de Segunda e Saia Justa.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769475.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769475.m3u8")
                    ),
                    Channel(
                        id = "var_viva",
                        number = "90",
                        title = "Viva HD",
                        category = "Variedades & Estilo",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769450.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a2/Canal_Viva_logo_2019.svg/320px-Canal_Viva_logo_2019.svg.png",
                        description = "As maiores novelas da teledramaturgia brasileira e programas que marcaram epoca no canal Viva.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769450.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769450.m3u8")
                    ),
                    Channel(
                        id = "var_canalbrasil",
                        number = "91",
                        title = "Canal Brasil HD",
                        category = "Variedades & Estilo",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769526.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b5/Canal_Brasil_logo_2019.svg/320px-Canal_Brasil_logo_2019.svg.png",
                        description = "O melhor da cultura nacional, cinema brasileiro, MPB e festivais artisticos.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769526.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769526.m3u8")
                    ),
                    Channel(
                        id = "var_bis",
                        number = "92",
                        title = "BIS HD",
                        category = "Variedades & Estilo",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5770476.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/36/Canal_Bis_logo.svg/320px-Canal_Bis_logo.svg.png",
                        description = "Grandes shows internacionais, documentarios musicais e turnes historicas.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5770476.ts", "http://gelosalgados.site:80/live/59471019/60849037/5770476.m3u8")
                    ),
                    Channel(
                        id = "var_viagem",
                        number = "93",
                        title = "Modo Viagem HD",
                        category = "Variedades & Estilo",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5770518.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f6/Modo_Viagem_logo.png/320px-Modo_Viagem_logo.png",
                        description = "Turismo pelo mundo, hoteis deslumbrantes e experiencias de viagem inesqueciveis.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5770518.ts", "http://gelosalgados.site:80/live/59471019/60849037/5770518.m3u8")
                    ),
                    Channel(
                        id = "var_e",
                        number = "94",
                        title = "E! Entertainment HD",
                        category = "Variedades & Estilo",
                        streamUrl = "https://isaocorp.cloudecast.com/playtv/index.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/af/E%21_logo_2012.svg/320px-E%21_logo_2012.svg.png",
                        description = "O tapete vermelho das celebridades de Hollywood, reality shows e fofocas VIP.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "var_terraviva",
                        number = "95",
                        title = "Terra Viva HD",
                        category = "Variedades & Estilo",
                        streamUrl = "https://9ada494d.wurl.com/master/f36d25e7e52f1ba8d7e56eb859c636563214f541/TEctYnJfVXBseW5rLU5ld2NvX0hMUw/playlist.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/9/90/Terra_Viva_logo.png/320px-Terra_Viva_logo.png",
                        description = "Vida no campo, sustentabilidade e transmissao de eventos rurais.",
                        quality = "HD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769450.m3u8", "http://64.31.23.207:80/live/59471019/60849037/5769450.ts")
                    ),
                    Channel(
                        id = "ch_358",
                        number = "96",
                        title = "Rede Minas",
                        category = "Variedades & Estilo",
                        streamUrl = "https://8hzcavccys.zoeweb.tv/redeminas/ngrp:redeminas_all/chunklist_w1997760151_b2771072.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/rede-minas.png",
                        description = "Cultura mineira, jornalismo regional e producoes independentes.",
                        quality = "HD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5770447.m3u8", "http://64.31.23.207:80/live/59471019/60849037/5770447.ts")
                    ),
                    Channel(
                        id = "ch_345",
                        number = "97",
                        title = "Amazon Sat HD",
                        category = "Variedades & Estilo",
                        streamUrl = "https://amazonsat.brasilstream.com.br/hls/amazonsat/index.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/amazon-sat.png",
                        description = "A emissora da Amazonia: biodiversidade, tradicoes e natureza exuberante.",
                        quality = "HD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5770472.m3u8", "http://64.31.23.207:80/live/59471019/60849037/5770472.ts")
                    ),
                    Channel(
                        id = "ch_386",
                        number = "98",
                        title = "Novelissima",
                        category = "Variedades & Estilo",
                        streamUrl = "https://cis-no-samsung.otteravision.com/cis/no/no_h265.m3u8?ROGERIOTORRES",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/novelissima.png",
                        description = "Grandes novelas latinas dubladas em transmissao continua 24 horas por dia.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_322",
                        number = "99",
                        title = "Rede TV",
                        category = "Variedades & Estilo",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769447.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/rede-tv.png",
                        description = "Entretenimento, talk shows, fofocas e debates ao vivo.",
                        quality = "HD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769447.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769447.m3u8")
                    ),
                    Channel(
                        id = "ch_224",
                        number = "100",
                        title = "PlayTV",
                        category = "Variedades & Estilo",
                        streamUrl = "https://isaocorp.cloudecast.com/playtv/tracks-v1a1/mono.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/playtv.png",
                        description = "Games, e-sports, cultura geek, animes e musica pop.",
                        quality = "HD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5298427.m3u8", "http://64.31.23.207:80/live/59471019/60849037/5298427.ts")
                    )
                )
            ),
            ChannelCategory(
                id = "cat_4k",
                name = "💎 Canais 4K Ultra HD",
                iconRes = R.drawable.ic_4k,
                isLocked = false,
                channels = listOf(
                    Channel(
                        id = "ch_4k_sportv",
                        number = "101",
                        title = "SporTV 4K UHD",
                        category = "Canais 4K Ultra HD",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769428.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/5/52/SporTV_logo_2021.svg/320px-SporTV_logo_2021.svg.png",
                        description = "Transmissao oficial em resolucao 4K Ultra HD (3840x2160) com maximo detalhe esportivo.",
                        quality = "4K",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769428.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769428.m3u8")
                    ),
                    Channel(
                        id = "ch_4k_prem",
                        number = "102",
                        title = "Premiere 4K UHD",
                        category = "Canais 4K Ultra HD",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769297.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d7/Premiere_logo_2021.svg/320px-Premiere_logo_2021.svg.png",
                        description = "O melhor do futebol brasileiro com qualidade de cinema 4K UHD e som imersivo.",
                        quality = "4K",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769297.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769297.m3u8")
                    ),
                    Channel(
                        id = "ch_4k_tc",
                        number = "103",
                        title = "Telecine Premium 4K UHD",
                        category = "Canais 4K Ultra HD",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769195.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/82/Telecine_Premium_logo_2019.svg/320px-Telecine_Premium_logo_2019.svg.png",
                        description = "Grandes lancamentos do cinema mundial na resolucao maxima 4K Ultra HD.",
                        quality = "4K",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769195.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769195.m3u8")
                    ),
                    Channel(
                        id = "ch_4k_hbo",
                        number = "104",
                        title = "HBO 4K UHD",
                        category = "Canais 4K Ultra HD",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769347.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/de/HBO_logo.svg/320px-HBO_logo.svg.png",
                        description = "Series premiadas e filmes HBO gravados em resolucao nativa 4K UHD.",
                        quality = "4K",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769347.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769347.m3u8")
                    ),
                    Channel(
                        id = "ch_4k_disc",
                        number = "105",
                        title = "Discovery Channel HD",
                        category = "Canais 4K Ultra HD",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/discovery-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/discovery-channel-hd.png",
                        description = "O canal pioneiro em exploracao cientifica, misterios do planeta e aventuras humanas.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769469.m3u8", "http://64.31.23.207:80/live/59471019/60849037/5769469.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769469.m3u8")
                    ),
                    Channel(
                        id = "ch_4k_nasa",
                        number = "106",
                        title = "Cosmos & NASA Space HD",
                        category = "Canais 4K Ultra HD",
                        streamUrl = "https://video.hostingcaaguazu.com:19360/cosmostv/cosmostv.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e5/NASA_logo.svg/320px-NASA_logo.svg.png",
                        description = "Transmissao oficial ao vivo do espaco sideral, astronomia, lancamentos espaciais e exploracao do cosmos.",
                        quality = "HD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769550.m3u8", "http://64.31.23.207:80/live/59471019/60849037/5769550.ts")
                    ),
                    Channel(
                        id = "ch_4k_nature",
                        number = "107",
                        title = "Love Nature 4K UHD",
                        category = "Canais 4K Ultra HD",
                        streamUrl = "https://rbmn-live.akamaized.net/hls/live/590964/BoRB-AT/master_6660.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e2/Love_Nature_logo.png/320px-Love_Nature_logo.png",
                        description = "Paisagens paradisiacas do planeta Terra filmadas em ultra alta definicao 4K.",
                        quality = "4K",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5770332.m3u8", "http://64.31.23.207:80/live/59471019/60849037/5770332.ts")
                    )
                )
            ),
            ChannelCategory(
                id = "cat_h265",
                name = "Canais H.265 (HEVC)",
                isLocked = false,
                channels = listOf(
                    Channel(
                        id = "ch_h265_globo",
                        number = "108",
                        title = "Globo SP H.265 (HEVC)",
                        category = "Canais H.265 (HEVC)",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/globo-sp-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/globo-sp-globo-sao-paulo.png",
                        description = "Transmissao em codec H.265 de alta eficiencia: imagem ultra nitida consumindo metade da banda.",
                        quality = "H265",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769268.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769268.m3u8")
                    ),
                    Channel(
                        id = "ch_h265_sportv",
                        number = "109",
                        title = "SporTV H.265 (HEVC)",
                        category = "Canais H.265 (HEVC)",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769428.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/5/52/SporTV_logo_2021.svg/320px-SporTV_logo_2021.svg.png",
                        description = "O canal lider de esportes otimizado em compressao H.265 sem travamentos.",
                        quality = "H265",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769428.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769428.m3u8")
                    ),
                    Channel(
                        id = "ch_h265_tc",
                        number = "110",
                        title = "Telecine H.265 (HEVC)",
                        category = "Canais H.265 (HEVC)",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769183.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/82/Telecine_Premium_logo_2019.svg/320px-Telecine_Premium_logo_2019.svg.png",
                        description = "Filmes em alta taxa de bits codificados em H.265 para TV Boxes modernas.",
                        quality = "H265",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769183.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769183.m3u8")
                    ),
                    Channel(
                        id = "ch_h265_hbo",
                        number = "111",
                        title = "HBO H.265 (HEVC)",
                        category = "Canais H.265 (HEVC)",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769347.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/de/HBO_logo.svg/320px-HBO_logo.svg.png",
                        description = "Series e sucessos de Hollywood em transmissao eficiente H.265.",
                        quality = "H265",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769347.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769347.m3u8")
                    ),
                    Channel(
                        id = "ch_h265_espn",
                        number = "112",
                        title = "ESPN H.265 (HEVC)",
                        category = "Canais H.265 (HEVC)",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/espn-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/espn-hd.png",
                        description = "Esportes internacionais com codificacao avancada H.265 a 60fps.",
                        quality = "H265",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769472.m3u8", "http://64.31.23.207:80/live/59471019/60849037/5769472.ts")
                    ),
                    Channel(
                        id = "ch_h265_prem",
                        number = "113",
                        title = "Premiere H.265 (HEVC)",
                        category = "Canais H.265 (HEVC)",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769297.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d7/Premiere_logo_2021.svg/320px-Premiere_logo_2021.svg.png",
                        description = "Futebol brasileiro com transmissao rapida e estavel pelo codec H.265.",
                        quality = "H265",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769297.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769297.m3u8")
                    ),
                    Channel(
                        id = "ch_h265_nov",
                        number = "114",
                        title = "Novelissima H.265 (HEVC)",
                        category = "Canais H.265 (HEVC)",
                        streamUrl = "https://cis-no-samsung.otteravision.com/cis/no/no_h265.m3u8?ROGERIOTORRES",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/novelissima.png",
                        description = "Novelas latinas codificadas em HEVC nativo para a melhor qualidade de imagem.",
                        quality = "H265"
                    ),
                    Channel(
                        id = "ch_h265_like",
                        number = "115",
                        title = "L!ke TV H.265 (HEVC)",
                        category = "Canais H.265 (HEVC)",
                        streamUrl = "https://cnl-cl-samsung.otteravision.com/cnl/cl_h265/cl_h265.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/like-tv.png",
                        description = "Canal de dicas de entretenimento e cultura pop em transmissao H.265.",
                        quality = "H265",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5770381.m3u8", "http://64.31.23.207:80/live/59471019/60849037/5770381.ts")
                    )
                )
            ),
            ChannelCategory(
                id = "cat_globo",
                name = "Globo",
                isLocked = false,
                channels = listOf(
                    Channel(
                        id = "globo_sp_fhd",
                        number = "115",
                        title = "Globo SP FHD (Principal)",
                        category = "Globo",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/globo-sp-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/globo-sp-globo-sao-paulo.png",
                        description = "TV Globo Sao Paulo FHD Oficial: Sinal direto de alta definicao com programacao completa e EPG ativo.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769268.m3u8", "http://64.31.23.207:80/live/59471019/60849037/5769268.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769268.m3u8")
                    ),
                    Channel(
                        id = "globo_sp_backup",
                        number = "116",
                        title = "Globo SP (Backup Alternativo)",
                        category = "Globo",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769268.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/globo-sp-globo-sao-paulo.png",
                        description = "Sinal de contingencia da TV Globo Sao Paulo para garantir transmissao sem interrupcoes.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769268.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769268.m3u8")
                    ),
                    Channel(
                        id = "globo_sp_4k",
                        number = "117",
                        title = "Globo SP 4K UHD",
                        category = "Globo",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769268.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/globo-sp-globo-sao-paulo.png",
                        description = "TV Globo Sao Paulo em resolucao maxima 4K Ultra HD com codec HEVC de alta definicao.",
                        quality = "4K",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769268.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769268.m3u8")
                    ),
                    Channel(
                        id = "globo_nac_fhd",
                        number = "118",
                        title = "Globo Nacional FHD",
                        category = "Globo",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769251.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/globo-sp-globo-sao-paulo.png",
                        description = "Sinal nacional da Rede Globo de Televisao para todo o Brasil em Full HD.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769251.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769251.m3u8")
                    ),
                    Channel(
                        id = "ch_158",
                        number = "119",
                        title = "Globo SP (Rede CDN)",
                        category = "Globo",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/globo-sp-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/globo-sp-globo-sao-paulo.png",
                        description = "TV Globo Sao Paulo: Jornalismo, novelas, futebol e programacao completa em alta definicao.",
                        quality = "HD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769268.m3u8", "http://64.31.23.207:80/live/59471019/60849037/5769268.ts")
                    ),
                    Channel(
                        id = "ch_160",
                        number = "117",
                        title = "Globo RJ - TV Globo Rio",
                        category = "Globo",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/globo-rj-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/globo-rj-tv-globo-rio.png",
                        description = "TV Globo Rio de Janeiro: Cobertura jornalistica carioca, novelas e transmissao ao vivo.",
                        quality = "HD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769260.m3u8", "http://64.31.23.207:80/live/59471019/60849037/5769260.ts")
                    ),
                    Channel(
                        id = "ch_163",
                        number = "118",
                        title = "Globo MG - TV Globo Minas",
                        category = "Globo",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/globo-minas-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/globo-mg-tv-globo-minas.png",
                        description = "Sinal oficial da TV Globo Minas com informacao e o melhor do entretenimento.",
                        quality = "HD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769254.m3u8", "http://64.31.23.207:80/live/59471019/60849037/5769254.ts")
                    ),
                    Channel(
                        id = "ch_166",
                        number = "119",
                        title = "Globo BA - TV Bahia",
                        category = "Globo",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/globo-bahia-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/globo-ba-tv-bahia.jpg",
                        description = "A TV Bahia e a emissora afiliada a Rede Globo com a maior cobertura do estado baiano.",
                        quality = "HD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769236.m3u8", "http://64.31.23.207:80/live/59471019/60849037/5769236.ts")
                    ),
                    Channel(
                        id = "ch_165",
                        number = "120",
                        title = "Globo DF - TV Globo Brasilia",
                        category = "Globo",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/globo-brasilia-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/globo-df-tv-globo-brasilia.png",
                        description = "Cobertura politica da capital federal, jornalismo nacional e entretenimento.",
                        quality = "HD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769228.m3u8", "http://64.31.23.207:80/live/59471019/60849037/5769228.ts")
                    ),
                    Channel(
                        id = "ch_324",
                        number = "121",
                        title = "Globo RBS - Globo Porto Alegre",
                        category = "Globo",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/globo-rbs-porto-alegre/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/globo-rbs-globo-porto-alegre.png",
                        description = "RBS TV Porto Alegre levando o sinal da Globo e tradicoes gauchas para o Rio Grande do Sul.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_167",
                        number = "122",
                        title = "Globo PR - RPC TV Curitiba",
                        category = "Globo",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/globo-rpc-curitiba-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/globo-pr-rpc-tv-curitiba.png",
                        description = "RPC TV Curitiba: jornalismo lider de audiencia no estado do Parana.",
                        quality = "HD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769280.m3u8", "http://64.31.23.207:80/live/59471019/60849037/5769280.ts")
                    )
                )
            ),
            ChannelCategory(
                id = "cat_sbt",
                name = "SBT",
                isLocked = false,
                channels = listOf(
                    Channel(
                        id = "ch_168",
                        number = "123",
                        title = "SBT SP - SBT Sao Paulo",
                        category = "SBT",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/sbt-sp-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/sbt-sp-sbt-sao-paulo.png",
                        description = "SBT Sao Paulo: A TV que tem torcida com Silvio Santos, jornalismo popular e novelas infantis.",
                        quality = "HD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769407.m3u8", "http://64.31.23.207:80/live/59471019/60849037/5769407.ts")
                    ),
                    Channel(
                        id = "ch_169",
                        number = "124",
                        title = "SBT RJ - SBT Rio",
                        category = "SBT",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/sbt-rj-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/sbt-rj-sbt-rio.png",
                        description = "SBT Rio: Isabele Benito trazendo as principais noticias e prestacao de servico no RJ.",
                        quality = "HD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769402.m3u8", "http://64.31.23.207:80/live/59471019/60849037/5769402.ts")
                    )
                )
            ),
            ChannelCategory(
                id = "cat_record",
                name = "Record",
                isLocked = false,
                channels = listOf(
                    Channel(
                        id = "ch_178",
                        number = "125",
                        title = "RedeRecord SP - RecordTV SP",
                        category = "Record",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/record-sp-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/rederecord-sp-recordtv-sp.png",
                        description = "RecordTV Sao Paulo: Cidade Alerta, Jornal da Record, novelas biblicas e reality shows.",
                        quality = "HD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769477.m3u8", "http://64.31.23.207:80/live/59471019/60849037/5769477.ts")
                    ),
                    Channel(
                        id = "ch_177",
                        number = "126",
                        title = "RedeRecord RJ - RecordTV Rio",
                        category = "Record",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/record-rj-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/rederecord-rj-recordtv-rio.png",
                        description = "RecordTV Rio com jornalismo policial de ponta e transmissao ao vivo.",
                        quality = "HD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5770560.m3u8", "http://64.31.23.207:80/live/59471019/60849037/5770560.ts")
                    )
                )
            ),
            ChannelCategory(
                id = "cat_band",
                name = "Band",
                isLocked = false,
                channels = listOf(
                    Channel(
                        id = "ch_176",
                        number = "127",
                        title = "Band SP - Band SP",
                        category = "Band",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769511.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/band-sp-band-sp.png",
                        description = "Band Sao Paulo: O canal do esporte, Formula 1, Jogo Aberto, Os Donos da Bola e MasterChef.",
                        quality = "HD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769511.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769511.m3u8")
                    ),
                    Channel(
                        id = "ch_175",
                        number = "128",
                        title = "Band RJ - Band Rio",
                        category = "Band",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/band-rj-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/band-rj-band-rio.png",
                        description = "Band Rio trazendo debates do futebol carioca e noticias da regiao metropolitana.",
                        quality = "HD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769506.m3u8", "http://64.31.23.207:80/live/59471019/60849037/5769506.ts")
                    )
                )
            ),
            ChannelCategory(
                id = "cat_noticias",
                name = "Notícias",
                isLocked = false,
                channels = listOf(
                    Channel(
                        id = "ch_212",
                        number = "129",
                        title = "CNN Brasil",
                        category = "Notícias",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/cnn-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/cnn-brasil.png",
                        description = "A maior marca de noticias do mundo no Brasil com analise politica e economica 24h.",
                        quality = "HD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769210.m3u8", "http://64.31.23.207:80/live/59471019/60849037/5769210.ts")
                    ),
                    Channel(
                        id = "ch_213",
                        number = "130",
                        title = "Jovem Pan News",
                        category = "Notícias",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/jovem-pam-news-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/jovem-pan-news.png",
                        description = "Opiniao forte, debates acalorados, Pingos nos Is e transmissao em tempo real.",
                        quality = "HD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769217.m3u8", "http://64.31.23.207:80/live/59471019/60849037/5769217.ts")
                    ),
                    Channel(
                        id = "ch_214",
                        number = "131",
                        title = "Band News",
                        category = "Notícias",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/band-news-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/band-news.png",
                        description = "Em 20 minutos tudo pode mudar: informacao rapida e atualizada no Brasil e no mundo.",
                        quality = "HD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769207.m3u8", "http://64.31.23.207:80/live/59471019/60849037/5769207.ts")
                    ),
                    Channel(
                        id = "ch_222",
                        number = "132",
                        title = "Record News",
                        category = "Notícias",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/record-news-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/record-news.png",
                        description = "O principal canal de jornalismo da televisao aberta no Brasil.",
                        quality = "HD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769220.m3u8", "http://64.31.23.207:80/live/59471019/60849037/5769220.ts")
                    )
                )
            ),
            ChannelCategory(
                id = "cat_f1",
                name = "Fórmula 1 (F1)",
                isLocked = false,
                channels = listOf(
                    Channel(
                        id = "ch_f1",
                        number = "133",
                        title = "Fórmula 1 - Transmissão Oficial HD",
                        category = "Fórmula 1 (F1)",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/3460200.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/33/F1.svg/500px-F1.svg.png",
                        description = "Transmissao oficial ao vivo dos treinos livres, classificacao, corridas sprint e Grandes Premios da Formula 1 no SporTV e Globo.",
                        quality = "FHD",
                        backupUrls = listOf(
                            "http://64.31.23.207:80/live/59471019/60849037/5726132.m3u8",
                            "http://64.31.23.207:80/live/59471019/60849037/5298295.m3u8"
                        )
                    )
                )
            ),
            ChannelCategory(
                id = "cat_kids",
                name = "Infantil & Kids",
                isLocked = false,
                channels = listOf(
                    Channel(
                        id = "ch_cartoon",
                        number = "134",
                        title = "Cartoon Network HD",
                        category = "Infantil & Kids",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5770501.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/80/Cartoon_Network_2010_logo.svg/320px-Cartoon_Network_2010_logo.svg.png",
                        description = "O canal dos desenhos animados: Ben 10, Teen Titans Go, Hora de Aventura e Incrivel Mundo de Gumball.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5770501.ts", "http://gelosalgados.site:80/live/59471019/60849037/5770501.m3u8")
                    ),
                    Channel(
                        id = "ch_cartoonito",
                        number = "135",
                        title = "Cartoonito HD",
                        category = "Infantil & Kids",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769466.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/cartoonito.png",
                        description = "O canal infantil pre-escolar oficial (sucessor do Boomerang): Batwheels, Lucas a Aranha e Thomas.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769466.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769466.m3u8")
                    ),
                    Channel(
                        id = "ch_nickelodeon",
                        number = "136",
                        title = "Nickelodeon HD",
                        category = "Infantil & Kids",
                        streamUrl = "https://x1colegal.com/hls/stream.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/nickelodeon.png",
                        description = "O mundo Nick: Bob Esponja, The Loud House, Tartarugas Ninja e Henry Danger.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_nicktoons",
                        number = "137",
                        title = "Nicktoons HD",
                        category = "Infantil & Kids",
                        streamUrl = "https://stmv2.srvif.com/nicktoons/nicktoons/playlist.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/7/7a/Nickelodeon_2009_logo.svg/250px-Nickelodeon_2009_logo.svg.png",
                        description = "Os melhores desenhos animados classicos e modernos da Nickelodeon 24 horas por dia.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_tooncast",
                        number = "138",
                        title = "Tooncast HD",
                        category = "Infantil & Kids",
                        streamUrl = "http://170.83.16.50:80/TOONCAST/index.m3u8",
                        logoUrl = "http://acstatic.co/logos/canais/tooncast.png",
                        description = "Os classicos inesqueciveis: Coragem o Cao Covarde, O Laboratorio de Dexter e Os Flintstones.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_discovery_kids_kids",
                        number = "139",
                        title = "Discovery Kids HD",
                        category = "Infantil & Kids",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5770298.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/07/Discovery_Kids_logo_%282016%29.svg/320px-Discovery_Kids_logo_%282016%29.svg.png",
                        description = "O canal lider para o publico infantil: Peppa Pig, O Show da Luna e Doki.",
                        quality = "HD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5770298.ts", "http://gelosalgados.site:80/live/59471019/60849037/5770298.m3u8")
                    ),
                    Channel(
                        id = "ch_gloob",
                        number = "140",
                        title = "Gloob HD",
                        category = "Infantil & Kids",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769231.m3u8",
                        logoUrl = "https://acstatic.co/logos/canais/gloob.png",
                        description = "Detetives do Predio Azul (DPA), Miraculous: As Aventuras de Ladybug e Bugados.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769231.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769231.m3u8")
                    ),
                    Channel(
                        id = "ch_gloobinho",
                        number = "141",
                        title = "Gloobinho HD",
                        category = "Infantil & Kids",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769231.m3u8",
                        logoUrl = "http://acstatic.co/logos/canais/gloobinho.png",
                        description = "Historias encantadoras, musica e brincadeiras para a primeira infancia.",
                        quality = "HD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769231.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769231.m3u8")
                    ),
                    Channel(
                        id = "ch_ratimbum",
                        number = "142",
                        title = "TV Rá-Tim-Bum HD",
                        category = "Infantil & Kids",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5726198.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/tv-ra-tim-bum.png",
                        description = "O canal infantil brasileiro da Fundacao Padre Anchieta / TV Cultura: Cocorico e Ilha Ra-Tim-Bum.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_344",
                        number = "143",
                        title = "Rakuten TV Kids",
                        category = "Infantil & Kids",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5769606.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/rakuten-tv-kids.png",
                        description = "Animacoes divertidas e educativas com programacao segura para criancas.",
                        quality = "HD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769606.ts", "http://gelosalgados.site:80/live/59471019/60849037/5769606.m3u8")
                    ),
                    Channel(
                        id = "ch_207",
                        number = "144",
                        title = "Retro Cartoon Channel",
                        category = "Infantil & Kids",
                        streamUrl = "https://stmv1.video.brstream.com.br/retrotv/retrotv/playlist.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/retro-cartoon-channel.png",
                        description = "Os desenhos animados nostalgicos das decadas de 80, 90 e 2000.",
                        quality = "HD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769598.m3u8", "http://64.31.23.207:80/live/59471019/60849037/5769598.ts")
                    )
                )
            ),
            ChannelCategory(
                id = "cat_musica",
                name = "Músicas & Shows",
                isLocked = false,
                channels = listOf(
                    Channel(
                        id = "ch_396",
                        number = "140",
                        title = "Now 70s",
                        category = "Músicas & Shows",
                        streamUrl = "https://jmp2.uk/plu-63dac32bda711800088a2ba3.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/now-70s.png",
                        description = "Os maiores sucessos que marcaram a inesquecivel decada de 1970.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_361",
                        number = "141",
                        title = "KPOP TV",
                        category = "Músicas & Shows",
                        streamUrl = "https://cdn-uw2-prod.tsv2.amagi.tv/linear/amg01131-tracetv-tracebrazuca-samsungbr/playlist.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/kpop-tv.png",
                        description = "Os melhores videoclipes, dancas e fenomenos do pop coreano mundial.",
                        quality = "HD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/5769428.m3u8", "http://64.31.23.207:80/live/59471019/60849037/5769428.ts")
                    )
                )
            )

        )
    }
    private const val SERVER_BASE = "http://64.31.23.207:80"
    private const val USERNAME = "59471019"
    private const val PASSWORD = "60849037"

    @Volatile
    private var dynamicJogosDoDia: List<Channel>? = null
    private var lastJogosFetchTime: Long = 0L

    fun getFallbackJogosDoDia(): List<Channel> {
        return listOf(
            Channel(
                id = "jogo_dia_317686",
                number = "15",
                title = "[15:00] Ah-Ali x Al-Khaleej",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://64.31.23.207:80/live/59471019/60849037/3460209.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Partida ao vivo de hoje: [15:00] Ah-Ali x Al-Khaleej",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_317687",
                number = "16",
                title = "[21:30] Flamengo x Estudiantes",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://64.31.23.207:80/live/59471019/60849037/3460200.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Partida ao vivo de hoje: [21:30] Flamengo x Estudiantes",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_317688",
                number = "17",
                title = "[17:00] Real Madrid x Bayern Munique",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://64.31.23.207:80/live/59471019/60849037/3460203.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_317689",
                number = "18",
                title = "[18:30] Manchester City x Arsenal",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "https://cdn1.uniredes.com.br/hls/caze-tv-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_317690",
                number = "19",
                title = "[19:00] Palmeiras x São Paulo",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "https://cdn1.uniredes.com.br/hls/espn-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_317691",
                number = "20",
                title = "[19:30] Corinthians x Santos",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "https://cdn1.uniredes.com.br/hls/espn-2-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_317692",
                number = "21",
                title = "[20:00] Grêmio x Internacional",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "https://cdn1.uniredes.com.br/hls/espn-4-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_317693",
                number = "22",
                title = "[21:00] Atlético-MG x Cruzeiro",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5726118.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_317694",
                number = "23",
                title = "[21:30] Botafogo x Fluminense",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://64.31.23.207:80/live/59471019/60849037/3460209.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_317695",
                number = "24",
                title = "[22:00] Boca Juniors x River Plate",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://64.31.23.207:80/live/59471019/60849037/3460200.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_317696",
                number = "25",
                title = "Jogos Ao Vivo (Canal 11)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://64.31.23.207:80/live/59471019/60849037/3460203.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_318080",
                number = "26",
                title = "Jogos Ao Vivo (Canal 12)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "https://cdn1.uniredes.com.br/hls/caze-tv-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_318081",
                number = "27",
                title = "Jogos Ao Vivo (Canal 13)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "https://cdn1.uniredes.com.br/hls/espn-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_318082",
                number = "28",
                title = "Jogos Ao Vivo (Canal 14)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "https://cdn1.uniredes.com.br/hls/espn-2-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_318083",
                number = "29",
                title = "Jogos Ao Vivo (Canal 15)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "https://cdn1.uniredes.com.br/hls/espn-4-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_318084",
                number = "30",
                title = "Jogos Ao Vivo (Canal 16)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5726118.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_318085",
                number = "31",
                title = "Jogos Ao Vivo (Canal 17)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://64.31.23.207:80/live/59471019/60849037/3460209.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_318086",
                number = "32",
                title = "Jogos Ao Vivo (Canal 18)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://64.31.23.207:80/live/59471019/60849037/3460200.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_318606",
                number = "33",
                title = "Jogos Ao Vivo (Canal 19)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://64.31.23.207:80/live/59471019/60849037/3460203.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_318607",
                number = "34",
                title = "Jogos Ao Vivo (Canal 20)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "https://cdn1.uniredes.com.br/hls/caze-tv-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_318608",
                number = "35",
                title = "Jogos Ao Vivo (Canal 21)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "https://cdn1.uniredes.com.br/hls/espn-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_318609",
                number = "36",
                title = "Jogos Ao Vivo (Canal 22)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "https://cdn1.uniredes.com.br/hls/espn-2-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_318610",
                number = "37",
                title = "Jogos Ao Vivo (Canal 23)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "https://cdn1.uniredes.com.br/hls/espn-4-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_318611",
                number = "38",
                title = "Jogos Ao Vivo (Canal 24)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5726118.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_318612",
                number = "39",
                title = "Jogos Ao Vivo (Canal 25)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://64.31.23.207:80/live/59471019/60849037/3460209.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_318613",
                number = "40",
                title = "Jogos Ao Vivo (Canal 26)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://64.31.23.207:80/live/59471019/60849037/3460200.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_361372",
                number = "41",
                title = "Jogos Ao Vivo (Canal 27)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://64.31.23.207:80/live/59471019/60849037/3460203.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_361373",
                number = "42",
                title = "Jogos Ao Vivo (Canal 28)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "https://cdn1.uniredes.com.br/hls/caze-tv-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_361374",
                number = "43",
                title = "Jogos Ao Vivo (Canal 29)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "https://cdn1.uniredes.com.br/hls/espn-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_362685",
                number = "44",
                title = "Jogos Ao Vivo (Canal 30)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "https://cdn1.uniredes.com.br/hls/espn-2-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_362686",
                number = "45",
                title = "Jogos Ao Vivo (Canal 31)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "https://cdn1.uniredes.com.br/hls/espn-4-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_362687",
                number = "46",
                title = "Jogos Ao Vivo (Canal 32)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5726118.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_362688",
                number = "47",
                title = "Jogos Ao Vivo (Canal 33)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://64.31.23.207:80/live/59471019/60849037/3460209.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_362826",
                number = "48",
                title = "Jogos Ao Vivo (Canal 34)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://64.31.23.207:80/live/59471019/60849037/3460200.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            )
        )
    }

    suspend fun fetchJogosDoDia(): List<Channel> = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        if (dynamicJogosDoDia != null && dynamicJogosDoDia!!.isNotEmpty() && now - lastJogosFetchTime < 5 * 60 * 1000L) {
            return@withContext dynamicJogosDoDia!!
        }
        try {
            val urlStr = "$SERVER_BASE/player_api.php?username=$USERNAME&password=$PASSWORD&action=get_live_streams&category_id=4024"
            val jsonText = httpGet(urlStr) ?: return@withContext dynamicJogosDoDia ?: getFallbackJogosDoDia()
            val array = JSONArray(jsonText)
            val list = mutableListOf<Channel>()

            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val sid = obj.optString("stream_id", "")
                val rawName = obj.optString("name", "").trim()
                val icon = obj.optString("stream_icon", "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png").ifEmpty {
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png"
                }

                if (sid.isNotEmpty()) {
                    val streamUrl = "$SERVER_BASE/live/$USERNAME/$PASSWORD/$sid.ts"
                    val isRealMatch = rawName.isNotEmpty() && !rawName.contains("Breve", ignoreCase = true)
                    val displayName = if (isRealMatch) rawName else "Jogos Ao Vivo (Canal ${String.format("%02d", i + 1)})"

                    list.add(
                        Channel(
                            id = "jogo_dia_$sid",
                            number = String.format("%02d", i + 15),
                            title = displayName,
                            category = "⚽ Jogos do Dia (Ao Vivo)",
                            streamUrl = streamUrl,
                            logoUrl = icon,
                            description = if (isRealMatch) {
                                "Partida ao vivo de hoje: $displayName"
                            } else {
                                "Canal reservado para transmissões dos jogos do dia."
                            },
                            quality = "FHD"
                        )
                    )
                }
            }

            val sorted = list.sortedWith(
                compareByDescending<Channel> { it.title.startsWith("[") }
                    .thenBy { it.number }
            )
            dynamicJogosDoDia = sorted
            lastJogosFetchTime = now
            sorted
        } catch (e: Exception) {
            dynamicJogosDoDia ?: getFallbackJogosDoDia()
        }
    }

    private fun httpGet(urlStr: String): String? {
        return try {
            val url = URL(urlStr)
            val conn = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 6000
                readTimeout = 6000
                setRequestProperty("User-Agent", "IPTVSmartersPro")
            }
            if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                conn.inputStream.bufferedReader().use { it.readText() }
            } else null
        } catch (e: Exception) {
            null
        }
    }

    fun getCategoriesForSection(section: MainSection): List<ChannelCategory> {
        val allCats = getCategories()

        return when (section) {
            MainSection.TV -> {
                val tvCats = mutableListOf<ChannelCategory>()
                val catIds = listOf(
                    "cat_globo", "cat_sbt", "cat_record", "cat_band", "cat_noticias",
                    "cat_discovery", "cat_docs", "cat_variedades", "cat_musica",
                    "cat_fazenda", "cat_24h", "cat_4k", "cat_h265"
                )
                val matched = catIds.mapNotNull { id -> allCats.find { it.id == id } }
                val allTvChannels = matched.flatMap { it.channels }.distinctBy { it.id }

                val catTodosTv = ChannelCategory(
                    id = "cat_tv_todos",
                    name = "⭐ Todos da TV",
                    iconRes = R.drawable.ic_all_channels,
                    channels = allTvChannels
                )
                tvCats.add(catTodosTv)
                tvCats.addAll(matched)
                tvCats
            }

            MainSection.FILMES -> {
                val movieCats = mutableListOf<ChannelCategory>()

                val baseTelecine = allCats.find { it.id == "cat_telecine" }?.channels ?: emptyList()
                val telecine4k = allCats.find { it.id == "cat_4k" }?.channels?.filter { it.title.contains("telecine", ignoreCase = true) } ?: emptyList()
                val telecineH265 = allCats.find { it.id == "cat_h265" }?.channels?.filter { it.title.contains("telecine", ignoreCase = true) } ?: emptyList()
                val telecineChannels = (baseTelecine + telecine4k + telecineH265).distinctBy { it.id }
                val catTelecine = ChannelCategory(
                    id = "cat_telecine",
                    name = "🎬 Rede Telecine",
                    iconRes = R.drawable.ic_movie_vod,
                    channels = telecineChannels
                )

                val baseHbo = allCats.find { it.id == "cat_hbo" }?.channels ?: emptyList()
                val hbo4k = allCats.find { it.id == "cat_4k" }?.channels?.filter { it.title.contains("hbo", ignoreCase = true) } ?: emptyList()
                val hboH265 = allCats.find { it.id == "cat_h265" }?.channels?.filter { it.title.contains("hbo", ignoreCase = true) } ?: emptyList()
                val hboChannels = (baseHbo + hbo4k + hboH265).distinctBy { it.id }
                val catHbo = ChannelCategory(
                    id = "cat_hbo",
                    name = "🍿 Rede HBO & Max",
                    iconRes = R.drawable.ic_movie_vod,
                    channels = hboChannels
                )

                val seriesKeywords = listOf("warner", "sony channel", "universal tv", "axn", "novela")
                val cinemaChannels = allCats.find { it.id == "cat_filmes" }?.channels?.filter { ch ->
                    !seriesKeywords.any { ch.title.contains(it, ignoreCase = true) }
                } ?: emptyList()
                val catCinema = ChannelCategory(
                    id = "cat_cinema",
                    name = "🎥 Canais de Cinema & Filmes",
                    iconRes = R.drawable.ic_movie_vod,
                    channels = cinemaChannels
                )

                val allFilmes = (telecineChannels + hboChannels + cinemaChannels).distinctBy { it.id }
                val catTodosFilmes = ChannelCategory(
                    id = "cat_filmes_todos",
                    name = "⭐ Todos os Filmes",
                    iconRes = R.drawable.ic_all_channels,
                    channels = allFilmes
                )

                movieCats.add(catTodosFilmes)
                movieCats.add(catTelecine)
                movieCats.add(catHbo)
                movieCats.add(catCinema)
                movieCats
            }

            MainSection.SERIES -> {
                val seriesCats = mutableListOf<ChannelCategory>()

                val seriesKeywords = listOf("warner", "sony channel", "universal tv", "axn", "novela")
                val seriesChannels = allCats.find { it.id == "cat_filmes" }?.channels?.filter { ch ->
                    seriesKeywords.any { ch.title.contains(it, ignoreCase = true) }
                } ?: emptyList()
                val extraSeriesH265 = allCats.find { it.id == "cat_h265" }?.channels?.filter { ch ->
                    ch.title.contains("novelissima", ignoreCase = true) || ch.title.contains("like tv", ignoreCase = true)
                } ?: emptyList()
                val catCanaisSeries = ChannelCategory(
                    id = "cat_series_canais",
                    name = "📺 Canais de Séries",
                    iconRes = R.drawable.ic_series_vod,
                    channels = (seriesChannels + extraSeriesH265).distinctBy { it.id }
                )

                val series24Keywords = listOf("chaves", "chapolin", "todo mundo", "friends", "sobrenatural", "the walking", "big bang", "dois homens", "greys")
                val series24Channels = allCats.find { it.id == "cat_24h" }?.channels?.filter { ch ->
                    series24Keywords.any { ch.title.contains(it, ignoreCase = true) }
                } ?: emptyList()
                val catSeries24h = ChannelCategory(
                    id = "cat_series_24h",
                    name = "⏳ Séries 24 Horas",
                    iconRes = R.drawable.ic_series_vod,
                    channels = series24Channels
                )

                val allSeries = (catCanaisSeries.channels + series24Channels).distinctBy { it.id }
                val catTodasSeries = ChannelCategory(
                    id = "cat_series_todos",
                    name = "⭐ Todas as Séries",
                    iconRes = R.drawable.ic_all_channels,
                    channels = allSeries
                )

                seriesCats.add(catTodasSeries)
                seriesCats.add(catCanaisSeries)
                seriesCats.add(catSeries24h)
                seriesCats
            }

            MainSection.INFANTIL -> {
                val kidsCats = mutableListOf<ChannelCategory>()

                val kidsChannels = (allCats.find { it.id == "cat_kids" }?.channels ?: emptyList()) +
                        (allCats.find { it.id == "cat_discovery" }?.channels?.filter { it.title.contains("discovery kids", ignoreCase = true) } ?: emptyList())
                val catCanaisKids = ChannelCategory(
                    id = "cat_kids_canais",
                    name = "👶 Canais Infantis",
                    iconRes = R.drawable.ic_star_kids,
                    channels = kidsChannels.distinctBy { it.id }
                )

                val kids24Keywords = listOf("desenho", "anime", "kids", "animacao", "dragon ball", "naruto", "bob esponja", "simpsons", "turma da monica", "ben 10", "pica pau", "tom e jerry")
                val desenhos24Channels = allCats.find { it.id == "cat_24h" }?.channels?.filter { ch ->
                    kids24Keywords.any { ch.title.contains(it, ignoreCase = true) }
                } ?: emptyList()
                val catDesenhos24h = ChannelCategory(
                    id = "cat_kids_24h",
                    name = "🎈 Desenhos 24 Horas",
                    iconRes = R.drawable.ic_star_kids,
                    channels = desenhos24Channels
                )

                val allKids = (catCanaisKids.channels + desenhos24Channels).distinctBy { it.id }
                val catTodosKids = ChannelCategory(
                    id = "cat_kids_todos",
                    name = "⭐ Todos os Infantis",
                    iconRes = R.drawable.ic_all_channels,
                    channels = allKids
                )

                kidsCats.add(catTodosKids)
                kidsCats.add(catCanaisKids)
                kidsCats.add(catDesenhos24h)
                kidsCats
            }

            MainSection.FUTEBOL -> {
                val futCats = mutableListOf<ChannelCategory>()

                val basePrem = allCats.find { it.id == "cat_premiere" }?.channels ?: emptyList()
                val prem4k = allCats.find { it.id == "cat_4k" }?.channels?.filter { it.title.contains("premiere", ignoreCase = true) } ?: emptyList()
                val premH265 = allCats.find { it.id == "cat_h265" }?.channels?.filter { it.title.contains("premiere", ignoreCase = true) } ?: emptyList()
                val premiereChannels = (basePrem + prem4k + premH265).distinctBy { it.id }
                val catPremiere = ChannelCategory(
                    id = "cat_premiere",
                    name = "⚽ Premiere FC (Todos os Jogos)",
                    iconRes = R.drawable.ic_premiere,
                    channels = premiereChannels
                )

                val jogosDiaChannels = allCats.find { it.id == "cat_jogos_dia" }?.channels ?: emptyList()
                val catJogosDia = ChannelCategory(
                    id = "cat_jogos_dia",
                    name = "🏆 Jogos do Dia (Ao Vivo)",
                    iconRes = R.drawable.ic_sports_tv,
                    channels = jogosDiaChannels
                )

                val baseSportv = allCats.find { it.id == "cat_esportes" }?.channels ?: emptyList()
                val sportv4k = allCats.find { it.id == "cat_4k" }?.channels?.filter { it.title.contains("sportv", ignoreCase = true) } ?: emptyList()
                val sportvH265 = allCats.find { it.id == "cat_h265" }?.channels?.filter { it.title.contains("sportv", ignoreCase = true) } ?: emptyList()
                val sportvChannels = (baseSportv + sportv4k + sportvH265).distinctBy { it.id }
                val catSportv = ChannelCategory(
                    id = "cat_esportes",
                    name = "📺 SporTV & Canais Esportivos",
                    iconRes = R.drawable.ic_sports_tv,
                    channels = sportvChannels
                )

                val baseEspn = allCats.find { it.id == "cat_espn" }?.channels ?: emptyList()
                val espnH265 = allCats.find { it.id == "cat_h265" }?.channels?.filter { it.title.contains("espn", ignoreCase = true) } ?: emptyList()
                val espnChannels = (baseEspn + espnH265).distinctBy { it.id }
                val catEspn = ChannelCategory(
                    id = "cat_espn",
                    name = "🌐 Rede ESPN (Champions & Ligas)",
                    iconRes = R.drawable.ic_sports_tv,
                    channels = espnChannels
                )

                val campChannels = allCats.find { it.id == "cat_campeonatos" }?.channels ?: emptyList()
                val catCamp = ChannelCategory(
                    id = "cat_campeonatos",
                    name = "🥇 Campeonatos & Estaduais",
                    iconRes = R.drawable.ic_sports_tv,
                    channels = campChannels
                )

                val f1Channels = allCats.find { it.id == "cat_f1" }?.channels ?: emptyList()
                val catF1 = ChannelCategory(
                    id = "cat_f1",
                    name = "🏁 Fórmula 1 & Outros",
                    iconRes = R.drawable.ic_sports_tv,
                    channels = f1Channels
                )

                val allFutebol = (premiereChannels + jogosDiaChannels + sportvChannels + espnChannels + campChannels + f1Channels).distinctBy { it.id }
                val catTodosFut = ChannelCategory(
                    id = "cat_futebol_todos",
                    name = "⭐ Todos do Futebol",
                    iconRes = R.drawable.ic_all_channels,
                    channels = allFutebol
                )

                // 1. Jogos do Dia (Ao Vivo) - no topo com partidas em tempo real
                if (jogosDiaChannels.isNotEmpty()) {
                    futCats.add(catJogosDia)
                } else {
                    val fallbackDia = ChannelCategory(
                        id = "cat_jogos_dia",
                        name = "🏆 Jogos do Dia (Ao Vivo)",
                        iconRes = R.drawable.ic_sports_tv,
                        channels = getFallbackJogosDoDia()
                    )
                    futCats.add(fallbackDia)
                }
                // 2. Campeonatos & Estaduais
                futCats.add(catCamp)
                // 3. Premiere FC (Todos os Jogos)
                futCats.add(catPremiere)
                // 4. SporTV & Canais Esportivos
                futCats.add(catSportv)
                // 5. Rede ESPN (Champions & Ligas)
                futCats.add(catEspn)
                // 6. CazéTV, DAZN & Lutas
                futCats.add(catF1)
                // 7. Todos do Futebol (Grade Consolidada)
                futCats.add(catTodosFut)
                futCats
            }
        }
    }

}
