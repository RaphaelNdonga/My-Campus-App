package com.example.android.mycampusapp.timetable.display.days.sunday

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.mycampusapp.databinding.ListItemSundayBinding
import com.example.android.mycampusapp.timetable.data.SundayClass

class SundayAdapter(private val clickListener: SundayListener) :
    ListAdapter<SundayClass, SundayAdapter.ViewHolder>(
        DiffUtilCallBack()
    ) {

    var tracker: SelectionTracker<Long>? = null

    class ViewHolder(val binding: ListItemSundayBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemSundayBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(
                    binding
                )
            }
        }

        fun bind(
            sundayClass: SundayClass,
            clickListener: SundayListener,
            isActivated: Boolean = false
        ) {
            binding.executePendingBindings()
            binding.sundayClass = sundayClass
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
        val currentSundayClass = getItem(position)
        tracker?.let {
            holder.bind(currentSundayClass, clickListener,it.isSelected(position.toLong()))
        }
    }
}

class SundayListener(val clickListener: (sundayClass: SundayClass) -> Unit) {
    fun onClick(sundayClass: SundayClass) = clickListener(sundayClass)
}

class DiffUtilCallBack : DiffUtil.ItemCallback<SundayClass>() {
    override fun areItemsTheSame(oldItem: SundayClass, newItem: SundayClass): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: SundayClass, newItem: SundayClass): Boolean {
        return oldItem == newItem
    }

}
