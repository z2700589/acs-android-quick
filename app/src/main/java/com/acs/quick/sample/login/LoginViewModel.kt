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

import androidx.lifecycle.viewModelScope
import com.acs.quick.common.data.state.UIState
import com.acs.quick.common.ui.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

/**
 * 登录页 ViewModel — 表单状态 + 校验 + 登录。
 *
 * @author Zhai Jie
 */
@HiltViewModel
class LoginViewModel @Inject constructor() : BaseViewModel() {

    // ---- 双向绑定 ----

    val phone = MutableStateFlow("")
    val password = MutableStateFlow("")
    val rememberMe = MutableStateFlow(false)
    val agreedToTerms = MutableStateFlow(false)

    // ---- 校验错误 ----

    private val _phoneError = MutableStateFlow<String?>(null)
    val phoneError: StateFlow<String?> = _phoneError.asStateFlow()

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError.asStateFlow()

    // ---- 表单有效性 ----

    /** 登录按钮启用条件：字段非空 + 同意协议 + 非加载中 */
    val isFormValid: StateFlow<Boolean> = combine(
        phone, password, agreedToTerms, uiState
    ) { p, pw, agreed, state ->
        p.isNotEmpty() && pw.isNotEmpty() && agreed && state !is UIState.Loading
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    // ---- 导航事件 ----

    private val _navigateToMain = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val navigateToMain: SharedFlow<Unit> = _navigateToMain.asSharedFlow()

    // ---- 业务方法 ----

    fun validatePhone(): Boolean {
        val value = phone.value.trim()
        return when {
            value.isEmpty() -> {
                _phoneError.value = "请输入手机号"
                false
            }
            !value.matches(Regex("^1[3-9]\\d{9}$")) -> {
                _phoneError.value = "手机号格式不正确"
                false
            }
            else -> {
                _phoneError.value = null
                true
            }
        }
    }

    fun validatePassword(): Boolean {
        val value = password.value
        return when {
            value.isEmpty() -> {
                _passwordError.value = "请输入密码"
                false
            }
            value.length < 6 -> {
                _passwordError.value = "密码长度不能少于6位"
                false
            }
            else -> {
                _passwordError.value = null
                true
            }
        }
    }

    /** 校验表单 + 登录 */
    fun login() {
        if (!validatePhone()) return
        if (!validatePassword()) return
        if (!agreedToTerms.value) return

        execute {
            // TODO: 接入实际登录 API
            delay(1500.milliseconds)
            _navigateToMain.tryEmit(Unit)
        }
    }

    fun loginWithWechat() {
        // TODO: 接入微信 SDK 登录
    }

    fun clearPhoneError() {
        _phoneError.value = null
    }

    fun clearPasswordError() {
        _passwordError.value = null
    }
}
