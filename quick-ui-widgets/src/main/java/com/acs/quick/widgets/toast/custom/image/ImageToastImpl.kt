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


package com.acs.quick.widgets.toast.custom.image

import android.content.Context
import android.content.res.Resources
import android.view.Gravity
import android.widget.Toast
import com.acs.quick.widgets.toast.config.Location
import com.acs.quick.widgets.toast.scheduler.ToastScheduler

/**
 * author： 马世鹏
 *
 * time: 2022/4/22
 *
 * desc:
 */
class ImageToastImpl : ImageToastFacade.ConfigBuilder, ImageToastFacade.Overall {

    private val config = ImageToast.Config()

    override fun config(): ImageToastFacade.ConfigBuilder = this

    override fun apply(): ImageToastApi = this

    override fun backgroundColor(color: Int): ImageToastFacade.ConfigBuilder =
        this.apply {
            config.backgroundColor = color
        }

    override fun iconResource(res: Int): ImageToastFacade.ConfigBuilder =
        this.apply {
            config.iconResource = res
        }

    override fun contentColor(color: Int): ImageToastFacade.ConfigBuilder =
        this.apply {
            config.contentColor = color
        }

    override fun info(context: Context, msg: CharSequence) {
        showHelper(context, TYPE_INFO, msg)
    }

    override fun info(context: Context, msg: Int) {
        showHelper(context, TYPE_INFO, msg.resourceToString(context))
    }

    override fun warning(context: Context, msg: CharSequence) {
        showHelper(context, TYPE_WARNING, msg)
    }

    override fun warning(context: Context, msg: Int) {
        showHelper(context, TYPE_WARNING, msg.resourceToString(context))
    }

    override fun complete(context: Context, msg: CharSequence) {
        showHelper(context, TYPE_COMPLETE, msg)
    }

    override fun complete(context: Context, msg: Int) {
        showHelper(context, TYPE_COMPLETE, msg.resourceToString(context))
    }

    override fun failed(context: Context, msg: CharSequence) {
        showHelper(context, TYPE_FAILED, msg)
    }

    override fun failed(context: Context, msg: Int) {
        showHelper(context, TYPE_FAILED, msg.resourceToString(context))
    }

    private fun showHelper(
        context: Context,
        @Type type: Int,
        content: CharSequence,
        duration: Int = Toast.LENGTH_SHORT
    ) {
        config.type = type
        config.content = content
        config.duration = duration
        config.location = Location(Gravity.CENTER, 0, 0)
        ToastScheduler.schedule(context, config, ImageToastFactory)
    }

    private fun Int.resourceToString(context: Context): String = try {
        context.getString(this)
    } catch (ex: Resources.NotFoundException) {
        ""
    }
}
