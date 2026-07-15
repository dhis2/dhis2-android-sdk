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

package org.hisp.dhis.android.core.sms.domain.interactor

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.sms.domain.converter.internal.*
import org.hisp.dhis.android.core.sms.domain.repository.SmsRepository
import org.hisp.dhis.android.core.sms.domain.repository.internal.DeviceStateRepository
import org.hisp.dhis.android.core.sms.domain.repository.internal.LocalDbRepository
import org.hisp.dhis.android.core.sms.domain.repository.internal.SubmissionType
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager
import java.util.Date

class SmsSubmitCase internal constructor(
    private val localDbRepository: LocalDbRepository,
    private val smsRepository: SmsRepository,
    private val deviceStateRepository: DeviceStateRepository,
    private val dhisVersionManager: DHISVersionManager
) {
    private var converter: Converter<*>? = null
    private var smsParts: List<String>? = null
    var submissionId: Int? = null
        private set
    private var finishedSending = false

    /**
     * Set a tracker event to send by SMS.
     * @param eventUid Event uid.
     * @return [Single] with the number of SMS to send.
     */
    fun convertTrackerEvent(eventUid: String): Single<Int> {
        return convert(TrackerEventConverter(localDbRepository, dhisVersionManager, eventUid))
    }

    /**
     * Generate the compressed message of a tracker event.
     * @param eventUid Event uid.
     * @return [Single] with the compressed message.
     */
    fun compressTrackerEvent(eventUid: String): Single<String> {
        return compress(TrackerEventConverter(localDbRepository, dhisVersionManager, eventUid))
    }

    /**
     * Set a simple event to send by SMS.
     * @param eventUid Event uid.
     * @return [Single] with the number of SMS to send.
     */
    fun convertSimpleEvent(eventUid: String): Single<Int> {
        return convert(SimpleEventConverter(localDbRepository, dhisVersionManager, eventUid))
    }

    /**
     * Generate the compressed message of a simple event.
     * @param eventUid Event uid.
     * @return [Single] with the compressed message.
     */
    fun compressSimpleEvent(eventUid: String): Single<String> {
        return compress(SimpleEventConverter(localDbRepository, dhisVersionManager, eventUid))
    }

    /**
     * Set an enrollment to send by SMS.
     * @param enrollmentUid Enrollment uid.
     * @return [Single] with the number of SMS to send.
     */
    fun convertEnrollment(enrollmentUid: String): Single<Int> {
        return convert(EnrollmentConverter(localDbRepository, dhisVersionManager, enrollmentUid))
    }

    /**
     * Generate the compressed message of an enrollment.
     * @param enrollmentUid Enrollment uid.
     * @return [Single] with the compressed message.
     */
    fun compressEnrollment(enrollmentUid: String): Single<String> {
        return compress(EnrollmentConverter(localDbRepository, dhisVersionManager, enrollmentUid))
    }

    /**
     * Set a dataSet to send by SMS.
     * @param dataSet DataSet uid.
     * @param orgUnit Organisation unit uid.
     * @param period Period identifier.
     * @param attributeOptionComboUid Attribute option combo uid.
     * @return [Single] with the number of SMS to send.
     */
    fun convertDataSet(
        dataSet: String,
        orgUnit: String,
        period: String,
        attributeOptionComboUid: String
    ): Single<Int> {
        return convert(
            DatasetConverter(
                localDbRepository,
                dhisVersionManager,
                dataSet,
                orgUnit,
                period,
                attributeOptionComboUid
            )
        )
    }

    /**
     * Generate the compressed message of a dataSet.
     * @param dataSet DataSet uid.
     * @param orgUnit Organisation unit uid.
     * @param period Period identifier.
     * @param attributeOptionComboUid Attribute option combo uid.
     * @return [Single] with the compressed message.
     */
    fun compressDataSet(
        dataSet: String,
        orgUnit: String,
        period: String,
        attributeOptionComboUid: String
    ): Single<String> {
        return compress(
            DatasetConverter(
                localDbRepository,
                dhisVersionManager,
                dataSet,
                orgUnit,
                period,
                attributeOptionComboUid
            )
        )
    }

    /**
     * Set a relationship to send by SMS.
     * @param relationshipUid Relationship uid.
     * @return [Single] with the number of SMS to send.
     */
    fun convertRelationship(relationshipUid: String): Single<Int> {
        return convert(RelationshipConverter(localDbRepository, dhisVersionManager, relationshipUid))
    }

    /**
     * Generate the compressed message of a relationship.
     * @param relationshipUid Relationship uid.
     * @return [Single] with the compressed message.
     */
    fun compressRelationship(relationshipUid: String): Single<String> {
        return compress(RelationshipConverter(localDbRepository, dhisVersionManager, relationshipUid))
    }

    /**
     * Set an event to delete by SMS.
     * @param itemToDeleteUid Event uid.
     * @return [Single] with the number of SMS to send.
     */
    fun convertDeletion(itemToDeleteUid: String): Single<Int> {
        return convert(DeletionConverter(localDbRepository, dhisVersionManager, itemToDeleteUid))
    }

    /**
     * Generate the compressed message of an event to delete.
     * @param itemToDeleteUid Event uid.
     * @return [Single] with the compressed message.
     */
    fun compressDeletion(itemToDeleteUid: String): Single<String> {
        return compress(DeletionConverter(localDbRepository, dhisVersionManager, itemToDeleteUid))
    }

    private fun convert(converter: Converter<*>): Single<Int> {
        return setConverter(converter)
            .andThen(checkAllPreconditions())
            .andThen(generateMessage(converter))
            .flatMap(smsRepository::generateSmsParts)
            .doOnSuccess { parts -> smsParts = parts }
            .map { it.size }
    }

    private fun compress(converter: Converter<*>): Single<String> {
        return setConverter(converter)
            .andThen(checkConfiguration())
            .andThen(generateMessage(converter))
    }

    private fun setConverter(converter: Converter<*>): Completable {
        return if (this.converter == null) {
            this.converter = converter
            Completable.complete()
        } else {
            Completable.error(IllegalStateException("SMS submit case should be used once"))
        }
    }

    private fun generateMessage(converter: Converter<*>): Single<String> {
        return localDbRepository.generateNextSubmissionId()
            .doOnSuccess { id -> submissionId = id }
            .flatMap(converter::readAndConvert)
    }

    /**
     * Call this method to send the SMS. You must call a "convert" method before to specify the data to send. This
     * method will fail if the app is not granted the permissions required to use SMS in the device: READ_PHONE_STATE,
     * SEND_SMS, READ_SMS and RECEIVE_SMS.
     * @return [Observable] emitting the sending states.
     */
    fun send(): Observable<SmsRepository.SmsSendingState> {
        val parts = smsParts
        if (parts.isNullOrEmpty()) {
            return Observable.error(IllegalStateException("Convert method should be called first"))
        }
        return checkAllPreconditions()
            .andThen(localDbRepository.addOngoingSubmission(submissionId!!, getSubmissionType()!!))
            .andThen(localDbRepository.getGatewayNumber())
            .flatMapObservable { number ->
                smsRepository.sendSms(number, parts, SENDING_TIMEOUT)
            }
            .flatMap { state ->
                if (!finishedSending && state.sent == state.total) {
                    finishedSending = true
                    converter!!.updateSubmissionState(State.SENT_VIA_SMS)
                        .andThen(localDbRepository.removeOngoingSubmission(submissionId!!))
                        .andThen(Observable.just(state))
                } else {
                    Observable.just(state)
                }
            }
    }

    private fun getSubmissionType(): SubmissionType? {
        return when (converter) {
            is TrackerEventConverter -> SubmissionType.TRACKER_EVENT
            is SimpleEventConverter -> SubmissionType.SIMPLE_EVENT
            is EnrollmentConverter -> SubmissionType.ENROLLMENT
            is DatasetConverter -> SubmissionType.DATA_SET
            is RelationshipConverter -> SubmissionType.RELATIONSHIP
            is DeletionConverter -> SubmissionType.DELETION
            else -> null
        }
    }

    /**
     * Observe incoming SMS waiting for a response to this submission case.
     * @param fromDate Starting date to listen for messages.
     * @return [Completable] that completes when a confirmation is received for this case.
     */
    fun checkConfirmationSms(fromDate: Date): Completable {
        return Single.zip(
            localDbRepository.getConfirmationSenderNumber(),
            localDbRepository.getWaitingResultTimeout()
        ) { number, timeout -> Pair(number, timeout) }
            .flatMapCompletable { pair ->
                smsRepository.listenToConfirmationSms(
                    fromDate,
                    pair.second,
                    pair.first,
                    submissionId!!,
                    getSubmissionType()!!
                )
            }
            .andThen(converter!!.updateSubmissionState(State.SYNCED_VIA_SMS))
    }

    fun isConfirmationMessage(sender: String, message: String): Single<Boolean> {
        return localDbRepository.getConfirmationSenderNumber()
            .flatMap { requiredSender ->
                smsRepository.isAwaitedSuccessMessage(
                    sender,
                    message,
                    requiredSender,
                    submissionId!!,
                    getSubmissionType()!!
                )
            }.doOnSuccess { isSuccess ->
                if (isSuccess) {
                    converter!!.updateSubmissionState(State.SYNCED_VIA_SMS)
                }
            }.onErrorResumeNext { error ->
                if (error is SmsRepository.ResultResponseException &&
                    error.reason == SmsRepository.ResultResponseIssue.RECEIVED_ERROR
                ) {
                    Single.just(true)
                } else {
                    Single.error(error)
                }
            }
    }

    fun markAsSentViaSMS(): Completable {
        return converter?.updateSubmissionState(State.SENT_VIA_SMS)
            ?: Completable.error(IllegalStateException("Converter is not initialized"))
    }

    private fun checkAllPreconditions(): Completable {
        return checkPermissions().andThen(checkConfiguration())
    }

    private fun checkPermissions(): Completable {
        return Completable.mergeArray(
            mapFail(
                deviceStateRepository.hasCheckNetworkPermission(),
                PreconditionFailed.Type.NO_CHECK_NETWORK_PERMISSION
            ),
            mapFail(deviceStateRepository.hasReceiveSMSPermission(), PreconditionFailed.Type.NO_RECEIVE_SMS_PERMISSION),
            mapFail(deviceStateRepository.hasSendSMSPermission(), PreconditionFailed.Type.NO_SEND_SMS_PERMISSION),
            mapFail(deviceStateRepository.isNetworkConnected(), PreconditionFailed.Type.NO_NETWORK)
        )
    }

    private fun checkConfiguration(): Completable {
        return Completable.mergeArray(
            mapFail(
                localDbRepository.getGatewayNumber().map { it.isNotEmpty() },
                PreconditionFailed.Type.NO_GATEWAY_NUMBER_SET
            ),
            mapFail(localDbRepository.getUserName().map { it.isNotEmpty() }, PreconditionFailed.Type.NO_USER_LOGGED_IN),
            mapFail(
                localDbRepository.getMetadataIds().map { it.lastSyncDate != null },
                PreconditionFailed.Type.NO_METADATA_DOWNLOADED
            ),
            mapFail(localDbRepository.isModuleEnabled(), PreconditionFailed.Type.SMS_MODULE_DISABLED)
        )
    }

    private fun mapFail(precondition: Single<Boolean>, failType: PreconditionFailed.Type): Completable {
        return precondition.flatMapCompletable { success ->
            if (success) Completable.complete() else Completable.error(Throwable())
        }.onErrorResumeNext {
            // on any error return this one
            Completable.error(PreconditionFailed(failType))
        }
    }

    class PreconditionFailed(val type: Type) : Throwable() {

        enum class Type {
            NO_NETWORK,
            NO_CHECK_NETWORK_PERMISSION,
            NO_RECEIVE_SMS_PERMISSION,
            NO_SEND_SMS_PERMISSION,
            NO_GATEWAY_NUMBER_SET,
            NO_USER_LOGGED_IN,
            NO_METADATA_DOWNLOADED,
            SMS_MODULE_DISABLED
        }

        override val message: String
            get() = when (type) {
                Type.NO_NETWORK -> "No network"
                Type.NO_CHECK_NETWORK_PERMISSION -> "No check network permission"
                Type.NO_RECEIVE_SMS_PERMISSION -> "No receive smsVersionRepository permission"
                Type.NO_SEND_SMS_PERMISSION -> "No send smsVersionRepository permission"
                Type.NO_GATEWAY_NUMBER_SET -> "No gateway number set"
                Type.NO_USER_LOGGED_IN -> "No user logged in"
                Type.NO_METADATA_DOWNLOADED -> "No metadata downloaded"
                Type.SMS_MODULE_DISABLED -> "Sms module disabled"
            }
    }

    companion object {
        private const val SENDING_TIMEOUT = 120
    }
}