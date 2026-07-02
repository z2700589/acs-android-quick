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

import android.text.Editable
import android.text.TextWatcher
import android.view.animation.DecelerateInterpolator
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.acs.quick.common.config.RouteUrl
import com.acs.quick.common.ui.SystemBarHelper
import com.acs.quick.common.ui.activity.BaseActivity
import com.acs.quick.common.ui.foldable.FoldableConfig
import com.acs.quick.common.ui.foldable.FoldableHelper
import com.acs.quick.common.ui.foldable.FoldableState
import com.acs.quick.sample.R
import com.acs.quick.sample.databinding.ActivityLoginBinding
import com.therouter.TheRouter
import com.therouter.router.Route
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * 登录页 — 蓝色渐变头部 + 白色状态栏图标，滚动时动态切换。
 *
 * @author Zhai Jie
 */
@AndroidEntryPoint
@Route(path = RouteUrl.Main.PAGE_LOGIN_ACTIVITY)
class LoginActivity : BaseActivity<ActivityLoginBinding, LoginViewModel>() {

    override val mViewModel: LoginViewModel by viewModels()

    override fun getLayoutId(): Int = R.layout.activity_login

    // ---- 系统栏配置 ----

    /** 深色背景 → 白色图标 */
    override val lightStatusBars: Boolean = false

    override val lightNavigationBars: Boolean = false

    /** 禁止 SystemBarHelper 干预 insets，由本页面自行接管键盘动画 */
    override val consumeWindowInsets: Boolean = false

    // ---- 折叠屏 ----

    /** 启用折叠屏监听，调整 layout-w600dp 中铰链 Guideline 位置 */
    override val foldableHelper: FoldableHelper = FoldableHelper(
        FoldableConfig(hingeSafeMarginDp = HINGE_SAFE_MARGIN_DP)
    )

    /** 滚动时动态切换状态栏图标颜色 */
    override fun configureSystemBars() {
        super.configureSystemBars()
        // 折叠屏/平板展开态：品牌区贯穿整个左半屏，状态栏始终在蓝色背景上，
        // 不需要根据滚动位置切换图标颜色（base 已设为浅色图标）
        if (resources.configuration.screenWidthDp < SMALLEST_WIDTH_LARGE_DP) {
            mBinding.root.setOnScrollChangeListener { _, _, scrollY, _, _ ->
                updateStatusBarForScroll(scrollY)
            }
        }
    }

    companion object {
        /** 头部高度比例阈值 */
        private const val HEADER_HEIGHT_RATIO = 0.30f

        /** 折叠屏铰链安全边距（dp），与 FoldableConfig 默认值一致 */
        private const val HINGE_SAFE_MARGIN_DP = 8f

        /** 小屏与大屏分界 dp 值（与 layout-w600dp 对齐） */
        private const val SMALLEST_WIDTH_LARGE_DP = 600
    }

    // ============================================================
    // 基类回调
    // ============================================================

