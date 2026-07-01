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

import android.graphics.Rect
import android.view.View
import androidx.annotation.Px
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * StaggeredGridLayoutManager 分割线。
 *
 * @author zyyoona7
 */
class StaggeredGridItemDecoration(builder: Builder) : RecyclerView.ItemDecoration() {

    private val spacingSize: Int = builder.spacingSize

    /** Vertical 时控制左右边距，Horizontal 时控制上下边距 */
    private val isIncludeEdge: Boolean = builder.isIncludeEdge

    /** Vertical 时控制第一行顶部边距，Horizontal 时控制第一列左侧边距 */
    private val isIncludeStartEdge: Boolean = builder.isIncludeStartEdge

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

        val layoutManager = parent.layoutManager as? StaggeredGridLayoutManager ?: return
        val params = view.layoutParams as? StaggeredGridLayoutManager.LayoutParams ?: return


        if (layoutManager.orientation == RecyclerView.VERTICAL) {
            //orientation vertical
            itemOffsetsVertical(outRect, itemPosition, layoutManager, params)
        } else {
            //orientation horizontal
            itemOffsetsHorizontal(outRect, itemPosition, layoutManager, params)
        }
    }

    private fun itemOffsetsVertical(
        outRect: Rect, itemPosition: Int, layoutManager: StaggeredGridLayoutManager,
        params: StaggeredGridLayoutManager.LayoutParams,
    ) {

        val spanCount = layoutManager.spanCount
        val spanIndex = params.spanIndex
        val isFullSpan = params.isFullSpan
        if (isIncludeStartEdge) {
            if (spanIndex == itemPosition) {
                //first row top spacing
                outRect.top = spacingSize
            }
        }

        if (isIncludeEdge) {
            when {
                isFullSpan -> {
                    outRect.left = spacingSize
                    outRect.right = spacingSize
                }
                spanIndex == 0 -> {
                    //first column
                    outRect.left = spacingSize
                    outRect.right = spacingSize / 2
                }
                (spanIndex + 1) % spanCount == 0 -> {
                    //last column
                    outRect.right = spacingSize
                    outRect.left = spacingSize / 2
                }
                else -> {
                    outRect.right = spacingSize / 2
                    outRect.left = spacingSize / 2
                }
            }
        } else {
            when {
                isFullSpan -> {
                    outRect.left = 0
                    outRect.right = 0
                }
                spanIndex == 0 -> {
                    //first column
                    outRect.left = 0
                    outRect.right = spacingSize / 2
                }
                (spanIndex + 1) % spanCount == 0 -> {
                    //last column
                    outRect.right = 0
                    outRect.left = spacingSize / 2
                }
                else -> {
                    outRect.right = spacingSize / 2
                    outRect.left = spacingSize / 2
                }
            }
        }

        //无法确定最后一行
        outRect.bottom = spacingSize
    }

    private fun itemOffsetsHorizontal(
        outRect: Rect, itemPosition: Int,
        layoutManager: StaggeredGridLayoutManager, params: StaggeredGridLayoutManager.LayoutParams,
    ) {

        val spanCount = layoutManager.spanCount
        val spanIndex = params.spanIndex
        val isFullSpan = params.isFullSpan
        if (isIncludeStartEdge) {
            if (spanIndex == itemPosition) {
                //first column left spacing
                outRect.left = spacingSize
            }
        }

        if (isIncludeEdge) {
            when {
                isFullSpan -> {
                    outRect.top = spacingSize
                    outRect.bottom = spacingSize
                }
                spanIndex == 0 -> {
                    //first row
                    outRect.top = spacingSize
                    outRect.bottom = spacingSize / 2
                }
                (spanIndex + 1) % spanCount == 0 -> {
                    //last row
                    outRect.top = spacingSize / 2
                    outRect.bottom = spacingSize
                }
                else -> {
                    outRect.top = spacingSize / 2
                    outRect.bottom = spacingSize / 2
                }
            }
        } else {
            when {
                isFullSpan -> {
                    outRect.top = 0
                    outRect.bottom = 0
                }
                spanIndex == 0 -> {
                    //first row
                    outRect.top = 0
                    outRect.bottom = spacingSize / 2
                }
                (spanIndex + 1) % spanCount == 0 -> {
                    //last row
                    outRect.top = spacingSize / 2
                    outRect.bottom = 0
                }
                else -> {
                    outRect.top = spacingSize / 2
                    outRect.bottom = spacingSize / 2
                }
            }
        }

        //无法确定最后一列
        outRect.right = spacingSize
    }

    class Builder {
        internal var spacingSize: Int = 0
        internal var isIncludeEdge: Boolean = false
        internal var isIncludeStartEdge: Boolean = false

        /**
         * orientation is Vertical,edge left right. Horizontal,edge top bottom
         */
        fun includeEdge() = apply { isIncludeEdge = true }

        /**
         * orientation is Vertical,first row top. Horizontal,first column left
         */
        fun includeStartEdge() = apply { isIncludeStartEdge = true }

        fun spacingSize(@Px size: Int) = apply { spacingSize = size }

        fun build() = StaggeredGridItemDecoration(this)
    }
}
