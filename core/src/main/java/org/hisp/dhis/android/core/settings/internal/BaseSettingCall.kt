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
package org.hisp.dhis.android.core.settings.internal

import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutor
import org.hisp.dhis.android.core.arch.call.internal.DownloadProvider
import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import java.net.HttpURLConnection

internal abstract class BaseSettingCall<T> internal constructor(
    val coroutineAPICallExecutor: CoroutineAPICallExecutor,
) : DownloadProvider {

    override suspend fun download(storeError: Boolean) {
        fetch(storeError).fold(
            { process(it) },
            { throwable ->
                if (isExpectedError(throwable)) {
                    process(null)
                }
            },
        )
    }

    suspend fun fetch(storeError: Boolean): Result<T, D2Error> {
        return try {
            tryFetch(storeError)
        } catch (d2Error: D2Error) {
            Result.Failure(d2Error)
        }
    }

    private fun isExpectedError(throwable: D2Error): Boolean {
        return throwable.httpErrorCode() == HttpURLConnection.HTTP_NOT_FOUND ||
            throwable.errorCode() == D2ErrorCode.SETTINGS_APP_NOT_SUPPORTED ||
            throwable.errorCode() == D2ErrorCode.SETTINGS_APP_NOT_INSTALLED
    }

    protected abstract suspend fun tryFetch(storeError: Boolean): Result<T, D2Error>

    protected abstract suspend fun process(item: T?)
}
