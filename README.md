# ACS Android Quick Framework

> **企业级 Android 快速开发框架** — 面向多微服务 BFF 架构的移动端基础设施，以模块化 + MVVM + Kotlin Flow 为核心，提供开箱即用的基类体系、UI 组件库、网络层方案，以及多形态设备（折叠屏/平板/横屏）完整适配。

---

## 目录

- [项目背景](#项目背景)
- [模块架构](#模块架构)
- [核心技术栈](#核心技术栈)
- [快速开始](#快速开始)
- [接入指南](#接入指南)
- [项目结构](#项目结构)
- [核心设计](#核心设计)
- [多形态设备适配](#多形态设备适配)
- [性能优化](#性能优化)
- [工程化实践](#工程化实践)
- [安全与兼容性](#安全与兼容性)
- [License](#license)

---

## 项目背景

本项目提取自企业级 IM 应用（`mms-app`）的通用基础设施层，经过解耦与重构，沉淀为一套可独立复用的快速开发框架。核心目标是解决以下工程痛点：

| 痛点 | 解决方案 |
|------|---------|
| 多微服务（14+）多环境（5 套）URL 管理混乱 | 枚举 + OkHttp 拦截器动态路由，Header 驱动服务发现 |
| 每个页面重复编写 Loading / 异常 / 键盘处理 | `BaseActivity` / `BaseViewModel` 模板方法统一收敛 |
| 嵌套网络请求 Loading 闪烁 | `AtomicInteger` 引用计数，最外层返回后才隐藏 |
| 组件样式不统一，重复造轮子 | `quick-ui-widgets` 模块提供按钮 / 弹窗 / Toast / Search 全套组件 |
| 折叠屏 / 平板 / 横屏适配分散 | `FoldableHelper` 全局封装 + 4 档尺寸资源体系 |
| 团队协作缺乏规范约束 | 统一命名前缀 `quick_`、Gradle Version Catalog、模块单向依赖 |

---

## 模块架构

### 依赖关系图（单向无环）

```
Your App ──→ quick-common ──→ quick-res
                │                 ↑
                ├──→ quick-ui-widgets ──→ quick-res
                └──→ quick-search ──→ quick-res
                                └──→ quick-ui-widgets
```

### 模块职责

| 模块 | 命名空间 | 层级 | 核心职责 |
|------|---------|------|---------|
| **quick-res** | `com.acs.quick.res` | L0 基础设施 | 12 级中性色 + 6 级品牌色体系、**4 档语义化尺寸（sw360dp / sw400dp / sw600dp / sw720dp）**、Shapeable 样式、3 种分割线装饰器、入场/退场动画 |
| **quick-ui-widgets** | `com.acs.quick.widgets` | L1 UI 组件 | 多风格按钮（6 种 + Loading 动画）、Dialog 体系（Dialog/BottomSheet + Builder 模式）、Toast 调度器（工厂 + 同位置去重） |
| **quick-search** | `com.acs.quick.search` | L1 业务组件 | 单选/多选搜索列表、关键字高亮（SpannableString）、全选/反选、分页加载、缺省页集成 |
| **quick-common** | `com.acs.quick.common` | L2 聚合中心 | Base 基类体系（**含折叠屏监听**）、网络层（Retrofit + 拦截器链）、Hilt DI、DataBinding 适配器、KTX 扩展、DataStore 持久化、文件下载管理 |

> **设计原则**：接入方仅需一行 `implementation(project(":quick-common"))` 即可获得全部基础能力，子模块新增功能自动透传，零配置接入。

---

## 核心技术栈

| 类别 | 技术选型 | 版本 |
|------|---------|------|
| **构建工具** | Gradle + AGP + Version Catalog | 8.4 / 8.2.2 / TOML |
| **语言** | Kotlin + JVM 17 | 1.9.22 |
| **架构模式** | MVVM + DataBinding | — |
| **依赖注入** | Hilt + KSP | 2.50 / 1.0.17 |
| **异步** | Kotlin Coroutines + Flow | 1.7.3 |
| **网络层** | Retrofit + OkHttp + Moshi | 2.9.0 / 4.12.0 / 1.15.0 |
| **路由** | TheRouter | 1.3.2 |
| **图片加载** | Glide | 4.16.0 |
| **列表** | BRV (Binding RecyclerView) | 1.6.1 |
| **屏幕适配** | 4 档 `values-*` 资源目录 + swdp 限定符 | — |
| **折叠屏** | Jetpack WindowManager | 1.2.0 |
| **持久化** | DataStore Preferences | 1.0.0 |
| **事件总线** | LiveEventBus | 1.8.0 |
| **日志** | Timber（DEBUG 自动启用） | 5.0.1 |
| **下拉刷新** | SmartRefreshLayout | 2.1.0 |
| **缺省页** | StateLayout | 1.3.5 |
| **多类型列表** | MultiType | 3.4.1 |
| **Fragment 可见性** | fragment-visibility | 1.0.0 |
| **应用启动** | AndroidX Startup | 1.1.1 |
| **数据库** | Room | 2.6.1 |
| **后台任务** | WorkManager | 2.9.0 |
| **工具类** | UtilCodeX | 1.31.1 |

### SDK 版本

| 参数 | 值 |
|------|-----|
| compileSdk | 36 |
| minSdk | 24 (Android 7.0) |
| targetSdk | 34 (Android 14) |
| Java 兼容性 | Java 17 |
| JVM Target | 17 |

---

## 快速开始

### 环境要求

| 工具 | 版本 |
|------|------|
| Android Studio | Hedgehog (2023.1.1) 及以上 |
| JDK | 17 |
| Gradle | 8.4 |
| Android SDK | API 34 (compile) / API 24 (min) |

### 构建项目

```bash
# 1. 克隆项目
git clone <repo-url> && cd acs-android-quick

# 2. 使用 Android Studio 打开项目根目录
# 3. Sync Gradle（首次同步约 2-3 分钟）
```

---

## 接入指南

### 在新项目中接入

**方式一：源码依赖**（推荐，适合二次开发）

将框架作为 Git Submodule 或直接复制模块目录到你的项目，然后：

```kotlin
// settings.gradle.kts
include(":quick-common")
include(":quick-res")
include(":quick-ui-widgets")
include(":quick-search")

// app/build.gradle.kts
dependencies {
    implementation(project(":quick-common"))
}
```

**方式二：AAR / Maven 发布**（适合直接使用）

```kotlin
// app/build.gradle.kts
dependencies {
    implementation("com.acs.quick:quick-common:1.0.0")
}
```

### 基础接入三步走

**Step 1：初始化 Application**

```kotlin
@HiltAndroidApp
class MyApp : BaseApplication() {
    // BaseApplication 已内置 Timber 初始化、ActivityStackManager 等
}
```

**Step 2：创建 Activity（跳转必须使用 TheRouter）**

```kotlin
@AndroidEntryPoint
@Route(path = "/main/MainActivity")  // 必须加 @Route 注解
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    override val mViewModel: MainViewModel by viewModels()

    override fun getLayoutId(): Int = R.layout.activity_main

    override fun initView() {}

    override fun initData() {}

    override fun initListener() {}
}
```

**Step 3：页面跳转统一走 TheRouter**

```kotlin
// ❌ 禁止直接 startActivity + Intent
// startActivity(Intent(this, MainActivity::class.java))

// ✅ 统一使用 TheRouter（支持路由拦截、跨模块跳转）
TheRouter.build("/main/MainActivity").navigation(this)

// ✅ 传递参数（通过 @Autowired 自动注入）
TheRouter.build("/main/DetailActivity")
    .withString("id", userId)
    .withString("title", "标题")
    .navigation(this)
```

**Step 4：创建 ViewModel 与 Repository**

```kotlin
@HiltViewModel
class MainViewModel @Inject constructor(
    private val repo: MyRepository
) : BaseViewModel() {

    // 便捷网络请求，自动管理 Loading + 异常
    fun fetchData() = execute {
        val result = repo.getData()
        // 更新 UI State
    }
}

class MyRepository : BaseRepository() {
    fun getData(): Flow<NetworkResult<Data>> = request {
        val response = apiService.getData()
        emit(NetworkResult.Success(response.data))
    }
}
```

### 配置环境 URL

在 `UrlConfig` 枚举中配置各微服务的环境地址：

```kotlin
enum class UrlConfig(val envCode: String, val baseUrl: String) {
    DEV("dev", "https://dev-api.example.com"),
    STAGING("staging", "https://staging-api.example.com"),
    PRODUCTION("prod", "https://api.example.com"),
}
```

通过 DataStore 持久化当前环境编码，`BaseUrlInterceptor` 自动完成路由切换。

---

## 项目结构

```
acs-android-quick/
├── build.gradle.kts                     # 根构建脚本（插件注册）
├── settings.gradle.kts                  # 模块声明 + 仓库配置
├── gradle.properties                    # Gradle 属性
├── gradle/
│   ├── libs.versions.toml               # Version Catalog（统一版本管理）
│   └── wrapper/                         # Gradle Wrapper
│
├── quick-common/                        # 🧩 聚合中心库
│   ├── build.gradle.kts                # api 方式暴露所有子模块 + 全部依赖
│   └── src/main/java/com/acs/quick/common/
│       ├── api/NetService.kt           # Retrofit API 接口定义
│       ├── app/                        # BaseApplication + ActivityLifecycle
│       ├── config/                     # UrlConfig / RouteKey / RouteUrl / AppConfig
│       ├── data/                       # 数据层
│       │   ├── bean/User.kt            # 数据模型
│       │   ├── callback/NetCallBack.kt # NetworkResult sealed class + onAuthExpired
│       │   ├── dataStore/              # CommonPreferencesDataStore
│       │   ├── download/DownloadState.kt
│       │   ├── repository/BaseRepository.kt
│       │   ├── response/BaseResponse.kt
│       │   └── state/UIState.kt
│       ├── databinding/                # BindingAdapter 集合
│       ├── di/NetworkModule.kt         # Hilt Module（网络层注入）
│       ├── download/DownloadManager.kt # 文件下载管理
│       ├── exception/ServerException.kt
│       ├── glide/GlideModule.kt
│       ├── init/AppInitializer.kt      # AndroidX Startup 初始化
│       ├── interceptor/                # OkHttp 拦截器链
│       │   ├── BaseUrlInterceptor.kt   # 多环境动态路由
│       │   └── BaseParamsInterceptor.kt # 公共参数注入
│       ├── ktx/                        # Kotlin 扩展函数集（40+）
│       ├── ui/                         # 基类体系
│       │   ├── SystemBarHelper.kt      # 边缘渲染辅助
│       │   ├── activity/BaseActivity.kt
│       │   ├── foldable/
│       │   │   └── FoldableHelper.kt   # 折叠屏全局监听封装（新增）
│       │   ├── fragment/BaseFragment.kt
│       │   └── viewmodel/BaseViewModel.kt
│       └── utils/                      # 工具类（Stack / SP / Network）
│
├── quick-res/                           # 🎨 基础资源
│   ├── build.gradle.kts
│   └── src/main/
│       ├── java/com/acs/quick/res/view/divider/
│       │   ├── LinearItemDecoration.kt
│       │   ├── GridItemDecoration.kt
│       │   ├── StaggeredGridItemDecoration.kt
│       │   └── RecyclerViewExt.kt
│       └── res/
│           ├── anim/                   # 8 种动画（底部/顶部 × 平移/缩放/透明度）
│           ├── color/                  # 色彩选择器（登录输入框状态色）
│           ├── drawable/               # 40+ 背景 Shape / 选择器 / 图标
│           ├── values/
│           │   ├── colors.xml          # 色彩体系（中性色 12 级 + 品牌色 6 级）
│           │   ├── dimens.xml          # 基准语义化尺寸（360dp）
│           │   ├── styles.xml          # 全局样式（含 quick_textinput_prefix）
│           │   └── button_colors.xml   # 按钮颜色预设
│           ├── values-sw360dp/        # 小屏手机适配（4.0"~4.7"）
│           ├── values-sw400dp/        # 大屏手机适配（4.7"~6.0"）
│           ├── values-sw600dp/        # 平板/折叠屏展开适配（7"~12"）
│           ├── values-sw720dp/        # 大平板适配（10"+）
│           └── values-night/          # 深色模式适配
│
├── quick-ui-widgets/                    # 🧰 UI 组件库
│   ├── build.gradle.kts
│   └── src/main/java/com/acs/quick/widgets/
│       ├── button/                     # 按钮系统
│       │   ├── TisSuperButton.kt       # GradientDrawable 四角圆角 + 三态颜色
│       │   ├── TisStyleButton.kt       # 6 种预定义风格（PRIMARY/SECONDARY/DANGER/...）
│       │   └── loading/                # Loading 动画（ValueAnimator 驱动）
│       ├── dialog/                     # 弹窗体系
│       │   ├── base/                   # TisBaseDialog / TisBaseBottomSheetDialog
│       │   └── dialog/                 # TisMessageDialog / QuickLoadingDialog 等
│       └── toast/                      # Toast 调度器
│           ├── scheduler/              # 基于位置 + 别名的智能调度
│           ├── factory/                # ToastFactory 工厂
│           ├── custom/text/            # TextToast（16 种显示方式）
│           ├── custom/image/           # ImageToast（成功/失败/信息/完成图标）
│           └── compact/                # 系统 Toast / DialogToast 降级方案
│
└── quick-search/                        # 🔍 搜索列表组件
    ├── build.gradle.kts
    └── src/main/java/com/acs/quick/search/
        ├── bean/SearchModel.kt         # 搜索数据模型
        └── list/TisSearchList.kt       # 搜索列表（纯 XML 属性配置）
```

---

## 核心设计

### 一、MVVM + Flow 基类体系

#### BaseActivity — 声明式配置，模板方法收敛

```kotlin
abstract class BaseActivity<VB : ViewDataBinding, VM : BaseViewModel> : AppCompatActivity()
```

- **双泛型约束**：编译期保证 ViewBinding 与 ViewModel 类型安全
- **属性覆盖**：子类通过 Kotlin 属性 `lightStatusBars` / `lightNavigationBars` / `consumeWindowInsets` 定制系统栏行为，无需重写生命周期方法
- **三步初始化**：`initView()` → `initData()` → `initListener()` 统一页面初始化节奏
- **Loading 自动订阅**：`repeatOnLifecycle(STARTED)` 自动收集 `ViewModel.isLoading` StateFlow
- **键盘智能隐藏**：`dispatchTouchEvent` 点击 EditText 外部自动收起输入法
- **Edge-to-Edge**：`SystemBarHelper` 实现边到边渲染，支持状态栏遮罩、insets 独立控制
- **401 鉴权过期**：`onAuthExpired` 全局回调，子类可覆写处理登录失效逻辑
- **折叠屏监听**：通过 `foldableHelper` 属性 + `onFoldableStateChanged` 回调钩子接入

#### BaseFragment — 延迟加载与可见性感知

- 继承 `VisibilityFragment`，感知 Fragment 对用户的可见/不可见状态
- `onVisibleFirst()` + 300ms `postDelayed` 延迟加载，避免 ViewPager 切换动画期间渲染卡顿
- `isFirst` 标志位确保数据仅加载一次

#### BaseViewModel — 并发安全的 Loading 与异常处理

```kotlin
abstract class BaseViewModel : ViewModel() {
    private val loadingCount = AtomicInteger(0)
    private val _isLoading = MutableStateFlow(false)
}
```

| 特性 | 实现 | 解决的问题 |
|------|------|-----------|
| **并发 Loading 计数** | `AtomicInteger.incrementAndGet()` | 页面同时发起 N 个请求时，只有 N→0 才隐藏 Loading，避免中间闪烁 |
| **StateFlow vs SharedFlow** | Loading 用 StateFlow（持久状态），Toast 用 SharedFlow（一次性事件） | 正确处理状态与事件的语义差异 |
| **execute() 便捷方法** | 自动管理 loading + `try/catch` + Toast + `Dispatchers.IO` | 80% 的网络请求场景一行搞定 |
| **CoroutineExceptionHandler** | 全局兜底未捕获异常 | 自动隐藏 loading + 弹出异常提示，防止页面假死 |

#### BaseRepository — Flow 驱动的数据层

```kotlin
open class BaseRepository {
    protected fun <T> request(
        requestBlock: suspend FlowCollector<NetworkResult<T>>.() -> Unit
    ): Flow<NetworkResult<T>> = flow(block = requestBlock).flowOn(Dispatchers.IO)
}
```

- 强制使用 `Flow<NetworkResult<T>>` 返回类型，调用方必须显式处理成功/失败分支
- 默认在 `Dispatchers.IO` 执行

---

### 二、多微服务环境动态路由

对接 **14 个微服务 × 5 套环境**（测试、预发布、API、本地、正式），架构如下：

```
Retrofit API 定义 Header("URL_TYPE")
    → BaseUrlInterceptor 拦截
        → UrlConfig 枚举查表
            → 替换 scheme/host/port
```

- **`UrlConfig`** 枚举集中管理所有环境 URL，编译期保证完整性
- **`BaseUrlInterceptor`** 在 OkHttp 拦截器层替换 base URL，对上层 Retrofit 接口完全透明
- **环境切换** 通过 DataStore 持久化环境编码，`BaseParamsInterceptor` 内存缓存优化性能
- 自动注入 `X-APP-NAME` / `X-APP-TYPE` Header 标识客户端身份

---

### 三、页面跳转 — TheRouter 路由

所有页面跳转**强制使用 TheRouter**，禁止直接 `startActivity(Intent(...))`：

```kotlin
// ✅ 正确：使用 @Route 注解 + TheRouter 跳转
@Route(path = "/main/LoginActivity")
class LoginActivity : BaseActivity<...>() { ... }

// 跳转时
TheRouter.build("/main/MainActivity").navigation(this)

// 带参数
TheRouter.build("/main/DetailActivity")
    .withString("id", userId)
    .navigation(this)
```

- **@Route 注解**：每个 Activity 必须标注路由路径，用于编译期生成路由表
- **全局路由常量**：统一在 `RouteUrl.kt` 中管理，防止硬编码路径散落
- **支持参数注入**：`@Autowired` 字段自动接收路由参数，无需手动解析 Intent

> ⚠️ **路由常量写法注意**：Kotlin `const val` 不支持字符串模板（如 `"\${VAR}/path"`）和 `+` 拼接。直接写完整路径字符串，或使用普通 `val` + `+` 拼接。

---

### 四、UI 组件库设计

#### 按钮系统 — 六种风格 + Loading 动画

```
AppCompatButton → TisSuperButton → TisStyleButton
```

- **TisSuperButton**：基于 `GradientDrawable` 独立四角圆角、`ColorStateList` 三态颜色（正常/按下/禁用）、`StateListAnimator` 阴影动画、`ValueAnimator` 驱动的 Loading 文字动画
- **TisStyleButton**：预定义 PRIMARY / SECONDARY / DANGER / GHOST / NORMAL / REFUSE 六种风格，通过 XML 属性 `quick_btnStyle` 声明式使用

#### 弹窗体系 — 自限定泛型 + Builder 模式

```
AppCompatDialogFragment → TisBaseDialog<T>          → TisMessageDialog / QuickLoadingDialog
BottomSheetDialogFragment → TisBaseBottomSheetDialog<T> → TisBottomSheetDialog / TisApprovalSuggestDialog
```

- **自限定泛型** `<T : TisBaseDialog<T>>`：Builder 链式调用始终返回具体子类型，无需强制转型
- **参数 Parcelable 化**：`@Parcelize` 保证配置在进程重建后恢复
- **ViewHolder 模式**：`SparseArray` 缓存 View 查找
- **键盘联动**：指定 `needKeyboardViewId`，弹窗显示时自动弹出键盘
- **屏幕适配**：支持百分比宽高（`widthScale` / `heightScale`）和 dp 精确值

#### Toast 调度器 — 工厂 + 同位置去重

```
ToastFactory → CompatToast（系统 Toast / DialogToast 降级）
ToastScheduler → 同位置 | 同别名 → 更新内容；不同 → 替换
```

- **非队列非栈**：基于位置 + 别名的智能调度，同位置同别名更新内容而非追加新 Toast
- 支持绑定页面 ID（`boundPageId`），离开页面延迟不显示
- `TextToastImpl` 支持 16 种显示方式（短/长 × 居中/顶部/底部/自定义位置）
- `ImageToast` 内置成功/失败/信息/完成图标

---

### 五、QuickSearchList — 高度封装的搜索选择组件

通过**纯 XML 自定义属性**即可配置的搜索选择列表：

- 单选/多选模式切换（`isMultiple`）
- 关键字高亮：`SpannableString` + 正则匹配
- 最大选择数量限制 + 自定义超限提示（`maxSelectCount` / `overLimitTip`）
- 全选 / 反选 / 手动设置选中
- 上拉加载更多（SmartRefreshLayout）+ 下拉刷新 + 缺省页（StateLayout）
- 水平/垂直两种布局样式（`orientation`）
- 完全解耦数据源，通过 `submitList` / `submitSelected` 驱动

---

## 多形态设备适配

### 一、4 档尺寸资源体系

通过 `values-*` 限定符目录，实现按屏幕宽度自动切换尺寸：

| 资源目录 | 适用设备 | 基准宽度 | 字号缩放 | 间距缩放 | 圆角缩放 |
|---------|---------|---------|---------|---------|---------|
| `values/` (基准) | 360dp 手机 | 360dp | 1.0× | 1.0× | 1.0× |
| `values-sw360dp/` | 小屏手机（4.0"~4.7"） | < 360dp | 0.93× | 0.93× | 0.5× |
| `values-sw400dp/` | 大屏手机（4.7"~6.0"） | 400dp | 1.0× | 1.0× | 1.0× |
| `values-sw600dp/` | 平板/折叠屏展开（7"~12"） | 600dp | **+21%** | **+25%** | **+25%** |
| `values-sw720dp/` | 大平板（10"+） | 720dp | **+40%** | **+40%** | **+38%** |

**尺寸命名规范**：`quick_T{N}_{sp值}` / `quick_Sp{N}_{dp值}` / `quick_Ra{N}_{dp值}`
- T = 字体（Text），Sp1~7 = 7 级字阶（28sp → 10sp）
- Sp = 间距（Space），Sp0~16 = 16 级间距（0dp → 60dp）
- Ra = 圆角（Radius），Ra1~5 = 5 级圆角（2dp → 16dp）

**覆盖原则**：只有需要缩放的尺寸才在 sw 目录中覆盖；不需要缩放的通用尺寸（如 `0dp`）可省略。

### 二、布局目录适配

| 布局目录 | 触发条件 | 典型场景 |
|---------|---------|---------|
| `layout/` | 默认 | 手机竖屏 |
| `layout-land/` | 横屏 | 手机横屏（视频/游戏） |
| `layout-sw600dp/` | 最窄可用宽度 ≥ 600dp | 平板竖屏 / 折叠屏展开态 |

> ⚠️ **主题属性 vs 样式资源**：布局中引用 `?attr/textAppearanceBodyMedium` 等主题属性时，不会跟随 `values-sw*/dimens.xml` 缩放；应使用 `@style/...` 引用自定义样式，样式内部再引用 `@dimen/...`，这样才能被多档尺寸资源覆盖。

### 三、折叠屏监听 — FoldableHelper

基于 **Jetpack WindowManager** 封装的全局折叠屏状态监听：

```kotlin
// FoldableState — 折叠屏设备状态（sealed class）
sealed class FoldableState {
    data object None              // 非折叠设备
    data class Flat(...)          // 完全展开（视为大屏）
    data class HalfOpened(...)     // 半折叠（平行视窗 / 帐篷 / 支架模式）
    data class Folded(...)        // 折叠合起（使用外屏）
}

// 方式 1：Callback 风格（推荐，最简用法）
override fun initView() {
    setupFoldable { state ->
        when (state) {
            is FoldableState.Flat ->       adaptLargeScreen()
            is FoldableState.HalfOpened -> adaptSplitScreen(state)
            is FoldableState.Folded ->     adaptCompact()
            is FoldableState.None -> {}
        }
    }
}

// 方式 2：Flow 风格（适合 ViewModel 层）
lifecycleScope.launch {
    repeatOnLifecycle(Lifecycle.State.STARTED) {
        foldableState().collect { state -> ... }
    }
}

// 方式 3：子类覆写 BaseActivity 钩子（完全自定义）
override fun onFoldableStateChanged(state: FoldableState) {
    // 完全自定义折叠态 UI 适配逻辑
}
```

**铰链避让**：当设备为 `HalfOpened` 态时，可通过 `state.hingeBounds` 获取铰链区域 Rect，动态调整内容区域的 Guideline / padding，避免内容被铰链遮挡。

**启用方式**：在子类中覆写 `foldableHelper` 属性：

```kotlin
// MainActivity 示例
override val foldableHelper: FoldableHelper
    get() = FoldableHelper(FoldableConfig(hingeSafeMarginDp = 12f))
```

---

## 性能优化

| 优化点 | 实现方式 | 收益 |
|--------|---------|------|
| **拦截器内存缓存** | `ConcurrentHashMap` 缓存基础参数，避免每次读 DataStore | 减少主线程阻塞 |
| **OkHttp 连接池** | 5 个空闲连接，5 分钟 keep-alive | 减少 TCP 握手开销 |
| **心跳保活** | `pingInterval = 30s` | 长连接稳定性 |
| **统一超时** | connect/read/write = 12s | 防止请求卡死 |
| **Moshi 懒加载** | `by lazy` 初始化 JSON 适配器 | 按需创建，减少内存 |
| **Fragment 延迟加载** | `onVisibleFirst` + 300ms postDelayed | 避免动画期间渲染卡顿 |
| **DataBinding 防重复** | `contentEquals` 检查后更新 | 减少无效布局刷新 |
| **防快速点击** | `ViewClickDelay` hashCode + 1000ms 间隔 | 防止重复提交 |
| **屏幕适配排外** | `isExcludeFontScale = true` | 屏蔽系统字体缩放干扰 |
| **Timber 条件初始化** | 仅 DEBUG 下 `plant(DebugTree())` | Release 零日志开销 |
| **MultiDex** | `multiDexEnabled = true` | 支持大型项目 |

---

## 工程化实践

### Gradle Version Catalog

所有依赖版本集中在 `gradle/libs.versions.toml`，模块中通过 `libs.xxx` 引用，版本升级时仅需修改一处。

### 统一命名规范

| 维度 | 规范 | 示例 |
|------|------|------|
| 资源前缀 | `quick_` | `quick_btnStyle`, `quick_color_S5_247BFF` |
| 模块命名 | `quick-*` | `quick-common`, `quick-ui-widgets` |
| 颜色命名 | 语义化：`{体系}_{色阶}_{色值}` | `quick_S5_247BFF`（系统色 5 阶 #247BFF） |
| 尺寸命名 | `quick_T{级}_{sp值}` / `quick_Sp{级}_{dp值}` | `quick_T1_28`（28sp标题）/ `quick_Sp7_24`（24dp边距） |
| 注释规范 | KDoc + `@author` + `@Description` | 全量代码覆盖 |

### 代码组织

- `ktx/` 目录：Kotlin 扩展函数集中管理（尺寸换算 / Flow 订阅 / Intent 序列化 / ViewPager2）
- `databinding/` 目录：BindingAdapter 集中管理（图片加载 / 阴影 / 时间格式化 / 防快速点击）
- `config/` 目录：常量集中管理（URL / 路由 / 事件 Key / SP Key）
- `ui/foldable/` 目录：折叠屏监听封装（FoldableHelper / FoldableState）

### 注解处理器策略

- **KSP** 用于 Room（编译速度优于 KAPT）
- **KAPT** 用于 Hilt、Moshi、TheRouter（兼容性考虑）
- `kotlin-parcelize` 插件替代手动 `Parcelable` 实现

---

## 安全与兼容性

- SSL 证书信任策略封装为独立拦截器，企业内网环境可配置
- `@SuppressLint` 标注明确：`StaticFieldLeak`（Application Context）、`SetTextI18n`、`TrustAllX509TrustManager`
- `@JvmStatic` / `@JvmOverloads` 确保 Java 调用友好（混合项目兼容）
- 折叠屏适配依赖 Jetpack WindowManager，支持 Android 10+（API 29）折叠特征检测；低于此版本自动回退为 `FoldableState.None`

---

## License

```
Copyright 2026 zhaijie

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
