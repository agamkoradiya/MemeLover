package com.example.mymeme.other

import com.example.mymeme.data.entities.ItemInfo

interface OnItemInfoClickListener {
    fun onItemClick(itemInfo: ItemInfo)
}