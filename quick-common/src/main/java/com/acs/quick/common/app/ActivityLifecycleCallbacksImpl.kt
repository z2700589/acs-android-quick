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


package com.acs.quick.common.app

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.acs.quick.common.utils.ActivityStackManager
import timber.log.Timber

/**
 * Activity 生命周期回调，管理 ActivityStack + 日志。
 *
 * @author Zhai Jie
 */
class ActivityLifecycleCallbacksImpl : Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
        ActivityStackManager.addActivityToStack(activity)
        Timber.e("${activity.javaClass.simpleName} --> onActivityCreated")
    }

    override fun onActivityStarted(activity: Activity) {
        Timber.e("${activity.javaClass.simpleName} --> onActivityStarted")
    }

    override fun onActivityResumed(activity: Activity) {
        Timber.e("%s --> onActivityResumed", activity.javaClass.simpleName)
    }

    override fun onActivityPaused(activity: Activity) {
        Timber.e("%s --> onActivityPaused", activity.javaClass.simpleName)
    }

    override fun onActivityStopped(activity: Activity) {
        Timber.e("%s --> onActivityStopped", activity.javaClass.simpleName)
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        Timber.e("%s --> onActivitySaveInstanceState", activity.javaClass.simpleName)
    }

    override fun onActivityDestroyed(activity: Activity) {
        ActivityStackManager.popActivityToStack(activity)
        Timber.e("%s --> onActivityDestroyed", activity.javaClass.simpleName)
    }

    companion object {
        private val TAG = "ActivityLifecycle"
    }
}
