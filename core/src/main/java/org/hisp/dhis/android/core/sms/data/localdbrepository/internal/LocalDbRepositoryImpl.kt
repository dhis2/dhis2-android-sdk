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
package org.hisp.dhis.android.core.sms.data.localdbrepository.internal

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.json.internal.ObjectMapperFactory.objectMapper
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.common.State.Companion.uploadableStatesIncludingError
import org.hisp.dhis.android.core.common.internal.DataStatePropagator
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistrationTableInfo
import org.hisp.dhis.android.core.dataset.internal.DataSetCompleteRegistrationStore
import org.hisp.dhis.android.core.datavalue.DataValue
import org.hisp.dhis.android.core.enrollment.EnrollmentInternalAccessor
import org.hisp.dhis.android.core.enrollment.EnrollmentModule
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.EventModule
import org.hisp.dhis.android.core.event.internal.EventStore
import org.hisp.dhis.android.core.relationship.Relationship
import org.hisp.dhis.android.core.relationship.RelationshipConstraintType
import org.hisp.dhis.android.core.relationship.internal.RelationshipItemStore
import org.hisp.dhis.android.core.relationship.internal.RelationshipStore
import org.hisp.dhis.android.core.sms.domain.model.internal.SMSDataValueSet
import org.hisp.dhis.android.core.sms.domain.repository.WebApiRepository.GetMetadataIdsConfig
import org.hisp.dhis.android.core.sms.domain.repository.internal.LocalDbRepository
import org.hisp.dhis.android.core.sms.domain.repository.internal.SubmissionType
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceInternalAccessor
import org.hisp.dhis.android.core.trackedentity.TrackedEntityModule
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStore
import org.hisp.dhis.android.core.user.AuthenticatedUserObjectRepository
import org.hisp.dhis.smscompression.models.SMSMetadata

