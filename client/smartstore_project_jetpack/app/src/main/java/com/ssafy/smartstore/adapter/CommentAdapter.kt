package com.ssafy.smartstore.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.smartstore.config.ApplicationClass.Companion.sharedPreferencesUtil
import com.ssafy.smartstore.databinding.ListItemCommentBinding
import com.ssafy.smartstore.dto.Comment
import com.ssafy.smartstore.fragment.MenuDetailFragment
import com.ssafy.smartstore.response.MenuDetailWithCommentResponse

private const val TAG = "CommentAdapter_싸피_바인딩"
class  CommentAdapter(val productId: Int, val fragment: MenuDetailFragment) :RecyclerView.Adapter<CommentAdapter.ViewHolder>(){
    var list: List<MenuDetailWithCommentResponse> = emptyList()
    lateinit var binding : ListItemCommentBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ListItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setItem(list[position])
        holder.itemView.tag = list[position]
        Log.d(TAG, "onBindViewHolder: ${holder.itemView.tag}")
    }

    inner class ViewHolder(private val binding: ListItemCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setItem(item: MenuDetailWithCommentResponse) {
//            binding.textNoticeContent.text = item.commentContent
            binding.comment = item  // databinding
            binding.adapter = this
            listView(item)
        }

        private fun listView(item: MenuDetailWithCommentResponse) {
            val user = sharedPreferencesUtil.getUser()

            if(user.id == item.userId) {
                binding.ivModifyComment.visibility = View.VISIBLE
                binding.ivDeleteComment.visibility = View.VISIBLE
            }
        }

        fun onClickListener(item: MenuDetailWithCommentResponse, action: Int) {
            val comment = Comment(item.commentId, item.userId.toString(), productId, item.productRating.toFloat(), item.commentContent.toString())

            when(action) {
                0 -> {
                    item.isClicked = when(item.isClicked) {
                        false -> true
                        true -> false
                    }
                }
                1 -> {  // update
                    comment.comment = binding.etCommentContent.text.toString()
                    fragment.update(comment)
                    item.isClicked = when(item.isClicked) {
                        false -> true
                        true -> false
                    }
                }
                2 -> {  // delete
                    fragment.delete(comment)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("NotifyDataSetChanged")
    internal fun setData(newItems: List<MenuDetailWithCommentResponse>) {
        this.list = newItems
        notifyDataSetChanged()
    }
}