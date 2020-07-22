package com.example.android.mycampusapp.display.days.saturday

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.mycampusapp.data.SaturdayClass
import com.example.android.mycampusapp.databinding.ListItemSaturdayBinding

class SaturdayAdapter(private val clickListener: SaturdayListener) :
    ListAdapter<SaturdayClass, SaturdayAdapter.ViewHolder>(DiffUtilCallBack()) {

    var tracker: SelectionTracker<Long>? = null

    class ViewHolder(val binding: ListItemSaturdayBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemSaturdayBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

        fun bind(
            saturdayClass: SaturdayClass,
            clickListener: SaturdayListener,
            isActivated: Boolean = false
        ) {
            binding.executePendingBindings()
            binding.saturdayClass = saturdayClass
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
        val currentSaturdayClass = getItem(position)
        tracker?.let {
            holder.bind(currentSaturdayClass, clickListener,it.isSelected(position.toLong()))
        }
    }
}

class SaturdayListener(val clickListener: (saturdayClass: SaturdayClass) -> Unit) {
    fun onClick(saturdayClass: SaturdayClass) = clickListener(saturdayClass)
}

class DiffUtilCallBack : DiffUtil.ItemCallback<SaturdayClass>() {
    override fun areItemsTheSame(oldItem: SaturdayClass, newItem: SaturdayClass): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: SaturdayClass, newItem: SaturdayClass): Boolean {
        return oldItem == newItem
    }

}
