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

package com.acs.quick.common.ui.foldable

import android.app.Activity
import android.graphics.Rect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import androidx.window.layout.WindowLayoutInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ============================================================
// 折叠状态 — FoldableState
// ============================================================

/**
 * 折叠屏/双屏设备的状态。
 *
 * 用法示例：
 * ```
 * setupFoldable { state ->
 *     when (state) {
 *         is FoldableState.Flat ->       // 屏幕展开，视为大屏
 *         is FoldableState.HalfOpened -> // 半折叠（平行/帐篷模式）
 *         is FoldableState.Folded ->     // 折叠态（单手握持外屏）
 *         is FoldableState.None ->       // 非折叠设备
 *     }
 * }
 * ```
 */
sealed class FoldableState {

    /** 非折叠设备，或未检测到折叠特征 */
    data object None : FoldableState()

    /** 屏幕完全展开（视为大屏/平板） */
    data class Flat(
        val orientation: Orientation,
        val hingeBounds: Rect? = null
    ) : FoldableState()

    /** 屏幕半折叠（平行视窗 / 帐篷 / 支架模式） */
    data class HalfOpened(
        val orientation: Orientation,
        val hingeBounds: Rect,
        val mode: HalfOpenedMode
    ) : FoldableState()

    /** 屏幕折叠合起（使用外屏 / 单手模式） */
    data class Folded(
        val orientation: Orientation,
        val hingeBounds: Rect
    ) : FoldableState()

    enum class Orientation {
        /** 竖向折叠线（左右分屏，如三星 Z Fold 系列） */
        VERTICAL,
        /** 横向折叠线（上下分屏，如联想 ThinkPad X1 Fold） */
        HORIZONTAL
    }

    enum class HalfOpenedMode {
        /** 平行模式：屏幕 180° 展开，两个 App 并排显示 */
        FLAT,
        /** 帐篷模式：屏幕以一定角度支撑，显示在两侧 */
        TENT,
        /** 支架模式：横向折叠，呈三角支撑 */
        TABLETOP
    }
}

// ============================================================
// 折叠屏辅助类 — FoldableHelper
// ============================================================

/**
 * 折叠屏/双屏设备监听辅助类。
 *
 * **核心设计：**
 * - 单例模式，全局共享一个 WindowInfoTracker 实例
 * - 生命周期感知：跟随 [LifecycleOwner] 自动启动/停止
 * - 支持 Flow 订阅和 callback 两种用法
 * - 过滤无折叠特征的 LayoutInfo，避免不必要的回调
 *
 * **用法示例 — Activity 中使用：**
 * ```
 * // 方式 1：Callback 风格（推荐，简单场景）
 * override fun initView() {
 *     setupFoldable { state ->
 *         when (state) {
 *             is FoldableState.Flat -> adaptToLargeScreen()
 *             is FoldableState.HalfOpened -> adaptToHalfOpened(state)
 *             is FoldableState.Folded -> adaptToFolded()
 *             is FoldableState.None -> {}
 *         }
 *     }
 * }
 *
 * // 方式 2：Flow 风格（适合 ViewModel 中处理）
 * lifecycleScope.launch {
 *     foldableState().collect { state ->
 *         // ...
 *     }
 * }
 * ```
 *
 * **用法示例 — Fragment 中使用：**
 * ```
 * viewLifecycleOwner.setupFoldable { state -> ... }
 * ```
 *
 * @param config  配置项，[FoldableConfig] 默认值适合大多数场景
 */
