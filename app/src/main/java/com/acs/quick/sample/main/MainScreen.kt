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

package com.acs.quick.sample.main

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.acs.quick.sample.ui.colorAttrResource
import com.acs.quick.sample.ui.textSizeResource
import com.acs.quick.res.R as ResR
import com.acs.quick.sample.R

private val TextViewPlatformStyle = PlatformTextStyle(includeFontPadding = true)

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val orientation = LocalConfiguration.current.orientation

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(ResR.color.quick_M8_F0F2F5))
    ) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            LandscapeMainContent()
        } else {
            PortraitMainContent()
        }
    }
}

@Composable
private fun PortraitMainContent() {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val pageHorizontalPadding = dimensionResource(ResR.dimen.quick_Sp7_24)
        val topPadding = dimensionResource(ResR.dimen.quick_Sp8_28)
        val headerBottomGap = dimensionResource(ResR.dimen.quick_Sp7_24)
        val cardGap = dimensionResource(ResR.dimen.quick_Sp5_16)
        val cardHalfGap = dimensionResource(ResR.dimen.quick_Sp2_8)
        val bottomPadding = dimensionResource(ResR.dimen.quick_Sp7_24)
        val cardTopCorrection = with(density) { 3.toDp() }
        val centerLineY = maxHeight * 0.57f
        var headerHeight by remember { mutableStateOf(0.dp) }

        MainHeader(
            titleWidth = maxWidth - pageHorizontalPadding * 2,
            subtitleWidth = maxWidth - pageHorizontalPadding * 2,
            modifier = Modifier
                .offset(x = pageHorizontalPadding, y = topPadding)
                .onSizeChanged {
                    headerHeight = with(density) { it.height.toDp() }
                }
        )

        val cardTop = topPadding + headerHeight + headerBottomGap + cardTopCorrection
        val gridWidth = maxWidth - pageHorizontalPadding * 2
        val cardWidth = ((gridWidth - cardGap) / 2).coerceAtLeast(0.dp)
        val topCardHeight = (centerLineY - cardHalfGap - cardTop).coerceAtLeast(0.dp)
        val bottomCardTop = centerLineY + cardHalfGap
        val bottomCardHeight = (maxHeight - bottomPadding - bottomCardTop).coerceAtLeast(0.dp)
        val rightCardStart = pageHorizontalPadding + cardWidth + cardGap

        MainCardGrid(
            firstColumnX = pageHorizontalPadding,
            secondColumnX = rightCardStart,
            firstRowY = cardTop,
            secondRowY = bottomCardTop,
            cardWidth = cardWidth,
            firstRowHeight = topCardHeight,
            secondRowHeight = bottomCardHeight
        )
    }
}

@Composable
private fun LandscapeMainContent() {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val startPadding = dimensionResource(ResR.dimen.quick_Sp7_24)
        val topPadding = dimensionResource(ResR.dimen.quick_Sp5_16)
        val endPadding = dimensionResource(ResR.dimen.quick_Sp7_24)
        val sideGap = dimensionResource(ResR.dimen.quick_Sp5_16)
        val cardGap = dimensionResource(ResR.dimen.quick_Sp5_16)
        val cardHalfGap = dimensionResource(ResR.dimen.quick_Sp2_8)
        val midLineX = maxWidth * 0.35f
        val centerLineY = maxHeight * 0.52f
        val titleWidth = (midLineX - startPadding).coerceAtLeast(0.dp)
        val subtitleWidth = (midLineX - startPadding - cardHalfGap).coerceAtLeast(0.dp)
        val gridStart = midLineX + sideGap
        val gridEnd = maxWidth - endPadding
        val cardWidth = ((gridEnd - gridStart - cardGap) / 2).coerceAtLeast(0.dp)
        val topCardHeight = (centerLineY - cardHalfGap - topPadding).coerceAtLeast(0.dp)
        val bottomCardTop = centerLineY + cardHalfGap
        val bottomCardHeight = (maxHeight - topPadding - bottomCardTop).coerceAtLeast(0.dp)

        MainHeader(
            titleWidth = titleWidth,
            subtitleWidth = subtitleWidth,
            modifier = Modifier
                .offset(x = startPadding, y = topPadding)
        )

        MainCardGrid(
            firstColumnX = gridStart,
            secondColumnX = gridStart + cardWidth + cardGap,
            firstRowY = topPadding,
            secondRowY = bottomCardTop,
            cardWidth = cardWidth,
            firstRowHeight = topCardHeight,
            secondRowHeight = bottomCardHeight
        )
    }
}

