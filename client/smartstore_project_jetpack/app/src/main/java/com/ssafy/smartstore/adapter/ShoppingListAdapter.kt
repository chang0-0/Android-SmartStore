package com.ssafy.smartstore.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.smartstore.config.ApplicationClass
import com.ssafy.smartstore.databinding.ListItemShoppingListBinding
import com.ssafy.smartstore.dto.OrderDetail
import com.ssafy.smartstore.fragment.ShoppingListFragment

private const val TAG = "ShoppingListAdapter_싸피"

class ShoppingListAdapter(
    val context: Context,
    var fragment: ShoppingListFragment
) : RecyclerView.Adapter<ShoppingListAdapter.ShoppingListHolder>() {
    var list: MutableList<OrderDetail> = mutableListOf()
    private lateinit var binding: ListItemShoppingListBinding

    inner class ShoppingListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindInfo(orderDetail: OrderDetail) {
            Glide.with(itemView)
                .load("${ApplicationClass.MENU_IMGS_URL}${orderDetail.img}")
                .into(binding.menuImage)

            binding.textShoppingMenuName.text = orderDetail.productName
            binding.textShoppingMenuMoney.text = orderDetail.unitPrice.toString() + "원"
            binding.textShoppingMenuCount.text = orderDetail.quantity.toString() + "잔"
            binding.textShoppingMenuMoneyAll.text = (orderDetail.quantity * orderDetail.unitPrice).toString() + "원"

            binding.cancel.setOnClickListener {
                fragment.shoppingListDelete(orderDetail)
                notifyDataSetChanged()
                fragment.moneyAndCountRefresh(list)
            }
        } // End of bindInfo
    } // End of ShoppingListHolder inner class

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingListHolder {
        binding = ListItemShoppingListBinding.inflate(LayoutInflater.from(parent.context))
        return ShoppingListHolder(binding.root)
    } // End of onCreateViewHolder

    override fun onBindViewHolder(holder: ShoppingListHolder, position: Int) {
        holder.bindInfo(list[position])
    } // End of onBindViewHolder

    override fun getItemCount(): Int {
        return list.size
    } // End of getItemCount

    @SuppressLint("NotifyDataSetChanged")
    internal fun setData(newItems: MutableList<OrderDetail>) {
        this.list = newItems
        notifyDataSetChanged()
    }
} // End of ShoppingListAdapter class
