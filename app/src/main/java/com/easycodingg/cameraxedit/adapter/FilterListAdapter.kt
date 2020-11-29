package com.easycodingg.cameraxedit.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.easycodingg.cameraxedit.FilterItem
import com.easycodingg.cameraxedit.databinding.FilterListItemBinding

class FilterListAdapter(
    var list: List<FilterItem>
): RecyclerView.Adapter<FilterListAdapter.FilterViewHolder>() {

    inner class FilterViewHolder(val binding: FilterListItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(currentFilterItem: FilterItem) {
            binding.tvFilterName.text = currentFilterItem.filterName

            Glide.with(binding.root)
                    .load(currentFilterItem.filterPreviewUri)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.ivFilterItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val binding = FilterListItemBinding.inflate(LayoutInflater.from(parent.context))
        return FilterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        val currentFilterItem = list[position]

        holder.bind(currentFilterItem)

        holder.binding.root.setOnClickListener {
            onItemClickListener?.let {
                it(currentFilterItem.filterPreviewUri)
            }
        }
    }

    override fun getItemCount() = list.size

    private var onItemClickListener:((Uri) -> Unit)? = null

    fun setOnItemClickListener(listener: (Uri) -> Unit) {
        onItemClickListener = listener
    }
}