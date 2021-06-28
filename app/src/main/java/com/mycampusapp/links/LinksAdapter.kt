package com.mycampusapp.links

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mycampusapp.data.Links
import com.mycampusapp.databinding.ListItemLinksBinding

class EssentialLinksAdapter(private val clickListener: EssentialLinksListener) :
    ListAdapter<Links, EssentialLinksViewHolder>(DiffUtilCallback) {
    var tracker: SelectionTracker<Long>? = null

    init {
        setHasStableIds(true)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EssentialLinksViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemLinksBinding.inflate(
            layoutInflater, parent, false
        )
        return EssentialLinksViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EssentialLinksViewHolder, position: Int) {
        val item = getItem(position)
        tracker?.let {
            holder.bind(item,it.isSelected(position.toLong()))
        }
        holder.itemView.setOnClickListener {
            clickListener.onClick(item)
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
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
        return false
    }

}

class EssentialLinksViewHolder(private val binding: ListItemLinksBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Links, selected: Boolean=false) {
        binding.subject.text = item.subject
        binding.link.text = item.link
        binding.link.setOnClickListener {
            val browserUri = Uri.parse(item.link)
            val browserIntent = Intent(Intent.ACTION_VIEW, browserUri)
            it.context.startActivity(browserIntent)
        }
        itemView.isActivated = selected
    }

    fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long>? =
        object : ItemDetailsLookup.ItemDetails<Long>() {
            override fun getSelectionKey(): Long? = itemId
            override fun getPosition(): Int = bindingAdapterPosition
        }
}
