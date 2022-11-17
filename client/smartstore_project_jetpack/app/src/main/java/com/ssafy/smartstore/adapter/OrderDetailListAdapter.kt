package com.ssafy.smartstore.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.smartstore.R
import com.ssafy.smartstore.config.ApplicationClass
import com.ssafy.smartstore.databinding.ListItemOrderBinding
import com.ssafy.smartstore.databinding.ListItemOrderDetailListBinding
import com.ssafy.smartstore.dto.OrderDetail
import com.ssafy.smartstore.response.OrderDetailResponse
import com.ssafy.smartstore.util.CommonUtils


class OrderDetailListAdapter(val context: Context, val orderDetail: List<OrderDetailResponse>) :
    RecyclerView.Adapter<OrderDetailListAdapter.OrderDetailListHolder>() {
    private lateinit var binding: ListItemOrderDetailListBinding

    inner class OrderDetailListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindInfo(data: OrderDetailResponse) {
            var type = if (data.productType == "coffee") "잔" else "개"

            Glide.with(itemView)
                .load("${ApplicationClass.MENU_IMGS_URL}${data.img}")
                .into(binding.menuImage)

            binding.textShoppingMenuName.text = data.productName
            binding.textShoppingMenuMoney.text = CommonUtils.makeComma(data.unitPrice)
            binding.textShoppingMenuCount.text = "${data.quantity} ${type}"
            binding.textShoppingMenuMoneyAll.text = CommonUtils.makeComma(data.unitPrice * data.quantity)
        } // End of bindInfo
    } // End of OrderDetailListHolder inner class

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderDetailListAdapter.OrderDetailListHolder {
        binding = ListItemOrderDetailListBinding.inflate(LayoutInflater.from(parent.context))
        return OrderDetailListHolder(binding.root)
    } // End of onCreateViewHolder

    override fun onBindViewHolder(holder: OrderDetailListHolder, position: Int) {
        holder.bindInfo(orderDetail[position])
    } // End of onBindViewHolder

    override fun getItemCount(): Int {
        return orderDetail.size
    } // End of getItemCount
} // End of OrderDetailListAdapter

