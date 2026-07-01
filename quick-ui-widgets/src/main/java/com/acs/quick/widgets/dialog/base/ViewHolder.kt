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


package com.acs.quick.widgets.dialog.base

import android.util.SparseArray
import android.view.View
import android.widget.TextView

/**
 * @author 翟杰
 * @Description ViewHolder
 * @Date 2022/5/26 11:08
 */
class ViewHolder private constructor(private val convertView: View) {

    private val views: SparseArray<View> = SparseArray()

    @Suppress("UNCHECKED_CAST")
    fun <T : View> getView(viewId: Int): T {
        var view: View? = views.get(viewId)
        if (view == null) {
            view = convertView.findViewById(viewId)
            views.put(viewId, view)
        }
        return view as T
    }

    companion object {

        fun create(view: View): ViewHolder {
            return ViewHolder(view)
        }
    }
}

fun ViewHolder.setText(viewId: Int, textId: Int) {
    val textView = getView<TextView>(viewId)
    textView.setText(textId)
}

fun ViewHolder.setText(viewId: Int, text: CharSequence) {
    val textView = getView<TextView>(viewId)
    textView.text = text
}

fun ViewHolder.setTextColor(viewId: Int, colorId: Int) {
    val textView = getView<TextView>(viewId)
    textView.setTextColor(colorId)
}

fun ViewHolder.setOnClickListener(viewId: Int, clickListener: View.OnClickListener?) {
    val view = getView<View>(viewId)
    view.setOnClickListener(clickListener)
}

fun ViewHolder.setBackgroundResource(viewId: Int, resId: Int) {
    val view = getView<View>(viewId)
    view.setBackgroundResource(resId)
}

fun ViewHolder.setBackgroundColor(viewId: Int, colorId: Int) {
    val view = getView<View>(viewId)
    view.setBackgroundColor(colorId)
}
