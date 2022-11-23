package com.ssafy.smartstore.fragment

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.smartstore.R
import com.ssafy.smartstore.activity.MainActivity
import com.ssafy.smartstore.adapter.ShoppingListAdapter
import com.ssafy.smartstore.databinding.FragmentShoppingListBinding
import com.ssafy.smartstore.dto.OrderDetail
import com.ssafy.smartstore.util.showSnackBarMessage
import com.ssafy.smartstore.viewModels.MainViewModel
import com.ssafy.smartstore.viewModels.ShoppingListViewModel

private const val TAG = "ShoppingListFragment_싸피"

//장바구니 Fragment
class ShoppingListFragment(val orderId: Int) : Fragment() {
    private lateinit var binding: FragmentShoppingListBinding
    private lateinit var shoppingListRecyclerView: RecyclerView
    private lateinit var shoppingListAdapter: ShoppingListAdapter
    private lateinit var mainActivity: MainActivity
    lateinit var dialog: AlertDialog


    private val shoppingListViewModel by lazy {
        ViewModelProvider(
            mainActivity,
            ShoppingListViewModel.Factory(mainActivity.application)
        )[ShoppingListViewModel::class.java]
    }
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
        binding = FragmentShoppingListBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun initDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("알림")
        builder.setMessage(
            "Table NFC를 찍어주세요.\n"
        )

        builder.setCancelable(true)
        builder.setNegativeButton(
            "취소"
        ) { dialog, _ ->
            dialog.cancel()
        }

        dialog = builder.create()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initDialog()

        binding.backButton.setOnClickListener {
            mainActivity.supportFragmentManager.beginTransaction()
                .replace(R.id.frame_layout_main, OrderFragment())
                .commit()
        }

        shoppingListRecyclerView = binding.recyclerViewShoppingList
        shoppingListAdapter = ShoppingListAdapter(requireContext(), this)

        // 일반 주문
        shoppingListRecyclerView.apply {
            adapter = shoppingListAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }

        // viewModel 관찰
        shoppingListViewModel.shoppingList.observe(mainActivity) {
            moneyAndCountRefresh(shoppingListViewModel.shoppingList.value!!)
            shoppingListViewModel.shoppingList.value?.let {
                shoppingListAdapter.setData(it)
                shoppingListAdapter.notifyDataSetChanged()
                shoppingListRecyclerView.adapter = shoppingListAdapter
            }
        }

        binding.changeSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            isShop = isChecked
        }

        binding.btnOrder.setOnClickListener {
            if(shoppingListViewModel.shoppingList.value!!.size == 0) {
                view.showSnackBarMessage("장바구니가 비어있습니다")
            } else {
                if (isShop) {   // 거리가 200 이하라면
                    activityViewModel.flag = true
                    showDialogForOrderInShop()


                } else {
                    // 거리가 200 이상이라면
                    showDialogForOrderTakeoutOver200m()
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        dialog.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainActivity.hideBottomNav(false)

        // 최근 주문에서 클릭해서 장바구니로 넘어온 경우 뒤로가기 시 장바구니 초기화
        if (orderId != 0) shoppingListViewModel.shoppingListClear()
    }

    fun shoppingListDelete(orderDetail: OrderDetail) {
        shoppingListViewModel.shoppingListDelete(orderDetail)
    }


    // 화면 하단에 장바구니에 담긴 상품의 총액, 개수 표시
    fun moneyAndCountRefresh(list: MutableList<OrderDetail>) {
        var count = 0
        var money = 0
        for (shopping in list) {
            count += shopping.quantity
            money += shopping.unitPrice * shopping.quantity
        }
        binding.textShoppingCount.text = "총 ${count}개"
        binding.textShoppingMoney.text = "${money}원"
    }   // End of moneyAndCountRefresh


    private fun showDialogForOrderInShop() {
        dialog.show()

        dialog.setOnDismissListener {
            activityViewModel.flag = false
        }
    }


    private fun showDialogForOrderTakeoutOver200m() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("알림")
        builder.setMessage(
            "현재 고객님의 위치가 매장과 200m 이상 떨어져 있습니다.\n정말 주문하시겠습니까?"
        )
        builder.setCancelable(true)
        builder.setNegativeButton(
            "취소"
        ) { dialog, _ -> dialog.cancel() }
        dialog = builder.create()
        dialog.show()
    }
}