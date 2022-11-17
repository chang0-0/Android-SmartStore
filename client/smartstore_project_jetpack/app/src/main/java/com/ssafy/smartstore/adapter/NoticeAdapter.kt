package com.ssafy.smartstore.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.smartstore.R


class NoticeAdapter :RecyclerView.Adapter<NoticeAdapter.NoticeHolder>(){

    inner class NoticeHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bindInfo(){

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_notice, parent, false)
        return NoticeHolder(view)
    }

    override fun onBindViewHolder(holder: NoticeHolder, position: Int) {
        holder.bindInfo()
    }

    override fun getItemCount(): Int {
        return 10
    }
}

