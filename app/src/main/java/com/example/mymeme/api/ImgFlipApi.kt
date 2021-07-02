package com.example.mymeme.api

import com.example.mymeme.data.entities.ImgFlipResponse
import com.example.mymeme.data.entities.ImgFlipTemplateResponse
import retrofit2.Response
import retrofit2.http.*

interface ImgFlipApi {

    @GET("get_memes")
    suspend fun getImgFlipTemplates(): Response<ImgFlipTemplateResponse>

    @FormUrlEncoded
    @POST("caption_image")
    suspend fun postImgFlipMemeReq(
        @Field("template_id") templateId: String,
        @Field("username") userName: String = "imgflip9577",
        @Field("password") password: String = "drujj2UGmezVwqv",
        @Field("font") font: String = "arial",
        @FieldMap boxes: Map<String, String>
    ): Response<ImgFlipResponse>
}