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

import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.annotation.ColorRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.acs.quick.widgets.button.TisStyleButton
import com.acs.quick.widgets.R
import com.acs.quick.widgets.dialog.action.ViewBottomHandlerListener
import com.acs.quick.widgets.dialog.base.TisBaseBottomSheetDialog
import com.acs.quick.widgets.dialog.base.ViewHolder
import com.acs.quick.widgets.dialog.models.TisButtonStyle
import com.acs.quick.res.R.color
import kotlinx.parcelize.Parcelize

/**
 * Tis bottom sheet dialog
 *
 * @constructor Create empty Tis bottom sheet dialog
 */
open class TisBottomSheetDialog : TisBaseBottomSheetDialog<TisBottomSheetDialog>() {

    companion object {
        @JvmStatic
        fun init(fragmentManager: FragmentManager) = TisBottomSheetDialog().apply {
            setFragmentManager(fragmentManager)
        }
    }

    protected var bottomSheetDialogParams: BottomSheetDialogParams = BottomSheetDialogParams()

    init {
        baseParams = bottomSheetDialogParams.apply {
            layoutRes = layoutRes()
            view = layoutView()
            tag = "BottomSheetDialog"
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

    override fun layoutRes(): Int = R.layout.quick_bottom_sheet_dialog

    override fun layoutView(): View? = null

    override fun viewHandler(): ViewBottomHandlerListener = BottomSheetViewHandlerListener()

    /**
     * Set message
     *
     * @param message
     * @return
     */
    fun setMessage(message: String?): TisBottomSheetDialog {
        bottomSheetDialogParams.message = message
        return this
    }

    /**
     * Set message color
     *
     * @param messageColor
     * @return
     */
    fun setMessageColor(@ColorRes messageColor: Int): TisBottomSheetDialog {
        bottomSheetDialogParams.messageColor = messageColor
        return this
    }

    fun setSubTitle(subTitle: String?): TisBottomSheetDialog {
        bottomSheetDialogParams.subTitle = subTitle
        return this
    }

    /**
     * Set cancel button
     *
     * @param cancelButton
     * @return
     */
    fun setCancelButton(cancelButton: TisButtonStyle): TisBottomSheetDialog {
        bottomSheetDialogParams.cancelButton = cancelButton
        return this
    }

    /**
     * Set button list
     *
     * @param buttonStyles
     * @return
     */
    fun setButtonList(vararg buttonStyles: TisButtonStyle): TisBottomSheetDialog {
        bottomSheetDialogParams.buttonStyles = buttonStyles.toMutableList()
        return this
    }

    /**
     * Set button list
     *
     * @param buttonStyles
     * @return
     */
    fun setButtonList(buttonStyles: MutableList<TisButtonStyle>): TisBottomSheetDialog {
        bottomSheetDialogParams.buttonStyles = buttonStyles
        return this
    }

    /**
     * Set button click listener
     *
     * @param block
     * @receiver
     * @return
     */
    fun setButtonClickListener(block: (dialog: DialogFragment, position: Int) -> Unit): TisBottomSheetDialog {
        bottomSheetDialogParams.buttonClickListener = block
        return this
    }

    /**
     * Set cancel click listener
     *
     * @param block
     * @receiver
     * @return
     */
    fun setCancelClickListener(block: (dialog: DialogFragment) -> Unit): TisBottomSheetDialog {
        bottomSheetDialogParams.cancelClickListener = block
        return this
    }

    /**
     * Bottom sheet view handler listener
     *
     * @constructor Create empty Bottom sheet view handler listener
     */
    inner class BottomSheetViewHandlerListener : ViewBottomHandlerListener() {

        override fun convertView(holder: ViewHolder, dialog: TisBaseBottomSheetDialog<*>) {
            val root = holder.getView<LinearLayoutCompat>(R.id.root)
            val tvMessage = holder.getView<AppCompatTextView>(R.id.tv_ui_message)
            val layoutSubTitle = holder.getView<ConstraintLayout>(R.id.layout_sub_title)
            val tvCancel = holder.getView<AppCompatTextView>(R.id.tv_ui_cancel)
            val rvContent = holder.getView<RecyclerView>(R.id.rv_ui_content)

            if (bottomSheetDialogParams.backgroundColorRes > 0) {
                root.setBackgroundResource(bottomSheetDialogParams.backgroundColorRes)
            }

            layoutSubTitle.apply {
                visibility = if (bottomSheetDialogParams.subTitle != null && bottomSheetDialogParams.message.isNullOrEmpty()) View.VISIBLE else View.GONE
                val tvInfo = layoutSubTitle.findViewById<AppCompatTextView>(R.id.tv_ui_info)
                tvInfo.text = bottomSheetDialogParams.subTitle
            }

            tvMessage.apply {
                visibility = if (bottomSheetDialogParams.message != null && bottomSheetDialogParams.subTitle.isNullOrEmpty()) View.VISIBLE else View.GONE
                text = bottomSheetDialogParams.message
                setTextColor(mContext.getColor(bottomSheetDialogParams.messageColor))
                viewTreeObserver.addOnPreDrawListener {
                    viewTreeObserver.removeOnPreDrawListener(this)
                    gravity = (if (lineCount > 2) Gravity.START else Gravity.CENTER)
                    true
                }
            }

            bottomSheetDialogParams.buttonStyles?.let {
                rvContent.linear().setup {
                    addType<TisButtonStyle>(R.layout.quick_item_dialog_button)
                    onBind {
                        val model = getModel<TisButtonStyle>()
                        findView<AppCompatImageView>(R.id.iv_ui_icon).apply {
                            visibility = if (model.icon != null) View.VISIBLE else View.GONE
                            model.icon?.let { setImageResource(it) }
                        }

                        findView<TisStyleButton>(R.id.tv_ui_button).apply {
                            buttonStyle = model.style
                            text = model.textName
                            textSize = model.textSize
                            setTextColor(mContext.getColor(model.textColor))
                        }
                    }
                    onClick(R.id.root) {
                        when (bottomSheetDialogParams.buttonClickListener) {
                            null -> dismiss()
                            else -> bottomSheetDialogParams.buttonClickListener!!.invoke(dialog, this.modelPosition)
                        }
                    }
                }.models = bottomSheetDialogParams.buttonStyles
            }

            tvCancel.apply {
                text = bottomSheetDialogParams.cancelButton.textName
                textSize = bottomSheetDialogParams.cancelButton.textSize
                setTextColor(mContext.getColor(bottomSheetDialogParams.cancelButton.textColor))
                setOnClickListener {
                    when (bottomSheetDialogParams.cancelClickListener) {
                        null -> dismiss()
                        else -> bottomSheetDialogParams.cancelClickListener!!.invoke(dialog)
                    }
                }
            }
        }

    }

    /**
     * Bottom sheet dialog params
     *
     * @property message
     * @property messageColor
     * @property cancelButton
     * @property buttonStyles
     * @property buttonClickListener
     * @property cancelClickListener
     * @constructor Create empty Bottom sheet dialog params
     */
    @Parcelize
    class BottomSheetDialogParams(

        var message: String? = null,

        var messageColor: Int = color.quick_M4_868A8F,

        var subTitle: String? = null,

        var cancelButton: TisButtonStyle = TisButtonStyle("取消"),

        var buttonStyles: MutableList<TisButtonStyle>? = null,

        var buttonClickListener: ((DialogFragment, Int) -> Unit)? = null,

        var cancelClickListener: ((DialogFragment) -> Unit)? = null,

        ) : BaseDialogParams()
}
