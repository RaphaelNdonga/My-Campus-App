package com.example.android.mycampusapp.timetable.display.days.wednesday

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView

class WednesdayItemDetailsLookup(private val recyclerView:RecyclerView):ItemDetailsLookup<Long>() {
    override fun getItemDetails(event: MotionEvent): ItemDetails<Long>? {
        val view = recyclerView.findChildViewUnder(event.x,event.y)
        if (view!=null){
            return (recyclerView.getChildViewHolder(view) as WednesdayAdapter.ViewHolder).getItemDetails()
        }
        return null
    }
}