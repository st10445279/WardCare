package com.wardcare.app.ui.notifications

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wardcare.app.data.model.AppNotification
import com.wardcare.app.databinding.ItemNotificationBinding

class NotificationAdapter :
    ListAdapter<AppNotification, NotificationAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(notification: AppNotification) {
            binding.tvNotificationTitle.text = notification.title
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<AppNotification>() {
        override fun areItemsTheSame(
            oldItem: AppNotification,
            newItem: AppNotification
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: AppNotification,
            newItem: AppNotification
        ): Boolean {
            return oldItem == newItem
        }
    }
}
