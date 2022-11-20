package com.ssafy.smartstore.fragment


import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    } // End of onCreateView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 로그인 버튼 클릭
        binding.btnLogin.setOnClickListener {
            if (binding.editTextLoginID.text.isNotEmpty() && binding.editTextLoginPW.text.isNotEmpty()) {

                CoroutineScope(Dispatchers.IO).launch {
                    login(
                        binding.editTextLoginID.text.toString(),
                        binding.editTextLoginPW.text.toString()
                    )
                }

            } else {
                requireContext().showToastMessage("ID 또는 패스워드를 확인해 주세요.")
            }
        }

        // 회원가입 버튼
        binding.btnJoin.setOnClickListener {
            loginActivity.openFragment(2)
        }
    } // End of onViewCreated

    // Login API Call
    private suspend fun login(loginId: String, loginPass: String) {
        val user = User(loginId, loginPass)

        val job = CoroutineScope(Dispatchers.IO).launch {
            loginViewModel.login(user)
            delay(100L)
        }

        job.join()
        // job.join()을 통해서 작업이 끝날 때까지 대기한 후 작업이 모두 끝나고 나면, 다음 작업을 실행한다.

        withContext(Dispatchers.Main) {
            if (loginViewModel.loginCheckUser.value == null) {
                requireContext().showToastMessage("ID 또는 패스워드를 확인해 주세요.")
            } else {
                requireContext().showToastMessage("로그인 되었습니다.")
                ApplicationClass.sharedPreferencesUtil.addUser(loginViewModel.loginCheckUser.value!!)
                loginActivity.openFragment(1)
            }
        }

    } // End of login
} // End of LoginFragment class

