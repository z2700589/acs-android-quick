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

import android.animation.Animator
import android.animation.IntEvaluator
import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.annotation.LayoutRes
import androidx.annotation.MainThread
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.acs.quick.common.ktx.ViewClickDelay.SPACE_TIME
import com.acs.quick.common.ktx.ViewClickDelay.hash
import com.acs.quick.common.ktx.ViewClickDelay.lastClickTime

/**
 * View 扩展方法。
 *
 * @author QuYunShuo
 */
@MainThread
inline fun <reified T : ViewDataBinding> View.binding(
    inflater: LayoutInflater,
    @LayoutRes resId: Int,
): Lazy<T> = lazy { DataBindingUtil.inflate(inflater, resId, null, false) }

@MainThread
inline fun <reified T : ViewDataBinding> ViewGroup.binding(
    inflater: LayoutInflater,
    @LayoutRes resId: Int,
): Lazy<T> = lazy { DataBindingUtil.inflate(inflater, resId, null, false) }

// ---- View 可见性 ----

/** GONE */
fun View.gone() {
    visibility = View.GONE
}

/** VISIBLE */
fun View.visible() {
    visibility = View.VISIBLE
}

/** INVISIBLE */
fun View.invisible() {
    visibility = View.INVISIBLE
}

/** 是否 VISIBLE */
val View.isVisible: Boolean
    get() {
        return visibility == View.VISIBLE
    }

/** 是否 INVISIBLE */
val View.isInvisible: Boolean
    get() {
        return visibility == View.INVISIBLE
    }

/** 是否 GONE */
val View.isGone: Boolean
    get() {
        return visibility == View.GONE
    }

// ---- View 宽高 ----

/** 设置高度 */
fun View.height(height: Int): View {
    val params = layoutParams ?: ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    params.height = height
    layoutParams = params
    return this
}

/** 设置宽度 */
fun View.width(width: Int): View {
    val params = layoutParams ?: ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    params.width = width
    layoutParams = params
    return this
}

/** 同时设置宽高 */
fun View.widthAndHeight(width: Int, height: Int): View {
    val params = layoutParams ?: ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    params.width = width
    params.height = height
    layoutParams = params
    return this
}

/** 带动画改变宽度 */
fun View.animateWidth(
    targetValue: Int, duration: Long = 400, listener: Animator.AnimatorListener? = null,
    action: ((Float) -> Unit)? = null,
): ValueAnimator? {
    var animator: ValueAnimator? = null
    post {
        animator = ValueAnimator.ofInt(width, targetValue).apply {
            addUpdateListener {
                width(it.animatedValue as Int)
                action?.invoke((it.animatedFraction))
            }
            if (listener != null) addListener(listener)
            setDuration(duration)
            start()
        }
    }
    return animator
}

/** 带动画改变高度 */
fun View.animateHeight(
    targetValue: Int,
    duration: Long = 400,
    listener: Animator.AnimatorListener? = null,
    action: ((Float) -> Unit)? = null,
): ValueAnimator? {
    var animator: ValueAnimator? = null
    post {
        animator = ValueAnimator.ofInt(height, targetValue).apply {
            addUpdateListener {
                height(it.animatedValue as Int)
                action?.invoke((it.animatedFraction))
            }
            if (listener != null) addListener(listener)
            setDuration(duration)
            start()
        }
    }
    return animator
}

/** 带动画同时改变宽高 */
fun View.animateWidthAndHeight(
    targetWidth: Int,
    targetHeight: Int,
    duration: Long = 400,
    listener: Animator.AnimatorListener? = null,
    action: ((Float) -> Unit)? = null,
): ValueAnimator? {
    var animator: ValueAnimator? = null
    post {
        val startHeight = height
        val evaluator = IntEvaluator()
        animator = ValueAnimator.ofInt(width, targetWidth).apply {
            addUpdateListener {
                widthAndHeight(
                    it.animatedValue as Int,
                    evaluator.evaluate(it.animatedFraction, startHeight, targetHeight)
                )
                action?.invoke((it.animatedFraction))
            }
            if (listener != null) addListener(listener)
            setDuration(duration)
            start()
        }
    }
    return animator
}

fun View.marginBottom(bottom: Int) {
    val marginLayoutParams = this.layoutParams as MarginLayoutParams
    marginLayoutParams.bottomMargin = bottom
    this.layoutParams = marginLayoutParams
}

fun View.marginTop(top: Int) {
    val marginLayoutParams = this.layoutParams as MarginLayoutParams
    marginLayoutParams.topMargin = top
    this.layoutParams = marginLayoutParams
}

fun View.marginLeft(left: Int) {
    val marginLayoutParams = this.layoutParams as MarginLayoutParams
    marginLayoutParams.leftMargin = left
    this.layoutParams = marginLayoutParams
}

fun View.marginRight(right: Int) {
    val marginLayoutParams = this.layoutParams as MarginLayoutParams
    marginLayoutParams.rightMargin = right
    this.layoutParams = marginLayoutParams
}

/** 获取或自动生成 View id */
fun View.getViewId(): Int {
    var id = id
    if (id == View.NO_ID) {
        id = View.generateViewId()
    }
    return id
}

object ViewClickDelay {
    var hash: Int = 0
    var lastClickTime: Long = 0
    var SPACE_TIME: Long = 1000  // 间隔时间
}

/** 防快速点击，同 View 1000ms 内只响应一次 */
infix fun View.clickDelay(clickAction: () -> Unit) {
    this.setOnClickListener {
        if (this.hashCode() != hash) {
            hash = this.hashCode()
            lastClickTime = System.currentTimeMillis()
            clickAction()
        } else {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime > SPACE_TIME) {
                lastClickTime = System.currentTimeMillis()
                clickAction()
            }
        }
    }
}
