package com.example.mymeme.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.mymeme.data.entities.Meme
import com.example.mymeme.databinding.TemplatePageItemBinding
import javax.inject.Inject

class TemplateImgFlipAdapter @Inject constructor(
    private val glide: RequestManager
) : ListAdapter<Meme, TemplateImgFlipAdapter.TemplateViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemplateViewHolder {
        val binding =
            TemplatePageItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return TemplateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TemplateViewHolder, position: Int) {
        val currentItem = getItem(position)
        currentItem?.let {
            holder.bind(it)
        }
    }

    inner class TemplateViewHolder(private val binding: TemplatePageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(imgFlipTemplateItem: Meme) {
            glide.load(imgFlipTemplateItem.url).into(binding.templateImgView)
            binding.templateImgView.setOnClickListener {
                onTemplateImgFlipClickListener?.let {
                    it(imgFlipTemplateItem)
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Meme>() {
        override fun areItemsTheSame(oldItem: Meme, newItem: Meme) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Meme, newItem: Meme) =
            oldItem == newItem
    }

    private var onTemplateImgFlipClickListener: ((Meme) -> Unit)? = null

    fun setOnTemplateImgFlipClickListener(listener: (Meme) -> Unit) {
        onTemplateImgFlipClickListener = listener
    }
}