package com.ssafy.smartstore.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ssafy.smartstore.R
import com.ssafy.smartstore.config.ApplicationClass.Companion.sharedPreferencesUtil
import com.ssafy.smartstore.databinding.ListItemCommentBinding
import com.ssafy.smartstore.dto.Comment
import com.ssafy.smartstore.fragment.MenuDetailFragment
import com.ssafy.smartstore.response.MenuDetailWithCommentResponse
import com.ssafy.smartstore.response.MenuDetailWithCommentResponseList
import com.ssafy.smartstore.service.CommentService
import com.ssafy.smartstore.util.RetrofitCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "CommentAdapter_싸피"
class CommentAdapter(val productId: Int, val fragment: MenuDetailFragment) :RecyclerView.Adapter<CommentAdapter.ViewHolder>(){
    var list: List<MenuDetailWithCommentResponse> = emptyList()
//    inner class CommentHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
//        val commentTv = itemView.findViewById<TextView>(R.id.textNoticeContent)
//        val commentEdt = itemView.findViewById<EditText>(R.id.et_comment_content)
//
//        val commentAcceptBtn = itemView.findViewById<ImageView>(R.id.iv_modify_accept_comment)
//        val commentCancelBtn = itemView.findViewById<ImageView>(R.id.iv_modify_cancel_comment)
//        val commentModifyBtn = itemView.findViewById<ImageView>(R.id.iv_modify_comment)
//        val commentDeleteBtn = itemView.findViewById<ImageView>(R.id.iv_delete_comment)
//        val user = sharedPreferencesUtil.getUser()
//
//        fun bindInfo(data :MenuDetailWithCommentResponse){
////            commentTv.text = data.commentContent
////            commentEdt.visibility = View.GONE
//
////            // 자기가 등록한 댓글일 경우
////            if(user.id == data.userId) {
////                commentAcceptBtn.visibility = View.GONE
////                commentCancelBtn.visibility = View.GONE
////                commentModifyBtn.visibility = View.VISIBLE
////                commentDeleteBtn.visibility = View.VISIBLE
////
////                commentModifyBtn.setOnClickListener {
////                    commentEdt.setText(data.commentContent)
////                    commentEdt.visibility = View.VISIBLE
////                    commentTv.visibility = View.GONE
////
////                    commentAcceptBtn.visibility = View.VISIBLE
////                    commentCancelBtn.visibility = View.VISIBLE
////                    commentModifyBtn.visibility = View.GONE
////                    commentDeleteBtn.visibility = View.GONE
////                }
////
////                commentDeleteBtn.setOnClickListener {
////                    delete(data.commentId)
////                    Log.d(TAG, "bindInfo: delete 눌렀")
//////                    fragment.initData()
////                }
////
////                commentAcceptBtn.setOnClickListener {
////                    val updateComment = Comment(data.commentId, data.userId.toString(), productId, data.productRating.toFloat(), commentEdt.text.toString())
////                    CoroutineScope(Dispatchers.IO).launch { update(updateComment) }
////
////
////                    Log.d(TAG, "bindInfo: update 눌렀")
//////                    fragment.initData()
////
////                    commentAcceptBtn.visibility = View.GONE
////                    commentCancelBtn.visibility = View.GONE
////                    commentModifyBtn.visibility = View.VISIBLE
////                    commentDeleteBtn.visibility = View.VISIBLE
////
////                    commentTv.text = commentEdt.text.toString()
////                    commentTv.visibility = View.VISIBLE
////                    commentEdt.visibility = View.GONE
////                }
////
////                commentCancelBtn.setOnClickListener {
////                    commentAcceptBtn.visibility = View.GONE
////                    commentCancelBtn.visibility = View.GONE
////                    commentModifyBtn.visibility = View.VISIBLE
////                    commentDeleteBtn.visibility = View.VISIBLE
////                    commentTv.visibility = View.VISIBLE
////                    commentEdt.visibility = View.GONE
////                }
////            }
////            // 자기가 등록한 댓글이 아닐 경우
////            else {
////                commentAcceptBtn.visibility = View.GONE
////                commentCancelBtn.visibility = View.GONE
////                commentModifyBtn.visibility = View.GONE
////                commentDeleteBtn.visibility = View.GONE
////            }
//        }
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_comment, parent, false)
//        return CommentHolder(view)
        val binding = ListItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.bindInfo(list.list[position])
        holder.setItem(list[position])
    }

    inner class ViewHolder(private val binding: ListItemCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setItem(item: MenuDetailWithCommentResponse) {
            binding.textNoticeContent.text = item.commentContent
            binding.etCommentContent.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun update(comment: Comment) {
        CommentService().update(comment, UpdateCallback())
//        fragment.refreshCommentList()
    }

    inner class UpdateCallback: RetrofitCallback<Boolean> {
        override fun onSuccess( code: Int, bool: Boolean) {
            if (bool != null) {
                Log.d(TAG, "onSuccess: update 성공")
            }else{
                Log.d(TAG, "onSuccess: update 실패")
            }
        }

        override fun onError(t: Throwable) {
            Log.d(TAG, t.message ?: "comment update 중 통신오류")
        }

        override fun onFailure(code: Int) {
            Log.d(TAG, "onResponse: Error Code $code")
        }
    }

    private fun delete(commentId: Int) {
        CommentService().delete(commentId, DeleteCallback())
//        fragment.refreshCommentList()
    }

    inner class DeleteCallback: RetrofitCallback<Boolean> {
        override fun onSuccess( code: Int, bool: Boolean) {
            if (bool != null) {
                Log.d(TAG, "onSuccess: delete 성공")
            }else{
                Log.d(TAG, "onSuccess: delete 실패")
            }
        }

        override fun onError(t: Throwable) {
            Log.d(TAG, t.message ?: "comment delete 중 통신오류")
        }

        override fun onFailure(code: Int) {
            Log.d(TAG, "onResponse: Error Code $code")
        }
    }
}
