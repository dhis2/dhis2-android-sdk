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
package org.hisp.dhis.android.core.sms.domain.repository.internal

import io.reactivex.Completable
import io.reactivex.Single
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.relationship.Relationship
import org.hisp.dhis.android.core.sms.domain.model.internal.SMSDataValueSet
import org.hisp.dhis.android.core.sms.domain.repository.WebApiRepository.GetMetadataIdsConfig
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.smscompression.models.SMSMetadata

@Suppress("TooManyFunctions")
interface LocalDbRepository {
    fun getUserName(): Single<String>
    fun getGatewayNumber(): Single<String>
    fun setGatewayNumber(number: String): Completable
    fun deleteGatewayNumber(): Completable
    fun getWaitingResultTimeout(): Single<Int>
    fun setWaitingResultTimeout(timeoutSeconds: Int): Completable
    fun deleteWaitingResultTimeout(): Completable
    fun getConfirmationSenderNumber(): Single<String>
    fun setConfirmationSenderNumber(number: String): Completable
    fun deleteConfirmationSenderNumber(): Completable
    fun getMetadataIds(): Single<SMSMetadata>
    suspend fun setMetadataIds(metadata: SMSMetadata)
    fun getTrackerEventToSubmit(eventUid: String): Single<Event>
    fun getSimpleEventToSubmit(eventUid: String): Single<Event>
    fun getTeiEnrollmentToSubmit(enrollmentUid: String): Single<TrackedEntityInstance>
    fun updateEventSubmissionState(eventUid: String, state: State): Completable
    fun updateEnrollmentSubmissionState(tei: TrackedEntityInstance, state: State): Completable
    fun updateRelationshipSubmissionState(relationshipUid: String, state: State): Completable
    fun setMetadataDownloadConfig(metadataIdsConfig: GetMetadataIdsConfig): Completable
    suspend fun getMetadataDownloadConfig(): GetMetadataIdsConfig
    fun setModuleEnabled(enabled: Boolean): Completable
    fun isModuleEnabled(): Single<Boolean>
    suspend fun isModuleEnabledSuspend(): Boolean
    fun setWaitingForResultEnabled(enabled: Boolean): Completable
    fun getWaitingForResultEnabled(): Single<Boolean>
    fun getOngoingSubmissions(): Single<Map<Int, SubmissionType>>
    fun generateNextSubmissionId(): Single<Int>
    fun addOngoingSubmission(id: Int, type: SubmissionType): Completable
    fun removeOngoingSubmission(id: Int): Completable
    fun getDataValueSet(
        dataSet: String,
        orgUnit: String,
        period: String,
        attributeOptionComboUid: String,
    ): Single<SMSDataValueSet>

    fun updateDataSetSubmissionState(
        dataSet: String,
        orgUnit: String,
        period: String,
        attributeOptionComboUid: String,
        state: State,
    ): Completable

    fun getRelationship(relationshipUid: String): Single<Relationship>
    class TooManySubmissionsException :
        IllegalStateException("Too many ongoing submissions at the same time >255")
}
