package com.example.mymeme.api

import com.example.mymeme.data.entities.MemeGenTemplateResponse
import retrofit2.Response
import retrofit2.http.*

interface MemeGenApi {

    @GET("templates")
    suspend fun getMemeGenTemplates(): Response<MemeGenTemplateResponse>

}