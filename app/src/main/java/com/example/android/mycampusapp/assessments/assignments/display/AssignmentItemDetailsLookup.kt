package com.example.android.mycampusapp.assessments.assignments.display

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import com.example.android.mycampusapp.assessments.AssessmentsAdapter

class AssignmentItemDetailsLookup(private val recyclerView: RecyclerView) :
    ItemDetailsLookup<Long>() {
    override fun getItemDetails(event: MotionEvent): ItemDetails<Long>? {
        val view = recyclerView.findChildViewUnder(event.x, event.y)
        if (view != null) {
            return (recyclerView.getChildViewHolder(view) as AssessmentsAdapter.ViewHolder).getItemDetails()
        }
        return null
    }
}