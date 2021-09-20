package com.mycampusapp.documentresource

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mycampusapp.R
import com.mycampusapp.data.DocumentData
import com.mycampusapp.databinding.ListItemDocumentBinding
import com.mycampusapp.util.COURSE_ID
import com.mycampusapp.util.IS_ADMIN
import com.mycampusapp.util.sharedPrefFile
import java.io.File

class DocumentsAdapter(
    private val clickListener: DocumentClickListener,
    private val itemListener: DocumentItemListener
) :
    ListAdapter<DocumentData, DocumentsAdapter.DocumentsViewHolder>(DocumentsDiffUtilCallback) {

    object DocumentsDiffUtilCallback : DiffUtil.ItemCallback<DocumentData>() {
        override fun areItemsTheSame(oldItem: DocumentData, newItem: DocumentData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DocumentData, newItem: DocumentData): Boolean {
            return false
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return DocumentsViewHolder(
            ListItemDocumentBinding.inflate(layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: DocumentsViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
        holder.itemView.setOnClickListener {
            clickListener.onClick(currentItem)
        }
    }

    class DocumentClickListener(val clickListener: (DocumentData) -> Unit) {
        fun onClick(documentData: DocumentData) {
            clickListener(documentData)
        }
    }

    inner class DocumentsViewHolder(private val binding: ListItemDocumentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val sharedPreferences = binding.root.context.getSharedPreferences(
            sharedPrefFile,
            Context.MODE_PRIVATE
        )

        private val isAdmin = sharedPreferences.getBoolean(IS_ADMIN, false)

        fun bind(currentItem: DocumentData) {
            binding.fileName.text = currentItem.fileName
            val fileExtension = currentItem.fileName.reversed().substringBefore(".").reversed()
            when (fileExtension) {
                "pdf" -> {
                    binding.fileImage.setImageResource(R.drawable.ic_pdf)
                }
                "docx" -> {
                    binding.fileImage.setImageResource(R.drawable.ic_word)
                }
                "xlsx" -> {
                    binding.fileImage.setImageResource(R.drawable.ic_excel)
                }
                "zip" -> {
                    binding.fileImage.setImageResource(R.drawable.ic_zip)
                }
                else -> {
                    binding.fileImage.setImageResource(R.drawable.ic_document)
                }
            }
            val root = binding.root.context.getExternalFilesDir(null)
            val file = File(root, currentItem.fileName)
            if (file.exists()) {
                binding.greenCheck.visibility = View.VISIBLE
                binding.moreIcon.visibility = View.VISIBLE
            } else {
                binding.greenCheck.visibility = View.GONE
                binding.moreIcon.visibility = View.GONE
            }
            if (isAdmin) {
                binding.moreIcon.visibility = View.VISIBLE
            }
            binding.moreIcon.setOnClickListener {
                itemListener.click(currentItem, it)
            }
        }
    }
}

class DocumentItemListener(val click: (document: DocumentData, view: View) -> Unit) {
    fun onClick(document: DocumentData, view: View) {
        click(document, view)
    }
}
