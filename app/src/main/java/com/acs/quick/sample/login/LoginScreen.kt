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

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.acs.quick.common.ui.foldable.FoldableState
import com.acs.quick.sample.ui.textSizeResource
import com.acs.quick.res.R as ResR
import com.acs.quick.sample.R
import kotlin.math.roundToInt

private const val HeaderRatio = 0.30f
private val LargeScreenMinWidth = 600.dp
private val HingeSafeMargin = 8.dp
private val TextViewPlatformStyle = PlatformTextStyle(includeFontPadding = true)
private val EditTextPlatformStyle = PlatformTextStyle(includeFontPadding = false)

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    foldableState: FoldableState,
    onCompactScroll: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val screenWidth = this.maxWidth
        val screenHeight = this.maxHeight

        if (screenWidth >= LargeScreenMinWidth) {
            val density = LocalDensity.current
            val widthPx = with(density) { screenWidth.toPx() }
            val hingeSafeMarginPx = with(density) { HingeSafeMargin.toPx() }
            val paneWeights = foldableState.toPaneWeights(widthPx, hingeSafeMarginPx)
            LargeLoginContent(
                viewModel = viewModel,
                brandWeight = paneWeights.brand,
                hingeWeight = paneWeights.hinge,
                formWeight = paneWeights.form
            )
        } else {
            CompactLoginContent(
                viewModel = viewModel,
                headerHeight = screenHeight * HeaderRatio,
                onCompactScroll = onCompactScroll
            )
        }
    }
}

@Composable
private fun CompactLoginContent(
    viewModel: LoginViewModel,
    headerHeight: Dp,
    onCompactScroll: (Int) -> Unit
) {
    val scrollState = rememberScrollState()
    val horizontalPadding = dimensionResource(ResR.dimen.quick_Sp7_24)
    val verticalPadding = dimensionResource(ResR.dimen.quick_Sp5_16)

    LaunchedEffect(scrollState.value) {
        onCompactScroll(scrollState.value)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        LoginBrandPanel(
            modifier = Modifier
                .fillMaxWidth()
                .height(headerHeight),
            showCopyright = false,
            contentVerticalBias = 0.55f
        )

        LoginForm(
            viewModel = viewModel,
            titleSize = textSizeResource(ResR.dimen.quick_T3_18),
            subtitleSize = textSizeResource(ResR.dimen.quick_T6_12),
            optionTextSize = textSizeResource(ResR.dimen.quick_T6_12),
            buttonTextSize = textSizeResource(ResR.dimen.quick_T4_16),
            agreementTextSize = textSizeResource(ResR.dimen.quick_T7_10),
            registerTextSize = textSizeResource(ResR.dimen.quick_T6_12),
            subtitleTopPadding = 0.dp,
            phoneTopPadding = dimensionResource(ResR.dimen.quick_Sp6_20),
            optionsTopPadding = 0.dp,
            buttonTopPadding = dimensionResource(ResR.dimen.quick_Sp7_24),
            agreementTopPadding = dimensionResource(ResR.dimen.quick_Sp4_12),
            agreementHorizontalPadding = 0.dp,
            registerTopPadding = dimensionResource(ResR.dimen.quick_Sp1_4),
            actionHorizontalPadding = dimensionResource(ResR.dimen.quick_Sp1_4),
            showRegisterEntry = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = horizontalPadding, vertical = verticalPadding)
        )
    }
}

