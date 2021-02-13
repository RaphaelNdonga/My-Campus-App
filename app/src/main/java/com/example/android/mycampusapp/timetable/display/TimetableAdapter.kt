package com.example.android.mycampusapp.timetable.display

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.mycampusapp.data.CustomTime
import com.example.android.mycampusapp.data.TimetableClass
import com.example.android.mycampusapp.databinding.ListItemTimetableBinding
import com.example.android.mycampusapp.util.formatTime

class TimetableAdapter(private val clickListener: TimetableListener) :
    ListAdapter<TimetableClass, TimetableAdapter.ViewHolder>(
        DiffUtilCallBack
    ) {

    var tracker: SelectionTracker<Long>? = null

    class ViewHolder(private val binding: ListItemTimetableBinding, private val context: Context) :
        RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemTimetableBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(
                    binding, parent.context
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
            binding.listItemTime.text = formatTime(CustomTime(
                timetableClass.hour,
                timetableClass.minute
            ))
            binding.listItemLocation.text = timetableClass.locationName
            val room = "Room ${timetableClass.room}"
            binding.listItemRoom.text = room
            binding.clickListener = clickListener
            itemView.isActivated = isActivated
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long>? =
            object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getSelectionKey(): Long? = itemId
                override fun getPosition(): Int = adapterPosition
            }

        fun setMapListener(currentClass: TimetableClass?) {
            val mapUri = Uri.parse(currentClass?.locationCoordinates)
            val mapIntent = Intent(Intent.ACTION_VIEW, mapUri)
            mapIntent.setPackage("com.google.android.apps.maps")

            binding.listItemLocation.setOnClickListener {
                context.startActivity(mapIntent)
            }
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
        val currentClass = getItem(position)
        tracker?.let {
            holder.bind(currentClass, clickListener, it.isSelected(position.toLong()))
        }
        holder.setMapListener(currentClass)
    }

    companion object DiffUtilCallBack : DiffUtil.ItemCallback<TimetableClass>() {
        override fun areItemsTheSame(oldItem: TimetableClass, newItem: TimetableClass): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TimetableClass, newItem: TimetableClass): Boolean {
            return false
        }
    }
}

class TimetableListener(val clickListener: (timetableClass: TimetableClass) -> Unit) {
    fun onClick(timetableClass: TimetableClass) = clickListener(timetableClass)
}


