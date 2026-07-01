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


package com.acs.quick.widgets.dialog.dialog

import android.content.Context
import android.os.Bundle
import android.os.IBinder
import android.text.InputFilter
import android.text.SpannableString
import android.text.Spanned
import android.text.method.ScrollingMovementMethod
import android.text.style.ForegroundColorSpan
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.acs.quick.widgets.button.TisStyleButton
import com.acs.quick.widgets.dialog.action.ViewBottomHandlerListener
import com.acs.quick.widgets.dialog.base.TisBaseBottomSheetDialog
import com.acs.quick.widgets.dialog.base.ViewHolder
import com.acs.quick.widgets.dialog.models.TisButtonStyle
import com.acs.quick.widgets.R
import kotlinx.parcelize.Parcelize

/**
 * Tis bottom sheet dialog
 *
 * @constructor Create empty Tis bottom sheet dialog
 */
open class TisApprovalSuggestDialog : TisBaseBottomSheetDialog<TisApprovalSuggestDialog>() {

    private lateinit var root: LinearLayoutCompat
    private lateinit var ivClose: AppCompatImageView
    private lateinit var editMessage: AppCompatEditText
    private lateinit var tvLimit: AppCompatTextView
    private lateinit var btnSubmit: TisStyleButton
    private lateinit var clPush: ConstraintLayout
    private lateinit var tvPush: AppCompatTextView
    private lateinit var cbPush: AppCompatCheckBox
    private lateinit var clTips: ConstraintLayout
    private lateinit var tvTips: AppCompatTextView
    private lateinit var viewLine: View

    companion object {

        private const val MAX_REASON_INPUT_LIMIT = 200

        @JvmStatic
        fun init(fragmentManager: FragmentManager) = TisApprovalSuggestDialog().apply {
            setFragmentManager(fragmentManager)
        }
    }

    protected var approvalSuggestParams: ApprovalSuggestParams = ApprovalSuggestParams()

    init {
        baseParams = approvalSuggestParams.apply {
            layoutRes = layoutRes()
            view = layoutView()
            tag = "ApprovalSuggestDialog"
            heightScale = 0.95f
            keepHeightScale = true
            animStyle = R.style.quick_BottomAnimStyle
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?) = BottomSheetDialog(mContext, theme).apply {
        // 默认展开
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        // 禁止拖拽
        behavior.isDraggable = false
    }

    override fun layoutRes(): Int = R.layout.quick_approval_suggest_dialog

    override fun layoutView(): View? = null

    override fun viewHandler(): ViewBottomHandlerListener = BottomSheetViewHandlerListener()

    override fun initView(view: View) {
        super.initView(view)
        view.setOnTouchListener { it, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                // 获得当前«得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
                val v = requireDialog().currentFocus
                if (v != null && isShouldHideInput(v, event)) {
                    hideSoftInput(v.windowToken)
                    v.clearFocus()
                }
            }
            it.performClick()
            false
        }
    }

