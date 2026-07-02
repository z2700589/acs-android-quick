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


package com.acs.quick.common.ui

import android.app.Activity
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

/**
 * 系统栏工具：动态前景色切换、WindowInsets 适配。
 * 边到边初始化已交由 [androidx.activity.enableEdgeToEdge]，在 [BaseActivity] 中调用。
 *
 * @author Zhai Jie
 */
object SystemBarHelper {

    /** 动态切换状态栏图标前景色 */
    fun setLightStatusBar(activity: Activity, light: Boolean) {
        WindowInsetsControllerCompat(activity.window, activity.window.decorView).apply {
            isAppearanceLightStatusBars = light
        }
    }

    /** 动态切换导航栏图标前景色 */
    fun setLightNavigationBar(activity: Activity, light: Boolean) {
        WindowInsetsControllerCompat(activity.window, activity.window.decorView).apply {
            isAppearanceLightNavigationBars = light
        }
    }

    /**
     * 对根视图应用 WindowInsets，防止内容被系统栏遮挡。
     */
    fun applyInsets(
        rootView: View,
        consumeTop: Boolean = true,
        consumeBottom: Boolean = true
    ) {
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())

            val topInset = if (consumeTop) systemBars.top else 0
            // 底部取系统栏和 IME 最大值，避免键盘遮挡输入框
            val bottomInset = if (consumeBottom) maxOf(systemBars.bottom, ime.bottom) else 0

            view.setPadding(
                view.paddingLeft,
                topInset,
                view.paddingRight,
                bottomInset
            )
            insets
        }
    }
}
