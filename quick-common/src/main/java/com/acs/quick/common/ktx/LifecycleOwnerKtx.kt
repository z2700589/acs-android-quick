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


package com.acs.quick.common.ktx

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * 生命周期感知的 Flow 收集，默认在 STARTED 时活跃。
 * 用法: collectFlow(viewModel.uiState) { state -> ... }
 */
inline fun <T> LifecycleOwner.collectFlow(
    flow: Flow<T>,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline action: suspend (T) -> Unit
) {
    lifecycleScope.launch {
        repeatOnLifecycle(minActiveState) {
            flow.collect { value -> action(value) }
        }
    }
}

/** collectLatest 版本，适合高频更新场景 */
inline fun <T> LifecycleOwner.collectLatestFlow(
    flow: Flow<T>,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline action: suspend (T) -> Unit
) {
    lifecycleScope.launch {
        repeatOnLifecycle(minActiveState) {
            flow.collectLatest { value -> action(value) }
        }
    }
}
