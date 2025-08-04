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
package org.hisp.dhis.android.network.twofactorauth

import org.hisp.dhis.android.core.arch.api.executors.internal.APICallErrorCatcher
import org.hisp.dhis.android.core.arch.api.internal.D2HttpResponse
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.network.common.dto.HttpMessageResponseDTO
import java.io.IOException
import javax.net.ssl.HttpsURLConnection

internal class TotpSecretErrorCatcher : APICallErrorCatcher {

    override fun mustBeStored() = false

    @Throws(IOException::class)
    override fun catchError(response: D2HttpResponse): D2ErrorCode? =
        HttpMessageResponseDTO.toErrorClass(response.errorBody).takeIf { error ->
            error.httpStatusCode() == HttpsURLConnection.HTTP_CONFLICT &&
                    error.message() == "User is not in TOTP 2FA enrollment mode"
        }?.let { D2ErrorCode.NOT_IN_TOTP_2FA_ENROLLMENT_MODE }
}
