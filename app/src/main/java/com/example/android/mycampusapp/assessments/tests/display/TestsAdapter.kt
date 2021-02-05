package com.example.android.mycampusapp.assessments.tests.display

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
import com.example.android.mycampusapp.data.Assessment
import com.example.android.mycampusapp.data.CustomDate
import com.example.android.mycampusapp.data.CustomTime
import com.example.android.mycampusapp.databinding.ListItemTestBinding
import com.example.android.mycampusapp.util.formatDate
import com.example.android.mycampusapp.util.formatTime

class TestsAdapter(private val clickListener:TestClickListener) : ListAdapter<Assessment, TestsAdapter.ViewHolder>(DiffUtilCallBack) {
    var tracker: SelectionTracker<Long>? = null

    class ViewHolder(private val binding: ListItemTestBinding,private val context: Context) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(assessment: Assessment, clickListener: TestClickListener, isActivated: Boolean) {
            binding.executePendingBindings()
            binding.test = assessment
            binding.clickListener = clickListener
            binding.testSubject.text = assessment.subject
            binding.testLocation.text = assessment.locationName
            binding.testRoom.text = assessment.room
            val testDate = CustomDate(assessment.year,assessment.month,assessment.day)
            binding.testDate.text = formatDate(testDate)
            val testTime = CustomTime(assessment.hour,assessment.minute)
            binding.testTime.text = formatTime(testTime)
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
        fun setMapListener(assessment: Assessment?) {
            val mapUri = Uri.parse(assessment?.locationCoordinates)
            val mapIntent = Intent(Intent.ACTION_VIEW, mapUri)
            mapIntent.setPackage("com.google.android.apps.maps")

            binding.testLocation.setOnClickListener {
                context.startActivity(mapIntent)
            }
        }
    }

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemTestBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding,parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val test = getItem(position)
        tracker?.let{ holder.bind(test,clickListener,it.isSelected(position.toLong())) }
        holder.setMapListener(test)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    companion object DiffUtilCallBack : DiffUtil.ItemCallback<Assessment>() {
        override fun areItemsTheSame(oldItem: Assessment, newItem: Assessment): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Assessment, newItem: Assessment): Boolean {
            return false
        }

    }
}

class TestClickListener(val clickListener: (assessment:Assessment)->Unit) {
    fun onClick(assessment:Assessment) = clickListener(assessment)
}