    /**
     * 设置字数限制的颜色
     *
     * @param count 输入计数
     */
    private fun setLimitText(count: Int) {
        val text = SpannableString(String.format("%s/%s", count, MAX_REASON_INPUT_LIMIT))
        val ctx = requireContext()
        val color = if (count == 0) {
            ContextCompat.getColor(ctx, com.acs.quick.res.R.color.quick_M6_C9CCD1)
        } else {
            ContextCompat.getColor(ctx, com.acs.quick.res.R.color.quick_M4_868A8F)
        }
        text.setSpan(ForegroundColorSpan(color), 0, count.toString().length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tvLimit.text = text
    }

    fun setButton(button: TisButtonStyle): TisApprovalSuggestDialog {
        approvalSuggestParams.button = button
        return this
    }

    /**
     * Set button click listener
     *
     * @param block
     * @receiver
     * @return
     */
    fun setSubmitClickListener(block: (dialog: DialogFragment, message: String, isChecked: Boolean) -> Unit): TisApprovalSuggestDialog {
        approvalSuggestParams.submitClickListener = block
        return this
    }

    /**
     * Set cancel click listener
     *
     * @param block
     * @receiver
     * @return
     */
    fun setCloseClickListener(block: (dialog: DialogFragment) -> Unit): TisApprovalSuggestDialog {
        approvalSuggestParams.closeClickListener = block
        return this
    }

    /**
     * Set edit hint
     * @param string
     */
    fun setEditHint(string: String): TisApprovalSuggestDialog {
        approvalSuggestParams.editHint = string
        return this
    }

    /**
     * Set push view
     */
    fun showCheck(isShow: Boolean): TisApprovalSuggestDialog {
        approvalSuggestParams.isShowCheck = isShow
        return this
    }

    /**
     * Set push
     */
    fun setCheck(isCheck: Boolean): TisApprovalSuggestDialog {
        approvalSuggestParams.isCheck = isCheck
        return this
    }

    fun setCheckText(text: String): TisApprovalSuggestDialog {
        approvalSuggestParams.checkText = text
        return this
    }

    /**
     * 显示提示
     */
    fun showTips(isShow: Boolean): TisApprovalSuggestDialog {
        approvalSuggestParams.isShowTips = isShow
        return this
    }

    /**
     * 设置提示文案
     */
    fun setTipsText(text: String): TisApprovalSuggestDialog {
        approvalSuggestParams.tipsText = text
        return this
    }


    private fun isShouldHideInput(v: View?, event: MotionEvent): Boolean {
        if (v is EditText) {
            val l = intArrayOf(0, 0)
            v.getLocationInWindow(l)
            val left = l[0]
            val top = l[1]
            val bottom = top + v.height
            val right = (left + v.width)
            return (event.rawX <= left || event.rawX >= right || event.y <= top || event.y >= bottom)
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false
    }

    private fun hideSoftInput(token: IBinder?) {
        if (requireDialog().currentFocus != null && token != null) {
            val manager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            manager.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    /**
     * Bottom sheet view handler listener
     *
     * @constructor Create empty Bottom sheet view handler listener
     */
    inner class BottomSheetViewHandlerListener : ViewBottomHandlerListener() {

        override fun convertView(holder: ViewHolder, dialog: TisBaseBottomSheetDialog<*>) {
            root = holder.getView(R.id.root)
            ivClose = holder.getView(R.id.iv_ui_close)
            editMessage = holder.getView(R.id.edit_ui_message)
            tvLimit = holder.getView(R.id.tv_ui_limit)
            btnSubmit = holder.getView(R.id.btn_ui_submit)
            clPush = holder.getView(R.id.cl_ui_push)
            viewLine = holder.getView(R.id.view_ui_line)
            cbPush = holder.getView(R.id.cb_ui_push)
            tvPush = holder.getView(R.id.tv_ui_push)
            clTips = holder.getView(R.id.cl_ui_tips)
            tvTips = holder.getView(R.id.tv_ui_tips)

            clTips.apply {
                visibility = if (approvalSuggestParams.isShowTips) View.VISIBLE else View.GONE
                tvTips.text = approvalSuggestParams.tipsText
            }

            editMessage.apply {
                hint = approvalSuggestParams.editHint
                movementMethod = ScrollingMovementMethod.getInstance()
                filters = arrayOf(InputFilter.LengthFilter(MAX_REASON_INPUT_LIMIT))
                doAfterTextChanged {
                    val mReasonInputCount = if (it.isNullOrEmpty()) 0 else it.length
                    setLimitText(mReasonInputCount)
                }
            }

            clPush.apply {
                cbPush.isChecked = approvalSuggestParams.isCheck
                tvPush.text = approvalSuggestParams.checkText
                visibility = if (approvalSuggestParams.isShowCheck) View.VISIBLE else View.GONE
                setOnClickListener { cbPush.isChecked = !cbPush.isChecked }
            }

            viewLine.apply {
                visibility = if (approvalSuggestParams.isShowCheck) View.VISIBLE else View.GONE
            }

            val closeListener = approvalSuggestParams.closeClickListener
            ivClose.setOnClickListener {
                when (closeListener) {
                    null -> dismiss()
                    else -> closeListener.invoke(dialog)
                }
            }

            btnSubmit.apply {
                enableShadow = false
                text = approvalSuggestParams.button.textName
                textSize = approvalSuggestParams.button.textSize
                setTextColor(ContextCompat.getColor(mContext, approvalSuggestParams.button.textColor))
                val submitListener = approvalSuggestParams.submitClickListener
                setOnClickListener {
                    when (submitListener) {
                        null -> dismiss()
                        else -> submitListener.invoke(dialog, editMessage.text.toString(), cbPush.isChecked)
                    }
                }
            }
        }
    }

    /**
     * Bottom sheet dialog params
     *
     * @property submitClickListener
     * @property closeClickListener
     * @property editHint 沉底
     * @constructor Create empty Bottom sheet dialog params
     */
    @Parcelize
    class ApprovalSuggestParams(
        var button: TisButtonStyle = TisButtonStyle(),

        var submitClickListener: ((DialogFragment, String, Boolean) -> Unit)? = null,

        var closeClickListener: ((DialogFragment) -> Unit)? = null,

        var editHint: String = "请填写审批意见（必填）",

        var isShowCheck: Boolean = false,

        var isCheck: Boolean = false,

        var checkText: String = "",

        var isShowTips: Boolean = false,

        var tipsText: String = "",
    ) : BaseDialogParams()
}