    override fun initView() {
        mBinding.viewModel = mViewModel
        mBinding.lifecycleOwner = this
        setupImeAnimation()
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

    override fun initListener() {
        // 手机号
        mBinding.etPhone.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) mViewModel.validatePhone()
        }
        mBinding.etPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {
                mViewModel.phone.value = s?.toString() ?: ""
                mViewModel.clearPhoneError()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // 密码
        mBinding.etPassword.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) mViewModel.validatePassword()
        }
        mBinding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {
                mViewModel.password.value = s?.toString() ?: ""
                mViewModel.clearPasswordError()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // 校验错误 → TextInputLayout
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.phoneError.collect { error ->
                    mBinding.tilPhone.error = error
                    mBinding.tilPhone.isErrorEnabled = error != null
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.passwordError.collect { error ->
                    mBinding.tilPassword.error = error
                    mBinding.tilPassword.isErrorEnabled = error != null
                }
            }
        }

        // CheckBox → StateFlow
        mBinding.cbRemember.setOnCheckedChangeListener { _, isChecked ->
            mViewModel.rememberMe.value = isChecked
        }
        mBinding.cbAgreement.setOnCheckedChangeListener { _, isChecked ->
            mViewModel.agreedToTerms.value = isChecked
        }

        // 登录
        mBinding.btnLogin.setOnClickListener {
            mViewModel.login()
        }

        mBinding.tvForgotPassword.setOnClickListener {
            // TODO: 跳转忘记密码页
        }

        mBinding.tvUserAgreement.setOnClickListener {
            // TODO: 打开用户协议与隐私政策页
        }

        mBinding.tvRegister.setOnClickListener {
            // TODO: 跳转注册页
        }
    }

    // ============================================================
    // 折叠屏状态回调
    // ============================================================

    override fun onFoldableStateChanged(state: FoldableState) {
        // 只处理竖向折叠（左右分屏）
        if (state !is FoldableState.Flat && state !is FoldableState.HalfOpened) return
        if (state is FoldableState.Flat && state.orientation != FoldableState.Orientation.VERTICAL) return

        val hingeStart = mBinding.guidelineHingeStart ?: return
        val hingeEnd = mBinding.guidelineHingeEnd ?: return

        val screenWidth = window.decorView.width
        if (screenWidth == 0) return

        val density = resources.displayMetrics.density
        val safeMarginPx = (HINGE_SAFE_MARGIN_DP * density).toInt()

        val bounds = when (state) {
            is FoldableState.Flat -> state.hingeBounds ?: return
            is FoldableState.HalfOpened -> state.hingeBounds
            else -> return
        }

        val startPercent = ((bounds.left - safeMarginPx).toFloat() / screenWidth).coerceIn(0.2f, 0.8f)
        val endPercent = ((bounds.right + safeMarginPx).toFloat() / screenWidth).coerceIn(0.2f, 0.8f)

        (hingeStart.layoutParams as? ConstraintLayout.LayoutParams)?.apply {
            guidePercent = startPercent
            hingeStart.layoutParams = this
        }
        (hingeEnd.layoutParams as? ConstraintLayout.LayoutParams)?.apply {
            guidePercent = endPercent
            hingeEnd.layoutParams = this
        }
    }

    // ============================================================
    // 键盘动画
    // ============================================================

    /** 根据滚动位置切换状态栏图标颜色 */
    private fun updateStatusBarForScroll(scrollY: Int) {
        val headerEndPx = (resources.displayMetrics.heightPixels * HEADER_HEIGHT_RATIO).toInt()
        val shouldBeLight = scrollY < headerEndPx
        SystemBarHelper.setLightStatusBar(this, !shouldBeLight)
    }

    /**
     * 通过 [ViewCompat.setOnApplyWindowInsetsListener] 监听 IME insets 变化，
     * 以 [translationY] 平滑上移/复位 NestedScrollView，避免 window resize 导致的
     * 百分比 Guideline 重新计算和 [SystemBarHelper] 底部 padding 突变。
     */
    private fun setupImeAnimation() {
        ViewCompat.setOnApplyWindowInsetsListener(mBinding.root) { _, insets ->
            val imeBottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            val navBarBottom = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            val keyboardHeight = (imeBottom - navBarBottom).coerceAtLeast(0)
            mBinding.root.post { applyKeyboardOffset(keyboardHeight) }
            insets
        }
    }

    private fun applyKeyboardOffset(keyboardHeight: Int) {
        if (keyboardHeight <= 0) {
            mBinding.root.animate()
                .translationY(0f)
                .setDuration(200)
                .setInterpolator(DecelerateInterpolator())
                .start()
            return
        }

        val focusedView = currentFocus ?: return
        val location = IntArray(2)
        focusedView.getLocationOnScreen(location)
        val viewBottomOnScreen = location[1] + focusedView.height

        val currentTranslationY = mBinding.root.translationY
        val rawViewBottom = (viewBottomOnScreen - currentTranslationY +
                (16 * resources.displayMetrics.density).toInt()).toInt()

        val screenHeight = window.decorView.height
        val keyboardTop = screenHeight - keyboardHeight

        if (rawViewBottom > keyboardTop) {
            val offset = (rawViewBottom - keyboardTop).toFloat()
            mBinding.root.animate()
                .translationY(-offset)
                .setDuration(200)
                .setInterpolator(DecelerateInterpolator())
                .start()
        }
    }
}
