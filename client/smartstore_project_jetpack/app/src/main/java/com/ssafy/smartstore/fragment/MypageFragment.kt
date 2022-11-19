package com.ssafy.smartstore.fragment

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.smartstore.activity.MainActivity
import com.ssafy.smartstore.adapter.AdapterItemClickListener
import com.ssafy.smartstore.adapter.OrderAdapter
import com.ssafy.smartstore.api.UserApi
import com.ssafy.smartstore.config.ApplicationClass
import com.ssafy.smartstore.databinding.FragmentMypageBinding
import com.ssafy.smartstore.dto.User
import com.ssafy.smartstore.response.LatestOrderResponse
import com.ssafy.smartstore.service.OrderService
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
    private lateinit var userViewModel: UserViewModel

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
        val userLastOrderLiveData = OrderService().getLastMonthOrder(id)
        userLastOrderLiveData.observe(viewLifecycleOwner) {
            list = it
            orderAdapter = OrderAdapter(mainActivity, list)
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
            binding.logout.setOnClickListener {
                mainActivity.openFragment(5)
            }

            Log.d(TAG, "onViewCreated: $it")
        }
    }

    // user id get & stamp 정보 화면에 표시
    private suspend fun getUserData(): String {
        user = ApplicationClass.sharedPreferencesUtil.getUser()
        //binding.textUserName.text = user.name

        val userApi : UserApi

        Log.d(TAG, "getUserData 테스트: ")

        // viewModel에 매개변수가 있을 때는 ViewModelProvider.Factory를 사용해야 한다.
//        val userViewModel: UserViewModel by viewModels {
//            object : ViewModelProvider.Factory {
//                override fun <T : ViewModel> create(modelClass: Class<T>): T {
//                    return UserViewModel(userApi) as T
//                }
//            }
//        }

//        this.userViewModel = userViewModel
//        userViewModel.getUserInfo(user.id)
//        Log.d(TAG, "getUserData@@@@@@@@@@@@@@@@@@@@: ${userViewModel.userInfo.value?.body()}")
//
//
//        userViewModel.userInfo.observe(viewLifecycleOwner, Observer {
//            if (it.isSuccessful) {
//                Log.d(TAG, "getUserData: ${it.body()?.id}")
//            } else {
//                Log.d(TAG, "getUserData: ${it.errorBody().toString()}")
//            }
//        })


        // 데이터가 변하면 화면을 자동으로 갱신
//        userViewModel.userInfo.observe(viewLifecycleOwner) {
//            Log.d(TAG, " @@@@@@@@@@@ userViewModel 옵저버 동작 @@@@@@@@@@@@@@@: ")
//
//            binding.textUserName.text = it.body()?.id.toString()
//            var stampDetail = stampLevelCalc(it.body()?.stamps ?: 0)
//            binding.textUserLevel.text = "${stampDetail.levelName} ${stampDetail.levelDetail}단계"
//            binding.textLevelRest.text = "다음 레벨까지 ${stampDetail.levelRest}잔 남았습니다."
//            binding.proBarUserLevel.setProgress(stampDetail.levelProgressRatio) // 백분율
//            binding.textUserNextLevel.text = stampDetail.nextLevelCountText
//        }

        // viewModel 적용 이전 코드
//        var stampDetail = stampLevelCalc(user.stamps)
//        binding.textUserLevel.text = "${stampDetail.levelName} ${stampDetail.levelDetail}단계"
//        binding.textLevelRest.text = "다음 레벨까지 ${stampDetail.levelRest}잔 남았습니다."
//        binding.proBarUserLevel.setProgress(stampDetail.levelProgressRatio) // 백분율
//        binding.textUserNextLevel.text = stampDetail.nextLevelCountText

        return user.id
    }


    private fun stampLevelCalc(stampCount: Int): StampDetail {
        val stampDetail = StampDetail("", 0, 0, 0, "")

        if (stampCount in 0..50) {
            return stampDetail.apply {
                levelName = "씨앗"
                levelDetail = (stampCount / 10) + 1
                levelRest = 10 - stampCount
                levelProgressRatio = (stampCount % 10) * 10
                nextLevelCountText = "${levelRest % 10}/${10}"
            }
        } else if (stampCount in 51..125) {
            return stampDetail.apply {
                levelName = "꽃"
                levelDetail = ((stampCount - 50) / 15) + 1
                levelRest = 15 - (stampCount - 50)
                levelProgressRatio = ((stampCount - 50) % 15) * 10
                nextLevelCountText = "${levelRest % 15}/${15}"
            }
        } else if (stampCount in 126..225) {
            return stampDetail.apply {
                levelName = "열매"
                levelDetail = ((stampCount - 125) / 20) + 1
                levelRest = 20 - (stampCount - 125)
                levelProgressRatio = ((stampCount - 125) % 20) * 10
                nextLevelCountText = "${levelRest % 20}/${20}"

            }
        } else if (stampCount in 226..350) {
            return stampDetail.apply {
                levelName = "커피콩"
                levelDetail = ((stampCount - 225) / 25) + 1
                levelRest = 25 - (stampCount - 225)
                levelProgressRatio = ((stampCount - 225) % 25) * 10
                nextLevelCountText = "${levelRest % 25}/${25}"
            }
        } else {
            return stampDetail.apply {
                levelName = "나무"
                levelDetail = 0
                levelRest = 0
                levelProgressRatio = 100
                nextLevelCountText = "∞/∞"
            }
        }
    }

    data class StampDetail(
        var levelName: String, // 등급 이름
        var levelDetail: Int, // 등급 단계
        var levelRest: Int, // 다음 등급 까지 남은 갯수
        var levelProgressRatio: Int, // progressbar에서 표시할 백분율 값
        var nextLevelCountText: String // 다음 단계로 가기위해서 몇잔을 더 필요로 하는지를 나타내는 텍스트
    )
}