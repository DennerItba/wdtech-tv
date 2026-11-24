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

    @Synchronized
    fun getOkHttpClient(context: Context): OkHttpClient {
        val useTurboDoh = AppSettings.isTurboDohEnabled(context)
        if (cachedOkHttpClient != null && lastDohState == useTurboDoh) {
            return cachedOkHttpClient!!
        }

        // Pool de conexões otimizado para streaming contínuo sem reabertura de sockets
        val pool = ConnectionPool(16, 5, TimeUnit.MINUTES)

        val clientBuilder = OkHttpClient.Builder()
            .connectionPool(pool)
            .connectTimeout(15000, TimeUnit.MILLISECONDS) // 15s para conexões internacionais/CDN
            .readTimeout(25000, TimeUnit.MILLISECONDS)    // 25s para download de chunks HLS sem aborto precoce
            .writeTimeout(15000, TimeUnit.MILLISECONDS)
            .retryOnConnectionFailure(true)
            .followRedirects(true)
            .followSslRedirects(true)

        if (useTurboDoh) {
            try {
                // Bootstrap com resolução direta de IPs para evitar dependência do DNS local do ISP
                val bootstrapClient = OkHttpClient.Builder()
                    .connectTimeout(5000, TimeUnit.MILLISECONDS)
                    .readTimeout(5000, TimeUnit.MILLISECONDS)
                    .build()

                val cloudflareDoh = DnsOverHttps.Builder()
                    .client(bootstrapClient)
                    .url("https://1.1.1.1/dns-query".toHttpUrl())
                    .bootstrapDnsHosts(
                        InetAddress.getByName("1.1.1.1"),
                        InetAddress.getByName("1.0.0.1"),
                        InetAddress.getByName("8.8.8.8"),
                        InetAddress.getByName("8.8.4.4")
                    )
                    .includeIPv6(false) // TV Boxes geralmente possuem melhor performance em IPv4
                    .post(true)
                    .build()

                clientBuilder.dns(cloudflareDoh)
                android.util.Log.i("WDTECH_TURBO", "Turbo Network: DNS-over-HTTPS (Cloudflare 1.1.1.1) ativado com sucesso!")
            } catch (e: Exception) {
                android.util.Log.w("WDTECH_TURBO", "Falha ao inicializar DoH, usando Dns.SYSTEM: ${e.message}")
                clientBuilder.dns(Dns.SYSTEM)
            }
        } else {
            clientBuilder.dns(Dns.SYSTEM)
            android.util.Log.i("WDTECH_TURBO", "Turbo Network: DNS nativo do sistema ativado.")
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
