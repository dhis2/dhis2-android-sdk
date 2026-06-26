/*
 *  Copyright (c) 2004-2026, University of Oslo
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

package org.hisp.dhis.android.core.maintenance

import org.hisp.dhis.android.annotations.ModelBuilder
import org.hisp.dhis.android.core.common.CoreObject
import java.util.Date

@ModelBuilder
data class D2Error(
    val url: String?,
    val errorComponent: D2ErrorComponent?,
    val errorCode: D2ErrorCode,
    val errorDescription: String,
    val httpErrorCode: Int?,
    val originalException: Exception?,
    val created: Date?,
) : Exception(), CoreObject {

    fun url(): String? = url
    fun errorComponent(): D2ErrorComponent? = errorComponent
    fun errorCode(): D2ErrorCode = errorCode
    fun errorDescription(): String = errorDescription
    fun httpErrorCode(): Int? = httpErrorCode
    fun originalException(): Exception? = originalException
    fun created(): Date? = created

    val isOffline: Boolean
        get() = errorCode == D2ErrorCode.SOCKET_TIMEOUT ||
            errorCode == D2ErrorCode.UNKNOWN_HOST ||
            errorCode == D2ErrorCode.SERVER_CONNECTION_ERROR

    fun toBuilder(): Builder = D2ErrorBuilder.from(this)

    class Builder : D2ErrorBuilder()

    companion object {
        @JvmStatic
        fun builder(): Builder = Builder()
            .errorCode(D2ErrorCode.UNEXPECTED)
            .created(Date())
    }
}
