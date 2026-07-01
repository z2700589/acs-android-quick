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


package com.acs.quick.widgets.toast.custom.text

import android.content.Context
import androidx.annotation.StringRes

/**
 * author： 马世鹏
 *
 * time: 2022/6/04
 *
 * desc:
 */
interface TextToastApi {

    fun show(context: Context, msg: CharSequence)
    fun show(context: Context, @StringRes msg: Int)

    fun showAtTop(context: Context, msg: CharSequence)
    fun showAtTop(context: Context, @StringRes msg: Int)

    fun showAtBottom(context: Context, msg: CharSequence)
    fun showAtBottom(context: Context, @StringRes msg: Int)

    fun showAtLocation(
        context: Context,
        msg: CharSequence,
        gravity: Int,
        xOffeseDp: Float,
        yOffsetDp: Float
    )

    fun showAtLocation(
        context: Context,
        @StringRes msg: Int,
        gravity: Int,
        xOffeseDp: Float,
        yOffsetDp: Float
    )

    fun showLong(context: Context, msg: CharSequence)
    fun showLong(context: Context, @StringRes msg: Int)

    fun showAtTopLong(context: Context, msg: CharSequence)
    fun showAtTopLong(context: Context, @StringRes msg: Int)

    fun showAtBottomLong(context: Context, msg: CharSequence)
    fun showAtBottomLong(context: Context, @StringRes msg: Int)

    fun showAtLocationLong(
        context: Context,
        msg: CharSequence,
        gravity: Int,
        xOffeseDp: Float,
        yOffsetDp: Float
    )

    fun showAtLocationLong(
        context: Context,
        @StringRes msg: Int,
        gravity: Int,
        xOffeseDp: Float,
        yOffsetDp: Float
    )
}
