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

import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.annotation.ColorInt

/** 加粗 */
fun CharSequence.toBold(): CharSequence {
    val spannableString = SpannableString(this)
    spannableString.setSpan(
        StyleSpan(Typeface.BOLD),
        0,
        this.length,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return spannableString
}

/** 设置前景色 */
fun CharSequence.toColor(@ColorInt color: Int): CharSequence {
    val spannableString = SpannableString(this)
    spannableString.setSpan(
        ForegroundColorSpan(color),
        0,
        this.length,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return spannableString
}
