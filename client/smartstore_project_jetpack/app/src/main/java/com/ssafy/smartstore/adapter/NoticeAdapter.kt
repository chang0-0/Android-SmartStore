package com.ssafy.smartstore.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.smartstore.databinding.ListItemNoticeBinding
import com.ssafy.smartstore.dto.Order
import com.ssafy.smartstore.fragment.HomeFragment

class NoticeAdapter(val fragment: HomeFragment) : RecyclerView.Adapter<NoticeAdapter.NoticeHolder>() {
    private lateinit var binding: ListItemNoticeBinding
    var list: List<Order> = emptyList()

    inner class NoticeHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindInfo(data: Order) {
            if(data.totalQuantity > 1) {
                binding.textNoticeContent.text = "${data.topProductName} 외 ${data.totalQuantity - 1}건"
            } else {
                binding.textNoticeContent.text = data.topProductName
            }

            binding.imageNoticeCancel.setOnClickListener {
                fragment.deleteNotice(data)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeHolder {
        binding = ListItemNoticeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoticeHolder(binding.root)
    }


    override fun onBindViewHolder(holder: NoticeHolder, position: Int) {
        holder.bindInfo(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("NotifyDataSetChanged")
    internal fun setData(newItems: List<Order>) {
        this.list = newItems
        notifyDataSetChanged()
    }
}

