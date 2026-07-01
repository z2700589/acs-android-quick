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


package com.acs.quick.common.utils

import android.content.Context
import android.content.SharedPreferences
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.acs.quick.common.data.bean.User
import androidx.core.content.edit

/**
 * 轻量 SP 工具，用于向后兼容读取旧数据。新代码请用 [CommonPreferencesDataStore]。
 *
 * @author 马世鹏
 */
class SpUtil(val context: Context) {

    companion object {
        private const val SP_NAME = "tanikawa"
        private const val KEY_USER_INFO = "userInfo"

        private val moshi: Moshi by lazy {
            Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        }

        private val userAdapter by lazy { moshi.adapter(User::class.java) }
    }

    val sp: SharedPreferences = context.applicationContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)

    fun user(): User? {
        val json = sp.getString(KEY_USER_INFO, null) ?: return null
        return runCatching { userAdapter.fromJson(json) }.getOrNull()
    }

    fun clearUser() {
        sp.edit {
            putString(KEY_USER_INFO, null)
            putString("UserAccount", null)
            putString("uid", null)
            putString("token", null)
            putString("ucmsToken", null)
        }
    }
}
