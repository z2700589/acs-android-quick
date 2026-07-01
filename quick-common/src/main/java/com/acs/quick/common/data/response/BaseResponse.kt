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


package com.acs.quick.common.data.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.acs.quick.common.data.callback.NetworkResult
import com.acs.quick.common.exception.ServerException

/**
 * 通用后端响应体。
 *
 * @author Zhai Jie
 */
@JsonClass(generateAdapter = true)
data class BaseResponse<T>(
    @Json(name = "code")
    val code: String,
    @Json(name = "message")
    val message: String?,
    @Json(name = "msg")
    val msg: String?,
    @Json(name = "error")
    val error: String?,
    @Json(name = "data")
    val data: T?,
)

/** 处理含 data 字段的响应，code=200 但 data 为 null 时返回 Failure */
fun <T> BaseResponse<T>.processData(): NetworkResult<T> {
    return try {
        if (code == "200") {
            data?.let { NetworkResult.Success(it) }
                ?: NetworkResult.Failure(ServerException(code, "返回数据为空"))
        } else {
            NetworkResult.Failure(extractFailure())
        }
    } catch (e: Exception) {
        NetworkResult.Failure(e)
    }
}

/** 处理仅需 message/msg 的响应（如提交类接口） */
fun <T> BaseResponse<T>.processMessage(): NetworkResult<String> {
    return try {
        if (code == "200") {
            NetworkResult.Success(message ?: msg ?: "成功")
        } else {
            NetworkResult.Failure(extractFailure())
        }
    } catch (e: Exception) {
        NetworkResult.Failure(e)
    }
}

/** 非 200 时按 message > msg > error 优先级提取错误信息 */
private fun <T> BaseResponse<T>.extractFailure(): ServerException {
    val errorMsg = message ?: msg ?: error ?: "未知错误"
    return ServerException(code, errorMsg)
}
