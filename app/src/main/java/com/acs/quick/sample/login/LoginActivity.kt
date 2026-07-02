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

import android.content.Intent
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
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import androidx.window.layout.WindowLayoutInfo
import com.acs.quick.common.ui.SystemBarHelper
import com.acs.quick.common.ui.activity.BaseActivity
import com.acs.quick.sample.R
import com.acs.quick.sample.databinding.ActivityLoginBinding
import com.acs.quick.sample.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * 登录页 — 蓝色渐变头部 + 白色状态栏图标，滚动时动态切换。
 *
 * @author Zhai Jie
 */
@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding, LoginViewModel>() {

    override val mViewModel: LoginViewModel by viewModels()

    override fun getLayoutId(): Int = R.layout.activity_login

    // ---- 系统栏配置 ----

    /** 深色背景 → 白色图标 */
    override val lightStatusBars: Boolean = false

    override val lightNavigationBars: Boolean = false

    /** 禁止 SystemBarHelper 干预 insets，由本页面自行接管键盘动画 */
    override val consumeWindowInsets: Boolean = false

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

        /** 折叠屏铰链安全边距（dp） */
        private const val HINGE_SAFE_MARGIN_DP = 8f

        /** 小屏与大屏分界 dp 值（与 layout-w600dp 对齐） */
        private const val SMALLEST_WIDTH_LARGE_DP = 600
    }

    /** 根据滚动位置切换状态栏图标颜色 */
    private fun updateStatusBarForScroll(scrollY: Int) {
        val headerEndPx = (resources.displayMetrics.heightPixels * HEADER_HEIGHT_RATIO).toInt()
        val shouldBeLight = scrollY < headerEndPx
        SystemBarHelper.setLightStatusBar(this, !shouldBeLight)
    }

    override fun initView() {
        mBinding.viewModel = mViewModel
        mBinding.lifecycleOwner = this
        setupImeAnimation()
        setupFoldingFeature()
    }

    // ============================================================
    // 键盘防抖动与输入框自动聚焦处理
    // ============================================================

    /**
     * 通过 [ViewCompat.setOnApplyWindowInsetsListener] 监听 IME insets 变化，
     * 以 [translationY] 平滑上移/复位 NestedScrollView，避免 window resize 导致的
     * 百分比 Guideline 重新计算和 [SystemBarHelper] 底部 padding 突变。
     *
     * 关键点：
     * - Manifest 中已设为 [adjustNothing]，窗口不会缩放，根除百分比布局重算
     * - [consumeWindowInsets] = false，[SystemBarHelper] 不会给底部加 padding
     * - 通过 post 到下一帧执行动画，避免在 layout 阶段触发 [animate()]
     */
    private fun setupImeAnimation() {
        ViewCompat.setOnApplyWindowInsetsListener(mBinding.root) { _, insets ->
            val imeBottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            val navBarBottom = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            val keyboardHeight = (imeBottom - navBarBottom).coerceAtLeast(0)
            // 推迟到下一帧，避免 layout → animate 的嵌套调用
            mBinding.root.post { applyKeyboardOffset(keyboardHeight) }
            insets
        }
    }

    /**
     * 根据键盘高度计算并执行平滑位移动画。
     *
     * @param keyboardHeight 键盘高度（已扣除导航栏高度），0 表示键盘收起
     */
    private fun applyKeyboardOffset(keyboardHeight: Int) {
        if (keyboardHeight <= 0) {
            // 键盘收起：平滑回到原位
            mBinding.root.animate()
                .translationY(0f)
                .setDuration(200)
                .setInterpolator(DecelerateInterpolator())
                .start()
            return
        }

        // 键盘弹起：计算焦点 View 所需偏移量
        val focusedView = currentFocus ?: return
        val location = IntArray(2)
        focusedView.getLocationOnScreen(location)
        val viewBottomOnScreen = location[1] + focusedView.height

        // 还原 NestedScrollView 平移前的原始坐标，避免 translationY 累积偏差
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

    // ============================================================
    // 折叠屏适配：监听 FoldingFeature 并调整铰链安全区域
    // ============================================================

    /**
     * 通过 [WindowInfoTracker] 监听设备折叠状态。
     * 仅当展开态布局（layout-w600dp/activity_login.xml）中存在铰链 Guideline 时动态调整，
     * 避免把输入框、按钮等交互元素放置在屏幕折叠铰链区域。
     */
    private fun setupFoldingFeature() {
        val windowInfoTracker = WindowInfoTracker.getOrCreate(this)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                windowInfoTracker.windowLayoutInfo(this@LoginActivity).collect { layoutInfo ->
                    applyFoldingFeature(layoutInfo)
                }
            }
        }
    }

    /**
     * 根据 [FoldingFeature] 的 bounds 调整左右分栏的铰链安全区域。
     * 只处理垂直折叠线；水平折叠不影响当前左右分栏布局。
     */
    private fun applyFoldingFeature(layoutInfo: WindowLayoutInfo) {
        val foldingFeature = layoutInfo.displayFeatures
            .filterIsInstance<FoldingFeature>()
            .firstOrNull { it.orientation == FoldingFeature.Orientation.VERTICAL }
            ?: return

        val hingeStart = mBinding.guidelineHingeStart ?: return
        val hingeEnd = mBinding.guidelineHingeEnd ?: return

        val screenWidth = window.decorView.width
        if (screenWidth == 0) return

        val bounds = foldingFeature.bounds
        val density = resources.displayMetrics.density
        val safeMarginPx = (HINGE_SAFE_MARGIN_DP * density).toInt()

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

    override fun initData() {
        // 观察导航事件
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.navigateToMain.collect {
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
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
}
