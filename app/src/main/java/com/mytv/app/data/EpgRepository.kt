package com.mytv.app.data

import com.mytv.app.model.Channel
import com.mytv.app.model.ChannelCategory
import com.mytv.app.model.EpgProgram
import java.util.Calendar
import java.util.TimeZone

object EpgRepository {

    private val timeZone = TimeZone.getTimeZone("America/Sao_Paulo")

    // Grade de horários padrão em minutos a partir da meia-noite (00:00)
    // 00:00(0), 02:00(120), 06:00(360), 08:30(510), 10:00(600), 12:00(720), 13:30(810), 15:00(900), 17:00(1020), 18:30(1110), 20:00(1200), 21:30(1290), 23:00(1380), 24:00(1440)
    private val timeSlots = listOf(
        Pair(0, 120),     // 00:00 - 02:00
        Pair(120, 360),   // 02:00 - 06:00
        Pair(360, 510),   // 06:00 - 08:30
        Pair(510, 600),   // 08:30 - 10:00
        Pair(600, 720),   // 10:00 - 12:00
        Pair(720, 810),   // 12:00 - 13:30
        Pair(810, 900),   // 13:30 - 15:00
        Pair(900, 1020),  // 15:00 - 17:00
        Pair(1020, 1110), // 17:00 - 18:30
        Pair(1110, 1200), // 18:30 - 20:00
        Pair(1200, 1290), // 20:00 - 21:30
        Pair(1290, 1380), // 21:30 - 23:00
        Pair(1380, 1440)  // 23:00 - 00:00
    )

    fun enrichChannel(channel: Channel): Channel {
        val (current, next) = getProgramsForChannel(channel)
        channel.currentProgram = current
        channel.nextProgram = next
        return channel
    }

    fun enrichCategories(categories: List<ChannelCategory>): List<ChannelCategory> {
        return categories.map { cat ->
            val enrichedChs = cat.channels.map { ch ->
                enrichChannel(ch)
            }
            cat.copy(channels = enrichedChs)
        }
    }

    private fun getProgramsForChannel(channel: Channel): Pair<EpgProgram, EpgProgram?> {
        val cal = Calendar.getInstance(timeZone)
        val currentMinutesOfDay = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)

        // Encontra o slot atual
        var currentSlotIdx = timeSlots.indexOfFirst { (start, end) ->
            currentMinutesOfDay in start until end
        }
        if (currentSlotIdx == -1) currentSlotIdx = 0

        val nextSlotIdx = (currentSlotIdx + 1) % timeSlots.size

        val currentSlot = timeSlots[currentSlotIdx]
        val nextSlot = timeSlots[nextSlotIdx]

        val currentStartCal = cal.clone() as Calendar
        currentStartCal.set(Calendar.HOUR_OF_DAY, currentSlot.first / 60)
        currentStartCal.set(Calendar.MINUTE, currentSlot.first % 60)
        currentStartCal.set(Calendar.SECOND, 0)
        currentStartCal.set(Calendar.MILLISECOND, 0)

        val currentEndCal = cal.clone() as Calendar
        currentEndCal.set(Calendar.HOUR_OF_DAY, currentSlot.second / 60)
        currentEndCal.set(Calendar.MINUTE, currentSlot.second % 60)
        currentEndCal.set(Calendar.SECOND, 0)
        currentEndCal.set(Calendar.MILLISECOND, 0)

        val nextEndCal = cal.clone() as Calendar
        nextEndCal.set(Calendar.HOUR_OF_DAY, nextSlot.second / 60)
        nextEndCal.set(Calendar.MINUTE, nextSlot.second % 60)
        nextEndCal.set(Calendar.SECOND, 0)
        nextEndCal.set(Calendar.MILLISECOND, 0)

        val curProgramMeta = getProgramMeta(channel, currentSlotIdx)
        val nextProgramMeta = getProgramMeta(channel, nextSlotIdx)

