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
package org.hisp.dhis.android.core.configuration.internal

import okhttp3.HttpUrl
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent

internal object ServerUrlParser {
    @JvmStatic
    @Throws(D2Error::class)
    fun parse(url: String?): HttpUrl {
        if (url == null) {
            throw D2Error.builder()
                .errorCode(D2ErrorCode.SERVER_URL_NULL)
                .errorDescription("Server URL is null")
                .errorComponent(D2ErrorComponent.SDK)
                .build()
        }
        val urlWithSlashAndAPI = appendSlashAndAPI(url)
        val httpUrl = HttpUrl.parse(appendSlashAndAPI(urlWithSlashAndAPI))

        return httpUrl
            ?: throw D2Error.builder()
                .errorCode(D2ErrorCode.SERVER_URL_MALFORMED)
                .errorDescription("Server URL is malformed")
                .errorComponent(D2ErrorComponent.SDK)
                .build()
    }

    fun trimAndRemoveTrailingSlash(url: String?): String? {
        return url?.trim()?.trimEnd('/')
    }

    fun removeTrailingApi(url: String): String {
        return url.trimEnd('/').removeSuffix("/api")
    }

    private fun appendSlashAndAPI(url: String): String {
        val trimmedUrl = url.trim().replace(" ", "")
        val withSlash = if (trimmedUrl.endsWith("/")) trimmedUrl else "$trimmedUrl/"

        return if (withSlash.endsWith("api/")) withSlash else withSlash + "api/"
    }
}
