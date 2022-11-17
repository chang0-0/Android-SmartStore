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
import com.ssafy.smartstore.activity.MainActivity
import com.ssafy.smartstore.config.ApplicationClass
import com.ssafy.smartstore.dto.Order
import com.ssafy.smartstore.dto.OrderDetail
import com.ssafy.smartstore.fragment.ShoppingListFragment

private const val TAG = "ShoppingListAdapter_싸피"

class ShoppingListAdapter(
    val context: Context,
    var list: MutableList<OrderDetail>,
    var fragment: ShoppingListFragment
) : RecyclerView.Adapter<ShoppingListAdapter.ShoppingListHolder>() {

    inner class ShoppingListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val menuName = itemView.findViewById<TextView>(R.id.textShoppingMenuName)
        val menuMoney = itemView.findViewById<TextView>(R.id.textShoppingMenuMoney)
        val menuCount = itemView.findViewById<TextView>(R.id.textShoppingMenuCount)
        val totalMoney = itemView.findViewById<TextView>(R.id.textShoppingMenuMoneyAll)
        val menuImage = itemView.findViewById<ImageView>(R.id.menuImage)
        val cancelBtn = itemView.findViewById<ImageView>(R.id.cancel)

        fun bindInfo(orderDetail: OrderDetail) {
            Glide.with(itemView)
                .load("${ApplicationClass.MENU_IMGS_URL}${orderDetail.img}")
                .into(menuImage)

            menuName.text = orderDetail.productName
            menuMoney.text = orderDetail.unitPrice.toString() + "원"
            menuCount.text = orderDetail.quantity.toString() + "잔"
            totalMoney.text = (orderDetail.quantity * orderDetail.unitPrice).toString() + "원"

            cancelBtn.setOnClickListener {
                list.remove(orderDetail)
                notifyDataSetChanged()
                fragment.moneyAndCountRefresh(list)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingListHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_shopping_list, parent, false)
        return ShoppingListHolder(view)
    }

    override fun onBindViewHolder(holder: ShoppingListHolder, position: Int) {
        holder.bindInfo(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
