package com.example.android.mycampusapp.util

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.timetable.data.*
import com.example.android.mycampusapp.timetable.display.days.friday.FridayAdapter
import com.example.android.mycampusapp.timetable.display.days.friday.FridayDataStatus
import com.example.android.mycampusapp.timetable.display.days.monday.MondayAdapter
import com.example.android.mycampusapp.timetable.display.days.monday.MondayDataStatus
import com.example.android.mycampusapp.timetable.display.days.saturday.SaturdayAdapter
import com.example.android.mycampusapp.timetable.display.days.saturday.SaturdayDataStatus
import com.example.android.mycampusapp.timetable.display.days.sunday.SundayAdapter
import com.example.android.mycampusapp.timetable.display.days.sunday.SundayDataStatus
import com.example.android.mycampusapp.timetable.display.days.thursday.ThursdayAdapter
import com.example.android.mycampusapp.timetable.display.days.thursday.ThursdayDataStatus
import com.example.android.mycampusapp.timetable.display.days.tuesday.TuesdayAdapter
import com.example.android.mycampusapp.timetable.display.days.tuesday.TuesdayDataStatus
import com.example.android.mycampusapp.timetable.display.days.wednesday.WednesdayAdapter
import com.example.android.mycampusapp.timetable.display.days.wednesday.WednesdayDataStatus

@BindingAdapter("mondayListData")
fun bindMondayRecyclerView(recyclerView: RecyclerView, data: List<MondayClass>?) {
    val adapter: MondayAdapter = recyclerView.adapter as MondayAdapter
    adapter.submitList(data)
}

@BindingAdapter("mondayDataStatus")
fun bindMondayImageStatus(statusImgView: ImageView, mondayStatus: MondayDataStatus?) {
    when (mondayStatus) {
        MondayDataStatus.EMPTY -> {
            statusImgView.visibility = View.VISIBLE
            statusImgView.setImageResource(R.drawable.ic_no_classes)
        }
        MondayDataStatus.NOT_EMPTY -> {
            statusImgView.visibility = View.GONE
        }
        MondayDataStatus.LOADING -> {
            statusImgView.visibility = View.VISIBLE
            statusImgView.setImageResource(R.drawable.loading_animation)
        }
    }
}

@BindingAdapter("mondayTextStatus")
fun bindMondayTextStatus(statusTextView: TextView, mondayStatus: MondayDataStatus?) {
    when (mondayStatus) {
        MondayDataStatus.EMPTY -> {
            statusTextView.visibility = View.VISIBLE
        }
        MondayDataStatus.NOT_EMPTY -> {
            statusTextView.visibility = View.GONE
        }
        MondayDataStatus.LOADING -> {
            statusTextView.visibility = View.GONE
        }
    }
}

@BindingAdapter("tuesdayListData")
fun bindTuesdayRecyclerView(recyclerView: RecyclerView, data: List<TuesdayClass>?) {
    val adapter = recyclerView.adapter as TuesdayAdapter
    adapter.submitList(data)
}

@BindingAdapter("tuesdayDataStatus")
fun bindTuesdayImageStatus(statusImgView: ImageView, status: TuesdayDataStatus?) {
    when (status) {
        TuesdayDataStatus.EMPTY -> {
            statusImgView.visibility = View.VISIBLE
            statusImgView.setImageResource(R.drawable.ic_no_classes)
        }
        TuesdayDataStatus.NOT_EMPTY -> {
            statusImgView.visibility = View.GONE
        }
        TuesdayDataStatus.LOADING -> {
            statusImgView.visibility = View.VISIBLE
            statusImgView.setImageResource(R.drawable.loading_animation)
        }
    }
}

@BindingAdapter("tuesdayTextStatus")
fun bindTuesdayTextStatus(statusTextView: TextView, status: TuesdayDataStatus?) {
    when (status) {
        TuesdayDataStatus.EMPTY -> {
            statusTextView.visibility = View.VISIBLE
        }
        TuesdayDataStatus.NOT_EMPTY -> {
            statusTextView.visibility = View.GONE
        }
        TuesdayDataStatus.LOADING -> {
            statusTextView.visibility = View.GONE
        }
    }
}

@BindingAdapter("wednesdayListData")
fun bindWednesdayRecyclerView(recyclerView: RecyclerView, data: List<WednesdayClass>?) {
    val adapter = recyclerView.adapter as WednesdayAdapter
    adapter.submitList(data)
}

@BindingAdapter("wednesdayDataStatus")
fun bindWednesdayImageStatus(statusImgView: ImageView, status: WednesdayDataStatus?) {
    when (status) {
        WednesdayDataStatus.EMPTY -> {
            statusImgView.visibility = View.VISIBLE
            statusImgView.setImageResource(R.drawable.ic_no_classes)
        }
        WednesdayDataStatus.NOT_EMPTY -> {
            statusImgView.visibility = View.GONE
        }
        WednesdayDataStatus.LOADING -> {
            statusImgView.visibility = View.VISIBLE
            statusImgView.setImageResource(R.drawable.loading_animation)
        }
    }
}
@BindingAdapter("wednesdayTextStatus")
fun bindWednesdayTextStatus(statusTextView: TextView, status: WednesdayDataStatus?){
    when (status){
        WednesdayDataStatus.EMPTY -> {
            statusTextView.visibility = View.VISIBLE
        }
        WednesdayDataStatus.NOT_EMPTY -> {
            statusTextView.visibility = View.GONE
        }
        WednesdayDataStatus.LOADING -> {
            statusTextView.visibility = View.GONE
        }
    }
}

