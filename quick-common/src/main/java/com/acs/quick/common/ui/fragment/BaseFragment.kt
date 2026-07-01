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


package com.acs.quick.common.ui.fragment

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.viewbinding.ViewBinding
import cc.taylorzhang.fragmentvisibility.VisibilityFragment
import com.acs.quick.common.ktx.binding
import com.acs.quick.common.ui.viewmodel.BaseViewModel
import com.therouter.TheRouter

/**
 * MVVM Fragment 基类，基于 VisibilityFragment 实现延迟加载。
 *
 * @author Zhai Jie
 */
abstract class BaseFragment<VB : ViewBinding, VM : BaseViewModel> : VisibilityFragment() {

    private var isFirst = true
    private val handler = Handler(Looper.getMainLooper())


    lateinit var mActivity: FragmentActivity

    protected lateinit var mBinding: VB

    protected abstract val mViewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TheRouter.inject(this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as FragmentActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        TheRouter.inject(this)
        mBinding = binding(inflater, this.getLayoutId(), container)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        isFirst = true
        initView()
        initListener()
    }

    abstract fun getLayoutId(): Int

    abstract fun initView()

    abstract fun initData()

    abstract fun initListener()

    override fun onVisibleFirst() {
        if (lifecycle.currentState == Lifecycle.State.STARTED && isFirst) {
            // 延迟 300ms 加载，避免切换动画期间渲染卡顿
            handler.postDelayed({
                this.initData()
            }, 300)
            isFirst = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
