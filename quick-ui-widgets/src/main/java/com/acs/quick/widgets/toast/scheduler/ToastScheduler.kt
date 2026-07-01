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


package com.acs.quick.widgets.toast.scheduler

import android.content.Context
import android.view.View
import com.blankj.utilcode.util.LogUtils
import com.acs.quick.widgets.toast.compact.CompatToast
import com.acs.quick.widgets.toast.compact.DialogToast
import com.acs.quick.widgets.toast.compact.ToastVisibilityChangedListener
import com.acs.quick.widgets.toast.config.ToastConfig
import com.acs.quick.widgets.toast.factory.ToastFactory

/**
 * author： 马世鹏
 * time: 2022/4/25
 * desc: toast 调度
 *
 * > 用来控制 toast的显示和隐藏，既不是队列也不是栈
 *
 * 显示规则为：
 *
 * - 当前如果没有显示的toast
 *
 *     创建一个新的toast进行显示
 *
 * - 当前有显示的toast
 *
 *     - 如果位置和别名（alias）相同，更新当前显示toast的配置，时间重新开始
 *
 *     - 如果位置和别名其中有一个不相同，关闭当前已经显示的toast，重新创建一个新的toast进行显示
 *
 */
internal object ToastScheduler : ToastVisibilityChangedListener {

    private var currentToast: CompatToast? = null

    @JvmStatic
    @Synchronized
    fun schedule(context: Context, newConfig: ToastConfig, toastFactory: ToastFactory) {
        LogUtils.d(newConfig.toString())
        val isSameAlias = currentToast?.config()?.alias == newConfig.alias
        val isSameLocation = newConfig.isSameLocation(currentToast?.config())
        val isShowing = currentToast?.isShowing() ?: false

        LogUtils.d("isSameAlias:$isSameAlias#isSameLocation:$isSameLocation#isShowing:$isShowing")

        if (isShowing && isSameAlias && isSameLocation) {
            // 如果已经显示的toast 相似位置，相似别名，更新配置，而不是取消再重新显示
            toastFactory.applyNewConfig(
                context,
                currentToast!!.view(),
                newConfig
            )
            currentToast?.updateConfig(newConfig)
            LogUtils.d("just update toast config info:$currentToast.")
            return
        }

        // 当位置 和 签名都不相同时，才会走下面的逻辑
        // 隐藏上一个 Toast
        if (isShowing) {
            currentToast?.cancel()
            LogUtils.d("showing toast was cancel and continue.")
        }

        toastFactory.produceToast(context, newConfig)?.apply {
            currentToast?.removeVisibilityObserver(ToastScheduler)
            setVisibilityObserver(ToastScheduler)
            currentToast = this
            LogUtils.d("create new toast and show it:$currentToast.")
        }?.let { compatToast ->
            if (newConfig.boundPageId.isEmpty() || currentToast !is DialogToast) {
                compatToast.show()
                return
            }
            LogUtils.d("suppress toast temporarily because of bound id (${newConfig.boundPageId}:$currentToast).")
        }
    }

    @JvmStatic
    @Synchronized
    fun schedule(boundPageId: String) {
        currentToast?.let {
            if (boundPageId == it.config().boundPageId && it is DialogToast && !it.isShowing()) {
                it.show()
                LogUtils.d("show bounded toast($boundPageId:$it).")
            }
        }
    }

    @Synchronized
    override fun onToastVisibilityChanged(view: View, visible: Boolean) {
        if (!visible && view == currentToast?.view()) {
            LogUtils.d("release current toast because of natural dismiss:$currentToast.")
            currentToast = null
        }
    }

    fun dismiss() {
        currentToast?.cancel()
    }

    fun isShowing() = currentToast?.isShowing() == true


}
