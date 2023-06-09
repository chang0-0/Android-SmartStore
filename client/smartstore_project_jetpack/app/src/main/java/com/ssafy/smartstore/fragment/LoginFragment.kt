package com.ssafy.smartstore.fragment


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.ssafy.smartstore.R
import com.ssafy.smartstore.activity.LoginActivity
import com.ssafy.smartstore.config.ApplicationClass
import com.ssafy.smartstore.databinding.FragmentLoginBinding
import com.ssafy.smartstore.dto.User
import com.ssafy.smartstore.util.showSnackBarMessage
import com.ssafy.smartstore.util.showToastMessage
import com.ssafy.smartstore.viewModels.LoginViewModel
import kotlinx.coroutines.*


// 로그인 화면
private const val TAG = "LoginFragment_싸피"

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var loginActivity: LoginActivity
    private val loginViewModel by activityViewModels<LoginViewModel>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        loginActivity = context as LoginActivity
    } // End of onAttach

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    } // End of onCreateView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 로그인 버튼 클릭
        binding.btnLogin.setOnClickListener {
            if (binding.editTextLoginID.text!!.isNotEmpty() && binding.editTextLoginPW.text!!.isNotEmpty()) {
                login(
                    binding.editTextLoginID.text.toString(),
                    binding.editTextLoginPW.text.toString()
                )
            } else {
                view.showSnackBarMessage("ID 또는 패스워드를 확인해 주세요.")
            }
        }

        observeLogin()

        // 회원가입 버튼
        binding.btnJoin.setOnClickListener {
            loginActivity.openFragment(2)
        }
    } // End of onViewCreated

    // Login API Call
    private fun login(loginId: String, loginPass: String) {
        val user = User(loginId, loginPass)

        val job2 = CoroutineScope(Dispatchers.IO).async {
            loginViewModel.login(user)
        }

        CoroutineScope(Dispatchers.Main).launch {
            job2.await()
        }
    } // End of login

    private fun observeLogin() {
        loginViewModel.loginCheckUser.observe(viewLifecycleOwner) {
            Log.d(TAG, "observeLogin 여기가 몇 번 동작할까요? ")

            if (loginViewModel.loginCheckUser.value!!.id != binding.editTextLoginID.text.toString() || loginViewModel.loginCheckUser.value!!.pass != binding.editTextLoginPW.text.toString()) {
                view!!.showSnackBarMessage("ID 또는 패스워드를 확인해 주세요.")
            } else {
                requireContext().showToastMessage("로그인 되었습니다.")
                ApplicationClass.sharedPreferencesUtil.addUser(loginViewModel.loginCheckUser.value!!)
                loginActivity.openFragment(1)
            }
        }
    } // End of observeLogin
} // End of LoginFragment class
