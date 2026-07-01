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
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.graphics.*
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt

/**
 * author: 翟杰
 *
 * time: 2022/6/7
 *
 * desc:
 */
abstract class Indicator : Drawable(), Animatable {

    private val mPaint = Paint()

    @ColorInt
    var color: Int = Color.WHITE
        get() = mPaint.color
        set(value) {
            field = value
            mPaint.color = value
        }

    init {
        mPaint.color = Color.WHITE
        mPaint.style = Paint.Style.FILL
        mPaint.isAntiAlias = true
    }

    companion object {
        private val ZERO_BOUNDS_RECT = Rect()
    }

    private val mAnimators: MutableList<ValueAnimator> = mutableListOf()
    private val mUpdateListeners = hashMapOf<ValueAnimator, AnimatorUpdateListener>()
    var drawBounds: Rect = ZERO_BOUNDS_RECT
        set(value) {
            field = value
            setBounds(value.left, value.top, value.right, value.bottom)
        }

    val width: Int
        get() = drawBounds.width()
    val height: Int
        get() = drawBounds.height()
    val centerX: Int
        get() = drawBounds.centerX()
    val centerY: Int
        get() = drawBounds.centerY()
    val exactCenterX
        get() = drawBounds.exactCenterX()
    val exactCenterY
        get() = drawBounds.exactCenterY()

    private var alpha: Int = 255

    override fun setAlpha(alpha: Int) {
        this.alpha = alpha
    }

    override fun getAlpha(): Int {
        return alpha
    }

    override fun draw(canvas: Canvas) {
        draw(canvas, mPaint)
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
    }

    @Deprecated("Deprecated in Java", ReplaceWith("PixelFormat.OPAQUE", "android.graphics.PixelFormat"))
    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }

    override fun start() {
        ensureAnimators()

        if (mAnimators.isEmpty()) {
            return
        }

        if (isStarted()) {
            return
        }

        startAnimators()
        invalidateSelf()
    }

    private fun isStarted(): Boolean {
        mAnimators.forEach {
            return it.isStarted
        }
        return false
    }

    override fun stop() {
        stopAnimators()
    }

    override fun setBounds(bounds: Rect) {
        drawBounds.set(bounds.left, bounds.top, bounds.right, bounds.bottom)
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        bounds.let {
            drawBounds.set(it.left, it.top, it.right, it.bottom)
        }
    }

    override fun isRunning(): Boolean {
        mAnimators.forEach {
            return it.isRunning
        }
        return false
    }

    abstract fun onCreateAnimators(): MutableList<ValueAnimator>

    abstract fun draw(canvas: Canvas, paint: Paint)

    fun addUpdateListener(animator: ValueAnimator, listener: AnimatorUpdateListener) {
        mUpdateListeners[animator] = listener
    }

    fun postInvalidate() {
        invalidateSelf()
    }

    private fun ensureAnimators() {
        if (mAnimators.isEmpty()) {
            mAnimators.addAll(onCreateAnimators())
        }
    }

    private fun startAnimators() {
        mAnimators.forEach {
            it.addUpdateListener(mUpdateListeners[it])
            it.start()
        }
    }

    private fun stopAnimators() {
        mAnimators.forEach {
            it.removeAllUpdateListeners()
            it.end()
        }
    }
}
