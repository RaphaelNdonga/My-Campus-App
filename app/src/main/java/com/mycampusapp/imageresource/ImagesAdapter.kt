package com.mycampusapp.imageresource

import android.content.Context
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
import com.mycampusapp.databinding.ListItemDocumentBinding
import com.mycampusapp.documentresource.DocumentItemListener
import com.mycampusapp.documentresource.DocumentsAdapter
import com.mycampusapp.util.IS_ADMIN
import com.mycampusapp.util.sharedPrefFile
import java.io.File

class ImagesAdapter(
    private val imageListener: DocumentsAdapter.DocumentClickListener,
    private val overflowListener: DocumentItemListener
) :
    ListAdapter<DocumentData, ImagesAdapter.ImageViewHolder>(DocumentsAdapter.DocumentsDiffUtilCallback) {
    inner class ImageViewHolder(private val binding: ListItemDocumentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val sharedPreferences = binding.root.context.getSharedPreferences(
            sharedPrefFile,
            Context.MODE_PRIVATE
        )

        private val isAdmin = sharedPreferences.getBoolean(IS_ADMIN, false)

        fun bind(imageData: DocumentData) {
            val root = binding.root.context.getExternalFilesDir(null).toString()
            val imageFile = File(root, imageData.fileName)
            if (imageFile.exists().not()) {
                binding.greenCheck.visibility = View.GONE
                binding.moreIcon.visibility = View.GONE
                val imageUri = imageData.url.toUri().buildUpon().scheme("https").build()
                Glide.with(itemView).load(imageUri).apply(
                    RequestOptions().placeholder(R.drawable.loading_animation)
                        .error(R.drawable.ic_broken_image)
                ).into(binding.fileImage)

            } else {
                binding.greenCheck.visibility = View.VISIBLE
                binding.moreIcon.visibility = View.VISIBLE
                val imageUri = imageFile.toUri()
                Glide.with(itemView).load(imageUri).apply(
                    RequestOptions().placeholder(R.drawable.loading_animation)
                        .error(R.drawable.ic_broken_image)
                ).into(binding.fileImage)
            }
            if (isAdmin) {
                binding.moreIcon.visibility = View.VISIBLE
            }
            binding.moreIcon.setOnClickListener {
                overflowListener.click(imageData, it)
            }
            binding.fileName.text = imageData.fileName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ImageViewHolder(ListItemDocumentBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
        holder.itemView.setOnClickListener {
            imageListener.onClick(currentItem)
        }
    }
}
