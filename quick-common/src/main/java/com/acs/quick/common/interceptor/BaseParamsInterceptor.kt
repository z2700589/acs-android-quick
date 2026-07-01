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


package com.acs.quick.common.interceptor

import android.content.Context
import com.acs.quick.common.config.UrlConfig.Companion.PARAMS_KEY_OS
import com.acs.quick.common.config.UrlConfig.Companion.PARAMS_KEY_OS_VERSION
import com.acs.quick.common.config.UrlConfig.Companion.PARAMS_KEY_TOKEN
import com.acs.quick.common.config.UrlConfig.Companion.PARAMS_KEY_UID
import com.acs.quick.common.config.UrlConfig.Companion.PARAMS_KEY_VERSION
import com.acs.quick.common.data.dataStore.CommonPreferencesDataStore
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.DeviceUtils
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.util.concurrent.ConcurrentHashMap

/**
 * 自动为每个请求注入公共参数（uid、token、version 等），用内存缓存避免 runBlocking 阻塞 OkHttp 线程。
 *
 * @author Zhai Jie
 */
class BaseParamsInterceptor(private val context: Context) : Interceptor {

    /** 公共参数内存缓存 */
    private val paramsCache = ConcurrentHashMap<String, String>()

    init {
        refreshCache()
    }

    /** 刷新缓存，构造时/登录后/切换账号后调用 */
    fun refreshCache() {
        CommonPreferencesDataStore.initCache(context, paramsCache)
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val oldRequest = chain.request()

        val newRequest = if (oldRequest.method == "POST") {
            buildPostRequest(oldRequest)
        } else {
            buildGetRequest(oldRequest)
        }

        return chain.proceed(newRequest)
    }

    private fun buildPostRequest(oldRequest: Request): Request {
        val formBuilder = FormBody.Builder()
        if (oldRequest.body is FormBody) {
            val body = oldRequest.body as FormBody
            for (i in 0 until body.size) {
                formBuilder.addEncoded(body.encodedName(i), body.encodedValue(i))
            }
        }
        appendCommonParams(formBuilder)
        return oldRequest.newBuilder()
            .post(formBuilder.build())
            .build()
    }

    private fun buildGetRequest(oldRequest: Request): Request {
        val originalUrl = oldRequest.url
        val newUrl = originalUrl.newBuilder()
            .addQueryParameter(PARAMS_KEY_UID, paramsCache[PARAMS_KEY_UID] ?: "")
            .addQueryParameter(PARAMS_KEY_TOKEN, paramsCache[PARAMS_KEY_TOKEN] ?: "")
            .addQueryParameter(PARAMS_KEY_VERSION, AppUtils.getAppVersionName())
            .addQueryParameter(PARAMS_KEY_OS, "2")
            .addQueryParameter(PARAMS_KEY_OS_VERSION, DeviceUtils.getSDKVersionName())
            .build()
        return oldRequest.newBuilder()
            .url(newUrl)
            .build()
    }

    private fun appendCommonParams(builder: FormBody.Builder) {
        builder.addEncoded(PARAMS_KEY_UID, paramsCache[PARAMS_KEY_UID] ?: "")
        builder.addEncoded(PARAMS_KEY_TOKEN, paramsCache[PARAMS_KEY_TOKEN] ?: "")
        builder.addEncoded(PARAMS_KEY_VERSION, AppUtils.getAppVersionName())
        builder.addEncoded(PARAMS_KEY_OS, "2")
        builder.addEncoded(PARAMS_KEY_OS_VERSION, DeviceUtils.getSDKVersionName())
    }
}
