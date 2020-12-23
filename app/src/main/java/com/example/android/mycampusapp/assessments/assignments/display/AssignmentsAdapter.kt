package com.example.android.mycampusapp.assessments.assignments.display

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.mycampusapp.data.Assignment
import com.example.android.mycampusapp.databinding.ListItemAssignmentBinding

class AssignmentsAdapter(private val clickListener: AssignmentsListener) :
    ListAdapter<Assignment, AssignmentsAdapter.ViewHolder>(DiffUtilCallBack) {

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
            currentAssignment: Assignment,
            clickListener: AssignmentsListener,
            isActivated: Boolean = false
        ) {
            binding.executePendingBindings()
            binding.assignment = currentAssignment
            binding.assignmentSubject.text = currentAssignment.subject
            val date = "${currentAssignment.day}/${currentAssignment.month}/${currentAssignment.year}"
            binding.assignmentDueDate.text = date
            binding.clickListener = clickListener
            itemView.isActivated = isActivated
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> {
            return object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getPosition(): Int = adapterPosition

                override fun getSelectionKey(): Long? = itemId
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
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    companion object DiffUtilCallBack : DiffUtil.ItemCallback<Assignment>() {
        override fun areItemsTheSame(oldItem: Assignment, newItem: Assignment): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Assignment,
            newItem: Assignment
        ): Boolean {
            return false
        }

    }
}

class AssignmentsListener(val clickListener: (assignment: Assignment) -> Unit) {
    fun onClick(assignment: Assignment) = clickListener(assignment)
}