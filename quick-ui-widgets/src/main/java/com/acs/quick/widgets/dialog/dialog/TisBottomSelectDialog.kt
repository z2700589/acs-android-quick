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
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.acs.quick.widgets.dialog.action.ViewBottomHandlerListener
import com.acs.quick.widgets.dialog.base.TisBaseBottomSheetDialog
import com.acs.quick.widgets.dialog.base.ViewHolder
import com.acs.quick.widgets.databinding.QuickItemSelectButtonBinding
import com.acs.quick.widgets.dialog.models.TisCheckModel
import com.acs.quick.res.view.divider.RecyclerViewDivider
import android.annotation.SuppressLint
import com.acs.quick.res.R.color
import com.acs.quick.widgets.R

open class TisBottomSelectDialog<T : TisCheckModel> : TisBaseBottomSheetDialog<TisBottomSelectDialog<T>>() {

    companion object {
        @JvmStatic
        fun <T : TisCheckModel> init(fragmentManager: FragmentManager) = TisBottomSelectDialog<T>().apply {
            setFragmentManager(fragmentManager)
        }
    }

    protected var bottomSelectParams: BottomSelectParams<T> = BottomSelectParams()

    init {
        baseParams = bottomSelectParams.apply {
            layoutRes = layoutRes()
            view = layoutView()
            tag = "BottomSelectDialog"
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

    override fun layoutRes(): Int = R.layout.quick_bottom_select_dialog

    override fun layoutView(): View? = null

    override fun viewHandler(): ViewBottomHandlerListener = BottomSheetViewHandlerListener()


    fun setSelectType(type: Int): TisBottomSelectDialog<T> {
        bottomSelectParams.selectType = type
        return this
    }

    fun setDataList(list: MutableList<T>): TisBottomSelectDialog<T> {
        bottomSelectParams.dataList = list
        return this
    }

    /**
     * Set button click listener
     *
     * @param block
     * @receiver
     * @return
     */
    fun setOperateClickListener(block: (dialog: DialogFragment) -> Unit): TisBottomSelectDialog<T> {
        bottomSelectParams.operateClickListener = block
        return this
    }

    /**
     * Set cancel click listener
     *
     * @param block
     * @receiver
     * @return
     */
    fun setCloseClickListener(block: (dialog: DialogFragment) -> Unit): TisBottomSelectDialog<T> {
        bottomSelectParams.closeClickListener = block
        return this
    }

    /**
     * Set submit click listener
     *
     * @param block
     * @receiver
     * @return
     */
    fun setSubmitClickListener(block: (dialog: DialogFragment, list: MutableList<T>) -> Unit): TisBottomSelectDialog<T> {
        bottomSelectParams.submitClickListener = block
        return this
    }

    /**
     * Set single select listener
     *
     * @param block
     * @receiver
     * @return
     */
    fun setSingleSelectListener(block: (dialog: DialogFragment, data: T) -> Unit): TisBottomSelectDialog<T> {
        bottomSelectParams.singleSelectListener = block
        return this
    }

    /**
     * Set submit text
     *
     * @param submitText
     * @receiver
     * @return
     */
    fun setSubmitText(submitText: String): TisBottomSelectDialog<T> {
        bottomSelectParams.submitText = submitText
        return this
    }


    /**
     * Set operator text
     *
     * @param operatorText
     * @receiver
     * @return
     */
    fun setOperatorText(operatorText: String): TisBottomSelectDialog<T> {
        bottomSelectParams.operatorText = operatorText
        return this
    }

    /**
     * Set tetle
     *
     * @param title
     * @receiver
     * @return
     */
    fun setTitle(title: String): TisBottomSelectDialog<T> {
        bottomSelectParams.title = title
        return this
    }


    /**
     * Bottom sheet view handler listener
     *
     * @constructor Create empty Bottom sheet view handler listener
     */
    inner class BottomSheetViewHandlerListener : ViewBottomHandlerListener() {

        override fun convertView(holder: ViewHolder, dialog: TisBaseBottomSheetDialog<*>) {
            val ivClose = holder.getView<AppCompatImageView>(R.id.iv_ui_close)
            val tvTitle = holder.getView<AppCompatTextView>(R.id.tv_ui_title)
            val tvSubmit = holder.getView<AppCompatTextView>(R.id.tv_ui_submit)
            val tvOperate = holder.getView<AppCompatTextView>(R.id.tv_ui_operate)
            val rvContent = holder.getView<RecyclerView>(R.id.rv_ui_content)

            tvTitle.text = bottomSelectParams.title

            when (bottomSelectParams.selectType) {
                SelectType.SINGLE_TYPE -> {
                    tvOperate.isVisible = true
                    tvSubmit.isVisible = false
                }

                SelectType.MULTIPLE_TYPE -> {
                    tvSubmit.text = "提交"
                    tvSubmit.isVisible = true
                    tvOperate.isVisible = false
                }
            }

            RecyclerViewDivider.linear()
                .color(mContext.getColor(color.quick_M10_FFFFFF))
                .dividerSize(dp2px(4f))
                .build()
                .addTo(rvContent)
            rvContent.linear().setup {
                addType<TisCheckModel>(R.layout.quick_item_select_button)
                onBind {
                    getBinding<QuickItemSelectButtonBinding>().apply {
                        item = getModel()
                    }
                }
                onClick(R.id.root) {
                    when (bottomSelectParams.selectType) {
                        SelectType.SINGLE_TYPE -> {
                            bottomSelectParams.dataList.forEachIndexed { index, t ->
                                t.checked = index == layoutPosition
                                notifyItemChanged(index)
                                if (t.checked) {
                                    when (bottomSelectParams.singleSelectListener) {
                                        null -> dismiss()
                                        else -> bottomSelectParams.singleSelectListener!!.invoke(dialog, t)
                                    }
                                }
                            }
                        }

                        SelectType.MULTIPLE_TYPE -> {
                            val checked = getModel<TisCheckModel>().checked
                            getModel<TisCheckModel>().checked = !checked
                            notifyItemChanged(layoutPosition)
                        }
                    }
                }
            }
            rvContent.bindingAdapter.models = bottomSelectParams.dataList

            ivClose.setOnClickListener {
                when (bottomSelectParams.closeClickListener) {
                    null -> dismiss()
                    else -> bottomSelectParams.closeClickListener!!.invoke(dialog)
                }
            }

            //操作
            if (bottomSelectParams.operatorText.isNotEmpty()) {
                tvOperate.text = bottomSelectParams.operatorText
                tvOperate.setOnClickListener {
                    when (bottomSelectParams.operateClickListener) {
                        null -> dismiss()
                        else -> bottomSelectParams.operateClickListener!!.invoke(dialog)
                    }
                }
            }

            //提交按钮
            if (bottomSelectParams.submitText.isNotEmpty()) {
                tvSubmit.text = bottomSelectParams.submitText
                tvSubmit.setOnClickListener {
                    when (bottomSelectParams.submitClickListener) {
                        null -> dismiss()
                        else -> bottomSelectParams.submitClickListener!!.invoke(
                            dialog,
                            bottomSelectParams.dataList.filter { it.checked }.toMutableList()
                        )
                    }
                }
            }
        }
    }

    /**
     * Bottom sheet dialog params
     *
     * @property operateClickListener
     * @property closeClickListener
     * @constructor Create empty Bottom sheet dialog params
     */
    @SuppressLint("ParcelCreator")
    class BottomSelectParams<T : TisCheckModel>(

        var selectType: Int = SelectType.SINGLE_TYPE,

        var dataList: MutableList<T> = mutableListOf(),

        var operateClickListener: ((DialogFragment) -> Unit)? = null,

        var closeClickListener: ((DialogFragment) -> Unit)? = null,

        //多选模式下使用
        var submitClickListener: ((DialogFragment, MutableList<T>) -> Unit)? = null,

        //单选模式下使用
        var singleSelectListener: ((DialogFragment, T) -> Unit)? = null,

        var submitText: String = "",

        var operatorText: String = "",

        var title: String = "",

        ) : BaseDialogParams()


    object SelectType {
        const val SINGLE_TYPE: Int = 1
        const val MULTIPLE_TYPE: Int = 2
    }
}
