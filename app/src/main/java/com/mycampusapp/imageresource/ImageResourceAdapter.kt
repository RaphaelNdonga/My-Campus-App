package com.mycampusapp.imageresource

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mycampusapp.R
import com.mycampusapp.data.ImageData
import com.mycampusapp.databinding.ListItemImageBinding

class ImageResourceAdapter :
    ListAdapter<ImageData, ImageResourceAdapter.ImageViewHolder>(ImageDiffUtilCallback) {
    class ImageViewHolder(private val binding: ListItemImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(imageData: ImageData) {
            val imageUri = imageData.imageUrl.toUri().buildUpon().scheme("https").build()
            Glide.with(itemView).load(imageUri).apply(
                RequestOptions().placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image)
            ).into(binding.imageResource)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ImageViewHolder(ListItemImageBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }
}

object ImageDiffUtilCallback : DiffUtil.ItemCallback<ImageData>() {
    override fun areItemsTheSame(oldItem: ImageData, newItem: ImageData): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: ImageData, newItem: ImageData): Boolean {
        return false
    }

}
