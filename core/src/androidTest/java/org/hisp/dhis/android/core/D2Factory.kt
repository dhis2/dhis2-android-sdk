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
package org.hisp.dhis.android.core

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import org.hisp.dhis.android.core.arch.storage.internal.*
import org.hisp.dhis.android.core.debug.flipper.debug.FlipperManager.setUp
import org.hisp.dhis.android.core.maintenance.D2Error

internal object D2Factory {

    @JvmStatic
    fun forNewDatabase(): D2 {
        return forNewDatabaseInternal(InMemorySecureStore(), InMemoryUnsecureStore())
    }

    @JvmStatic
    fun clear() {
        D2Manager.clear()
    }

    @Throws(D2Error::class)
    fun forNewDatabaseWithAndroidSecureStore(): D2 {
        val context = InstrumentationRegistry.getInstrumentation().context
        return forNewDatabaseInternal(AndroidSecureStore(context), AndroidInsecureStore(context))
    }

    private fun forNewDatabaseInternal(secureStore: SecureStore, insecureStore: InsecureStore): D2 {
        val context = InstrumentationRegistry.getInstrumentation().context
        val d2Configuration = d2Configuration(context)

        D2Manager.setTestMode(true)
        D2Manager.setTestingSecureStore(secureStore)
        D2Manager.setTestingInsecureStore(insecureStore)

        return D2Manager.blockingInstantiateD2(d2Configuration)!!
    }

    private fun d2Configuration(context: Context): D2Configuration {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC

        return D2Configuration.builder()
            .appVersion("1.0.0")
            .readTimeoutInSeconds(30)
            .connectTimeoutInSeconds(30)
            .writeTimeoutInSeconds(30)
            .networkInterceptors(listOf(setUp(context)))
            .interceptors(listOf<Interceptor>(loggingInterceptor))
            .context(context)
            .build()
    }
}
