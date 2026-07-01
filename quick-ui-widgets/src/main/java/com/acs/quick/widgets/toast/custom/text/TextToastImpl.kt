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

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import android.view.Gravity
import android.widget.Toast
import com.acs.quick.widgets.toast.config.Location
import com.acs.quick.widgets.toast.scheduler.ToastScheduler
import kotlin.math.roundToInt

/**
 * author： 马世鹏
 *
 * time: 2022/4/22
 *
 * desc:
 */
class TextToastImpl : TextToastFacade.ConfigBuilder, TextToastFacade.Overall {

    private val config = TextToast.Config()

    override fun config(): TextToastFacade.ConfigBuilder = this

    override fun show(context: Context, msg: CharSequence) {
        showHelper(context, msg)
    }

    override fun show(context: Context, msg: Int) {
        showHelper(context, msg.resourceToString(context))
    }

    override fun showAtTop(context: Context, msg: CharSequence) {
        showHelper(
            context,
            msg,
            location = Location(
                Gravity.TOP or Gravity.CENTER_HORIZONTAL,
                0,
                context.yOffset()
            )
        )

    }

    override fun showAtTop(context: Context, msg: Int) {
        showHelper(
            context,
            msg.resourceToString(context),
            location = Location(
                Gravity.TOP or Gravity.CENTER_HORIZONTAL,
                0,
                context.yOffset()
            )
        )
    }

    override fun showAtBottom(context: Context, msg: CharSequence) {
        showHelper(
            context,
            msg,
            location = Location(
                Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL,
                0,
                context.yOffset()
            )
        )
    }

    override fun showAtBottom(context: Context, msg: Int) {
        showHelper(
            context,
            msg.resourceToString(context),
            location = Location(
                Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL,
                0,
                context.yOffset()
            )
        )
    }

    override fun showAtLocation(
        context: Context,
        msg: CharSequence,
        gravity: Int,
        xOffeseDp: Float,
        yOffsetDp: Float
    ) {
        showHelper(
            context,
            msg,
            location = Location(
                gravity,
                xOffeseDp.dpToPx(context),
                yOffsetDp.dpToPx(context)
            )
        )
    }

    override fun showAtLocation(context: Context, msg: Int, gravity: Int, xOffeseDp: Float, yOffsetDp: Float) {
        showHelper(
            context,
            msg.resourceToString(context),
            location = Location(
                gravity,
                xOffeseDp.dpToPx(context),
                yOffsetDp.dpToPx(context)
            )
        )
    }

    override fun showLong(context: Context, msg: CharSequence) {
        showHelper(context, msg, Toast.LENGTH_LONG)
    }

    override fun showLong(context: Context, msg: Int) {
        showHelper(context, msg.resourceToString(context), Toast.LENGTH_LONG)
    }

    override fun showAtTopLong(context: Context, msg: CharSequence) {
        showHelper(
            context,
            msg,
            Toast.LENGTH_LONG,
            Location(
                Gravity.TOP or Gravity.CENTER_HORIZONTAL,
                0,
                context.yOffset()
            )
        )
    }

    override fun showAtTopLong(context: Context, msg: Int) {
        showHelper(
            context,
            msg.resourceToString(context),
            Toast.LENGTH_LONG,
            Location(
                Gravity.TOP or Gravity.CENTER_HORIZONTAL,
                0,
                context.yOffset()
            )
        )
    }

    override fun showAtBottomLong(context: Context, msg: CharSequence) {
        showHelper(
            context,
            msg,
            Toast.LENGTH_LONG,
            Location(
                Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL,
                0,
                context.yOffset()
            )
        )
    }

    override fun showAtBottomLong(context: Context, msg: Int) {
        showHelper(
            context,
            msg.resourceToString(context),
            Toast.LENGTH_LONG,
            Location(
                Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL,
                0,
                context.yOffset()
            )
        )
    }

    override fun showAtLocationLong(
        context: Context,
        msg: CharSequence,
        gravity: Int,
        xOffeseDp: Float,
        yOffsetDp: Float
    ) {
        showHelper(
            context,
            msg,
            Toast.LENGTH_LONG,
            location = Location(
                gravity,
                xOffeseDp.dpToPx(context),
                yOffsetDp.dpToPx(context)
            )
        )
    }

    override fun showAtLocationLong(
        context: Context,
        msg: Int,
        gravity: Int,
        xOffeseDp: Float,
        yOffsetDp: Float
    ) {
        showHelper(
            context,
            msg.resourceToString(context),
            Toast.LENGTH_LONG,
            location = Location(
                gravity,
                xOffeseDp.dpToPx(context),
                yOffsetDp.dpToPx(context)
            )
        )
    }

    override fun apply(): TextToastApi = this

    override fun backgroundColor(color: Int): TextToastFacade.ConfigBuilder =
        this.apply {
            config.backgroundColor = color
        }


    override fun contentColor(color: Int): TextToastFacade.ConfigBuilder =
        this.apply {
            config.contentColor = color
        }

    override fun contentSize(sizeSp: Float): TextToastFacade.ConfigBuilder =
        this.apply {
            config.contentSize = sizeSp
        }

    private fun showHelper(
        context: Context,
        content: CharSequence,
        duration: Int = Toast.LENGTH_SHORT,
        location: Location = Location(Gravity.CENTER, 0, 0)
    ) {
        config.content = content
        config.duration = duration
        config.location = location
        ToastScheduler.schedule(context, config, TextToastFactory)
    }

    private fun Int.resourceToString(context: Context): String = try {
        context.getString(this)
    } catch (ex: Resources.NotFoundException) {
        ""
    }

    private fun Context.yOffset() = Toast(this).yOffset

    private fun Float.dpToPx(context: Context): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        context.resources.displayMetrics,
    ).roundToInt()
}
