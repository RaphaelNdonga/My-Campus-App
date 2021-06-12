package com.mycampusapp.links

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mycampusapp.data.Links
import com.mycampusapp.databinding.ListItemLinksBinding

class EssentialLinksAdapter(private val clickListener: EssentialLinksListener) :
    ListAdapter<Links, EssentialLinksViewHolder>(DiffUtilCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EssentialLinksViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemLinksBinding.inflate(
            layoutInflater, parent, false
        )
        return EssentialLinksViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EssentialLinksViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.setOnClickListener {
            clickListener.onClick(item)
        }
    }
}

class EssentialLinksListener(private val clickListener: (link: Links) -> Unit) {
    fun onClick(link: Links) {
        clickListener(link)
    }
}

object DiffUtilCallback : DiffUtil.ItemCallback<Links>() {
    override fun areItemsTheSame(oldItem: Links, newItem: Links): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Links, newItem: Links): Boolean {
        return oldItem.subject == newItem.subject && newItem.link == oldItem.link
    }

}

class EssentialLinksViewHolder(private val binding: ListItemLinksBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Links) {
        binding.subject.text = item.subject
        binding.link.text = item.link
        binding.link.setOnClickListener {
            val browserUri = Uri.parse(item.link)
            val browserIntent = Intent(Intent.ACTION_VIEW, browserUri)
            it.context.startActivity(browserIntent)
        }
    }
}
