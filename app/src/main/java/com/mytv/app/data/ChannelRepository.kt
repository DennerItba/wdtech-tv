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
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/1832882.m3u8",
                        logoUrl = "http://www.fontedecanais.dev/logos/canais/cinesky.png",
                        description = "Cine Sky Oficial: Grandes lancamentos do cinema mundial 24h sem intervalos comerciais.",
                        quality = "FHD",
                        backupUrls = listOf("http://64.31.23.207:80/live/59471019/60849037/1832882.ts")
                    ),
                    Channel(
                        id = "cinesky_02",
                        number = "02",
                        title = "Cine SKY 02 HD",
                        category = "Cine Sky",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/1882618.m3u8",
                        logoUrl = "http://www.fontedecanais.org/logos/canais/cinesky.png",
                        description = "Os melhores sucessos de bilheteria e estreias exclusivas do cinema direto na sua TV.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "cinesky_03",
                        number = "03",
                        title = "Cine SKY 03 HD",
                        category = "Cine Sky",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/1882619.m3u8",
                        logoUrl = "http://www.fontedecanais.org/logos/canais/cinesky.png",
                        description = "Filmes consagrados, acao, suspense e producoes premiadas de Hollywood.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "cinesky_05",
                        number = "04",
                        title = "Cine SKY 05 HD",
                        category = "Cine Sky",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5378594.m3u8",
                        logoUrl = "http://www.fontedecanais.org/logos/canais/cinesky.png",
                        description = "Cinema em alta definicao com lancamentos imperdiveis e classicos mundiais.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "cinesky_acao",
                        number = "05",
                        title = "Cine SKY Acao HD",
                        category = "Cine Sky",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5378580.m3u8",
                        logoUrl = "http://www.fontedecanais.dev/logos/canais/cinesky.png",
                        description = "Tiros, explosoes e adrenalina pura com os maiores astros da acao.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "cinesky_fam",
                        number = "06",
                        title = "Cine SKY Familia HD",
                        category = "Cine Sky",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5378586.m3u8",
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
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5726032.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/pt/thumb/d/d7/Logotipo_de_A_Fazenda.png/320px-Logotipo_de_A_Fazenda.png",
                        description = "Camera principal ao vivo 24 horas por dia dentro da sede de A Fazenda com todos os peoes.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_fazenda_2",
                        number = "08",
                        title = "A Fazenda 24H - Camera 2 (Baia & Animais)",
                        category = "A Fazenda 24H (7 Câmeras)",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5726033.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/pt/thumb/d/d7/Logotipo_de_A_Fazenda.png/320px-Logotipo_de_A_Fazenda.png",
                        description = "Camera exclusiva na baia dos animais, rotina rural e conversas reservadas.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_fazenda_3",
                        number = "09",
                        title = "A Fazenda 24H - Camera 3 (Oficina & Varanda)",
                        category = "A Fazenda 24H (7 Câmeras)",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5726034.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/pt/thumb/d/d7/Logotipo_de_A_Fazenda.png/320px-Logotipo_de_A_Fazenda.png",
                        description = "Acompanhe as estrategias e reunioes secretas na area da varanda e oficina.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_fazenda_4",
                        number = "10",
                        title = "A Fazenda 24H - Camera 4 (Cozinha & Sala)",
                        category = "A Fazenda 24H (7 Câmeras)",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5726035.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/pt/thumb/d/d7/Logotipo_de_A_Fazenda.png/320px-Logotipo_de_A_Fazenda.png",
                        description = "Toda a movimentacao das refeicoes, discussoes na cozinha e momentos na sala de estar.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_fazenda_5",
                        number = "11",
                        title = "A Fazenda 24H - Camera 5 (Piscina & Academia)",
                        category = "A Fazenda 24H (7 Câmeras)",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5726036.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/pt/thumb/d/d7/Logotipo_de_A_Fazenda.png/320px-Logotipo_de_A_Fazenda.png",
                        description = "Area externa, treinos na academia, banho de sol na piscina e conversas quentes.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_fazenda_6",
                        number = "12",
                        title = "A Fazenda 24H - Camera 6 (Quartos & Espelho)",
                        category = "A Fazenda 24H (7 Câmeras)",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5726037.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/pt/thumb/d/d7/Logotipo_de_A_Fazenda.png/320px-Logotipo_de_A_Fazenda.png",
                        description = "Transmissao ao vivo dos quartos, pre-festas, fofocas e descanso dos peoes.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_fazenda_7",
                        number = "13",
                        title = "A Fazenda 24H - Camera 7 (Mosaico Multicameras)",
                        category = "A Fazenda 24H (7 Câmeras)",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5726038.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/b/b4/Logo_A_Fazenda_-_Atual.png",
                        description = "Visao panoramica mosaico transmitindo todos os cantos da fazenda simultaneamente.",
                        quality = "FHD"
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
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5727538.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/1/14/Logo_Chaves.png/320px-Logo_Chaves.png",
                        description = "Transmissao oficial 24h sem intervalos com todos os episodios classicos da vila do Chaves.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_24h_chapolin",
                        number = "2402",
                        title = "24H Chapolin Colorado",
                        category = "Canais 24 Horas",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5727539.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/ca/Chapolin_Colorado_logo.png/320px-Chapolin_Colorado_logo.png",
                        description = "Nao contavam com a minha astucia! O heroi mais amado da TV 24 horas no ar.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_24h_simpsons",
                        number = "2403",
                        title = "24H Os Simpsons",
                        category = "Canais 24 Horas",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5727547.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/02/The_Simpsons_Logo.svg/320px-The_Simpsons_Logo.svg.png",
                        description = "Todas as temporadas de Homer, Marge, Bart, Lisa e Maggie em Springfield 24h por dia.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_24h_chris",
                        number = "2404",
                        title = "24H Todo Mundo Odeia o Chris",
                        category = "Canais 24 Horas",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5727565.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2f/Everybody_Hates_Chris_title.png/320px-Everybody_Hates_Chris_title.png",
                        description = "Julius, Rochelle, Tonya, Drew e Greg nas aventuras mais engracadas de Nova York.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_24h_friends",
                        number = "2405",
                        title = "24H Friends",
                        category = "Canais 24 Horas",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5727545.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/bc/Friends_logo.svg/320px-Friends_logo.svg.png",
                        description = "Rachel, Monica, Phoebe, Joey, Chandler e Ross no Central Perk 24 horas por dia.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_24h_dois_homens",
                        number = "2406",
                        title = "24H Dois Homens e Meio",
                        category = "Canais 24 Horas",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5727542.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/5/52/Two_and_a_Half_Men_logo.svg/320px-Two_and_a_Half_Men_logo.svg.png",
                        description = "Charlie Harper, Alan e Jake na comedia de maior sucesso da TV 24h sem parar.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_24h_bob_esponja",
                        number = "2407",
                        title = "24H Bob Esponja",
                        category = "Canais 24 Horas",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5727199.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/87/SpongeBob_SquarePants_logo.svg/320px-SpongeBob_SquarePants_logo.svg.png",
                        description = "Na Fenda do Biquini com Bob Esponja, Patrick Estrela, Lula Molusco e Sr. Sirigueijo 24h.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_24h_pica_pau",
                        number = "2408",
                        title = "24H Pica Pau",
                        category = "Canais 24 Horas",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5727301.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e0/Woody_Woodpecker_logo.png/320px-Woody_Woodpecker_logo.png",
                        description = "O passaro mais biruta dos desenhos animados em suas classicas trapalhadas 24 horas.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_24h_dragon_ball",
                        number = "2409",
                        title = "24H Dragon Ball Z",
                        category = "Canais 24 Horas",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5727318.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/22/Dragon_Ball_Z_logo.svg/320px-Dragon_Ball_Z_logo.svg.png",
                        description = "Goku, Vegeta, Gohan e os Guerreiros Z nas batalhas mais epicas do anime 24h por dia.",
                        quality = "HD"
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
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/3460209.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d7/Premiere_logo.svg/320px-Premiere_logo.svg.png",
                        description = "O melhor do futebol brasileiro 24 horas por dia com jogos ao vivo e programas exclusivos.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "prem_2",
                        number = "50",
                        title = "Premiere 2 FHD",
                        category = "⚽ Premiere FC (Todos os Jogos)",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/3460214.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d7/Premiere_logo.svg/320px-Premiere_logo.svg.png",
                        description = "Transmissões ao vivo das Séries A e B do Brasileirão e campeonatos regionais.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "prem_3",
                        number = "51",
                        title = "Premiere 3 FHD",
                        category = "⚽ Premiere FC (Todos os Jogos)",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/3460214.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d7/Premiere_logo.svg/320px-Premiere_logo.svg.png",
                        description = "Partidas simultâneas ao vivo e transmissão completa dos principais clubes do Brasil.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "prem_4",
                        number = "52",
                        title = "Premiere 4 FHD",
                        category = "⚽ Premiere FC (Todos os Jogos)",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/3460218.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d7/Premiere_logo.svg/320px-Premiere_logo.svg.png",
                        description = "Futebol ao vivo em alta definição direto dos estádios de todo o Brasil.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "prem_5",
                        number = "53",
                        title = "Premiere 5 FHD",
                        category = "⚽ Premiere FC (Todos os Jogos)",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/3460221.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d7/Premiere_logo.svg/320px-Premiere_logo.svg.png",
                        description = "Canal de transmissão direta de partidas simultâneas dos campeonatos nacionais.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "prem_6",
                        number = "54",
                        title = "Premiere 6 FHD",
                        category = "⚽ Premiere FC (Todos os Jogos)",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/3460224.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d7/Premiere_logo.svg/320px-Premiere_logo.svg.png",
                        description = "Cobertura completa dos jogos da Série B e confrontos regionais.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "prem_7",
                        number = "55",
                        title = "Premiere 7 FHD",
                        category = "⚽ Premiere FC (Todos os Jogos)",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/3460227.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d7/Premiere_logo.svg/320px-Premiere_logo.svg.png",
                        description = "Canal pay-per-view exclusivo para transmissões de mata-mata e decisões.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "prem_8",
                        number = "56",
                        title = "Premiere 8 FHD",
                        category = "⚽ Premiere FC (Todos os Jogos)",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/3460230.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d7/Premiere_logo.svg/320px-Premiere_logo.svg.png",
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
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/3460158.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/320px-Soccerball.svg.png",
                        description = "Transmissões ao vivo dos Campeonatos Estaduais, divisões de acesso e copas regionais.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "sportynet_2",
                        number = "58",
                        title = "SportyNet 2 FHD",
                        category = "🏆 Campeonatos & Estaduais",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/3460158.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/320px-Soccerball.svg.png",
                        description = "Canal complementar com múltiplos jogos ao vivo simultâneos dos campeonatos estaduais.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "dazn_1",
                        number = "59",
                        title = "DAZN 1 FHD",
                        category = "🏆 Campeonatos & Estaduais",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/caze-tv-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.pngdazn.png",
                        description = "O serviço global de esportes: Série C, futebol europeu, boxe e torneios mundiais.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "fcf_tv_1",
                        number = "60",
                        title = "FCF TV 1 FHD",
                        category = "🏆 Campeonatos & Estaduais",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/297545.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/320px-Soccerball.svg.png",
                        description = "Transmissão oficial das federações estaduais com jogos ao vivo e copas do Nordeste.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "xsports_fhd",
                        number = "61",
                        title = "X Sports FHD",
                        category = "🏆 Campeonatos & Estaduais",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/297545.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/320px-Soccerball.svg.png",
                        description = "Esportes alternativos, campeonatos internacionais e partidas ao vivo.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "bandsports_campeonatos",
                        number = "62",
                        title = "BandSports FHD",
                        category = "🏆 Campeonatos & Estaduais",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/3460152.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.pngbandsports.png",
                        description = "Futebol internacional, campeonatos de base, tênis, automobilismo e esportes olímpicos.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "combate_campeonatos",
                        number = "63",
                        title = "Combate FHD",
                        category = "🏆 Campeonatos & Estaduais",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/297542.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.pngcombate.png",
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
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/3460200.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.pngsportv.png",
                        description = "O canal campeão: cobertura ao vivo dos maiores campeonatos esportivos do Brasil e do mundo.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "sportv_2",
                        number = "65",
                        title = "SporTV 2 FHD",
                        category = "📺 SporTV & Canais Esportivos",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/3460203.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.pngsportv2.png",
                        description = "Vôlei, basquete, tênis, automobilismo e futebol ao vivo todos os dias.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "sportv_3",
                        number = "66",
                        title = "SporTV 3 FHD",
                        category = "📺 SporTV & Canais Esportivos",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/3460206.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.pngsportv3.png",
                        description = "Esportes olímpicos, atletismo, natação e transmissão simultânea de grandes torneios.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "sportv_4",
                        number = "67",
                        title = "SporTV 4 FHD",
                        category = "📺 SporTV & Canais Esportivos",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/caze-tv-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.pngsportv2.png",
                        description = "Canal de apoio para eventos simultâneos e rodadas decisivas em alta resolução.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "bandsports_fhd",
                        number = "68",
                        title = "BandSports FHD",
                        category = "📺 SporTV & Canais Esportivos",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/3460152.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.pngbandsports.png",
                        description = "Futebol internacional, treinos de Fórmula 1, lutas e esportes radicais ao vivo.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "combate_fhd",
                        number = "69",
                        title = "Combate FHD",
                        category = "📺 SporTV & Canais Esportivos",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/297542.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.pngcombate.png",
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
                        streamUrl = "https://cdn1.uniredes.com.br/hls/espn-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.pngespn.png",
                        description = "O lider mundial em esportes: Premier League, LaLiga, Serie A italiana e NFL.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "espn_2",
                        number = "71",
                        title = "ESPN 2 FHD",
                        category = "🌐 Rede ESPN (Champions & Ligas)",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/espn-2-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.pngespn2.png",
                        description = "Futebol europeu, NBA, beisebol da MLB, college football e programas esportivos de debate.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "espn_3",
                        number = "72",
                        title = "ESPN 3 FHD",
                        category = "🌐 Rede ESPN (Champions & Ligas)",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/espn-3-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.pngespn.png",
                        description = "Tenis com Grand Slams ao vivo, ciclismo, esportes a motor e jogos internacionais.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "espn_4",
                        number = "73",
                        title = "ESPN 4 FHD",
                        category = "🌐 Rede ESPN (Champions & Ligas)",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/3460182.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.pngfoxsports1.png",
                        description = "A casa da Copa Libertadores da América, Copa Sul-Americana e futebol sul-americano.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "espn_5",
                        number = "74",
                        title = "ESPN 5 FHD",
                        category = "🌐 Rede ESPN (Champions & Ligas)",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/espn-5-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.pngfoxsports2.png",
                        description = "Cobertura multiplataforma e grandes eventos de esportes de combate e automobilismo.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "espn_6",
                        number = "75",
                        title = "ESPN 6 FHD",
                        category = "🌐 Rede ESPN (Champions & Ligas)",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/espn-6-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.pngespnextra.png",
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
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5378595.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/82/Telecine_Premium_logo_2019.svg/320px-Telecine_Premium_logo_2019.svg.png",
                        description = "Grandes sucessos de bilheteria e estreias exclusivas do cinema mundial em primeira mao.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "tc_action",
                        number = "43",
                        title = "Telecine Action HD",
                        category = "Rede Telecine",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5378581.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/ca/Telecine_Action_logo_2019.svg/320px-Telecine_Action_logo_2019.svg.png",
                        description = "Muita adrenalina, velocidade, acao e perseguicoes sem limite.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "tc_touch",
                        number = "44",
                        title = "Telecine Touch HD",
                        category = "Rede Telecine",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5378597.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/eb/Telecine_Touch_logo_2019.svg/320px-Telecine_Touch_logo_2019.svg.png",
                        description = "Emocao e historias marcantes: os maiores dramas e romances de todos os tempos.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "tc_pipoca",
                        number = "45",
                        title = "Telecine Pipoca HD",
                        category = "Rede Telecine",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5378591.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a2/Telecine_Pipoca_logo_2019.svg/320px-Telecine_Pipoca_logo_2019.svg.png",
                        description = "Os filmes mais divertidos do cinema nacional e internacional 100% dublados.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "tc_fun",
                        number = "46",
                        title = "Telecine Fun HD",
                        category = "Rede Telecine",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5378586.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/8d/Telecine_Fun_logo_2019.svg/320px-Telecine_Fun_logo_2019.svg.png",
                        description = "Comedias e animacoes para garantir boas risadas com a familia reunida.",
                        quality = "FHD",
                        backupUrls = listOf(
                            "http://64.31.23.207:80/live/59471019/60849037/5676605.m3u8",
                            "http://64.31.23.207:80/live/59471019/60849037/5378587.m3u8"
                        )
                    ),
                    Channel(
                        id = "tc_cult",
                        number = "47",
                        title = "Telecine Cult HD",
                        category = "Rede Telecine",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5378583.m3u8",
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
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/514108.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/de/HBO_logo.svg/320px-HBO_logo.svg.png",
                        description = "Series consagradas, grandes documentarios e as maiores producoes mundiais.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "hbo_2",
                        number = "49",
                        title = "HBO 2 HD",
                        category = "Rede HBO",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/514111.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/4/4b/HBO2_logo_2014.svg/320px-HBO2_logo_2014.svg.png",
                        description = "Reprises dos melhores filmes e series em horarios alternativos dublados e legendados.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "hbo_plus",
                        number = "50",
                        title = "HBO Plus HD",
                        category = "Rede HBO",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/514119.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/be/HBO_Plus_logo_2019.svg/320px-HBO_Plus_logo_2019.svg.png",
                        description = "Conteudo jovem, comedia, acao e musica da franquia HBO.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "hbo_family",
                        number = "51",
                        title = "HBO Family HD",
                        category = "Rede HBO",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/514113.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/7/70/HBO_Family_2014.svg/320px-HBO_Family_2014.svg.png",
                        description = "Programacao voltada para criancas e jovens com animacoes e filmes familiares.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "hbo_sig",
                        number = "52",
                        title = "HBO Signature HD",
                        category = "Rede HBO",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/514125.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/9/91/HBO_Signature_logo_2014.svg/320px-HBO_Signature_logo_2014.svg.png",
                        description = "O acervo mais exclusivo e refinado das producoes originais e documentarios HBO.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "hbo_mundi",
                        number = "53",
                        title = "HBO Mundi HD",
                        category = "Rede HBO",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/514123.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/de/HBO_logo.svg/320px-HBO_logo.svg.png",
                        description = "Filmes internacionais e producoes independentes aclamadas pela critica.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "hbo_pop",
                        number = "54",
                        title = "HBO Pop HD",
                        category = "Rede HBO",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/514117.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/de/HBO_logo.svg/320px-HBO_logo.svg.png",
                        description = "Filmes leves, comedias romanticas e grandes sucessos da cultura pop.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "hbo_xtreme",
                        number = "55",
                        title = "HBO Xtreme HD",
                        category = "Rede HBO",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/514129.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/de/HBO_logo.svg/320px-HBO_logo.svg.png",
                        description = "Acao frenetica, suspense e terror para quem gosta de fortes emocoes.",
                        quality = "FHD"
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
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_discovery_kids",
                        number = "57",
                        title = "Discovery Kids HD",
                        category = "Rede Discovery",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/297945.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/07/Discovery_Kids_logo_%282016%29.svg/320px-Discovery_Kids_logo_%282016%29.svg.png",
                        description = "O canal lider para o publico infantil: Peppa Pig, O Show da Luna e Doki.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_discovery_hh",
                        number = "58",
                        title = "Discovery Home & Health HD",
                        category = "Rede Discovery",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/discovery-hh/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/discovery-hh-hd.png",
                        description = "Estilo de vida, reformas de imoveis, saude, culinaria e maternidade.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_discovery_turbo",
                        number = "59",
                        title = "Discovery Turbo HD",
                        category = "Rede Discovery",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/discovery-turbo-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/discovery-turbo-hd.png",
                        description = "Carros personalizados, restauracao de classicos e motores de alta performance.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "disc_id",
                        number = "60",
                        title = "Discovery ID (Investigacao) HD",
                        category = "Rede Discovery",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/297948.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/cd/Investigation_Discovery_logo_%282020%29.svg/320px-Investigation_Discovery_logo_%282020%29.svg.png",
                        description = "Crimes reais, investigacoes policiais forenses e os misterios mais intrigantes.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "disc_world",
                        number = "61",
                        title = "Discovery World HD",
                        category = "Rede Discovery",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/297957.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a2/Discovery_World_logo.svg/320px-Discovery_World_logo.svg.png",
                        description = "Documentarios deslumbrantes sobre culturas globais, paisagens e a historia da Terra.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "disc_science",
                        number = "62",
                        title = "Discovery Science HD",
                        category = "Rede Discovery",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/297951.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/9/91/Discovery_Science_logo.svg/320px-Discovery_Science_logo.svg.png",
                        description = "Tecnologia de ponta, astrofisica, experimentos ousados e inovacoes do futuro.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "disc_theater",
                        number = "63",
                        title = "Discovery Theater HD",
                        category = "Rede Discovery",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/297954.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/23/Discovery_Theater_logo.png/320px-Discovery_Theater_logo.png",
                        description = "Imagens cinematograficas em alta definicao da natureza e animais selvagens.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "disc_animal",
                        number = "64",
                        title = "Animal Planet HD",
                        category = "Rede Discovery",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/296619.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/20/Animal_Planet_logo_2018.svg/320px-Animal_Planet_logo_2018.svg.png",
                        description = "O reino animal de perto: resgate de fauna, predadores selvagens e vida dos pets.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "disc_tlc",
                        number = "65",
                        title = "TLC HD",
                        category = "Rede Discovery",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/tlc-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/tlc-hd.png",
                        description = "Historias reais e inspiradoras: Muquiranas, Quilos Mortais, 90 Dias para Casar e Cake Boss.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "disc_hgtv",
                        number = "66",
                        title = "HGTV HD",
                        category = "Rede Discovery",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/297940.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/hgtv.png",
                        description = "O canal mundial de reformas de casas, decoracao de interiores e compra e venda de imoveis.",
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
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5726976.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/60/Sony_Channel_logo_2019.svg/320px-Sony_Channel_logo_2019.svg.png",
                        description = "O lendario Canal Sony: Grey's Anatomy, The Good Doctor e as series medicas e policiais mais amadas.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_sony_movies",
                        number = "57",
                        title = "Sony Movies HD",
                        category = "Filmes & Séries",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5726997.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/7/77/Sony_Movies_logo_2022.svg/320px-Sony_Movies_logo_2022.svg.png",
                        description = "O acervo completo dos lendarios estudios da Columbia Pictures e Sony Pictures.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "warner_ch",
                        number = "58",
                        title = "Warner Channel HD",
                        category = "Filmes & Séries",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5726994.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/7/72/Warner_Channel_2019_logo.svg/320px-Warner_Channel_2019_logo.svg.png",
                        description = "Universo DC, The Big Bang Theory, Friends e os maiores sucessos da Warner Bros.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "megapix_ch",
                        number = "59",
                        title = "Megapix HD",
                        category = "Filmes & Séries",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5726975.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f3/Megapix_logo_2020.svg/320px-Megapix_logo_2020.svg.png",
                        description = "O canal dos grandes campeoes de bilheteria dublados com sessao pipoca todo dia.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_universal_tv",
                        number = "60",
                        title = "Universal TV HD",
                        category = "Filmes & Séries",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5726990.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e0/Universal_TV_logo_2018.svg/320px-Universal_TV_logo_2018.svg.png",
                        description = "Franquia Chicago (Fire, P.D., Med), Law & Order e grandes investigacoes.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_studio_universal",
                        number = "61",
                        title = "Studio Universal HD",
                        category = "Filmes & Séries",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5726980.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/1/1a/Studio_Universal_logo.svg/320px-Studio_Universal_logo.svg.png",
                        description = "Cinema hollywoodiano e classicos dos estúdios Universal Pictures.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_paramount",
                        number = "62",
                        title = "Paramount Network HD",
                        category = "Filmes & Séries",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/3460171.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f2/Paramount_Network_2018.svg/320px-Paramount_Network_2018.svg.png",
                        description = "Yellowstone, Tulsa King e superproducoes consagradas da Paramount Pictures.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_cinemax",
                        number = "63",
                        title = "Cinemax HD",
                        category = "Filmes & Séries",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5726973.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/1/14/Cinemax_logo_2011.svg/320px-Cinemax_logo_2011.svg.png",
                        description = "Filmes de sucesso, acao e suspense com a chancela do grupo HBO.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_tnt",
                        number = "64",
                        title = "TNT HD",
                        category = "Filmes & Séries",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/tnt-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b5/TNT_Logo_2016.svg/320px-TNT_Logo_2016.svg.png",
                        description = "Grandes premiacoes como o Oscar, superproducoes cinematograficas e esportes.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_space",
                        number = "65",
                        title = "Space HD",
                        category = "Filmes & Séries",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/space-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/36/Space_logo_2017.svg/320px-Space_logo_2017.svg.png",
                        description = "Especializado em acao, ficcao cientifica, suspense e filmes de luta.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_axn",
                        number = "66",
                        title = "AXN HD",
                        category = "Filmes & Séries",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5726968.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b5/AXN_logo_2015.svg/320px-AXN_logo_2015.svg.png",
                        description = "As maiores series policiais e de investigacao forense do mundo: NCIS e Criminal Minds.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_284",
                        number = "67",
                        title = "TNT Novelas HD",
                        category = "Filmes & Séries",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/tnt-novelas-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
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
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/3460200.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/hallo-series.png",
                        description = "Maratonas das melhores series de comedia e drama da televisao.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_331",
                        number = "72",
                        title = "RunTime - Cinema",
                        category = "Filmes & Séries",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5378617.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/runtime-cinema.png",
                        description = "Filmes aclamados com exibicao continua e alta definicao.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_332",
                        number = "73",
                        title = "RunTime - Terror",
                        category = "Filmes & Séries",
                        streamUrl = "https://stream.ads.ottera.tv/playlist.m3u8?network_id=4865",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/runtime-terror.png",
                        description = "O melhor do horror psicologico, sobrenatural e suspense horripilante.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_273",
                        number = "74",
                        title = "RunTime - Acao",
                        category = "Filmes & Séries",
                        streamUrl = "https://stream.ads.ottera.tv/playlist.m3u8?network_id=2552",
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
                        id = "ch_history",
                        number = "75",
                        title = "History Channel HD",
                        category = "Documentários",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/297936.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f5/History_Logo.svg/320px-History_Logo.svg.png",
                        description = "Trato Feito, Alienigenas do Passado, Forjado no Fogo e grandes acontecimentos historicos.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_history_2",
                        number = "76",
                        title = "History 2 (H2) HD",
                        category = "Documentários",
                        streamUrl = "http://vivlar.me:80/live/497238/KK8Kdy/72147.ts",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6b/History2_logo.svg/320px-History2_logo.svg.png",
                        description = "Aprofundamento historico, grandes guerras, arqueologia e conspiracoes mundiais.",
                        quality = "FHD"
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
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5378614.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/curta.png",
                        description = "Cinema brasileiro, artes plasticas, musica, filosofia e literatura.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "doc_arte1",
                        number = "80",
                        title = "Arte 1 HD",
                        category = "Documentários",
                        streamUrl = "http://45.162.64.114/ARTE1/index.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/arte-1.png",
                        description = "Danca, musica classica, teatro, fotografia e arquitetura.",
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
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5726243.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/1/15/Multishow_logo_2017.svg/320px-Multishow_logo_2017.svg.png",
                        description = "Shows ao vivo, humor com Vai Que Cola, festivais Rock in Rio e Lollapalooza.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "var_gnt",
                        number = "89",
                        title = "GNT HD",
                        category = "Variedades & Estilo",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5726128.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/ab/GNT_logo_2020.svg/320px-GNT_logo_2020.svg.png",
                        description = "Gastronomia, moda, decoracao, Papo de Segunda e Saia Justa.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "var_viva",
                        number = "90",
                        title = "Viva HD",
                        category = "Variedades & Estilo",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5726253.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a2/Canal_Viva_logo_2019.svg/320px-Canal_Viva_logo_2019.svg.png",
                        description = "As maiores novelas da teledramaturgia brasileira e programas que marcaram epoca no canal Viva.",
                        quality = "FHD",
                        backupUrls = listOf(
                            "http://64.31.23.207:80/live/59471019/60849037/5676610.m3u8",
                            "http://64.31.23.207:80/live/59471019/60849037/518596.m3u8"
                        )
                    ),
                    Channel(
                        id = "var_canalbrasil",
                        number = "91",
                        title = "Canal Brasil HD",
                        category = "Variedades & Estilo",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5726970.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b5/Canal_Brasil_logo_2019.svg/320px-Canal_Brasil_logo_2019.svg.png",
                        description = "O melhor da cultura nacional, cinema brasileiro, MPB e festivais artisticos.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "var_bis",
                        number = "92",
                        title = "BIS HD",
                        category = "Variedades & Estilo",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5726214.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/36/Canal_Bis_logo.svg/320px-Canal_Bis_logo.svg.png",
                        description = "Grandes shows internacionais, documentarios musicais e turnes historicas.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "var_viagem",
                        number = "93",
                        title = "Modo Viagem HD",
                        category = "Variedades & Estilo",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5726237.m3u8",
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
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5298055.m3u8",
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
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5676601.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/5/52/SporTV_logo_2021.svg/320px-SporTV_logo_2021.svg.png",
                        description = "Transmissao oficial em resolucao 4K Ultra HD (3840x2160) com maximo detalhe esportivo.",
                        quality = "4K"
                    ),
                    Channel(
                        id = "ch_4k_prem",
                        number = "102",
                        title = "Premiere 4K UHD",
                        category = "Canais 4K Ultra HD",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5676611.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d7/Premiere_logo_2021.svg/320px-Premiere_logo_2021.svg.png",
                        description = "O melhor do futebol brasileiro com qualidade de cinema 4K UHD e som imersivo.",
                        quality = "4K"
                    ),
                    Channel(
                        id = "ch_4k_tc",
                        number = "103",
                        title = "Telecine Premium 4K UHD",
                        category = "Canais 4K Ultra HD",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5676603.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/82/Telecine_Premium_logo_2019.svg/320px-Telecine_Premium_logo_2019.svg.png",
                        description = "Grandes lancamentos do cinema mundial na resolucao maxima 4K Ultra HD.",
                        quality = "4K"
                    ),
                    Channel(
                        id = "ch_4k_hbo",
                        number = "104",
                        title = "HBO 4K UHD",
                        category = "Canais 4K Ultra HD",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/514107.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/de/HBO_logo.svg/320px-HBO_logo.svg.png",
                        description = "Series premiadas e filmes HBO gravados em resolucao nativa 4K UHD.",
                        quality = "4K"
                    ),
                    Channel(
                        id = "ch_4k_disc",
                        number = "105",
                        title = "Discovery 4K UHD",
                        category = "Canais 4K Ultra HD",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5676591.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/discovery-channel-hd.png",
                        description = "Documentarios deslumbrantes da natureza, ciencia e vida selvagem em 4K.",
                        quality = "4K"
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
                        backupUrls = listOf(
                            "http://64.31.23.207:80/live/59471019/60849037/5378645.m3u8"
                        )
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
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5676596.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/globo-sp-globo-sao-paulo.png",
                        description = "Transmissao em codec H.265 de alta eficiencia: imagem ultra nitida consumindo metade da banda.",
                        quality = "H265",
                        backupUrls = listOf(
                            "http://64.31.23.207:80/live/59471019/60849037/5298295.m3u8",
                            "https://cdn1.uniredes.com.br/hls/globo-sp-hd/index.m3u8?token=2d3232fe89d2b637a2093703"
                        )
                    ),
                    Channel(
                        id = "ch_h265_sportv",
                        number = "109",
                        title = "SporTV H.265 (HEVC)",
                        category = "Canais H.265 (HEVC)",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5676601.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/5/52/SporTV_logo_2021.svg/320px-SporTV_logo_2021.svg.png",
                        description = "O canal lider de esportes otimizado em compressao H.265 sem travamentos.",
                        quality = "H265"
                    ),
                    Channel(
                        id = "ch_h265_tc",
                        number = "110",
                        title = "Telecine H.265 (HEVC)",
                        category = "Canais H.265 (HEVC)",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5676607.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/82/Telecine_Premium_logo_2019.svg/320px-Telecine_Premium_logo_2019.svg.png",
                        description = "Filmes em alta taxa de bits codificados em H.265 para TV Boxes modernas.",
                        quality = "H265"
                    ),
                    Channel(
                        id = "ch_h265_hbo",
                        number = "111",
                        title = "HBO H.265 (HEVC)",
                        category = "Canais H.265 (HEVC)",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/514107.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/de/HBO_logo.svg/320px-HBO_logo.svg.png",
                        description = "Series e sucessos de Hollywood em transmissao eficiente H.265.",
                        quality = "H265"
                    ),
                    Channel(
                        id = "ch_h265_espn",
                        number = "112",
                        title = "ESPN H.265 (HEVC)",
                        category = "Canais H.265 (HEVC)",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/espn-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/espn-hd.png",
                        description = "Esportes internacionais com codificacao avancada H.265 a 60fps.",
                        quality = "H265"
                    ),
                    Channel(
                        id = "ch_h265_prem",
                        number = "113",
                        title = "Premiere H.265 (HEVC)",
                        category = "Canais H.265 (HEVC)",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5676611.m3u8",
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
                        id = "globo_sp_fhd",
                        number = "115",
                        title = "Globo SP FHD (Principal)",
                        category = "Globo",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5726127.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/globo-sp-globo-sao-paulo.png",
                        description = "TV Globo Sao Paulo FHD Oficial: Sinal direto de alta definicao com programacao completa e EPG ativo.",
                        quality = "FHD",
                        backupUrls = listOf(
                            "http://64.31.23.207:80/live/59471019/60849037/5298258.m3u8",
                            "https://cdn1.uniredes.com.br/hls/globo-sp-hd/index.m3u8?token=2d3232fe89d2b637a2093703"
                        )
                    ),
                    Channel(
                        id = "globo_sp_backup",
                        number = "116",
                        title = "Globo SP (Backup Alternativo)",
                        category = "Globo",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5727129.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/globo-sp-globo-sao-paulo.png",
                        description = "Sinal de contingencia da TV Globo Sao Paulo para garantir transmissao sem interrupcoes.",
                        quality = "FHD",
                        backupUrls = listOf(
                            "http://64.31.23.207:80/live/59471019/60849037/5298295.m3u8"
                        )
                    ),
                    Channel(
                        id = "globo_sp_4k",
                        number = "117",
                        title = "Globo SP 4K UHD",
                        category = "Globo",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5726127.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/globo-sp-globo-sao-paulo.png",
                        description = "TV Globo Sao Paulo em resolucao maxima 4K Ultra HD com codec HEVC de alta definicao.",
                        quality = "4K",
                        backupUrls = listOf(
                            "http://64.31.23.207:80/live/59471019/60849037/5298295.m3u8"
                        )
                    ),
                    Channel(
                        id = "globo_nac_fhd",
                        number = "118",
                        title = "Globo Nacional FHD",
                        category = "Globo",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5727129.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/globo-sp-globo-sao-paulo.png",
                        description = "Sinal nacional da Rede Globo de Televisao para todo o Brasil em Full HD.",
                        quality = "FHD"
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
                        backupUrls = listOf(
                            "http://64.31.23.207:80/live/59471019/60849037/5298295.m3u8"
                        )
                    ),
                    Channel(
                        id = "ch_160",
                        number = "117",
                        title = "Globo RJ - TV Globo Rio",
                        category = "Globo",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/globo-rj-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/globo-rj-tv-globo-rio.png",
                        description = "TV Globo Rio de Janeiro: Cobertura jornalistica carioca, novelas e transmissao ao vivo.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_163",
                        number = "118",
                        title = "Globo MG - TV Globo Minas",
                        category = "Globo",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/globo-minas-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/globo-mg-tv-globo-minas.png",
                        description = "Sinal oficial da TV Globo Minas com informacao e o melhor do entretenimento.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_166",
                        number = "119",
                        title = "Globo BA - TV Bahia",
                        category = "Globo",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/globo-bahia-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/globo-ba-tv-bahia.jpg",
                        description = "A TV Bahia e a emissora afiliada a Rede Globo com a maior cobertura do estado baiano.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_165",
                        number = "120",
                        title = "Globo DF - TV Globo Brasilia",
                        category = "Globo",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/globo-brasilia-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/globo-df-tv-globo-brasilia.png",
                        description = "Cobertura politica da capital federal, jornalismo nacional e entretenimento.",
                        quality = "HD"
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
                        streamUrl = "https://cdn1.uniredes.com.br/hls/sbt-sp-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/sbt-sp-sbt-sao-paulo.png",
                        description = "SBT Sao Paulo: A TV que tem torcida com Silvio Santos, jornalismo popular e novelas infantis.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_169",
                        number = "124",
                        title = "SBT RJ - SBT Rio",
                        category = "SBT",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/sbt-rj-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
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
                        streamUrl = "https://cdn1.uniredes.com.br/hls/record-sp-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/rederecord-sp-recordtv-sp.png",
                        description = "RecordTV Sao Paulo: Cidade Alerta, Jornal da Record, novelas biblicas e reality shows.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_177",
                        number = "126",
                        title = "RedeRecord RJ - RecordTV Rio",
                        category = "Record",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/record-rj-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
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
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5721427.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/band-sp-band-sp.png",
                        description = "Band Sao Paulo: O canal do esporte, Formula 1, Jogo Aberto, Os Donos da Bola e MasterChef.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_175",
                        number = "128",
                        title = "Band RJ - Band Rio",
                        category = "Band",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/band-rj-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
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
                        streamUrl = "https://cdn1.uniredes.com.br/hls/cnn-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/cnn-brasil.png",
                        description = "A maior marca de noticias do mundo no Brasil com analise politica e economica 24h.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_213",
                        number = "130",
                        title = "Jovem Pan News",
                        category = "Notícias",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/jovem-pam-news-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/jovem-pan-news.png",
                        description = "Opiniao forte, debates acalorados, Pingos nos Is e transmissao em tempo real.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_214",
                        number = "131",
                        title = "Band News",
                        category = "Notícias",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/band-news-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/band-news.png",
                        description = "Em 20 minutos tudo pode mudar: informacao rapida e atualizada no Brasil e no mundo.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_222",
                        number = "132",
                        title = "Record News",
                        category = "Notícias",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/record-news-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/record-news.png",
                        description = "O principal canal de jornalismo da televisao aberta no Brasil.",
                        quality = "HD"
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
                            "http://64.31.23.207:80/live/59471019/60849037/5676601.m3u8",
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
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5726197.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/80/Cartoon_Network_2010_logo.svg/320px-Cartoon_Network_2010_logo.svg.png",
                        description = "O canal dos desenhos animados: Ben 10, Teen Titans Go, Hora de Aventura e Incrivel Mundo de Gumball.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_cartoonito",
                        number = "135",
                        title = "Cartoonito HD",
                        category = "Infantil & Kids",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5726119.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/cartoonito.png",
                        description = "O canal infantil pre-escolar oficial (sucessor do Boomerang): Batwheels, Lucas a Aranha e Thomas.",
                        quality = "FHD"
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
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5726201.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/07/Discovery_Kids_logo_%282016%29.svg/320px-Discovery_Kids_logo_%282016%29.svg.png",
                        description = "O canal lider para o publico infantil: Peppa Pig, O Show da Luna e Doki.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_gloob",
                        number = "140",
                        title = "Gloob HD",
                        category = "Infantil & Kids",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5726203.m3u8",
                        logoUrl = "https://acstatic.co/logos/canais/gloob.png",
                        description = "Detetives do Predio Azul (DPA), Miraculous: As Aventuras de Ladybug e Bugados.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_gloobinho",
                        number = "141",
                        title = "Gloobinho HD",
                        category = "Infantil & Kids",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5726205.m3u8",
                        logoUrl = "http://acstatic.co/logos/canais/gloobinho.png",
                        description = "Historias encantadoras, musica e brincadeiras para a primeira infancia.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_ratimbum",
                        number = "142",
                        title = "TV Rá-Tim-Bum HD",
                        category = "Infantil & Kids",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/1882634.ts",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/tv-ra-tim-bum.png",
                        description = "O canal infantil brasileiro da Fundacao Padre Anchieta / TV Cultura: Cocorico e Ilha Ra-Tim-Bum.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_344",
                        number = "143",
                        title = "Rakuten TV Kids",
                        category = "Infantil & Kids",
                        streamUrl = "http://64.31.23.207:80/live/59471019/60849037/514107.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/rakuten-tv-kids.png",
                        description = "Animacoes divertidas e educativas com programacao segura para criancas.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_207",
                        number = "144",
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
                streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5676611.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Partida ao vivo de hoje: [15:00] Ah-Ali x Al-Khaleej",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_317687",
                number = "16",
                title = "[21:30] Flamengo x Estudiantes",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5676611.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Partida ao vivo de hoje: [21:30] Flamengo x Estudiantes",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_317688",
                number = "17",
                title = "Jogos Ao Vivo (Canal 03)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5676611.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_317689",
                number = "18",
                title = "Jogos Ao Vivo (Canal 04)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5676611.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_317690",
                number = "19",
                title = "Jogos Ao Vivo (Canal 05)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5676611.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_317691",
                number = "20",
                title = "Jogos Ao Vivo (Canal 06)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5676611.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_317692",
                number = "21",
                title = "Jogos Ao Vivo (Canal 07)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5676611.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_317693",
                number = "22",
                title = "Jogos Ao Vivo (Canal 08)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5676611.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_317694",
                number = "23",
                title = "Jogos Ao Vivo (Canal 09)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5676611.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_317695",
                number = "24",
                title = "Jogos Ao Vivo (Canal 10)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5676611.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_317696",
                number = "25",
                title = "Jogos Ao Vivo (Canal 11)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5676611.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_318080",
                number = "26",
                title = "Jogos Ao Vivo (Canal 12)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5676611.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_318081",
                number = "27",
                title = "Jogos Ao Vivo (Canal 13)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5676611.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_318082",
                number = "28",
                title = "Jogos Ao Vivo (Canal 14)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5676611.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_318083",
                number = "29",
                title = "Jogos Ao Vivo (Canal 15)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5676611.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_318084",
                number = "30",
                title = "Jogos Ao Vivo (Canal 16)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5676611.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_318085",
                number = "31",
                title = "Jogos Ao Vivo (Canal 17)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5676611.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_318086",
                number = "32",
                title = "Jogos Ao Vivo (Canal 18)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5676611.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_318606",
                number = "33",
                title = "Jogos Ao Vivo (Canal 19)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5676611.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_318607",
                number = "34",
                title = "Jogos Ao Vivo (Canal 20)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5676611.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_318608",
                number = "35",
                title = "Jogos Ao Vivo (Canal 21)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5676611.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_318609",
                number = "36",
                title = "Jogos Ao Vivo (Canal 22)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5676611.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_318610",
                number = "37",
                title = "Jogos Ao Vivo (Canal 23)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5676611.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_318611",
                number = "38",
                title = "Jogos Ao Vivo (Canal 24)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5676611.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_318612",
                number = "39",
                title = "Jogos Ao Vivo (Canal 25)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5676611.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_318613",
                number = "40",
                title = "Jogos Ao Vivo (Canal 26)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5676611.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_361372",
                number = "41",
                title = "Jogos Ao Vivo (Canal 27)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5676611.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_361373",
                number = "42",
                title = "Jogos Ao Vivo (Canal 28)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5676611.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_361374",
                number = "43",
                title = "Jogos Ao Vivo (Canal 29)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5676611.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_362685",
                number = "44",
                title = "Jogos Ao Vivo (Canal 30)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5676611.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_362686",
                number = "45",
                title = "Jogos Ao Vivo (Canal 31)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5676611.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_362687",
                number = "46",
                title = "Jogos Ao Vivo (Canal 32)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5676611.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_362688",
                number = "47",
                title = "Jogos Ao Vivo (Canal 33)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5676611.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Soccerball.svg/240px-Soccerball.svg.png",
                description = "Canal reservado para transmissões dos jogos do dia.",
                quality = "FHD"
            ),
            Channel(
                id = "jogo_dia_362826",
                number = "48",
                title = "Jogos Ao Vivo (Canal 34)",
                category = "⚽ Jogos do Dia (Ao Vivo)",
                streamUrl = "http://64.31.23.207:80/live/59471019/60849037/5676611.m3u8",
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
}
