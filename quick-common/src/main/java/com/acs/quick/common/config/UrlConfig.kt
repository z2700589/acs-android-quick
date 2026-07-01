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


package com.acs.quick.common.config

import android.content.Context
import com.acs.quick.common.data.dataStore.CommonPreferencesDataStore
import kotlinx.coroutines.runBlocking


/**
 * 多环境 URL 配置枚举。
 *
 * @author Zhai Jie
 */
enum class UrlConfig(
    val code: Int,
    val value: String,
    val main_url: String,
    val oms_url: String,
    val mms_url: String,
    val ums_url: String,
    val ams_url: String,
    val oss_url: String,
    val img_url: String,
    val tcs_url: String,
    val bpm_url: String,
    val clue_url: String,
    val socket_url: String,
    val contract_url: String,
) {
    TEST_ENV(
        code = 1,
        value = "测试环境",
        main_url = "https://mms-group1.dev.tanikawa.com/",
        oms_url = "https://oms-group1.dev.tanikawa.com/",
        mms_url = "https://mms-group1.dev.tanikawa.com/",
        ums_url = "https://ucms-group1.dev.tanikawa.com/api/",
        ams_url = "http://ams.api.dev.tanikawa.com/",
        oss_url = "https://oss-group1.dev.tanikawa.com/",
        img_url = "https://img-group1-mms.dev.tanikawa.com/",
        tcs_url = "https://tcs-group1.dev.tanikawa.com/api/",
        bpm_url = "https://bpm.dev.tanikawa.com/",
        clue_url = "https://mms-group1.dev.tanikawa.com/tis",
        socket_url = "wss://tcs-group1.dev.tanikawa.com/ws",
        contract_url = "https://contract-group1.dev.tanikawa.com/",
    ),
    DEVELOP_ENV(
        code = 2,
        value = "预发布环境",
        main_url = "https://mms.dev.tanikawa.com/",
        oms_url = "https://oms.dev.tanikawa.com/",
        mms_url = "https://mms.dev.tanikawa.com/",
        ums_url = "https://ucms.dev.tanikawa.com/api/",
        ams_url = "http://amsa.api.dev.tanikawa.com/",
        oss_url = "https://oss.dev.tanikawa.com/",
        img_url = "https://img.mms.dev.tanikawa.com/",
        tcs_url = "https://tcs.dev.tanikawa.com/api",
        bpm_url = "https://bpm.dev.tanikawa.com/",
        clue_url = "https://mms.dev.tanikawa.com/tis",
        socket_url = "ws://tcs.dev.tanikawa.com/ws",
        contract_url = "https://contract.dev.tanikawa.com/",
    ),
    API_ENV(
        code = 3,
        value = "Api环境",
        main_url = "https://mms-api.dev.tanikawa.com/",
        oms_url = "https://oms-group1.dev.tanikawa.com/",
        mms_url = "https://mms-group1.dev.tanikawa.com/",
        ums_url = "https://ucms-group1.dev.tanikawa.com/api/",
        ams_url = "https://ams.api.dev.tanikawa.com/",
        oss_url = "https://oss-group1.dev.tanikawa.com/",
        img_url = "https://img-group1-mms.dev.tanikawa.com",
        tcs_url = "https://tcs-group1.dev.tanikawa.com/api",
        bpm_url = "https://bpm.dev.tanikawa.com/",
        clue_url = "https://mms-group1.dev.tanikawa.com/tis",
        socket_url = "ws://tcs-group1.dev.tanikawa.com/ws",
        contract_url = "https://contract-group1.dev.tanikawa.com/",
    ),
    LOCAL_ENV(
        code = 4,
        value = "本地环境",
        main_url = "http://mms.app_php.com/",
        oms_url = "http://oms-api.dev.tanikawa.com/",
        mms_url = "http://mms-api.dev.tanikawa.com/",
        ums_url = "http://ucms-group1.dev.tanikawa.com/api/",
        ams_url = "http://ams.api.dev.tanikawa.com/",
        oss_url = "https://oss-group1.dev.tanikawa.com/",
        img_url = "http://img-group1.mms.dev.tanikawa.com",
        tcs_url = "http://www.currency_api.com/api",
        bpm_url = "https://bpm.dev.tanikawa.com/",
        clue_url = "https://mms-group1.dev.tanikawa.com/tis",
        socket_url = "ws://192.168.0.137:9501/",
        contract_url = "https://contract-group1.dev.tanikawa.com/",
    ),
    RELEASE_ENV(
        code = 5,
        value = "正式环境",
        main_url = "https://tis.tanikawa.com/",
        oms_url = "https://oms.tanikawa.com/",
        mms_url = "https://tis.tanikawa.com/",
        ums_url = "https://ums.tanikawa.com/api/",
        ams_url = "https://apiams.tanikawa.com/",
        oss_url = "https://oss.tanikawa.com/",
        img_url = "https://img-mms.tanikawa.com",
        tcs_url = "https://tcs.tanikawa.com/api/",
        bpm_url = "https://bpm.tanikawa.com/",
        clue_url = "https://tis.tanikawa.com/tis",
        socket_url = "wss://tcs.tanikawa.com/ws",
        contract_url = "https://contract.tanikawa.com/",
    );

    companion object {

        const val URL_TYPE = "URL_TYPE"
        const val URL_TYPE_MAIN = "${URL_TYPE}:MAIN"
        const val URL_TYPE_OMS = "${URL_TYPE}:OMS"
        const val URL_TYPE_MMS = "${URL_TYPE}:MMS"
        const val URL_TYPE_UMS = "${URL_TYPE}:UMS"
        const val URL_TYPE_AMS = "${URL_TYPE}:AMS"
        const val URL_TYPE_OSS = "${URL_TYPE}:OSS"               // OSS
        const val URL_TYPE_IMG = "${URL_TYPE}:IMG"               // 图片
        const val URL_TYPE_TCS = "${URL_TYPE}:TCS"               // 谷川币
        const val URL_TYPE_SOCKET = "${URL_TYPE}:SOCKET"         // 谷川币长链接
        const val URL_TYPE_CONTRACT = "${URL_TYPE}:CONTRACT"     // 合同
        const val URL_TYPE_T_LEARNING = "${URL_TYPE}:T_LEARNING" // 谷川培训学校


        const val HEADER_KEY_APP_NAME = "xuanner-AppName"
        const val HEADER_KEY_PLATFORM = "xuanner-AppType"
        const val HEADER_VALUE_APPNAME = "XUANNER"
        const val HEADER_VALUE_PLATFORM = "ANDROID"


        const val PARAMS_KEY_UID = "uid"
        const val PARAMS_KEY_TOKEN = "token"
        const val PARAMS_KEY_VERSION = "version"
        const val PARAMS_KEY_OS = "os"
        const val PARAMS_KEY_OS_VERSION = "osVersion"

        /** 环境 code 内存缓存，消除 runBlocking */
        @Volatile
        private var cachedEnvCode: Int? = null

        /** 预填充环境缓存，调用后 [getEnv] 不再阻塞 */
        fun initEnvCache(code: Int) {
            cachedEnvCode = code
        }

        /** 清除环境缓存 */
        fun clearEnvCache() {
            cachedEnvCode = null
        }

        /** 获取当前环境，优先从内存缓存取 */
        fun getEnv(context: Context): UrlConfig {
            val code = cachedEnvCode ?: runBlocking {
                CommonPreferencesDataStore.getEnvironmentCode(context)
            }.also { cachedEnvCode = it }

            for (value in UrlConfig.entries) {
                if (value.code == code) {
                    return value
                }
            }
            return RELEASE_ENV
        }
    }
}
