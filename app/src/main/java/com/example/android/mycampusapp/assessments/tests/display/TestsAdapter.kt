package com.example.android.mycampusapp.assessments.tests.display

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.mycampusapp.data.Test
import com.example.android.mycampusapp.databinding.ListItemTestBinding

class TestsAdapter : ListAdapter<Test, TestsAdapter.ViewHolder>(DiffUtilCallBack) {
    class ViewHolder(private val binding: ListItemTestBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(test: Test?) {
            binding.testSubject.text = test?.subject
            binding.testLocation.text = test?.locationName
            binding.testRoom.text = test?.room
            val testDate = "${test?.day}/${test?.month}/${test?.year}"
            binding.testDate.text = testDate
            val testTime = "${test?.hour}:${test?.minute}"
            binding.testTime.text = testTime
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemTestBinding.inflate(layoutInflater)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val test = getItem(position)
        holder.bind(test)
    }

    object DiffUtilCallBack : DiffUtil.ItemCallback<Test>() {
        override fun areItemsTheSame(oldItem: Test, newItem: Test): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Test, newItem: Test): Boolean {
            return false
        }

    }
}