package com.example.android.mycampusapp.timetable.days.monday

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.mycampusapp.data.MondayClass
import com.example.android.mycampusapp.databinding.ListItemMondayBinding

class MondayAdapter(val clickListener:MondayListener) : ListAdapter<MondayClass, MondayAdapter.ViewHolder>(DiffUtilCallBack()) {

    class ViewHolder(val binding: ListItemMondayBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object{
            fun from(parent: ViewGroup):ViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemMondayBinding.inflate(layoutInflater,parent,false)
                return ViewHolder(binding)
            }
        }
        fun bind(mondayClass: MondayClass,clickListener: MondayListener){
            binding.executePendingBindings()
            binding.mondayClass = mondayClass
            binding.clickListener = clickListener
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentMondayClass = getItem(position)
        holder.bind(currentMondayClass, clickListener)
    }
}

class MondayListener(val clickListener:(mondayClass:MondayClass)->Unit) {
    fun onClick(mondayClass: MondayClass) = clickListener(mondayClass)
}

class DiffUtilCallBack : DiffUtil.ItemCallback<MondayClass>() {
    override fun areItemsTheSame(oldItem: MondayClass, newItem: MondayClass): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: MondayClass, newItem: MondayClass): Boolean {
        return oldItem == newItem
    }

}
