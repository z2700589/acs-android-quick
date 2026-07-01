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


package com.acs.quick.widgets.toast.config

import android.widget.Toast

/**
 * author： 马世鹏
 * time: 2022/4/22
 * desc:
 */
abstract class DefaultToastConfig(override val alias: String) : ToastConfig {

    override var boundPageId: String = ""
    override var duration: Int = Toast.LENGTH_SHORT
    override lateinit var content: CharSequence
    override lateinit var location: Location
    override fun isSameLocation(config: ToastConfig?): Boolean = location == config?.location
}
