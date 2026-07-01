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


package com.acs.quick.widgets.toast.custom

import android.app.Dialog
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.acs.quick.widgets.R


/**
 * author： 马世鹏
 *
 * time: 2022/5/5
 *
 * desc:
 */
class CommonDialog(
    context: Context,
    private val toastView: View,
    theme: Int = R.style.quick_show_virtual_toast_dialog
) : Dialog(context, theme) {

    override fun onStart() {
        super.onStart()
        window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        )
        window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
        )
        window?.setFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
        )
        window?.apply {
            val d = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                var display = context.display
                if (display == null) {
                    display = windowManager.defaultDisplay!!
                }
                display
            } else {
                windowManager.defaultDisplay
            }
            val size = Point()
            d.getSize(size)
            attributes = attributes?.apply {
                windowAnimations = android.R.style.Animation_Toast
                val maxWidth = (size.x * 0.7f).toInt()
                if (width > maxWidth) {
                    width = maxWidth
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(toastView)
    }


}
