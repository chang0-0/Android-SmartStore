package com.ssafy.smartstore.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.smartstore.activity.MainActivity
import com.ssafy.smartstore.adapter.LatestOrderAdapter
import com.ssafy.smartstore.adapter.NoticeAdapter
import com.ssafy.smartstore.adapter.OrderAdapter
import com.ssafy.smartstore.config.ApplicationClass
import com.ssafy.smartstore.databinding.FragmentHomeBinding
import com.ssafy.smartstore.response.LatestOrderResponse
import com.ssafy.smartstore.service.OrderService

// Home 탭
private const val TAG = "HomeFragment_싸피"
class HomeFragment : Fragment(){
    private lateinit var latestOrderAdapter : LatestOrderAdapter
    private var noticeAdapter: NoticeAdapter = NoticeAdapter()
    private lateinit var mainActivity: MainActivity
    private lateinit var list: List<LatestOrderResponse>
    private lateinit var binding:FragmentHomeBinding
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData(ApplicationClass.sharedPreferencesUtil.getUser().id)
        initUserName()
        initAdapter()
    }

    fun initData(id: String) {
        val userLastOrderLiveData = OrderService().getLastMonthOrder(id)
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
            latestOrderAdapter.setItemClickListener(object : LatestOrderAdapter.ItemClickListener{
                override fun onClick(view: View, position: Int, orderId: Int) {
//                    mainActivity!!.openFragment(1)
                    mainActivity.openFragment(1, "orderId", orderId)
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

    private fun initUserName(){
        var user = ApplicationClass.sharedPreferencesUtil.getUser()
        binding.textUserName.text = "${user.name} 님"
    }
}