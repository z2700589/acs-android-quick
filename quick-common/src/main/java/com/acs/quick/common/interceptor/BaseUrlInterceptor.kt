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
import com.acs.quick.common.config.UrlConfig
import com.acs.quick.common.config.UrlConfig.Companion.HEADER_KEY_APP_NAME
import com.acs.quick.common.config.UrlConfig.Companion.HEADER_KEY_PLATFORM
import com.acs.quick.common.config.UrlConfig.Companion.HEADER_VALUE_APPNAME
import com.acs.quick.common.config.UrlConfig.Companion.HEADER_VALUE_PLATFORM
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response

/**
 * 动态替换 Base URL 的 OkHttp 拦截器，通过 Header 中的 URL_TYPE 查表路由。
 *
 * @author Zhai Jie
 */
class BaseUrlInterceptor(private val context: Context) : Interceptor {

    /** 切换环境后调用，清除缓存 */
    fun refreshEnv() {
        UrlConfig.clearEnvCache()
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val urlConfig = UrlConfig.getEnv(context)
        val request = chain.request()
        val oldHttpUrl = request.url
        val builder = request.newBuilder()
        val headerValues = request.headers(UrlConfig.URL_TYPE)

        if (headerValues.isNotEmpty()) {
            builder.removeHeader(UrlConfig.URL_TYPE)
            val newBaseUrl: HttpUrl? = when (headerValues[0]) {
                UrlConfig.URL_TYPE_MAIN -> urlConfig.main_url.toHttpUrlOrNull()
                UrlConfig.URL_TYPE_OMS -> urlConfig.oms_url.toHttpUrlOrNull()
                UrlConfig.URL_TYPE_MMS -> urlConfig.mms_url.toHttpUrlOrNull()
                UrlConfig.URL_TYPE_UMS -> urlConfig.ums_url.toHttpUrlOrNull()
                UrlConfig.URL_TYPE_AMS -> urlConfig.ams_url.toHttpUrlOrNull()
                UrlConfig.URL_TYPE_OSS -> urlConfig.oss_url.toHttpUrlOrNull()
                UrlConfig.URL_TYPE_IMG -> urlConfig.img_url.toHttpUrlOrNull()
                UrlConfig.URL_TYPE_CONTRACT -> urlConfig.contract_url.toHttpUrlOrNull()
                UrlConfig.URL_TYPE_TCS -> urlConfig.tcs_url.toHttpUrlOrNull()
                else -> urlConfig.main_url.toHttpUrlOrNull()
            }
            newBaseUrl?.run {
                val newHttpUrl = oldHttpUrl.newBuilder()
                    .scheme(scheme)
                    .host(host)
                    .port(port)
                    .build()
                val newBuilder = builder
                    .url(newHttpUrl)
                    .addHeader(HEADER_KEY_APP_NAME, HEADER_VALUE_APPNAME)
                    .addHeader(HEADER_KEY_PLATFORM, HEADER_VALUE_PLATFORM)
                    .build()
                return chain.proceed(newBuilder)
            }
        }
        return chain.proceed(request)
    }

}
