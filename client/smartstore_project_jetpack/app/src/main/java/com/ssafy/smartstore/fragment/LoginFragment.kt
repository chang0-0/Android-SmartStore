package com.ssafy.smartstore.fragment


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.ssafy.smartstore.activity.LoginActivity
import com.ssafy.smartstore.config.ApplicationClass
import com.ssafy.smartstore.databinding.FragmentLoginBinding
import com.ssafy.smartstore.dto.User
import com.ssafy.smartstore.util.showToastMessage
import com.ssafy.smartstore.viewModels.LoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


// 로그인 화면
private const val TAG = "LoginFragment_싸피"

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var loginActivity: LoginActivity
    private val loginViewModel by activityViewModels<LoginViewModel>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        loginActivity = context as LoginActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 로그인 버튼 클릭
        binding.btnLogin.setOnClickListener {
            if (binding.editTextLoginID.text.isNotEmpty() && binding.editTextLoginPW.text.isNotEmpty()) {

                login(
                    binding.editTextLoginID.text.toString(),
                    binding.editTextLoginPW.text.toString()
                )


            } else {
                requireContext().showToastMessage("ID 또는 패스워드를 확인해 주세요.")
            }
        }

        // 회원가입 버튼
        binding.btnJoin.setOnClickListener {
            loginActivity.openFragment(2)
        }
    }

    // Login API Call
    private fun login(loginId: String, loginPass: String) {
        val user = User(loginId, loginPass)

        val scope = CoroutineScope(Dispatchers.IO)
        
        scope.launch {
            loginViewModel.login(user)

        }

        scope.launch(Dispatchers.Main) {
            if (loginViewModel.loginCheckUser.value == null) {
                requireContext().showToastMessage("ID 또는 패스워드를 확인해 주세요.")
            } else {
                requireContext().showToastMessage("로그인 되었습니다.")
                ApplicationClass.sharedPreferencesUtil.addUser(loginViewModel.loginCheckUser.value!!)
                loginActivity.openFragment(1)
            }
        }



    }

    private fun obserLoginUser(loginId: String, loginPass: String) {
        Log.d(TAG, "obserLoginUser: ${loginViewModel.loginCheckUser.value}")
        Log.d(TAG, "obserLoginUser: ${loginViewModel.loginCheckUser.value == null}")

        loginViewModel.loginCheckUser.observe(viewLifecycleOwner) {
            Log.d(TAG, "옵저버 안임: ${loginViewModel.loginCheckUser.value}")
            if (it.id == loginId && it.pass == loginPass) {
                requireContext().showToastMessage("로그인 되었습니다.")
                ApplicationClass.sharedPreferencesUtil.addUser(loginViewModel.loginCheckUser.value!!)
                loginActivity.openFragment(1)
            }
        }
    }

}


