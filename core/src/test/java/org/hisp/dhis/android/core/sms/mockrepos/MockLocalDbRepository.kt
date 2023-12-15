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
import io.reactivex.Single
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.relationship.Relationship
import org.hisp.dhis.android.core.sms.domain.model.internal.SMSDataValueSet
import org.hisp.dhis.android.core.sms.domain.repository.WebApiRepository.GetMetadataIdsConfig
import org.hisp.dhis.android.core.sms.domain.repository.internal.LocalDbRepository
import org.hisp.dhis.android.core.sms.domain.repository.internal.LocalDbRepository.TooManySubmissionsException
import org.hisp.dhis.android.core.sms.domain.repository.internal.SubmissionType
import org.hisp.dhis.android.core.sms.mockrepos.testobjects.MockMetadata
import org.hisp.dhis.android.core.sms.mockrepos.testobjects.MockObjects
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.smscompression.models.SMSMetadata
import java.util.Date

class MockLocalDbRepository : LocalDbRepository {
    private var gatewayNumber: String? = "525525"
    private var confirmationSenderNumber: String? = null
    private var resultWaitingTimeout = RESULT_WAITING_TIMEOUT_DEFAULT
    private var metadata: SMSMetadata = MockMetadata()
    private var metadataIdsConfig = GetMetadataIdsConfig()
    private var moduleEnabled = true
    private var waitingForResult = false
    private val ongoingSubmissions = HashMap<Int, SubmissionType>()

    init {
        metadata.lastSyncDate = Date()
    }

    override fun getUserName(): Single<String> {
        return Single.fromCallable { MockObjects.user }
    }

    override fun getGatewayNumber(): Single<String> {
        return Single.fromCallable { gatewayNumber ?: "" }
    }

    override fun setGatewayNumber(number: String): Completable {
        return Completable.fromAction { gatewayNumber = number }
    }

    override fun deleteGatewayNumber(): Completable {
        return Completable.fromAction { gatewayNumber = null }
    }

    override fun getWaitingResultTimeout(): Single<Int> {
        return Single.fromCallable { resultWaitingTimeout }
    }

    override fun setWaitingResultTimeout(timeoutSeconds: Int): Completable {
        return Completable.fromAction { resultWaitingTimeout = timeoutSeconds }
    }

    override fun deleteWaitingResultTimeout(): Completable {
        return Completable.fromAction { resultWaitingTimeout = RESULT_WAITING_TIMEOUT_DEFAULT }
    }

    override fun getConfirmationSenderNumber(): Single<String> {
        return Single.fromCallable { confirmationSenderNumber ?: "" }
    }

    override fun setConfirmationSenderNumber(number: String): Completable {
        return Completable.fromAction { confirmationSenderNumber = number }
    }

    override fun deleteConfirmationSenderNumber(): Completable {
        return Completable.fromAction { confirmationSenderNumber = null }
    }

    override fun getMetadataIds(): Single<SMSMetadata> {
        return Single.fromCallable { metadata }
    }

    override fun setMetadataIds(metadata: SMSMetadata): Completable {
        return Completable.fromAction { this.metadata = metadata }
    }

    override fun getTrackerEventToSubmit(eventUid: String): Single<Event> {
        return Single.fromCallable { MockObjects.getTrackerEvent() }
    }

    override fun getSimpleEventToSubmit(eventUid: String): Single<Event> {
        return Single.fromCallable { MockObjects.getSimpleEvent() }
    }

    override fun getTeiEnrollmentToSubmit(enrollmentUid: String): Single<TrackedEntityInstance> {
        return when (enrollmentUid) {
            MockObjects.enrollmentUidWithNullEvents ->
                Single.fromCallable { MockObjects.getTEIEnrollmentWithoutEvents() }

            MockObjects.enrollmentUidWithoutEvents ->
                Single.fromCallable { MockObjects.getTEIEnrollmentWithEventEmpty() }

            MockObjects.enrollmentUidWithoutGeometry ->
                Single.fromCallable { MockObjects.getTEIEnrollmentWithoutGeometry() }

            else ->
                Single.fromCallable { MockObjects.getTEIEnrollment() }
        }
    }

    override fun updateEventSubmissionState(eventUid: String, state: State): Completable {
        return Completable.complete()
    }

    override fun updateEnrollmentSubmissionState(
        trackedEntityInstance: TrackedEntityInstance,
        state: State,
    ): Completable {
        return Completable.complete()
    }

    override fun updateRelationshipSubmissionState(
        relationshipUid: String,
        state: State,
    ): Completable {
        return Completable.complete()
    }

    override fun setMetadataDownloadConfig(metadataIdsConfig: GetMetadataIdsConfig): Completable {
        return Completable.fromAction { this.metadataIdsConfig = metadataIdsConfig }
    }

    override fun getMetadataDownloadConfig(): Single<GetMetadataIdsConfig> {
        return Single.fromCallable { metadataIdsConfig }
    }

    override fun setModuleEnabled(enabled: Boolean): Completable {
        return Completable.fromAction { moduleEnabled = enabled }
    }

    override fun isModuleEnabled(): Single<Boolean> {
        return Single.fromCallable { moduleEnabled }
    }

    override fun setWaitingForResultEnabled(enabled: Boolean): Completable {
        return Completable.fromAction { waitingForResult = enabled }
    }

    override fun getWaitingForResultEnabled(): Single<Boolean> {
        return Single.fromCallable { waitingForResult }
    }

    override fun getOngoingSubmissions(): Single<Map<Int, SubmissionType>> {
        return Single.fromCallable { ongoingSubmissions }
    }

    override fun generateNextSubmissionId(): Single<Int> {
        return Single.fromCallable {
            var next = 0
            do {
                if (!ongoingSubmissions.keys.contains(next)) {
                    return@fromCallable next
                }
                next++
            } while (next <= 255)
            throw TooManySubmissionsException()
        }
    }

    override fun addOngoingSubmission(id: Int, type: SubmissionType): Completable {
        return Completable.fromAction { ongoingSubmissions[id] = type }
    }

    override fun removeOngoingSubmission(id: Int): Completable {
        return Completable.fromAction { ongoingSubmissions.remove(id) }
    }

    override fun getDataValueSet(
        dataSetUid: String,
        orgUnit: String,
        period: String,
        attributeOptionComboUid: String,
    ): Single<SMSDataValueSet> {
        return if (dataSetUid == MockObjects.dataSetEmptyListUid) {
            Single.fromCallable { MockObjects.getSMSDataValueSetEmptyList() }
        } else {
            Single.fromCallable { MockObjects.getSMSDataValueSet() }
        }
    }

    override fun updateDataSetSubmissionState(
        dataSet: String,
        orgUnit: String,
        period: String,
        attributeOptionComboUid: String,
        state: State,
    ): Completable {
        return Completable.complete()
    }

    override fun getRelationship(relationshipUid: String): Single<Relationship> {
        return Single.fromCallable { MockObjects.getRelationship() }
    }

    companion object {
        private const val RESULT_WAITING_TIMEOUT_DEFAULT = 120
    }
}
