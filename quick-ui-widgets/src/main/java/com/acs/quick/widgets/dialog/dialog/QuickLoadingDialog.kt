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


package com.acs.quick.widgets.dialog.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import com.acs.quick.widgets.R

/**
 * @author Zhai Jie (migrated from mms-app)
 * @Description (migrated from mms-app) 全屏加载 Loading Dialog
 * 内容居中显示，左右距离屏幕边缘各 24dp。
 * @Date 2022/4/19 15:37
 */
class QuickLoadingDialog private constructor(context: Context) : Dialog(context, R.style.quick_BaseDialogTheme) {

    class Builder(private val context: Context) {

        private var cancelable: Boolean = true
        private var cancelOutside: Boolean = true

        fun setCancelable(cancelable: Boolean): Builder {
            this.cancelable = cancelable
            return this
        }

        fun setCancelOutside(cancelOutside: Boolean): Builder {
            this.cancelOutside = cancelOutside
            return this
        }

        fun create(): QuickLoadingDialog {
            return QuickLoadingDialog(context).apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                setContentView(LayoutInflater.from(context).inflate(R.layout.quick_loading_dialog, null))
                setCancelable(cancelable)
                setCanceledOnTouchOutside(cancelOutside)
                // 宽度撑满以支持内容自行通过 paddingHorizontal 控制 24dp 边距
                window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                window?.setGravity(Gravity.CENTER)
            }
        }
    }
}
