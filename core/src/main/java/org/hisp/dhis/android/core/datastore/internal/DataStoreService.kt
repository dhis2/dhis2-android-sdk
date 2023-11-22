/*
 *  Copyright (c) 2004-2023, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.android.core.datastore.internal

import org.hisp.dhis.android.core.imports.internal.HttpMessageResponse
import org.koin.core.annotation.Singleton
import retrofit2.Retrofit
import retrofit2.http.*

internal interface DataStoreService {
    @GET(DATA_STORE)
    suspend fun getNamespaces(): List<String>

    @GET("$DATA_STORE/{$NAMESPACE}")
    suspend fun getNamespaceKeys(
        @Path(NAMESPACE) namespace: String,
    ): List<String>

    @GET("$DATA_STORE/{$NAMESPACE}")
    suspend fun getNamespaceValues38(
        @Path(NAMESPACE) namespace: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Query("fields") fields: String = ".",
    ): DataStorePagedEntry

    @GET("$DATA_STORE/{$NAMESPACE}/{$KEY}")
    suspend fun getNamespaceKeyValue(
        @Path(NAMESPACE) namespace: String,
        @Path(KEY) key: String,
    ): Any

    @POST("$DATA_STORE/{$NAMESPACE}/{$KEY}")
    suspend fun postNamespaceKeyValue(
        @Path(NAMESPACE) namespace: String,
        @Path(KEY) key: String,
        @Body value: Any?,
    ): HttpMessageResponse

    @PUT("$DATA_STORE/{$NAMESPACE}/{$KEY}")
    suspend fun putNamespaceKeyValue(
        @Path(NAMESPACE) namespace: String,
        @Path(KEY) key: String,
        @Body value: Any?,
    ): HttpMessageResponse

    @DELETE("$DATA_STORE/{$NAMESPACE}/{$KEY}")
    suspend fun deleteNamespaceKeyValue(
        @Path(NAMESPACE) namespace: String,
        @Path(KEY) key: String,
    ): HttpMessageResponse

    companion object {
        private const val DATA_STORE = "dataStore"
        private const val NAMESPACE = "namespace"
        private const val KEY = "key"
    }
}

@Singleton
internal fun service(retrofit: Retrofit): DataStoreService = retrofit.create(DataStoreService::class.java)
