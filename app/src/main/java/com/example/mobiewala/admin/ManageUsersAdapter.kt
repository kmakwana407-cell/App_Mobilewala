package com.example.mobiewala.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mobiewala.database.UserEntity
import com.example.mobiewala.databinding.ItemManageUserBinding

class ManageUsersAdapter(
    private val onDeleteClick: (UserEntity) -> Unit
) : ListAdapter<UserEntity, ManageUsersAdapter.UserViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemManageUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = getItem(position)
        // Do not show the admin user in the list
        if (user.role != "ADMIN") {
            holder.bind(user)
            holder.binding.deleteButton.setOnClickListener { onDeleteClick(user) }
        } else {
            holder.itemView.visibility = ViewGroup.GONE
            holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
        }
    }

    inner class UserViewHolder(val binding: ItemManageUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: UserEntity) {
            binding.userNameTextView.text = user.name
            binding.userEmailTextView.text = user.email
        }
    }

    class UserDiffCallback : DiffUtil.ItemCallback<UserEntity>() {
        override fun areItemsTheSame(oldItem: UserEntity, newItem: UserEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: UserEntity, newItem: UserEntity): Boolean {
            return oldItem == newItem
        }
    }
}