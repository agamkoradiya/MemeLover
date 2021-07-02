package com.example.mymeme.data.entities

data class ImgFlipResponse(
    val `data`: ImgFlipData,
    val success: Boolean,
    val error_message: String?
)