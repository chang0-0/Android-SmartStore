package com.ssafy.smartstore.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.smartstore.config.ApplicationClass.Companion.sharedPreferencesUtil
import com.ssafy.smartstore.databinding.ListItemCommentBinding
import com.ssafy.smartstore.dto.Comment
import com.ssafy.smartstore.fragment.MenuDetailFragment
import com.ssafy.smartstore.response.MenuDetailWithCommentResponse

class CommentAdapter(val productId: Int, val fragment: MenuDetailFragment) :RecyclerView.Adapter<CommentAdapter.ViewHolder>(){
    var list: List<MenuDetailWithCommentResponse> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setItem(list[position])
    }

    inner class ViewHolder(private val binding: ListItemCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setItem(item: MenuDetailWithCommentResponse) {
            binding.textNoticeContent.text = item.commentContent
            listView(item)
        }

        private fun listView(item: MenuDetailWithCommentResponse) {
            val user = sharedPreferencesUtil.getUser()
            val comment = Comment(item.commentId, item.userId.toString(), productId, item.productRating.toFloat(), item.commentContent.toString())

            binding.etCommentContent.visibility = View.GONE

            binding.ivModifyComment.visibility = View.GONE
            binding.ivDeleteComment.visibility = View.GONE
            binding.ivModifyAcceptComment.visibility = View.GONE
            binding.ivModifyCancelComment.visibility = View.GONE

            if(user.id == item.userId) {
                binding.ivModifyComment.visibility = View.VISIBLE
                binding.ivDeleteComment.visibility = View.VISIBLE

                binding.ivModifyComment.setOnClickListener {
                    binding.etCommentContent.apply {
                        setText(item.commentContent)
                        visibility = View.VISIBLE
                    }
                    binding.textNoticeContent.visibility = View.GONE

                    binding.ivModifyComment.visibility = View.GONE
                    binding.ivDeleteComment.visibility = View.GONE
                    binding.ivModifyAcceptComment.visibility = View.VISIBLE
                    binding.ivModifyCancelComment.visibility = View.VISIBLE
                }

                binding.ivDeleteComment.setOnClickListener {
                    fragment.delete(comment)
                }

                binding.ivModifyAcceptComment.setOnClickListener {
                    comment.comment = binding.etCommentContent.text.toString()
                    fragment.update(comment)

                    binding.textNoticeContent.visibility = View.VISIBLE
                    binding.etCommentContent.visibility = View.GONE

                    binding.ivModifyComment.visibility = View.VISIBLE
                    binding.ivDeleteComment.visibility = View.VISIBLE
                    binding.ivModifyAcceptComment.visibility = View.GONE
                    binding.ivModifyCancelComment.visibility = View.GONE
                }

                binding.ivModifyCancelComment.setOnClickListener {
                    binding.textNoticeContent.visibility = View.VISIBLE
                    binding.etCommentContent.visibility = View.GONE

                    binding.ivModifyComment.visibility = View.VISIBLE
                    binding.ivDeleteComment.visibility = View.VISIBLE
                    binding.ivModifyAcceptComment.visibility = View.GONE
                    binding.ivModifyCancelComment.visibility = View.GONE
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