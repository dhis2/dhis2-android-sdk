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
package org.hisp.dhis.android.core.sms.domain.converter.internal

import android.annotation.SuppressLint
import android.util.Base64
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.functions.Function3
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent
import org.hisp.dhis.android.core.sms.domain.repository.internal.LocalDbRepository
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager
import org.hisp.dhis.smscompression.SMSSubmissionWriter
import org.hisp.dhis.smscompression.models.SMSMetadata
import org.hisp.dhis.smscompression.models.SMSSubmission

internal abstract class Converter<P> internal constructor(
    internal val localDbRepository: LocalDbRepository,
    private val dhisVersionManager: DHISVersionManager,
) {

    fun readAndConvert(): Single<String> = readAndConvert(0)

    fun readAndConvert(submissionId: Int): Single<String> {
        return Single.zip(
            localDbRepository.getMetadataIds(),
            localDbRepository.getUserName(),
            readItemFromDb(),
            Function3<SMSMetadata, String, P, CompressionData> { metadata, user, item ->
                CompressionData(metadata, user, item)
            },
        ).flatMap { d ->
            convert(d.item, d.metadata, d.user, submissionId)
        }
    }

    /**
     * @param dataItem object to convert
     * @return text ready to be sent by sms
     */
    private fun convert(
        dataItem: P,
        metadata: SMSMetadata,
        user: String,
        submissionId: Int,
    ): Single<String> {
        return convert(dataItem, user, submissionId).map { submission ->
            val writer = SMSSubmissionWriter(metadata)
            val smsVersion = dhisVersionManager.getSmsVersion()
                ?: throw D2Error.builder()
                    .errorCode(D2ErrorCode.SMS_NOT_SUPPORTED)
                    .errorDescription("SMS is not supported in version ${dhisVersionManager.getPatchVersion()}")
                    .errorComponent(D2ErrorComponent.SDK)
                    .build()

            base64(writer.compress(submission, smsVersion.intValue))
        }
    }

    @Suppress("TooGenericExceptionCaught")
    @SuppressLint("NewApi")
    private fun base64(bytes: ByteArray): String {
        return try {
            Base64.encodeToString(bytes, Base64.NO_WRAP)
        } catch (t: Throwable) {
            // not android, so will try with pure java
            java.util.Base64.getEncoder().encodeToString(bytes)
        }
    }

    protected abstract fun convert(dataItem: P, user: String, submissionId: Int): Single<out SMSSubmission>

    abstract fun updateSubmissionState(state: State): Completable

    protected abstract fun readItemFromDb(): Single<P>

    private inner class CompressionData(
        val metadata: SMSMetadata,
        val user: String,
        val item: P,
    )
}
