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

import org.hisp.dhis.android.core.arch.api.executors.internal.APIErrorMapper.Companion.getIfNotEmpty
import org.hisp.dhis.android.core.arch.api.executors.internal.APIErrorMapper.Companion.noErrorMessage
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception

internal class D2HttpException(val response: D2HttpResponse) : Exception() {
    override val message: String?
        get() = this.response.message
}
data class D2HttpResponse(
    val statusCode: Int,
    val message: String?,
    val errorBody: String,
    val requestUrl: String?,
)

internal fun HttpException.toD2Response(): D2HttpResponse {
    val errorBody =
        try {
            getIfNotEmpty(this.response()?.errorBody()!!.string())
                ?: getIfNotEmpty(this.response()?.errorBody().toString())
        } catch (e: IOException) {
            null
        }
    val requestUrl = this.response()?.raw()?.request?.url.toString()
    return D2HttpResponse(this.code(), this.message(), errorBody ?: noErrorMessage, requestUrl)
}
