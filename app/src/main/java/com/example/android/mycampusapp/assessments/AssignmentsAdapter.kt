package com.example.android.mycampusapp.assessments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.mycampusapp.data.Assignment
import com.example.android.mycampusapp.databinding.ListItemAssignmentBinding

class AssignmentsAdapter : ListAdapter<Assignment, AssignmentsAdapter.ViewHolder>(DiffUtilCallBack) {
    class ViewHolder(private val binding: ListItemAssignmentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(currentAssignment: Assignment) {
            binding.assignment = currentAssignment
            binding.assignmentSubject.text = currentAssignment.subject
            binding.assignmentDueDate.text = currentAssignment.date
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemAssignmentBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentAssignment = getItem(position)
        holder.bind(currentAssignment)
    }

    companion object DiffUtilCallBack : DiffUtil.ItemCallback<Assignment>() {
        override fun areItemsTheSame(oldItem: Assignment, newItem: Assignment): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Assignment,
            newItem: Assignment
        ): Boolean {
            return oldItem == newItem
        }

    }
}