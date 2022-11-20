package com.ssafy.smartstore.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.ssafy.smartstore.activity.LoginActivity
import com.ssafy.smartstore.databinding.FragmentJoinBinding
import com.ssafy.smartstore.dto.User
import com.ssafy.smartstore.util.RetrofitUtil.Companion.userService
import com.ssafy.smartstore.util.showToastMessage
import com.ssafy.smartstore.viewModels.LoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
        binding.btnConfirm.setOnClickListener {
            if (binding.editTextJoinID.text.isEmpty()) {
                requireContext().showToastMessage("아이디를 입력해주세요")
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    loginViewModel.checkUsedId(binding.editTextJoinID.text.toString())
                }
            }
        }

        checkIdShowToastMessage()

        // 회원가입 버튼
        binding.btnJoin.setOnClickListener {
            if (emptyEditTextCheck()) {
                requireContext().showToastMessage("빈 칸이 없어야 합니다.")
            } else {
                val user = User().apply {
                    id = binding.editTextJoinID.text.toString()
                    pass = binding.editTextJoinPW.text.toString()
                    name = binding.editTextJoinName.text.toString()
                }

                CoroutineScope(Dispatchers.IO).launch {
                    loginViewModel.joinUser(user)
                }

                userJoin()
            }
        }
    } // End of onViewCreated

    private fun checkIdShowToastMessage() {
        loginViewModel.isUsedId.observe(viewLifecycleOwner) {
            if (it) {
                requireContext().showToastMessage("이미 사용중인 아이디 입니다.")
            } else {
                requireContext().showToastMessage("사용 가능한 아이디 입니다.")
            }
        }
    } // End of checkIdShowToastMessage

    private fun userJoin() {
        if (loginViewModel.isUsedId.value == false) {
            requireContext().showToastMessage("아이디 중복 체크를 해주세요")
            return
        }

        loginViewModel.isCompleteJoin.observe(viewLifecycleOwner) {
            if (it) {
                requireContext().showToastMessage("회원가입 되었습니다 환영합니다.")
                loginActivity.openFragment(3)
            } else {
                requireContext().showToastMessage("회원가입에 실패하였습니다.")
            }
        }
    } // End of userJoin

    private fun emptyEditTextCheck(): Boolean {
        if (binding.editTextJoinID.text.isEmpty() || binding.editTextJoinPW.text.isEmpty() || binding.editTextJoinName.text.isEmpty()) {
            return false
        }

        return true
    } // End of emptyEditTextCheck
} // End of JoinFragment