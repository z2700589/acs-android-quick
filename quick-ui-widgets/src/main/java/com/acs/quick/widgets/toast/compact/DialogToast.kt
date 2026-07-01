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


package com.acs.quick.widgets.toast.compact

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.widget.Toast
import com.blankj.utilcode.util.LogUtils
import com.acs.quick.widgets.toast.config.ToastConfig
import com.acs.quick.widgets.toast.custom.CommonDialog

/**
 * author： 马世鹏
 * time: 2022/4/25
 * desc:
 */
internal class DialogToast(toastView: View, config: ToastConfig) :
    BaseCompatToast(toastView, config) {

    lateinit var toast: CommonDialog
    private val handler = Handler(Looper.getMainLooper()) {
        if (it.what == delayedWhat) {
            kotlin.runCatching { toast.dismiss() }
        }
        return@Handler false
    }
    private val delayedWhat = 1

    override fun show() {
        kotlin.runCatching {
            toast = createToast()
            toast.show()
            durationExec()
        }
    }

    private fun durationExec() {
        val message = Message()
        message.what = delayedWhat
        handler.sendMessageDelayed(message, if (config.duration == Toast.LENGTH_SHORT) DURATION_SHORT else DURATION_LONG)
    }

    override fun cancel() {
        kotlin.runCatching {
            toast.dismiss()
        }
    }

    override fun updateConfig(newConfig: ToastConfig) {
        LogUtils.d("-----------------------------" + 123)
        super.updateConfig(newConfig)
        handler.removeMessages(delayedWhat)
        durationExec()
    }

    private fun createToast() =
        CommonDialog(toastView.context, toastView)
            .apply {
                window?.attributes?.apply {
                    gravity = config.location.gravity
                    x = config.location.xOffset
                    y = config.location.yOffset
                }
            }

    private fun getScreenWidth(): Int = toastView.context.resources.displayMetrics.widthPixels
}
