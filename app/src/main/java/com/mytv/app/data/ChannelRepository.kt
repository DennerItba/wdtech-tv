package com.mytv.app.data

import com.mytv.app.model.Channel
import com.mytv.app.model.ChannelCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

object ChannelRepository {

    fun getCategories(): List<ChannelCategory> {
        return listOf(
            ChannelCategory(
                id = "cat_cinesky",
                name = "Cine Sky",
                isLocked = false,
                channels = listOf(
                    Channel(
                        id = "cinesky_01",
                        number = "01",
                        title = "Cine SKY 01 HD",
                        category = "Cine Sky",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72364.ts",
                        logoUrl = "http://www.fontedecanais.dev/logos/canais/cinesky.png",
                        description = "Cine Sky Oficial: Grandes lancamentos do cinema mundial 24h sem intervalos comerciais.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "cinesky_02",
                        number = "02",
                        title = "Cine SKY 02 HD",
                        category = "Cine Sky",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72348.ts",
                        logoUrl = "http://www.fontedecanais.org/logos/canais/cinesky.png",
                        description = "Os melhores sucessos de bilheteria e estreias exclusivas do cinema direto na sua TV.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "cinesky_03",
                        number = "03",
                        title = "Cine SKY 03 HD",
                        category = "Cine Sky",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72120.ts",
                        logoUrl = "http://www.fontedecanais.org/logos/canais/cinesky.png",
                        description = "Filmes consagrados, acao, suspense e producoes premiadas de Hollywood.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "cinesky_05",
                        number = "04",
                        title = "Cine SKY 05 HD",
                        category = "Cine Sky",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72112.ts",
                        logoUrl = "http://www.fontedecanais.org/logos/canais/cinesky.png",
                        description = "Cinema em alta definicao com lancamentos imperdiveis e classicos mundiais.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "cinesky_acao",
                        number = "05",
                        title = "Cine SKY Acao HD",
                        category = "Cine Sky",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72319.ts",
                        logoUrl = "http://www.fontedecanais.dev/logos/canais/cinesky.png",
                        description = "Tiros, explosoes e adrenalina pura com os maiores astros da acao.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "cinesky_fam",
                        number = "06",
                        title = "Cine SKY Familia HD",
                        category = "Cine Sky",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72360.ts",
                        logoUrl = "http://www.fontedecanais.org/logos/canais/cinesky.png",
                        description = "Comedias, animacoes e os melhores filmes para toda a familia reunir.",
                        quality = "FHD"
                    )
                )
            ),
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
                        streamUrl = "http://corruptx.org:80/live/hfp5ejnrey/zq0u3kzads/2470061.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/pt/thumb/d/d7/Logotipo_de_A_Fazenda.png/320px-Logotipo_de_A_Fazenda.png",
                        description = "Camera principal ao vivo 24 horas por dia dentro da sede de A Fazenda com todos os peoes.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_fazenda_2",
                        number = "08",
                        title = "A Fazenda 24H - Camera 2 (Baia & Animais)",
                        category = "A Fazenda 24H (7 Câmeras)",
                        streamUrl = "http://corruptx.org:80/live/hfp5ejnrey/zq0u3kzads/2470062.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/pt/thumb/d/d7/Logotipo_de_A_Fazenda.png/320px-Logotipo_de_A_Fazenda.png",
                        description = "Camera exclusiva na baia dos animais, rotina rural e conversas reservadas.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_fazenda_3",
                        number = "09",
                        title = "A Fazenda 24H - Camera 3 (Oficina & Varanda)",
                        category = "A Fazenda 24H (7 Câmeras)",
                        streamUrl = "http://corruptx.org:80/live/hfp5ejnrey/zq0u3kzads/2470063.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/pt/thumb/d/d7/Logotipo_de_A_Fazenda.png/320px-Logotipo_de_A_Fazenda.png",
                        description = "Acompanhe as estrategias e reunioes secretas na area da varanda e oficina.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_fazenda_4",
                        number = "10",
                        title = "A Fazenda 24H - Camera 4 (Cozinha & Sala)",
                        category = "A Fazenda 24H (7 Câmeras)",
                        streamUrl = "http://corruptx.org:80/live/hfp5ejnrey/zq0u3kzads/2470064.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/pt/thumb/d/d7/Logotipo_de_A_Fazenda.png/320px-Logotipo_de_A_Fazenda.png",
                        description = "Toda a movimentacao das refeicoes, discussoes na cozinha e momentos na sala de estar.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_fazenda_5",
                        number = "11",
                        title = "A Fazenda 24H - Camera 5 (Piscina & Academia)",
                        category = "A Fazenda 24H (7 Câmeras)",
                        streamUrl = "http://corruptx.org:80/live/hfp5ejnrey/zq0u3kzads/2470065.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/pt/thumb/d/d7/Logotipo_de_A_Fazenda.png/320px-Logotipo_de_A_Fazenda.png",
                        description = "Area externa, treinos na academia, banho de sol na piscina e conversas quentes.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_fazenda_6",
                        number = "12",
                        title = "A Fazenda 24H - Camera 6 (Quartos & Espelho)",
                        category = "A Fazenda 24H (7 Câmeras)",
                        streamUrl = "http://corruptx.org:80/live/hfp5ejnrey/zq0u3kzads/2470066.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/pt/thumb/d/d7/Logotipo_de_A_Fazenda.png/320px-Logotipo_de_A_Fazenda.png",
                        description = "Transmissao ao vivo dos quartos, pre-festas, fofocas e descanso dos peoes.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_fazenda_7",
                        number = "13",
                        title = "A Fazenda 24H - Camera 7 (Mosaico Multicameras)",
                        category = "A Fazenda 24H (7 Câmeras)",
                        streamUrl = "http://corruptx.org:80/live/hfp5ejnrey/zq0u3kzads/2470067.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/b/b4/Logo_A_Fazenda_-_Atual.png",
                        description = "Visao panoramica mosaico transmitindo todos os cantos da fazenda simultaneamente.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_bbb_24h",
                        number = "14",
                        title = "Reality BBB 24H HD",
                        category = "A Fazenda 24H (7 Câmeras)",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72598.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/cd/Big_Brother_Brasil_logo.svg/320px-Big_Brother_Brasil_logo.svg.png",
                        description = "A casa mais vigiada do Brasil ao vivo 24 horas por dia com cobertura completa.",
                        quality = "FHD"
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
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72262.ts",
                        logoUrl = "http://vivlar.me/static/logos/canais/premiere.png",
                        description = "O melhor do futebol brasileiro 24 horas por dia com jogos ao vivo e programas exclusivos.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "prem_2",
                        number = "50",
                        title = "Premiere 2 FHD",
                        category = "⚽ Premiere FC (Todos os Jogos)",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72237.ts",
                        logoUrl = "http://vivlar.me/static/logos/canais/premiere.png",
                        description = "Transmissões ao vivo das Séries A e B do Brasileirão e campeonatos regionais.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "prem_3",
                        number = "51",
                        title = "Premiere 3 FHD",
                        category = "⚽ Premiere FC (Todos os Jogos)",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72241.ts",
                        logoUrl = "http://vivlar.me/static/logos/canais/premiere.png",
                        description = "Partidas simultâneas ao vivo e transmissão completa dos principais clubes do Brasil.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "prem_4",
                        number = "52",
                        title = "Premiere 4 FHD",
                        category = "⚽ Premiere FC (Todos os Jogos)",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72245.ts",
                        logoUrl = "http://vivlar.me/static/logos/canais/premiere.png",
                        description = "Futebol ao vivo em alta definição direto dos estádios de todo o Brasil.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "prem_5",
                        number = "53",
                        title = "Premiere 5 FHD",
                        category = "⚽ Premiere FC (Todos os Jogos)",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72249.ts",
                        logoUrl = "http://vivlar.me/static/logos/canais/premiere.png",
                        description = "Canal de transmissão direta de partidas simultâneas dos campeonatos nacionais.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "prem_6",
                        number = "54",
                        title = "Premiere 6 FHD",
                        category = "⚽ Premiere FC (Todos os Jogos)",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72253.ts",
                        logoUrl = "http://vivlar.me/static/logos/canais/premiere.png",
                        description = "Cobertura completa dos jogos da Série B e confrontos regionais.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "prem_7",
                        number = "55",
                        title = "Premiere 7 FHD",
                        category = "⚽ Premiere FC (Todos os Jogos)",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72257.ts",
                        logoUrl = "http://vivlar.me/static/logos/canais/premiere.png",
                        description = "Canal pay-per-view exclusivo para transmissões de mata-mata e decisões.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "prem_8",
                        number = "56",
                        title = "Premiere 8 FHD",
                        category = "⚽ Premiere FC (Todos os Jogos)",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/180158.ts",
                        logoUrl = "http://vivlar.me/static/logos/canais/premiere.png",
                        description = "Transmissão em tempo real de partidas complementares da rodada de futebol.",
                        quality = "FHD"
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
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/184569.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/320px-Soccerball.svg.png",
                        description = "Transmissões ao vivo dos Campeonatos Estaduais, divisões de acesso e copas regionais.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "sportynet_2",
                        number = "58",
                        title = "SportyNet 2 FHD",
                        category = "🏆 Campeonatos & Estaduais",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/184578.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/320px-Soccerball.svg.png",
                        description = "Canal complementar com múltiplos jogos ao vivo simultâneos dos campeonatos estaduais.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "dazn_1",
                        number = "59",
                        title = "DAZN 1 FHD",
                        category = "🏆 Campeonatos & Estaduais",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/184550.ts",
                        logoUrl = "http://vivlar.me/static/logos/canais/dazn.png",
                        description = "O serviço global de esportes: Série C, futebol europeu, boxe e torneios mundiais.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "fcf_tv_1",
                        number = "60",
                        title = "FCF TV 1 FHD",
                        category = "🏆 Campeonatos & Estaduais",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/247976.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/320px-Soccerball.svg.png",
                        description = "Transmissão oficial das federações estaduais com jogos ao vivo e copas do Nordeste.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "xsports_fhd",
                        number = "61",
                        title = "X Sports FHD",
                        category = "🏆 Campeonatos & Estaduais",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/340449.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/320px-Soccerball.svg.png",
                        description = "Esportes alternativos, campeonatos internacionais e partidas ao vivo.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "bandsports_campeonatos",
                        number = "62",
                        title = "BandSports FHD",
                        category = "🏆 Campeonatos & Estaduais",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/71801.ts",
                        logoUrl = "http://vivlar.me/static/logos/canais/bandsports.png",
                        description = "Futebol internacional, campeonatos de base, tênis, automobilismo e esportes olímpicos.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "combate_campeonatos",
                        number = "63",
                        title = "Combate FHD",
                        category = "🏆 Campeonatos & Estaduais",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/71842.ts",
                        logoUrl = "http://vivlar.me/static/logos/canais/combate.png",
                        description = "O canal oficial das lutas: UFC, Boxe, Jiu-Jitsu e grandes torneios de artes marciais.",
                        quality = "FHD"
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
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72329.ts",
                        logoUrl = "http://vivlar.me/static/logos/canais/sportv.png",
                        description = "O canal campeão: cobertura ao vivo dos maiores campeonatos esportivos do Brasil e do mundo.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "sportv_2",
                        number = "65",
                        title = "SporTV 2 FHD",
                        category = "📺 SporTV & Canais Esportivos",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72321.ts",
                        logoUrl = "http://vivlar.me/static/logos/canais/sportv2.png",
                        description = "Vôlei, basquete, tênis, automobilismo e futebol ao vivo todos os dias.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "sportv_3",
                        number = "66",
                        title = "SporTV 3 FHD",
                        category = "📺 SporTV & Canais Esportivos",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72325.ts",
                        logoUrl = "http://vivlar.me/static/logos/canais/sportv3.png",
                        description = "Esportes olímpicos, atletismo, natação e transmissão simultânea de grandes torneios.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "sportv_4",
                        number = "67",
                        title = "SporTV 4 FHD",
                        category = "📺 SporTV & Canais Esportivos",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/276092.ts",
                        logoUrl = "http://vivlar.me/static/logos/canais/sportv2.png",
                        description = "Canal de apoio para eventos simultâneos e rodadas decisivas em alta resolução.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "bandsports_fhd",
                        number = "68",
                        title = "BandSports FHD",
                        category = "📺 SporTV & Canais Esportivos",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/71801.ts",
                        logoUrl = "http://vivlar.me/static/logos/canais/bandsports.png",
                        description = "Futebol internacional, treinos de Fórmula 1, lutas e esportes radicais ao vivo.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "combate_fhd",
                        number = "69",
                        title = "Combate FHD",
                        category = "📺 SporTV & Canais Esportivos",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/71842.ts",
                        logoUrl = "http://vivlar.me/static/logos/canais/combate.png",
                        description = "O canal oficial das artes marciais: UFC, Boxe, Jiu-Jitsu, Kickboxing e Jungle Fight 24H.",
                        quality = "FHD"
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
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/71911.ts",
                        logoUrl = "http://vivlar.me/static/logos/canais/espn.png",
                        description = "O lider mundial em esportes: Premier League, LaLiga, Serie A italiana e NFL.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "espn_2",
                        number = "71",
                        title = "ESPN 2 FHD",
                        category = "🌐 Rede ESPN (Champions & Ligas)",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/71919.ts",
                        logoUrl = "http://vivlar.me/static/logos/canais/espn2.png",
                        description = "Futebol europeu, NBA, beisebol da MLB, college football e programas esportivos de debate.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "espn_3",
                        number = "72",
                        title = "ESPN 3 FHD",
                        category = "🌐 Rede ESPN (Champions & Ligas)",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/71907.ts",
                        logoUrl = "http://vivlar.me/static/logos/canais/espn.png",
                        description = "Tenis com Grand Slams ao vivo, ciclismo, esportes a motor e jogos internacionais.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "espn_4",
                        number = "73",
                        title = "ESPN 4 FHD",
                        category = "🌐 Rede ESPN (Champions & Ligas)",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/71957.ts",
                        logoUrl = "http://vivlar.me/static/logos/canais/foxsports1.png",
                        description = "A casa da Copa Libertadores da América, Copa Sul-Americana e futebol sul-americano.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "espn_5",
                        number = "74",
                        title = "ESPN 5 FHD",
                        category = "🌐 Rede ESPN (Champions & Ligas)",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/71952.ts",
                        logoUrl = "http://vivlar.me/static/logos/canais/foxsports2.png",
                        description = "Cobertura multiplataforma e grandes eventos de esportes de combate e automobilismo.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "espn_6",
                        number = "75",
                        title = "ESPN 6 FHD",
                        category = "🌐 Rede ESPN (Champions & Ligas)",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/71915.ts",
                        logoUrl = "http://vivlar.me/static/logos/canais/espnextra.png",
                        description = "Transmissoes simultaneas de rodadas europeias, campeonatos de rugby e esportes radicais.",
                        quality = "FHD"
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
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72362.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/82/Telecine_Premium_logo_2019.svg/320px-Telecine_Premium_logo_2019.svg.png",
                        description = "Grandes sucessos de bilheteria e estreias exclusivas do cinema mundial em primeira mao.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "tc_action",
                        number = "43",
                        title = "Telecine Action HD",
                        category = "Rede Telecine",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72346.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/ca/Telecine_Action_logo_2019.svg/320px-Telecine_Action_logo_2019.svg.png",
                        description = "Muita adrenalina, velocidade, acao e perseguicoes sem limite.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "tc_touch",
                        number = "44",
                        title = "Telecine Touch HD",
                        category = "Rede Telecine",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72366.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/eb/Telecine_Touch_logo_2019.svg/320px-Telecine_Touch_logo_2019.svg.png",
                        description = "Emocao e historias marcantes: os maiores dramas e romances de todos os tempos.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "tc_pipoca",
                        number = "45",
                        title = "Telecine Pipoca HD",
                        category = "Rede Telecine",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72358.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a2/Telecine_Pipoca_logo_2019.svg/320px-Telecine_Pipoca_logo_2019.svg.png",
                        description = "Os filmes mais divertidos do cinema nacional e internacional 100% dublados.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "tc_fun",
                        number = "46",
                        title = "Telecine Fun HD",
                        category = "Rede Telecine",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72354.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/8d/Telecine_Fun_logo_2019.svg/320px-Telecine_Fun_logo_2019.svg.png",
                        description = "Comedias e animacoes para garantir boas risadas com a familia reunida.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "tc_cult",
                        number = "47",
                        title = "Telecine Cult HD",
                        category = "Rede Telecine",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72350.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b2/Telecine_Cult_logo_2019.svg/320px-Telecine_Cult_logo_2019.svg.png",
                        description = "Obras-primas da historia da setima arte, cinema de autor e producoes premiadas.",
                        quality = "FHD"
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
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72118.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/de/HBO_logo.svg/320px-HBO_logo.svg.png",
                        description = "Series consagradas, grandes documentarios e as maiores producoes mundiais.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "hbo_2",
                        number = "49",
                        title = "HBO 2 HD",
                        category = "Rede HBO",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72110.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/4/4b/HBO2_logo_2014.svg/320px-HBO2_logo_2014.svg.png",
                        description = "Reprises dos melhores filmes e series em horarios alternativos dublados e legendados.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "hbo_plus",
                        number = "50",
                        title = "HBO Plus HD",
                        category = "Rede HBO",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72125.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/be/HBO_Plus_logo_2019.svg/320px-HBO_Plus_logo_2019.svg.png",
                        description = "Conteudo jovem, comedia, acao e musica da franquia HBO.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "hbo_family",
                        number = "51",
                        title = "HBO Family HD",
                        category = "Rede HBO",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72114.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/7/70/HBO_Family_2014.svg/320px-HBO_Family_2014.svg.png",
                        description = "Programacao voltada para criancas e jovens com animacoes e filmes familiares.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "hbo_sig",
                        number = "52",
                        title = "HBO Signature HD",
                        category = "Rede HBO",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72134.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/9/91/HBO_Signature_logo_2014.svg/320px-HBO_Signature_logo_2014.svg.png",
                        description = "O acervo mais exclusivo e refinado das producoes originais e documentarios HBO.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "hbo_mundi",
                        number = "53",
                        title = "HBO Mundi HD",
                        category = "Rede HBO",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72121.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/de/HBO_logo.svg/320px-HBO_logo.svg.png",
                        description = "Filmes internacionais e producoes independentes aclamadas pela critica.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "hbo_pop",
                        number = "54",
                        title = "HBO Pop HD",
                        category = "Rede HBO",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72129.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/de/HBO_logo.svg/320px-HBO_logo.svg.png",
                        description = "Filmes leves, comedias romanticas e grandes sucessos da cultura pop.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "hbo_xtreme",
                        number = "55",
                        title = "HBO Xtreme HD",
                        category = "Rede HBO",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72138.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/de/HBO_logo.svg/320px-HBO_logo.svg.png",
                        description = "Acao frenetica, suspense e terror para quem gosta de fortes emocoes.",
                        quality = "FHD"
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
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/71824.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/60/Sony_Channel_logo_2019.svg/320px-Sony_Channel_logo_2019.svg.png",
                        description = "O lendario Canal Sony: Grey's Anatomy, The Good Doctor e as series medicas e policiais mais amadas.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_sony_movies",
                        number = "57",
                        title = "Sony Movies HD",
                        category = "Filmes & Séries",
                        streamUrl = "http://45.162.64.114/SONY_MOVIES/index.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/7/77/Sony_Movies_logo_2022.svg/320px-Sony_Movies_logo_2022.svg.png",
                        description = "O acervo completo dos lendarios estudios da Columbia Pictures e Sony Pictures.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "warner_ch",
                        number = "58",
                        title = "Warner Channel HD",
                        category = "Filmes & Séries",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72431.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/7/72/Warner_Channel_2019_logo.svg/320px-Warner_Channel_2019_logo.svg.png",
                        description = "Universo DC, The Big Bang Theory, Friends e os maiores sucessos da Warner Bros.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "megapix_ch",
                        number = "59",
                        title = "Megapix HD",
                        category = "Filmes & Séries",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72166.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f3/Megapix_logo_2020.svg/320px-Megapix_logo_2020.svg.png",
                        description = "O canal dos grandes campeoes de bilheteria dublados com sessao pipoca todo dia.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_universal_tv",
                        number = "60",
                        title = "Universal TV HD",
                        category = "Filmes & Séries",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72419.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e0/Universal_TV_logo_2018.svg/320px-Universal_TV_logo_2018.svg.png",
                        description = "Franquia Chicago (Fire, P.D., Med), Law & Order e grandes investigacoes.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_studio_universal",
                        number = "61",
                        title = "Studio Universal HD",
                        category = "Filmes & Séries",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72334.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/1/1a/Studio_Universal_logo.svg/320px-Studio_Universal_logo.svg.png",
                        description = "Cinema hollywoodiano e classicos dos estúdios Universal Pictures.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_paramount",
                        number = "62",
                        title = "Paramount Network HD",
                        category = "Filmes & Séries",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72217.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f2/Paramount_Network_2018.svg/320px-Paramount_Network_2018.svg.png",
                        description = "Yellowstone, Tulsa King e superproducoes consagradas da Paramount Pictures.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_cinemax",
                        number = "63",
                        title = "Cinemax HD",
                        category = "Filmes & Séries",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/71833.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/1/14/Cinemax_logo_2011.svg/320px-Cinemax_logo_2011.svg.png",
                        description = "Filmes de sucesso, acao e suspense com a chancela do grupo HBO.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_tnt",
                        number = "64",
                        title = "TNT HD",
                        category = "Filmes & Séries",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72375.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b5/TNT_Logo_2016.svg/320px-TNT_Logo_2016.svg.png",
                        description = "Grandes premiacoes como o Oscar, superproducoes cinematograficas e esportes.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_space",
                        number = "65",
                        title = "Space HD",
                        category = "Filmes & Séries",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72317.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/36/Space_logo_2017.svg/320px-Space_logo_2017.svg.png",
                        description = "Especializado em acao, ficcao cientifica, suspense e filmes de luta.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_axn",
                        number = "66",
                        title = "AXN HD",
                        category = "Filmes & Séries",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/71781.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b5/AXN_logo_2015.svg/320px-AXN_logo_2015.svg.png",
                        description = "As maiores series policiais e de investigacao forense do mundo: NCIS e Criminal Minds.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_284",
                        number = "67",
                        title = "TNT Novelas HD",
                        category = "Filmes & Séries",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72342.ts",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/tnt-novelas.png",
                        description = "O canal dedicado aos grandes sucessos mundiais das telenovelas e melodramas.",
                        quality = "FHD"
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
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_388",
                        number = "70",
                        title = "Top Mix Filmes",
                        category = "Filmes & Séries",
                        streamUrl = "http://corruptx.org:80/live/hfp5ejnrey/zq0u3kzads/1169216.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/top-mix-filmes.png",
                        description = "Uma selecao diversificada com filmes de varios generos 24 horas por dia.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_385",
                        number = "71",
                        title = "Hallo! Series",
                        category = "Filmes & Séries",
                        streamUrl = "http://corruptx.org:80/live/hfp5ejnrey/zq0u3kzads/1169218.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/hallo-series.png",
                        description = "Maratonas das melhores series de comedia e drama da televisao.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_331",
                        number = "72",
                        title = "RunTime - Cinema",
                        category = "Filmes & Séries",
                        streamUrl = "http://corruptx.org:80/live/hfp5ejnrey/zq0u3kzads/1169244.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/runtime-cinema.png",
                        description = "Filmes aclamados com exibicao continua e alta definicao.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_332",
                        number = "73",
                        title = "RunTime - Terror",
                        category = "Filmes & Séries",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/138065.ts",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/runtime-terror.png",
                        description = "O melhor do horror psicologico, sobrenatural e suspense horripilante.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_273",
                        number = "74",
                        title = "RunTime - Acao",
                        category = "Filmes & Séries",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/135445.ts",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/runtime-acao.png",
                        description = "Tiros, explosoes e perseguicoes para os apaixonados por adrenalina.",
                        quality = "HD"
                    )
                )
            ),
            ChannelCategory(
                id = "cat_docs",
                name = "Documentários",
                isLocked = false,
                channels = listOf(
                    Channel(
                        id = "ch_299",
                        number = "75",
                        title = "Discovery Channel HD",
                        category = "Documentários",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/71862.ts",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/discovery-channel-hd.png",
                        description = "O canal pioneiro em exploracao cientifica, misterios do planeta e aventuras humanas.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_discovery_kids",
                        number = "76",
                        title = "Discovery Kids HD",
                        category = "Documentários",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/71871.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/07/Discovery_Kids_logo_%282016%29.svg/320px-Discovery_Kids_logo_%282016%29.svg.png",
                        description = "O canal lider para o publico infantil: Peppa Pig, O Show da Luna e Doki.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_296",
                        number = "77",
                        title = "Discovery Home & Health HD",
                        category = "Documentários",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/71867.ts",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/discovery-hh-hd.png",
                        description = "Estilo de vida, reformas de imoveis, saude, culinaria e maternidade.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_297",
                        number = "78",
                        title = "Discovery Turbo HD",
                        category = "Documentários",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/71883.ts",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/discovery-turbo-hd.png",
                        description = "Carros personalizados, restauracao de classicos e motores de alta performance.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "disc_id",
                        number = "79",
                        title = "Discovery ID (Investigacao) HD",
                        category = "Documentários",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72150.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/cd/Investigation_Discovery_logo_%282020%29.svg/320px-Investigation_Discovery_logo_%282020%29.svg.png",
                        description = "Crimes reais, investigacoes policiais forenses e os misterios mais intrigantes.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "disc_world",
                        number = "80",
                        title = "Discovery World HD",
                        category = "Documentários",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/71887.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a2/Discovery_World_logo.svg/320px-Discovery_World_logo.svg.png",
                        description = "Documentarios deslumbrantes sobre culturas globais, paisagens e a historia da Terra.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "disc_science",
                        number = "81",
                        title = "Discovery Science HD",
                        category = "Documentários",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/71875.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/9/91/Discovery_Science_logo.svg/320px-Discovery_Science_logo.svg.png",
                        description = "Tecnologia de ponta, astrofisica, experimentos ousados e inovacoes do futuro.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "disc_theater",
                        number = "82",
                        title = "Discovery Theater HD",
                        category = "Documentários",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/71879.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/23/Discovery_Theater_logo.png/320px-Discovery_Theater_logo.png",
                        description = "Imagens cinematograficas em alta definicao da natureza e animais selvagens.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "disc_animal",
                        number = "83",
                        title = "Animal Planet HD",
                        category = "Documentários",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/71772.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/20/Animal_Planet_logo_2018.svg/320px-Animal_Planet_logo_2018.svg.png",
                        description = "O reino animal de perto: resgate de fauna, predadores selvagens e vida dos pets.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_history",
                        number = "84",
                        title = "History Channel HD",
                        category = "Documentários",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72146.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f5/History_Logo.svg/320px-History_Logo.svg.png",
                        description = "Trato Feito, Alienigenas do Passado, Forjado no Fogo e grandes acontecimentos historicos.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_history_2",
                        number = "85",
                        title = "History 2 (H2) HD",
                        category = "Documentários",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72106.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6b/History2_logo.svg/320px-History2_logo.svg.png",
                        description = "Aprofundamento historico, grandes guerras, arqueologia e conspiracoes mundiais.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "doc_natgeo",
                        number = "86",
                        title = "National Geographic (Nat Geo) HD",
                        category = "Documentários",
                        streamUrl = "https://cdn-ue1-prod.tsv2.amagi.tv/linear/outsidetv-outsidetvplus-oando/playlist.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6a/National_Geographic_Logo.svg/320px-National_Geographic_Logo.svg.png",
                        description = "Ciencia, exploracao geografica e preservacao do meio ambiente.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "doc_natgeowild",
                        number = "87",
                        title = "Nat Geo Wild HD",
                        category = "Documentários",
                        streamUrl = "https://rbmn-live.akamaized.net/hls/live/590964/BoRB-AT/master_6660.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/36/Nat_Geo_Wild_logo.svg/320px-Nat_Geo_Wild_logo.svg.png",
                        description = "O lado mais selvagem e intocado da natureza nos ecossistemas mais extremos.",
                        quality = "FHD"
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
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/131193.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/1/15/Multishow_logo_2017.svg/320px-Multishow_logo_2017.svg.png",
                        description = "Shows ao vivo, humor com Vai Que Cola, festivais Rock in Rio e Lollapalooza.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "var_gnt",
                        number = "89",
                        title = "GNT HD",
                        category = "Variedades & Estilo",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72102.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/ab/GNT_logo_2020.svg/320px-GNT_logo_2020.svg.png",
                        description = "Gastronomia, moda, decoracao, Papo de Segunda e Saia Justa.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "var_viva",
                        number = "90",
                        title = "Viva HD",
                        category = "Variedades & Estilo",
                        streamUrl = "https://9ada494d.wurl.com/master/f36d25e7e52f1ba8d7e56eb859c636563214f541/TEctYnJfVXBseW5rLU5ld2NvX0hMUw/playlist.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a2/Canal_Viva_logo_2019.svg/320px-Canal_Viva_logo_2019.svg.png",
                        description = "As maiores novelas da teledramaturgia brasileira e programas que marcaram epoca.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "var_canalbrasil",
                        number = "91",
                        title = "Canal Brasil HD",
                        category = "Variedades & Estilo",
                        streamUrl = "http://vaiagora.vip:80/live/5q8VUj/5BeUd8/103518.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b5/Canal_Brasil_logo_2019.svg/320px-Canal_Brasil_logo_2019.svg.png",
                        description = "O melhor da cultura nacional, cinema brasileiro, MPB e festivais artisticos.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "var_bis",
                        number = "92",
                        title = "BIS HD",
                        category = "Variedades & Estilo",
                        streamUrl = "http://64.31.23.207:80/59471019/60849037/518551.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/36/Canal_Bis_logo.svg/320px-Canal_Bis_logo.svg.png",
                        description = "Grandes shows internacionais, documentarios musicais e turnes historicas.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "var_viagem",
                        number = "93",
                        title = "Modo Viagem HD",
                        category = "Variedades & Estilo",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72162.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f6/Modo_Viagem_logo.png/320px-Modo_Viagem_logo.png",
                        description = "Turismo pelo mundo, hoteis deslumbrantes e experiencias de viagem inesqueciveis.",
                        quality = "FHD"
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
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_358",
                        number = "96",
                        title = "Rede Minas",
                        category = "Variedades & Estilo",
                        streamUrl = "https://8hzcavccys.zoeweb.tv/redeminas/ngrp:redeminas_all/chunklist_w1997760151_b2771072.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/rede-minas.png",
                        description = "Cultura mineira, jornalismo regional e producoes independentes.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_345",
                        number = "97",
                        title = "Amazon Sat HD",
                        category = "Variedades & Estilo",
                        streamUrl = "https://amazonsat.brasilstream.com.br/hls/amazonsat/index.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/amazon-sat.png",
                        description = "A emissora da Amazonia: biodiversidade, tradicoes e natureza exuberante.",
                        quality = "HD"
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
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72297.ts",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/rede-tv.png",
                        description = "Entretenimento, talk shows, fofocas e debates ao vivo.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_224",
                        number = "100",
                        title = "PlayTV",
                        category = "Variedades & Estilo",
                        streamUrl = "https://isaocorp.cloudecast.com/playtv/tracks-v1a1/mono.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/playtv.png",
                        description = "Games, e-sports, cultura geek, animes e musica pop.",
                        quality = "HD"
                    )
                )
            ),
            ChannelCategory(
                id = "cat_4k",
                name = "Canais 4K Ultra HD",
                isLocked = false,
                channels = listOf(
                    Channel(
                        id = "ch_4k_sportv",
                        number = "101",
                        title = "SporTV 4K UHD",
                        category = "Canais 4K Ultra HD",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/131188.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/5/52/SporTV_logo_2021.svg/320px-SporTV_logo_2021.svg.png",
                        description = "Transmissao oficial em resolucao 4K Ultra HD (3840x2160) com maximo detalhe esportivo.",
                        quality = "4K"
                    ),
                    Channel(
                        id = "ch_4k_prem",
                        number = "102",
                        title = "Premiere 4K UHD",
                        category = "Canais 4K Ultra HD",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/103481.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d7/Premiere_logo_2021.svg/320px-Premiere_logo_2021.svg.png",
                        description = "O melhor do futebol brasileiro com qualidade de cinema 4K UHD e som imersivo.",
                        quality = "4K"
                    ),
                    Channel(
                        id = "ch_4k_tc",
                        number = "103",
                        title = "Telecine Premium 4K UHD",
                        category = "Canais 4K Ultra HD",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/90237.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/82/Telecine_Premium_logo_2019.svg/320px-Telecine_Premium_logo_2019.svg.png",
                        description = "Grandes lancamentos do cinema mundial na resolucao maxima 4K Ultra HD.",
                        quality = "4K"
                    ),
                    Channel(
                        id = "ch_4k_hbo",
                        number = "104",
                        title = "HBO 4K UHD",
                        category = "Canais 4K Ultra HD",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/90247.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/de/HBO_logo.svg/320px-HBO_logo.svg.png",
                        description = "Series premiadas e filmes HBO gravados em resolucao nativa 4K UHD.",
                        quality = "4K"
                    ),
                    Channel(
                        id = "ch_4k_disc",
                        number = "105",
                        title = "Discovery 4K UHD",
                        category = "Canais 4K Ultra HD",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/103510.ts",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/discovery-channel-hd.png",
                        description = "Documentarios deslumbrantes da natureza, ciencia e vida selvagem em 4K.",
                        quality = "4K"
                    ),
                    Channel(
                        id = "ch_4k_nasa",
                        number = "106",
                        title = "NASA TV 4K UHD",
                        category = "Canais 4K Ultra HD",
                        streamUrl = "https://ntv1.akamaized.net/hls/live/2014075/NASA-NTV1-HLS/master.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e5/NASA_logo.svg/320px-NASA_logo.svg.png",
                        description = "Transmissao oficial ao vivo do espaco sideral da Estacao Espacial Internacional em 4K.",
                        quality = "4K"
                    ),
                    Channel(
                        id = "ch_4k_nature",
                        number = "107",
                        title = "Love Nature 4K UHD",
                        category = "Canais 4K Ultra HD",
                        streamUrl = "https://rbmn-live.akamaized.net/hls/live/590964/BoRB-AT/master_6660.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e2/Love_Nature_logo.png/320px-Love_Nature_logo.png",
                        description = "Paisagens paradisiacas do planeta Terra filmadas em ultra alta definicao 4K.",
                        quality = "4K"
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
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72598.ts",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/globo-sp-globo-sao-paulo.png",
                        description = "Transmissao em codec H.265 de alta eficiencia: imagem ultra nitida consumindo metade da banda.",
                        quality = "H265"
                    ),
                    Channel(
                        id = "ch_h265_sportv",
                        number = "109",
                        title = "SporTV H.265 (HEVC)",
                        category = "Canais H.265 (HEVC)",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72330.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/5/52/SporTV_logo_2021.svg/320px-SporTV_logo_2021.svg.png",
                        description = "O canal lider de esportes otimizado em compressao H.265 sem travamentos.",
                        quality = "H265"
                    ),
                    Channel(
                        id = "ch_h265_tc",
                        number = "110",
                        title = "Telecine H.265 (HEVC)",
                        category = "Canais H.265 (HEVC)",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72363.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/82/Telecine_Premium_logo_2019.svg/320px-Telecine_Premium_logo_2019.svg.png",
                        description = "Filmes em alta taxa de bits codificados em H.265 para TV Boxes modernas.",
                        quality = "H265"
                    ),
                    Channel(
                        id = "ch_h265_hbo",
                        number = "111",
                        title = "HBO H.265 (HEVC)",
                        category = "Canais H.265 (HEVC)",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72119.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/de/HBO_logo.svg/320px-HBO_logo.svg.png",
                        description = "Series e sucessos de Hollywood em transmissao eficiente H.265.",
                        quality = "H265"
                    ),
                    Channel(
                        id = "ch_h265_espn",
                        number = "112",
                        title = "ESPN H.265 (HEVC)",
                        category = "Canais H.265 (HEVC)",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/71912.ts",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/espn-hd.png",
                        description = "Esportes internacionais com codificacao avancada H.265 a 60fps.",
                        quality = "H265"
                    ),
                    Channel(
                        id = "ch_h265_prem",
                        number = "113",
                        title = "Premiere H.265 (HEVC)",
                        category = "Canais H.265 (HEVC)",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72238.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d7/Premiere_logo_2021.svg/320px-Premiere_logo_2021.svg.png",
                        description = "Futebol brasileiro com transmissao rapida e estavel pelo codec H.265.",
                        quality = "H265"
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
                        quality = "H265"
                    )
                )
            ),
            ChannelCategory(
                id = "cat_globo",
                name = "Globo",
                isLocked = false,
                channels = listOf(
                    Channel(
                        id = "ch_158",
                        number = "116",
                        title = "Globo SP - Globo Sao Paulo",
                        category = "Globo",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72598.ts",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/globo-sp-globo-sao-paulo.png",
                        description = "TV Globo Sao Paulo: Jornalismo, novelas, futebol e programacao completa em alta definicao.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_160",
                        number = "117",
                        title = "Globo RJ - TV Globo Rio",
                        category = "Globo",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/131163.ts",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/globo-rj-tv-globo-rio.png",
                        description = "TV Globo Rio de Janeiro: Cobertura jornalistica carioca, novelas e transmissao ao vivo.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_163",
                        number = "118",
                        title = "Globo MG - TV Globo Minas",
                        category = "Globo",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/131159.ts",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/globo-mg-tv-globo-minas.png",
                        description = "Sinal oficial da TV Globo Minas com informacao e o melhor do entretenimento.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_166",
                        number = "119",
                        title = "Globo BA - TV Bahia",
                        category = "Globo",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/306782.ts",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/globo-ba-tv-bahia.jpg",
                        description = "A TV Bahia e a emissora afiliada a Rede Globo com a maior cobertura do estado baiano.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_165",
                        number = "120",
                        title = "Globo DF - TV Globo Brasilia",
                        category = "Globo",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/131164.ts",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/globo-df-tv-globo-brasilia.png",
                        description = "Cobertura politica da capital federal, jornalismo nacional e entretenimento.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_324",
                        number = "121",
                        title = "Globo RBS - Globo Porto Alegre",
                        category = "Globo",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72017.ts",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/globo-rbs-globo-porto-alegre.png",
                        description = "RBS TV Porto Alegre levando o sinal da Globo e tradicoes gauchas para o Rio Grande do Sul.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_167",
                        number = "122",
                        title = "Globo PR - RPC TV Curitiba",
                        category = "Globo",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/131174.ts",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/globo-pr-rpc-tv-curitiba.png",
                        description = "RPC TV Curitiba: jornalismo lider de audiencia no estado do Parana.",
                        quality = "HD"
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
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/77963.ts",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/sbt-sp-sbt-sao-paulo.png",
                        description = "SBT Sao Paulo: A TV que tem torcida com Silvio Santos, jornalismo popular e novelas infantis.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_169",
                        number = "124",
                        title = "SBT RJ - SBT Rio",
                        category = "SBT",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/108681.ts",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/sbt-rj-sbt-rio.png",
                        description = "SBT Rio: Isabele Benito trazendo as principais noticias e prestacao de servico no RJ.",
                        quality = "HD"
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
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/77654.ts",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/rederecord-sp-recordtv-sp.png",
                        description = "RecordTV Sao Paulo: Cidade Alerta, Jornal da Record, novelas biblicas e reality shows.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_177",
                        number = "126",
                        title = "RedeRecord RJ - RecordTV Rio",
                        category = "Record",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72594.ts",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/rederecord-rj-recordtv-rio.png",
                        description = "RecordTV Rio com jornalismo policial de ponta e transmissao ao vivo.",
                        quality = "HD"
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
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/79311.ts",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/band-sp-band-sp.png",
                        description = "Band Sao Paulo: O canal do esporte, Formula 1, Jogo Aberto, Os Donos da Bola e MasterChef.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_175",
                        number = "128",
                        title = "Band RJ - Band Rio",
                        category = "Band",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72597.ts",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/band-rj-band-rio.png",
                        description = "Band Rio trazendo debates do futebol carioca e noticias da regiao metropolitana.",
                        quality = "HD"
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
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/71837.ts",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/cnn-brasil.png",
                        description = "A maior marca de noticias do mundo no Brasil com analise politica e economica 24h.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_213",
                        number = "130",
                        title = "Jovem Pan News",
                        category = "Notícias",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/134023.ts",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/jovem-pan-news.png",
                        description = "Opiniao forte, debates acalorados, Pingos nos Is e transmissao em tempo real.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_214",
                        number = "131",
                        title = "Band News",
                        category = "Notícias",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/71789.ts",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/band-news.png",
                        description = "Em 20 minutos tudo pode mudar: informacao rapida e atualizada no Brasil e no mundo.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_222",
                        number = "132",
                        title = "Record News",
                        category = "Notícias",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72271.ts",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/record-news.png",
                        description = "O principal canal de jornalismo da televisao aberta no Brasil.",
                        quality = "HD"
                    )
                )
            ),
            ChannelCategory(
                id = "cat_f1",
                name = "Fórmula 1 & Velocidade",
                isLocked = false,
                channels = listOf(
                    Channel(
                        id = "ch_f1_pro",
                        number = "133",
                        title = "F1 TV Pro HD - Oficial",
                        category = "Fórmula 1 & Velocidade",
                        streamUrl = "https://cdn.live.br1.jmvstream.com/w/LVW-15748/LVW15748_Yed7yzLuRC/chunklist.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/33/F1.svg/320px-F1.svg.png",
                        description = "Transmissao oficial mundial de todos os treinos livres, classificacoes e Grandes Premios da F1.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_f1_band_gp",
                        number = "134",
                        title = "Band HD - Formula 1 GP Ao Vivo",
                        category = "Fórmula 1 & Velocidade",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/79311.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/33/F1.svg/320px-F1.svg.png",
                        description = "A casa oficial da Formula 1 no Brasil: narracao consagrada e reportagens direto do paddock.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_f1_onboard",
                        number = "135",
                        title = "F1 OnBoard & Pit Lane 24H",
                        category = "Fórmula 1 & Velocidade",
                        streamUrl = "https://cdn.live.br1.jmvstream.com/w/LVW-15748/LVW15748_Yed7yzLuRC/chunklist.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/33/F1.svg/320px-F1.svg.png",
                        description = "Cameras exclusivas a bordo dos cockpits e comunicacao direta de radio da equipe.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_f1_redbull",
                        number = "136",
                        title = "Red Bull Racing F1 TV HD",
                        category = "Fórmula 1 & Velocidade",
                        streamUrl = "https://rbmn-live.akamaized.net/hls/live/590964/BoRB-AT/master_6660.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/en/thumb/4/44/Red_Bull_Racing_logo.svg/320px-Red_Bull_Racing_logo.svg.png",
                        description = "Bastidores da equipe tetracampea mundial Red Bull Racing com Max Verstappen.",
                        quality = "FHD"
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
                        number = "137",
                        title = "Cartoon Network HD",
                        category = "Infantil & Kids",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/71828.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/80/Cartoon_Network_2010_logo.svg/320px-Cartoon_Network_2010_logo.svg.png",
                        description = "O canal dos desenhos animados: Ben 10, Teen Titans Go, Hora de Aventura e Incrivel Mundo de Gumball.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_344",
                        number = "138",
                        title = "Rakuten TV Kids",
                        category = "Infantil & Kids",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/138011.ts",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/rakuten-tv-kids.png",
                        description = "Animacoes divertidas e educativas com programacao segura para criancas.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_207",
                        number = "139",
                        title = "Retro Cartoon Channel",
                        category = "Infantil & Kids",
                        streamUrl = "https://stmv1.video.brstream.com.br/retrotv/retrotv/playlist.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/retro-cartoon-channel.png",
                        description = "Os desenhos animados nostalgicos das decadas de 80, 90 e 2000.",
                        quality = "HD"
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
                        quality = "HD"
                    )
                )
            ),
            ChannelCategory(
                id = "cat_adultos",
                name = "Adultos (+18) & OnlyFans",
                isLocked = true,
                channels = listOf(
                    Channel(
                        id = "ch_onlyfans",
                        number = "142",
                        title = "OnlyFans & Privacy Stars 24H",
                        category = "Adultos (+18) & OnlyFans",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/149548.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/09/OnlyFans_logo.svg/320px-OnlyFans_logo.svg.png",
                        description = "Transmissao continua com as principais criadoras e ensaios do OnlyFans e Privacy (+18).",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_onlyfans_vip",
                        number = "143",
                        title = "OnlyFans VIP & Amador 24H",
                        category = "Adultos (+18) & OnlyFans",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/149544.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/09/OnlyFans_logo.svg/320px-OnlyFans_logo.svg.png",
                        description = "Conteudo exclusivo de criadores independentes e bastidores sem censura (+18).",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_playboy",
                        number = "144",
                        title = "Playboy TV FHD",
                        category = "Adultos (+18) & OnlyFans",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72221.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f6/Playboy_Enterprises_logo.svg/320px-Playboy_Enterprises_logo.svg.png",
                        description = "O canal pioneiro do entretenimento erotico refinado mundial (+18).",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_sexy_hot",
                        number = "145",
                        title = "Sexy Hot FHD",
                        category = "Adultos (+18) & OnlyFans",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/180153.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6e/Sexy_Hot_logo_2018.svg/320px-Sexy_Hot_logo_2018.svg.png",
                        description = "As maiores producoes nacionais e estrelas consagradas do cinema adulto (+18).",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_brasileirinhas",
                        number = "146",
                        title = "A Casa das Brasileirinhas 24H",
                        category = "Adultos (+18) & OnlyFans",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/140487.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6e/Sexy_Hot_logo_2018.svg/320px-Sexy_Hot_logo_2018.svg.png",
                        description = "O reality show mais quente do Brasil com exibicao continua 24 horas por dia (+18).",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_venus",
                        number = "147",
                        title = "Venus HD",
                        category = "Adultos (+18) & OnlyFans",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/140536.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/7/7b/Venus_logo.svg/320px-Venus_logo.svg.png",
                        description = "Sensualidade e producoes eroticas latinas e internacionais (+18).",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_sex_prive",
                        number = "148",
                        title = "Sex Privé HD",
                        category = "Adultos (+18) & OnlyFans",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/140537.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/7/77/Sex_Prive_logo_2018.png/320px-Sex_Prive_logo_2018.png",
                        description = "Conteudo explicito e producoes exclusivas com as maiores estrelas do Brasil (+18).",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_brazzers",
                        number = "149",
                        title = "Brazzers TV HD",
                        category = "Adultos (+18) & OnlyFans",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/166035.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/89/Brazzers_logo.svg/320px-Brazzers_logo.svg.png",
                        description = "A produtora mais famosa do mundo com as atrizes mais requisitadas (+18).",
                        quality = "FHD"
                    )
                )
            )
        )
    }
    private const val SERVER_BASE = "http://vivlar.me:80"
    private const val USERNAME = "497238"
    private const val PASSWORD = "KK8Kdy"

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
                streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/317686.ts",
                logoUrl = "http://vivlar.me/static/logos/canais/jogos.png",
                description = "Partida ao vivo de hoje: [15:00] Ah-Ali x Al-Khaleej",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_317687",
                number = "16",
                title = "[21:30] Flamengo x Estudiantes",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/317687.ts",
                logoUrl = "http://vivlar.me/static/logos/canais/jogos.png",
                description = "Partida ao vivo de hoje: [21:30] Flamengo x Estudiantes",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_317688",
                number = "17",
                title = "Jogos Ao Vivo (Canal 03)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/317688.ts",
                logoUrl = "http://vivlar.me/static/logos/canais/jogos.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_317689",
                number = "18",
                title = "Jogos Ao Vivo (Canal 04)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/317689.ts",
                logoUrl = "http://vivlar.me/static/logos/canais/jogos.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_317690",
                number = "19",
                title = "Jogos Ao Vivo (Canal 05)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/317690.ts",
                logoUrl = "http://vivlar.me/static/logos/canais/jogos.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_317691",
                number = "20",
                title = "Jogos Ao Vivo (Canal 06)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/317691.ts",
                logoUrl = "http://vivlar.me/static/logos/canais/jogos.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_317692",
                number = "21",
                title = "Jogos Ao Vivo (Canal 07)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/317692.ts",
                logoUrl = "http://vivlar.me/static/logos/canais/jogos.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_317693",
                number = "22",
                title = "Jogos Ao Vivo (Canal 08)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/317693.ts",
                logoUrl = "http://vivlar.me/static/logos/canais/jogos.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_317694",
                number = "23",
                title = "Jogos Ao Vivo (Canal 09)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/317694.ts",
                logoUrl = "http://vivlar.me/static/logos/canais/jogos.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_317695",
                number = "24",
                title = "Jogos Ao Vivo (Canal 10)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/317695.ts",
                logoUrl = "http://vivlar.me/static/logos/canais/jogos.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_317696",
                number = "25",
                title = "Jogos Ao Vivo (Canal 11)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/317696.ts",
                logoUrl = "http://vivlar.me/static/logos/canais/jogos.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_318080",
                number = "26",
                title = "Jogos Ao Vivo (Canal 12)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/318080.ts",
                logoUrl = "http://vivlar.me/static/logos/canais/jogos.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_318081",
                number = "27",
                title = "Jogos Ao Vivo (Canal 13)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/318081.ts",
                logoUrl = "http://vivlar.me/static/logos/canais/jogos.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_318082",
                number = "28",
                title = "Jogos Ao Vivo (Canal 14)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/318082.ts",
                logoUrl = "http://vivlar.me/static/logos/canais/jogos.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_318083",
                number = "29",
                title = "Jogos Ao Vivo (Canal 15)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/318083.ts",
                logoUrl = "http://vivlar.me/static/logos/canais/jogos.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_318084",
                number = "30",
                title = "Jogos Ao Vivo (Canal 16)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/318084.ts",
                logoUrl = "http://vivlar.me/static/logos/canais/jogos.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_318085",
                number = "31",
                title = "Jogos Ao Vivo (Canal 17)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/318085.ts",
                logoUrl = "http://vivlar.me/static/logos/canais/jogos.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_318086",
                number = "32",
                title = "Jogos Ao Vivo (Canal 18)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/318086.ts",
                logoUrl = "http://vivlar.me/static/logos/canais/jogos.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_318606",
                number = "33",
                title = "Jogos Ao Vivo (Canal 19)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/318606.ts",
                logoUrl = "http://vivlar.me/static/logos/canais/jogos.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_318607",
                number = "34",
                title = "Jogos Ao Vivo (Canal 20)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/318607.ts",
                logoUrl = "http://vivlar.me/static/logos/canais/jogos.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_318608",
                number = "35",
                title = "Jogos Ao Vivo (Canal 21)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/318608.ts",
                logoUrl = "http://vivlar.me/static/logos/canais/jogos.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_318609",
                number = "36",
                title = "Jogos Ao Vivo (Canal 22)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/318609.ts",
                logoUrl = "http://vivlar.me/static/logos/canais/jogos.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_318610",
                number = "37",
                title = "Jogos Ao Vivo (Canal 23)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/318610.ts",
                logoUrl = "http://vivlar.me/static/logos/canais/jogos.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_318611",
                number = "38",
                title = "Jogos Ao Vivo (Canal 24)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/318611.ts",
                logoUrl = "http://vivlar.me/static/logos/canais/jogos.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_318612",
                number = "39",
                title = "Jogos Ao Vivo (Canal 25)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/318612.ts",
                logoUrl = "http://vivlar.me/static/logos/canais/jogos.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_318613",
                number = "40",
                title = "Jogos Ao Vivo (Canal 26)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/318613.ts",
                logoUrl = "http://vivlar.me/static/logos/canais/jogos.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_361372",
                number = "41",
                title = "Jogos Ao Vivo (Canal 27)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/361372.ts",
                logoUrl = "http://vivlar.me/static/logos/canais/jogos.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_361373",
                number = "42",
                title = "Jogos Ao Vivo (Canal 28)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/361373.ts",
                logoUrl = "http://vivlar.me/static/logos/canais/jogos.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_361374",
                number = "43",
                title = "Jogos Ao Vivo (Canal 29)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/361374.ts",
                logoUrl = "http://vivlar.me/static/logos/canais/jogos.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_362685",
                number = "44",
                title = "Jogos Ao Vivo (Canal 30)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/362685.ts",
                logoUrl = "http://vivlar.me/static/logos/canais/jogos.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_362686",
                number = "45",
                title = "Jogos Ao Vivo (Canal 31)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/362686.ts",
                logoUrl = "http://vivlar.me/static/logos/canais/jogos.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_362687",
                number = "46",
                title = "Jogos Ao Vivo (Canal 32)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/362687.ts",
                logoUrl = "http://vivlar.me/static/logos/canais/jogos.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_362688",
                number = "47",
                title = "Jogos Ao Vivo (Canal 33)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/362688.ts",
                logoUrl = "http://vivlar.me/static/logos/canais/jogos.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_362826",
                number = "48",
                title = "Jogos Ao Vivo (Canal 34)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/362826.ts",
                logoUrl = "http://vivlar.me/static/logos/canais/jogos.png",
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
                val icon = obj.optString("stream_icon", "http://vivlar.me/static/logos/canais/jogos.png").ifEmpty {
                    "http://vivlar.me/static/logos/canais/jogos.png"
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
}
