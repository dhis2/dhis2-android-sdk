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
package org.hisp.dhis.android.core.arch.api.internal

import android.os.Build
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.hisp.dhis.android.BuildConfig
import org.hisp.dhis.android.core.D2Configuration
import java.util.concurrent.TimeUnit

internal object OkHttpClientFactory {
    fun okHttpClient(
        d2Configuration: D2Configuration,
        authenticator: Interceptor,
    ): OkHttpClient {
        val client = OkHttpClient.Builder()
            .addInterceptor(DynamicServerURLInterceptor())
            .addInterceptor(ServerURLVersionRedirectionInterceptor())
            .addInterceptor(authenticator)
            .addInterceptor(PreventURLDecodeInterceptor())
            .addInterceptor(
                Interceptor { chain: Interceptor.Chain ->
                    val originalRequest = chain.request()
                    val withUserAgent = originalRequest.newBuilder()
                        .header("User-Agent", getUserAgent(d2Configuration))
                        .build()
                    chain.proceed(withUserAgent)
                },
            )
            .readTimeout(d2Configuration.readTimeoutInSeconds().toLong(), TimeUnit.SECONDS)
            .connectTimeout(d2Configuration.connectTimeoutInSeconds().toLong(), TimeUnit.SECONDS)
            .writeTimeout(d2Configuration.writeTimeoutInSeconds().toLong(), TimeUnit.SECONDS)
            .followRedirects(false)

        for (interceptor in d2Configuration.networkInterceptors()) {
            client.addNetworkInterceptor(interceptor)
        }

        for (interceptor in d2Configuration.interceptors()) {
            client.addInterceptor(interceptor)
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            val (socketFactory, trustManager) = TrustFactory.getTrustFactoryManager(d2Configuration.context())
            client.sslSocketFactory(socketFactory, trustManager)
        }

        return client.build()
    }

    private fun getUserAgent(d2Configuration: D2Configuration): String {
        return String.format(
            "%s/%s/%s/Android_%s",
            d2Configuration.appName(),
            BuildConfig.VERSION_NAME, // SDK version
            d2Configuration.appVersion(),
            Build.VERSION.SDK_INT, // Android Version
        )
    }
}
