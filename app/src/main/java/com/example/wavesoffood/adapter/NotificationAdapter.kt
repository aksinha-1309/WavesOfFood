package com.example.wavesoffood.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wavesoffood.databinding.NotificationItemBinding

class NotificationAdapter(private var notification: ArrayList<String>, private  var  notificationImage: ArrayList<Int>): RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): NotificationViewHolder {
        val binding = NotificationItemBinding.inflate(LayoutInflater.from(p0.context),p0,false)
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(
        p0: NotificationViewHolder,
        p1: Int
    ) {
       p0.bind(p1)
    }

    override fun getItemCount(): Int =notification.size
    inner class NotificationViewHolder(private val binding: NotificationItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(p1: Int) {

            binding.apply {
                notificationtextView.text =notification[p1]
                notificationimageView.setImageResource(notificationImage[p1])
            }
        }

    }
}