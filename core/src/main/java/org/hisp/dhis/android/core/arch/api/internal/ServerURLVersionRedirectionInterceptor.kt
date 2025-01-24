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

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.hisp.dhis.android.core.arch.api.HttpServiceClient.Companion.IsExternalRequestHeader
import org.hisp.dhis.android.core.arch.api.authentication.internal.PasswordAndCookieAuthenticator.Companion.LOCATION_KEY
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL

internal class ServerURLVersionRedirectionInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var response = chain.proceed(request)

        var redirects = 0
        while (response.isRedirect && redirects <= MaxRedirects) {
            val location = response.header(LOCATION_KEY)
            if (isInternal(request)) {
                location?.let { ServerURLWrapper.setServerUrl(it) }
            }
            response.close()

            val redirectReq = location?.let {
                try {
                    URL(it) // Verify if location is a valid URL
                    request.newBuilder().url(it).build()
                } catch (e: MalformedURLException) {
                    request
                }
            } ?: request

            response = chain.proceed(redirectReq)
            redirects++
        }
        return response
    }

    private fun isInternal(request: Request): Boolean {
        return request.header(IsExternalRequestHeader) == null
    }

    companion object {
        const val MaxRedirects = 20
    }
}
