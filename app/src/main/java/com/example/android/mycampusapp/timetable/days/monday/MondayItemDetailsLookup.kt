package com.example.android.mycampusapp.timetable.days.monday

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import com.example.android.mycampusapp.timetable.days.monday.MondayAdapter

class MondayItemDetailsLookup(private val recyclerView:RecyclerView):ItemDetailsLookup<Long>() {
    override fun getItemDetails(event: MotionEvent): ItemDetails<Long>? {
        val view = recyclerView.findChildViewUnder(event.x,event.y)
        if (view!=null){
            return (recyclerView.getChildViewHolder(view) as MondayAdapter.ViewHolder).getItemDetails()
        }
        return null
    }
}