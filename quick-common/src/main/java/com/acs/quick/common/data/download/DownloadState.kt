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


package com.acs.quick.common.data.download

import java.io.File

/** 下载状态 */
sealed class DownloadState {
    /** 下载中，progress 为百分比 */
    data class InProgress(val progress: Int, val bytesCopied: Long, val total: Long) : DownloadState()
    /** 下载完成 */
    data class Success(val file: File) : DownloadState()
    /** 下载失败 */
    data class Error(val throwable: Throwable) : DownloadState()
}
