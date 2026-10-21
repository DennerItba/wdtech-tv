package com.mytv.app.data

import com.mytv.app.model.Channel
import com.mytv.app.model.ChannelCategory

object ChannelRepository {

    fun getCategories(): List<ChannelCategory> {
        return listOf(
            ChannelCategory(
                id = "cat_globo",
                name = "Globo",
                isLocked = false,
                channels = listOf(
                    Channel(
                        id = "ch_324",
                        number = "01",
                        title = "Globo RBS - Globo Porto Alegre",
                        category = "Globo",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/globo-rbs-porto-alegre/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/globo-rbs-globo-porto-alegre.png",
                        description = "",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_321",
                        number = "02",
                        title = "Globo MA - Globo Maranhao",
                        category = "Globo",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/globo-maranhao-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/globo-ma-globo-maranhao.png",
                        description = "",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_167",
                        number = "03",
                        title = "Globo PR - RPC TV Curitiba",
                        category = "Globo",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/globo-rpc-curitiba-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/globo-pr-rpc-tv-curitiba.png",
                        description = "A RPC TV Curitiba é uma emissora afiliada à Rede Globo que atua na região metropolitana de Curitiba e em parte do estado do Paraná",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_166",
                        number = "04",
                        title = "Globo BA - TV Bahia",
                        category = "Globo",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/globo-bahia-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/globo-ba-tv-bahia.jpg",
                        description = "A TV Bahia é uma emissora afiliada à Rede Globo que opera no estado da Bahia, Brasil. Através de uma programação diversificada, a ",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_165",
                        number = "05",
                        title = "Globo DF - TV Globo Brasilia",
                        category = "Globo",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/globo-brasilia-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/globo-df-tv-globo-brasilia.jpg",
                        description = "A TV Globo Brasília é uma emissora afiliada à Rede Globo que atua no Distrito Federal e na região metropolitana de Brasília, Brasi",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_163",
                        number = "06",
                        title = "Globo MG - TV Globo Minas",
                        category = "Globo",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/globo-minas-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/globo-mg-tv-globo-minas.jpg",
                        description = "A TV Globo Minas é uma emissora afiliada à Rede Globo que atua no estado de Minas Gerais, Brasil. Através de sua programação diver",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_162",
                        number = "07",
                        title = "Gobo NE - TV Globo Nordeste",
                        category = "Globo",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/globo-nordeste-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/gobo-ne-tv-globo-nordeste.jpg",
                        description = "A TV Globo Nordeste é uma rede de emissoras afiliadas à Rede Globo que atende aos estados do Nordeste do Brasil. Com várias filiai",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_160",
                        number = "08",
                        title = "Globo RJ - TV Globo Rio",
                        category = "Globo",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/globo-rj-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/globo-rj-tv-globo-rj.jpg",
                        description = "A TV Globo Rio de Janeiro é uma das emissoras mais emblemáticas da Rede Globo, oferecendo uma programação variada e abrangente par",
                        quality = "4K"
                    ),
                    Channel(
                        id = "ch_158",
                        number = "09",
                        title = "Globo SP - Globo Sao Paulo",
                        category = "Globo",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/globo-sp-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/globo-sp-tv-globo-sp.jpg",
                        description = "A TV Globo SP é uma das emissoras de televisão mais influentes e prestigiadas do Brasil, pertencente ao Grupo Globo. Atendendo à r",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_155",
                        number = "10",
                        title = "Globo RJ - TV Rio Sul",
                        category = "Globo",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/globo-tv-rio-sul-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/globo-rj-tv-rio-sul.jpg",
                        description = "",
                        quality = "FHD"
                    ),
                )
            ),
            ChannelCategory(
                id = "cat_sbt",
                name = "SBT",
                isLocked = false,
                channels = listOf(
                    Channel(
                        id = "ch_173",
                        number = "11",
                        title = "SBT SP - SBT Interior",
                        category = "SBT",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/sbt-interior-sp-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/sbt-sp-sbt-interior.jpg",
                        description = "O \"SBT Interior\" é uma subdivisão do Sistema Brasileiro de Televisão (SBT) que abrange diversas cidades do interior do estado de",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_169",
                        number = "12",
                        title = "SBT RJ - SBT Rio",
                        category = "SBT",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/sbt-rj-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/sbt-rj-sbt-rio.jpg",
                        description = "O SBT Rio é uma emissora do Sistema Brasileiro de Televisão (SBT) que opera na região metropolitana do Rio de Janeiro, Brasil. Com",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_168",
                        number = "13",
                        title = "SBT SP - SBT Sao Paulo",
                        category = "SBT",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/sbt-sp-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/sbt-sp-sbt-sp.png",
                        description = "",
                        quality = "4K"
                    ),
                )
            ),
            ChannelCategory(
                id = "cat_record",
                name = "Record",
                isLocked = false,
                channels = listOf(
                    Channel(
                        id = "ch_178",
                        number = "14",
                        title = "RedeRecord SP - RecordTV SP",
                        category = "Record",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/record-sp-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/rederecord-recordtv-sp.jpg",
                        description = "A \"RecordTV São Paulo\" é uma emissora da Rede Record de Televisão que opera na região metropolitana de São Paulo, Brasil. Com um",
                        quality = "4K"
                    ),
                    Channel(
                        id = "ch_177",
                        number = "15",
                        title = "RedeRecord RJ - RecordTV Rio",
                        category = "Record",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/record-rj-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/rederecord-rj-recordtv-rio.png",
                        description = "A \"RecordTV Rio\" é uma emissora da Rede Record de Televisão que opera na região metropolitana do Rio de Janeiro, Brasil. Com uma",
                        quality = "HD"
                    ),
                )
            ),
            ChannelCategory(
                id = "cat_band",
                name = "Band",
                isLocked = false,
                channels = listOf(
                    Channel(
                        id = "ch_406",
                        number = "16",
                        title = "Band RBATV",
                        category = "Band",
                        streamUrl = "https://cdn.live.br1.jmvstream.com/w/LVW-15748/LVW15748_Yed7yzLuRC/chunklist.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/band-rbatv.png",
                        description = "",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_176",
                        number = "17",
                        title = "Band SP - Band SP",
                        category = "Band",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/band-sp-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/band-sp-band-sp.jpg",
                        description = "A \"Band São Paulo\" é uma emissora da Rede Bandeirantes de Televisão (Band) que opera na região metropolitana de São Paulo, Brasi",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_175",
                        number = "18",
                        title = "Band RJ - Band Rio",
                        category = "Band",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/band-rj-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/band-rj-band-rio.png",
                        description = "A \"Band Rio\" é uma emissora da Rede Bandeirantes de Televisão (Band) que opera na região metropolitana do Rio de Janeiro, Brasil",
                        quality = "HD"
                    ),
                )
            ),
            ChannelCategory(
                id = "cat_noticias",
                name = "Notícias",
                isLocked = false,
                channels = listOf(
                    Channel(
                        id = "ch_359",
                        number = "19",
                        title = "TVT News",
                        category = "Notícias",
                        streamUrl = "https://video05.logicahost.com.br/comunitariario/comunitariario/playlist.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/tvt-news.png",
                        description = "",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_349",
                        number = "20",
                        title = "Portal Cultura",
                        category = "Notícias",
                        streamUrl = "https://www.portalcultura.com.br/playerhtml/funtelpa/tv_funtelpa/playlist.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/portal-cultura.png",
                        description = "",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_222",
                        number = "21",
                        title = "Record News",
                        category = "Notícias",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/record-news-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/record-news.jpg",
                        description = "A \"Record News\" é um canal de televisão brasileiro que pertence ao Grupo Record, um dos maiores grupos de mídia do Brasil. O can",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_215",
                        number = "22",
                        title = "Euro News",
                        category = "Notícias",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/euronews-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/euro-news.jpg",
                        description = "A \"Euronews\" é um canal de notícias internacional que oferece cobertura abrangente e imparcial dos eventos globais. Com sede na ",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_214",
                        number = "23",
                        title = "Band News",
                        category = "Notícias",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/band-news-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/band-news.jpg",
                        description = "A \"BandNews TV\" é um canal de notícias que oferece cobertura jornalística contínua e diversificada. Pertencente ao Grupo Bandeir",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_213",
                        number = "24",
                        title = "Jovem Pan News",
                        category = "Notícias",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/jovem-pam-news-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/jovem-pan-news.jpg",
                        description = "A \"Jovem Pan News\" é um canal de notícias que oferece cobertura jornalística diversificada e atualizada. Pertencente ao Grupo Jo",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_212",
                        number = "25",
                        title = "CNN Brasil",
                        category = "Notícias",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/cnn-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/cnn-brasil.jpg",
                        description = "A \"CNN Brasil\" é um canal de notícias televisivo e digital que traz cobertura jornalística abrangente e atualizada. Inspirado no",
                        quality = "HD"
                    ),
                )
            ),
            ChannelCategory(
                id = "cat_f1",
                name = "Fórmula 1 & Velocidade",
                isLocked = false,
                channels = listOf(
                    Channel(
                        id = "ch_f1_pro",
                        number = "26",
                        title = "F1 TV Pro HD - Oficial",
                        category = "Fórmula 1 & Velocidade",
                        streamUrl = "https://mvg-mv-xumo.otteravision.com/mvg/mv/mv.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/33/F1.svg/320px-F1.svg.png",
                        description = "Transmissão mundial da Fórmula 1 em alta definição, telemetria, câmeras OnBoard e pit lane em tempo real.",
                        quality = "4K"
                    ),
                    Channel(
                        id = "ch_f1_bandsports",
                        number = "27",
                        title = "BandSports HD - F1 Treinos & Qualificação",
                        category = "Fórmula 1 & Velocidade",
                        streamUrl = "http://45.162.64.114/BAND_SPORTS/index.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/cf/Bandsports_logo_2018.png/320px-Bandsports_logo_2018.png",
                        description = "Transmissão exclusiva de todos os Treinos Livres (FP1, FP2, FP3), Classificação, Sprint Shootout e Fórmula 2 / 3.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_f1_band_gp",
                        number = "28",
                        title = "Band HD - Fórmula 1 GP Ao Vivo",
                        category = "Fórmula 1 & Velocidade",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/band-sp-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/33/F1.svg/320px-F1.svg.png",
                        description = "Emissora oficial da Fórmula 1 no Brasil. Todos os Grandes Prêmios, largadas e cerimônia de pódio ao vivo.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_f1_onboard",
                        number = "29",
                        title = "F1 OnBoard & Pit Lane 24H",
                        category = "Fórmula 1 & Velocidade",
                        streamUrl = "https://digicom.hls.iptvdc.com/canalmotor/index.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/33/F1.svg/320px-F1.svg.png",
                        description = "Câmeras exclusivas acopladas nos bólidos da Fórmula 1, comunicação de rádio dos pilotos e telemetria de pista.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_f1_motorvision",
                        number = "30",
                        title = "Motorvision TV HD",
                        category = "Fórmula 1 & Velocidade",
                        streamUrl = "https://mvg-mv-xumo.otteravision.com/mvg/mv/mv.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/33/F1.svg/320px-F1.svg.png",
                        description = "O canal dos apaixonados por velocidade: análises de carros de F1, supermáquinas, 24 Horas de Le Mans e automobilismo internacional",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_f1_redbull",
                        number = "31",
                        title = "Red Bull Racing F1 TV HD",
                        category = "Fórmula 1 & Velocidade",
                        streamUrl = "https://mvg-mv-xumo.otteravision.com/mvg/mv/mv.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d4/Red_Bull_Racing_logo.svg/320px-Red_Bull_Racing_logo.svg.png",
                        description = "Conteúdo exclusivo dos bastidores da Red Bull Racing na Fórmula 1, simuladores e preparação dos pilotos.",
                        quality = "FHD"
                    ),
                )
            ),
            ChannelCategory(
                id = "cat_esportes",
                name = "Esportes & CazéTV",
                isLocked = false,
                channels = listOf(
                    Channel(
                        id = "ch_320_caze",
                        number = "32",
                        title = "Cazé TV HD",
                        category = "Esportes & CazéTV",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/caze-tv-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/caze-tv.png",
                        description = "Transmissões ao vivo dos maiores eventos esportivos, Brasileirão, Libertadores e produções com Casimiro Miguel e equipe CazéTV.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_374",
                        number = "33",
                        title = "Hard Knocks Fight Championship TV",
                        category = "Esportes & CazéTV",
                        streamUrl = "https://d3uyzhwvmemdyf.cloudfront.net/scheduler/scheduleMaster/109.m3u8?ROGERIOTORRES",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/hard-knocks-fight-championship-tv.jpg",
                        description = "",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_373",
                        number = "34",
                        title = "Fuel TV",
                        category = "Esportes & CazéTV",
                        streamUrl = "https://cdn-uw2-prod.tsv2.amagi.tv/linear/amg01074-fueltv-fueltvbr-samsungbr/playlist.m3u8?ROGERIOTORRES",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/fuel-tv.png",
                        description = "",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_365",
                        number = "35",
                        title = "ESPN 6",
                        category = "Esportes & CazéTV",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/espn-6-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/espn-6.png",
                        description = "",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_364",
                        number = "36",
                        title = "ESPN 4 HD",
                        category = "Esportes & CazéTV",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/espn-4/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/espn-5-hd-yp7n8qt10m13.png",
                        description = "",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_293",
                        number = "37",
                        title = "ESPN 5 HD",
                        category = "Esportes & CazéTV",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/espn-5-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/espn-5-hd.link",
                        description = "ESPN 5, anteriormente conhecido como Sports 5, é um canal de esportes nas Filipinas que surgiu de uma parceria entre a TV5 Network",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_292",
                        number = "38",
                        title = "ESPN 3 HD",
                        category = "Esportes & CazéTV",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/espn-3-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/espn-3-hd.link",
                        description = "ESPN 3 é um canal de televisão por assinatura que faz parte da família de redes ESPN, oferecendo uma ampla gama de conteúdos espor",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_291",
                        number = "39",
                        title = "ESPN 2 HD",
                        category = "Esportes & CazéTV",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/espn-2-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/espn-2-hd.link",
                        description = "ESPN 2 é um canal de televisão por assinatura que faz parte da família de redes ESPN, oferecendo uma ampla gama de conteúdos espor",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_290",
                        number = "40",
                        title = "ESPN HD",
                        category = "Esportes & CazéTV",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/espn-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/espn-hd.link",
                        description = "ESPN HD é um canal de televisão por assinatura especializado em esportes, conhecido por sua transmissão em alta definição que prop",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_253",
                        number = "41",
                        title = "Red Bull TV",
                        category = "Esportes & CazéTV",
                        streamUrl = "https://rbmn-live.akamaized.net/hls/live/590964/BoRB-AT/master_6660.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/red-bull-tv.jpg",
                        description = "Bem-vindo à \"Red Bull TV\"! Aqui, exploramos um universo de emoção, aventura e energia sem limites. Nosso canal é o seu ingresso ",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_249",
                        number = "42",
                        title = "partypoker TV",
                        category = "Esportes & CazéTV",
                        streamUrl = "https://d1m7jfoe9zdc1j.cloudfront.net/5d5a816c31e610d4c5f9_partypokertv_40169660985_1662408479/chunked/index-dvr.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/partypoker-tv.jpeg",
                        description = "Bem-vindo ao \"partypoker TV\"! Este é o canal que coloca você no centro da ação emocionante do poker. Aqui, mergulhamos nas mesas",
                        quality = "HD"
                    ),
                )
            ),
            ChannelCategory(
                id = "cat_premiere",
                name = "Premiere & Combate",
                isLocked = false,
                channels = listOf(
                    Channel(
                        id = "prem_clubes",
                        number = "43",
                        title = "Premiere Clubes HD",
                        category = "Premiere & Combate",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/espn-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d7/Premiere_logo_2021.svg/320px-Premiere_logo_2021.svg.png",
                        description = "O melhor do futebol brasileiro ao vivo: Brasileirão Série A e B com cobertura completa.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "prem_2",
                        number = "44",
                        title = "Premiere 2 HD",
                        category = "Premiere & Combate",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/espn-4/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d7/Premiere_logo_2021.svg/320px-Premiere_logo_2021.svg.png",
                        description = "Jogos simultâneos dos maiores clubes do país em alta definição.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "prem_3",
                        number = "45",
                        title = "Premiere 3 HD",
                        category = "Premiere & Combate",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/espn-6-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d7/Premiere_logo_2021.svg/320px-Premiere_logo_2021.svg.png",
                        description = "Transmissão exclusiva de partidas decisivas e pré-jogos.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "prem_4",
                        number = "46",
                        title = "Premiere 4 HD",
                        category = "Premiere & Combate",
                        streamUrl = "http://45.162.64.114/BAND_SPORTS/index.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d7/Premiere_logo_2021.svg/320px-Premiere_logo_2021.svg.png",
                        description = "Grade especial com mosaico de gols e rodadas decisivas.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "prem_combate",
                        number = "47",
                        title = "Combate HD",
                        category = "Premiere & Combate",
                        streamUrl = "https://d3uyzhwvmemdyf.cloudfront.net/scheduler/scheduleMaster/109.m3u8?ROGERIOTORRES",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c5/Canal_Combate_logo.png/320px-Canal_Combate_logo.png",
                        description = "O mundo das artes marciais: UFC ao vivo, boxe mundial, jiu-jitsu e kickboxing.",
                        quality = "FHD"
                    ),
                )
            ),
            ChannelCategory(
                id = "cat_telecine",
                name = "Rede Telecine",
                isLocked = false,
                channels = listOf(
                    Channel(
                        id = "tc_premium",
                        number = "48",
                        title = "Telecine Premium HD",
                        category = "Rede Telecine",
                        streamUrl = "http://45.162.64.114/SONY_MOVIES/index.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/82/Telecine_Premium_logo_2019.svg/320px-Telecine_Premium_logo_2019.svg.png",
                        description = "Grandes sucessos de bilheteria e estreias exclusivas do cinema mundial em primeira mão.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "tc_action",
                        number = "49",
                        title = "Telecine Action HD",
                        category = "Rede Telecine",
                        streamUrl = "http://46.151.196.223:14326/",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/eb/Telecine_Action_logo_2019.svg/320px-Telecine_Action_logo_2019.svg.png",
                        description = "Adrenalina pura, perseguições eletrizantes, super-heróis e grandes clássicos de ação.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "tc_touch",
                        number = "50",
                        title = "Telecine Touch HD",
                        category = "Rede Telecine",
                        streamUrl = "https://stream.ads.ottera.tv/playlist.m3u8?network_id=6178",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d7/Telecine_Touch_logo_2019.svg/320px-Telecine_Touch_logo_2019.svg.png",
                        description = "Dramas emocionantes, histórias de superação e romances inesquecíveis.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "tc_pipoca",
                        number = "51",
                        title = "Telecine Pipoca HD",
                        category = "Rede Telecine",
                        streamUrl = "https://cc-hqw8u5r1nshjc.akamaized.net/scheduler/scheduleMaster/352.m3u8?ROGERIOTORRES",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/87/Telecine_Pipoca_logo_2019.svg/320px-Telecine_Pipoca_logo_2019.svg.png",
                        description = "O canal dos filmes dublados para assistir reunido com toda a família.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "tc_fun",
                        number = "52",
                        title = "Telecine Fun HD",
                        category = "Rede Telecine",
                        streamUrl = "https://amogonetworx-grjngo-1-eu.plex.wurl.tv/4300.m3u8?ROGERIOTORRES",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/4/4b/Telecine_Fun_logo_2019.svg/320px-Telecine_Fun_logo_2019.svg.png",
                        description = "Comédias inteligentes, animações premiadas e diversão garantida.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "tc_cult",
                        number = "53",
                        title = "Telecine Cult HD",
                        category = "Rede Telecine",
                        streamUrl = "https://live20.bozztv.com/giatvplayout7/giatv-208272/tracks-v1a1/mono.ts.m3u8?ROGERIOTORRES",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c9/Telecine_Cult_logo_2019.svg/320px-Telecine_Cult_logo_2019.svg.png",
                        description = "O melhor do cinema autoral, clássicos eternos de Hollywood e festivais internacionais.",
                        quality = "HD"
                    ),
                )
            ),
            ChannelCategory(
                id = "cat_hbo",
                name = "HBO & Warner",
                isLocked = false,
                channels = listOf(
                    Channel(
                        id = "hbo_main",
                        number = "54",
                        title = "HBO HD",
                        category = "HBO & Warner",
                        streamUrl = "http://45.162.64.114/TNT/index.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/de/HBO_logo.svg/320px-HBO_logo.svg.png",
                        description = "Séries consagradas, grandes documentários e as maiores produções mundiais.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "hbo_2",
                        number = "55",
                        title = "HBO 2 HD",
                        category = "HBO & Warner",
                        streamUrl = "http://45.162.64.114/SPACE/index.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/de/HBO_logo.svg/320px-HBO_logo.svg.png",
                        description = "Filmes e séries originais HBO dublados em português.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "hbo_plus",
                        number = "56",
                        title = "HBO Plus HD",
                        category = "HBO & Warner",
                        streamUrl = "http://170.83.16.50/SONY_CHANNEL/index.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/de/HBO_logo.svg/320px-HBO_logo.svg.png",
                        description = "Muita adrenalina com filmes de ação, shows mundiais e comédias.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "hbo_family",
                        number = "57",
                        title = "HBO Family HD",
                        category = "HBO & Warner",
                        streamUrl = "http://45.162.64.114/SONY_MOVIES/index.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/de/HBO_logo.svg/320px-HBO_logo.svg.png",
                        description = "Programação voltada para a família com filmes e animações para todas as idades.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "warner_ch",
                        number = "58",
                        title = "Warner Channel HD",
                        category = "HBO & Warner",
                        streamUrl = "http://170.83.49.66:8083/AXNHD/index.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3d/Warner_Channel_2013_logo.svg/320px-Warner_Channel_2013_logo.svg.png",
                        description = "As melhores séries da TV americana, universo DC Comics e comédias consagradas.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "megapix_ch",
                        number = "59",
                        title = "Megapix HD",
                        category = "HBO & Warner",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/tnt-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/8a/Megapix_logo_2020.svg/320px-Megapix_logo_2020.svg.png",
                        description = "O canal de filmes mais assistido da TV por assinatura, 100% dublado.",
                        quality = "FHD"
                    ),
                )
            ),
            ChannelCategory(
                id = "cat_filmes",
                name = "Filmes & Séries",
                isLocked = false,
                channels = listOf(
                    Channel(
                        id = "ch_tnt",
                        number = "60",
                        title = "TNT HD",
                        category = "Filmes & Séries",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/tnt-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/9/91/TNT_Logo_2016.svg/320px-TNT_Logo_2016.svg.png",
                        description = "Grandes filmes campeões de bilheteria e transmissões dos maiores prêmios do cinema.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_space",
                        number = "61",
                        title = "Space HD",
                        category = "Filmes & Séries",
                        streamUrl = "http://45.162.64.114/SPACE/index.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/1/1b/Space_logo_2015.svg/320px-Space_logo_2015.svg.png",
                        description = "Ação extrema, filmes de ficção científica, artes marciais e suspense.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_axn",
                        number = "62",
                        title = "AXN HD",
                        category = "Filmes & Séries",
                        streamUrl = "http://170.83.49.66:8083/AXNHD/index.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/9/9e/AXN_logo_2015.svg/320px-AXN_logo_2015.svg.png",
                        description = "Séries de investigação forense, criminalística, perseguições e suspense.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_sony_movies",
                        number = "63",
                        title = "Sony Movies HD",
                        category = "Filmes & Séries",
                        streamUrl = "http://45.162.64.114/SONY_MOVIES/index.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/7/77/Sony_Movies_logo_2022.svg/320px-Sony_Movies_logo_2022.svg.png",
                        description = "O acervo completo dos lendários estúdios da Columbia Pictures e Sony Pictures.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_392",
                        number = "64",
                        title = "TV Classicos do Cinema",
                        category = "Filmes & Séries",
                        streamUrl = "https://live20.bozztv.com/giatvplayout7/giatv-208272/tracks-v1a1/mono.ts.m3u8?ROGERIOTORRES",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/tv-classicos-do-cinema.jpg",
                        description = "",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_391",
                        number = "65",
                        title = "TV Sonata",
                        category = "Filmes & Séries",
                        streamUrl = "https://video04.logicahost.com.br/isonata/isonata/chunklist.m3u8?ROGERIOTORRES",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/tv-sonata.jpg",
                        description = "",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_390",
                        number = "66",
                        title = "TV Mais Brasil",
                        category = "Filmes & Séries",
                        streamUrl = "https://stmv8.samcast.com.br/tvmaisbrasil/tvmaisbrasil/playlist.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/tv-mais-brasil.png",
                        description = "",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_388",
                        number = "67",
                        title = "Top Mix Filmes",
                        category = "Filmes & Séries",
                        streamUrl = "https://video03.logicahost.com.br/filmestopmixtv/filmestopmixtv/chunklist.m3u8?ROGERIOTORRES",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/top-mix-filmes.webp",
                        description = "",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_387",
                        number = "68",
                        title = "Spark TV",
                        category = "Filmes & Séries",
                        streamUrl = "https://d25usgadhphvwi.cloudfront.net/v1/master/3722c60a815c199d9c0ef36c5b73da68a62b09d1/cc-sr15nxkp06uk9/playlist.m3u8?ROGERIOTORRES",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/spark-tv.jpg",
                        description = "",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_385",
                        number = "69",
                        title = "Hallo! Series",
                        category = "Filmes & Séries",
                        streamUrl = "https://cdn-5.nxplay.com.br/HALLOSERIES/tracks-v1a1/mono.m3u8?ROGERIOTORRES",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/hallo-series.png",
                        description = "",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_384",
                        number = "70",
                        title = "Grjngo Filmes",
                        category = "Filmes & Séries",
                        streamUrl = "https://amogonetworx-grjngo-1-eu.plex.wurl.tv/4300.m3u8?ROGERIOTORRES",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/grjngo-filmes.jpg",
                        description = "",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_380",
                        number = "71",
                        title = "Cindie Lite HD",
                        category = "Filmes & Séries",
                        streamUrl = "https://cc-hqw8u5r1nshjc.akamaized.net/scheduler/scheduleMaster/352.m3u8?ROGERIOTORRES",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/cindie-lite-hd.jpg",
                        description = "",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_285",
                        number = "72",
                        title = "TNT HD",
                        category = "Filmes & Séries",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/tnt-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/tnt-hd.link",
                        description = "TNT HD é a versão em alta definição do canal Turner Network Television (TNT), oferecendo uma mistura de programação que inclui fil",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_367",
                        number = "73",
                        title = "Movie Sphere",
                        category = "Filmes & Séries",
                        streamUrl = "https://stream.ads.ottera.tv/playlist.m3u8?network_id=6178",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/movie-sphere.jpeg",
                        description = "",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_340",
                        number = "74",
                        title = "AWTV",
                        category = "Filmes & Séries",
                        streamUrl = "https://awtv.nuvemplay.live/hls/stream.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/awtv.png",
                        description = "Uma plataforma de televisão baseada na web que fornece aos espectadores as últimas notícias e atualidades. Diferentemente dos cana",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_332",
                        number = "75",
                        title = "RunTime - Terror",
                        category = "Filmes & Séries",
                        streamUrl = "https://stream.ads.ottera.tv/playlist.m3u8?network_id=4865",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/runtime-terror.png",
                        description = "O&nbsp;Runtime Terror&nbsp;é um canal da plataforma de streaming gratuita&nbsp;Runtime, dedicado a oferecer entretenimento para to",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_331",
                        number = "76",
                        title = "RunTime - Cinema",
                        category = "Filmes & Séries",
                        streamUrl = "https://stream.ads.ottera.tv/playlist.m3u8?network_id=2153",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/runtime-cinema.png",
                        description = "O&nbsp;Runtime Cinema&nbsp;é um canal da plataforma de streaming gratuita&nbsp;Runtime, dedicado a oferecer entretenimento para to",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_330",
                        number = "77",
                        title = "RunTime - Comédia",
                        category = "Filmes & Séries",
                        streamUrl = "https://stream.ads.ottera.tv/playlist.m3u8?network_id=2553",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/runtime-comédia.png",
                        description = "O&nbsp;Runtime Comédia&nbsp;é um canal da plataforma de streaming gratuita&nbsp;Runtime, dedicado a oferecer entretenimento para t",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_329",
                        number = "78",
                        title = "RunTime - Crime",
                        category = "Filmes & Séries",
                        streamUrl = "https://stream.ads.ottera.tv/playlist.m3u8?network_id=4864",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/runtime-crime.png",
                        description = "O&nbsp;Runtime Crime&nbsp;é um canal da plataforma de streaming gratuita&nbsp;Runtime, dedicado a oferecer entretenimento para tod",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_328",
                        number = "79",
                        title = "RunTime - Familia",
                        category = "Filmes & Séries",
                        streamUrl = "https://stream.ads.ottera.tv/playlist.m3u8?network_id=4866",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/runtime-familia.png",
                        description = "O&nbsp;Runtime Família&nbsp;é um canal da plataforma de streaming gratuita&nbsp;Runtime, dedicado a oferecer entretenimento para t",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_318",
                        number = "80",
                        title = "Comedy Club",
                        category = "Filmes & Séries",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/comedy-club/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/comedy-club.png",
                        description = "",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_295",
                        number = "81",
                        title = "RunTime - Romance",
                        category = "Filmes & Séries",
                        streamUrl = "https://stream.ads.ottera.tv/playlist.m3u8?network_id=4866",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/runtime-romance.png",
                        description = "O canal National Geographic, também conhecido como Nat Geo, é um canal de televisão por assinatura que oferece uma vasta gama de d",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_284",
                        number = "82",
                        title = "TNT Novelas HD",
                        category = "Filmes & Séries",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/tnt-novelas-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/tnt-novelas-hd.link",
                        description = "TNT Novelas é um canal de TV por assinatura da Warner Bros. Discovery, voltado exclusivamente para novelas. Lançado em 26 de junho",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_283",
                        number = "83",
                        title = "SPACE HD",
                        category = "Filmes & Séries",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/space-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/space-hd.link",
                        description = "Space HD é um canal de televisão por assinatura latino-americano pertencente à Warner Bros. Discovery. Lançado em 11 de março de 1",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_274",
                        number = "84",
                        title = "My Time Movie",
                        category = "Filmes & Séries",
                        streamUrl = "https://appletree-mytime-samsungbrazil.amagi.tv/playlist.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/my-time-movie.webp",
                        description = "Bem-vindo ao \"My Time Movie\"! Este é o canal onde cada momento é uma experiência cinematográfica única. Aqui, exploramos uma sel",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_273",
                        number = "85",
                        title = "RunTime - Ação",
                        category = "Filmes & Séries",
                        streamUrl = "https://stream.ads.ottera.tv/playlist.m3u8?network_id=2552",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/runtime-ação.png",
                        description = "Prepare-se para rir com o \"Cine Pipoca 03\"! Este é o canal que traz uma seleção hilária de comédias de todos os tempos. De cláss",
                        quality = "HD"
                    ),
                )
            ),
            ChannelCategory(
                id = "cat_reality",
                name = "Reality Shows 24H",
                isLocked = false,
                channels = listOf(
                    Channel(
                        id = "ch_fazenda_1",
                        number = "86",
                        title = "Reality A Fazenda 24H HD",
                        category = "Reality Shows 24H",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/record-sp-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/pt/thumb/d/d7/Logotipo_de_A_Fazenda.png/320px-Logotipo_de_A_Fazenda.png",
                        description = "Câmera principal ao vivo 24 horas por dia dentro da sede de A Fazenda com todos os peões.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_fazenda_2",
                        number = "87",
                        title = "Reality A Fazenda (Sede & Baia)",
                        category = "Reality Shows 24H",
                        streamUrl = "http://45.162.64.114/RECORD_NEWS/index.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/pt/thumb/d/d7/Logotipo_de_A_Fazenda.png/320px-Logotipo_de_A_Fazenda.png",
                        description = "Transmissão das tarefas diárias, animais e área de convivência dos confinados.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_bbb_24h",
                        number = "88",
                        title = "Reality BBB 24H HD",
                        category = "Reality Shows 24H",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/globo-sp-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/62/TV_Globo_2021.svg/320px-TV_Globo_2021.svg.png",
                        description = "A casa mais vigiada do país ao vivo com câmeras exclusivas e confessionário.",
                        quality = "FHD"
                    ),
                )
            ),
            ChannelCategory(
                id = "cat_docs",
                name = "Documentários & History",
                isLocked = false,
                channels = listOf(
                    Channel(
                        id = "ch_history",
                        number = "89",
                        title = "History Channel HD",
                        category = "Documentários & History",
                        streamUrl = "http://170.83.16.50/HISTORY/index.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f5/History_Logo.svg/320px-History_Logo.svg.png",
                        description = "Grandes civilizações, relíquias históricas, enigmas da humanidade e batalhas militares.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_history_2",
                        number = "90",
                        title = "History 2 (H2) HD",
                        category = "Documentários & History",
                        streamUrl = "http://170.83.49.66:8083/DISCOVERYWORLDHD/index.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f5/History_Logo.svg/320px-History_Logo.svg.png",
                        description = "Conexões científicas, mistérios do planeta Terra e tecnologia através dos tempos.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_299",
                        number = "91",
                        title = "Discovery Channel HD",
                        category = "Documentários & History",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/discovery-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/discovery-channel-hd.link",
                        description = "Discovery Channel&nbsp; é um canal de televisão que oferece uma programação variada focada em documentários e programas educativos",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_298",
                        number = "92",
                        title = "TLC HD",
                        category = "Documentários & History",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/tlc-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/tlc-hd.link",
                        description = "TLC HD é um canal de televisão por assinatura que pertence à Warner Bros. Discovery. Originalmente lançado como The Learning Chann",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_297",
                        number = "93",
                        title = "Discovery Turbo HD",
                        category = "Documentários & History",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/discovery-turbo-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/discovery-turbo-hd.link",
                        description = "Discovery Turbo HD é um canal de televisão dedicado a programas sobre motores e veículos, oferecendo entretenimento adrenalina par",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_296",
                        number = "94",
                        title = "Discovery h&h HD",
                        category = "Documentários & History",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/discovery-hh/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/discovery-hh-hd.link",
                        description = "Discovery Home &amp; Health (Discovery H&amp;H) é um canal de televisão por assinatura focado em programação sobre estilo de vida,",
                        quality = "HD"
                    ),
                )
            ),
            ChannelCategory(
                id = "cat_kids",
                name = "Infantil & Kids",
                isLocked = false,
                channels = listOf(
                    Channel(
                        id = "ch_cartoon",
                        number = "95",
                        title = "Cartoon Network HD",
                        category = "Infantil & Kids",
                        streamUrl = "http://45.162.64.114/CARTOON_NETWORK/index.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/80/Cartoon_Network_2010_logo.svg/320px-Cartoon_Network_2010_logo.svg.png",
                        description = "O melhor dos desenhos animados 24 horas por dia com humor e aventura.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_discovery_kids",
                        number = "96",
                        title = "Discovery Kids HD",
                        category = "Infantil & Kids",
                        streamUrl = "http://170.83.49.66:8083/DISCOVERYKIDSHD/index.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f6/Discovery_Kids_2016.svg/320px-Discovery_Kids_2016.svg.png",
                        description = "Líder em entretenimento infantil com conteúdos educativos e muita diversão.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_400",
                        number = "97",
                        title = "Wording TV",
                        category = "Infantil & Kids",
                        streamUrl = "https://tv02.zas.media:1936/wordingtv/wordingtv/chunklist.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/wording-tv.png",
                        description = "",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_393",
                        number = "98",
                        title = "Ministerio Infantil TV",
                        category = "Infantil & Kids",
                        streamUrl = "https://stmv4.voxtvhd.com.br/1990/1990/playlist.m3u8?ROGERIOTORRES",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/ministerio-infantil-tv.jpg",
                        description = "",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_369",
                        number = "99",
                        title = "Otaku Sign TV",
                        category = "Infantil & Kids",
                        streamUrl = "https://stmv1.srvif.com/anime/anime/playlist.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/otaku-sign-tv.jpg",
                        description = "",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_368",
                        number = "100",
                        title = "Sato +",
                        category = "Infantil & Kids",
                        streamUrl = "https://sto-sat-samsung.otteravision.com/sto/sat/sat.m3u8?ROGERIOTORRES",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/sato.jpg",
                        description = "",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_363",
                        number = "101",
                        title = "Rede Brasil",
                        category = "Infantil & Kids",
                        streamUrl = "https://tv02.zas.media:1936/tvmarajoara/tvmarajoara/playlist.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/rede-brasil.png",
                        description = "",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_356",
                        number = "102",
                        title = "TV NBN",
                        category = "Infantil & Kids",
                        streamUrl = "https://cdn.jmvstream.com/w/LVW-8410/LVW8410_uiZOVm6vz1/playlist.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/tv-nbn.png",
                        description = "",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_354",
                        number = "103",
                        title = "TV +Marica",
                        category = "Infantil & Kids",
                        streamUrl = "https://5cf4a2c2512a2.streamlock.net/tvmaismarica/tvmaismarica/playlist.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/tv-marica.png",
                        description = "",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_351",
                        number = "104",
                        title = "Cine Kuriakos",
                        category = "Infantil & Kids",
                        streamUrl = "https://w2.manasat.com/kcine/smil:kcine.smil/chunklist_w1486728732_b30048000_sleng.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/cine-kuriakos.png",
                        description = "",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_350",
                        number = "105",
                        title = "Gospel Cartoon",
                        category = "Infantil & Kids",
                        streamUrl = "https://stmv1.srvif.com/gospelcartoon/gospelcartoon/playlist.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/gospel-cartoon.png",
                        description = "",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_344",
                        number = "106",
                        title = "Rakuten TV Kids",
                        category = "Infantil & Kids",
                        streamUrl = "http://45.162.64.114/CARTOON_NETWORK/index.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/rakuten-tv-kids.png",
                        description = "",
                        quality = "HD"
                    ),
                )
            ),
            ChannelCategory(
                id = "cat_musica",
                name = "Músicas & Shows",
                isLocked = false,
                channels = listOf(
                    Channel(
                        id = "ch_396",
                        number = "107",
                        title = "Now 70s",
                        category = "Músicas & Shows",
                        streamUrl = "https://lightning-now70s-samsungnz.amagi.tv/playlist720p.m3u8?ROGERIOTORRES",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/now-70s.png",
                        description = "",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_389",
                        number = "108",
                        title = "BS TV",
                        category = "Músicas & Shows",
                        streamUrl = "https://br5093.streamingdevideo.com.br/bstv/bstv/chunklist.m3u8?ROGERIOTORRES",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/bs-tv.png",
                        description = "",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_361",
                        number = "109",
                        title = "KPOP TV",
                        category = "Músicas & Shows",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/tnt-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/kpop-tv.png",
                        description = "",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_341",
                        number = "110",
                        title = "Top Mix",
                        category = "Músicas & Shows",
                        streamUrl = "https://video01.logicahost.com.br/topmixtvsertanejooficial/topmixtvsertanejooficial/chunklist_w1579950137.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/top-mix.png",
                        description = "A&nbsp;Top Mix TV&nbsp;é uma emissora de televisão brasileira que se destaca por sua programação diversificada e conteúdo de quali",
                        quality = "HD"
                    ),
                )
            ),
            ChannelCategory(
                id = "cat_agro",
                name = "Agro & Rural",
                isLocked = false,
                channels = listOf(
                    Channel(
                        id = "ch_229",
                        number = "111",
                        title = "Canal Rural",
                        category = "Agro & Rural",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/canal-rural-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/canal-rural.jpg",
                        description = "O \"Canal Rural\" é uma emissora de televisão brasileira dedicada ao agronegócio e à ruralidade. Com uma programação focada em tem",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_188",
                        number = "112",
                        title = "AgroBrasil TV",
                        category = "Agro & Rural",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/agrobrasil-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/agrobrasiltv.jpg",
                        description = "O AgroBrasil TV é o canal que tem sua programação totalmente voltada para o AgronegócioO Agronegócio brasileiro é forte, promove o",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_187",
                        number = "113",
                        title = "Canal do Boi",
                        category = "Agro & Rural",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/canal-do-boi-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/canal-do-boi.jpg",
                        description = "O Canal do Boi é um canal de televisão brasileiro dedicado ao setor agropecuário e à pecuária. Lançado em 1995, o canal é especial",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_185",
                        number = "114",
                        title = "Rede CNT",
                        category = "Agro & Rural",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/rede-cnt-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/rede-cnt.jpg",
                        description = "A Rede CNT, sigla para Central Nacional de Televisão, é uma emissora de televisão brasileira. Fundada em 1979, a rede tem cobertur",
                        quality = "HD"
                    ),
                )
            ),
            ChannelCategory(
                id = "cat_cultura",
                name = "Educação & Cultura",
                isLocked = false,
                channels = listOf(
                    Channel(
                        id = "ch_348",
                        number = "115",
                        title = "Canal Gov",
                        category = "Educação & Cultura",
                        streamUrl = "https://canalgov-stream.ebc.com.br/index.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/canal-gov.png",
                        description = "",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_347",
                        number = "116",
                        title = "Canal Educacao",
                        category = "Educação & Cultura",
                        streamUrl = "https://canaleducacao-stream.ebc.com.br/index.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/canal-educação.png",
                        description = "",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_346",
                        number = "117",
                        title = "Canal 25",
                        category = "Educação & Cultura",
                        streamUrl = "https://srv6.zcast.com.br/tvdagente/tvdagente/playlist.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/canal-25.png",
                        description = "",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_184",
                        number = "118",
                        title = "TV Brasil",
                        category = "Educação & Cultura",
                        streamUrl = "https://tvbrasil-stream.ebc.com.br/EBC_HD-avc1_2300000=10000.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/tv-brasil.jpg",
                        description = "A TV Brasil é uma emissora de televisão pública brasileira, operada pela Empresa Brasil de Comunicação (EBC). Criada em 2007, a TV",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_183",
                        number = "119",
                        title = "TV Cultura",
                        category = "Educação & Cultura",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/tv-cultura-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/tv-cultura.jpg",
                        description = "A TV Cultura é uma emissora pública brasileira conhecida por sua programação educativa e cultural. Fundada em 1969, oferece conteú",
                        quality = "FHD"
                    ),
                )
            ),
            ChannelCategory(
                id = "cat_religiosos",
                name = "Religiosos",
                isLocked = false,
                channels = listOf(
                    Channel(
                        id = "ch_355",
                        number = "120",
                        title = "TV Universal",
                        category = "Religiosos",
                        streamUrl = "https://644398c.ha.azioncdn.net/primary/tvuniversal_480p.sdp/playlist.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/tv-universal.png",
                        description = "",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_316",
                        number = "121",
                        title = "TV Aparecida",
                        category = "Religiosos",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/tv-aparecida-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/tv-aparecida.png",
                        description = "A TV Aparecida é uma emissora brasileira de inspiração católica, com programação voltada para a fé, cultura, entretenimento e educ",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_270",
                        number = "122",
                        title = "Rede Gospel",
                        category = "Religiosos",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/rede-gospel-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/rede-gospel.png",
                        description = "Bem-vindo à \"Rede Gospel\"! Este é o canal que traz inspiração e mensagens de fé para enriquecer sua vida espiritual. Aqui, mergu",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_267",
                        number = "123",
                        title = "Rede Mundial",
                        category = "Religiosos",
                        streamUrl = "https://68882bdaf156a.streamlock.net/impd/ngrp:impd_all/chunklist_w1146366542_b2691072.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/rede-mundial.jpg",
                        description = "Bem-vindo à \"Rede Mundial\"! Este é o canal que conecta você a uma variedade de conteúdos informativos, culturais e educativos. A",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_403",
                        number = "124",
                        title = "TV Unidos HD",
                        category = "Religiosos",
                        streamUrl = "https://video09.logicahost.com.br/tvunidoss/tvunidoss/playlist.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/tv-unidos-hd.jpeg",
                        description = "",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_402",
                        number = "125",
                        title = "TV Vip Gospel",
                        category = "Religiosos",
                        streamUrl = "https://video05.logicahost.com.br/vipgospel/vipgospel/playlist.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/tv-vip-gospel.jpeg",
                        description = "",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_343",
                        number = "126",
                        title = "Avivando Nacoes",
                        category = "Religiosos",
                        streamUrl = "https://video04.logicahost.com.br/tvavivandonacoes/tvavivandonacoes/playlist.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/avivando-nações.png",
                        description = "\"O canal Avivando Nações é um espaço dedicado a compartilhar a Palavra de Deus com poder e unção, levando mensagens de fé, espera",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_333",
                        number = "127",
                        title = "Rede Super",
                        category = "Religiosos",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/band-sp-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/rede-super.png",
                        description = "A&nbsp;Rede Super&nbsp;é uma emissora de televisão brasileira com sede em Belo Horizonte, Minas Gerais, fundada em 2000. Pertencen",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_317",
                        number = "128",
                        title = "TV Evangelizar",
                        category = "Religiosos",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/tv-evangelizar-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/tv-evangelizar.png",
                        description = "A TV Evangelizar é uma emissora brasileira de inspiração cristã, dedicada à evangelização e à promoção de valores como fé, esperan",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_265",
                        number = "129",
                        title = "TV Cancao Nova",
                        category = "Religiosos",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/cancao-nova-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/tv-canção-nova.jpg",
                        description = "Bem-vindo à \"Canção Nova\"! Este é o canal que ilumina corações e promove a espiritualidade por meio de mensagens religiosas, ora",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_261",
                        number = "130",
                        title = "RIT - Rede Internacional de Televisao",
                        category = "Religiosos",
                        streamUrl = "https://acesso.ecast.site:3648/live/ritlive.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/rit-rede-internacional-de-televisão.jpg",
                        description = "Bem-vindo à \"Rede Internacional de Televisão\" (RIT)! Este é o canal que conecta e inspira através de uma variedade de conteúdo e",
                        quality = "HD"
                    ),
                )
            ),
            ChannelCategory(
                id = "cat_variedades",
                name = "Variedades & Estilo",
                isLocked = false,
                channels = listOf(
                    Channel(
                        id = "ch_362",
                        number = "131",
                        title = "TV ZOOM",
                        category = "Variedades & Estilo",
                        streamUrl = "https://cdn.jmvstream.com/w/LVW-9730/LVW9730_LmUwslM8jt/playlist.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/tv-zoom.png",
                        description = "",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_360",
                        number = "132",
                        title = "TV Terceiro Anjo",
                        category = "Variedades & Estilo",
                        streamUrl = "https://streamer1.streamhost.org/salive/GMI3anjoh/playlist.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/tv-terceiro-anjo.png",
                        description = "",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_358",
                        number = "133",
                        title = "Rede Minas",
                        category = "Variedades & Estilo",
                        streamUrl = "https://8hzcavccys.zoeweb.tv/redeminas/ngrp:redeminas_all/chunklist_w1997760151_b2771072.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/rede-minas.png",
                        description = "",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_345",
                        number = "134",
                        title = "Amazon Sat HD",
                        category = "Variedades & Estilo",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/record-sp-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/amazon-sat-hd.png",
                        description = "O Amazon Sat é uma emissora de televisão regional brasileira, operada pelo Grupo Rede Amazônica, com sede em Manaus (AM) e concess",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_339",
                        number = "135",
                        title = "Astral TV",
                        category = "Variedades & Estilo",
                        streamUrl = "https://isaocorp.cloudecast.com/astraltv/playlist.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/astral-tv.png",
                        description = "A&nbsp;Astral TV&nbsp;é um canal de televisão brasileiro dedicado a temas como esoterismo, astrologia e espiritualidade. Fundado p",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_319",
                        number = "136",
                        title = "Miami Vice e Convidados",
                        category = "Variedades & Estilo",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/miami-vice-e-convidados/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/miami-vice-e-convidados.png",
                        description = "",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_376",
                        number = "137",
                        title = "L!ke TV",
                        category = "Variedades & Estilo",
                        streamUrl = "https://cnl-cl-samsung.otteravision.com/cnl/cl_h265/cl_h265.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/lke-tv.png",
                        description = "",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_251",
                        number = "138",
                        title = "GO Outside",
                        category = "Variedades & Estilo",
                        streamUrl = "https://cdn-ue1-prod.tsv2.amagi.tv/linear/outsidetv-outsidetvplus-oando/playlist.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/go-outside.jpg",
                        description = "Bem-vindo ao \"GO Outside\"! Este é o canal para os amantes da aventura e da vida ao ar livre. Aqui, exploramos o mundo da adrenal",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_237",
                        number = "139",
                        title = "Qwest TV",
                        category = "Variedades & Estilo",
                        streamUrl = "https://qwest-jazz-plex.amagi.tv/playlist.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/qwest-tv.png",
                        description = "A \"Qwest TV\" é um serviço de streaming de música fundado pelo renomado músico Quincy Jones. O canal oferece uma ampla variedade ",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_225",
                        number = "140",
                        title = "Revry Brasil",
                        category = "Variedades & Estilo",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/revry-br-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/revry-brasil.jpg",
                        description = "A \"Revry Brasil\" é um canal de televisão voltado para a comunidade LGBTQIA+ no Brasil. Ele oferece uma programação diversificada",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_352",
                        number = "141",
                        title = "Chroma TV",
                        category = "Variedades & Estilo",
                        streamUrl = "https://5c483b9d1019c.streamlock.net/8054/8054/playlist.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/chroma-tv.png",
                        description = "",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_327",
                        number = "142",
                        title = "Cine Catastrofe",
                        category = "Variedades & Estilo",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/cine-catastrofe/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/cine-catastrofe.png",
                        description = "",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_228",
                        number = "143",
                        title = "TV Loading",
                        category = "Variedades & Estilo",
                        streamUrl = "https://stmv1.srvif.com/loadingtv/loadingtv/playlist.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/tv-loading.jpeg",
                        description = "A \"TV Loading\" é um canal de televisão brasileiro que busca oferecer uma programação voltada para o público geek, gamer e otaku.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_226",
                        number = "144",
                        title = "Geekdot",
                        category = "Variedades & Estilo",
                        streamUrl = "https://stream.ichibantv.com:3764/hybrid/play.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/geekdot.jpg",
                        description = "Bem-vindo ao \"Geekdot\"! Aqui, exploramos um mundo de animações japonesas repletas de aventuras, emoções e personagens cativantes",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_207",
                        number = "145",
                        title = "Retro Cartoon Channel",
                        category = "Variedades & Estilo",
                        streamUrl = "https://stmv1.video.brstream.com.br/retrotv/retrotv/playlist.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/retro-cartoon-channel.jpeg",
                        description = "Bem-vindo ao \"Retrô Cartoon Channel\"! Aqui, voltamos no tempo para trazer de volta os desenhos animados clássicos que fizeram pa",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_386",
                        number = "146",
                        title = "Novelissima",
                        category = "Variedades & Estilo",
                        streamUrl = "https://cis-no-samsung.otteravision.com/cis/no/no_h265.m3u8?ROGERIOTORRES",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/novelissima.webp",
                        description = "",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_353",
                        number = "147",
                        title = "TV Camara",
                        category = "Variedades & Estilo",
                        streamUrl = "https://stream3.camara.gov.br/tv1/manifest.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/tv-camara.png",
                        description = "",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_322",
                        number = "148",
                        title = "Rede TV",
                        category = "Variedades & Estilo",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/rede-tv-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/rede-tv.png",
                        description = "",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_230",
                        number = "149",
                        title = "Demais TV",
                        category = "Variedades & Estilo",
                        streamUrl = "https://stmv1.samcast.com.br/demaistv6503/demaistv6503/playlist.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/demais-tv.jpg",
                        description = "Bem-vindo à \"Demais TV\"! Aqui, oferecemos uma mistura vibrante de entretenimento e variedades para alegrar seus dias. Nosso cana",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_224",
                        number = "150",
                        title = "PlayTV",
                        category = "Variedades & Estilo",
                        streamUrl = "https://isaocorp.cloudecast.com/playtv/tracks-v1a1/mono.m3u8",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/playtv.jpg",
                        description = "A \"PlayTV\" é um canal de televisão brasileiro que oferece uma programação diversificada voltada para o público jovem e adolescen",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_181",
                        number = "151",
                        title = "TV Galega Blumenau",
                        category = "Variedades & Estilo",
                        streamUrl = "https://cdn1.uniredes.com.br/hls/tv-galega-hd/index.m3u8?token=2d3232fe89d2b637a2093703",
                        logoUrl = "https://unitv.cdntv.com.br/uploads/tv_image/sm/tv-galega-blumenau.jpg",
                        description = "TV Galega , que atende a um moderno conceito de fazer televisão inspirado nos chamados “canais de acesso público”, leva ao ar repo",
                        quality = "HD"
                    ),
                )
            ),
            ChannelCategory(
                id = "cat_adultos",
                name = "Adultos (+18)",
                isLocked = true,
                channels = listOf(
                    Channel(
                        id = "ch_playboy",
                        number = "152",
                        title = "Playboy TV HD",
                        category = "Adultos (+18)",
                        streamUrl = "https://cdn.adultiptv.net/anal.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/en/thumb/8/8d/Playboy_TV_Logo.svg/320px-Playboy_TV_Logo.svg.png",
                        description = "Transmissão adulta premium 24h sem cortes com conteúdo exclusivo internacional.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_sexy_hot",
                        number = "153",
                        title = "Sexy Hot HD",
                        category = "Adultos (+18)",
                        streamUrl = "https://cdn.adultiptv.net/compilation.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/pt/thumb/a/a2/Sexy_Hot_logo.png/320px-Sexy_Hot_logo.png",
                        description = "O mais consagrado canal adulto do Brasil com produções e compilações exclusivas.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_venus",
                        number = "154",
                        title = "Venus HD",
                        category = "Adultos (+18)",
                        streamUrl = "https://cdn.adultiptv.net/bigtits.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c5/Canal_Combate_logo.png/320px-Canal_Combate_logo.png",
                        description = "Programação erótica e explícita internacional com as maiores estrelas do entretenimento adulto.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_sex_prive",
                        number = "155",
                        title = "Sex Privé HD",
                        category = "Adultos (+18)",
                        streamUrl = "https://cdn.adultiptv.net/blowjob.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/pt/thumb/d/d4/Sex_Priv%C3%A9_logo.png/320px-Sex_Priv%C3%A9_logo.png",
                        description = "Entretenimento adulto 24 horas no ar com o melhor do cinema explícito mundial.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_brazzers",
                        number = "156",
                        title = "Brazzers TV HD",
                        category = "Adultos (+18)",
                        streamUrl = "https://cdn.adultiptv.net/milf.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/82/Telecine_Premium_logo_2019.svg/320px-Telecine_Premium_logo_2019.svg.png",
                        description = "O canal mais assistido do mundo com superproduções adultas diárias em alta definição.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_redlight",
                        number = "157",
                        title = "Redlight TV HD",
                        category = "Adultos (+18)",
                        streamUrl = "https://cdn.adultiptv.net/hardcore.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/1/1b/Space_logo_2015.svg/320px-Space_logo_2015.svg.png",
                        description = "Ação extrema e filmes adultos hardcore com as modelos mais famosas da Europa e América.",
                        quality = "HD"
                    ),
                    Channel(
                        id = "ch_penthouse",
                        number = "158",
                        title = "Penthouse Gold HD",
                        category = "Adultos (+18)",
                        streamUrl = "https://cdn.adultiptv.net/latina.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/de/HBO_logo.svg/320px-HBO_logo.svg.png",
                        description = "O luxo e a sensualidade das maiores produções adultas com atrizes latinas consagradas.",
                        quality = "FHD"
                    ),
                    Channel(
                        id = "ch_private",
                        number = "159",
                        title = "Private TV HD",
                        category = "Adultos (+18)",
                        streamUrl = "https://cdn.adultiptv.net/amateur.m3u8",
                        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/9/91/TNT_Logo_2016.svg/320px-TNT_Logo_2016.svg.png",
                        description = "Clássicos do cinema erótico europeu e lançamentos das produtoras mais premiadas.",
                        quality = "HD"
                    ),
                )
            ),
        )
    }
}
