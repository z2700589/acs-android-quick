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

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePaddingRelative
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.drake.brv.annotaion.DividerOrientation
import com.drake.brv.utils.divider
import com.drake.brv.utils.grid
import com.drake.brv.utils.setup
import com.acs.quick.widgets.button.TisStyleButton
import com.acs.quick.widgets.R
import com.acs.quick.widgets.dialog.action.ViewHandlerListener
import com.acs.quick.widgets.dialog.base.TisBaseDialog
import com.acs.quick.widgets.dialog.base.ViewHolder
import com.acs.quick.widgets.dialog.models.TisButtonStyle
import android.annotation.SuppressLint
import com.acs.quick.res.R.color
import com.acs.quick.res.R.drawable

/**
 * Tis message dialog
 *
 * @constructor Create empty Tis message dialog
 */
open class TisMessageDialog : TisBaseDialog<TisMessageDialog>() {

    companion object {

        lateinit var root: ConstraintLayout
        lateinit var layoutContent: LinearLayoutCompat
        lateinit var tvTitle: AppCompatTextView
        lateinit var tvMessage: AppCompatTextView
        lateinit var rvDialogBottom: RecyclerView
        lateinit var layoutCustomer: LinearLayoutCompat
        lateinit var scrolView: NestedScrollView

        @JvmStatic
        fun init(fragmentManager: FragmentManager) = TisMessageDialog().apply {
            setFragmentManager(fragmentManager)
        }
    }

    protected var messageParams: MessageDialogParams = MessageDialogParams()

    init {
        baseParams = messageParams.apply {
            layoutRes = layoutRes()
            view = layoutView()
            tag = "MessageDialog"
            heightScale = 0.6f
            buttonStyles = mutableListOf(
                TisButtonStyle("Cancel", color.quick_M1_1D2126),
                TisButtonStyle("Confirm")
            )
            backgroundColorRes = color.quick_M10_FFFFFF
        }
    }

    override fun layoutRes(): Int = R.layout.quick_message_dialog

    override fun layoutView(): View? = null

    override fun viewHandler(): ViewHandlerListener = MessageViewHandlerListener()

    /**
     * Set title
     *
     * @param title
     * @return
     */
    fun setTitle(title: String?): TisMessageDialog {
        messageParams.title = title
        return this
    }

    /**
     * Set title color
     *
     * @param titleColor
     * @return
     */
    fun setTitleColor(@ColorRes titleColor: Int): TisMessageDialog {
        messageParams.titleColor = titleColor
        return this
    }

    /**
     * Set message
     *
     * @param message
     * @return
     */
    fun setMessage(message: CharSequence?): TisMessageDialog {
        messageParams.message = message
        return this
    }

    /**
     * Set message color
     *
     * @param messageColor
     * @return
     */
    fun setMessageColor(@ColorRes messageColor: Int): TisMessageDialog {
        messageParams.messageColor = messageColor
        return this
    }

    /**
     * Set background color
     *
     * @param colorRes
     * @return
     */
    fun setBackgroundColor(@ColorRes colorRes: Int): TisMessageDialog {
        messageParams.backgroundColorRes = colorRes
        return this
    }

    /**
     * Set button list
     *
     * @param buttonStyles
     * @return
     */
    fun setButtonList(vararg buttonStyles: TisButtonStyle): TisMessageDialog {
        messageParams.buttonStyles = buttonStyles.toMutableList()
        return this
    }

    /**
     * Set button list
     *
     * @param buttonStyles
     * @return
     */
    fun setButtonList(buttonStyles: MutableList<TisButtonStyle>): TisMessageDialog {
        messageParams.buttonStyles = buttonStyles
        return this
    }

    /**
     * Set button click listener
     *
     * @param block
     * @receiver
     * @return
     */
    fun setButtonClickListener(block: (dialog: DialogFragment, position: Int) -> Unit): TisMessageDialog {
        messageParams.buttonClickListener = block
        return this
    }

    /**
     * Set customer view
     *
     * @param view
     * @return
     */
    fun setCustomerView(view: View): TisMessageDialog {
        messageParams.customerView = view
        return this
    }

