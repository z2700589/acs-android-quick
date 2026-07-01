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


package com.acs.quick.common.api

import com.acs.quick.common.config.UrlConfig
import com.acs.quick.common.data.response.BaseResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit API 接口定义。
 *
 * @author Zhai Jie
 */
interface NetService {

    /** 上传文件 */
    @FormUrlEncoded
    @POST("api.php/Upload/carrierPicUpForRn")
    @Headers(UrlConfig.URL_TYPE_MAIN)
    suspend fun uploadFiles(
        @FieldMap fieldMap: Map<String, String>
    ): BaseResponse<MutableList<String>>


    /** 通过 id 下载文件 */
    @FormUrlEncoded
    @POST("api.php/OfficeNew/downloadQualityMulti")
    @Headers(UrlConfig.URL_TYPE_MAIN)
    @Streaming
    suspend fun downloadFile(@Field("id") id: String): Response<ResponseBody>

    /** 通过 URL 下载文件 */
    @GET
    @Streaming
    suspend fun downloadFileFormUrl(@Url url: String): Response<ResponseBody>
}