class FoldableHelper(
    private val config: FoldableConfig = FoldableConfig()
) {
    // ---- 状态流 ----
    private val _state = MutableStateFlow<FoldableState>(FoldableState.None)
    val state: Flow<FoldableState> = _state.asStateFlow()

    // 当前活跃的 LifecycleOwner（用于 cancel）
    private var activeOwner: LifecycleOwner? = null

    // ---- 配置 ----
    // 安全边距（铰链两侧各留的间距，dp）
    var hingeSafeMarginDp: Float = config.hingeSafeMarginDp

    // 是否监听半折叠状态
    var detectHalfOpened: Boolean = config.detectHalfOpened

    // ---- 公开 API ----

    /**
     * 订阅折叠状态变化，自动跟随 [lifecycleOwner] 的 STARTED/STOPPED。
     * **推荐用法**：在 [Activity.initView] 或 [Fragment.onViewCreated] 中调用。
     *
     * @param owner      生命周期所有者，通常传入 `this`（Activity）或 `viewLifecycleOwner`（Fragment）
     * @param onChanged  折叠状态变化回调
     */
    fun setup(
        owner: LifecycleOwner,
        onChanged: (FoldableState) -> Unit
    ) {
        // 取消上一个 owner 的订阅
        cancel()

        activeOwner = owner

        val tracker = WindowInfoTracker.getOrCreate(owner as android.app.Activity)
        owner.lifecycleScope.launch {
            owner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                tracker.windowLayoutInfo(owner).collect { layoutInfo ->
                    val newState = parseLayoutInfo(layoutInfo)
                    // 仅在状态变化时触发回调（减少不必要的刷新）
                    if (newState != _state.value) {
                        _state.value = newState
                        onChanged(newState)
                    }
                }
            }
        }
    }

    /**
     * 订阅折叠状态，返回 [Flow]。
     * **适合 ViewModel 层**：可与 StateFlow 组合做更复杂的响应式逻辑。
     *
     * 用法：
     * ```
     * val vm: MyViewModel by viewModels()
     *
     * // 在 Activity 中
     * lifecycleScope.launch {
     *     foldableHelper.state()
     *         .filterIsInstance<FoldableState.Flat>()
     *         .collect { vm.onLargeScreen() }
     * }
     */
    fun state(): Flow<FoldableState> = state

    /**
     * 同步获取当前折叠状态（调用时若尚未收到首次回调，返回 [FoldableState.None]）。
     */
    fun currentState(): FoldableState = _state.value

    /**
     * 立即触发一次状态解析并回调。
     * **适合在 `onResume` 中调用**，确保 Activity 恢复时状态最新。
     *
     * @param owner      生命周期所有者
     * @param onChanged  回调
     */
    fun refresh(owner: LifecycleOwner, onChanged: (FoldableState) -> Unit) {
        val activity = owner as Activity
        activeOwner = owner
        val tracker = WindowInfoTracker.getOrCreate(activity)
        owner.lifecycleScope.launch {
            tracker.windowLayoutInfo(activity).collect { layoutInfo ->
                val newState = parseLayoutInfo(layoutInfo)
                _state.value = newState
                onChanged(newState)
            }
        }
    }

    /**
     * 取消订阅。在 [Lifecycle.State.DESTROYED] 时自动调用，也可手动调用。
     */
    fun cancel() {
        activeOwner = null
    }

    // ---- 内部解析 ----

    private fun parseLayoutInfo(layoutInfo: WindowLayoutInfo): FoldableState {
        val foldingFeature = layoutInfo.displayFeatures
            .filterIsInstance<FoldingFeature>()
            .firstOrNull()
            ?: return FoldableState.None

        val orientation = when (foldingFeature.orientation) {
            FoldingFeature.Orientation.VERTICAL -> FoldableState.Orientation.VERTICAL
            FoldingFeature.Orientation.HORIZONTAL -> FoldableState.Orientation.HORIZONTAL
            else -> return FoldableState.None
        }

        val hingeBounds = foldingFeature.bounds

        return when (foldingFeature.state) {
            FoldingFeature.State.HALF_OPENED -> {
                val mode = when {
                    !foldingFeature.isSeparating -> FoldableState.HalfOpenedMode.TENT
                    orientation == FoldableState.Orientation.VERTICAL ->
                        FoldableState.HalfOpenedMode.FLAT

                    else -> FoldableState.HalfOpenedMode.TABLETOP
                }
                FoldableState.HalfOpened(
                    orientation = orientation,
                    hingeBounds = hingeBounds,
                    mode = mode
                )
            }
            FoldingFeature.State.FLAT -> {
                FoldableState.Flat(
                    orientation = orientation,
                    hingeBounds = hingeBounds.takeIf { foldingFeature.isSeparating }
                )
            }
            else -> FoldableState.None
        }
    }
}

// ============================================================
// 配置项 — FoldableConfig
// ============================================================

/**
 * [FoldableHelper] 的配置。
 *
 * 默认值适合大多数业务场景，通常不需要修改。
 */
data class FoldableConfig(
    /** 铰链两侧的安全边距（dp），用于避让铰链区域 */
    val hingeSafeMarginDp: Float = 8f,

    /** 是否监听半折叠（HalfOpened）状态，默认 true */
    val detectHalfOpened: Boolean = true
)

// ============================================================
// 扩展函数 — Activity / Fragment 一行调用
// ============================================================

/**
 * 全局 FoldableHelper 实例，懒加载，Activity/Fragment 间共享。
 * 第一次调用 `setupFoldable()` 时初始化，后续复用同一个实例。
 */
private val globalFoldableHelper: FoldableHelper by lazy { FoldableHelper() }

/**
 * Activity 中一行调用折叠屏监听。
 *
 * **自动处理生命周期**，在 ON_START 开始监听，ON_STOP 自动取消。
 *
 * 用法：
 * ```
 * override fun initView() {
 *     setupFoldable { state ->
 *         when (state) {
 *             is FoldableState.Flat -> showLargeScreenLayout()
 *             is FoldableState.HalfOpened -> showSplitScreenLayout(state)
 *             is FoldableState.Folded -> showCompactLayout()
 *             is FoldableState.None -> {}
 *         }
 *     }
 * }
 * ```
 *
 * @param helper    可选，自定义 [FoldableHelper] 实例。
 *                  默认使用全局共享实例，传入新实例可独立配置。
 * @param onChanged 折叠状态变化回调
 */
fun Activity.setupFoldable(
    helper: FoldableHelper = globalFoldableHelper,
    onChanged: (FoldableState) -> Unit
) {
    // @Suppress 理由：调用方（BaseActivity / Fragment）传入的始终是 AppCompatActivity
    // 其在 AndroidX activity-ktx 中实现了 LifecycleOwner，cast 必然成功
    @Suppress("UNCHECKED_CAST")
    helper.setup(this as LifecycleOwner, onChanged)
}

/**
 * Fragment 中一行调用折叠屏监听。
 *
 * 用法：
 * ```
 * override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
 *     super.onViewCreated(view, savedInstanceState)
 *     viewLifecycleOwner.setupFoldable { state -> ... }
 * }
 * ```
 */
fun LifecycleOwner.setupFoldable(
    helper: FoldableHelper = globalFoldableHelper,
    onChanged: (FoldableState) -> Unit
) {
    helper.setup(this, onChanged)
}

/**
 * 获取全局 [FoldableHelper] 实例。
 * 适合在 ViewModel 中需要访问折叠状态时使用：
 *
 * 用法：
 * ```
 * val vm: MyViewModel by viewModels()
 * lifecycleScope.launch {
 *     foldableHelper().state()
 *         .collect { state -> /* ... */ }
 * }
 * ```
 */
fun LifecycleOwner.foldableHelper(): FoldableHelper = globalFoldableHelper
