package com.example.android.mycampusapp.util

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.assessments.assignments.display.AssignmentsAdapter
import com.example.android.mycampusapp.assessments.tests.display.TestsAdapter
import com.example.android.mycampusapp.data.Assignment
import com.example.android.mycampusapp.data.DataStatus
import com.example.android.mycampusapp.data.Test
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

@BindingAdapter("assignmentListData")
fun bindAssignmentRecyclerView(recyclerView: RecyclerView,data:List<Assignment>?){
    val adapter = recyclerView.adapter as AssignmentsAdapter
    adapter.submitList(data)
}

@BindingAdapter("testsListData")
fun bindTestsListData(recyclerView: RecyclerView,data:List<Test>?){
    val adapter = recyclerView.adapter as TestsAdapter
    adapter.submitList(data)
}



