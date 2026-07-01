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

import android.view.View
import com.acs.quick.widgets.toast.config.ToastConfig

/**
 * author： 马世鹏
 * time: 2022/4/25
 * desc:
 */
internal abstract class BaseCompatToast(val toastView: View, var config: ToastConfig) :
    CompatToast {

    private var viewAttachListener: ViewAttachListener? = null

    override fun config(): ToastConfig = config

    override fun updateConfig(newConfig: ToastConfig) {
        config = newConfig
    }

    override fun view(): View = toastView

    override fun isShowing(): Boolean = toastView.windowVisibility == View.VISIBLE

    override fun setVisibilityObserver(changedListener: ToastVisibilityChangedListener) {
        viewAttachListener?.let {
            toastView.removeOnAttachStateChangeListener(viewAttachListener)
        }
        viewAttachListener = ViewAttachListener(visibilityChangedListener = changedListener)
        toastView.addOnAttachStateChangeListener(viewAttachListener)
    }

    override fun removeVisibilityObserver(changedListener: ToastVisibilityChangedListener) {
        toastView.removeOnAttachStateChangeListener(viewAttachListener)
    }
}

interface ToastVisibilityChangedListener {
    fun onToastVisibilityChanged(view: View, visible: Boolean)
}

const val DURATION_SHORT = 2000L
const val DURATION_LONG = 3500L
