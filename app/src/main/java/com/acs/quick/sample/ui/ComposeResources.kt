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

package com.acs.quick.sample.ui

import android.util.TypedValue
import androidx.annotation.DimenRes
import androidx.annotation.AttrRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun textSizeResource(@DimenRes id: Int): TextUnit {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    return remember(id, configuration.fontScale, configuration.screenWidthDp) {
        val value = TypedValue()
        context.resources.getValue(id, value, true)
        TypedValue.complexToFloat(value.data).sp
    }
}

@Composable
fun colorAttrResource(@AttrRes id: Int): Color {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    return remember(id, configuration.uiMode) {
        val typedArray = context.theme.obtainStyledAttributes(intArrayOf(id))
        try {
            Color(typedArray.getColor(0, 0))
        } finally {
            typedArray.recycle()
        }
    }
}
