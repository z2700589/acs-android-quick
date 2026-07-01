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


package com.acs.quick.search.list

import android.annotation.SuppressLint
import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.toColorInt
import androidx.core.view.isGone
import com.acs.quick.res.R.color
import com.acs.quick.search.R
import com.acs.quick.search.bean.SearchModel
import com.acs.quick.search.databinding.*
import com.acs.quick.widgets.toast.QuickToastUtils
import com.blankj.utilcode.util.StringUtils
import com.drake.brv.BindingAdapter
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import java.util.regex.Pattern

@SuppressLint("NotifyDataSetChanged")
class QuickSearchList @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    private lateinit var mBinding: QuickSearchListBinding

    private var mCheckedListener: ((position: Int, checked: Boolean, list: MutableList<SearchModel>) -> Unit)? = null

    private var checkedData: MutableList<SearchModel> = mutableListOf()

    private var keyword: String? = null

    //默认最大可选数量，默认不限
    private var maxCount: Int = -1

    // 最大可选数量提示
    private var maxToastPart: String = "职务"

    // 是否显示id
    private var isShowId = false

    // 是否显示底部按钮
    private var isShowBottomView = false

    init {
        initView(context, attrs, defStyleAttr, defStyleRes)
    }

    /** 初始化视图 */
    @SuppressLint("SetTextI18n")
    fun initView(context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0, defStyleRes: Int = 0) {
        if (attrs == null) return
        context.withStyledAttributes(attrs, R.styleable.QuickSearchList, defStyleAttr, defStyleRes) {
            mBinding = QuickSearchListBinding.inflate(LayoutInflater.from(context), this@QuickSearchList, false)
                .apply {
                    rvSearchList.linear().setup {
                        singleMode = !getBoolean(R.styleable.QuickSearchList_isMultiple, false)
                        isShowId = getBoolean(R.styleable.QuickSearchList_isShowId, false)
                        isShowBottomView = getBoolean(R.styleable.QuickSearchList_isShowBottomView, true)
                        tisSearchBottomView.isGone = if (singleMode) true else !isShowBottomView

                        when (getInt(R.styleable.QuickSearchList_orientation, STYLE_HORIZONTAL)) {
                            STYLE_HORIZONTAL -> if (singleMode) {
                                addType<SearchModel>(R.layout.quick_item_horizontal_search_single)
                            } else {
                                addType<SearchModel>(R.layout.quick_item_horizontal_search)
                            }

                            STYLE_VERTICAL -> if (singleMode) {
                                addType<SearchModel>(R.layout.quick_item_vertical_search_single)
                            } else {
                                addType<SearchModel>(R.layout.quick_item_vertical_search)
                            }
                        }
                        onBind {
                            val model = getModel<SearchModel>()
                            btnSearchConfirm.text = if (getCheckedData().size > 0) "确定·${getCheckedData().size}" else "确定"
                            when (itemViewType) {
                                R.layout.quick_item_horizontal_search -> {
                                    getBinding<QuickItemHorizontalSearchBinding>().apply {
                                        // 名称
                                        tvSearchName.text = if (isShowId) {
                                            if (model.name.isNotEmpty()) {
                                                keyword?.let { getHighLightKeyWord("[${model.id}]${model.name}", it) } ?: "[${model.id}]${model.name}"
                                            } else {
                                                keyword?.let { getHighLightKeyWord("[${model.id}]${model.title}", it) } ?: "[${model.id}]${model.title}"
                                            }
                                        } else {
                                            if (model.name.isNotEmpty()) {
                                                keyword?.let { getHighLightKeyWord(model.name, it) } ?: model.name
                                            } else {
                                                keyword?.let { getHighLightKeyWord(model.title, it) } ?: model.title
                                            }
                                        }
                                        tvSearchName.setTextColor(if (model.isChecked) context.getColor(color.quick_function_info) else context.getColor(color.quick_M1_1D2126))
                                        // 描述
                                        tvSearchDesc.apply {
                                            isGone = StringUtils.isEmpty(model.desc)
                                            text = model.desc
                                        }
                                        // 选中
                                        cbSearchItem.isChecked = model.isChecked
                                        // 禁用
                                        cbSearchItem.isEnabled = !model.disable
                                    }
                                }

                                R.layout.quick_item_horizontal_search_single -> {
                                    getBinding<QuickItemHorizontalSearchSingleBinding>().apply {
                                        // 名称
                                        tvSearchName.text = if (isShowId) {
                                            if (model.name.isNotEmpty()) {
                                                keyword?.let { getHighLightKeyWord("[${model.id}]${model.name}", it) } ?: "[${model.id}]${model.name}"
                                            } else {
                                                keyword?.let { getHighLightKeyWord("[${model.id}]${model.title}", it) } ?: "[${model.id}]${model.title}"
                                            }
                                        } else {
                                            if (model.name.isNotEmpty()) {
                                                keyword?.let { getHighLightKeyWord(model.name, it) } ?: model.name
                                            } else {
                                                keyword?.let { getHighLightKeyWord(model.title, it) } ?: model.title
                                            }
                                        }
                                        tvSearchName.setTextColor(if (model.isChecked) context.getColor(color.quick_function_info) else context.getColor(color.quick_M1_1D2126))
                                        // 描述
                                        tvSearchDesc.apply {
                                            isGone = StringUtils.isEmpty(model.desc)
                                            text = model.desc
                                        }
                                        // 选中
                                        cbSearchItem.isChecked = model.isChecked
                                        // 禁用
                                        cbSearchItem.isEnabled = !model.disable
                                    }
                                }

                                R.layout.quick_item_vertical_search -> {
                                    getBinding<QuickItemVerticalSearchBinding>().apply {
                                        // 名称
                                        tvSearchName.text = if (isShowId) {
                                            if (model.name.isNotEmpty()) {
                                                keyword?.let { getHighLightKeyWord("[${model.id}]${model.name}", it) } ?: "[${model.id}]${model.name}"
                                            } else {
                                                keyword?.let { getHighLightKeyWord("[${model.id}]${model.title}", it) } ?: "[${model.id}]${model.title}"
                                            }
                                        } else {
                                            if (model.name.isNotEmpty()) {
                                                keyword?.let { getHighLightKeyWord(model.name, it) } ?: model.name
                                            } else {
                                                keyword?.let { getHighLightKeyWord(model.title, it) } ?: model.title
                                            }
                                        }
                                        tvSearchName.setTextColor(if (model.isChecked) context.getColor(color.quick_function_info) else context.getColor(color.quick_M1_1D2126))
                                        // 描述
                                        tvSearchDesc.apply {
                                            isGone = StringUtils.isEmpty(model.desc)
                                            text = model.desc
                                        }
                                        // 选中
                                        cbSearchItem.isChecked = model.isChecked
                                        // 禁用
                                        cbSearchItem.isEnabled = !model.disable
                                    }
                                }

                                R.layout.quick_item_vertical_search_single -> {
                                    getBinding<QuickItemVerticalSearchSingleBinding>().apply {
                                        // 名称
                                        tvSearchName.text = if (isShowId) {
                                            if (model.name.isNotEmpty()) {
                                                keyword?.let { getHighLightKeyWord("[${model.id}]${model.name}", it) } ?: "[${model.id}]${model.name}"
                                            } else {
                                                keyword?.let { getHighLightKeyWord("[${model.id}]${model.title}", it) } ?: "[${model.id}]${model.title}"
                                            }
                                        } else {
                                            if (model.name.isNotEmpty()) {
                                                keyword?.let { getHighLightKeyWord(model.name, it) } ?: model.name
                                            } else {
                                                keyword?.let { getHighLightKeyWord(model.title, it) } ?: model.title
                                            }
                                        }
                                        tvSearchName.setTextColor(if (model.isChecked) context.getColor(color.quick_function_info) else context.getColor(color.quick_M1_1D2126))
                                        // 描述
                                        tvSearchDesc.apply {
                                            isGone = StringUtils.isEmpty(model.desc)
                                            text = model.desc
                                        }
                                        // 选中
                                        cbSearchItem.isChecked = model.isChecked
                                        // 禁用
                                        cbSearchItem.isEnabled = !model.disable
                                    }
                                }
                            }
                        }
                        // 点击列表触发选中
                        onClick(R.id.rootView) {
                            val model = getModel<SearchModel>()
                            // disable的直接返回
                            if (model.disable) return@onClick

                            if (maxCount != -1 && !model.isChecked && checkedData.size >= maxCount) {
                                QuickToastUtils.toast().show(context, "最多选择${maxCount}个$maxToastPart")
                                return@onClick
                            }
                            if (singleMode && model.isChecked) {
                                checkedData.clear()
                                checkedData.add(model)
                                mCheckedListener?.invoke(modelPosition, model.isChecked, getCheckedData())
                            } else {
                                checkedSwitch(modelPosition)
                            }
                        }
                        // 监听列表选中
                        onChecked { position, checked, _ ->
                            val searchModel = getModel<SearchModel>(position)
                            searchModel.isChecked = checked
                            notifyItemChanged(position)
                            if (checked) {
                                if (singleMode) {
                                    checkedData.clear()
                                    checkedData.add(searchModel)
                                } else if (checkedData.none { default -> default.id == searchModel.id }) {
                                    checkedData.add(searchModel)
                                }
                            } else {
                                checkedData.removeAll { default -> default.id == searchModel.id }
                            }
                            mCheckedListener?.invoke(position, checked, getCheckedData())
                        }
                    }
                }
        }
        addView(mBinding.root)
    }


    /**
     * 设置关键字
     */
    fun setKeyword(keyword: String) {
        this.keyword = keyword
    }

    /**
     * 设置是否显示id
     */
    fun isShowId(isShowId: Boolean) {
        this.isShowId = isShowId
    }

    /**
     * 设置数据
     * @param list 列表源数据
     */
    fun setSearchData(list: MutableList<SearchModel>) {
        mBinding.rvSearchList.bindingAdapter.models = list
        mBinding.rvSearchList.smoothScrollToPosition(0)
        checkData()
    }

    /**
     * 设置默认选中数据
     * @param defaultChecked 列表源数据
     */
    fun setDefaultCheckedData(defaultChecked: MutableList<SearchModel>) {
        checkedData.clear()
        checkedData.addAll(defaultChecked)
        checkData()
    }

    /**
     * 设置默认选中数据
     * @param maxCount 列表最大可选值，默认-1，不限
     */
    fun setMaxCount(maxCount: Int) {
        this.maxCount = maxCount
    }

    /**
     * 设置超出最大限制提示字符
     * @param maxToastPart toast提示的最后一段文字
     */
    fun setMaxToastPart(maxToastPart: String) {
        this.maxToastPart = maxToastPart
    }

    /**
     * 获取选中的数据
     */
    fun getCheckedData(): MutableList<SearchModel> {
        return checkedData
    }

    /**
     * 添加数据（多个）
     */
    fun addData(list: MutableList<SearchModel>) {
        mBinding.rvSearchList.bindingAdapter.addModels(list)
        checkData()
    }

    /**
     * 添加数据（单个）
     */
    fun addData(data: SearchModel) {
        mBinding.rvSearchList.bindingAdapter.addModels(listOf(data))
        checkData()
    }

    /**
     * 手动设置选中
     */
    fun setChecked(id: String, checked: Boolean) {
        mBinding.rvSearchList.bindingAdapter.models?.forEachIndexed { index, model ->
            if (model is SearchModel && model.id == id) {
                mBinding.rvSearchList.bindingAdapter.setChecked(index, checked)
            }
        }
        if (!checked) {
            checkedData.removeAll { default -> default.id == id }
            mBinding.rvSearchList.bindingAdapter.notifyDataSetChanged()
        }
    }


    /**
     * 全选/全不选
     */
    fun checkedAll(checked: Boolean) {
        mBinding.rvSearchList.bindingAdapter.checkedAll(checked)
        if (!checked) {
            checkedData.clear()
            mBinding.rvSearchList.bindingAdapter.notifyDataSetChanged()
        }
    }

    /**
     * 检查数据
     */
    private fun checkData() {
        if (mBinding.rvSearchList.bindingAdapter.models.isNullOrEmpty()) {
            mBinding.tisSearchBottomView.isGone = if (mBinding.rvSearchList.bindingAdapter.singleMode) true else !isShowBottomView
            mBinding.stateLayout.showEmpty()
        } else {
            mBinding.rvSearchList.bindingAdapter.apply {
                models?.forEachIndexed { index, model ->
                    if (model is SearchModel) {
                        model.isChecked = checkedData.any { default -> default.id == model.id }
                        if (model.isChecked) {
                            if (singleMode) {
                                checkedPosition.add(0, index)
                            } else {
                                setChecked(index, true)
                            }
                        } else if (!singleMode) {
                            setChecked(index, false)
                        }
                    }
                }
                notifyDataSetChanged()
                mBinding.tisSearchBottomView.isGone = if (singleMode) true else !isShowBottomView
            }
            mBinding.stateLayout.showContent()
        }
    }

    /**
     * 设置空视图
     */
    fun setEmptyView(@LayoutRes int: Int) {
        mBinding.stateLayout.emptyLayout = int
    }

    /**
     * 设置重置事件
     */
    fun setOnCancelClickListener(listener: ((adapter: BindingAdapter, list: MutableList<SearchModel>) -> Unit)) {
        mBinding.btnSearchCancel.setOnClickListener {
            checkedAll(false)
            listener.invoke(mBinding.rvSearchList.bindingAdapter, getCheckedData())
        }
    }

    /**
     * 设置确认事件
     */
    fun setOnConfirmClickListener(listener: ((adapter: BindingAdapter, list: MutableList<SearchModel>) -> Unit)) {
        mBinding.btnSearchConfirm.setOnClickListener {
            listener.invoke(mBinding.rvSearchList.bindingAdapter, getCheckedData())
        }
    }

    /**
     * 设置选中监听
     */
    fun setOnCheckListener(block: (position: Int, checked: Boolean, list: MutableList<SearchModel>) -> Unit) {
        mCheckedListener = block
    }

    /**
     * 设置LoadMore事件
     */
    fun setOnLoadMoreListener(listener: ((adapter: BindingAdapter) -> Unit)) {
        mBinding.pageRefresh.setOnLoadMoreListener {
            listener.invoke(mBinding.rvSearchList.bindingAdapter)
        }
    }

    /**
     * 设置onRefresh事件
     */
    fun setOnRefreshListener(listener: ((adapter: BindingAdapter) -> Unit)) {
        mBinding.pageRefresh.setOnRefreshListener {
            listener.invoke(mBinding.rvSearchList.bindingAdapter)
        }
    }

    /**
     * 全局Loading
     */
    fun showLoading(loading: Boolean) {
        if (loading) mBinding.stateLayout.showLoading()
    }

    // pageRefreshLayout相关内置方法
    /**
     * 设置完成刷新
     */
    fun finishRefresh() {
        mBinding.pageRefresh.finishRefresh()
    }

    /**
     * 设置完成加载更多
     */
    fun finishLoadMore() {
        mBinding.pageRefresh.finishLoadMore()
    }

    /**
     * 设置没有更多数据
     * @param {Boolean} isNoMore
     */
    fun setNoMoreData(isNoMore: Boolean) {
        mBinding.pageRefresh.setNoMoreData(isNoMore)
    }

    /**
     * 设置是否允许刷新
     * @param {Boolean} isEnableRefresh
     */
    fun setEnableRefresh(isEnableRefresh: Boolean = true) {
        mBinding.pageRefresh.setEnableRefresh(isEnableRefresh)
    }

    /**
     * 设置是否允许加载更多
     * @param {Boolean} isEnableLoadMore
     */
    fun setEnableLoadMore(isEnableLoadMore: Boolean = true) {
        mBinding.pageRefresh.setEnableLoadMore(isEnableLoadMore)
    }

    /**
     * 获取高亮关键字
     * @param {String} text
     * @param {String} keyword
     */
    private fun getHighLightKeyWord(text: String, keyword: String): SpannableString {
        val s = SpannableString(text)
        val p = Pattern.compile(keyword)
        val m = p.matcher(s)
        val highlightColor = ContextCompat.getColor(context, com.acs.quick.res.R.color.quick_search_highlight)
        while (m.find()) {
            val start = m.start()
            val end = m.end()
            s.setSpan(ForegroundColorSpan(highlightColor), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        return s
    }

    companion object {

        //左右布局
        private const val STYLE_HORIZONTAL = 1

        //上下布局
        private const val STYLE_VERTICAL = 2
    }
}
