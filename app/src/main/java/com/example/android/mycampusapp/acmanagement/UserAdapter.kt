package com.example.android.mycampusapp.acmanagement

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.mycampusapp.data.UserEmail
import com.example.android.mycampusapp.databinding.ListItemUserBinding

class UserAdapter(private val userListener: UserListener) :
    ListAdapter<UserEmail, UserAdapter.UserViewHolder>(DiffUtilCallback) {
    class UserViewHolder(private val binding: ListItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(currentItem: UserEmail) {
            binding.userEmail.text = currentItem.email
        }
    }

    object DiffUtilCallback : DiffUtil.ItemCallback<UserEmail>() {
        override fun areItemsTheSame(oldItem: UserEmail, newItem: UserEmail): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: UserEmail, newItem: UserEmail): Boolean {
            return oldItem.email == newItem.email
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemUserBinding.inflate(layoutInflater, parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
        holder.itemView.setOnClickListener {
            userListener.onClick(currentItem)
        }
    }

}

class UserListener(val clickListener: (UserEmail) -> Unit) {
    fun onClick(userEmail: UserEmail) {
        clickListener(userEmail)
    }
}