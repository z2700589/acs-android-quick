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


package com.acs.quick.common.ktx

import android.app.Activity
import android.content.Context
import android.content.Intent

/**
 * Context 扩展。
 *
 * @author 马世鹏
 */

/** 通过泛型启动 Activity */
inline fun <reified T : Activity> Context.startActivity() {
    val intent = Intent(this, T::class.java)
    startActivity(intent)
}

/** 通过类名启动 Activity */
fun Context.startActivityForName(className: String) {
    val intent = Intent(this, Class.forName(className))
    startActivity(intent)
}
