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
package org.hisp.dhis.android.core.settings.internal

import io.reactivex.Completable
import io.reactivex.Single
import java.net.HttpURLConnection
import org.hisp.dhis.android.core.arch.call.internal.CompletableProvider
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode

internal abstract class BaseSettingCall<T> : CompletableProvider {

    override fun getCompletable(storeError: Boolean): Completable {
        return Completable
            .fromSingle(download(storeError))
            .onErrorComplete()
    }

    fun download(storeError: Boolean): Single<T> {
        return fetch(storeError)
            .doOnSuccess { process(it) }
            .doOnError { throwable: Throwable ->
                if (throwable is D2Error && isExpectedError(throwable)) {
                    process(null)
                }
            }
    }

    private fun isExpectedError(throwable: D2Error): Boolean {
        return throwable.httpErrorCode() == HttpURLConnection.HTTP_NOT_FOUND ||
            throwable.errorCode() == D2ErrorCode.SETTINGS_APP_NOT_SUPPORTED ||
            throwable.errorCode() == D2ErrorCode.SETTINGS_APP_NOT_INSTALLED
    }

    abstract fun fetch(storeError: Boolean): Single<T>

    abstract fun process(item: T?)
}
