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


package com.acs.quick.common.ui.activity

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.os.IBinder
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.acs.quick.common.data.callback.onAuthExpired
import com.acs.quick.common.data.dataStore.CommonPreferencesDataStore
import com.acs.quick.common.data.state.UIState
import com.acs.quick.common.ui.SystemBarHelper
import com.acs.quick.common.ui.foldable.FoldableHelper
import com.acs.quick.common.ui.foldable.FoldableState
import com.acs.quick.common.ui.viewmodel.BaseViewModel
import com.acs.quick.common.utils.SpUtil
import com.acs.quick.widgets.dialog.dialog.QuickLoadingDialog
import com.therouter.TheRouter
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * MVVM Activity 基类，封装了沉浸式系统栏、Loading、键盘收起等通用逻辑。
 * 子类实现 [getLayoutId]、[initView]、[initData]、[initListener] 即可。
 *
 * @author Zhai Jie
 */
abstract class BaseActivity<VB : ViewDataBinding, VM : BaseViewModel> : AppCompatActivity() {

    private lateinit var manager: InputMethodManager

    protected val mBinding: VB by lazy {
        DataBindingUtil.setContentView(this@BaseActivity, getLayoutId()) as VB
    }

    protected abstract val mViewModel: VM

    private lateinit var loadingDialog: QuickLoadingDialog

    // ---- 系统栏配置（子类可覆盖） ----

    /** 状态栏图标浅色模式（true=深色图标/浅底, false=白色图标/深底） */
    protected open val lightStatusBars: Boolean = true

    /** 导航栏图标浅色模式 */
    protected open val lightNavigationBars: Boolean = true

    /** 根布局自动处理 WindowInsets */
    protected open val consumeWindowInsets: Boolean = true

    /** 消费顶部 insets，仅在 [consumeWindowInsets]=true 时生效 */
    protected open val consumeTopInsets: Boolean = true

    /** 消费底部 insets（含 IME），仅在 [consumeWindowInsets]=true 时生效 */
    protected open val consumeBottomInsets: Boolean = true

    // ---- 折叠屏 ----

    /**
     * 子类可通过覆盖此方法启用折叠屏状态监听。
     *
     * 默认关闭。子类如需监听，覆写此属性并返回一个新的 [FoldableHelper] 实例：
     * ```
     * override val foldableHelper: FoldableHelper
     *     get() = FoldableHelper(FoldableConfig(hingeSafeMarginDp = 12f))
     * ```
     *
     * 监听结果通过 [onFoldableStateChanged] 回调。
     */
    protected open val foldableHelper: FoldableHelper? = null

    // ---- 生命周期 ----

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = if (lightStatusBars) {
                SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
            } else {
                SystemBarStyle.dark(Color.TRANSPARENT)
            },
            navigationBarStyle = if (lightNavigationBars) {
                SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
            } else {
                SystemBarStyle.dark(Color.TRANSPARENT)
            }
        )
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        TheRouter.inject(this)
        Timber.tag("Kotlin").d("当前进入哪个页面：${this.javaClass.simpleName}")
        manager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        // 401 鉴权过期回调
        onAuthExpired = {
            CommonPreferencesDataStore.clear(this)
            SpUtil(this).clearUser()
        }

        applySystemBars()

        onBackPressedDispatcher.addCallback(this@BaseActivity, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Timber.tag("Kotlin").d("handleOnBackPressed: ${this.javaClass.simpleName}退出了")
                finish()
            }
        })

        initView()
        initData()
        initListener()

        // 订阅 UIState，驱动 Loading / 异常 / 空态
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.uiState.collect { state ->
                    when (state) {
                        is UIState.Loading -> showLoading()
                        is UIState.Error -> {
                            dismissLoading()
                            onUIError(state.message)
                        }
                        is UIState.Empty -> {
                            dismissLoading()
                            onUIEmpty()
                        }
                        else -> dismissLoading()
                    }
                }
            }
        }

        // 折叠屏状态监听
        applyFoldableIfNeeded()
    }

    /** 启动折叠屏监听（由 [foldableHelper] 控制开关） */
    private fun applyFoldableIfNeeded() {
        foldableHelper?.setup(this) { state ->
            onFoldableStateChanged(state)
        }
    }

    /**
     * 折叠屏状态变化时回调。
     *
     * 默认空实现。子类覆写此方法处理折叠状态：
     * ```
     * override fun onFoldableStateChanged(state: FoldableState) {
     *     when (state) {
     *         is FoldableState.Flat -> adaptLargeScreen()
     *         is FoldableState.HalfOpened -> adaptSplitScreen(state)
         *         is FoldableState.Folded -> adaptCompact()
     *         is FoldableState.None -> {}
     *     }
     * }
     * ```
     *
     * @param state  当前折叠状态
     */
    protected open fun onFoldableStateChanged(state: FoldableState) {}

    /** 应用沉浸式系统栏，在 [initView] 之前调用 */
    private fun applySystemBars() {
        configureSystemBars()
        if (consumeWindowInsets) {
            SystemBarHelper.applyInsets(
                rootView = mBinding.root,
                consumeTop = consumeTopInsets,
                consumeBottom = consumeBottomInsets
            )
        }
    }

    /** 子类可覆盖以定制系统栏，默认在 [initView] 之前调用 */
    protected open fun configureSystemBars() {}

    /** [UIState.Error] 回调，默认不处理（Toast 由 VM 层发） */
    protected open fun onUIError(message: String) {}

    /** [UIState.Empty] 回调 */
    protected open fun onUIEmpty() {}

    // ---- 抽象方法 ----

    abstract fun getLayoutId(): Int

    abstract fun initView()

    abstract fun initData()

    abstract fun initListener()

    // ---- Foldable ----

    /**
     * 同步获取当前折叠状态。
     * 仅当 [foldableHelper] 不为 null 时有效，否则返回 [FoldableState.None]。
     */
    protected fun currentFoldableState(): FoldableState {
        return foldableHelper?.currentState() ?: FoldableState.None
    }

    // ---- Loading ----

    fun showLoading() {
        if (!::loadingDialog.isInitialized) {
            loadingDialog = QuickLoadingDialog.Builder(this)
                .setCancelable(false)
                .setCancelOutside(false)
                .create()
            loadingDialog.show()
        } else {
            loadingDialog.show()
        }
    }

    fun dismissLoading() {
        if (::loadingDialog.isInitialized && loadingDialog.isShowing) {
            loadingDialog.dismiss()
        }
    }

    // ---- 触摸事件 / 键盘 ----

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (isShouldHideInput(v, ev)) {
                hideSoftInput(v!!.windowToken)
                v.clearFocus()
            }
        }
        return super.dispatchTouchEvent(ev)
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
        return false
    }

    private fun hideSoftInput(token: IBinder?) {
        if (currentFocus != null && token != null) {
            manager.hideSoftInputFromWindow(currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

}
