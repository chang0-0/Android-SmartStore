package com.ssafy.smartstore.activity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.NfcManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.gun0912.tedpermission.provider.TedPermissionProvider.context
import com.ssafy.smartstore.R
import com.ssafy.smartstore.api.UserApi
import com.ssafy.smartstore.config.ApplicationClass
import com.ssafy.smartstore.databinding.ActivityMainBinding
import com.ssafy.smartstore.dto.Order
import com.ssafy.smartstore.dto.OrderDetail
import com.ssafy.smartstore.fragment.*
import com.ssafy.smartstore.repository.UserRepository
import com.ssafy.smartstore.util.SharedPreferencesUtil
import com.ssafy.smartstore.util.showToastMessage
import com.ssafy.smartstore.viewModels.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "MainActivity_싸피"

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var nAdapter: NfcAdapter
    private lateinit var binding: ActivityMainBinding
    private lateinit var filters: Array<IntentFilter>
    private lateinit var pIntent: PendingIntent
    private val newActivityViewModel: MainViewModel by viewModels()
    private val userViewModel by lazy { ViewModelProvider(this, UserViewModel.Factory(this.application, ApplicationClass.sharedPreferencesUtil.getUser().id))[UserViewModel::class.java] }
    private val shoppingListViewModel by lazy {
        ViewModelProvider(
            this,
            ShoppingListViewModel.Factory(this.application)
        )[ShoppingListViewModel::class.java]
    }
    private val orderViewModel by lazy {
        ViewModelProvider(
            this,
            OrderViewModel.Factory(
                this.application,
                ApplicationClass.sharedPreferencesUtil.getUser().id
            )
        )[OrderViewModel::class.java]
    }
    private val noticeViewModel by lazy {
        ViewModelProvider(
            this,
            NoticeViewModel.Factory(this.application)
        )[NoticeViewModel::class.java]
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 가장 첫 화면은 홈 화면의 Fragment로 지정

        setNdef()

        setBeacon()

        createNotificationChannel("ssafy_channel", "ssafy")

        checkPermissions()

        CoroutineScope(Dispatchers.IO).launch {
            setUserData()
        }


        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout_main, HomeFragment())
            .commit()

        bottomNavigation = binding.tabLayoutBottomNavigation
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_page_1 -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout_main, HomeFragment())
                        .commit()
                    true
                }
                R.id.navigation_page_2 -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout_main, OrderFragment())
                        .commit()
                    true
                }
                R.id.navigation_page_3 -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout_main, MypageFragment())
                        .commit()
                    true
                }
                else -> false
            }
        }

        bottomNavigation.setOnNavigationItemReselectedListener { item ->
            // 재선택시 다시 랜더링 하지 않기 위해 수정
            if (bottomNavigation.selectedItemId != item.itemId) {
                bottomNavigation.selectedItemId = item.itemId
            }
        }
    }

    private suspend fun setUserData() { // 사용자 데이터 viewModel에 저장. 반영
        val user = SharedPreferencesUtil(this).getUser()

        //userViewModel = ViewModelProvider(this, UserViewModelFactory(userApi = userApi, userRepository)).get(UserViewModel::class.java)
        // user 정보 viewModel에 저장해서 LiveData로 관리
        userViewModel.getUserData(user.id)

        //val userViewModel = UserViewModel(this.UserViewModelFactory).get(main)
    } // End of setUserData 

    fun openFragment(index: Int, key: String, value: Int) {
        moveFragment(index, key, value)
    }

    fun openFragment(index: Int) {
        moveFragment(index, "", 0)
    }

    private fun moveFragment(index: Int, key: String, value: Int) {
        val transaction = supportFragmentManager.beginTransaction()
        when (index) {
            //장바구니
            1 -> {
                transaction.replace(R.id.frame_layout_main, ShoppingListFragment(value))
                    .addToBackStack(null)
            }
            //주문 상세 보기
            2 -> transaction.replace(
                R.id.frame_layout_main,
                OrderDetailFragment.newInstance(key, value)
            )
                .addToBackStack(null)
            //메뉴 상세 보기
            3 -> transaction.replace(
                R.id.frame_layout_main,
                MenuDetailFragment.newInstance(key, value)
            )
                .addToBackStack(null)
            //map으로 가기
            4 -> transaction.replace(R.id.frame_layout_main, MapFragment())
                .addToBackStack(null)
            //logout
            5 -> {
                logout()
            }
        }
        transaction.commit()
    }

    fun logout() {
        //preference 지우기
        ApplicationClass.sharedPreferencesUtil.deleteUser()

        //화면이동
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent)
    }

    fun hideBottomNav(state: Boolean) {
        if (state) bottomNavigation.visibility = View.GONE
        else bottomNavigation.visibility = View.VISIBLE
    }

    private fun setNdef() {
        var manager: NfcManager =
            this.getSystemService(Context.NFC_SERVICE) as NfcManager
        nAdapter = manager.defaultAdapter

        val i = Intent(this, MainActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        pIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_MUTABLE)

        val filter = IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED) // 제일 마지막 호출(태그면 다 불림)
        filters = arrayOf(filter)
    }

    private fun setBeacon() {

    }

    // NotificationChannel 설정
    private fun createNotificationChannel(id: String, name: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(id, name, importance)

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        getNFCData(getIntent())

        if(newActivityViewModel.flag)
            orderCompletedThenGoToMain()
    }

    private fun orderCompletedThenGoToMain() {
        parseData(intent)
        completedOrder()

        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout_main, HomeFragment())
            .commit()

        showToastMessage("주문 완료되었습니다.")
        newActivityViewModel.tableIdSet("")
    }

    private fun completedOrder() {
        var totalQuantity = 0
        val order = Order().apply {
            userId = ApplicationClass.sharedPreferencesUtil.getUser().id
            orderTable = "${newActivityViewModel.tableId}번 테이블"
            for (detail in shoppingListViewModel.shoppingList.value!!) {
                details.add(OrderDetail(productId = detail.productId, quantity = detail.quantity))
                totalQuantity += detail.quantity
            }
        }

        makeOrder(order)

        // 알림판에 정보를 집어넣기 위해 view model 이용
        order.totalQuantity = totalQuantity
        // 여러 개 주문의 경우 마지막 상품을 대표 상품으로 텍스트 표시 => 최근 주문내역에 표시되는 것과 동일
        order.topProductName =
            shoppingListViewModel.shoppingList.value!![shoppingListViewModel.shoppingList.value!!.size - 1].productName
        noticeViewModel.noticeInsert(order)

        // 장바구니 초기화 -> 추후 livedata로 바뀔 시 수정 필요
        shoppingListViewModel.shoppingListClear()
    }

    private fun makeOrder(order: Order) {
        orderViewModel.makeOrder(order)
    }

    private fun getNFCData(intent: Intent) {
        val action = intent.action
        if (action == NfcAdapter.ACTION_NDEF_DISCOVERED || action == NfcAdapter.ACTION_TECH_DISCOVERED || action == NfcAdapter.ACTION_TAG_DISCOVERED) {
            intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)!!.takeWhile {
                val msg = it as NdefMessage

                false
            }
        }
    }


    private fun parseData(intent: Intent) {
        val data = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)!!
        data.takeWhile {
            val message = it as NdefMessage
            val record = message.records
            val payload = String(record[0].payload, 3, record[0].payload.size - 3)

            newActivityViewModel.tableIdSet(payload)
            false
        }
    }

    private fun checkPermissions() {
    }

    override fun onResume() {
        super.onResume()
        nAdapter.enableForegroundDispatch(this, pIntent, filters, null)
    }

    override fun onPause() {
        super.onPause()
        nAdapter.disableForegroundDispatch(this)
    }

}