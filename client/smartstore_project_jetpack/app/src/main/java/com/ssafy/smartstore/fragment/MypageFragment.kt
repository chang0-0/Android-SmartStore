package com.ssafy.smartstore.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.smartstore.activity.MainActivity
import com.ssafy.smartstore.adapter.AdapterItemClickListener
import com.ssafy.smartstore.adapter.OrderAdapter
import com.ssafy.smartstore.config.ApplicationClass
import com.ssafy.smartstore.databinding.FragmentMypageBinding
import com.ssafy.smartstore.dto.User
import com.ssafy.smartstore.response.LatestOrderResponse
import com.ssafy.smartstore.viewModels.OrderViewModel
import com.ssafy.smartstore.viewModels.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// MyPage 탭
private const val TAG = "MypageFragment_싸피"


class MypageFragment : Fragment() {
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var mainActivity: MainActivity
    private lateinit var list: List<LatestOrderResponse>
    private lateinit var user: User
    private val userViewModel by lazy { ViewModelProvider(this, UserViewModel.Factory(mainActivity.application, user.id))[UserViewModel::class.java] }
    private val orderViewModel by lazy { ViewModelProvider(this, OrderViewModel.Factory(mainActivity.application, user.id))[OrderViewModel::class.java]}

    private lateinit var binding: FragmentMypageBinding
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMypageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CoroutineScope(Dispatchers.Main).launch {
            val id = getUserData()
            initData(id)
        }
    }

    private fun initData(id: String) {
        val userLastOrderLiveData = orderViewModel.lastMonthOrderList
        userLastOrderLiveData.observe(viewLifecycleOwner) {
            list = it
            orderAdapter = OrderAdapter(requireContext(), list)
            orderAdapter.setItemClickListener(object : AdapterItemClickListener {
                override fun onClick(view: View, position: Int, orderid: Any?) {
                    mainActivity.openFragment(2, "orderId", orderid as Int)
                }
            })

            binding.recyclerViewOrder.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = orderAdapter
                //원래의 목록위치로 돌아오게함
                adapter!!.stateRestorationPolicy =
                    RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            }
        }

        binding.logout.setOnClickListener {
            mainActivity.openFragment(5)
        }
    }

    // user id get & stamp 정보 화면에 표시
    private fun getUserData(): String {
        user = ApplicationClass.sharedPreferencesUtil.getUser()
        binding.textUserName.text = user.name

        Log.d(TAG, "getUserData 테스트: ")


        val grade = userViewModel.userGrade
        Log.d(TAG, "getUserData: grade:1 ${grade.value}")
        grade.observe(viewLifecycleOwner) {
            Log.d(TAG, "getUserData: grade:2 ${grade.value}")
            Glide.with(this)
                .load("${ApplicationClass.GRADE_IMGS_URL}${grade.value!!.img}")
                .into(binding.imageLevel)
            binding.textUserLevel.text = "${grade.value!!.title} ${grade.value!!.step}단계"
            binding.textLevelRest.text = "다음 레벨까지 ${grade.value!!.to}잔 남았습니다."

            var total = 0
            when(grade.value!!.title) {
                "씨앗" -> {
                    total = 10
                }
                "꽃" -> {
                    total = 15
                }
                "열매" -> {
                    total = 20
                }
                "커피콩" -> {
                    total = 25
                }
            }
            binding.proBarUserLevel.max = total
            binding.proBarUserLevel.progress = total - grade.value!!.to
            binding.textUserNextLevel.text = "${total - grade.value!!.to}/$total"
        }
        return user.id
    }
}