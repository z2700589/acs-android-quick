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
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

/** 禁用 over scroll */
fun ViewPager2.setOverScrollModeToNever() {
    val childView: View = this.getChildAt(0)
    if (childView is RecyclerView) {
        childView.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
    }
}

/** 始终允许 over scroll */
fun ViewPager2.setOverScrollModeToAlways() {
    val childView: View = this.getChildAt(0)
    if (childView is RecyclerView) {
        childView.overScrollMode = RecyclerView.OVER_SCROLL_ALWAYS
    }
}

/** 内容溢出时才允许 over scroll */
fun ViewPager2.setOverScrollModeToIfContentScrolls() {
    val childView: View = this.getChildAt(0)
    if (childView is RecyclerView) {
        childView.overScrollMode = RecyclerView.OVER_SCROLL_IF_CONTENT_SCROLLS
    }
}
