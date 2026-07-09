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


package com.acs.quick.sample.login

import androidx.activity.viewModels
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.acs.quick.common.config.RouteUrl
import com.acs.quick.common.ui.SystemBarHelper
import com.acs.quick.common.ui.activity.BaseActivity
import com.acs.quick.common.ui.foldable.FoldableConfig
import com.acs.quick.common.ui.foldable.FoldableHelper
import com.acs.quick.common.ui.foldable.FoldableState
import com.acs.quick.sample.ui.QuickComposeTheme
import com.acs.quick.sample.R
import com.acs.quick.sample.databinding.ActivityLoginBinding
import com.therouter.TheRouter
import com.therouter.router.Route
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * 登录页 — Compose 实现，保留蓝色渐变头部与滚动状态栏切换。
 *
 * @author Zhai Jie
 */
@AndroidEntryPoint
@Route(path = RouteUrl.Main.PAGE_LOGIN_ACTIVITY)
class LoginActivity : BaseActivity<ActivityLoginBinding, LoginViewModel>() {

    override val mViewModel: LoginViewModel by viewModels()
    private val foldableState = mutableStateOf<FoldableState>(FoldableState.None)

    override fun getLayoutId(): Int = R.layout.activity_login

    // ---- 系统栏配置 ----

    /** 深色背景 → 白色图标 */
    override val lightStatusBars: Boolean = false

    override val lightNavigationBars: Boolean = false

    /** 禁止 SystemBarHelper 干预 insets，保持登录页沉浸式和 adjustNothing 行为 */
    override val consumeWindowInsets: Boolean = false

    /** 启用折叠屏监听，让 Compose 大屏布局避开竖向铰链 */
    override val foldableHelper: FoldableHelper = FoldableHelper(
        FoldableConfig(hingeSafeMarginDp = HINGE_SAFE_MARGIN_DP)
    )

    companion object {
        /** 头部高度比例阈值 */
        private const val HEADER_HEIGHT_RATIO = 0.30f

        /** 折叠屏铰链安全边距（dp），与原 View 版一致 */
        private const val HINGE_SAFE_MARGIN_DP = 8f

        /** 小屏与大屏分界 dp 值（与 layout-w600dp 对齐） */
        private const val SMALLEST_WIDTH_LARGE_DP = 600
    }

    // ============================================================
    // 基类回调
    // ============================================================

    override fun initView() {
        mBinding.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                QuickComposeTheme {
                    LoginScreen(
                        viewModel = mViewModel,
                        foldableState = foldableState.value,
                        onCompactScroll = { scrollY ->
                            if (resources.configuration.screenWidthDp < SMALLEST_WIDTH_LARGE_DP) {
                                updateStatusBarForScroll(scrollY)
                            }
                        }
                    )
                }
            }
        }
    }

    override fun initData() {
        // 观察导航事件
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.navigateToMain.collect {
                    TheRouter.build(RouteUrl.Main.PAGE_MAIN_ACTIVITY).navigation(this@LoginActivity)
                    finish()
                }
            }
        }
    }

    override fun initListener() {}

    override fun onFoldableStateChanged(state: FoldableState) {
        foldableState.value = state
    }

    /** 根据滚动位置切换状态栏图标颜色 */
    private fun updateStatusBarForScroll(scrollY: Int) {
        val headerEndPx = (resources.displayMetrics.heightPixels * HEADER_HEIGHT_RATIO).toInt()
        val shouldBeLight = scrollY < headerEndPx
        SystemBarHelper.setLightStatusBar(this, !shouldBeLight)
    }
}
