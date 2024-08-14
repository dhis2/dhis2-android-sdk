/*
 *  Copyright (c) 2004-2022, University of Oslo
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
package org.hisp.dhis.android.core.arch.api.testutils

import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.OkHttpClient
import okhttp3.OkHttpClient.Builder
import okhttp3.mockwebserver.MockWebServer
import org.hisp.dhis.android.core.arch.api.fields.internal.FieldsConverterFactory
import org.hisp.dhis.android.core.arch.api.filters.internal.FilterConverterFactory
import org.hisp.dhis.android.core.arch.api.internal.PreventURLDecodeInterceptor
import org.hisp.dhis.android.core.mockwebserver.Dhis2MockServer
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory

object RetrofitFactory {
    fun fromDHIS2MockServer(server: Dhis2MockServer): Retrofit {
        return fromServerUrl(server.baseEndpoint)
    }

    fun fromMockWebServer(mockWebServer: MockWebServer): Retrofit {
        return Retrofit.Builder()
            .client(okClient)
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(FieldsConverterFactory())
            .addConverterFactory(FilterConverterFactory())
            .build()
    }

    @JvmStatic
    fun fromServerUrl(serverUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(serverUrl)
            .addConverterFactory(JacksonConverterFactory.create(ObjectMapper()))
            .addConverterFactory(FilterConverterFactory())
            .addConverterFactory(FieldsConverterFactory())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    private val okClient: OkHttpClient
        get() = Builder()
            .addInterceptor(PreventURLDecodeInterceptor())
            .build()
}
