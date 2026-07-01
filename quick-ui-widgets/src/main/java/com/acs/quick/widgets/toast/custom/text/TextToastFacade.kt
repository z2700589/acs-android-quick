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


package com.acs.quick.widgets.toast.custom.text

import androidx.annotation.ColorInt

/**
 * author： 马世鹏
 * time: 2022/4/22
 * desc: 纯文字 toast
 */
interface TextToastFacade {

    interface Overall : TextToastApi {
        fun config(): ConfigBuilder
    }

    interface ConfigBuilder {
        fun apply(): TextToastApi
        fun backgroundColor(@ColorInt color: Int): ConfigBuilder
        fun contentColor(@ColorInt color: Int): ConfigBuilder
        fun contentSize(sizeSp: Float): ConfigBuilder
    }
}
