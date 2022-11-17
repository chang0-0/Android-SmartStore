package com.ssafy.smartstore.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.smartstore.config.ApplicationClass
import com.ssafy.smartstore.databinding.ListItemOrderBinding
import com.ssafy.smartstore.response.LatestOrderResponse
import com.ssafy.smartstore.util.CommonUtils

private const val TAG = "OrderAdapter_싸피"

class OrderAdapter(val context: Context, val list: List<LatestOrderResponse>) :
    RecyclerView.Adapter<OrderAdapter.OrderHolder>() {
    private lateinit var binding: ListItemOrderBinding

    inner class OrderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindInfo(data: LatestOrderResponse) {
            Log.d(TAG, "bindInfo: ${data}")

            Glide.with(itemView)
                .load("${ApplicationClass.MENU_IMGS_URL}${data.img}")
                .into(binding.menuImage)

            if (data.orderCnt > 1) {
                binding.textMenuNames.text = "${data.productName} 외 ${data.orderCnt - 1}건"  //외 x건
            } else {
                binding.textMenuNames.text = data.productName
            }

            binding.textMenuPrice.text = CommonUtils.makeComma(data.totalPrice)
            binding.textMenuDate.text = CommonUtils.getFormattedString(data.orderDate)
            binding.textCompleted.text = CommonUtils.isOrderCompleted(data)
            //클릭연결
            itemView.setOnClickListener {
                Log.d(TAG, "bindInfo: ${data.orderId}")
                adapterItemClickListener.onClick(it, layoutPosition, data.orderId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHolder {
        binding = ListItemOrderBinding.inflate(LayoutInflater.from(parent.context))
        return OrderHolder(binding.root)
    }

    override fun onBindViewHolder(holder: OrderHolder, position: Int) {
        holder.bindInfo(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private lateinit var adapterItemClickListener: AdapterItemClickListener

    //클릭리스너 등록 매소드
    fun setItemClickListener(adapterItemClickListener: AdapterItemClickListener) {
        this.adapterItemClickListener = adapterItemClickListener
    }
}

