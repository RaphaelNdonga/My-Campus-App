package com.mycampusapp.timetable.display

import android.content.Intent
import android.net.Uri
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mycampusapp.R
import com.mycampusapp.data.CustomTime
import com.mycampusapp.data.TimetableClass
import com.mycampusapp.databinding.ListItemTimetableBinding
import com.mycampusapp.util.format24HourTime
import com.mycampusapp.util.formatAmPmTime

class TimetableAdapter(private val clickListener: TimetableListener) :
    ListAdapter<TimetableClass, TimetableAdapter.ViewHolder>(
        DiffUtilCallBack
    ) {

    var tracker: SelectionTracker<Long>? = null

    class ViewHolder(private val binding: ListItemTimetableBinding) :
        RecyclerView.ViewHolder(binding.root) {
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
            binding.listItemTime.text = formatTime(
                CustomTime(
                    timetableClass.hour,
                    timetableClass.minute
                )
            )
            binding.listItemLocation.text = timetableClass.locationNameOrLink
            val room = "Room ${timetableClass.room}"
            binding.listItemRoom.text = room
            binding.clickListener = clickListener
            itemView.isActivated = isActivated
        }

        private fun formatTime(customTime: CustomTime): String {
            return if (DateFormat.is24HourFormat(itemView.context)) {
                format24HourTime(customTime)
            } else {
                formatAmPmTime(customTime)
            }
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long>? =
            object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getSelectionKey(): Long? = itemId
                override fun getPosition(): Int = adapterPosition
            }

        fun setClickListener(currentClass: TimetableClass?) {
            /**
             * The location coordinates shall be used to determine whether a timetableClass
             * contains a location or a link
             */
            if (!currentClass?.locationCoordinates.isNullOrBlank()) {
                val mapUri = Uri.parse(currentClass?.locationCoordinates)
                val mapIntent = Intent(Intent.ACTION_VIEW, mapUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                binding.locationImg.setImageResource(R.drawable.ic_location)
                binding.listItemLocation.setOnClickListener {
                    it.context.startActivity(mapIntent)
                }
            } else {
                val browserUri = Uri.parse(currentClass?.locationNameOrLink)
                val browserIntent = Intent(Intent.ACTION_VIEW, browserUri)

                binding.locationImg.setImageResource(R.drawable.ic_internet)

                binding.listItemLocation.setOnClickListener {
                    it.context.startActivity(browserIntent)
                }
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
        holder.setClickListener(currentClass)
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


