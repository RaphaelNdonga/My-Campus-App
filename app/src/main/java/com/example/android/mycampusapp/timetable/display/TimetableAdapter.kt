package com.example.android.mycampusapp.timetable.display

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.mycampusapp.databinding.ListItemTimetableBinding
import com.example.android.mycampusapp.timetable.data.TimetableClass

class TimetableAdapter(private val clickListener: TimetableListener) :
    ListAdapter<TimetableClass, TimetableAdapter.ViewHolder>(
        DiffUtilCallBack()
    ) {

    var tracker: SelectionTracker<Long>? = null

    class ViewHolder(val binding: ListItemTimetableBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemTimetableBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(
                    binding
                )
            }
        }

        fun bind(
            timetableClass: TimetableClass,
            clickListener: TimetableListener,
            isActivated: Boolean = false
        ) {
            binding.executePendingBindings()
            binding.timetableClass = timetableClass
            binding.listItemSubject.text = timetableClass.subject
            binding.listItemTime.text = timetableClass.time
            binding.listItemLocation.text = timetableClass.locationName
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
        val currentFridayClass = getItem(position)
        tracker?.let {
            holder.bind(currentFridayClass, clickListener, it.isSelected(position.toLong()))
        }
    }
}

class TimetableListener(val clickListener: (timetableClass: TimetableClass) -> Unit) {
    fun onClick(timetableClass: TimetableClass) = clickListener(timetableClass)
}

class DiffUtilCallBack : DiffUtil.ItemCallback<TimetableClass>() {
    override fun areItemsTheSame(oldItem: TimetableClass, newItem: TimetableClass): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: TimetableClass, newItem: TimetableClass): Boolean {
        /**
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
