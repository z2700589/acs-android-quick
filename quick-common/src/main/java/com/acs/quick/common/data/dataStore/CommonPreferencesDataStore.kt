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


package com.acs.quick.common.data.dataStore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.acs.quick.common.BuildConfig
import com.acs.quick.common.config.UrlConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


/**
 * DataStore 持久化工具，管理环境、用户信息等键值对。
 *
 * @author Zhai Jie
 */
object CommonPreferencesDataStore {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val Context.commonDataStore: DataStore<Preferences> by preferencesDataStore(name = "common")

    private val envCode = intPreferencesKey("envCode")
    private val uid = stringPreferencesKey("uid")
    private val cid = stringPreferencesKey("cid")
    private val token = stringPreferencesKey("token")
    private val ucmsToken = stringPreferencesKey("ucms_token")
    private val userName = stringPreferencesKey("user_name")
    private val userAccount = stringPreferencesKey("user_account")
    private val location = stringPreferencesKey("location")
    private val longitude = stringPreferencesKey("longitude")
    private val latitude = stringPreferencesKey("latitude")

    // ---- 环境 ----

    @JvmStatic
    fun setEnvironmentCode(context: Context, code: Int) = scope.launch {
        context.commonDataStore.edit { setting -> setting[envCode] = code }
        UrlConfig.initEnvCache(code)
    }

    @JvmStatic
    fun getEnvironmentCode(context: Context): Int = runBlocking {
        context.commonDataStore.data.map { settings ->
            if (BuildConfig.DEBUG) settings[envCode] ?: UrlConfig.TEST_ENV.code else UrlConfig.RELEASE_ENV.code
        }.first()
    }

    // ---- uid ----

    @JvmStatic
    fun saveUid(context: Context, _uid: String?) = scope.launch {
        context.commonDataStore.edit { setting -> setting[uid] = _uid ?: "" }
    }

    @JvmStatic
    fun getUid(context: Context): String = runBlocking {
        context.commonDataStore.data.map { setting -> setting[uid] ?: "" }.first()
    }

    // ---- cid / clientId ----

    @JvmStatic
    fun saveClientId(context: Context, clientId: String?) = scope.launch {
        context.commonDataStore.edit { setting -> setting[cid] = clientId ?: "" }
    }

    @JvmStatic
    fun getClientId(context: Context): String = runBlocking {
        context.commonDataStore.data.map { setting -> setting[cid] ?: "" }.first()
    }

    // ---- token ----

    @JvmStatic
    fun saveToken(context: Context, _token: String?) = scope.launch {
        context.commonDataStore.edit { setting -> setting[token] = _token ?: "" }
    }

    @JvmStatic
    fun getToken(context: Context): String = runBlocking {
        context.commonDataStore.data.map { setting -> setting[token] ?: "" }.first()
    }

    // ---- 用户信息 ----

    @JvmStatic
    fun saveUserName(context: Context, _username: String?) = scope.launch {
        context.commonDataStore.edit { setting -> setting[userName] = _username ?: "" }
    }

    @JvmStatic
    fun getUserName(context: Context): String = runBlocking {
        context.commonDataStore.data.map { setting -> setting[userName] ?: "" }.first()
    }

    @JvmStatic
    fun saveUserAccount(context: Context, _userAccount: String?) = scope.launch {
        context.commonDataStore.edit { setting -> setting[userAccount] = _userAccount ?: "" }
    }

    @JvmStatic
    fun getUserAccount(context: Context): String = runBlocking {
        context.commonDataStore.data.map { setting -> setting[userAccount] ?: "" }.first()
    }

    // ---- 地理位置 ----

    @JvmStatic
    fun saveLocation(context: Context, mLocation: String?) = scope.launch {
        context.commonDataStore.edit { setting -> setting[location] = mLocation ?: "" }
    }

    @JvmStatic
    fun getLocation(context: Context): String = runBlocking {
        context.commonDataStore.data.map { setting -> setting[location] ?: "" }.first()
    }

    @JvmStatic
    fun saveLongitude(context: Context, mLongitude: String?) = scope.launch {
        context.commonDataStore.edit { setting -> setting[longitude] = mLongitude ?: "" }
    }

    @JvmStatic
    fun getLongitude(context: Context): String = runBlocking {
        context.commonDataStore.data.map { setting -> setting[longitude] ?: "" }.first()
    }

    @JvmStatic
    fun saveLatitude(context: Context, mLatitude: String?) = scope.launch {
        context.commonDataStore.edit { setting -> setting[latitude] = mLatitude ?: "" }
    }

    @JvmStatic
    fun getLatitude(context: Context): String = runBlocking {
        context.commonDataStore.data.map { setting -> setting[latitude] ?: "" }.first()
    }

    // ---- 清空 ----

    @JvmStatic
    fun clear(context: Context) = scope.launch {
        context.commonDataStore.edit { it.clear() }
    }

    /**
     * 预取常用键值到内存缓存，供 Interceptor 高频调用。
     * runBlocking 仅在 Hilt 初始化时调用一次，不阻塞网络线程。
     */
    @JvmStatic
    fun initCache(context: Context, cache: MutableMap<String, String>) = runBlocking {
        val prefs = context.commonDataStore.data.map { it }.firstOrNull() ?: return@runBlocking
        cache[UrlConfig.PARAMS_KEY_UID] = prefs[uid] ?: ""
        cache[UrlConfig.PARAMS_KEY_TOKEN] = prefs[token] ?: ""

        // 预填充环境 code 缓存，避免后续 getEnv() 阻塞 OkHttp 线程
        val envCodeValue = if (BuildConfig.DEBUG) {
            prefs[envCode] ?: UrlConfig.TEST_ENV.code
        } else {
            UrlConfig.RELEASE_ENV.code
        }
        UrlConfig.initEnvCache(envCodeValue)
    }

}