@SuppressWarnings("LongParameterList", "TooManyFunctions")
internal class LocalDbRepositoryImpl @Inject constructor(
    private val userRepository: AuthenticatedUserObjectRepository,
    private val trackedEntityModule: TrackedEntityModule,
    private val eventModule: EventModule,
    private val enrollmentModule: EnrollmentModule,
    private val fileResourceCleaner: FileResourceCleaner,
    private val eventStore: EventStore,
    private val enrollmentStore: EnrollmentStore,
    private val relationshipStore: RelationshipStore,
    private val relationshipItemStore: RelationshipItemStore,
    private val dataSetsStore: DataSetsStore,
    private val trackedEntityInstanceStore: TrackedEntityInstanceStore,
    private val dataSetCompleteRegistrationStore: DataSetCompleteRegistrationStore,
    private val metadataIdsStore: MetadataIdsStore,
    private val smsConfigStore: SMSConfigStore,
    private val ongoingSubmissionsStore: OngoingSubmissionsStore,
    private val dataStatePropagator: DataStatePropagator
) : LocalDbRepository {

    override fun getUserName(): Single<String> {
        return Single.fromCallable { userRepository.blockingGet().user() }
    }

    override fun getGatewayNumber(): Single<String> {
        return Single.fromCallable { smsConfigStore.get(SMSConfigKey.GATEWAY) ?: "" }
    }

    override fun setGatewayNumber(number: String): Completable {
        return Completable.fromAction { smsConfigStore.set(SMSConfigKey.GATEWAY, number) }
    }

    override fun deleteGatewayNumber(): Completable {
        return Completable.fromAction { smsConfigStore.delete(SMSConfigKey.GATEWAY) }
    }

    override fun getWaitingResultTimeout(): Single<Int> {
        return Single.fromCallable {
            smsConfigStore.get(SMSConfigKey.WAITING_RESULT_TIMEOUT)?.toInt() ?: DefaultWaitTimeout
        }
    }

    override fun setWaitingResultTimeout(timeoutSeconds: Int): Completable {
        return Completable.fromAction {
            smsConfigStore.set(SMSConfigKey.WAITING_RESULT_TIMEOUT, timeoutSeconds.toString())
        }
    }

    override fun deleteWaitingResultTimeout(): Completable {
        return Completable.fromAction { smsConfigStore.delete(SMSConfigKey.WAITING_RESULT_TIMEOUT) }
    }

    override fun getConfirmationSenderNumber(): Single<String> {
        return Single.fromCallable { smsConfigStore.get(SMSConfigKey.CONFIRMATION_SENDER) ?: "" }
    }

    override fun setConfirmationSenderNumber(number: String): Completable {
        return Completable.fromAction { smsConfigStore.set(SMSConfigKey.CONFIRMATION_SENDER, number) }
    }

    override fun deleteConfirmationSenderNumber(): Completable {
        return Completable.fromAction { smsConfigStore.delete(SMSConfigKey.CONFIRMATION_SENDER) }
    }

    override fun getMetadataIds(): Single<SMSMetadata> {
        return metadataIdsStore.getMetadataIds()
    }

    override fun setMetadataIds(metadata: SMSMetadata): Completable {
        return metadataIdsStore.setMetadataIds(metadata)
    }

    override fun getTrackerEventToSubmit(eventUid: String): Single<Event> {
        // simple event is the same object as tracker event
        return getSimpleEventToSubmit(eventUid)
    }

    override fun getSimpleEventToSubmit(eventUid: String): Single<Event> {
        return eventModule.events().withTrackedEntityDataValues()
            .byUid().eq(eventUid).one()
            .get()
            .flatMap { event: Event? -> fileResourceCleaner.removeFileDataValues(event) }
    }

    override fun getTeiEnrollmentToSubmit(enrollmentUid: String): Single<TrackedEntityInstance> {
        return Single.fromCallable {
            val enrollment = enrollmentModule.enrollments().byUid().eq(enrollmentUid).one().blockingGet()
            val events = getEventsForEnrollment(enrollmentUid).blockingGet()
            val enrollmentWithEvents = EnrollmentInternalAccessor
                .insertEvents(enrollment.toBuilder(), events)
                .build()
            val trackedEntityInstance = getTrackedEntityInstance(enrollment.trackedEntityInstance()).blockingGet()
            TrackedEntityInstanceInternalAccessor
                .insertEnrollments(trackedEntityInstance.toBuilder(), listOf(enrollmentWithEvents))
                .build()
        }
    }

    private fun getTrackedEntityInstance(instanceUid: String?): Single<TrackedEntityInstance> {
        return trackedEntityModule.trackedEntityInstances()
            .withTrackedEntityAttributeValues()
            .uid(instanceUid)
            .get()
            .flatMap { instance: TrackedEntityInstance? -> fileResourceCleaner.removeFileAttributeValues(instance) }
    }

    private fun getEventsForEnrollment(enrollmentUid: String): Single<List<Event>> {
        return eventModule.events()
            .byEnrollmentUid().eq(enrollmentUid)
            .bySyncState().`in`(uploadableStatesIncludingError().toList())
            .withTrackedEntityDataValues()
            .get()
            .flatMapObservable { source: List<Event> -> Observable.fromIterable(source) }
            .flatMapSingle { event: Event -> fileResourceCleaner.removeFileDataValues(event) }
            .toList()
    }

    override fun updateEventSubmissionState(eventUid: String, state: State): Completable {
        return Completable.fromAction {
            eventStore.setSyncState(eventUid, state)
            val event = eventStore.selectByUid(eventUid)
            dataStatePropagator.propagateEventUpdate(event)
        }
    }

    override fun updateEnrollmentSubmissionState(tei: TrackedEntityInstance, state: State): Completable {
        return Completable.fromAction {
            val enrollment = TrackedEntityInstanceInternalAccessor.accessEnrollments(tei)[0]
            val events = EnrollmentInternalAccessor.accessEvents(enrollment)
            events?.forEach { event ->
                eventStore.setSyncState(event.uid(), state)
                dataStatePropagator.propagateEventUpdate(event)
            }
            enrollmentStore.setSyncState(enrollment.uid(), state)
            dataStatePropagator.propagateEnrollmentUpdate(enrollment)
            trackedEntityInstanceStore.setSyncState(enrollment.trackedEntityInstance()!!, state)
            dataStatePropagator.propagateTrackedEntityInstanceUpdate(tei)
        }
    }

    override fun updateRelationshipSubmissionState(relationshipUid: String, state: State): Completable {
        return Completable.fromAction {
            relationshipStore.setSyncState(relationshipUid, state)
            val relationship = relationshipStore.selectByUid(relationshipUid)

            relationship?.let {
                val fromItem = relationshipItemStore
                    .getForRelationshipUidAndConstraintType(relationshipUid, RelationshipConstraintType.FROM)
                val toItem = relationshipItemStore
                    .getForRelationshipUidAndConstraintType(relationshipUid, RelationshipConstraintType.TO)
                dataStatePropagator.propagateRelationshipUpdate(
                    relationship.toBuilder()
                        .from(fromItem)
                        .to(toItem)
                        .build()
                )
            }
        }
    }

    override fun setMetadataDownloadConfig(config: GetMetadataIdsConfig): Completable {
        return Completable.fromAction {
            val value = objectMapper().writeValueAsString(config)
            smsConfigStore.set(SMSConfigKey.METADATA_CONFIG, value)
        }
    }

    override fun getMetadataDownloadConfig(): Single<GetMetadataIdsConfig> {
        return Single.fromCallable {
            val stringVal = smsConfigStore.get(SMSConfigKey.METADATA_CONFIG)
            objectMapper()
                .readValue(stringVal, GetMetadataIdsConfig::class.java)
        }
    }

    override fun setModuleEnabled(enabled: Boolean): Completable {
        return Completable.fromAction { smsConfigStore.set(SMSConfigKey.MODULE_ENABLED, enabled.toString()) }
    }

    override fun isModuleEnabled(): Single<Boolean> {
        return Single.fromCallable { smsConfigStore.get(SMSConfigKey.MODULE_ENABLED)?.toBoolean() ?: false }
    }

    override fun setWaitingForResultEnabled(enabled: Boolean): Completable {
        return Completable.fromAction { smsConfigStore.set(SMSConfigKey.WAIT_FOR_RESULT, enabled.toString()) }
    }

    override fun getWaitingForResultEnabled(): Single<Boolean> {
        return Single.fromCallable { smsConfigStore.get(SMSConfigKey.WAIT_FOR_RESULT)?.toBoolean() ?: false }
    }

    override fun getOngoingSubmissions(): Single<Map<Int, SubmissionType>> {
        return ongoingSubmissionsStore.getOngoingSubmissions()
    }

    override fun generateNextSubmissionId(): Single<Int> {
        return ongoingSubmissionsStore.generateNextSubmissionId()
    }

    override fun addOngoingSubmission(id: Int, type: SubmissionType): Completable {
        return ongoingSubmissionsStore.addOngoingSubmission(id, type)
    }

    override fun removeOngoingSubmission(id: Int): Completable {
        return ongoingSubmissionsStore.removeOngoingSubmission(id)
    }

    override fun getDataValueSet(
        dataset: String,
        orgUnit: String,
        period: String,
        attributeOptionComboUid: String
    ): Single<SMSDataValueSet> {
        return dataSetsStore.getDataValues(dataset, orgUnit, period, attributeOptionComboUid)
            .map { values: List<DataValue?>? ->
                val isCompleted = isDataValueSetCompleted(dataset, orgUnit, period, attributeOptionComboUid)
                SMSDataValueSet.builder()
                    .dataValues(values)
                    .completed(isCompleted)
                    .build()
            }
    }

    private fun isDataValueSetCompleted(
        dataset: String,
        orgUnit: String,
        period: String,
        attributeOptionComboUid: String
    ): Boolean {
        val whereClause = WhereClauseBuilder()
            .appendKeyStringValue(DataSetCompleteRegistrationTableInfo.Columns.DATA_SET, dataset)
            .appendKeyStringValue(DataSetCompleteRegistrationTableInfo.Columns.ORGANISATION_UNIT, orgUnit)
            .appendKeyStringValue(DataSetCompleteRegistrationTableInfo.Columns.PERIOD, period)
            .appendKeyStringValue(
                DataSetCompleteRegistrationTableInfo.Columns.ATTRIBUTE_OPTION_COMBO,
                attributeOptionComboUid
            )
            .appendKeyNumberValue(DataSetCompleteRegistrationTableInfo.Columns.DELETED, 0)
            .build()
        return dataSetCompleteRegistrationStore.countWhere(whereClause) > 0
    }

    override fun updateDataSetSubmissionState(
        dataSetId: String,
        orgUnit: String,
        period: String,
        attributeOptionComboUid: String,
        state: State
    ): Completable {
        return Completable.mergeArray(
            dataSetsStore.updateDataSetValuesState(
                dataSetId, orgUnit, period, attributeOptionComboUid, state
            ),
            dataSetsStore.updateDataSetCompleteRegistrationState(
                dataSetId, orgUnit, period, attributeOptionComboUid, state
            )
        )
    }

    override fun getRelationship(relationshipUid: String): Single<Relationship> {
        return Single.fromCallable { relationshipStore.selectByUid(relationshipUid) }
    }

    companion object {
        const val DefaultWaitTimeout = 120
    }
}
