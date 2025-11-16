package com.notesbible.app.data.remote

import retrofit2.http.GET
import retrofit2.http.Url

interface BibleApi {
    @GET
    suspend fun downloadVersion(@Url url: String): String
}
