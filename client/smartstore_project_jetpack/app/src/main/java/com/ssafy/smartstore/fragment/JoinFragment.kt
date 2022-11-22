package com.ssafy.smartstore.fragment

import android.content.Context
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.ssafy.smartstore.R
import com.ssafy.smartstore.activity.LoginActivity
import com.ssafy.smartstore.databinding.FragmentJoinBinding
import com.ssafy.smartstore.dto.User
import com.ssafy.smartstore.util.RetrofitUtil.Companion.userService
import com.ssafy.smartstore.util.showSnackBarMessage
import com.ssafy.smartstore.util.showToastMessage
import com.ssafy.smartstore.viewModels.LoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.log

// 회원 가입 화면
private const val TAG = "JoinFragment_싸피"

class JoinFragment : Fragment() {
    lateinit var binding: FragmentJoinBinding
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
        binding = FragmentJoinBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //id 중복 확인 버튼을 눌렀을 때, Api를 call해서 중복체크를 함.
        // 중복체크를 한 결과를 Boolean값으로 받아와서 loginViewModel의 _isUsedId.value를 갱신해준다.
        // 이 값이 갱신되면 observer가 value가 변한것을 감지해서 Toast Message를 띄운다.
        binding.checkIdButton.setOnClickListener {
            if (binding.editTextJoinID.text!!.isEmpty()) {
                view.showSnackBarMessage("아이디를 입력해주세요")
            } else {
                loginViewModel.checkUsedId(binding.editTextJoinID.text.toString())
            }
        }

        // observe는 버튼이 클릭될 때 마다 생성되므로, observer는 한번만 생성되도록 하는것이 맞다.
        // setOnClickListener안에 넣으면 obser가 누적되서 계속 생성되므로 LiveData를 감시하는 observer가 계속 생성되게 된다
        loginViewModel.isUsedId.observe(viewLifecycleOwner) {
            if (binding.editTextJoinID.text!!.isEmpty()) {

            } else {
                Log.d(TAG, "checkIdResultMessage: ${it}")
                if (it == true) {
                    view!!.showSnackBarMessage("이미 사용중인 아이디 입니다.")
                    binding.checkIdButton.setImageResource(R.drawable.ic_check_fail2)
                    loginActivity.setVibrate()

                } else {
                    view!!.showSnackBarMessage("사용 가능한 아이디 입니다.")
                    binding.checkIdButton.setImageResource(R.drawable.ic_check_success)
                }
            }
        }

        // 회원가입 버튼
        binding.btnJoin.setOnClickListener {
            Log.d(TAG, "JoinFragment Join 버튼 누름")

            if (!emptyEditTextCheck()) {

            } else {
                val user = User().apply {
                    id = binding.editTextJoinID.text.toString()
                    pass = binding.editTextJoinPW.text.toString()
                    name = binding.editTextJoinName.text.toString()
                }

                Log.d(TAG, "onViewCreated: $user")
                Log.d(TAG, "loginViewModel.joinUser 실행됨 ")

                loginViewModel.joinUser(user)

                userJoin()
            }
        }

        loginViewModel.isCompleteJoin.observe(viewLifecycleOwner) {
            if (it == null) {
            } else {
                if (it == true) {

                    view!!.showSnackBarMessage("회원가입 되었습니다. 환영합니다.")

//                    loginViewModel.isCompleteJoin = true
                    loginViewModel.stateChange()

                    loginActivity.openFragment(3)
                } else {
                    view!!.showSnackBarMessage("회원가입에 실패하였습니다.")
                }
            }
        }
    } // End of onViewCreated

    private fun userJoin() {
        if (loginViewModel.isUsedId.value == false) {
            view!!.showSnackBarMessage("아이디 중복 체크를 해주세요")
            return
        }

    } // End of userJoin

    private fun emptyEditTextCheck(): Boolean {
        if (binding.editTextJoinID.text!!.isEmpty()) {
            view!!.showSnackBarMessage("아이디를 입력해주세요")
            return false
        } else if (binding.editTextJoinPW.text!!.isEmpty()) {
            view!!.showSnackBarMessage("비밀번호를 입력해주세요")
            return false
        } else if (binding.editTextJoinName.text!!.isEmpty()) {
            view!!.showSnackBarMessage("닉네임을 입력해주세요")
            return false
        }

        return true
    } // End of emptyEditTextCheck
} // End of JoinFragment
