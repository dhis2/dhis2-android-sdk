/*
 *  Copyright (c) 2004-2024, University of Oslo
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
package org.hisp.dhis.android.core.systeminfo.internal

import io.ktor.http.isSuccess
import io.reactivex.Single
import kotlinx.coroutines.rx2.rxSingle
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent
import org.hisp.dhis.android.core.systeminfo.Ping
import org.koin.core.annotation.Singleton
import java.io.IOException

@Singleton
class PingImpl internal constructor(
    private val pingNetworkHandler: PingNetworkHandler,
) : Ping {

    override fun get(): Single<String> {
        return rxSingle { checkPing() }
    }

    @Throws(D2Error::class)
    override fun blockingGet(): String {
        return get().blockingGet()
    }

    @Suppress("TooGenericExceptionCaught")
    private suspend fun checkPing(): String {
        try {
            val response = pingNetworkHandler.getPing()
            return if (response.status.isSuccess()) {
                "pong"
            } else {
                throw IOException("Ping to the server failed with status code: ${response.status.value}")
            }
        } catch (e: Exception) {
            throw toD2Error(e)
        }
    }

    private fun toD2Error(e: Exception): D2Error {
        return D2Error.builder()
            .originalException(e)
            .errorCode(D2ErrorCode.API_UNSUCCESSFUL_RESPONSE)
            .errorDescription("Unable to ping the server.")
            .errorComponent(D2ErrorComponent.Server)
            .build()
    }
}
