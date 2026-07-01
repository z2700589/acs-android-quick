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


package com.acs.quick.res.view.divider

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/** 安全获取 itemCount，adapter 为 null 时返回 0 */
internal fun RecyclerView.itemCount(): Int {
    return this.adapter?.itemCount ?: 0
}

/** 安全获取 itemType，adapter 为 null 时返回 -1 */
internal fun RecyclerView.itemType(itemPosition: Int): Int {
    return this.adapter?.getItemViewType(itemPosition) ?: -1
}

/** 获取指定位置的 spanSize */
internal fun GridLayoutManager.spanSize(itemPosition: Int): Int {
    return this.spanSizeLookup.getSpanSize(itemPosition)
}

/** 获取指定位置在当前行/列的 spanIndex */
internal fun GridLayoutManager.spanIndex(itemPosition: Int): Int {
    return this.spanSizeLookup.getSpanIndex(itemPosition, spanCount)
}
