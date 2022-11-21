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
import com.ssafy.smartstore.activity.MainActivity
import com.ssafy.smartstore.adapter.AdapterItemClickListener
import com.ssafy.smartstore.adapter.LatestOrderAdapter
import com.ssafy.smartstore.adapter.NoticeAdapter
import com.ssafy.smartstore.config.ApplicationClass
import com.ssafy.smartstore.databinding.FragmentHomeBinding
import com.ssafy.smartstore.response.LatestOrderResponse
import com.ssafy.smartstore.viewModels.OrderViewModel
import com.ssafy.smartstore.viewModels.ShoppingListViewModel

// Home 탭
private const val TAG = "HomeFragment_싸피"

class HomeFragment : Fragment() {
    private val shoppingListViewModel by lazy { ViewModelProvider(mainActivity, ShoppingListViewModel.Factory(mainActivity.application))[ShoppingListViewModel::class.java]}
    private val orderViewModel by lazy { ViewModelProvider(this, OrderViewModel.Factory(mainActivity.application, userId))[OrderViewModel::class.java]}
    private lateinit var latestOrderAdapter: LatestOrderAdapter
    private var noticeAdapter: NoticeAdapter = NoticeAdapter()
    private lateinit var mainActivity: MainActivity
    private lateinit var list: List<LatestOrderResponse>
    private lateinit var binding: FragmentHomeBinding
    private var userId = ""
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
        userId = ApplicationClass.sharedPreferencesUtil.getUser().id
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ")
        orderViewModel.getLatesetOrderList(userId)
        initData(ApplicationClass.sharedPreferencesUtil.getUser().id)
        initUserName()
        initAdapter()
    }

    fun initData(id: String) {
        val userLastOrderLiveData = orderViewModel.lastMonthOrderList
        userLastOrderLiveData.observe(viewLifecycleOwner) {
            list = it
            latestOrderAdapter = LatestOrderAdapter(requireContext(), list)
            Log.d(TAG, "initData: list: ${list}")
            binding.recyclerViewLatestOrder.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = latestOrderAdapter
                //원래의 목록위치로 돌아오게함
                adapter!!.stateRestorationPolicy =
                    RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            }

            // latestOrderAdapter 기능 구현.
            latestOrderAdapter.setItemClickListener(object : AdapterItemClickListener {
                // interface 메소드 구현
                override fun onClick(view: View, position: Int, orderId: Any?) {
                    mainActivity.openFragment(1, "orderId", orderId as Int)
                    shoppingListViewModel.shoppingListClear()
                    shoppingListViewModel.shoppingListInitWithLatestOrder(orderId)
                }
            })
        }
    }

    fun initAdapter() {
        noticeAdapter = NoticeAdapter()
        binding.recyclerViewNoticeOrder.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = noticeAdapter
            //원래의 목록위치로 돌아오게함
            adapter!!.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }
    }

    private fun initUserName() {
        var user = ApplicationClass.sharedPreferencesUtil.getUser()
        binding.textUserName.text = "${user.name} 님"
    }
}
