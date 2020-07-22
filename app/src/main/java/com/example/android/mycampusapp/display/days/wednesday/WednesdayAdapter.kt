package com.example.android.mycampusapp.display.days.wednesday

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.mycampusapp.data.WednesdayClass
import com.example.android.mycampusapp.databinding.ListItemWednesdayBinding

class WednesdayAdapter(private val clickListener: WednesdayListener) :
    ListAdapter<WednesdayClass, WednesdayAdapter.ViewHolder>(DiffUtilCallBack()) {

    var tracker: SelectionTracker<Long>? = null

    class ViewHolder(val binding: ListItemWednesdayBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemWednesdayBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

        fun bind(
            wednesdayClass: WednesdayClass,
            clickListener: WednesdayListener,
            isActivated: Boolean = false
        ) {
            binding.executePendingBindings()
            binding.wednesdayClass = wednesdayClass
            binding.clickListener = clickListener
            itemView.isActivated = isActivated
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long>? =
            object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getSelectionKey(): Long? = itemId
                override fun getPosition(): Int = adapterPosition
            }
    }

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentWednesdayClass = getItem(position)
        tracker?.let {
            holder.bind(currentWednesdayClass, clickListener,it.isSelected(position.toLong()))
        }
    }
}

class WednesdayListener(val clickListener: (wednesdayClass: WednesdayClass) -> Unit) {
    fun onClick(wednesdayClass: WednesdayClass) = clickListener(wednesdayClass)
}

class DiffUtilCallBack : DiffUtil.ItemCallback<WednesdayClass>() {
    override fun areItemsTheSame(oldItem: WednesdayClass, newItem: WednesdayClass): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: WednesdayClass, newItem: WednesdayClass): Boolean {
        return oldItem == newItem
    }

}
