package com.mycampusapp.assessments

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mycampusapp.R
import com.mycampusapp.data.Assessment
import com.mycampusapp.data.CustomDate
import com.mycampusapp.data.CustomTime
import com.mycampusapp.databinding.ListItemAssessmentBinding
import com.mycampusapp.util.format24HourTime
import com.mycampusapp.util.formatAmPmTime
import com.mycampusapp.util.formatDate
import com.mycampusapp.util.getAssessmentTimeDifference
import java.util.concurrent.TimeUnit

class AssessmentsAdapter(private val clickListener: AssessmentsListener) :
    ListAdapter<Assessment, AssessmentsAdapter.ViewHolder>(DiffUtilCallBack) {

    var tracker: SelectionTracker<Long>? = null

    class ViewHolder(private val binding: ListItemAssessmentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemAssessmentBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

        fun bind(
            currentAssessment: Assessment,
            clickListener: AssessmentsListener,
            isActivated: Boolean = false
        ) {
            binding.executePendingBindings()
            binding.assessment = currentAssessment
            binding.assessmentSubject.text = currentAssessment.subject
            val date = formatDate(
                CustomDate(currentAssessment.year, currentAssessment.month, currentAssessment.day)
            )
            val time = formatTime(
                CustomTime(currentAssessment.hour, currentAssessment.minute)
            )
            binding.assessmentDate.text = date
            binding.assessmentTime.text = time
            binding.assessmentLocation.text = currentAssessment.locationName
            binding.assessmentRoom.text = currentAssessment.room

            val milliSecDifference = getAssessmentTimeDifference(currentAssessment)
            val dayDifference = TimeUnit.MILLISECONDS.toDays(milliSecDifference)

            binding.assessmentSubject.setTextColor(colorText(dayDifference))
            binding.assessmentDate.setTextColor(colorText(dayDifference))

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

            binding.assessmentLocation.setOnClickListener {
                this.itemView.context.startActivity(mapIntent)
            }
        }

        @ColorInt
        private fun colorText(dayDifference: Long): Int {
            if (dayDifference in 0..1) {
                return Color.RED
            }
            if (dayDifference in 2..6) {
                return ResourcesCompat.getColor(
                    itemView.context.resources,
                    R.color.yellow,
                    null
                )
            }
            if (dayDifference >= 7) {
                return ResourcesCompat.getColor(
                    itemView.context.resources,
                    R.color.colorPrimary,
                    null
                )
            }
            return Color.BLACK
        }
    }

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentassessment = getItem(position)
        tracker?.let {
            holder.bind(currentassessment, clickListener, it.isSelected(position.toLong()))
        }
        holder.setMapListener(currentassessment)
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

class AssessmentsListener(val clickListener: (assessment: Assessment) -> Unit) {
    fun onClick(assessment: Assessment) = clickListener(assessment)
}