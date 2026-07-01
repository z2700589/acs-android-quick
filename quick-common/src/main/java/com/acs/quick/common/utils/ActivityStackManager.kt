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


package com.acs.quick.common.utils

import android.app.Activity
import java.util.*

/**
 * Activity 栈管理。
 *
 * @author QuYunShuo
 */
object ActivityStackManager {

    private val activityStack by lazy { Stack<Activity>() }

    /** 入栈 */
    fun addActivityToStack(activity: Activity) {
        activityStack.push(activity)
    }

    /** 移除指定 Activity，不 finish */
    fun popActivityToStack(activity: Activity) {
        if (!activityStack.empty()) {
            activityStack.forEach {
                if (it == activity) {
                    activityStack.remove(activity)
                    return
                }
            }
        }
    }

    /** 返回上一个 Activity 并结束当前 */
    fun backToPreviousActivity() {
        if (!activityStack.empty()) {
            val activity = activityStack.pop()
            if (!activity.isFinishing) activity.finish()
        }
    }

    /** 判断当前是否是指定 Activity */
    fun isCurrentActivity(cls: Class<*>): Boolean {
        val currentActivity = getCurrentActivity()
        return if (currentActivity != null) currentActivity.javaClass == cls else false
    }

    /** 获取栈顶 Activity */
    fun getCurrentActivity(): Activity? =
        if (!activityStack.empty()) activityStack.lastElement() else null

    /** 结束指定类名的 Activity */
    fun finishActivity(cls: Class<*>) {
        activityStack.forEach {
            if (it.javaClass == cls) {
                if (!it.isFinishing) it.finish()
                return
            }
        }
    }

    /** 弹出除当前外的所有 Activity */
    fun popOtherActivity() {
        val activityList = activityStack.toList()
        getCurrentActivity()?.run {
            activityList.forEach { activity ->
                if (this != activity) {
                    activityStack.remove(activity)
                    activity.finish()
                }
            }
        }
    }

    /** 返回到指定 Activity */
    fun backToSpecifyActivity(activityClass: Class<*>) {
        val activityList = activityStack.toList().reversed()
        activityList.forEach {
            if (it.javaClass == activityClass) {
                return
            } else {
                activityStack.pop()
                it.finish()
            }
        }
    }
}
