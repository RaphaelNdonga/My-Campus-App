package com.example.android.mycampusapp.assessments.tests.display

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import com.example.android.mycampusapp.assessments.AssessmentsAdapter

class TestItemDetailsLookup(private val recyclerView: RecyclerView): ItemDetailsLookup<Long>() {
    override fun getItemDetails(e: MotionEvent): ItemDetails<Long>? {
        val view = recyclerView.findChildViewUnder(e.x,e.y)
        if(view!=null){
            return (recyclerView.getChildViewHolder(view) as AssessmentsAdapter.ViewHolder).getItemDetails()
        }
        return null
    }
}