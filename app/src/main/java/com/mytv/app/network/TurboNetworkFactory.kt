package com.mytv.app.network

import android.content.Context
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.okhttp.OkHttpDataSource
import com.mytv.app.utils.AppSettings
import okhttp3.ConnectionPool
import okhttp3.Dns
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.dnsoverhttps.DnsOverHttps
import java.net.InetAddress
import java.util.concurrent.TimeUnit

object TurboNetworkFactory {

    private const val USER_AGENT = "Mozilla/5.0 (Linux; Android 10; TV) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36 IPTVSmartersPro/3.1.5 VLC/3.0.18"
    private var cachedOkHttpClient: OkHttpClient? = null
    private var lastDohState: Boolean? = null

    // DNS STB dedicados para IPTV (Netflix, TV Antiga, V3, V4)
    val STB_DNS_SERVERS = listOf(
        "173.208.207.156", // DNS STB (Netflix)
        "173.208.207.158", // DNS STB TV Antiga
        "178.156.242.193", // DNS STB V3
        "173.208.207.157"  // DNS STB V4
    )

    // Resolvedor Turbo com IP Pinning instantâneo (0ms) e Fallback nos DNS STB
    class TurboStbDns(private val fallbackDns: Dns) : Dns {
        private val staticHosts: Map<String, List<InetAddress>> = mapOf(
            "nslistam3u6olwz.xyz" to listOf(
                InetAddress.getByName("104.21.64.142"),
                InetAddress.getByName("172.67.151.145")
            ),
            "e.nslistam3u6olwz.xyz" to listOf(
                InetAddress.getByName("104.21.64.142"),
                InetAddress.getByName("172.67.151.145")
            ),
            "suportepriorizado.xyz" to listOf(
                InetAddress.getByName("104.21.15.216"),
                InetAddress.getByName("172.67.164.129")
            ),
            "cdn9.my" to listOf(
                InetAddress.getByName("172.67.211.148"),
                InetAddress.getByName("104.21.67.27")
            ),
            "e.cdn9.my" to listOf(
                InetAddress.getByName("172.67.211.148"),
                InetAddress.getByName("104.21.67.27")
            )
        )

        override fun lookup(hostname: String): List<InetAddress> {
            val key = hostname.lowercase()
            staticHosts[key]?.let { return it }
            return try {
                fallbackDns.lookup(hostname)
            } catch (_: Exception) {
                Dns.SYSTEM.lookup(hostname)
            }
        }
    }

    @Synchronized
    fun getOkHttpClient(context: Context): OkHttpClient {
        val useTurboDoh = AppSettings.isTurboDohEnabled(context)
        if (cachedOkHttpClient != null && lastDohState == useTurboDoh) {
            return cachedOkHttpClient!!
        }

        // Pool de conexões otimizado para streaming contínuo sem reabertura de sockets
        val pool = ConnectionPool(16, 5, TimeUnit.MINUTES)

        // Timeouts rápidos (2.5s conexão / 4.0s leitura) para failover instantâneo quando canal estiver instável
        val clientBuilder = OkHttpClient.Builder()
            .connectionPool(pool)
            .connectTimeout(2500, TimeUnit.MILLISECONDS)
            .readTimeout(4000, TimeUnit.MILLISECONDS)
            .writeTimeout(3000, TimeUnit.MILLISECONDS)
            .retryOnConnectionFailure(true)
            .followRedirects(true)
            .followSslRedirects(true)

        try {
            // Bootstrap com servidores DNS STB oficiais + Anycast Cloudflare/Google
            val bootstrapHosts = mutableListOf<InetAddress>()
            for (ip in STB_DNS_SERVERS) {
                try { bootstrapHosts.add(InetAddress.getByName(ip)) } catch (_: Exception) {}
            }
            bootstrapHosts.add(InetAddress.getByName("1.1.1.1"))
            bootstrapHosts.add(InetAddress.getByName("1.0.0.1"))
            bootstrapHosts.add(InetAddress.getByName("8.8.8.8"))
            bootstrapHosts.add(InetAddress.getByName("8.8.4.4"))

            val bootstrapClient = OkHttpClient.Builder()
                .connectTimeout(3000, TimeUnit.MILLISECONDS)
                .readTimeout(3000, TimeUnit.MILLISECONDS)
                .build()

            val cloudflareDoh = DnsOverHttps.Builder()
                .client(bootstrapClient)
                .url("https://1.1.1.1/dns-query".toHttpUrl())
                .bootstrapDnsHosts(bootstrapHosts)
                .includeIPv6(false) // TV Boxes operam com máxima estabilidade em IPv4
                .post(true)
                .build()

            clientBuilder.dns(TurboStbDns(cloudflareDoh))
            android.util.Log.i("WDTECH_TURBO", "Turbo Network: DNS STB + DoH ativado com sucesso!")
        } catch (e: Exception) {
            android.util.Log.w("WDTECH_TURBO", "Falha ao inicializar DoH, usando TurboStbDns(Dns.SYSTEM): ${e.message}")
            clientBuilder.dns(TurboStbDns(Dns.SYSTEM))
        }

        val client = clientBuilder.build()
        cachedOkHttpClient = client
        lastDohState = useTurboDoh
        return client
    }

    fun createDataSourceFactory(context: Context): DataSource.Factory {
        val client = getOkHttpClient(context)
        val okHttpFactory = OkHttpDataSource.Factory(client)
            .setUserAgent(USER_AGENT)
            .setDefaultRequestProperties(
                mapOf(
                    "Accept" to "*/*",
                    "Connection" to "keep-alive",
                    "Icy-MetaData" to "1"
                )
            )

        return DefaultDataSource.Factory(context, okHttpFactory)
    }

    fun invalidateClient() {
        cachedOkHttpClient = null
        lastDohState = null
    }
}
