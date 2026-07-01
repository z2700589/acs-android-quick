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


package com.acs.quick.widgets.dialog.models

import android.os.Parcelable
import com.acs.quick.res.R.color
import com.acs.quick.widgets.button.TisStyleButton
import kotlinx.parcelize.Parcelize

@Parcelize
data class TisButtonStyle @JvmOverloads constructor(
    var textName: String = "Confirm",
    var textColor: Int = color.quick_function_info,
    var textSize: Float = 16f,
    var style: TisStyleButton.ButtonStyle = TisStyleButton.ButtonStyle.NORMAL,
    var icon: Int? = null
) : Parcelable
