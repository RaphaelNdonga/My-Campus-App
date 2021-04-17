package com.example.android.mycampusapp.util

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.assessments.AssessmentsAdapter
import com.example.android.mycampusapp.data.Assessment
import com.example.android.mycampusapp.data.DataStatus
import com.example.android.mycampusapp.data.TimetableClass
import com.example.android.mycampusapp.timetable.display.TimetableAdapter

@BindingAdapter("dataStatusImage")
fun bindDataImageStatus(statusImgView: ImageView, status: DataStatus?) {
    when (status) {
        DataStatus.EMPTY -> {
            statusImgView.visibility = View.VISIBLE
            statusImgView.setImageResource(R.drawable.ic_no_classes)
        }
        DataStatus.NOT_EMPTY -> {
            statusImgView.visibility = View.GONE
        }
        DataStatus.LOADING -> {
            statusImgView.visibility = View.VISIBLE
            statusImgView.setImageResource(R.drawable.loading_animation)
        }
    }
}
@BindingAdapter("dataStatusText")
fun bindDataTextStatus(statusTextView: TextView, status: DataStatus?){
    when(status){
        DataStatus.EMPTY -> {
            statusTextView.visibility = View.VISIBLE
        }
        DataStatus.NOT_EMPTY -> {
            statusTextView.visibility = View.GONE
        }
        DataStatus.LOADING -> {
            statusTextView.visibility = View.GONE
        }
    }
}

@BindingAdapter("timetableListData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<TimetableClass>?) {
    val adapter = recyclerView.adapter as TimetableAdapter
    adapter.submitList(data)
}

@BindingAdapter("assessmentListData")
fun bindAssignmentRecyclerView(recyclerView: RecyclerView,data:List<Assessment>?){
    val adapter = recyclerView.adapter as AssessmentsAdapter
    adapter.submitList(data)
}



