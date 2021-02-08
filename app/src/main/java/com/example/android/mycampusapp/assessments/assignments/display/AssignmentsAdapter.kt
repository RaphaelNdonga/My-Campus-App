package com.example.android.mycampusapp.assessments.assignments.display

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.mycampusapp.data.Assessment
import com.example.android.mycampusapp.data.CustomDate
import com.example.android.mycampusapp.data.CustomTime
import com.example.android.mycampusapp.databinding.ListItemAssignmentBinding
import com.example.android.mycampusapp.util.formatDate
import com.example.android.mycampusapp.util.formatTime

class AssignmentsAdapter(private val clickListener: AssignmentsListener) :
    ListAdapter<Assessment, AssignmentsAdapter.ViewHolder>(DiffUtilCallBack) {

    var tracker : SelectionTracker<Long>? = null

    class ViewHolder(private val binding: ListItemAssignmentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object{
            fun from(parent: ViewGroup):ViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemAssignmentBinding.inflate(layoutInflater,parent,false)
                return ViewHolder(binding)
            }
        }

        fun bind(
            currentAssignment: Assessment,
            clickListener: AssignmentsListener,
            isActivated: Boolean = false
        ) {
            binding.executePendingBindings()
            binding.assignment = currentAssignment
            binding.assignmentSubject.text = currentAssignment.subject
            val date = formatDate(
                CustomDate(currentAssignment.year,currentAssignment.month,currentAssignment.day)
            )
            val time = formatTime(
                CustomTime(currentAssignment.hour,currentAssignment.minute)
            )
            binding.assignmentDate.text = date
            binding.assignmentTime.text = time
            binding.assignmentLocation.text = currentAssignment.locationName
            binding.assignmentRoom.text = currentAssignment.room
            binding.clickListener = clickListener
            itemView.isActivated = isActivated
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> {
            return object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getPosition(): Int = adapterPosition

                override fun getSelectionKey(): Long? = itemId
            }
        }
        fun setMapListener(assessment: Assessment?) {
            val mapUri = Uri.parse(assessment?.locationCoordinates)
            val mapIntent = Intent(Intent.ACTION_VIEW, mapUri)
            mapIntent.setPackage("com.google.android.apps.maps")

            binding.assignmentLocation.setOnClickListener {
                this.itemView.context.startActivity(mapIntent)
            }
        }
    }

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentAssignment = getItem(position)
        tracker?.let{
            holder.bind(currentAssignment, clickListener,it.isSelected(position.toLong()))
        }
        holder.setMapListener(currentAssignment)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    companion object DiffUtilCallBack : DiffUtil.ItemCallback<Assessment>() {
        override fun areItemsTheSame(oldItem: Assessment, newItem: Assessment): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Assessment,
            newItem: Assessment
        ): Boolean {
            return false
        }

    }
}

class AssignmentsListener(val clickListener: (assignment: Assessment) -> Unit) {
    fun onClick(assignment: Assessment) = clickListener(assignment)
}