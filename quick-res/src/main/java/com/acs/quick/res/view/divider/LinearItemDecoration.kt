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

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.ArraySet
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class LinearItemDecoration internal constructor(builder: Builder) : RecyclerView.ItemDecoration() {

    private val isSpace: Boolean = builder.isSpace
    private val isHideLastDivider: Boolean = builder.isHideLastDivider
    private val divider: Drawable = builder.divider
    private val dividerSize: Int = builder.dividerSize
    private val marginStart: Int = builder.marginStart
    private val marginEnd: Int = builder.marginEnd
    private val hideDividerItemTypeSet: ArraySet<Int> = builder.hideDividerItemTypeSet
    private val hideAroundDividerItemTypeSet: ArraySet<Int> = builder.hideAroundDividerItemTypeSet

    /** 添加 ItemDecoration 到 RecyclerView */
    fun addTo(recyclerView: RecyclerView) {
        removeFrom(recyclerView)
        recyclerView.addItemDecoration(this)
    }

    /** 从 RecyclerView 移除 ItemDecoration */
    fun removeFrom(recyclerView: RecyclerView) {
        recyclerView.removeItemDecoration(this)
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val itemCount = parent.itemCount()
        if (itemCount == 0) {
            return
        }

        val itemPosition = parent.getChildAdapterPosition(view)
        if (itemPosition == RecyclerView.NO_POSITION) {
            return
        }

        val layoutManager = parent.layoutManager as? LinearLayoutManager ?: return

        val size = calculateDividerSize(layoutManager)

        if (layoutManager.orientation == RecyclerView.VERTICAL) {
            //LinearLayoutManager vertical
            if ((isHideLastDivider && isLastItem(itemPosition, itemCount))
                || nextIsHideItemType(itemPosition, itemCount, parent) || isHideItemType(itemPosition, parent)
            ) {
                outRect.setEmpty()
            } else {
                outRect.set(0, 0, 0, size)
            }
        } else {
            //LinearLayoutManager horizontal
            if ((isHideLastDivider && isLastItem(itemPosition, itemCount))
                || nextIsHideItemType(itemPosition, itemCount, parent) || isHideItemType(itemPosition, parent)
            ) {
                outRect.setEmpty()
            } else {
                outRect.set(0, 0, size, 0)
            }
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)

        val itemCount = parent.itemCount()
        if (isSpace || itemCount == 0) {
            return
        }

        val layoutManager = parent.layoutManager as? LinearLayoutManager ?: return

        if (layoutManager.orientation == RecyclerView.VERTICAL) {
            //LinearLayoutManager vertical
            drawVertical(c, parent, layoutManager, itemCount)
        } else {
            drawHorizontal(c, parent, layoutManager, itemCount)
        }
    }

    private fun drawVertical(canvas: Canvas, parent: RecyclerView, layoutManager: LinearLayoutManager, itemCount: Int) {

        val left = parent.paddingLeft + marginStart
        val right = parent.width - parent.paddingRight - marginEnd
        val size = calculateDividerSize(layoutManager)

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val childView = parent.getChildAt(i)
            val itemPosition = parent.getChildAdapterPosition(childView)
            if (itemPosition == RecyclerView.NO_POSITION) {
                return
            }

            if ((isHideLastDivider && isLastItem(itemPosition, itemCount))
                || nextIsHideItemType(itemPosition, itemCount, parent) || isHideItemType(itemPosition, parent)
            ) {
                continue
            }

            val params = childView.layoutParams as RecyclerView.LayoutParams
            val top = childView.bottom + params.bottomMargin
            val bottom = top + size

            divider.setBounds(left, top, right, bottom)
            divider.draw(canvas)
        }
    }

    private fun drawHorizontal(canvas: Canvas, parent: RecyclerView, layoutManager: LinearLayoutManager, itemCount: Int) {

        val top = parent.paddingTop + marginStart
        val bottom = parent.height - parent.paddingBottom - marginEnd
        val size = calculateDividerSize(layoutManager)

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val childView = parent.getChildAt(i)
            val itemPosition = parent.getChildAdapterPosition(childView)
            if (itemPosition == RecyclerView.NO_POSITION) {
                return
            }

            if ((isHideLastDivider && isLastItem(itemPosition, itemCount))
                || nextIsHideItemType(itemPosition, itemCount, parent) || isHideItemType(itemPosition, parent)
            ) {
                continue
            }

            val params = childView.layoutParams as RecyclerView.LayoutParams
            val left = childView.right + params.rightMargin
            val right = left + size

            divider.setBounds(left, top, right, bottom)
            divider.draw(canvas)
        }
    }

    /** 是否最后一项 */
    private fun isLastItem(itemPosition: Int, itemCount: Int): Boolean {
        return itemCount > 0 && itemPosition == itemCount - 1
    }

    /** 下一项是否隐藏分割线 */
    private fun nextIsHideItemType(itemPosition: Int, itemCount: Int, parent: RecyclerView): Boolean {
        return if (itemPosition + 1 < itemCount) hideAroundDividerItemTypeSet.contains(parent.itemType(itemPosition + 1)) else false
    }

    /** 当前项是否隐藏分割线 */
    private fun isHideItemType(itemPosition: Int, parent: RecyclerView): Boolean {
        val itemType = parent.itemType(itemPosition)
        return hideAroundDividerItemTypeSet.contains(itemType) || hideDividerItemTypeSet.contains(itemType)
    }

    /** 计算分割线尺寸 */
    private fun calculateDividerSize(layoutManager: LinearLayoutManager): Int {
        return if (layoutManager.orientation == RecyclerView.VERTICAL) {
            if (divider is ColorDrawable) dividerSize else divider.intrinsicHeight
        } else {
            if (divider is ColorDrawable) dividerSize else divider.intrinsicWidth
        }
    }

    class Builder {

        internal var isSpace: Boolean = false
        internal var isHideLastDivider: Boolean = false
        internal var divider: Drawable = ColorDrawable(Color.TRANSPARENT)
        internal var dividerSize: Int = 0
        internal var marginStart: Int = 0
        internal var marginEnd: Int = 0
        internal val hideDividerItemTypeSet: ArraySet<Int> = ArraySet(1)
        internal val hideAroundDividerItemTypeSet: ArraySet<Int> = ArraySet(1)

        fun asSpace() = apply { isSpace = true }

        fun hideLastDivider() = apply { isHideLastDivider = true }

        fun color(@ColorInt color: Int) = apply { divider = ColorDrawable(color) }

        fun drawable(drawable: Drawable) = apply { divider = drawable }

        fun dividerSize(@Px size: Int) = apply { dividerSize = size }

        fun marginStart(@Px marginStart: Int) = apply { this.marginStart = marginStart }

        fun marginEnd(@Px marginEnd: Int) = apply { this.marginEnd = marginEnd }

        /** 隐藏指定 itemType 的分割线 */
        fun hideDividerForItemType(vararg itemTypes: Int) = apply {
            if (itemTypes.isNotEmpty()) {
                itemTypes.forEach { hideDividerItemTypeSet.add(it) }
            }
        }

        /** 隐藏指定 itemType 及其上一项的分割线 */
        fun hideAroundDividerForItemType(vararg itemTypes: Int) = apply {
            if (itemTypes.isNotEmpty()) {
                itemTypes.forEach { hideAroundDividerItemTypeSet.add(it) }
            }
        }

        fun build() = LinearItemDecoration(this)
    }
}
