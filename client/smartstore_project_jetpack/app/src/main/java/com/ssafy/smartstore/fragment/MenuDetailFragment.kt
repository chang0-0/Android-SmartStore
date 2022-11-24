package com.ssafy.smartstore.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.ssafy.smartstore.R
import com.ssafy.smartstore.activity.MainActivity
import com.ssafy.smartstore.adapter.CommentAdapter
import com.ssafy.smartstore.config.ApplicationClass
import com.ssafy.smartstore.databinding.FragmentMenuDetailBinding
import com.ssafy.smartstore.dto.Comment
import com.ssafy.smartstore.dto.OrderDetail
import com.ssafy.smartstore.response.MenuDetailWithCommentResponse
import com.ssafy.smartstore.util.CommonUtils
import com.ssafy.smartstore.util.showToastMessage
import com.ssafy.smartstore.viewModels.CommentViewModel
import com.ssafy.smartstore.viewModels.ShoppingListViewModel
import kotlin.math.round

//메뉴 상세 화면 . Order탭 - 특정 메뉴 선택시 열림
private const val TAG = "MenuDetailFragment_싸피"
class MenuDetailFragment : Fragment(){
    private val commentViewModel by lazy { ViewModelProvider(this, CommentViewModel.Factory(mainActivity.application, productId))[CommentViewModel::class.java]}
    private val shoppingListViewModel by lazy { ViewModelProvider(mainActivity, ShoppingListViewModel.Factory(mainActivity.application))[ShoppingListViewModel::class.java]}
    private lateinit var mainActivity: MainActivity
    private var productId = -1
    lateinit var commentAdapter : CommentAdapter

    private lateinit var binding:FragmentMenuDetailBinding
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity.hideBottomNav(true)

        arguments?.let {
            productId = it.getInt("productId", -1)
            Log.d(TAG, "onCreate: $productId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMenuDetailBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = commentViewModel

        binding.lifecycleOwner = this

        initAdapter()
        initListener()

    }

    // comment adapter 초기화
    private fun initAdapter() {
        commentAdapter = CommentAdapter(productId, this)
        binding.recyclerViewMenuDetail.apply {
            adapter = commentAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
        // viewModel 관찰
        commentViewModel.commentList.observe(mainActivity) {
            commentViewModel.commentList.value?.let {
                commentAdapter.setData(it)
                commentAdapter.notifyDataSetChanged()
                Log.d(TAG, "initAdapter: 리스트: ${it}")

                setScreen(it[0])
            }
        }
    }

    // 초기 화면 설정
    @SuppressLint("NotifyDataSetChanged")
    private fun setScreen(menu: MenuDetailWithCommentResponse){
        Log.d(TAG, "setScreen: menu: ${menu}")
        Glide.with(this)
            .load("${ApplicationClass.MENU_IMGS_URL}${menu.productImg}")
            .into(binding.menuImage)

        binding.txtMenuName.text = menu.productName
        binding.txtMenuPrice.text = "${CommonUtils.makeComma(menu.productPrice)}"
        binding.txtRating.text = "${(round(menu.productRatingAvg*10) /10)}점"
        binding.ratingBar.rating = menu.productRatingAvg.toFloat()/2
    }

    private fun initListener(){
        // 개수 증가
        binding.btnAddCount.setOnClickListener {
            binding.textMenuCount.text = (binding.textMenuCount.text.toString().toInt() + 1).toString()
        }

        // 개수 감소
        binding.btnMinusCount.setOnClickListener {
            if(binding.textMenuCount.text == "1") {
                // 표시된 개수가 1이므로 더 이상 뺄 수 없음
            } else {
                binding.textMenuCount.text = (binding.textMenuCount.text.toString().toInt() - 1).toString()
            }
        }

        // 장바구니 담기 -> 장바구니 화면으로 전환
        binding.btnAddList.setOnClickListener {
            requireContext().showToastMessage("상품이 장바구니에 담겼습니다.")
            val orderDetail = OrderDetail(productId, binding.textMenuCount.text.toString().toInt())
            orderDetail.apply {
                productName = commentViewModel.commentList.value?.get(0)!!.productName
                unitPrice = commentViewModel.commentList.value?.get(0)!!.productPrice
                img = commentViewModel.commentList.value?.get(0)!!.productImg
                productType = commentViewModel.commentList.value?.get(0)!!.type
            }

            shoppingListViewModel.shoppingListInsert(orderDetail)
            mainActivity.supportFragmentManager.beginTransaction()
                .replace(R.id.frame_layout_main, OrderFragment())
                .commit()
        }

        // comment 등록
        binding.btnCreateComment.setOnClickListener {
            showDialogRatingStar()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainActivity.hideBottomNav(false)
    }

    // 별점 등록 시 댓글 등록
    private fun showDialogRatingStar() {
        val builder = AlertDialog.Builder(mainActivity)
        val v = layoutInflater.inflate(R.layout.dialog_ratingbar,null)
        builder.setView(v)

        val listener = DialogInterface.OnClickListener { p0, _ ->
            val alert = p0 as AlertDialog
            val dialogRatingBar : RatingBar = alert.findViewById(R.id.ratingBar)

            binding.ratingBar.rating = dialogRatingBar.rating

            val comment = Comment(comment = binding.commentEdt.text.toString(), productId = productId,
                userId = ApplicationClass.sharedPreferencesUtil.getUser().id, rating = binding.ratingBar.rating)
            insert(comment)

            // 댓글 창 초기화
            binding.commentEdt.setText("")

        }
        builder.setPositiveButton("확인", listener)
        builder.show()
    }

    companion object {
        @JvmStatic
        fun newInstance(key:String, value:Int) =
            MenuDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt(key, value)
                }
            }
    }

    // 댓글 등록 서비스 호출
    private fun insert(comment: Comment) {
        commentViewModel.insertComment(comment)
    }
    // 댓글 수정 서비스 호출
    fun update(comment: Comment) {
        commentViewModel.updateComment(comment)
    }
    // 댓글 삭제 서비스 호출
    fun delete(comment: Comment) {
        commentViewModel.deleteComment(comment)
    }
}