@Composable
private fun LargeLoginContent(
    viewModel: LoginViewModel,
    brandWeight: Float,
    hingeWeight: Float,
    formWeight: Float
) {
    val formPanelHorizontalPadding = dimensionResource(ResR.dimen.quick_Sp8_28)
    val formMaxWidth = dimensionResource(ResR.dimen.quick_Sp80_400)
    val bottomPadding = dimensionResource(ResR.dimen.quick_Sp5_16)

    Row(modifier = Modifier.fillMaxSize()) {
        LoginBrandPanel(
            modifier = Modifier
                .fillMaxHeight()
                .weight(brandWeight),
            showCopyright = true,
            contentVerticalBias = 0.45f
        )

        if (hingeWeight > 0f) {
            Spacer(modifier = Modifier.weight(hingeWeight))
        }

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxHeight()
                .weight(formWeight)
                .padding(horizontal = formPanelHorizontalPadding),
        ) {
            var formHeightPx by rememberSaveable { mutableStateOf(0) }
            val freeHeightPx = (constraints.maxHeight - formHeightPx).coerceAtLeast(0)

            LoginForm(
                viewModel = viewModel,
                titleSize = textSizeResource(ResR.dimen.quick_T2_24),
                subtitleSize = textSizeResource(ResR.dimen.quick_T5_14),
                optionTextSize = textSizeResource(ResR.dimen.quick_T5_14),
                buttonTextSize = textSizeResource(ResR.dimen.quick_T3_18),
                agreementTextSize = textSizeResource(ResR.dimen.quick_T6_12),
                registerTextSize = textSizeResource(ResR.dimen.quick_T5_14),
                subtitleTopPadding = dimensionResource(ResR.dimen.quick_Sp2_8),
                phoneTopPadding = dimensionResource(ResR.dimen.quick_Sp5_16),
                optionsTopPadding = dimensionResource(ResR.dimen.quick_Sp3_10),
                buttonTopPadding = dimensionResource(ResR.dimen.quick_Sp5_16),
                agreementTopPadding = dimensionResource(ResR.dimen.quick_Sp3_10),
                agreementHorizontalPadding = dimensionResource(ResR.dimen.quick_Sp1_4),
                registerTopPadding = 0.dp,
                actionHorizontalPadding = dimensionResource(ResR.dimen.quick_Sp2_8),
                showRegisterEntry = false,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset { IntOffset(0, (freeHeightPx * 0.442f).roundToInt()) }
                    .widthIn(max = formMaxWidth)
                    .onSizeChanged { formHeightPx = it.height }
            )

            RegisterEntry(
                textSize = textSizeResource(ResR.dimen.quick_T5_14),
                horizontalPadding = dimensionResource(ResR.dimen.quick_Sp2_8),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = bottomPadding)
            )
        }
    }
}

private data class PaneWeights(
    val brand: Float,
    val hinge: Float,
    val form: Float
)

private fun FoldableState.toPaneWeights(
    screenWidthPx: Float,
    safeMarginPx: Float
): PaneWeights {
    if (screenWidthPx <= 0f) return PaneWeights(0.45f, 0f, 0.55f)

    val bounds = when (this) {
        is FoldableState.Flat -> {
            if (orientation != FoldableState.Orientation.VERTICAL) return PaneWeights(0.45f, 0f, 0.55f)
            hingeBounds ?: return PaneWeights(0.45f, 0f, 0.55f)
        }

        is FoldableState.HalfOpened -> {
            if (orientation != FoldableState.Orientation.VERTICAL) return PaneWeights(0.45f, 0f, 0.55f)
            hingeBounds
        }

        else -> return PaneWeights(0.45f, 0f, 0.55f)
    }

    val start = ((bounds.left - safeMarginPx) / screenWidthPx).coerceIn(0.2f, 0.8f)
    val end = ((bounds.right + safeMarginPx) / screenWidthPx).coerceIn(0.2f, 0.8f)
    return PaneWeights(
        brand = start.coerceAtLeast(0.01f),
        hinge = (end - start).coerceAtLeast(0f),
        form = (1f - end).coerceAtLeast(0.01f)
    )
}

