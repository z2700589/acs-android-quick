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


package com.acs.quick.widgets.toast.custom.image

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.IntDef
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isVisible

import com.acs.quick.widgets.toast.config.DefaultToastConfig
import com.acs.quick.widgets.databinding.QuickImageToastBinding
import com.acs.quick.widgets.R

/**
 * author： 马世鹏
 * time: 2022/4/22
 * desc:
 */

internal class ImageToast {

    @SuppressLint("InflateParams")
    fun provideToastView(context: Context, cachedView: View?, config: Config): View =
        QuickImageToastBinding.bind(
            cachedView ?: LayoutInflater.from(context)
                .inflate(R.layout.quick_image_toast, null)
        ).apply {
            DrawableCompat.setTint(root.background.mutate(), config.backgroundColor)
            val iconResource = if (config.iconResource == -1) {
                parseDefaultIconResource(config.type)
            } else {
                config.iconResource
            }
            ContextCompat.getDrawable(context, iconResource)?.let {
                image.isVisible = true
                image.setImageDrawable(it)
            }
            content.text = config.content
            content.setTextColor(config.contentColor)
        }.root

    class Config : DefaultToastConfig("imageToast") {

        @JvmField
        @Type
        var type = TYPE_INFO

        @JvmField
        @ColorInt
        var backgroundColor = DEFAULT_TOAST_BACKGROUND_COLOR

        @JvmField
        @DrawableRes
        var iconResource: Int = -1

        @JvmField
        @ColorInt
        var contentColor = Color.WHITE  // matches @color/quick_white
    }
}

@Retention(AnnotationRetention.BINARY)
@IntDef(
    TYPE_INFO,
    TYPE_WARNING,
    TYPE_COMPLETE,
    TYPE_FAILED,
)

internal annotation class Type

internal const val TYPE_INFO = 0
internal const val TYPE_WARNING = 1
internal const val TYPE_COMPLETE = 2
internal const val TYPE_FAILED = 3

// TODO: 默认的图
private fun parseDefaultIconResource(@Type type: Int): Int = when (type) {
    TYPE_INFO, TYPE_COMPLETE -> R.mipmap.quick_ic_image_toast_success
    TYPE_FAILED -> R.mipmap.quick_ic_image_toast_failed
    else -> R.mipmap.quick_ic_image_toast_success
}

/** matches @color/quick_toast_bg_dark */
@ColorInt
internal val DEFAULT_TOAST_BACKGROUND_COLOR = 0xFF0F1114.toInt()