@BindingAdapter("thursdayListData")
fun bindThursdayRecyclerView(recyclerView: RecyclerView, data: List<ThursdayClass>?) {
    val adapter = recyclerView.adapter as ThursdayAdapter
    adapter.submitList(data)
}

@BindingAdapter("thursdayDataStatus")
fun bindThursdayImageStatus(statusImgView: ImageView, status: ThursdayDataStatus?) {
    when (status) {
        ThursdayDataStatus.EMPTY -> {
            statusImgView.visibility = View.VISIBLE
            statusImgView.setImageResource(R.drawable.ic_no_classes)
        }
        ThursdayDataStatus.NOT_EMPTY -> {
            statusImgView.visibility = View.GONE
        }
        ThursdayDataStatus.LOADING -> {
            statusImgView.visibility = View.VISIBLE
            statusImgView.setImageResource(R.drawable.loading_animation)
        }
    }
}
@BindingAdapter("thursdayTextStatus")
fun bindThursdayTextStatus(statusTextView: TextView, status: ThursdayDataStatus?){
    when(status){
        ThursdayDataStatus.EMPTY -> {
            statusTextView.visibility = View.VISIBLE
        }
        ThursdayDataStatus.NOT_EMPTY -> {
            statusTextView.visibility = View.GONE
        }
        ThursdayDataStatus.LOADING -> {
            statusTextView.visibility = View.GONE
        }
    }
}

@BindingAdapter("fridayListData")
fun bindFridayRecyclerview(recyclerView: RecyclerView, data: List<FridayClass>?) {
    val adapter = recyclerView.adapter as FridayAdapter
    adapter.submitList(data)
}

@BindingAdapter("fridayDataStatus")
fun bindFridayImageStatus(statusImgView: ImageView, status: FridayDataStatus?) {
    when (status) {
        FridayDataStatus.EMPTY -> {
            statusImgView.visibility = View.VISIBLE
            statusImgView.setImageResource(R.drawable.ic_no_classes)
        }
        FridayDataStatus.NOT_EMPTY -> {
            statusImgView.visibility = View.GONE
        }
        FridayDataStatus.LOADING -> {
            statusImgView.visibility = View.VISIBLE
            statusImgView.setImageResource(R.drawable.loading_animation)
        }
    }
}
@BindingAdapter("fridayTextStatus")
fun bindFridayTextStatus(statusTextView: TextView, status: FridayDataStatus?){
    when(status){
        FridayDataStatus.EMPTY -> {
            statusTextView.visibility = View.VISIBLE
        }
        FridayDataStatus.NOT_EMPTY -> {
            statusTextView.visibility = View.GONE
        }
        FridayDataStatus.LOADING -> {
            statusTextView.visibility = View.GONE
        }
    }
}

@BindingAdapter("saturdayListData")
fun bindSaturdayRecyclerView(recyclerView: RecyclerView, data: List<SaturdayClass>?) {
    val adapter = recyclerView.adapter as SaturdayAdapter
    adapter.submitList(data)
}

@BindingAdapter("saturdayDataStatus")
fun bindSaturdayImageStatus(statusImgView: ImageView, status: SaturdayDataStatus?) {
    when (status) {
        SaturdayDataStatus.EMPTY -> {
            statusImgView.visibility = View.VISIBLE
            statusImgView.setImageResource(R.drawable.ic_no_classes)
        }
        SaturdayDataStatus.NOT_EMPTY -> {
            statusImgView.visibility = View.GONE
        }
        SaturdayDataStatus.LOADING -> {
            statusImgView.visibility = View.VISIBLE
            statusImgView.setImageResource(R.drawable.loading_animation)
        }
    }
}
@BindingAdapter("saturdayTextStatus")
fun bindSaturdayTextStatus(statusTextView: TextView, status: SaturdayDataStatus?){
    when(status){
        SaturdayDataStatus.EMPTY -> {
            statusTextView.visibility = View.VISIBLE
        }
        SaturdayDataStatus.NOT_EMPTY -> {
            statusTextView.visibility = View.GONE
        }
        SaturdayDataStatus.LOADING -> {
            statusTextView.visibility = View.GONE
        }
    }
}

@BindingAdapter("sundayListData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<SundayClass>?) {
    val adapter = recyclerView.adapter as SundayAdapter
    adapter.submitList(data)
}

@BindingAdapter("sundayDataStatus")
fun bindSundayImageStatus(statusImgView: ImageView, status: SundayDataStatus?) {
    when (status) {
        SundayDataStatus.EMPTY -> {
            statusImgView.visibility = View.VISIBLE
            statusImgView.setImageResource(R.drawable.ic_no_classes)
        }
        SundayDataStatus.NOT_EMPTY -> {
            statusImgView.visibility = View.GONE
        }
        SundayDataStatus.LOADING -> {
            statusImgView.visibility = View.VISIBLE
            statusImgView.setImageResource(R.drawable.loading_animation)
        }
    }
}
@BindingAdapter("sundayTextStatus")
fun bindSundayTextStatus(statusTextView: TextView, status: SundayDataStatus?){
    when(status){
        SundayDataStatus.EMPTY -> {
            statusTextView.visibility = View.VISIBLE
        }
        SundayDataStatus.NOT_EMPTY -> {
            statusTextView.visibility = View.GONE
        }
        SundayDataStatus.LOADING -> {
            statusTextView.visibility = View.GONE
        }
    }
}



