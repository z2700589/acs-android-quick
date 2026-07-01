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


package com.acs.quick.widgets.button

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.animation.LinearInterpolator
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.res.ResourcesCompat
import com.acs.quick.widgets.R
import com.blankj.utilcode.util.ConvertUtils
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.withTranslation

/**
 * author： 马世鹏
 * time: 2022/4/18
 * desc:
 */
open class TisSuperButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttrs: Int = 0,
) : AppCompatButton(context, attrs, defStyleAttrs) {

    // 圆角度数, 单位DP,默认 4dp
    private var mBtnLeftTopRadiusPx: Int = 0
    private var mBtnLeftBottomRadiusPx: Int = 0
    private var mBtnRightTopRadiusPx: Int = 0
    private var mBtnRightBottomRadiusPx: Int = 0

    companion object {
        // 圆角度数, 单位DP,默认 4dp
        private const val DEFAULT_BTN_RADIUS_DP = 4
    }

    // 背景
    private val gradientDrawable: GradientDrawable by lazy { GradientDrawable() }

    /**
     * 正常颜色，设置正常颜色
     */
    @ColorInt
    private var mBtnNormalBackgroundColor: Int = Color.TRANSPARENT

    /**
     * 按下颜色，设置按下颜色
     */
    @ColorInt
    private var mBtnPressedBackgroundColor: Int = Color.TRANSPARENT

    /**
     * 按钮被禁用颜色，设置按下
     */
    @ColorInt
    private var mBtnDisableBackgroundColor: Int = Color.TRANSPARENT

    /**
     * 边框
     */
    private var mStrokeWidthPx: Int = ConvertUtils.dp2px(0f)

    /**
     * 边框颜色
     */
    @ColorInt
    private var mStrokeColor: Int = Color.WHITE

    /**
     * 是否开启 shadow
     *
     * true 开启
     *
     * fasle 关闭
     *
     * 默认关闭
     */
    var enableShadow: Boolean = false
        set(value) {
            field = value
            if (value) {
                createButtonShadowAnimator(context)
            }
        }

    /**
     * 左侧图片
     */
    var drawableLeft: Drawable? = null
        set(value) {
            value?.setBounds(0, 0, ConvertUtils.dp2px(20f), ConvertUtils.dp2px(20f))
            field = value
        }

    /**
     * 文字 和 左侧图片之间的距离，单位px
     */
    var drawableLeftPaddingPx: Int = ConvertUtils.dp2px(4f)

    var isLoading: Boolean = false

    /**
     * 文字测量后的宽度
     */
    private var measureTextWidth: Float = 0f

    private var loadingAnimator: ValueAnimator? = null

    var loadingDrawable: Drawable? = null
        set(value) {
            value?.setBounds(0, 0, ConvertUtils.dp2px(20f), ConvertUtils.dp2px(20f))
            field = value
        }


    init {
        this.setAttributeSet(context, attrs)
    }

    /**
     * 获取基础样式
     */
    protected open fun setAttributeSet(context: Context, attrs: AttributeSet? = null) {
        // 初始化 画笔
        context.withStyledAttributes(attrs, R.styleable.TisSuperButton) {
            val radiusPx =
                getDimensionPixelOffset(
                    R.styleable.TisSuperButton_quick_btnRadius,
                    DEFAULT_BTN_RADIUS_DP
                )
            radiusPx.apply {
                mBtnLeftTopRadiusPx = this
                mBtnLeftBottomRadiusPx = this
                mBtnRightTopRadiusPx = this
                mBtnRightBottomRadiusPx = this
            }
            val leftTopRadius =
                getDimensionPixelSize(
                    R.styleable.TisSuperButton_quick_btnLeftTopRadius,
                    radiusPx
                )
            val leftBottomRadius =
                getDimensionPixelSize(
                    R.styleable.TisSuperButton_quick_btnLeftBottomRadius,
                    radiusPx
                )
            val rightTopRadius =
                getDimensionPixelSize(
                    R.styleable.TisSuperButton_quick_btnRightTopRadius,
                    radiusPx
                )
            val rightBottomRadius =
                getDimensionPixelSize(
                    R.styleable.TisSuperButton_quick_btnRightBottomRadius,
                    radiusPx
                )

            mBtnLeftTopRadiusPx = ConvertUtils.dp2px(leftTopRadius.toFloat())
            mBtnLeftBottomRadiusPx = ConvertUtils.dp2px(leftBottomRadius.toFloat())
            mBtnRightTopRadiusPx = ConvertUtils.dp2px(rightTopRadius.toFloat())
            mBtnRightBottomRadiusPx = ConvertUtils.dp2px(rightBottomRadius.toFloat())

            // 获取颜色，有默认颜色
            mBtnNormalBackgroundColor = getColor(
                R.styleable.TisSuperButton_quick_btnNormalColor,
                mBtnNormalBackgroundColor
            )
            mBtnPressedBackgroundColor = getColor(
                R.styleable.TisSuperButton_quick_btnPressedColor,
                mBtnPressedBackgroundColor
            )
            mBtnDisableBackgroundColor = getColor(
                R.styleable.TisSuperButton_quick_btnDisableColor,
                mBtnDisableBackgroundColor
            )

            mStrokeWidthPx = getDimensionPixelSize(
                R.styleable.TisSuperButton_quick_btnStrokeWidth,
                mStrokeWidthPx
            )
            // 边框颜色
            mStrokeColor = getColor(R.styleable.TisSuperButton_quick_btnStrokeColor, Color.WHITE)

            // 左侧 图片
            drawableLeft = getDrawable(R.styleable.TisSuperButton_quick_drawable)
            // 图片距离文字的距离
            drawableLeftPaddingPx = getDimensionPixelOffset(R.styleable.TisSuperButton_quick_drawablePadding, drawableLeftPaddingPx)

            // 是否开启 阴影
            enableShadow = getBoolean(R.styleable.TisSuperButton_quick_enableShadow, false)

        }

        // 设置背景（包括按钮 圆角效果,背景颜色）
        val shapeDrawable = ShapeDrawable()
        shapeDrawable.shape
        gradientDrawable.let {
            it.color = updateButtonColorStateList()
            it.setStroke(
                mStrokeWidthPx,
                mStrokeColor
            )
            it.cornerRadii =
                floatArrayOf(
                    mBtnLeftTopRadiusPx.toFloat(),
                    mBtnLeftTopRadiusPx.toFloat(),
                    mBtnRightTopRadiusPx.toFloat(),
                    mBtnRightTopRadiusPx.toFloat(),
                    mBtnRightBottomRadiusPx.toFloat(),
                    mBtnRightBottomRadiusPx.toFloat(),
                    mBtnLeftBottomRadiusPx.toFloat(),
                    mBtnLeftBottomRadiusPx.toFloat()
                )
            background = it
        }
        // 默认居中
        gravity = Gravity.CENTER
        // 默认单行
        setLines(1)
        maxLines = 1
        // 默认最后省略
        ellipsize = TextUtils.TruncateAt.END
    }

    /**
     * 设置圆角
     *
     * [leftTop] 左上
     *
     * [rightTop] 右上
     *
     * [leftBottom] 左下
     *
     * [rightBottom] 右下
     *
     */
    fun setRadius(
        leftTop: Int,
        rightTop: Int,
        rightBottom: Int,
        leftBottom: Int,
    ): TisSuperButton {
        mBtnLeftTopRadiusPx = ConvertUtils.dp2px(leftTop.toFloat())
        mBtnRightTopRadiusPx = ConvertUtils.dp2px(rightTop.toFloat())
        mBtnRightBottomRadiusPx = ConvertUtils.dp2px(rightBottom.toFloat())
        mBtnLeftBottomRadiusPx = ConvertUtils.dp2px(leftBottom.toFloat())
        gradientDrawable.cornerRadii = floatArrayOf(
            mBtnLeftTopRadiusPx.toFloat(),
            mBtnLeftTopRadiusPx.toFloat(),
            mBtnRightTopRadiusPx.toFloat(),
            mBtnRightTopRadiusPx.toFloat(),
            mBtnRightBottomRadiusPx.toFloat(),
            mBtnRightBottomRadiusPx.toFloat(),
            mBtnLeftBottomRadiusPx.toFloat(),
            mBtnLeftBottomRadiusPx.toFloat()
        )
        background = gradientDrawable
        return this
    }

    /**
     * 设置圆角
     *
     * [radiu] 四个角
     */
    fun setRadiu(radiu: Int): TisSuperButton = setRadius(radiu, radiu, radiu, radiu)

    /**
     * 设置正常背景颜色
     * [colorId] 颜色id
     */
    fun setNormalBackgroundColor(@ColorRes colorId: Int): TisSuperButton {
        mBtnNormalBackgroundColor = ResourcesCompat.getColor(context.resources, colorId, null)
        gradientDrawable.color = updateButtonColorStateList()
        background = gradientDrawable
        return this
    }

    /**
     * 设置按下背景颜色
     * [colorId] 颜色id
     */
    fun setPressedBackgroundColor(@ColorRes colorId: Int): TisSuperButton {
        mBtnPressedBackgroundColor = ResourcesCompat.getColor(context.resources, colorId, null)
        gradientDrawable.color = updateButtonColorStateList()
        background = gradientDrawable
        return this
    }

    /**
     * 设置按下禁用颜色
     *
     * [colorId] 颜色id
     */
    fun setDisableBackgroundColor(@ColorRes colorId: Int): TisSuperButton {
        mBtnDisableBackgroundColor = ResourcesCompat.getColor(context.resources, colorId, null)
        gradientDrawable.color = updateButtonColorStateList()
        background = gradientDrawable
        return this
    }

    /**
     * 设置边框宽度 和 颜色
     *
     * [widthDp] 是宽度，单位dp
     *
     * [colorId] 是 颜色资源id 如：color.white
     */
    fun setStroke(widthDp: Int, @ColorRes colorId: Int): TisSuperButton {
        mStrokeWidthPx = ConvertUtils.dp2px(widthDp.toFloat())
        mStrokeColor = ResourcesCompat.getColor(context.resources, colorId, null)
        gradientDrawable.setStroke(mStrokeWidthPx, mStrokeColor)
        background = gradientDrawable
        return this
    }

    /**
     * 显示 loading
     */
    @SuppressLint("SetTextI18n")
    fun showLoading() {
        if (null != loadingAnimator && loadingAnimator?.isRunning == true) {
            loadingAnimator?.cancel()
        }
        loadingAnimator = ValueAnimator.ofFloat(0f, 3f)
        loadingAnimator?.apply {
            // 无限
            repeatCount = ValueAnimator.INFINITE
            // 持续3s
            duration = 3000
            // 插值器
            interpolator = LinearInterpolator()
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    isLoading = true
                }

                override fun onAnimationEnd(animation: Animator) {
                    isLoading = false
                    text = "loading stop"
                }

                override fun onAnimationCancel(animation: Animator) {
                    isLoading = false
                }

                override fun onAnimationRepeat(animation: Animator) {}
            })
            addUpdateListener {
                val value = it.animatedValue as Float
                text = "loading $value"
            }
            start()
        }
    }

    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        stateListAnimator
    }

    /**
     * 隐藏loading
     */
    fun hideLoading() {
        loadingAnimator?.let {
            if (it.isRunning) {
                it.cancel()
            }
        }
        loadingAnimator = null
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        // View 从窗口移除时取消动画，防止持有 View 引用导致泄漏
        loadingAnimator?.let {
            if (it.isRunning) {
                it.cancel()
            }
        }
        loadingAnimator = null
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // drawable left image
        drawableLeft?.let { drawable ->
            if (measureTextWidth != 0f) {
                canvas.withTranslation(
                    (width / 2f) - (measureTextWidth / 2f) - drawable.bounds.height() - drawableLeftPaddingPx,
                    (height / 2) - (drawable.bounds.height() / 2).toFloat()
                ) {
                    drawable.draw(canvas)
                }
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // measure button text width
        measureTextWidth = paint.measureText(text, 0, text.length)
    }

    /**
     * 按钮状态颜色
     */
    private fun updateButtonColorStateList(): ColorStateList {

        val states = arrayOfNulls<IntArray>(3)
        val colors = IntArray(3)
        var i = 0

        // disable color
        states[i] = intArrayOf(-android.R.attr.state_enabled)
        colors[i] = mBtnDisableBackgroundColor
        i++

        // pressed color
        states[i] = intArrayOf(android.R.attr.state_pressed)
        colors[i] = mBtnPressedBackgroundColor
        i++

        // defalut color
        states[i] = IntArray(0)
        colors[i] = mBtnNormalBackgroundColor

        return ColorStateList(states, colors)
    }

    protected fun createButtonShadowAnimator(context: Context) {
        val stateListAnimator = AnimatorInflater.loadStateListAnimator(context, R.animator.quick_style_btn_state_anim)
        this.stateListAnimator = stateListAnimator
    }
}