@Composable
private fun MainHeader(
    modifier: Modifier = Modifier,
    titleWidth: Dp? = null,
    subtitleWidth: Dp? = null
) {
    val primaryTextColor = colorAttrResource(android.R.attr.textColorPrimary)
    val secondaryTextColor = colorAttrResource(android.R.attr.textColorSecondary)

    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.main_welcome),
            color = primaryTextColor,
            fontSize = textSizeResource(ResR.dimen.quick_T2_24),
            fontWeight = FontWeight.Bold,
            style = TextStyle(platformStyle = TextViewPlatformStyle),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = titleWidth?.let { Modifier.width(it) } ?: Modifier.fillMaxWidth()
        )
        Text(
            text = stringResource(R.string.main_welcome_sub),
            color = secondaryTextColor,
            fontSize = textSizeResource(ResR.dimen.quick_T5_14),
            style = TextStyle(platformStyle = TextViewPlatformStyle),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = (subtitleWidth?.let { Modifier.width(it) } ?: Modifier.fillMaxWidth())
                .padding(top = dimensionResource(ResR.dimen.quick_Sp1_4))
        )
    }
}

@Composable
private fun MainCardGrid(
    firstColumnX: Dp,
    secondColumnX: Dp,
    firstRowY: Dp,
    secondRowY: Dp,
    cardWidth: Dp,
    firstRowHeight: Dp,
    secondRowHeight: Dp
) {
    MainFeatureCard(
        title = stringResource(R.string.main_card_project),
        subtitle = stringResource(R.string.main_card_project_sub),
        tint = colorResource(ResR.color.quick_S5_247BFF),
        modifier = Modifier
            .offset(x = firstColumnX, y = firstRowY)
            .size(width = cardWidth, height = firstRowHeight)
    )
    MainFeatureCard(
        title = stringResource(R.string.main_card_customer),
        subtitle = stringResource(R.string.main_card_customer_sub),
        tint = colorResource(ResR.color.quick_green5_00BF8F),
        modifier = Modifier
            .offset(x = secondColumnX, y = firstRowY)
            .size(width = cardWidth, height = firstRowHeight)
    )
    MainFeatureCard(
        title = stringResource(R.string.main_card_analytics),
        subtitle = stringResource(R.string.main_card_analytics_sub),
        tint = colorResource(ResR.color.quick_purple5_6B61FF),
        modifier = Modifier
            .offset(x = firstColumnX, y = secondRowY)
            .size(width = cardWidth, height = secondRowHeight)
    )
    MainFeatureCard(
        title = stringResource(R.string.main_card_profile),
        subtitle = stringResource(R.string.main_card_profile_sub),
        tint = colorResource(ResR.color.quick_orange4_FFAA00),
        modifier = Modifier
            .offset(x = secondColumnX, y = secondRowY)
            .size(width = cardWidth, height = secondRowHeight)
    )
}

@Composable
private fun MainFeatureCard(
    title: String,
    subtitle: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    val primaryTextColor = colorAttrResource(android.R.attr.textColorPrimary)
    val secondaryTextColor = colorAttrResource(android.R.attr.textColorSecondary)
    val cardRadius = dimensionResource(ResR.dimen.quick_Ra5_16)
    val iconContainerSize = 36.dp
    val iconPadding = 9.dp
    val titleTopPadding = dimensionResource(ResR.dimen.quick_Sp2_8)
    val textHorizontalPadding = dimensionResource(ResR.dimen.quick_Sp2_8)

    Card(
        modifier = modifier.clickable { },
        shape = RoundedCornerShape(cardRadius),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(ResR.color.quick_M10_FFFFFF)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .requiredSize(iconContainerSize)
                    .clip(CircleShape)
                    .background(colorResource(ResR.color.quick_S1_EBF6FF))
                    .padding(iconPadding),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(ResR.drawable.quick_ic_login_logo),
                    contentDescription = title,
                    colorFilter = ColorFilter.tint(tint),
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(titleTopPadding))

            Text(
                text = title,
                color = primaryTextColor,
                fontSize = textSizeResource(ResR.dimen.quick_T5_14),
                fontWeight = FontWeight.Bold,
                style = TextStyle(platformStyle = TextViewPlatformStyle),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = textHorizontalPadding)
            )

            Text(
                text = subtitle,
                color = secondaryTextColor,
                fontSize = textSizeResource(ResR.dimen.quick_T7_10),
                style = TextStyle(platformStyle = TextViewPlatformStyle),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = textHorizontalPadding)
            )
        }
    }
}
