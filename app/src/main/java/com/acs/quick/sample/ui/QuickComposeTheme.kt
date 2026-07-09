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

package com.acs.quick.sample.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import com.acs.quick.res.R as ResR

@Composable
fun QuickComposeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = colorResource(ResR.color.quick_S4_3D94FF),
            onPrimary = colorResource(ResR.color.quick_M10_FFFFFF),
            primaryContainer = colorResource(ResR.color.quick_S3_6BB0FF),
            onPrimaryContainer = colorResource(ResR.color.quick_S9_001B66),
            secondary = colorResource(ResR.color.quick_purple4_7773FF),
            onSecondary = colorResource(ResR.color.quick_M10_FFFFFF),
            tertiary = colorResource(ResR.color.quick_cyan4_10C9C9),
            onTertiary = colorResource(ResR.color.quick_M10_FFFFFF),
            error = colorResource(ResR.color.quick_red4_F0595E),
            onError = colorResource(ResR.color.quick_M10_FFFFFF),
            background = colorResource(ResR.color.quick_M10_FFFFFF),
            onBackground = colorResource(ResR.color.quick_M1_1D2126),
            surface = colorResource(ResR.color.quick_M8_F0F2F5),
            onSurface = colorResource(ResR.color.quick_M1_1D2126),
            surfaceVariant = colorResource(ResR.color.quick_M7_E6E8EB),
            onSurfaceVariant = colorResource(ResR.color.quick_M2_33373D),
            outline = colorResource(ResR.color.quick_M5_B0B3B8),
            outlineVariant = colorResource(ResR.color.quick_M6_C9CCD1)
        )
    } else {
        lightColorScheme(
            primary = colorResource(ResR.color.quick_S5_247BFF),
            onPrimary = colorResource(ResR.color.quick_M10_FFFFFF),
            primaryContainer = colorResource(ResR.color.quick_S1_EBF6FF),
            onPrimaryContainer = colorResource(ResR.color.quick_S8_002A8C),
            secondary = colorResource(ResR.color.quick_S4_3D94FF),
            onSecondary = colorResource(ResR.color.quick_M10_FFFFFF),
            tertiary = colorResource(ResR.color.quick_purple5_6B61FF),
            onTertiary = colorResource(ResR.color.quick_M10_FFFFFF),
            error = colorResource(ResR.color.quick_red5_E34950),
            onError = colorResource(ResR.color.quick_M10_FFFFFF),
            background = colorResource(ResR.color.quick_M10_FFFFFF),
            onBackground = colorResource(ResR.color.quick_M1_1D2126),
            surface = colorResource(ResR.color.quick_M10_FFFFFF),
            onSurface = colorResource(ResR.color.quick_M1_1D2126),
            surfaceVariant = colorResource(ResR.color.quick_M8_F0F2F5),
            onSurfaceVariant = colorResource(ResR.color.quick_M3_5E6166),
            outline = colorResource(ResR.color.quick_M5_B0B3B8),
            outlineVariant = colorResource(ResR.color.quick_M7_E6E8EB)
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
