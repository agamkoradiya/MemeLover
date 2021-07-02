package com.example.mymeme.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.mymeme.data.entities.MemeGenTemplateItem
import com.example.mymeme.databinding.TemplatePageItemBinding
import javax.inject.Inject

class TemplateMemeGenAdapter @Inject constructor(
    private val glide: RequestManager
) : ListAdapter<MemeGenTemplateItem, TemplateMemeGenAdapter.TemplateViewHolder>(DiffCallback()) {

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
        fun bind(memeGenTemplateItem: MemeGenTemplateItem) {

            glide
//                .setDefaultRequestOptions(RequestOptions().override(100, 100))
                .load(memeGenTemplateItem.blank).into(binding.templateImgView)

            binding.templateImgView.setOnClickListener {
                onTemplateMemeGenClickListener?.let {
                    it(memeGenTemplateItem)
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<MemeGenTemplateItem>() {
        override fun areItemsTheSame(oldItem: MemeGenTemplateItem, newItem: MemeGenTemplateItem) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: MemeGenTemplateItem,
            newItem: MemeGenTemplateItem
        ) =
            oldItem == newItem
    }

    private var onTemplateMemeGenClickListener: ((MemeGenTemplateItem) -> Unit)? = null

    fun setOnTemplateMemeGenClickListener(listener: (MemeGenTemplateItem) -> Unit) {
        onTemplateMemeGenClickListener = listener
    }
}