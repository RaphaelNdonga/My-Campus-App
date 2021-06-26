package com.mycampusapp.links

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView

class LinkItemDetailsLookup(private val linksRecyclerView: RecyclerView) : ItemDetailsLookup<Long>() {
    override fun getItemDetails(e: MotionEvent): ItemDetails<Long>? {
        val view = linksRecyclerView.findChildViewUnder(e.x,e.y)
        if(view != null){
            return (linksRecyclerView.getChildViewHolder(view) as EssentialLinksViewHolder).getItemDetails()
        }
        return null
    }

}
