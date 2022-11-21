package com.ssafy.smartstore.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.smartstore.activity.MainActivity
import com.ssafy.smartstore.adapter.AdapterItemClickListener
import com.ssafy.smartstore.adapter.MenuAdapter
import com.ssafy.smartstore.databinding.FragmentOrderBinding
import com.ssafy.smartstore.dto.Product
import com.ssafy.smartstore.service.ProductService
import com.ssafy.smartstore.util.RetrofitCallback
import com.ssafy.smartstore.viewModels.MapViewModel

// 하단 주문 탭
private const val TAG = "OrderFragment_싸피"

class OrderFragment : Fragment() {
    private lateinit var menuAdapter: MenuAdapter
    private lateinit var mainActivity: MainActivity
    private lateinit var binding: FragmentOrderBinding
    private val viewModel: MapViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onResume() {
        super.onResume()
        // MapFragment 에 들어갔다 나와야 표시됨, 추후 처리 필요
//        binding.dist.text = "매장과의 거리가 ${viewModel.distance}m 입니다."
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()

        binding.floatingBtn.setOnClickListener {
            //장바구니 이동
            mainActivity.openFragment(1)
        }

        binding.btnMap.setOnClickListener {
            mainActivity.openFragment(4)
        }
    }

    private fun initData() {
        ProductService().getProductList(ProductCallback())
    }

    inner class ProductCallback : RetrofitCallback<List<Product>> {
        override fun onSuccess(code: Int, productList: List<Product>) {
            productList.let {
                Log.d(TAG, "onSuccess: ${productList}")
                menuAdapter = MenuAdapter(productList)
                menuAdapter.setItemClickListener(object : AdapterItemClickListener {
                    override fun onClick(view: View, position: Int, productId: Any?) {
                        mainActivity.openFragment(3, "productId", productId as Int)
                    }
                })
            }

            binding.recyclerViewMenu.apply {
                layoutManager = GridLayoutManager(context, 3)
                adapter = menuAdapter
                //원래의 목록위치로 돌아오게함
                adapter!!.stateRestorationPolicy =
                    RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            }

            Log.d(TAG, "ProductCallback: $productList")
        }

        override fun onError(t: Throwable) {
            Log.d(TAG, t.message ?: "유저 정보 불러오는 중 통신오류")
        }

        override fun onFailure(code: Int) {
            Log.d(TAG, "onResponse: Error Code $code")
        }
    }


}