    /**
     * Message view handler listener
     *
     * @constructor Create empty Message view handler listener
     */
    inner class MessageViewHandlerListener : ViewHandlerListener() {

        override fun convertView(holder: ViewHolder, dialog: TisBaseDialog<*>) {
            root = holder.getView(R.id.root)
            layoutContent = holder.getView(R.id.layout_content)
            tvTitle = holder.getView(R.id.tv_ui_title)
            tvMessage = holder.getView(R.id.tv_ui_message)
            rvDialogBottom = holder.getView(R.id.rv_dialog_bottom)
            layoutCustomer = holder.getView(R.id.layout_customer)
            scrolView = holder.getView(R.id.scroll_view)

            if (messageParams.cancelableOutside) {
                root.setOnClickListener { dismiss() }
            }

            if (messageParams.backgroundColorRes > 0) {
                layoutContent.setBackgroundColor(mContext.getColor(messageParams.backgroundColorRes))
            }

            tvTitle.apply {
                visibility = if (messageParams.title?.isNotEmpty() == true) View.VISIBLE else View.GONE
                text = messageParams.title
                setTextColor(mContext.getColor(messageParams.titleColor))
            }
            tvMessage.apply {
                visibility = if (messageParams.message?.isNotEmpty() == true) View.VISIBLE else View.GONE
                text = messageParams.message
                setTextColor(mContext.getColor(messageParams.messageColor))
                viewTreeObserver.addOnPreDrawListener {
                    viewTreeObserver.removeOnPreDrawListener(this)
                    gravity = (if (lineCount > 2) Gravity.START else Gravity.CENTER)
                    true
                }
            }

            layoutCustomer.apply {
                visibility = if (messageParams.customerView != null) View.VISIBLE else View.GONE
                if (messageParams.customerView != null) {
                    addView(messageParams.customerView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
                }
            }

            scrolView.apply {
                updateLayoutParams<LinearLayoutCompat.LayoutParams> {
                    topMargin = if (messageParams.title?.isNotEmpty() == true && messageParams.message?.isNotEmpty() == true) dp2px(8f) else 0
                }
            }

            messageParams.buttonStyles?.let {
                when (it.size) {
                    2 -> rvDialogBottom.grid(2).divider(drawable.quick_divider_dialog_button_vertical, DividerOrientation.VERTICAL)
                    else -> rvDialogBottom.grid(1).divider(drawable.quick_divider_dialog_button_horizontal)
                }.setup {
                    addType<TisButtonStyle>(R.layout.quick_item_dialog_button)
                    onBind {
                        val model = getModel<TisButtonStyle>()
                        findView<TisStyleButton>(R.id.tv_ui_button).apply {
                            buttonStyle = model.style
                            text = model.textName
                            textSize = model.textSize
                            setTextColor(mContext.getColor(model.textColor))
                            updatePaddingRelative(
                                if (itemCount > 2) dp2px(24f) else dp2px(16f), 0,
                                if (itemCount > 2) dp2px(24f) else dp2px(16f), 0
                            )
                        }
                    }
                    onClick(R.id.root) {
                        when (messageParams.buttonClickListener) {
                            null -> dismiss()
                            else -> messageParams.buttonClickListener!!.invoke(dialog, this.modelPosition)
                        }
                    }
                }.models = messageParams.buttonStyles
            }

        }
    }

    /**
     * Message dialog params
     *
     * @property title
     * @property titleColor
     * @property message
     * @property messageColor
     * @property buttonStyles
     * @property buttonClickListener
     * @constructor Create empty Message dialog params
     */
    @SuppressLint("ParcelCreator")
    class MessageDialogParams(
        var title: String? = "",

        var titleColor: Int = color.quick_M1_1D2126,

        var message: CharSequence? = "",

        var messageColor: Int = color.quick_M3_5E6166,

        var buttonStyles: MutableList<TisButtonStyle>? = null,

        var buttonClickListener: ((DialogFragment, Int) -> Unit)? = null,

        var customerView: View? = null,

        ) : BaseDialogParams()
}
