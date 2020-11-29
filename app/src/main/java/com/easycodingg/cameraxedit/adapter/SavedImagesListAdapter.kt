package com.easycodingg.cameraxedit.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.easycodingg.cameraxedit.databinding.SavedImagesListItemBinding

class SavedImagesListAdapter: ListAdapter<Uri, SavedImagesListAdapter.SavedImageViewHolder>(diffCallBack) {

    inner class SavedImageViewHolder(val binding: SavedImagesListItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(currentItemUri: Uri) {
            Glide.with(binding.root)
                    .load(currentItemUri)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.ivSavedImage)
        }
    }

    companion object {
        val diffCallBack = object : DiffUtil.ItemCallback<Uri>() {
            override fun areItemsTheSame(oldItem: Uri, newItem: Uri): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Uri, newItem: Uri): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedImageViewHolder {
        val binding = SavedImagesListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SavedImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SavedImageViewHolder, position: Int) {
        val currentItemUri = getItem(position)
        holder.bind(currentItemUri)

        holder.binding.root.setOnClickListener {
            onItemClickListener?.let {
                it(currentItemUri)
            }
        }

        holder.binding.root.setOnLongClickListener {
            onItemLongClickListener?.let {
                it(currentItemUri)
            }
            true
        }
    }

    private var onItemClickListener: ((Uri) -> Unit)? = null
    private var onItemLongClickListener: ((Uri) -> Unit)? = null

    fun setOnItemClickListener(listener: (Uri) -> Unit) {
        onItemClickListener = listener
    }

    fun setOnItemLongClickListener(listener: (Uri) -> Unit) {
        onItemLongClickListener = listener
    }
}