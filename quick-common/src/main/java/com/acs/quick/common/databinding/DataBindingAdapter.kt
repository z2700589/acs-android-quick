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


package com.acs.quick.common.databinding


import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContextWrapper
import android.graphics.Paint
import android.util.TypedValue
import android.view.View
import android.view.View.NO_ID
import android.webkit.WebView
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.BindingMethod
import androidx.databinding.BindingMethods
import com.acs.quick.common.ktx.clickDelay
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.core.graphics.toColorInt

@BindingMethods(
    BindingMethod(type = View::class, attribute = "android:enabled", method = "enabled"),
    BindingMethod(type = View::class, attribute = "android:selected", method = "selected"),
    BindingMethod(type = View::class, attribute = "android:activated", method = "activated"),
)
object DataBindingComponent {

    @BindingAdapter("paddingStart", "paddingEnd", requireAll = false)
    @JvmStatic
    fun setPaddingHorizontal(v: View, start: View?, end: View?) {
        v.post {
            val startFinal = (start?.width ?: 0) + v.paddingStart
            val endFinal = (end?.width ?: 0) + v.paddingEnd
            v.setPaddingRelative(startFinal, v.paddingTop, endFinal, v.paddingBottom)
        }
    }

    @BindingAdapter(
        value = ["leftDrawable", "topDrawable", "rightDrawable", "bottomDrawable"],
        requireAll = false
    )
    @JvmStatic
    fun setImageDrawable(
        v: TextView,
        leftDrawable: Int, topDrawable: Int, rightDrawable: Int, bottomDrawable: Int
    ) {
        v.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, topDrawable, rightDrawable, bottomDrawable)
    }

    @BindingAdapter(
        value = ["startDrawable", "topDrawable", "endDrawable", "bottomDrawable"],
        requireAll = false
    )
    @JvmStatic
    fun setImageDrawableRelative(
        v: TextView,
        startDrawable: Int, topDrawable: Int, endDrawable: Int, bottomDrawable: Int
    ) {
        v.setCompoundDrawablesRelativeWithIntrinsicBounds(startDrawable, topDrawable, endDrawable, bottomDrawable)
    }

    @BindingAdapter("android:background")
    @JvmStatic
    fun setBackgroundRes(v: View, drawableId: Int) {
        if (drawableId > NO_ID) v.setBackgroundResource(drawableId) else v.background = null
    }

    @SuppressLint("ResourceType")
    @BindingAdapter("android:src")
    @JvmStatic
    fun setImageDrawable(v: ImageView, @DrawableRes drawableId: Int) {
        if (drawableId > NO_ID) v.setImageResource(drawableId) else v.setImageDrawable(null)
    }

    /** 不可见（保留占位），true=VISIBLE, false=INVISIBLE */
    @BindingAdapter("invisible")
    @JvmStatic
    fun setVisibleOrInvisible(v: View, isVisible: Boolean) {
        v.visibility = if (isVisible) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
    }

    /** 不可见（保留占位），非 null=VISIBLE, null=INVISIBLE */
    @BindingAdapter("invisible")
    @JvmStatic
    fun setVisibleOrInvisible(v: View, isVisible: Any?) {
        v.visibility = if (isVisible != null) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
    }

    /** 消失（不占位），true=VISIBLE, false=GONE */
    @BindingAdapter("gone")
    @JvmStatic
    fun setVisibleOrGone(v: View, isVisible: Boolean) {
        v.visibility = if (isVisible) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    /** 消失（不占位），非 null=VISIBLE, null=GONE */
    @BindingAdapter("gone")
    @JvmStatic
    fun setVisibleOrGone(v: View, isVisible: Any?) {
        v.visibility = if (isVisible != null) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }


    @BindingAdapter("android:elevation")
    @JvmStatic
    fun setElevation(v: View, dp: Int) {
        ViewCompat.setElevation(v, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), v.resources.displayMetrics))
    }

    @BindingAdapter("android:elevation")
    @JvmStatic
    fun setElevation(v: CardView, dp: Int) {
        v.cardElevation = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), v.resources.displayMetrics)
    }

    @BindingAdapter("android:enabled")
    @JvmStatic
    fun setEnabled(v: View, enable: Any?) {
        v.isEnabled = enable != null
    }

    @BindingAdapter("selected")
    @JvmStatic
    fun setSelected(v: View, selected: Any?) {
        v.isSelected = selected != null
    }

    @BindingAdapter("activated")
    @JvmStatic
    fun setActivated(v: View, activated: Any?) {
        v.isActivated = activated != null
    }

    /** 防快速点击 */
    @SuppressLint("CheckResult")
    @BindingAdapter("click")
    @JvmStatic
    fun setThrottleClickListener(v: View, onClickListener: View.OnClickListener?) {
        if (onClickListener != null) {
            v.clickDelay { onClickListener.onClick(v) }
        }
    }


    /** 自动将点击事件映射到 Activity，throttle 控制是否防抖 */
    @SuppressLint("CheckResult")
    @BindingAdapter("hit")
    @JvmStatic
    fun hit(v: View, throttle: Boolean = true) {
        var context = v.context
        while (context is ContextWrapper) {
            if (context is View.OnClickListener) {
                val clickListener = context as View.OnClickListener
                if (throttle) {
                    v.clickDelay { clickListener.onClick(v) }
                } else {
                    v.setOnClickListener(clickListener)
                }
            }
            context = context.baseContext
        }
    }


    /** 关闭当前 Activity */
    @SuppressLint("CheckResult")
    @BindingAdapter("finish")
    @JvmStatic
    fun finishActivity(v: View, enabled: Boolean = true) {
        if (enabled) {
            var temp = v.context
            var activity: Activity? = null

            while (temp is ContextWrapper) {
                if (temp is Activity) {
                    activity = temp
                }
                temp = temp.baseContext
            }

            val finalActivity = activity

            v.clickDelay {
                finalActivity!!.finishAfterTransition()
            }
        }
    }

    /** 格式化 RMB（String 金额） */
    @SuppressLint("SetTextI18n")
    @BindingAdapter("rmb", "rmbUnit", requireAll = false)
    @JvmStatic
    fun formatCNY(v: TextView, number: String?, unit: String?) {
        if (!number.isNullOrEmpty() && v.text.contentEquals(number)) {
            val format = "${unit ?: "¥"}${number.format()}"
            if (format != v.text.toString()) v.text = format
        }
    }

    /** 格式化 RMB（Double 金额） */
    @SuppressLint("SetTextI18n")
    @BindingAdapter("rmb", "rmbUnit", "roundingMode", requireAll = false)
    @JvmStatic
    fun formatCNY(v: TextView, number: Double?, prefix: String?, roundingMode: RoundingMode?) {
        val numberFormat = NumberFormat.getInstance()
        numberFormat.minimumFractionDigits = 2
        numberFormat.maximumFractionDigits = 2
        numberFormat.roundingMode = roundingMode ?: RoundingMode.UP
        val format = "${prefix ?: "¥"}${numberFormat.format(number ?: 0.0)}"
        if (format != v.text.toString()) v.text = format
    }

    /** 格式化 RMB（Long 金额，默认「分」→ 除以 100） */
    @SuppressLint("SetTextI18n")
    @BindingAdapter("rmb", "rmbUnit", "roundingMode", requireAll = false)
    @JvmStatic
    fun formatCNY(v: TextView, number: Long?, prefix: String?, roundingMode: RoundingMode?) {
        val numberFormat = NumberFormat.getInstance()
        numberFormat.minimumFractionDigits = 2
        numberFormat.maximumFractionDigits = 2
        numberFormat.roundingMode = roundingMode ?: RoundingMode.UP
        val format = "${prefix ?: "¥"}${numberFormat.format(number ?: (0 / 100.0))}"
        if (format != v.text.toString()) v.text = format
    }

    /** 毫秒时间戳 → 格式化日期 */
    @BindingAdapter(value = ["dateMilli", "dateFormat"], requireAll = false)
    @JvmStatic
    fun setDateFromMillis(v: TextView, milli: Long, format: String? = "yyyy-MM-dd") {
        if (milli < 0) {
            v.text = ""
            return
        }
        val finalFormat = if (format.isNullOrBlank()) "yyyy-MM-dd" else format
        val date = Date(milli)
        val sf = SimpleDateFormat(finalFormat, Locale.CHINA)
        val formatText = sf.format(date)
        if (v.text.contentEquals(formatText)) return
        v.text = formatText
    }


    /** 毫秒时间戳（String）→ 格式化日期 */
    @BindingAdapter(value = ["dateMilli", "dateFormat"], requireAll = false)
    @JvmStatic
    fun setDateFromMillis(v: TextView, milli: String?, format: String? = "yyyy-MM-dd") {
        val finalFormat = if (format.isNullOrBlank()) "yyyy-MM-dd" else format
        val finalMilli = milli?.toLongOrNull() ?: return
        if (finalMilli < 0 || milli.isBlank()) {
            v.text = ""
            return
        }
        val date = Date(finalMilli)
        val sf = SimpleDateFormat(finalFormat, Locale.CHINA)
        val formatText = sf.format(date)
        if (v.text.contentEquals(formatText)) return
        v.text = formatText
    }

    /** 秒时间戳 → 格式化日期 */
    @BindingAdapter(value = ["dateSecond", "dateFormat"], requireAll = false)
    @JvmStatic
    fun setDateFromSecond(v: TextView, second: Long, format: String? = "yyyy-MM-dd") {
        if (second < 0) {
            v.text = ""
            return
        }
        val finalFormat = if (format.isNullOrBlank()) "yyyy-MM-dd" else format
        val date = Date(second * 1000)
        val sf = SimpleDateFormat(finalFormat, Locale.CHINA)
        val formatText = sf.format(date)
        if (v.text.contentEquals(formatText)) return
        v.text = formatText
    }

    /** 秒时间戳（String）→ 格式化日期 */
    @BindingAdapter(value = ["dateSecond", "dateFormat"], requireAll = false)
    @JvmStatic
    fun setDateFromSecond(v: TextView, second: String?, format: String? = "yyyy-MM-dd") {
        val finalFormat = if (format.isNullOrBlank()) "yyyy-MM-dd" else format
        val finalSecond = second?.toLongOrNull() ?: return
        if (finalSecond < 0 || second.isBlank()) {
            v.text = ""
            return
        }
        val date = Date(finalSecond * 1000)
        val sf = SimpleDateFormat(finalFormat, Locale.CHINA)
        val formatText = sf.format(date)
        if (v.text.contentEquals(formatText)) return
        v.text = formatText
    }

    @BindingAdapter("del")
    @JvmStatic
    fun setDel(v: TextView, isAdd: Boolean) {
        if (isAdd) {
            v.paint.flags = Paint.STRIKE_THRU_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG   // 设置中划线并加清晰
        }
    }

    @BindingAdapter("android:text")
    @JvmStatic
    fun setTextOfNumber(v: TextView, number: Int) {
        val finalText = number.toString()
        if (!v.text.contentEquals(finalText)) {
            v.text = finalText
        }
    }

    @BindingAdapter("android:text")
    @JvmStatic
    fun setTextOfNumber(v: TextView, number: Long) {
        val finalText = number.toString()
        if (!v.text.contentEquals(finalText)) {
            v.text = finalText
        }
    }

    @BindingAdapter("android:text")
    @JvmStatic
    fun setTextOfNumber(v: TextView, number: Double) {
        val finalText = BigDecimal.valueOf(number).toPlainString()
        if (!v.text.contentEquals(finalText)) {
            v.text = finalText
        }
    }

    @BindingAdapter("android:text")
    @JvmStatic
    fun setTextOfNumber(v: TextView, number: Float) {
        val finalText = BigDecimal(number.toString()).toPlainString()
        if (!v.text.contentEquals(finalText)) {
            v.text = finalText
        }
    }

    @BindingAdapter("android:textColor")
    @JvmStatic
    fun setTextColor(text: TextView, color: String) {
        text.setTextColor(color.toColorInt())
    }

    @BindingAdapter("url")
    @JvmStatic
    fun setUrl(v: WebView, url: String?) {
        if (!url.isNullOrEmpty()) {
            v.loadDataWithBaseURL(null, url, "text/html", "UTF-8", null)
        }
    }

    /**
     * 将视图传给函数处理（破坏 MVVM 解耦，不推荐）。
     * 用法: onBind="{(v)->m.bind(v)}" 或 onBind="{m::bind}"
     */
    @BindingAdapter("onBind")
    @JvmStatic
    fun setOnBindListener(v: View, listener: OnBindListener) {
        listener.onBind(v)
    }

    interface OnBindListener {
        fun onBind(v: View)
    }
}
