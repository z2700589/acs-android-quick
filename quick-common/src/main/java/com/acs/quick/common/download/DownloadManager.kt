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


package com.acs.quick.common.download

import com.acs.quick.common.api.NetService
import com.acs.quick.common.data.download.DownloadState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.ResponseBody
import retrofit2.Retrofit
import java.io.File
import java.io.IOException

class DownloadManager(
    private val retrofit: Retrofit
) {

    /** 通过 id 下载文件 */
    suspend fun download(id: String, file: File): Flow<DownloadState> = flow {
        val response = retrofit.create(NetService::class.java).downloadFile(id)
        when {
            response.isSuccessful && response.body() != null -> {
                val responseBody = response.body() as ResponseBody
                if (responseBody.contentLength() != -1L) {
                    saveToFile(responseBody, file) { progress, bytesCopied, total ->
                        emit(DownloadState.InProgress(progress, bytesCopied, total))
                    }
                    delay(100L)
                    emit(DownloadState.Success(file))
                } else {
                    emit(DownloadState.Error(IOException(response.toString())))
                }
            }

            else -> emit(DownloadState.Error(IOException(response.toString())))
        }
    }.catch {
        emit(DownloadState.Error(it))
    }.flowOn(Dispatchers.IO)

    /** 通过 URL 下载文件 */
    suspend fun downloadFromUrl(url: String, file: File): Flow<DownloadState> = flow {
        val response = retrofit.create(NetService::class.java).downloadFileFormUrl(url)
        when {
            response.isSuccessful && response.body() != null -> {
                val responseBody = response.body() as ResponseBody
                if (responseBody.contentLength() != -1L) {
                    saveToFile(responseBody, file) { progress, bytesCopied, total ->
                        emit(DownloadState.InProgress(progress, bytesCopied, total))
                    }
                    delay(100L)
                    emit(DownloadState.Success(file))
                } else {
                    emit(DownloadState.Error(IOException(response.toString())))
                }
            }

            else -> emit(DownloadState.Error(IOException(response.toString())))
        }
    }.catch {
        emit(DownloadState.Error(it))
    }.flowOn(Dispatchers.IO)

    private inline fun saveToFile(responseBody: ResponseBody, file: File, progressListener: (Int, Long, Long) -> Unit) {
        val total = responseBody.contentLength()
        var bytesCopied = 0
        var emittedProgress = 0
        file.outputStream().use { output ->
            val input = responseBody.byteStream()
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            var bytes = input.read(buffer)
            while (bytes >= 0) {
                output.write(buffer, 0, bytes)
                bytesCopied += bytes
                bytes = input.read(buffer)
                val progress = (bytesCopied * 100 / total).toInt()
                if (progress - emittedProgress > 0) {
                    progressListener(progress, bytesCopied.toLong(), total)
                    emittedProgress = progress
                }
            }
        }
    }
}
