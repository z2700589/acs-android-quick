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


package com.acs.quick.widgets.dialog.base

import android.content.Context
import android.content.DialogInterface
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Point
import android.os.Bundle
import android.os.Parcelable
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.DrawableRes
import androidx.annotation.FloatRange
import androidx.annotation.LayoutRes
import androidx.annotation.StyleRes
import androidx.core.os.BundleCompat
import androidx.fragment.app.FragmentManager
import com.acs.quick.widgets.R
import com.acs.quick.widgets.dialog.action.OnDialogDismissListener
import com.acs.quick.widgets.dialog.action.ViewBottomHandlerListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.parcelize.Parcelize

abstract class TisBaseBottomSheetDialog<T : TisBaseBottomSheetDialog<T>> : BottomSheetDialogFragment() {

    @Suppress("UNCHECKED_CAST")
    private fun self(): T = this as T

    companion object {
        private const val KEY_PARAMS = "key_params"
        private const val KEY_VIEW_HANDLER = "view_handler"
        private const val KEY_DISMISS_LISTENER = "dismiss_listener"

        fun dp2px(dpValue: Float): Int {
            val scale = Resources.getSystem().displayMetrics.density
            return (dpValue * scale + 0.5f).toInt()
        }

        fun px2dp(pxValue: Float): Int {
            val scale = Resources.getSystem().displayMetrics.density
            return (pxValue / scale + 0.5f).toInt()
        }
    }

    protected var baseParams: BaseDialogParams

    private var viewHandlerListener: ViewBottomHandlerListener? = null

    private var onDialogDismissListener: OnDialogDismissListener? = null

    /** 键盘弹出前绘制监听器，用于在 onDestroyView 中安全移除 */
    private var keyboardPreDrawListener: ViewTreeObserver.OnPreDrawListener? = null

    /** 需要弹出键盘的 EditText 引用，用于 onDestroyView 清理 */
    private var keyboardEditText: EditText? = null

    protected lateinit var mContext: Context

    init {
        this.setStyle(STYLE_NORMAL, R.style.quick_BaseDialogTheme)
        baseParams = BaseDialogParams().apply {
            layoutRes = layoutRes()
            view = layoutView()
        }
    }

    /**
     * Layout res
     *
     * @return
     */
    @LayoutRes
    protected abstract fun layoutRes(): Int

    /**
     * Layout view
     *
     * @return
     */
    protected abstract fun layoutView(): View?

    /**
     * View handler
     *
     * @return
     */
    protected abstract fun viewHandler(): ViewBottomHandlerListener?

    /**
     * Init view
     *
     * @param view
     */
    open fun initView(view: View) {}

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Restore UI status
        savedInstanceState?.let {
            baseParams = BundleCompat.getParcelable(it, KEY_PARAMS, BaseDialogParams::class.java)
                ?: BaseDialogParams()
            viewHandlerListener = BundleCompat.getParcelable(it, KEY_VIEW_HANDLER, ViewBottomHandlerListener::class.java)
            onDialogDismissListener = BundleCompat.getParcelable(it, KEY_DISMISS_LISTENER, OnDialogDismissListener::class.java)
        }

