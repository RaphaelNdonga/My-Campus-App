package com.example.android.mycampusapp.assessments.tests.display

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.mycampusapp.data.Test
import com.example.android.mycampusapp.databinding.ListItemTestBinding

class TestsAdapter(private val clickListener:TestClickListener) : ListAdapter<Test, TestsAdapter.ViewHolder>(DiffUtilCallBack) {
    var tracker: SelectionTracker<Long>? = null

    class ViewHolder(private val binding: ListItemTestBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(test: Test, clickListener: TestClickListener, isActivated: Boolean) {
            binding.executePendingBindings()
            binding.test = test
            binding.clickListener = clickListener
            binding.testSubject.text = test.subject
            binding.testLocation.text = test.locationName
            binding.testRoom.text = test.room
            val testDate = "${test.day}/${test.month.plus(1)}/${test.year}"
            binding.testDate.text = testDate
            val testTime = "${test.hour}:${test.minute}"
            binding.testTime.text = testTime
            itemView.isActivated = isActivated
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> {
            return object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getPosition(): Int {
                    return adapterPosition
                }

                override fun getSelectionKey(): Long? {
                    return itemId
                }

            }
        }
    }

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemTestBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val test = getItem(position)
        tracker?.let{ holder.bind(test,clickListener,it.isSelected(position.toLong())) }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    companion object DiffUtilCallBack : DiffUtil.ItemCallback<Test>() {
        override fun areItemsTheSame(oldItem: Test, newItem: Test): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Test, newItem: Test): Boolean {
            return false
        }

    }
}

class TestClickListener(val clickListener: (test:Test)->Unit) {
    fun onClick(test:Test) = clickListener(test)
}