        val currentProgram = EpgProgram(
            title = curProgramMeta.first,
            category = channel.category,
            startTime = formatTime(currentSlot.first),
            endTime = formatTime(currentSlot.second),
            startTimestamp = currentStartCal.timeInMillis,
            endTimestamp = currentEndCal.timeInMillis,
            synopsis = curProgramMeta.second,
            rating = getRatingForCategory(channel.category)
        )

        val nextProgram = EpgProgram(
            title = nextProgramMeta.first,
            category = channel.category,
            startTime = formatTime(nextSlot.first),
            endTime = formatTime(nextSlot.second),
            startTimestamp = currentEndCal.timeInMillis,
            endTimestamp = nextEndCal.timeInMillis,
            synopsis = nextProgramMeta.second,
            rating = getRatingForCategory(channel.category)
        )

        return Pair(currentProgram, nextProgram)
    }

    private fun formatTime(minutes: Int): String {
        val h = (minutes / 60) % 24
        val m = minutes % 60
        return String.format("%02d:%02d", h, m)
    }

    private fun getRatingForCategory(cat: String): String {
        return when {
            cat.contains("Adulto", ignoreCase = true) -> "18"
            cat.contains("Kids", ignoreCase = true) || cat.contains("Infantil", ignoreCase = true) -> "L"
            cat.contains("Notícia", ignoreCase = true) -> "10"
            cat.contains("Telecine", ignoreCase = true) || cat.contains("HBO", ignoreCase = true) -> "14"
            cat.contains("Combate", ignoreCase = true) -> "14"
            else -> "12"
        }
    }

    private fun getProgramMeta(channel: Channel, slotIdx: Int): Pair<String, String> {
        val titleLower = channel.title.lowercase()
        val catLower = channel.category.lowercase()

        // 0. Fórmula 1 & Velocidade
        if (catLower.contains("f1") || catLower.contains("fórmula") || catLower.contains("formula") || titleLower.contains("f1") || titleLower.contains("motorvision") || titleLower.contains("pit lane")) {
            val f1Grid = listOf(
                Pair("F1 Retrô: Vitórias Históricas", "Reviva os Grandes Prêmios inesquecíveis e duelos lendários."),
                Pair("Fórmula 1 Madrugada: Compacto do GP", "Os melhores momentos e ultrapassagens da última corrida."),
                Pair("F1 Aquecimento & Bastidores do Paddock", "Notícias das equipes, novidades de motores e asas móveis."),
                Pair("Treino Livre 1 (FP1) - Transmissão Ao Vivo", "Primeira sessão de testes de pista com telemetria."),
                Pair("Treino Livre 2 (FP2) - Análise de Pneus", "Pilotos testam ritmo de corrida e desgaste dos compostos."),
                Pair("Fórmula 2: Treino de Classificação", "Jovens talentos buscam vaga no grid da categoria de acesso."),
                Pair("Treino Livre 3 (FP3) - Acerto Final", "Última oportunidade de ajuste dos carros antes do qualifying."),
                Pair("Classificação Oficial (Pole Position Ao Vivo)", "Q1, Q2 e Q3 na disputa pelos milésimos da primeira fila."),
                Pair("Sprint Shootout & Corrida Sprint Ao Vivo", "Pontos decisivos na corrida curta de sábado."),
                Pair("Desfile de Pilotos & Hino Nacional", "Cerimônia de abertura e volta de honra no circuito."),
                Pair("Grande Prêmio de Fórmula 1: Corrida Ao Vivo", "Largada oficial, pit stops em tempo real e bandeira quadriculada."),
                Pair("Cerimônia de Pódio & Entrevistas dos Vencedores", "Festa com champanhe e declarações exclusivas dos pilotos."),
                Pair("Fórmula 1 Pós-Corrida: Análise Técnica", "Especialistas debatem táticas de pit stop, undercut e pneus.")
            )
            return f1Grid[slotIdx % f1Grid.size]
        }

        // 1. Cazé TV
        if (titleLower.contains("caze")) {
            val cazeGrid = listOf(
                Pair("CazéTV Madrugada: Melhores Momentos", "Os lances mais marcantes e cortes exclusivos com Casimiro."),
                Pair("CazéTV Highlights & Resenha", "Resenha esportiva com gols, memes e bastidores."),
                Pair("Café com Cazé: Notícias do Futebol", "Análise matinal das principais notícias do futebol mundial."),
                Pair("Aquecimento CazéTV Ao Vivo", "Preparação e pré-jogo com a equipe de transmissão."),
                Pair("Copa do Brasil & Libertadores Especial", "Cobertura das maiores competições esportivas da América Latina."),
                Pair("CazéTV Ao Vivo: Debate da Rodada", "Debate com a bancada mais irreverente da internet."),
                Pair("Casimiro Reage: Fails & Golaços", "As reações mais engraçadas aos gols da semana."),
                Pair("Transmissão Ao Vivo CazéTV", "Acompanhe as partidas e transmissões de alta definição."),
                Pair("Pré-Jogo Especial Brasileirão", "Entrevistas exclusivas e escalações em tempo real."),
                Pair("CazéTV Noturno: Giro Internacional", "Champions League, futebol europeu e campeonatos internacionais."),
                Pair("Resenha Pós-Jogo CazéTV", "Análise detalhada de todos os resultados da rodada."),
                Pair("CazéTV Noite: Cortes & Humor", "Os melhores momentos do dia com muita descontração."),
                Pair("CazéTV Madrugada Sem Limites", "Entrevistas com atletas e personalidades da internet.")
            )
            return cazeGrid[slotIdx % cazeGrid.size]
        }

        // 2. Globo
        if (catLower.contains("globo") || titleLower.contains("globo")) {
            val globoGrid = listOf(
                Pair("Jornal da Globo / Sessão Comédia", "As principais notícias do fechamento do dia."),
                Pair("Hora Um / Corujão", "Cinema na madrugada e o início da cobertura jornalística."),
                Pair("Bom Dia Brasil - Ao Vivo", "Telejornal matinal com correspondentes em todo o país."),
                Pair("Encontro com Patrícia Poeta", "Música, debate e informação das manhãs."),
                Pair("Mais Você com Ana Maria Braga", "Culinária, reportagens especiais e entrevistas."),
                Pair("Praça TV - 1ª Edição", "Notícias locais da sua região com foco na comunidade."),
                Pair("Globo Esporte / Jornal Hoje", "A cobertura esportiva e os destaques do Brasil."),
                Pair("Sessão da Tarde Especial", "Grandes sucessos do cinema para toda a família."),
                Pair("Vale a Pena Ver de Novo", "Reapresentação dos maiores sucessos da teledramaturgia."),
                Pair("Novela das Seis / Praça TV 2ª", "Emoção na faixa das 18h e o jornal local noturno."),
                Pair("Jornal Nacional - Ao Vivo", "O telejornal de maior audiência do país em alta definição."),
                Pair("Novela das Nove (Horário Nobre)", "A principal produção dramática da TV brasileira."),
                Pair("Futebol / Linha de Shows / BBB", "Transmissões esportivas decisivas e grandes realities.")
            )
            return globoGrid[slotIdx % globoGrid.size]
        }

        // 3. SBT
        if (catLower.contains("sbt") || titleLower.contains("sbt")) {
            val sbtGrid = listOf(
                Pair("Operação Mesquita / SBT News", "Reportagens curiosas e jornalismo na madrugada."),
                Pair("SBT PodNight / Notícias", "Os podcasts de maior sucesso na telinha."),
                Pair("Primeiro Impacto - Ao Vivo", "Jornalismo dinâmico e cobertura policial da manhã."),
                Pair("Chega Mais - Revista Eletrônica", "Entretenimento, variedades e prestação de serviço."),
                Pair("Notícias do SBT e Desenhos", "Informação e diversão para as manhãs."),
                Pair("Tá na Hora - Notícias Locais", "O que acontece na sua cidade no meio do dia."),
                Pair("Fofocalizando - Ao Vivo", "Os bastidores do mundo das celebridades e fofocas."),
                Pair("Novelas da Tarde SBT", "Grandes dramas românticos internacionais."),
                Pair("Tá na Hora com Datena e Equipe", "Cobertura ao vivo dos fatos que marcam a tarde."),
                Pair("SBT Brasil - Ao Vivo", "Telejornal noturno com as principais notícias do mundo."),
                Pair("A Infância de Romeu e Julieta", "Novela infanto-juvenil para reunir toda a família."),
                Pair("Programa do Ratinho - Ao Vivo", "Jogos, música, atrações populares e humor."),
                Pair("The Noite com Danilo Gentili", "O talk show mais premiado da televisão brasileira.")
            )
            return sbtGrid[slotIdx % sbtGrid.size]
        }

        // 4. Record
        if (catLower.contains("record") || titleLower.contains("record")) {
            val recordGrid = listOf(
                Pair("Inteligência e Fé / Programação", "Conteúdo reflexivo da madrugada."),
                Pair("Igreja Universal / Balanço Madrugada", "Abertura dos trabalhos jornalísticos."),
                Pair("Balanço Geral Manhã - Ao Vivo", "Notícias do trânsito e ocorrências do amanhecer."),
                Pair("Fala Brasil - Ao Vivo", "Informações matinais com credibilidade e agilidade."),
                Pair("Hoje em Dia - Revista Matinal", "Moda, gastronomia, fofocas e entretenimento."),
                Pair("Balanço Geral Tarde com Gottino", "Notícias quentes e o quadro Hora da Venenosa."),
                Pair("A Hora da Venenosa Especial", "As fofocas mais quentes com Fabiola Reipert."),
                Pair("Cidade Alerta - Ao Vivo", "Cobertura investigativa ao vivo com Luiz Bacci."),
                Pair("Cidade Alerta - Edição Noturna", "Aprofundamento dos casos mais complexos do país."),
                Pair("Jornal da Record - Ao Vivo", "Noticiário nacional e internacional em alta definição."),
                Pair("Novela Bíblica: Reis / Força de Mulher", "Superprodução dramática com qualidade de cinema."),
                Pair("A Fazenda 24H: Transmissão da Sede", "Conflitos, votações e dinâmicas dos peões ao vivo."),
                Pair("Câmera Record / Chicago P.D.", "Jornalismo investigativo premiado e séries de ação.")
            )
            return recordGrid[slotIdx % recordGrid.size]
        }

        // 5. Premiere & Combate
        if (catLower.contains("premiere") || catLower.contains("combate") || titleLower.contains("prem") || titleLower.contains("combate")) {
            val premGrid = listOf(
                Pair("Gols da Rodada & Melhores Lances", "Todos os gols dos jogos de ontem com análise."),
                Pair("Arquivo Premiere: Finais Históricas", "Relembre jogos inesquecíveis da história do futebol."),
                Pair("Aquecimento Brasileirão Série A", "Bastidores dos estádios e chegada dos ônibus."),
                Pair("Giro dos Clubes: Treinos & Tática", "A rotina dos grandes clubes de futebol do Brasil."),
                Pair("Painel Tático Premiere", "Especialistas debatem formações e estratégias de jogo."),
                Pair("Campeonato Brasileiro Série B Ao Vivo", "Disputa acirrada pelo acesso à elite do futebol."),
                Pair("Pré-Jogo Exclusivo com Narradores", "Entrevistas à beira do gramado e escalações oficiais."),
                Pair("Campeonato Brasileiro Série A (Ao Vivo)", "Transmissão principal da rodada com narração premium."),
                Pair("Campeonato Brasileiro Série A (Ao Vivo)", "Jogos simultâneos com mosaico e câmeras exclusivas."),
                Pair("Pós-Jogo: Coletivas de Imprensa", "Técnicos e jogadores comentam os resultados."),
                Pair("UFC Fight Night: Card Preliminar", "O octógono pega fogo com as promessas das artes marciais."),
                Pair("UFC Fight Night: Card Principal Ao Vivo", "Grandes lutas mundiais com disputa de cinturão."),
                Pair("Resenha de Luta & Nocautes da Noite", "Os golpes mais espetaculares e entrevistas dos campeões.")
            )
            return premGrid[slotIdx % premGrid.size]
        }

        // 6. Rede Telecine
        if (catLower.contains("telecine") || titleLower.contains("telecine")) {
            val tcGrid = listOf(
                Pair("Sessão Madrugada: Suspense & Mistério", "Grandes filmes para quem gosta de roer as unhas."),
                Pair("Cine Madrugada: Clássicos Inesquecíveis", "Obras consagradas do cinema mundial."),
                Pair("Telecine Cult: Festivais Internacionais", "Cinema autoral e obras premiadas em Cannes e Veneza."),
                Pair("Telecine Touch: Histórias Inspiradoras", "Dramas emocionantes e filmes baseados em fatos reais."),
                Pair("Telecine Pipoca: Diversão em Família", "Comédias leves e animações 100% dubladas."),
                Pair("Telecine Fun: Gargalhadas Garantidas", "As comédias mais engraçadas de Hollywood."),
                Pair("Telecine Action: Perseguição Implacável", "Adrenalina pura com perseguições e tiros."),
                Pair("Sessão Superestreia Telecine", "Filmes campeões de bilheteria recém-saídos dos cinemas."),
                Pair("Telecine Premium: O Grande Filme da Noite", "A maior produção de Hollywood na sua tela."),
                Pair("Festival Telecine: Universo Marvel", "Ação eletrizante com os heróis mais amados do mundo."),
                Pair("Telecine Action: Heróis Sem Limites", "Blockbuster de ficção científica e aventura."),
                Pair("Cine Terror: A Hora do Pesadelo", "O melhor do gênero de terror contemporâneo."),
                Pair("Telecine Pós-Sessão: Bastidores do Cinema", "Making of e segredos das maiores produções.")
            )
            return tcGrid[slotIdx % tcGrid.size]
        }

        // 7. HBO & Warner
        if (catLower.contains("hbo") || catLower.contains("warner") || titleLower.contains("hbo") || titleLower.contains("warner")) {
            val hboGrid = listOf(
                Pair("The Sopranos / The Wire", "Grandes marcos da era de ouro das séries HBO."),
                Pair("Séries Clássicas Warner", "Comédias que marcaram gerações na televisão."),
                Pair("Documentários Exclusivos HBO", "Grandes investigações e biografias cinematográficas."),
                Pair("Sessão Matinal: Animações DC Comics", "Batman, Superman e a Liga da Justiça em ação."),
                Pair("Two and a Half Men / The Big Bang Theory", "Maratona das comédias mais premiadas da TV."),
                Pair("Friends: Os Melhores Episódios", "Reviva os momentos mais hilários dos amigos em NY."),
                Pair("Universo DC: Filmes em Alta Definição", "Super-heróis com efeitos visuais de última geração."),
                Pair("House of the Dragon - Temporada Épica", "A guerra civil Targaryen pelo Trono de Ferro."),
                Pair("The Last of Us - Episódio Especial", "A jornada emocionante em um mundo pós-apocalíptico."),
                Pair("Blockbuster HBO: Estreia de Sábado", "O filme mais assistido do ano na TV por assinatura."),
                Pair("Game of Thrones: Batalhas Inesquecíveis", "A saga de Westeros com qualidade máxima de imagem."),
                Pair("True Detective: Casos Criminais", "Suspense policial e investigações profundas."),
                Pair("Last Week Tonight com John Oliver", "Humor inteligente e sátira com notícias da semana.")
            )
            return hboGrid[slotIdx % hboGrid.size]
        }

        // 8. Reality Shows 24H
        if (catLower.contains("reality") || titleLower.contains("fazenda") || titleLower.contains("bbb")) {
            val realityGrid = listOf(
                Pair("Dormitório dos Peões: Madrugada Ao Vivo", "Câmeras noturnas registram o sono e conversas na madrugada."),
                Pair("Câmera do Quarto: Silêncio na Sede", "Acompanhe as últimas horas da noite dentro da sede."),
                Pair("Despertador dos Peões & Café da Manhã", "Os participantes acordam e começam a organizar a casa."),
                Pair("Trato com os Animais (Ao Vivo)", "Vaca, cavalos, ovelhas e aves sendo alimentados pelos peões."),
                Pair("Área Externa & Piscina Ao Vivo", "Conversas estratégicas e fofocas no sol."),
                Pair("Almoço na Sede: Convivência e Tensão", "Os participantes preparam o almoço e discutem regras."),
                Pair("Treino na Academia & Fofocas da Tarde", "Comentários sobre votos e alianças para a Roça."),
                Pair("Dinâmica Surpresa / Preparação da Noite", "A produção envia avisos e materiais para a prova."),
                Pair("Jantar na Casa: Estratégias de Votação", "Reunião de grupos para definir quem vai para a Roça."),
                Pair("Programa Ao Vivo com Apresentador", "Votação aberta, Prova do Fazendeiro e eliminação ao vivo."),
                Pair("Festa da Semana Ao Vivo (Sem Cortes)", "Música alta, figurinos especiais e romance entre os confinados."),
                Pair("Madrugada Pós-Festa: Treta na Sede", "As discussões e discussões pós-votação."),
                Pair("Câmera Exclusiva da Baia (24H)", "Os peões que dormem na baia preparam suas camas.")
            )
            return realityGrid[slotIdx % realityGrid.size]
        }

        // 9. Documentários & History
        if (catLower.contains("doc") || titleLower.contains("history") || titleLower.contains("discovery")) {
            val docGrid = listOf(
                Pair("Mistérios Subaquáticos: Oceanos Secretos", "Mergulhadores revelam relíquias no fundo do mar."),
                Pair("Grandes Construções da Antiguidade", "Como as pirâmides e templos romanos foram erguidos."),
                Pair("Planeta Selvagem: Predadores da Savana", "Leões, leopardos e crocodilos em seu habitat natural."),
                Pair("Ouro Maldito: Corrida no Alasca", "Garimpeiros enfrentam temperaturas extremas pelo ouro."),
                Pair("Trato Feito: Relíquias de Guerra", "Rick e Chumlee negociam armas e medalhas históricas."),
                Pair("Alienígenas do Passado: Novas Descobertas", "Teorias sobre a intervenção extraterrestre na história."),
                Pair("Guerra das Ferramentas & Oficinas", "Mecânicos e artesãos disputam o melhor projeto."),
                Pair("Segredos do Império Romano", "Documentário em 4K sobre as legiões e imperadores."),
                Pair("Desastres da Engenharia", "Erros de cálculo que levaram a colapsos históricos."),
                Pair("Segredos da Segunda Guerra Mundial", "Imagens raras e colorizadas dos combates na Europa."),
                Pair("O Universo: Buracos Negros e Estrelas", "Física quântica e astronomia para todos os públicos."),
                Pair("Invenções que Mudaram o Mundo", "Como a eletricidade, o motor e o chip transformaram o homem."),
                Pair("Expedição Planeta Terra 4K", "Imagens aéreas de tirar o fôlego das maiores florestas.")
            )
            return docGrid[slotIdx % docGrid.size]
        }

        // 10. Infantil & Kids
        if (catLower.contains("kids") || catLower.contains("infantil") || titleLower.contains("cartoon")) {
            val kidsGrid = listOf(
                Pair("Hora de Dormir: Músicas Suaves", "Canções relaxantes para os pequeninos descansarem."),
                Pair("Aventuras no Espaço dos Desenhos", "Viagens intergalácticas com personagens divertidos."),
                Pair("Patrulha Canina: Resgates Épicos", "Os filhotes mais corajosos em missões de salvamento."),
                Pair("Peppa Pig e Amigos: Manhã Feliz", "Histórias do cotidiano com muita alegria e lições."),
                Pair("O Incrível Mundo de Gumball", "A rotina maluca de Gumball e Darwin na cidade de Elmore."),
                Pair("Jovens Titãs em Ação: Superpoderes", "Robin, Ravena, Ciborgue e Estelar em confusões hilárias."),
                Pair("Cine Cartoon: O Grande Filme Infantil", "Longa-metragem premiado de animação com humor."),
                Pair("Ben 10: Omniverse em Ação", "Batalhas com alienígenas para salvar o universo."),
                Pair("Ursos Sem Curso: Aventuras na Cidade", "Pardo, Panda e Polar tentando se enturmar."),
                Pair("Show da Luna: Por Que as Coisas São Assim?", "Curiosidades científicas explicadas para crianças."),
                Pair("Aventuras de Tom & Jerry", "O gato e o rato mais famosos da TV em perseguições eternas."),
                Pair("Steven Universo: As Joias de Cristal", "Amizade, magia e canções inesquecíveis."),
                Pair("Desenhos Clássicos da Madrugada", "Looney Tunes, Pica-Pau e Pernalonga.")
            )
            return kidsGrid[slotIdx % kidsGrid.size]
        }

        // 11. Adultos (+18)
        if (catLower.contains("adulto") || titleLower.contains("playboy") || titleLower.contains("sexy") || titleLower.contains("brazzers")) {
            val adultGrid = listOf(
                Pair("Sessão Madrugada Sem Cortes (+18)", "As mais belas modelos internacionais em produções explícitas."),
                Pair("Superprodução Exclusiva da Madrugada", "Conteúdo adulto premium em ultra alta definição."),
                Pair("Madrugada Quente HD", "Produções europeias e americanas sem censura."),
                Pair("Sessão Especial: As Estrelas do Mês", "Compilação das atrizes mais premiadas do entretenimento adulto."),
                Pair("Especial Dia: Sensualidade & Prazer", "Cenas eletrizantes com direção premiada."),
                Pair("Supercombo Adulto HD", "Grandes clássicos das maiores produtoras de filmes adultos."),
                Pair("Tarde Proibida (+18)", "Entretenimento erótico 24 horas no ar."),
                Pair("Encontros Sensuais em Alta Definição", "O melhor do cinema erótico contemporâneo."),
                Pair("Sessão Prime Time Adulto (+18)", "As maiores novidades da indústria mundial para adultos."),
                Pair("Superestreia Adulta Exclusiva", "Lançamento mensal com elenco de superestrelas."),
                Pair("Hardcore Sem Censura HD", "Ação explícita intensa das maiores franquias mundiais."),
                Pair("Noite Selvagem (+18)", "Filmes premiados nos principais festivais adultos."),
                Pair("Festival das Modelos Internacionais", "Entrevistas, bastidores e cenas completas.")
            )
            return adultGrid[slotIdx % adultGrid.size]
        }

        // Padrão Geral (Notícias / Variedades / Agro / Cultura / Músicas)
        val defaultGrid = listOf(
            Pair("Programação da Madrugada", "Acompanhe a transmissão noturna do canal em alta definição."),
            Pair("Giro de Notícias & Clima", "Abertura da programação diária com as primeiras informações."),
            Pair("Revista Matinal de Conteúdo", "Entrevistas, convidados especiais e debates do dia a dia."),
            Pair("Painel de Especialistas", "Análise aprofundada dos principais acontecimentos."),
            Pair("Edição do Meio-Dia", "Resumo das notícias e destaques mais importantes da manhã."),
            Pair("Tarde em Foco", "Cobertura especial, reportagens exclusivas e prestação de serviço."),
            Pair("Magazine Cultural & Variedades", "O melhor da cultura, tendências e atualidades."),
            Pair("Transmissão Principal da Tarde", "Programação especial voltada ao público do canal."),
            Pair("Jornal da Noite em Alta Definição", "O fechamento dos fatos mais relevantes com comentaristas."),
            Pair("Especial Nobre do Canal", "A principal produção do canal no horário de maior audiência."),
            Pair("Grandes Histórias & Debates", "Documentários e entrevistas com grandes personalidades."),
            Pair("Noite em Revista", "O resumo dos momentos mais marcantes do dia."),
            Pair("Encerramento da Grade Diária", "Os melhores momentos e a grade de amanhã.")
        )
        return defaultGrid[slotIdx % defaultGrid.size]
    }
}
