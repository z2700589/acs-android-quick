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


package com.acs.quick.widgets.toast

import com.acs.quick.widgets.toast.custom.image.ImageToastFacade
import com.acs.quick.widgets.toast.custom.image.ImageToastImpl
import com.acs.quick.widgets.toast.custom.text.TextToastFacade
import com.acs.quick.widgets.toast.custom.text.TextToastImpl

/**
 * author： 马世鹏
 * time: 2022/4/26
 * desc:
 */
object TisToastUtils {

    @JvmStatic
    fun imageToast(): ImageToastFacade.Overall = ImageToastImpl()

    @JvmStatic
    fun toast(): TextToastFacade.Overall = TextToastImpl()

}
