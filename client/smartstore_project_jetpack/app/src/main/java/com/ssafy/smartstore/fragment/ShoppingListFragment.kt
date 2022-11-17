package com.ssafy.smartstore.fragment

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.smartstore.R
import com.ssafy.smartstore.activity.MainActivity
import com.ssafy.smartstore.adapter.OrderDetailListAdapter
import com.ssafy.smartstore.adapter.ShoppingListAdapter
import com.ssafy.smartstore.config.ApplicationClass
import com.ssafy.smartstore.dto.Order
import com.ssafy.smartstore.dto.OrderDetail
import com.ssafy.smartstore.response.OrderDetailResponse
import com.ssafy.smartstore.service.OrderService
import com.ssafy.smartstore.util.RetrofitCallback
import com.ssafy.smartstore.util.showToastMessage
import com.ssafy.smartstore.viewModels.MainViewModel

private const val TAG = "ShoppingListFragment_싸피"

//장바구니 Fragment
class ShoppingListFragment(val orderId : Int) : Fragment() {
    private lateinit var shoppingListRecyclerView: RecyclerView
    private lateinit var shoppingListAdapter: ShoppingListAdapter
    private lateinit var mainActivity: MainActivity
    private lateinit var btnShop: Button
    private lateinit var btnTakeout: Button
    private lateinit var btnOrder: Button
    private lateinit var txtShoppingCount: TextView
    private lateinit var txtShoppingMoney: TextView
    var tableNumber = 0
    var list = mutableListOf<OrderDetail>()

    private val activityViewModel by activityViewModels<MainViewModel>()
    private var isShop: Boolean = true

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity.hideBottomNav(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_shopping_list, null)
        shoppingListRecyclerView = view.findViewById(R.id.recyclerViewShoppingList)
        btnShop = view.findViewById(R.id.btnShop)
        btnTakeout = view.findViewById(R.id.btnTakeout)
        btnOrder = view.findViewById(R.id.btnOrder)
        txtShoppingCount = view.findViewById(R.id.textShoppingCount)
        txtShoppingMoney = view.findViewById(R.id.textShoppingMoney)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 일반 주문
        if(orderId == 0) {
            list = mainActivity.shoppingList
            shoppingListAdapter = ShoppingListAdapter(requireContext(), list, this)
            shoppingListRecyclerView.apply {
                val linearLayoutManager = LinearLayoutManager(context)
                linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
                layoutManager = linearLayoutManager
                adapter = shoppingListAdapter
                //원래의 목록위치로 돌아오게함
                adapter!!.stateRestorationPolicy =
                    RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            }
        }
        // 최근 주문에서의 주문
        else {
            val orderDetails = OrderService().getOrderDetails(orderId)
            orderDetails.observe(
                viewLifecycleOwner,
                { orderDetails ->
                    orderDetails.let {
                        for(o in orderDetails) {
                            var detail = OrderDetail(productId = o.productId, quantity = o.quantity)
                            detail.img = o.img
                            detail.productName = o.productName
                            detail.unitPrice = o.unitPrice
                            list.add(detail)
                        }
                        shoppingListAdapter = ShoppingListAdapter(mainActivity, list, this@ShoppingListFragment)
                    }

                    shoppingListRecyclerView.apply {
                        val linearLayoutManager = LinearLayoutManager(context)
                        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
                        layoutManager = linearLayoutManager
                        adapter = shoppingListAdapter
                        //원래의 목록위치로 돌아오게함
                        adapter!!.stateRestorationPolicy =
                            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
                    }

                    moneyAndCountRefresh(list)
                }
            )
        }

        moneyAndCountRefresh(list)


        btnShop.setOnClickListener {
            btnShop.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.button_color)
            btnTakeout.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.button_non_color)
            isShop = true
        }
        btnTakeout.setOnClickListener {
            btnTakeout.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.button_color)
            btnShop.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.button_non_color)
            isShop = false
        }
        btnOrder.setOnClickListener {
            if (isShop) {
                showDialogForOrderInShop()


            } else {
                //거리가 200이상이라면
                if (true) showDialogForOrderTakeoutOver200m()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainActivity.hideBottomNav(false)
    }

    fun moneyAndCountRefresh(list: MutableList<OrderDetail>) {
        var count = 0
        var money = 0
        for (shopping in list) {
            count += shopping.quantity
            money += shopping.unitPrice * shopping.quantity
        }
        txtShoppingCount.text = "총 ${count}개"
        txtShoppingMoney.text = "${money}원"
    }

    private fun showDialogForOrderInShop() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("알림")
        builder.setMessage(
            "Table NFC를 찍어주세요.\n"
        )

        completedOrder()

        requireContext().showToastMessage(activityViewModel.tableId)

        if (activityViewModel.tableId != "") {
            completedOrder()
        }

        builder.setCancelable(true)
        builder.setNegativeButton(
            "취소"
        ) { dialog, _ ->
            dialog.cancel()

        }
        dialog = builder.create()

        dialog.show()
//        builder.create().show()
    }

    lateinit var dialog: AlertDialog
    private fun showDialogForOrderTakeoutOver200m() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("알림")
        builder.setMessage(
            "현재 고객님의 위치가 매장과 200m 이상 떨어져 있습니다.\n정말 주문하시겠습니까?"
        )
        builder.setCancelable(true)
        builder.setPositiveButton("확인") { _, _ ->
            completedOrder()
        }
        builder.setNegativeButton(
            "취소"
        ) { dialog, _ -> dialog.cancel() }
        dialog = builder.create()
//        builder.create().show()
        dialog.show()
    }

    private fun completedOrder() {
        val order = Order()
        order.apply {
            userId = ApplicationClass.sharedPreferencesUtil.getUser().id
            orderTable = "${activityViewModel.tableId}번 테이블"
            for (detail in list) {
                details.add(OrderDetail(productId = detail.productId, quantity = detail.quantity))
            }
        }

        Log.d(TAG, "completedOrder: order: ${order}")
        makeOrder(order)

        Toast.makeText(context, "주문이 완료되었습니다.", Toast.LENGTH_SHORT).show()
        mainActivity.shoppingList = mutableListOf()

        mainActivity.supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout_main, OrderFragment())
            .commit()
    }

    private fun makeOrder(order: Order) {
        OrderService().makeOrder(order, OrderCallback())
    }

    inner class OrderCallback : RetrofitCallback<Int> {
        override fun onSuccess(code: Int, num: Int) {
            if (num != null) {
                Log.d(TAG, "onSuccess: insert 성공, num : ${num}")
            } else {
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