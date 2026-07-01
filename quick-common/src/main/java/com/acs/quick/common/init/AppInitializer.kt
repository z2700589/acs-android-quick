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


package com.acs.quick.common.init

import android.content.Context
import androidx.appcompat.widget.AppCompatTextView
import androidx.startup.Initializer
import com.drake.statelayout.StateConfig
import com.acs.quick.common.BuildConfig
import com.acs.quick.common.R
import com.therouter.TheRouter
import me.jessyan.autosize.AutoSizeConfig
import timber.log.Timber

/**
 * AndroidX Startup 初始化入口。
 *
 * @author Zhai Jie
 */
class AppInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            TheRouter.isDebug = true
        }
        // 屏蔽系统字体缩放
        AutoSizeConfig.getInstance().isExcludeFontScale = true
        // BRV 缺省页
        StateConfig.apply {
            loadingLayout = R.layout.quick_layout_loading
            emptyLayout = R.layout.quick_layout_empty
            onEmpty {
                it?.let {
                    val tvEmpty = this.findViewById<AppCompatTextView>(R.id.quick_tv_empty)
                    if (it is String) tvEmpty.text = it
                }
            }
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