@Composable
private fun LoginBrandPanel(
    showCopyright: Boolean,
    contentVerticalBias: Float,
    modifier: Modifier = Modifier
) {
    val headerStart = colorResource(ResR.color.quick_S5_247BFF)
    val headerEnd = colorResource(ResR.color.quick_S7_0740B3)
    val logoSize = dimensionResource(ResR.dimen.quick_Sp15_56)
    val logoIconSize = 28.dp
    val brandTopPadding = dimensionResource(ResR.dimen.quick_Sp4_12)
    val compactSubtitleTopPadding = dimensionResource(ResR.dimen.quick_Sp1_4)
    val largeSubtitleTopPadding = dimensionResource(ResR.dimen.quick_Sp2_8)
    val copyrightBottomPadding = dimensionResource(ResR.dimen.quick_Sp5_16)
    val horizontalPadding = dimensionResource(ResR.dimen.quick_Sp7_24)

    BoxWithConstraints(
        modifier = modifier.drawWithCache {
            val brush = Brush.linearGradient(
                colors = listOf(headerStart, headerEnd),
                start = Offset(size.width, size.height),
                end = Offset.Zero
            )
            onDrawBehind { drawRect(brush) }
        }
    ) {
        var contentHeightPx by rememberSaveable { mutableStateOf(0) }
        var copyrightHeightPx by rememberSaveable { mutableStateOf(0) }
        val copyrightReservedPx = if (showCopyright) {
            copyrightHeightPx
        } else {
            0
        }
        val freeHeightPx = (constraints.maxHeight - copyrightReservedPx - contentHeightPx).coerceAtLeast(0)

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset { IntOffset(0, (freeHeightPx * contentVerticalBias).roundToInt()) }
                .onSizeChanged { contentHeightPx = it.height }
                .padding(horizontal = horizontalPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(logoSize)
                    .clip(CircleShape)
                    .background(colorResource(ResR.color.quick_M10_FFFFFF)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(ResR.drawable.quick_ic_login_logo),
                    contentDescription = stringResource(R.string.login_app_name),
                    modifier = Modifier.size(logoIconSize),
                    colorFilter = ColorFilter.tint(colorResource(ResR.color.quick_S5_247BFF))
                )
            }

            Text(
                text = stringResource(R.string.login_app_name),
                color = Color.White,
                fontSize = textSizeResource(ResR.dimen.quick_T2_24),
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.05.em,
                style = TextStyle(platformStyle = TextViewPlatformStyle),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = brandTopPadding)
            )

            Text(
                text = stringResource(R.string.login_subtitle),
                color = colorResource(ResR.color.quick_white_70alpha),
                fontSize = textSizeResource(ResR.dimen.quick_T6_12),
                letterSpacing = 0.08.em,
                style = TextStyle(platformStyle = TextViewPlatformStyle),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(
                    top = if (showCopyright) largeSubtitleTopPadding else compactSubtitleTopPadding
                )
            )
        }

        if (showCopyright) {
            Text(
                text = stringResource(R.string.login_brand_copyright),
                color = colorResource(ResR.color.quick_white_50alpha),
                fontSize = textSizeResource(ResR.dimen.quick_T6_12),
                letterSpacing = 0.05.em,
                style = TextStyle(platformStyle = TextViewPlatformStyle),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .onSizeChanged { copyrightHeightPx = it.height }
                    .padding(bottom = copyrightBottomPadding)
            )
        }
    }
}

