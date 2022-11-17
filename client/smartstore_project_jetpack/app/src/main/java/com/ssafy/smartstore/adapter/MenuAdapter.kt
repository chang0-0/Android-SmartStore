
package com.ssafy.smartstore.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ssafy.smartstore.R
import com.ssafy.smartstore.config.ApplicationClass
import com.ssafy.smartstore.dto.Product

private const val TAG = "MenuAdapter_싸피"
class MenuAdapter(var productList:List<Product>) :RecyclerView.Adapter<MenuAdapter.MenuHolder>(){
    private lateinit var adapterItemClickListener: AdapterItemClickListener //클릭리스너 선언

    inner class MenuHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val menuName = itemView.findViewById<TextView>(R.id.textMenuNames)
        val menuImage = itemView.findViewById<ImageView>(R.id.menuImage)

        fun bindInfo(product : Product){
            menuName.text = product.name
            Glide.with(itemView)
                .load("${ApplicationClass.MENU_IMGS_URL}${product.img}")
                .into(menuImage)

            itemView.setOnClickListener{
                adapterItemClickListener.onClick(it, layoutPosition, productList[layoutPosition].id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_menu, parent, false)
        return MenuHolder(view)
    }

    override fun onBindViewHolder(holder: MenuHolder, position: Int) {
        holder.apply{
            bindInfo(productList[position])
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }
    
    //클릭리스너 등록 매소드
    fun setItemClickListener(adapterItemClickListener: AdapterItemClickListener) {
        this.adapterItemClickListener = adapterItemClickListener
    }
}

