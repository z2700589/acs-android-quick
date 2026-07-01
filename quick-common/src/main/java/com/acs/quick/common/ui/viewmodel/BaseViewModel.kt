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


package com.acs.quick.common.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acs.quick.common.data.state.UIState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicInteger

/**
 * MVVM ViewModel 基类，基于 Kotlin Flow + UIState，封装 Loading 计数、异常捕获、Toast 发送。
 *
 * @author Zhai Jie
 */
abstract class BaseViewModel : ViewModel() {

    /** 并发请求计数，0 → 隐藏 loading，>0 → 显示 */
    private val loadingCount = AtomicInteger(0)

    protected val _uiState = MutableStateFlow<UIState<*>>(UIState.Idle)
    val uiState: StateFlow<UIState<*>> = _uiState.asStateFlow()

    /** DataBinding 兼容，XML 中可用 `viewModel.isLoading` */
    val isLoading: Boolean get() = _uiState.value is UIState.Loading

    private val _toastMessage = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

    /**
     * 执行一段挂起代码，自动管理 Loading/Error/Toast。
     * 支持嵌套调用 — 只有最外层返回才切到非 Loading 状态。
     */
    protected fun execute(
        onError: ((Throwable) -> Unit)? = null,
        block: suspend () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.Main.immediate + exceptionHandler) {
            applyLoading()
            try {
                withContext(Dispatchers.IO) { block() }
                resolveLoading(UIState.Idle)
            } catch (e: Exception) {
                resolveLoading(UIState.Error(e.message ?: "未知错误", e))
                if (onError != null) {
                    onError(e)
                } else {
                    _toastMessage.tryEmit(e.message ?: "未知错误")
                }
            }
        }
    }

    /**
     * 同 [execute]，但成功时携带返回数据，状态流转至 Success(data)。
     */
    protected fun <T> executeWithResult(
        onError: ((Throwable) -> Unit)? = null,
        block: suspend () -> T
    ) {
        viewModelScope.launch(Dispatchers.Main.immediate + exceptionHandler) {
            applyLoading()
            try {
                val result = withContext(Dispatchers.IO) { block() }
                resolveLoading(UIState.Success(result))
            } catch (e: Exception) {
                resolveLoading(UIState.Error(e.message ?: "未知错误", e))
                if (onError != null) {
                    onError(e)
                } else {
                    _toastMessage.tryEmit(e.message ?: "未知错误")
                }
            }
        }
    }

    /** 手动切换 UIState（如展示空状态） */
    protected fun setUIState(state: UIState<*>) {
        _uiState.value = state
    }

    protected fun showToast(message: String) {
        _toastMessage.tryEmit(message)
    }

    // ---- 内部实现 ----

    /** 首次请求时切到 Loading */
    private fun applyLoading() {
        if (loadingCount.incrementAndGet() == 1) {
            _uiState.value = UIState.Loading
        }
    }

    /** 解除 Loading：所有嵌套请求都返回后才流转到目标状态 */
    private fun resolveLoading(target: UIState<*>) {
        if (loadingCount.decrementAndGet() <= 0) {
            loadingCount.set(0)
            _uiState.value = target
        }
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        // 异常穿透 try/catch → 强制重置计数
        loadingCount.set(0)
        _uiState.value = UIState.Error(throwable.message ?: "未知错误", throwable)
        _toastMessage.tryEmit(throwable.message ?: "未知错误")
    }
}
