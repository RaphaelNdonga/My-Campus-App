package com.example.android.mycampusapp.timetable.display.days.monday

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.mycampusapp.databinding.ListItemMondayBinding
import com.example.android.mycampusapp.timetable.data.MondayClass

class MondayAdapter(private val clickListener: MondayListener) :
    ListAdapter<MondayClass, MondayAdapter.ViewHolder>(
        DiffUtilCallBack()
    ) {

    var tracker: SelectionTracker<Long>? = null

    class ViewHolder(val binding: ListItemMondayBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemMondayBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(
                    binding
                )
            }
        }

        fun bind(
            mondayClass: MondayClass,
            clickListener: MondayListener,
            isActivated: Boolean = false
        ) {
            binding.executePendingBindings()
            binding.mondayClass = mondayClass
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
        val currentMondayClass = getItem(position)
        tracker?.let {
            holder.bind(currentMondayClass, clickListener,it.isSelected(position.toLong()))
        }
    }
}

class MondayListener(val clickListener: (mondayClass: MondayClass) -> Unit) {
    fun onClick(mondayClass: MondayClass) = clickListener(mondayClass)
}

class DiffUtilCallBack : DiffUtil.ItemCallback<MondayClass>() {
    override fun areItemsTheSame(oldItem: MondayClass, newItem: MondayClass): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: MondayClass, newItem: MondayClass): Boolean {
        /*
Previous was return oldItem == newItem.
An error runtime error was persisting whereby the contents were being regarded as the same
even when they weren't the same.
I chose to always return false because very rarely, if ever, will 2 classes be scheduled at
the same time. And even when so, they will not be many such cases, therefore the value for
oldItem == newItem is very low
 */
        return false
    }

}
