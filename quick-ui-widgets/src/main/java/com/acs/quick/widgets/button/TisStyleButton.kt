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
import com.acs.quick.res.R.color
import com.acs.quick.widgets.R

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.util.TypedValue
import androidx.annotation.ColorRes
import androidx.core.content.res.ResourcesCompat
import com.blankj.utilcode.util.ConvertUtils
import android.R as android

/**
 * author： 马世鹏
 *
 * time: 2022/4/18
 *
 * desc: Tis 风格按钮，分为多种风格：
 *
 * 1. primary 主要
 *
 * 2. secondary 次要
 *
 * 3. danger 危险
 *
 * 4. ghost 幽灵
 *
 * **如果需要自定义风格的按钮,请使用(SuperButton)[com.acs.quick.widgets.button.TisSuperButton]**
 */
class TisStyleButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    style: ButtonStyle? = null,
) : TisSuperButton(context, attrs) {

    var buttonStyle: ButtonStyle = ButtonStyle.PRIMARY
        set(value) {
            field = value
            setStyle(field)
        }

    init {
        style?.let { this.buttonStyle = it }
    }


    @SuppressLint("CustomViewStyleable")
    override fun setAttributeSet(context: Context, attrs: AttributeSet?) {
        super.setAttributeSet(context, attrs)
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TisStyleButton)
        val initButtonStyle =
            ButtonStyle.get(
                typedArray.getInt(
                    R.styleable.TisStyleButton_quick_btnStyle,
                    ButtonStyle.PRIMARY.code
                )
            )

        typedArray.recycle()
        setStyle(initButtonStyle)
    }

    /**
     * 设置样式
     */
    private fun setStyle(buttonStyle: ButtonStyle) {
        setNormalBackgroundColor(buttonStyle.btnNormalColor)
        setPressedBackgroundColor(buttonStyle.btnPressedColor)
        setDisableBackgroundColor(buttonStyle.btnDisableColor)

        setTextColor(createButtonTextColorStateList(context, buttonStyle))
        setRadiu(4)

        when (buttonStyle) {
            ButtonStyle.SECONDARY, ButtonStyle.GHOST -> {
                setStroke(1, color.quick_M7_E6E8EB)
                setPadding(20f.dp2px(), 10f.dp2px(), 20f.dp2px(), 10f.dp2px())
            }

            ButtonStyle.PRIMARY, ButtonStyle.DANGER -> {
                setStroke(0, android.color.black)
                setPadding(20f.dp2px(), 10f.dp2px(), 20f.dp2px(), 10f.dp2px())
            }

            ButtonStyle.REFUSE -> {
                setStroke(1, color.quick_red5_E34950)
                setPadding(20f.dp2px(), 10f.dp2px(), 20f.dp2px(), 10f.dp2px())
            }

            else -> {}
        }
    }

    /**
     * 文字状态颜色
     * [context] 上下文
     * [buttonStyle] buttonStyle
     */
    private fun createButtonTextColorStateList(
        context: Context,
        buttonStyle: ButtonStyle,
    ): ColorStateList {

        val states = arrayOfNulls<IntArray>(3)
        val colors = IntArray(3)
        var i = 0

        // disable color
        states[i] = intArrayOf(-android.attr.state_enabled)
        colors[i] = ResourcesCompat.getColor(context.resources, buttonStyle.textDisableColor, null)
        i++

        // pressed color
        states[i] = intArrayOf(android.attr.state_pressed)
        colors[i] = ResourcesCompat.getColor(context.resources, buttonStyle.textPressedColor, null)
        i++

        // defalut color
        states[i] = IntArray(0)
        colors[i] = ResourcesCompat.getColor(context.resources, buttonStyle.textColor, null)

        return ColorStateList(states, colors)
    }

    /**
     * 大中小
     */
    @Suppress("unused")
    enum class ButtonSize {
        SMALL,
        MIDDLE,
        LARGE
    }

    enum class ButtonStyle(
        val code: Int,
        @param:ColorRes val btnNormalColor: Int,
        @param:ColorRes val btnPressedColor: Int,
        @param:ColorRes val btnDisableColor: Int,
        @param:ColorRes val textColor: Int,
        @param:ColorRes val textDisableColor: Int,
        @param:ColorRes val textPressedColor: Int,
    ) {
        PRIMARY(
            code = 1,
            btnNormalColor = color.quick_S5_247BFF,
            btnPressedColor = color.quick_S6_1459D9,
            btnDisableColor = color.quick_primary_disable,
            textColor = android.color.white,
            textDisableColor = color.quick_primary_text_disable,
            textPressedColor = android.color.white
        ),
        SECONDARY(
            code = 2,
            btnNormalColor = android.color.white,
            btnPressedColor = android.color.white,
            btnDisableColor = android.color.white,
            textColor = color.quick_M1_1D2126,
            textDisableColor = color.quick_M1_1D2126,
            textPressedColor = color.quick_M1_1D2126
        ),
        DANGER(
            code = 3,
            btnNormalColor = color.quick_red5_E34950,
            btnPressedColor = color.quick_red6_D13B45,
            btnDisableColor = color.quick_danger_disable,
            textColor = android.color.white,
            textDisableColor = android.color.white,
            textPressedColor = android.color.white
        ),
        GHOST(
            code = 4,
            btnNormalColor = android.color.transparent,
            btnPressedColor = color.quick_danger_pressed,
            btnDisableColor = android.color.transparent,
            textColor = android.color.white,
            textDisableColor = color.quick_primary_text_disable,
            textPressedColor = android.color.white
        ),
        NORMAL(
            code = 5,
            btnNormalColor = android.color.transparent,
            btnPressedColor = android.color.transparent,
            btnDisableColor = android.color.transparent,
            textColor = color.quick_S5_247BFF,
            textDisableColor = color.quick_primary_disable,
            textPressedColor = color.quick_S6_1459D9
        ),
        REFUSE(
            code = 6,
            btnNormalColor = android.color.transparent,
            btnPressedColor = color.quick_red1_FFE7E6,
            btnDisableColor = color.quick_M8_F0F2F5,
            textColor = color.quick_red5_E34950,
            textDisableColor = color.quick_M1_1D2126,
            textPressedColor = color.quick_red5_E34950
        );

        companion object {
            fun get(code: Int): ButtonStyle {
                for (value in ButtonStyle.entries) {
                    if (value.code == code) {
                        return value
                    }
                }
                return PRIMARY
            }
        }
    }
}

private fun Float.dp2px() = ConvertUtils.dp2px(this)