        if (viewHandlerListener == null) {
            viewHandlerListener = this.viewHandler()
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        //Clear the title of Android4.4
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return when {
            baseParams.layoutRes > 0 -> inflater.inflate(baseParams.layoutRes, container)
            baseParams.view != null -> baseParams.view!!
            else -> throw IllegalArgumentException("请先设置LayoutRes或View!")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewHandlerListener?.convertView(ViewHolder.create(view), this)
        initView(view)
        //Set open Keyboard
        if (this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT && baseParams.needKeyboardViewId != 0) {
            keyboardEditText = view.findViewById(baseParams.needKeyboardViewId)
            keyboardEditText?.let {
                keyboardPreDrawListener = object : ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        val imm = it.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager ?: return true
                        it.isFocusable = true
                        it.isFocusableInTouchMode = true
                        it.requestFocus()
                        if (imm.showSoftInput(it, 0)) {
                            it.viewTreeObserver.removeOnPreDrawListener(this)
                            keyboardPreDrawListener = null
                        }
                        return true
                    }
                }
                it.viewTreeObserver.addOnPreDrawListener(keyboardPreDrawListener!!)
            }
        }
    }

    //save UI state
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.apply {
            putParcelable(KEY_PARAMS, baseParams)
            putParcelable(KEY_VIEW_HANDLER, viewHandlerListener)
            putParcelable(KEY_DISMISS_LISTENER, onDialogDismissListener)
        }
    }

    override fun onStart() {
        super.onStart()

        //Get screen size
        val point = Point(resources.displayMetrics.widthPixels, resources.displayMetrics.heightPixels)

        //Set window
        dialog?.window?.let {
            val params = it.attributes
            params.gravity = Gravity.BOTTOM
            params.width = WindowManager.LayoutParams.MATCH_PARENT

            //Set dialog width
            when {
                baseParams.widthScale > 0f -> {
                    if ((this.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE && baseParams.keepWidthScale) || this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                        //横屏并且保持比例 或者 竖屏
                        params.width = (point.x * baseParams.widthScale).toInt()
                    }
                }

                baseParams.widthDp > 0f -> params.width = dp2px(baseParams.widthDp)
            }

            //Set dialog height
            when {
                baseParams.heightScale > 0f -> {
                    if ((this.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE && baseParams.keepHeightScale) || this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                        //横屏并且保持比例 或者 竖屏
                        params.height = (point.y * baseParams.heightScale).toInt()
                    }
                }

                baseParams.heightDp > 0f -> params.height = dp2px(baseParams.heightDp)
            }
            //Set Window verticalMargin
            params.verticalMargin = baseParams.verticalMargin

            it.attributes = params
            if (baseParams.backgroundDrawableRes == 0) {
                it.setBackgroundDrawable(null)
            } else {
                it.setBackgroundDrawableResource(baseParams.backgroundDrawableRes)
            }
            it.setWindowAnimations(baseParams.animStyle)
        }

        //Set touch cancelable
        if (!baseParams.cancelable) {
            isCancelable = baseParams.cancelable
        } else {
            dialog?.setCanceledOnTouchOutside(baseParams.cancelableOutside)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        if (baseParams.needKeyboardViewId != 0) {
            val editText = view?.findViewById<EditText>(baseParams.needKeyboardViewId)
            editText?.let {
                val imm = it.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager ?: return
                imm.hideSoftInputFromWindow(it.windowToken, 0)
            }
        }
        super.onDismiss(dialog)
        onDialogDismissListener?.onDismiss(dialog)
    }

    override fun onDestroyView() {
        // 安全移除键盘监听器，防止泄漏
        keyboardPreDrawListener?.let { listener ->
            keyboardEditText?.viewTreeObserver?.removeOnPreDrawListener(listener)
        }
        keyboardPreDrawListener = null
        keyboardEditText = null

        super.onDestroyView()
    }


    /**
     * Set fragment manager
     *
     * @param fragmentManager
     */
    protected fun setFragmentManager(fragmentManager: FragmentManager) {
        baseParams.fragmentManager = fragmentManager
    }

    /**
     * Set tag
     *
     * @param tag
     * @return
     */
    fun setTag(tag: String): T {
        baseParams.tag = tag
        return self()
    }

    /**
     * Set dismiss listener
     *
     * @param onDialogDismissListener
     * @return
     */
    fun setDismissListener(onDialogDismissListener: OnDialogDismissListener): T {
        this.onDialogDismissListener = onDialogDismissListener
        return self()
    }

    /**
     * Set width scale
     *
     * @param scale
     * @return
     */
    fun setWidthScale(@FloatRange(from = 0.0, to = 1.0) scale: Float): T {
        baseParams.widthScale = scale
        return self()
    }

    /**
     * Set width dp
     *
     * @param dp
     * @return
     */
    fun setWidthDp(dp: Float): T {
        baseParams.widthDp = dp
        return self()
    }

    /**
     * Set height scale
     *
     * @param scale
     * @return
     */
    fun setHeightScale(@FloatRange(from = 0.0, to = 1.0) scale: Float): T {
        baseParams.heightScale = scale
        return self()
    }

    /**
     * Set height dp
     *
     * @param dp
     * @return
     */
    fun setHeightDp(dp: Float): T {
        baseParams.heightDp = dp
        return self()
    }

    /**
     * Set keep width scale
     *
     * @param isKeep
     * @return
     */
    fun setKeepWidthScale(isKeep: Boolean): T {
        baseParams.keepWidthScale = isKeep
        return self()
    }

    /**
     * Set keep height scale
     *
     * @param isKeep
     * @return
     */
    fun setKeepHeightScale(isKeep: Boolean): T {
        baseParams.keepHeightScale = isKeep
        return self()
    }

    /**
     * Set vertical margin
     *
     * @param verticalMargin
     * @return
     */
    fun setVerticalMargin(@FloatRange(from = 0.0, to = 0.1) verticalMargin: Float): T {
        baseParams.verticalMargin = verticalMargin
        return self()
    }


    /**
     * Set cancelable all
     *
     * @param cancelable
     * @return
     */
    fun setCancelableAll(cancelable: Boolean): T {
        baseParams.cancelable = cancelable
        return self()
    }


    /**
     * Set cancelable outside
     *
     * @param cancelableOutside
     * @return
     */
    fun setCancelableOutside(cancelableOutside: Boolean): T {
        baseParams.cancelableOutside = cancelableOutside
        return self()
    }

    /**
     * Set background drawable res
     *
     * @param resId
     * @return
     */
    fun setBackgroundDrawableRes(@DrawableRes resId: Int): T {
        baseParams.backgroundDrawableRes = resId
        return self()
    }

    /**
     * Set anim style
     *
     * @param animStyleRes
     * @return
     */
    fun setAnimStyle(@StyleRes animStyleRes: Int): T {
        baseParams.animStyle = animStyleRes
        return self()
    }

    /**
     * Set need keyboard edit text id
     *
     * @param id
     * @return
     */
    fun setNeedKeyboardEditTextId(id: Int): T {
        baseParams.needKeyboardViewId = id
        return self()
    }

    /**
     * Show
     *
     * @return
     */
    fun show(): T {
        baseParams.fragmentManager?.let { show(it, baseParams.tag) }
        return self()
    }

    /**
     * Un parcelable params
     *
     * @property fragmentManager
     * @property view
     * @constructor Create empty Un parcelable params
     */
    abstract class UnParcelableParams(
        var fragmentManager: FragmentManager? = null,
        var view: View? = null,
    )

    /**
     * Base dialog params
     *
     * @property layoutRes
     * @property widthScale
     * @property widthDp
     * @property heightScale
     * @property heightDp
     * @property keepWidthScale
     * @property keepHeightScale
     * @property verticalMargin
     * @property tag
     * @property cancelable
     * @property cancelableOutside
     * @property backgroundColorRes
     * @property backgroundDrawableRes
     * @property animStyle
     * @property needKeyboardViewId
     * @constructor Create empty Base dialog params
     */
    @Parcelize
    open class BaseDialogParams(
        @LayoutRes var layoutRes: Int = 0,

        var widthScale: Float = 0f,
        var widthDp: Float = 0f,

        var heightScale: Float = 0f,
        var heightDp: Float = 0f,

        var keepWidthScale: Boolean = false,
        var keepHeightScale: Boolean = false,

        var verticalMargin: Float = 0f,

        var tag: String = "TisDialog",

        var cancelable: Boolean = true,
        var cancelableOutside: Boolean = true,

        var backgroundColorRes: Int = 0,

        var backgroundDrawableRes: Int = 0,

        var animStyle: Int = R.style.quick_BottomAnimStyle,

        var needKeyboardViewId: Int = 0,

        ) : UnParcelableParams(), Parcelable
}
