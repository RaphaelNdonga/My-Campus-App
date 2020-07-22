package com.example.android.mycampusapp.util

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.mycampusapp.data.*
import com.example.android.mycampusapp.display.days.friday.FridayAdapter
import com.example.android.mycampusapp.display.days.monday.MondayAdapter
import com.example.android.mycampusapp.display.days.sunday.SundayAdapter
import com.example.android.mycampusapp.display.days.thursday.ThursdayAdapter
import com.example.android.mycampusapp.display.days.tuesday.TuesdayAdapter
import com.example.android.mycampusapp.display.days.wednesday.WednesdayAdapter

@BindingAdapter("mondaySubject")
fun TextView.bindMondaySubject(item:MondayClass?){
    item?.let {
        text = item.subject
    }
}
@BindingAdapter("mondayTime")
fun TextView.bindMondayTime(item:MondayClass?){
    item?.let {
        text = item.time
    }
}
@BindingAdapter("mondayListData")
fun bindMondayRecyclerView(recyclerView: RecyclerView,data:List<MondayClass>?){
    val adapter:MondayAdapter = recyclerView.adapter as MondayAdapter
    adapter.submitList(data)
}

@BindingAdapter("tuesdaySubject")
fun TextView.bindTuesdaySubject(item: TuesdayClass?){
    item?.let{
        text = item.subject
    }
}

@BindingAdapter("tuesdayTime")
fun TextView.bindTuesdayTime(item: TuesdayClass?){
    item?.let {
        text = item.time
    }
}

@BindingAdapter("tuesdayListData")
fun bindTuesdayRecyclerView(recyclerView:RecyclerView, data: List<TuesdayClass>?){
    val adapter = recyclerView.adapter as TuesdayAdapter
    adapter.submitList(data)
}

@BindingAdapter("wednesdaySubject")
fun TextView.bindWednesdaySubject(item: WednesdayClass?){
    text = item?.subject
}
@BindingAdapter("wednesdayTime")
fun TextView.bindWednesdayTime(item:WednesdayClass?){
    text = item?.time
}
@BindingAdapter("wednesdayListData")
fun bindWednesdayRecyclerView(recyclerView: RecyclerView,data: List<WednesdayClass>?){
    val adapter = recyclerView.adapter as WednesdayAdapter
    adapter.submitList(data)
}

@BindingAdapter("thursdaySubject")
fun TextView.bindThursdaySubject(item:ThursdayClass?){
    text = item?.subject
}
@BindingAdapter("thursdayTime")
fun TextView.bindThursdayTime(item:ThursdayClass?){
    text = item?.time
}
@BindingAdapter("thursdayListData")
fun bindThursdayRecyclerView(recyclerView:RecyclerView, data:List<ThursdayClass>?){
    val adapter = recyclerView.adapter as ThursdayAdapter
    adapter.submitList(data)
}

@BindingAdapter("fridaySubject")
fun TextView.bindFridaySubject(item:FridayClass?){
    text = item?.subject
}
@BindingAdapter("fridayTime")
fun TextView.bindFridayTime(item:FridayClass?){
    text = item?.time
}
@BindingAdapter("fridayListData")
fun bindFridayRecyclerview(recyclerView: RecyclerView, data: List<FridayClass>?){
    val adapter = recyclerView.adapter as FridayAdapter
    adapter.submitList(data)
}

@BindingAdapter("sundaySubject")
fun TextView.bindSundaySubject(item:SundayClass?){
    text = item?.subject
}
@BindingAdapter("sundayTime")
fun TextView.bindSundayTime(item:SundayClass?){
    text = item?.time
}
@BindingAdapter("sundayListData")
fun bindRecyclerView(recyclerView: RecyclerView,data: List<SundayClass>?){
    val adapter = recyclerView.adapter as SundayAdapter
    adapter.submitList(data)
}


