package com.ssafy.smartstore.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.smartstore.databinding.ListItemNoticeBinding


class NoticeAdapter : RecyclerView.Adapter<NoticeAdapter.NoticeHolder>() {
    private lateinit var binding: ListItemNoticeBinding

    inner class NoticeHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindInfo() {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeHolder {
        binding = ListItemNoticeBinding.inflate(LayoutInflater.from(parent.context))
        return NoticeHolder(binding.root)
    }

    override fun onBindViewHolder(holder: NoticeHolder, position: Int) {
        holder.bindInfo()
    }

    override fun getItemCount(): Int {
        return 10
    }
}

