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


package com.acs.quick.common.data.repository

import com.acs.quick.common.data.callback.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

/**
 * 网络请求基类，强制返回 Flow&lt;NetworkResult&lt;T&gt;&gt;。
 *
 * @author Zhai Jie
 */
open class BaseRepository {

    protected fun <T> request(requestBlock: suspend FlowCollector<NetworkResult<T>>.() -> Unit): Flow<NetworkResult<T>> {
        return flow(block = requestBlock).flowOn(Dispatchers.IO)
    }
}
