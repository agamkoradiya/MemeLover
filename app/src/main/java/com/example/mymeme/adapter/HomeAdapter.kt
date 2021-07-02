package com.example.mymeme.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mymeme.data.entities.ItemInfo
import com.example.mymeme.databinding.HomePageItemBinding
import com.example.mymeme.other.OnItemInfoClickListener

class HomeAdapter(private val listener: OnItemInfoClickListener) :
    ListAdapter<ItemInfo, HomeAdapter.HomeViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val binding =
            HomePageItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return HomeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val currentItem = getItem(position)
        currentItem?.let {
            holder.bind(it)
        }
    }

    inner class HomeViewHolder(private val binding: HomePageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(itemInfo: ItemInfo) {
            binding.itemNameTxt.text = itemInfo.name
            binding.itemUrlBtn.setOnClickListener {
                listener.onItemClick(itemInfo)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ItemInfo>() {
        override fun areItemsTheSame(oldItem: ItemInfo, newItem: ItemInfo) =
            oldItem.name == newItem.name

        override fun areContentsTheSame(oldItem: ItemInfo, newItem: ItemInfo) =
            oldItem == newItem
    }

//    private var onItemClickListener: ((ItemInfo) -> Unit)? = null
//
//    fun setOnClickListener(listener: (ItemInfo) -> Unit) {
//        onItemClickListener = listener
//    }

}