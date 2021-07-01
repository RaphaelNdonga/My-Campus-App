package com.mycampusapp.timetable.display

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.common.io.Resources
import com.google.firebase.firestore.CollectionReference
import com.mycampusapp.R
import com.mycampusapp.data.CustomTime
import com.mycampusapp.data.TimetableClass
import com.mycampusapp.databinding.ListItemTimetableBinding
import com.mycampusapp.util.format24HourTime
import com.mycampusapp.util.formatAmPmTime

class TimetableAdapter(
    private val dayCollection: CollectionReference,
    private val clickListener: TimetableListener,
    private val overflowListener: OverflowListener
) :
    ListAdapter<TimetableClass, TimetableAdapter.ViewHolder>(
        DiffUtilCallBack
    ) {

    var tracker: SelectionTracker<Long>? = null

    inner class ViewHolder(private val binding: ListItemTimetableBinding) :
        RecyclerView.ViewHolder(binding.root) {

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
            if (timetableClass.locationCoordinates.isNotBlank()) {
                val room = "Room: ${timetableClass.room}"
                binding.listItemRoom.text = room
            } else {
                val password = "Password: ${timetableClass.room}"
                binding.listItemRoom.text = password
                binding.clipboard.visibility = View.VISIBLE
            }

            binding.clickListener = clickListener
            itemView.isActivated = isActivated
            if (timetableClass.isActive.not()) {
                binding.listItemSubject.apply {
                    paintFlags = this.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    setTextColor(Color.GRAY)
                }
            }else{
                binding.listItemSubject.apply {
                    /**
                     * The anti alias flag smoothens out the text so that it does not look distorted
                     */
                    paintFlags = Paint.ANTI_ALIAS_FLAG
                }
            }
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
                override fun getPosition(): Int = bindingAdapterPosition
            }

        fun setClickListener(currentClass: TimetableClass) {
            /**
             * The location coordinates shall be used to determine whether a timetableClass
             * contains a location or a link
             */
            if (currentClass.locationCoordinates.isNotBlank()) {
                val mapUri = Uri.parse(currentClass.locationCoordinates)
                val mapIntent = Intent(Intent.ACTION_VIEW, mapUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                binding.locationImg.setImageResource(R.drawable.ic_location)
                binding.listItemLocation.setOnClickListener {
                    it.context.startActivity(mapIntent)
                }
            } else {
                val browserUri = Uri.parse(currentClass.locationNameOrLink)
                val browserIntent = Intent(Intent.ACTION_VIEW, browserUri)

                binding.locationImg.setImageResource(R.drawable.ic_internet)

                binding.listItemLocation.setOnClickListener {
                    it.context.startActivity(browserIntent)
                }
                binding.clipboard.setOnClickListener {
                    val clipBoard =
                        binding.root.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("password", currentClass.room)
                    clipBoard.setPrimaryClip(clip)
                    Snackbar.make(
                        binding.root,
                        "The password has been copied to the clipboard",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }

        fun setOverflowClickListener(timetableClass: TimetableClass) {
            binding.moreIcon.setOnClickListener {
                overflowListener.onClick(timetableClass,it)
            }
        }
    }

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemTimetableBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(
            binding
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
        holder.setOverflowClickListener(currentClass)
    }

    override fun submitList(list: List<TimetableClass>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    companion object DiffUtilCallBack : DiffUtil.ItemCallback<TimetableClass>() {
        override fun areItemsTheSame(oldItem: TimetableClass, newItem: TimetableClass): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TimetableClass, newItem: TimetableClass): Boolean {
            return oldItem == newItem
        }
    }
}

class TimetableListener(val clickListener: (timetableClass: TimetableClass) -> Unit) {
    fun onClick(timetableClass: TimetableClass) = clickListener(timetableClass)
}

class OverflowListener(val clickListener: (timetableClass:TimetableClass,view:View)->Unit){
    fun onClick(timetableClass: TimetableClass,view: View) = clickListener(timetableClass,view)
}


