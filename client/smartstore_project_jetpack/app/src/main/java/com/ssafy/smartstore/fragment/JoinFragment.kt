package com.ssafy.smartstore.fragment

import android.content.Context
import android.graphics.Paint.Join
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ssafy.smartstore.activity.LoginActivity
import com.ssafy.smartstore.activity.MainActivity
import com.ssafy.smartstore.databinding.FragmentJoinBinding
import com.ssafy.smartstore.dto.User
import com.ssafy.smartstore.service.UserService
import com.ssafy.smartstore.util.RetrofitCallback
import com.ssafy.smartstore.util.showToastMessage

// 회원 가입 화면
private const val TAG = "JoinFragment_싸피"

class JoinFragment : Fragment() {
    private var checkedId = false
    lateinit var binding: FragmentJoinBinding
    lateinit var userService: UserService
    private lateinit var loginActivity: LoginActivity

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
        userService = UserService()

        //id 중복 확인 버튼
        binding.btnConfirm.setOnClickListener {
            // DB랑 통신해서 getId해야됨
            var id = binding.editTextJoinID.text.toString()

            userService.getId(id, CheckIdCallback())
        }

        // 회원가입 버튼
        binding.btnJoin.setOnClickListener {
            val inputId = binding.editTextJoinID.text.toString()
            val inputPass = binding.editTextJoinPW.text.toString()
            val inputName = binding.editTextJoinName.text.toString()

            if (checkedId && inputId.isNotEmpty() && inputPass.isNotEmpty() && inputName.isNotEmpty()) {
                val user = User().apply {
                    id = inputId
                    pass = inputPass
                    name = inputName
                }

                userService.join(user, JoinCallback())


            } else {
                if (!checkedId) {
                    requireContext().showToastMessage("아이디 중복 체크를 해주세요.")
                } else {
                    requireContext().showToastMessage("입력창이 비어있을 수 없습니다.")
                }
            }

            // 회원가입 성공시 프레그먼트 이동
        }
    }

    inner class JoinCallback : RetrofitCallback<Boolean> {

        override fun onSuccess(code: Int, responseData: Boolean) {
            requireContext().showToastMessage("회원가입 되었습니다 환영합니다.")
            loginActivity.openFragment(3)
        }

        override fun onError(t: Throwable) {
            Log.d(TAG, t.message ?: "아이디 중복 체크 중 에러가 발생했습니다.")
        }

        override fun onFailure(code: Int) {
            Log.d(TAG, "onResponse: Error Code $code")
        }

    }

    inner class CheckIdCallback : RetrofitCallback<Boolean> {
        override fun onSuccess(code: Int, responseData: Boolean) {
            if (responseData) {
                requireContext().showToastMessage("이미 사용중인 아이디 입니다.")
                checkedId = false
            } else {
                requireContext().showToastMessage("사용 가능한 아이디 입니다.")
                checkedId = true
            }
        }

        override fun onError(t: Throwable) {
            Log.d(TAG, t.message ?: "아이디 중복 체크 중 에러가 발생했습니다.")
        }

        override fun onFailure(code: Int) {
            Log.d(TAG, "onResponse: Error Code $code")
        }
    }
}