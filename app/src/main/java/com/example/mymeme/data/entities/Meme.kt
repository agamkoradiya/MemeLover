package com.example.mymeme.data.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Meme(
    val box_count: Int,
    val id: String,
    val url: String
) : Parcelable