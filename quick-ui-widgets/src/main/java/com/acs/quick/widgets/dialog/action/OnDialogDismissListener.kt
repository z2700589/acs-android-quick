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

import android.content.DialogInterface
import android.os.Parcel
import android.os.Parcelable

/**
 * @author 翟杰
 * @Description OnDialogDismissListener
 * @Date 2022/5/26 11:06
 */
abstract class OnDialogDismissListener() : DialogInterface.OnDismissListener, Parcelable {

    constructor(parcel: Parcel) : this()

    override fun writeToParcel(parcel: Parcel, flags: Int) {}

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<OnDialogDismissListener> = object : Parcelable.Creator<OnDialogDismissListener> {
            override fun createFromParcel(source: Parcel): OnDialogDismissListener {
                return object : OnDialogDismissListener(source) {
                    override fun onDismiss(dialog: DialogInterface) {

                    }
                }
            }

            override fun newArray(size: Int): Array<OnDialogDismissListener?> {
                return arrayOfNulls(size)
            }
        }
    }
}
