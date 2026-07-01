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


package com.acs.quick.widgets.button.loading.drawable

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Paint
import androidx.annotation.ColorInt
import androidx.core.graphics.withSave

/**
 * author: 翟杰
 *
 * time: 2022/6/7
 *
 * desc:
 */
class LoadingDrawable : Indicator() {

    companion object {
        private const val SCALE = 1.0f

        /** matches @color/quick_S5_247BFF (主题色) */
        @ColorInt
        private val COLOR_BLUE = 0xFF247BFF.toInt()

        /** matches @color/quick_cyan4_10C9C9 */
        @ColorInt
        private val COLOR_CYAN = 0xFF10C9C9.toInt()

        /** matches @color/quick_orange4_FFAA00 -- visually identical */
        @ColorInt
        private val COLOR_ORANGE = 0xFFFEAA00.toInt()
    }

    /** 预解析的颜色数组，避免每帧 draw() 中重复解析 */
    private val parsedColors = intArrayOf(COLOR_BLUE, COLOR_CYAN, COLOR_ORANGE)

    private val scaleFloats = floatArrayOf(SCALE, SCALE, SCALE)

    override fun draw(canvas: Canvas, paint: Paint) {
        val circleSpacing = 10
        val radius = (width.coerceAtMost(height) - circleSpacing * 2f) / 6f
        val x = width / 2f - (radius * 2 + circleSpacing)
        val y = height / 2

        for (i in 0..2) {
            paint.color = parsedColors[i]
            canvas.withSave {
                val translateX = x + radius * 2 * i + circleSpacing * i
                translate(translateX, y.toFloat())
                scale(scaleFloats[i], scaleFloats[i])
                drawCircle(0f, 0f, radius, paint)
            }
        }
    }

    override fun onCreateAnimators(): MutableList<ValueAnimator> =
        mutableListOf<ValueAnimator>().apply {
            val delays = intArrayOf(120, 240, 360)
            for (i in 0..2) {
                val anim = ValueAnimator.ofFloat(1f, 0.3f, 1f)
                anim.duration = 750
                anim.repeatCount = ValueAnimator.INFINITE
                anim.startDelay = delays[i].toLong()
                addUpdateListener(anim) {
                    scaleFloats[i] = it.animatedValue as Float
                    postInvalidate()
                }
                add(anim)
            }
        }
}
