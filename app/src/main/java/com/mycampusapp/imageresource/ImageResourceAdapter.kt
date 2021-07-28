package com.mycampusapp.imageresource

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mycampusapp.R
import com.mycampusapp.data.DocumentData
import com.mycampusapp.databinding.ListItemImageBinding
import com.mycampusapp.documentresource.DocumentsAdapter
import java.io.File

class ImageResourceAdapter(private val imageListener:DocumentsAdapter.DocumentClickListener) :
    ListAdapter<DocumentData, ImageResourceAdapter.ImageViewHolder>(DocumentsAdapter.DocumentsDiffUtilCallback) {
    class ImageViewHolder(private val binding: ListItemImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(imageData: DocumentData) {
            val root = binding.root.context.getExternalFilesDir(null).toString()
            val imageFile = File(root,imageData.fileName)
            if(imageFile.exists().not()) {
                binding.greenCheck.visibility = View.GONE
                val imageUri = imageData.url.toUri().buildUpon().scheme("https").build()
                Glide.with(itemView).load(imageUri).apply(
                    RequestOptions().placeholder(R.drawable.loading_animation)
                        .error(R.drawable.ic_broken_image)
                ).into(binding.imageResource)

            }else{
                binding.greenCheck.visibility = View.VISIBLE
                val imageUri = imageFile.toUri()
                Glide.with(itemView).load(imageUri).apply(
                    RequestOptions().placeholder(R.drawable.loading_animation)
                        .error(R.drawable.ic_broken_image)
                ).into(binding.imageResource)
            }
            binding.imageName.text = imageData.fileName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ImageViewHolder(ListItemImageBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
        holder.itemView.setOnClickListener {
            imageListener.onClick(currentItem)
        }
    }
}
