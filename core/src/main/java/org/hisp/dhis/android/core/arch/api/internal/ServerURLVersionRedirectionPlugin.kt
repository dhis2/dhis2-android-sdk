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

package org.hisp.dhis.android.core.arch.api.internal

import io.ktor.client.call.body
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.client.request.takeFrom
import io.ktor.http.HttpHeaders
import org.hisp.dhis.android.core.arch.api.internal.HttpStatusCodes.REDIRECT_MAX
import org.hisp.dhis.android.core.arch.api.internal.HttpStatusCodes.REDIRECT_MIN

object ServerURLVersionRedirectionPlugin {
    private const val MAX_REDIRECTS = 20
    private var redirects = 0

    val instance = createClientPlugin(name = "ServerURLVersionRedirectionPlugin") {
        transformResponseBody { response, _, requestedType ->
            var iteration = response

            while (iteration.status.value in REDIRECT_MIN..REDIRECT_MAX && redirects <= MAX_REDIRECTS) {
                redirects++

                val location = iteration.headers[HttpHeaders.Location]
                location?.let {
                    ServerURLWrapper.setServerUrl(it)
                }

                val originalRequest = iteration.call.request

                val redirectRequest = HttpRequestBuilder().apply {
                    takeFrom(originalRequest)
                }

                iteration = iteration.call.client.request(redirectRequest)
            }
            iteration.body(requestedType)
        }
    }
}
