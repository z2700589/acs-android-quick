/*
 * Copyright 2026 zhaijie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.acs.quick.sample.login

import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.acs.quick.common.ui.SystemBarHelper
import com.acs.quick.common.ui.activity.BaseActivity
import com.acs.quick.sample.main.MainActivity
import com.acs.quick.sample.R
import com.acs.quick.sample.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * 登录页 — 蓝色渐变头部 + 白色状态栏图标，滚动时动态切换。
 *
 * @author Zhai Jie
 */
@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding, LoginViewModel>() {

    override val mViewModel: LoginViewModel by viewModels()

    override fun getLayoutId(): Int = R.layout.activity_login

    // ---- 系统栏配置 ----

    /** 深色背景 → 白色图标 */
    override val lightStatusBars: Boolean = false

    override val lightNavigationBars: Boolean = false

    /** 顶部不消费 insets，蓝色 header 延伸到状态栏后方 */
    override val consumeTopInsets: Boolean = false

    /** 滚动时动态切换状态栏图标颜色 */
    override fun configureSystemBars() {
        super.configureSystemBars()
        mBinding.root.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            updateStatusBarForScroll(scrollY)
        }
    }

    companion object {
        /** 头部高度比例阈值 */
        private const val HEADER_HEIGHT_RATIO = 0.30f
    }

    /** 根据滚动位置切换状态栏图标颜色 */
    private fun updateStatusBarForScroll(scrollY: Int) {
        val headerEndPx = (resources.displayMetrics.heightPixels * HEADER_HEIGHT_RATIO).toInt()
        val shouldBeLight = scrollY < headerEndPx
        SystemBarHelper.setLightStatusBar(this, !shouldBeLight)
    }

    override fun initView() {
        mBinding.viewModel = mViewModel
        mBinding.lifecycleOwner = this
    }

    override fun initData() {
        // 观察导航事件
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.navigateToMain.collect {
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }
            }
        }
    }

    override fun initListener() {
        // 手机号
        mBinding.etPhone.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) mViewModel.validatePhone()
        }
        mBinding.etPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {
                mViewModel.phone.value = s?.toString() ?: ""
                mViewModel.clearPhoneError()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // 密码
        mBinding.etPassword.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) mViewModel.validatePassword()
        }
        mBinding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {
                mViewModel.password.value = s?.toString() ?: ""
                mViewModel.clearPasswordError()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // 校验错误 → TextInputLayout
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.phoneError.collect { error ->
                    mBinding.tilPhone.error = error
                    mBinding.tilPhone.isErrorEnabled = error != null
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.passwordError.collect { error ->
                    mBinding.tilPassword.error = error
                    mBinding.tilPassword.isErrorEnabled = error != null
                }
            }
        }

        // CheckBox → StateFlow
        mBinding.cbRemember.setOnCheckedChangeListener { _, isChecked ->
            mViewModel.rememberMe.value = isChecked
        }
        mBinding.cbAgreement.setOnCheckedChangeListener { _, isChecked ->
            mViewModel.agreedToTerms.value = isChecked
        }

        // 登录
        mBinding.btnLogin.setOnClickListener {
            mViewModel.login()
        }

        mBinding.tvForgotPassword.setOnClickListener {
            // TODO: 跳转忘记密码页
        }

        mBinding.tvUserAgreement.setOnClickListener {
            // TODO: 打开用户协议与隐私政策页
        }

        mBinding.tvRegister.setOnClickListener {
            // TODO: 跳转注册页
        }
    }
}
