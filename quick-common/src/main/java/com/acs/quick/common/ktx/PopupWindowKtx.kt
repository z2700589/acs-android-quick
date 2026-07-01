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

import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow

/** 测量 PopupWindow 宽高 */
fun PopupWindow.makeDropDownMeasureSpec(measureSpec: Int): Int {
    val mode =
        if (measureSpec == ViewGroup.LayoutParams.WRAP_CONTENT) View.MeasureSpec.UNSPECIFIED else View.MeasureSpec.EXACTLY
    return View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(measureSpec), mode)
}
