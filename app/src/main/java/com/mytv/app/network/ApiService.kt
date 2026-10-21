package com.mytv.app.network

import com.mytv.app.model.ChannelCategory
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface ApiService {

    @GET("channels")
    suspend fun getChannels(
        @Header("API-KEY") apiKey: String? = null
    ): Response<List<ChannelCategory>>
}
