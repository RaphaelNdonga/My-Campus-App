package com.example.android.mycampusapp.display.days.friday

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.mycampusapp.data.FridayClass
import com.example.android.mycampusapp.databinding.ListItemFridayBinding

class FridayAdapter(private val clickListener: FridayListener) :
    ListAdapter<FridayClass, FridayAdapter.ViewHolder>(DiffUtilCallBack()) {

    var tracker: SelectionTracker<Long>? = null

    class ViewHolder(val binding: ListItemFridayBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemFridayBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

        fun bind(
            fridayClass: FridayClass,
            clickListener: FridayListener,
            isActivated: Boolean = false
        ) {
            binding.executePendingBindings()
            binding.fridayClass = fridayClass
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
        val currentFridayClass = getItem(position)
        tracker?.let {
            holder.bind(currentFridayClass, clickListener,it.isSelected(position.toLong()))
        }
    }
}

class FridayListener(val clickListener: (fridayClass: FridayClass) -> Unit) {
    fun onClick(fridayClass: FridayClass) = clickListener(fridayClass)
}

class DiffUtilCallBack : DiffUtil.ItemCallback<FridayClass>() {
    override fun areItemsTheSame(oldItem: FridayClass, newItem: FridayClass): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FridayClass, newItem: FridayClass): Boolean {
        return oldItem == newItem
    }

}
