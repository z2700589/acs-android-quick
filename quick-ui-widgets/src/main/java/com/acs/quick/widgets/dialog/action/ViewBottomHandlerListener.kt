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


package com.acs.quick.widgets.dialog.action

import android.os.Parcel
import android.os.Parcelable
import com.acs.quick.widgets.dialog.base.TisBaseBottomSheetDialog
import com.acs.quick.widgets.dialog.base.ViewHolder

/**
 * @author 翟杰
 * @Description ViewHandlerListener
 * @Date 2022/5/26 11:07
 */
abstract class ViewBottomHandlerListener() : Parcelable {

    constructor(parcel: Parcel) : this()

    abstract fun convertView(holder: ViewHolder, dialog: TisBaseBottomSheetDialog<*>)

    override fun writeToParcel(parcel: Parcel, flags: Int) {}

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField
        var CREATOR: Parcelable.Creator<ViewBottomHandlerListener> = object : Parcelable.Creator<ViewBottomHandlerListener> {
            override fun createFromParcel(parcel: Parcel): ViewBottomHandlerListener {
                return object : ViewBottomHandlerListener(parcel) {
                    override fun convertView(holder: ViewHolder, dialog: TisBaseBottomSheetDialog<*>) {
                    }
                }
            }

            override fun newArray(size: Int): Array<ViewBottomHandlerListener?> {
                return arrayOfNulls(size)
            }
        }

    }
}
