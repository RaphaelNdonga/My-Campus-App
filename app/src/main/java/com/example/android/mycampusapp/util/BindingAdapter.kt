package com.example.android.mycampusapp.util

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.data.*
import com.example.android.mycampusapp.display.days.friday.FridayAdapter
import com.example.android.mycampusapp.display.days.friday.FridayDataStatus
import com.example.android.mycampusapp.display.days.monday.MondayAdapter
import com.example.android.mycampusapp.display.days.monday.MondayDataStatus
import com.example.android.mycampusapp.display.days.saturday.SaturdayAdapter
import com.example.android.mycampusapp.display.days.saturday.SaturdayDataStatus
import com.example.android.mycampusapp.display.days.sunday.SundayAdapter
import com.example.android.mycampusapp.display.days.sunday.SundayDataStatus
import com.example.android.mycampusapp.display.days.thursday.ThursdayAdapter
import com.example.android.mycampusapp.display.days.thursday.ThursdayDataStatus
import com.example.android.mycampusapp.display.days.tuesday.TuesdayAdapter
import com.example.android.mycampusapp.display.days.tuesday.TuesdayDataStatus
import com.example.android.mycampusapp.display.days.wednesday.WednesdayAdapter
import com.example.android.mycampusapp.display.days.wednesday.WednesdayDataStatus

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
@BindingAdapter("mondayDataStatus")
fun bindMondayStatus(statusImgView:ImageView,mondayStatus:MondayDataStatus?){
    when(mondayStatus){
        MondayDataStatus.EMPTY->{
            statusImgView.visibility = View.VISIBLE
            statusImgView.setImageResource(R.drawable.ic_no_classes)
        }
        MondayDataStatus.NOT_EMPTY -> {
            statusImgView.visibility = View.GONE
        }
    }
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
@BindingAdapter("tuesdayDataStatus")
fun bindTuesdayDataStatus(statusImgView: ImageView, status: TuesdayDataStatus?){
    when(status){
        TuesdayDataStatus.EMPTY ->{
            statusImgView.visibility = View.VISIBLE
            statusImgView.setImageResource(R.drawable.ic_no_classes)
        }
        TuesdayDataStatus.NOT_EMPTY -> {
            statusImgView.visibility = View.GONE
        }
    }
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
@BindingAdapter("wednesdayDataStatus")
fun bindWednesdayDataStatus(statusImgView: ImageView, status: WednesdayDataStatus?){
    when(status){
        WednesdayDataStatus.EMPTY -> {
            statusImgView.visibility = View.VISIBLE
            statusImgView.setImageResource(R.drawable.ic_no_classes)
        }
        WednesdayDataStatus.NOT_EMPTY -> {
            statusImgView.visibility = View.GONE
        }
    }
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
@BindingAdapter("thursdayDataStatus")
fun bindThursdayDataStatus(statusImgView: ImageView, status: ThursdayDataStatus?){
    when(status){
        ThursdayDataStatus.EMPTY -> {
            statusImgView.visibility = View.VISIBLE
            statusImgView.setImageResource(R.drawable.ic_no_classes)
        }
        ThursdayDataStatus.NOT_EMPTY -> {
            statusImgView.visibility = View.GONE
        }
    }
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
@BindingAdapter("fridayDataStatus")
fun bindFridayDataStatus(statusImgView: ImageView, status: FridayDataStatus?){
    when(status){
        FridayDataStatus.EMPTY->{
            statusImgView.visibility = View.VISIBLE
            statusImgView.setImageResource(R.drawable.ic_no_classes)
        }
        FridayDataStatus.NOT_EMPTY->{
            statusImgView.visibility = View.GONE
        }
    }
}

@BindingAdapter("saturdaySubject")
fun TextView.bindSaturdaySubject(item:SaturdayClass?){
    text = item?.subject
}
@BindingAdapter("saturdayTime")
fun TextView.bindSaturdayTime(item:SaturdayClass?){
    text = item?.time
}
@BindingAdapter("saturdayListData")
fun bindSaturdayRecyclerView(recyclerView: RecyclerView, data: List<SaturdayClass>?){
    val adapter = recyclerView.adapter as SaturdayAdapter
    adapter.submitList(data)
}
@BindingAdapter("saturdayDataStatus")
fun bindSaturdayDataStatus(statusImgView: ImageView, status: SaturdayDataStatus?){
    when(status){
        SaturdayDataStatus.EMPTY ->{
            statusImgView.visibility = View.VISIBLE
            statusImgView.setImageResource(R.drawable.ic_no_classes)
        }
        SaturdayDataStatus.NOT_EMPTY ->{
            statusImgView.visibility = View.GONE
        }
    }
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
@BindingAdapter("sundayDataStatus")
fun bindSundayDataStatus(statusImgView: ImageView, status: SundayDataStatus?){
    when(status){
        SundayDataStatus.EMPTY -> {
            statusImgView.visibility = View.VISIBLE
            statusImgView.setImageResource(R.drawable.ic_no_classes)
        }
        SundayDataStatus.NOT_EMPTY -> {
            statusImgView.visibility = View.GONE
        }
    }
}



