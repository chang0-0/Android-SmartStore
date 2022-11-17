package com.ssafy.smartstore.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.smartstore.R
import com.ssafy.smartstore.config.ApplicationClass
import com.ssafy.smartstore.databinding.ListItemLatestOrderBinding
import com.ssafy.smartstore.response.LatestOrderResponse
import com.ssafy.smartstore.util.CommonUtils

private const val TAG = "LatestOrderAdapter_싸피"

class LatestOrderAdapter(val context: Context, val list: List<LatestOrderResponse>) :
    RecyclerView.Adapter<LatestOrderAdapter.LatestOrderHolder>() {
    private lateinit var binding: ListItemLatestOrderBinding
    private lateinit var adapterItemClickListener: AdapterItemClickListener

    inner class LatestOrderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val menuImage = itemView.findViewById<ImageView>(R.id.menuImage)
        val textMenuNames = itemView.findViewById<TextView>(R.id.textMenuNames)
        val textMenuPrice = itemView.findViewById<TextView>(R.id.textMenuPrice)
        val textMenuDate = itemView.findViewById<TextView>(R.id.textMenuDate)

        fun bindInfo(data: LatestOrderResponse) {
            Log.d(TAG, "bindInfo: data: ${data}")
            Glide.with(itemView).load("${ApplicationClass.MENU_IMGS_URL}${data.img}")
                .into(menuImage)

            if (data.orderCnt > 1) {
                textMenuNames.text = "${data.productName} 외 ${data.orderCnt - 1}건"  //외 x건
            } else {
                textMenuNames.text = data.productName
            }

            textMenuPrice.text = CommonUtils.makeComma(data.totalPrice)
            textMenuDate.text = CommonUtils.getFormattedString(data.orderDate)

            //클릭연결
            itemView.setOnClickListener {
                adapterItemClickListener.onClick(it, layoutPosition, data.orderId)
            }
        } // End of bindInfo
    } // End of LatestOrderHolder inner class

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LatestOrderHolder {
        binding = ListItemLatestOrderBinding.inflate(LayoutInflater.from(parent.context))
        return LatestOrderHolder(binding.root)
    } // End of onCreateViewHolder

    override fun onBindViewHolder(holder: LatestOrderHolder, position: Int) {
        holder.bindInfo(list[position])
    } // End of onBindViewHolder

    override fun getItemCount(): Int {
        return list.size
    } // End of getItemCount

    // 메소드 구현.
    fun setItemClickListener(adapterItemClickListener: AdapterItemClickListener) {
        this.adapterItemClickListener = adapterItemClickListener
    } // End of setItemClickListener
} // End of LatestOrderAdapter class
