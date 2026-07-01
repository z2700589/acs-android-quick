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


package com.acs.quick.widgets.toast.custom.text

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.graphics.drawable.DrawableCompat

import com.acs.quick.widgets.toast.config.DefaultToastConfig
import com.acs.quick.widgets.databinding.QuickTextToastBinding
import com.acs.quick.widgets.R

/**
 * author： 马世鹏
 * time: 2022/4/22
 * desc:
 */

internal class TextToast {

    @SuppressLint("InflateParams")
    fun provideToastView(context: Context, cachedView: View?, config: Config): View = QuickTextToastBinding.bind(cachedView ?: LayoutInflater.from(context).inflate(R.layout.quick_text_toast, null))
        .apply {
            DrawableCompat.setTint(root.background.mutate(), config.backgroundColor)
            content.text = config.content
            content.setTextColor(config.contentColor)
        }.root


    class Config : DefaultToastConfig("toast") {

        @JvmField
        @ColorInt
        var backgroundColor = DEFAULT_TOAST_BACKGROUND_COLOR

        @JvmField
        @ColorInt
        var contentColor = Color.WHITE  // matches @color/quick_white

        @JvmField
        var contentSize: Float = 16f
    }
}

/** matches @color/quick_toast_bg */
@ColorInt
internal val DEFAULT_TOAST_BACKGROUND_COLOR = 0xB30F1114.toInt()
