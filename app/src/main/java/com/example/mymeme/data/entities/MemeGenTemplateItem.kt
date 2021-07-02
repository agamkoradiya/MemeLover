package com.example.mymeme.data.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MemeGenTemplateItem(
    val blank: String,
    val id: String,
    val lines: Int
) : Parcelable