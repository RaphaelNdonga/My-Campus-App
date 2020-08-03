package com.example.android.mycampusapp.timetable.display.days.thursday

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.mycampusapp.databinding.ListItemThursdayBinding
import com.example.android.mycampusapp.timetable.data.ThursdayClass

class ThursdayAdapter(private val clickListener: ThursdayListener) :
    ListAdapter<ThursdayClass, ThursdayAdapter.ViewHolder>(
        DiffUtilCallBack()
    ) {

    var tracker: SelectionTracker<Long>? = null

    class ViewHolder(val binding: ListItemThursdayBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemThursdayBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(
                    binding
                )
            }
        }

        fun bind(
            thursdayClass: ThursdayClass,
            clickListener: ThursdayListener,
            isActivated: Boolean = false
        ) {
            binding.executePendingBindings()
            binding.thursdayClass = thursdayClass
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
        return ViewHolder.from(
            parent
        )
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentThursdayClass = getItem(position)
        tracker?.let {
            holder.bind(currentThursdayClass, clickListener,it.isSelected(position.toLong()))
        }
    }
}

class ThursdayListener(val clickListener: (thursdayClass: ThursdayClass) -> Unit) {
    fun onClick(thursdayClass: ThursdayClass) = clickListener(thursdayClass)
}

class DiffUtilCallBack : DiffUtil.ItemCallback<ThursdayClass>() {
    override fun areItemsTheSame(oldItem: ThursdayClass, newItem: ThursdayClass): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ThursdayClass, newItem: ThursdayClass): Boolean {
        return oldItem == newItem
    }

}
