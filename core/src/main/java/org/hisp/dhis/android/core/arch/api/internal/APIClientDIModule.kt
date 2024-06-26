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

import android.util.Log
import io.ktor.client.HttpClient
import okhttp3.OkHttpClient
import org.hisp.dhis.android.core.D2Configuration
import org.hisp.dhis.android.core.arch.api.authentication.internal.ParentAuthenticator
import org.hisp.dhis.android.core.maintenance.D2Error
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton
import retrofit2.Retrofit

@Module
internal class APIClientDIModule {
    @Singleton
    fun okHttpClient(d2Configuration: D2Configuration, authenticator: ParentAuthenticator): OkHttpClient {
        return OkHttpClientFactory.okHttpClient(d2Configuration, authenticator)
    }

    @Singleton
    @Suppress("TooGenericExceptionThrown")
    fun retrofit(okHttpClient: OkHttpClient): Retrofit {
        return try {
            RetrofitFactory.retrofit(okHttpClient)
        } catch (d2Error: D2Error) {
            Log.e("APIClientDIModule", d2Error.message!!)
            throw RuntimeException("Can't instantiate retrofit")
        }
    }

    @Singleton
    @Suppress("TooGenericExceptionThrown")
    fun ktor(okHttpClient: OkHttpClient): HttpClient {
        return try {
            KtorFactory.ktor(okHttpClient)
        } catch (d2Error: D2Error) {
            Log.e("APIClientDIModule", d2Error.message!!)
            throw RuntimeException("Can't instantiate ktor")
        }
    }
}
