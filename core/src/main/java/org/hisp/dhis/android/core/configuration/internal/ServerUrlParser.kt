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
package org.hisp.dhis.android.core.configuration.internal

import io.ktor.http.URLBuilder
import io.ktor.http.URLParserException
import io.ktor.http.Url
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent

internal object ServerUrlParser {
    @JvmStatic
    @Suppress("ThrowsCount")
    @Throws(D2Error::class)
    fun parse(url: String?): Url {
        when {
            url.isNullOrBlank() -> throw nullOrBlankUrlD2Error()
            !url.startsWith("http") -> throw malformedUrlD2Error()
            else -> try {
                val urlBuilder = URLBuilder(removeTrailingApi(url))
                urlBuilder.encodedPathSegments += "api/"
                return urlBuilder.build()
            } catch (e: URLParserException) {
                throw malformedUrlD2Error()
            }
        }
    }

    private fun malformedUrlD2Error(): D2Error {
        return urlD2Error(D2ErrorCode.SERVER_URL_MALFORMED, "Server URL is malformed")
    }

    private fun nullOrBlankUrlD2Error(): D2Error {
        return urlD2Error(D2ErrorCode.SERVER_URL_NULL, "Server URL is null or blank")
    }

    private fun urlD2Error(d2ErrorCode: D2ErrorCode, description: String): D2Error {
        return D2Error.builder()
            .errorCode(d2ErrorCode)
            .errorDescription(description)
            .errorComponent(D2ErrorComponent.SDK)
            .build()
    }

    fun trimAndRemoveTrailingSlash(url: String?): String? {
        return url?.trim()?.trimEnd('/')
    }

    fun removeTrailingApi(url: String): String {
        return url.trimEnd('/').removeSuffix("/api")
    }
}
