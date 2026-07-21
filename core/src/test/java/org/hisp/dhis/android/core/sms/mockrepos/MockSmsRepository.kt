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
package org.hisp.dhis.android.core.sms.mockrepos

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import org.hisp.dhis.android.core.sms.domain.repository.SmsRepository
import org.hisp.dhis.android.core.sms.domain.repository.internal.SubmissionType
import java.util.Date

class MockSmsRepository : SmsRepository {
    override fun sendSms(
        number: String,
        smsParts: List<String>,
        sendingTimeoutSeconds: Int,
    ): Observable<SmsRepository.SmsSendingState> {
        return Observable.defer {
            Observable.just(
                SmsRepository.SmsSendingState(0, 1),
                SmsRepository.SmsSendingState(1, 1),
            )
        }
    }

    override fun generateSmsParts(value: String): Single<List<String>> {
        return Single.fromCallable { listOf(value) }
    }

    override fun listenToConfirmationSms(
        fromDate: Date,
        waitingTimeoutSeconds: Int,
        requiredSender: String,
        submissionId: Int,
        submissionType: SubmissionType,
    ): Completable {
        return Completable.complete()
    }

    override fun isAwaitedSuccessMessage(
        sender: String,
        message: String,
        requiredSender: String,
        submissionId: Int,
        submissionType: SubmissionType,
    ): Single<Boolean> {
        return Single.just(true)
    }
}