@Composable
private fun LoginForm(
    viewModel: LoginViewModel,
    titleSize: TextUnit,
    subtitleSize: TextUnit,
    optionTextSize: TextUnit,
    buttonTextSize: TextUnit,
    agreementTextSize: TextUnit,
    registerTextSize: TextUnit,
    subtitleTopPadding: Dp,
    phoneTopPadding: Dp,
    optionsTopPadding: Dp,
    buttonTopPadding: Dp,
    agreementTopPadding: Dp,
    agreementHorizontalPadding: Dp,
    registerTopPadding: Dp,
    actionHorizontalPadding: Dp,
    showRegisterEntry: Boolean,
    modifier: Modifier = Modifier
) {
    val phone by viewModel.phone.collectAsState()
    val password by viewModel.password.collectAsState()
    val rememberMe by viewModel.rememberMe.collectAsState()
    val agreedToTerms by viewModel.agreedToTerms.collectAsState()
    val phoneError by viewModel.phoneError.collectAsState()
    val passwordError by viewModel.passwordError.collectAsState()
    val isFormValid by viewModel.isFormValid.collectAsState()
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var phoneHasFocused by rememberSaveable { mutableStateOf(false) }
    var passwordHasFocused by rememberSaveable { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.login_welcome_back),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = titleSize,
            fontWeight = FontWeight.Bold,
            style = TextStyle(platformStyle = TextViewPlatformStyle),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = stringResource(R.string.login_form_subtitle),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = subtitleSize,
            style = TextStyle(platformStyle = TextViewPlatformStyle),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = subtitleTopPadding)
        )

        LoginOutlinedTextField(
            value = phone,
            onValueChange = {
                viewModel.phone.value = it.take(11)
                viewModel.clearPhoneError()
            },
            label = stringResource(R.string.login_phone_hint),
            prefix = stringResource(R.string.login_phone_prefix),
            error = phoneError,
            textSize = textSizeResource(ResR.dimen.quick_T5_14),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next
            ),
            trailingIcon = {
                if (phone.isNotEmpty()) {
                    IconButton(onClick = {
                        viewModel.phone.value = ""
                        viewModel.clearPhoneError()
                    }) {
                        Icon(
                            painter = painterResource(ResR.mipmap.res_icon_clear),
                            contentDescription = "清除手机号",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = phoneTopPadding)
                .onFocusChanged {
                    if (it.isFocused) {
                        phoneHasFocused = true
                    } else if (phoneHasFocused) {
                        viewModel.validatePhone()
                    }
                }
        )

        LoginOutlinedTextField(
            value = password,
            onValueChange = {
                viewModel.password.value = it
                viewModel.clearPasswordError()
            },
            label = stringResource(R.string.login_password_hint),
            error = passwordError,
            textSize = textSizeResource(ResR.dimen.quick_T5_14),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { viewModel.login() }),
            visualTransformation = if (passwordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        painter = painterResource(
                            if (passwordVisible) {
                                ResR.drawable.quick_ic_visibility
                            } else {
                                ResR.drawable.quick_ic_visibility_off
                            }
                        ),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimensionResource(ResR.dimen.quick_Sp4_12))
                .onFocusChanged {
                    if (it.isFocused) {
                        passwordHasFocused = true
                    } else if (passwordHasFocused) {
                        viewModel.validatePassword()
                    }
                }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = optionsTopPadding)
                .heightIn(min = 48.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .heightIn(min = 48.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = rememberMe,
                    onCheckedChange = { viewModel.rememberMe.value = it }
                )
                Text(
                    text = stringResource(R.string.login_remember_me),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = optionTextSize,
                    style = TextStyle(platformStyle = TextViewPlatformStyle)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = stringResource(R.string.login_forgot_password),
                color = MaterialTheme.colorScheme.primary,
                fontSize = optionTextSize,
                style = TextStyle(platformStyle = TextViewPlatformStyle),
                modifier = Modifier
                    .clickable { }
                    .padding(horizontal = actionHorizontalPadding, vertical = dimensionResource(ResR.dimen.quick_Sp1_4))
            )
        }

        Spacer(modifier = Modifier.height(buttonTopPadding))

        Button(
            onClick = { viewModel.login() },
            enabled = isFormValid,
            shape = RoundedCornerShape(dimensionResource(ResR.dimen.quick_Ra4_12)),
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(ResR.dimen.quick_Sp13_48))
        ) {
            Text(
                text = stringResource(R.string.login_button),
                fontSize = buttonTextSize,
                fontWeight = FontWeight.Bold,
                style = TextStyle(platformStyle = TextViewPlatformStyle),
                letterSpacing = 0.12.em
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = agreementTopPadding)
                .padding(horizontal = agreementHorizontalPadding),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = agreedToTerms,
                onCheckedChange = { viewModel.agreedToTerms.value = it },
                modifier = Modifier.size(40.dp)
            )
            Text(
                text = stringResource(R.string.login_agreement_prefix),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = agreementTextSize,
                style = TextStyle(platformStyle = TextViewPlatformStyle)
            )
            Text(
                text = stringResource(R.string.login_user_agreement),
                color = MaterialTheme.colorScheme.primary,
                fontSize = agreementTextSize,
                style = TextStyle(platformStyle = TextViewPlatformStyle),
                modifier = Modifier.clickable { }
            )
        }

        if (showRegisterEntry) {
            RegisterEntry(
                textSize = registerTextSize,
                horizontalPadding = dimensionResource(ResR.dimen.quick_Sp1_4),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = registerTopPadding)
            )
        }
    }
}

@Composable
private fun RegisterEntry(
    textSize: TextUnit,
    horizontalPadding: Dp,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.login_no_account),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = textSize,
            style = TextStyle(platformStyle = TextViewPlatformStyle)
        )
        Text(
            text = stringResource(R.string.login_register_now),
            color = MaterialTheme.colorScheme.primary,
            fontSize = textSize,
            fontWeight = FontWeight.Bold,
            style = TextStyle(platformStyle = TextViewPlatformStyle),
            modifier = Modifier
                .clickable { }
                .padding(horizontal = horizontalPadding, vertical = dimensionResource(ResR.dimen.quick_Sp1_4))
        )
    }
}

@Composable
private fun LoginOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String?,
    textSize: TextUnit,
    modifier: Modifier = Modifier,
    prefix: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = label,
                color = if (error != null) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                fontSize = textSize,
                style = TextStyle(platformStyle = TextViewPlatformStyle)
            )
        },
        prefix = prefix?.let {
            {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = textSize,
                    style = TextStyle(platformStyle = EditTextPlatformStyle)
                )
            }
        },
        isError = error != null,
        supportingText = error?.let { { Text(it) } },
        singleLine = true,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        trailingIcon = trailingIcon,
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = textSize,
            platformStyle = EditTextPlatformStyle
        ),
        shape = RoundedCornerShape(4.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = colorResource(ResR.color.quick_login_textfield_stroke),
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            cursorColor = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier
    )
}
