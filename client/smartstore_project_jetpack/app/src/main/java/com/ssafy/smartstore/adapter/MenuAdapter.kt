package com.ssafy.smartstore.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.smartstore.config.ApplicationClass
import com.ssafy.smartstore.databinding.ListItemMenuBinding
import com.ssafy.smartstore.dto.Product

private const val TAG = "MenuAdapter_싸피"

class MenuAdapter(var productList: List<Product>) : RecyclerView.Adapter<MenuAdapter.MenuHolder>() {
    private lateinit var binding: ListItemMenuBinding
    private lateinit var adapterItemClickListener: AdapterItemClickListener //클릭리스너 선언

    inner class MenuHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindInfo(product: Product) {
            binding.textMenuNames.text = product.name
            Glide.with(itemView)
                .load("${ApplicationClass.MENU_IMGS_URL}${product.img}")
                .into(binding.menuImage)

            itemView.setOnClickListener {
                adapterItemClickListener.onClick(it, layoutPosition, productList[layoutPosition].id)
            }
        }
    } // End of MenuHolder inner class

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuHolder {
        binding = ListItemMenuBinding.inflate(LayoutInflater.from(parent.context))
        return MenuHolder(binding.root)
    } // End of onCreateViewHolder

    override fun onBindViewHolder(holder: MenuHolder, position: Int) {
        holder.apply {
            bindInfo(productList[position])
        }
    } // End of onBindViewHolder

    override fun getItemCount(): Int {
        return productList.size
    } // End of getItemCount

    //클릭리스너 등록 매소드
    fun setItemClickListener(adapterItemClickListener: AdapterItemClickListener) {
        this.adapterItemClickListener = adapterItemClickListener
    } // End of setItemClickListener
} // End of MenuAdapter class

