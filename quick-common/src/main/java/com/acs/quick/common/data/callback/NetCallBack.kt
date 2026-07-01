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


package com.acs.quick.common.data.callback

import com.acs.quick.common.exception.ServerException
import org.json.JSONException
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.net.UnknownServiceException

/**
 * 网络请求结果 sealed class，替代 CallBack 回调链。
 *
 * @author Zhai Jie
 */
sealed class NetworkResult<out T> {

    data class Success<out T>(val value: T) : NetworkResult<T>()

    data class Failure(val throwable: Throwable) : NetworkResult<Nothing>() {
        /** 格式化错误消息，401 时触发 [onAuthExpired] */
        fun formatMessage(): String = formatErrorMessage(throwable)
    }
}

/** 鉴权过期回调，由 BaseActivity 注入 */
var onAuthExpired: (() -> Unit)? = null

/** 将网络异常转为用户可读的错误文案 */
fun formatErrorMessage(throwable: Throwable): String {
    return when (throwable) {
        is UnknownServiceException, is UnknownHostException -> "请求失败，请检查网络"
        is ConnectException -> "请求失败，无法连接至服务器"
        is SocketTimeoutException -> "请求失败，网络超时"
        is JSONException -> "数据解析错误：${throwable.message}"
        is HttpException -> when (throwable.code()) {
            500 -> "服务器发生错误"
            404 -> "请求地址不存在"
            403 -> "请求被服务器拒绝"
            307 -> "请求被重定向到其他页面"
            else -> throwable.message() ?: "未知错误"
        }
        is ServerException -> {
            if (throwable.code == "401") {
                onAuthExpired?.invoke()
            }
            throwable.message ?: "未知错误"
        }
        else -> throwable.message ?: "未知错误"
    }
}
