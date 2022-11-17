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
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.smartstore.R
import com.ssafy.smartstore.activity.MainActivity
import com.ssafy.smartstore.adapter.CommentAdapter
import com.ssafy.smartstore.config.ApplicationClass
import com.ssafy.smartstore.databinding.FragmentMenuDetailBinding
import com.ssafy.smartstore.dto.Comment
import com.ssafy.smartstore.dto.OrderDetail
import com.ssafy.smartstore.response.MenuDetailWithCommentResponse
import com.ssafy.smartstore.service.CommentService
import com.ssafy.smartstore.service.ProductService
import com.ssafy.smartstore.util.CommonUtils
import com.ssafy.smartstore.util.RetrofitCallback
import com.ssafy.smartstore.util.showToastMessage
import com.ssafy.smartstore.viewModels.MainViewModel
import kotlin.math.round

//메뉴 상세 화면 . Order탭 - 특정 메뉴 선택시 열림
private const val TAG = "MenuDetailFragment_싸피"
class MenuDetailFragment : Fragment(){
    private val activityWithFragmentViewModel: MainViewModel by activityViewModels()
    private lateinit var mainActivity: MainActivity
    private var productId = -1
    private var commentAdapter = CommentAdapter(emptyList(), productId, this)
    lateinit var menuDetailWithCommentResponse : MenuDetailWithCommentResponse

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
        initData()
        initListener()
    }

    fun initData(){
        ProductService().getProductWithComments(productId, ProductWithCommentInsertCallback())

        binding.recyclerViewMenuDetail.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = commentAdapter
            Log.d(TAG, "initData: adapter 설정")
            //원래의 목록위치로 돌아오게함
            adapter!!.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            adapter!!.notifyDataSetChanged()
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

        commentAdapter.notifyDataSetChanged()
        binding.recyclerViewMenuDetail.adapter = commentAdapter

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

        binding.btnAddList.setOnClickListener {
            requireContext().showToastMessage("상품이 장바구니에 담겼습니다.")
            val orderDetail = OrderDetail(productId, binding.textMenuCount.text.toString().toInt())
            orderDetail.apply {
                productName = menuDetailWithCommentResponse.productName
                unitPrice = menuDetailWithCommentResponse.productPrice
                img = menuDetailWithCommentResponse.productImg
                productType = menuDetailWithCommentResponse.type
            }

            mainActivity.shoppingList.add(orderDetail)  // 장바구니에 상품 추가
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

            initData()
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

    inner class ProductWithCommentInsertCallback: RetrofitCallback<List<MenuDetailWithCommentResponse>> {
        override fun onSuccess(
            code: Int,
            responseData: List<MenuDetailWithCommentResponse>
        ) {
            if(responseData.isNotEmpty()) {
                Log.d(TAG, "initData: ${responseData}")

                // comment 가 없을 경우 -> 들어온 response가 1개이고 해당 userId 가 null일 경우 빈 배열 Adapter 연결
                commentAdapter = if (responseData.size == 1 && responseData[0].userId == null) {
                    CommentAdapter(emptyList(), productId, this@MenuDetailFragment)
                } else {
                    CommentAdapter(responseData, productId, this@MenuDetailFragment)
                }

                // 화면 정보 갱신
                setScreen(responseData[0])

                menuDetailWithCommentResponse = responseData[0]
            }

        }

        override fun onError(t: Throwable) {
            Log.d(TAG, t.message ?: "물품 정보 받아오는 중 통신오류")
        }

        override fun onFailure(code: Int) {
            Log.d(TAG, "onResponse: Error Code $code")
        }
    }

    // 댓글 등록 서비스 호출
    private fun insert(comment: Comment) {
        CommentService().insert(comment, InsertCallback())
    }

    inner class InsertCallback: RetrofitCallback<Boolean> {
        override fun onSuccess( code: Int, bool: Boolean) {
            if (bool) {
                Log.d(TAG, "onSuccess: insert 성공")
            }else{
                Log.d(TAG, "onSuccess: insert 실패")
            }
        }

        override fun onError(t: Throwable) {
            Log.d(TAG, t.message ?: "comment insert 중 통신오류")
        }

        override fun onFailure(code: Int) {
            Log.d(TAG, "onResponse: Error Code $code")
        }
    }

}