package com.example.android.mycampusapp.display.days.tuesday

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.mycampusapp.data.TuesdayClass
import com.example.android.mycampusapp.databinding.ListItemTuesdayBinding

class TuesdayAdapter(private val clickListener: TuesdayListener) :
    ListAdapter<TuesdayClass, TuesdayAdapter.ViewHolder>(DiffUtilCallBack()) {

    var tracker: SelectionTracker<Long>? = null

    class ViewHolder(val binding: ListItemTuesdayBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemTuesdayBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

        fun bind(
            tuesdayClass: TuesdayClass,
            clickListener: TuesdayListener,
            isActivated: Boolean = false
        ) {
            binding.executePendingBindings()
            binding.tuesdayClass = tuesdayClass
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
        val currentTuesdayClass = getItem(position)
        tracker?.let {
            holder.bind(currentTuesdayClass, clickListener,it.isSelected(position.toLong()))
        }
    }
}

class TuesdayListener(val clickListener: (tuesdayClass: TuesdayClass) -> Unit) {
    fun onClick(tuesdayClass: TuesdayClass) = clickListener(tuesdayClass)
}

class DiffUtilCallBack : DiffUtil.ItemCallback<TuesdayClass>() {
    override fun areItemsTheSame(oldItem: TuesdayClass, newItem: TuesdayClass): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: TuesdayClass, newItem: TuesdayClass): Boolean {
        return oldItem == newItem
    }

}
