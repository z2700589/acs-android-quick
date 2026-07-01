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


package com.acs.quick.common.di

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.acs.quick.common.BuildConfig
import com.acs.quick.common.config.UrlConfig
import com.acs.quick.common.download.DownloadManager
import com.acs.quick.common.interceptor.BaseParamsInterceptor
import com.acs.quick.common.interceptor.BaseUrlInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import okhttp3.logging.HttpLoggingInterceptor.Level.NONE
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Hilt 网络层注入模块（OkHttp、Retrofit、Moshi）。
 *
 * @author Zhai Jie
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val TIMEOUT_SECONDS = 12L

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder().run {
            connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            pingInterval(30, TimeUnit.SECONDS)
            connectionPool(ConnectionPool(5, 5, TimeUnit.MINUTES))
            addInterceptor(BaseUrlInterceptor(context))
            addInterceptor(BaseParamsInterceptor(context))
            addInterceptor(loggingInterceptor)
            build()
        }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) BODY else NONE
        }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit =
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(UrlConfig.RELEASE_ENV.main_url)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Singleton
    fun provideDownloadManager(retrofit: Retrofit): DownloadManager =
        DownloadManager(retrofit)
}
