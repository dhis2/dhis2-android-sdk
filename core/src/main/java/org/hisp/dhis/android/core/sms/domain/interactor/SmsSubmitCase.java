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

package org.hisp.dhis.android.core.sms.domain.interactor;

import androidx.core.util.Pair;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.sms.domain.converter.internal.Converter;
import org.hisp.dhis.android.core.sms.domain.converter.internal.DatasetConverter;
import org.hisp.dhis.android.core.sms.domain.converter.internal.DeletionConverter;
import org.hisp.dhis.android.core.sms.domain.converter.internal.EnrollmentConverter;
import org.hisp.dhis.android.core.sms.domain.converter.internal.RelationshipConverter;
import org.hisp.dhis.android.core.sms.domain.converter.internal.SimpleEventConverter;
import org.hisp.dhis.android.core.sms.domain.converter.internal.TrackerEventConverter;
import org.hisp.dhis.android.core.sms.domain.repository.SmsRepository;
import org.hisp.dhis.android.core.sms.domain.repository.internal.DeviceStateRepository;
import org.hisp.dhis.android.core.sms.domain.repository.internal.LocalDbRepository;
import org.hisp.dhis.android.core.sms.domain.repository.internal.SubmissionType;
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager;

import java.util.Date;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

public class SmsSubmitCase {
    private final static int SENDING_TIMEOUT = 120;
    private final LocalDbRepository localDbRepository;
    private final SmsRepository smsRepository;
    private final DeviceStateRepository deviceStateRepository;
    private final DHISVersionManager dhisVersionManager;
    private Converter<?> converter;
    private List<String> smsParts;
    private Integer submissionId;
    private boolean finishedSending;

    public SmsSubmitCase(LocalDbRepository localDbRepository,
                         SmsRepository smsRepository,
                         DeviceStateRepository deviceStateRepository,
                         DHISVersionManager dhisVersionManager) {
        this.localDbRepository = localDbRepository;
        this.smsRepository = smsRepository;
        this.deviceStateRepository = deviceStateRepository;
        this.dhisVersionManager = dhisVersionManager;
    }

    /**
     * Set a tracker event to send by SMS.
     * @param eventUid Event uid.
     * @return {@code Single} with the number of SMS to send.
     */
    public Single<Integer> convertTrackerEvent(String eventUid) {
        return convert(new TrackerEventConverter(localDbRepository, dhisVersionManager, eventUid));
    }

    /**
     * Generate the compressed message of a tracker event.
     * @param eventUid Event uid.
     * @return {@code Single} with the compressed message.
     */
    public Single<String> compressTrackerEvent(String eventUid) {
        return compress(new TrackerEventConverter(localDbRepository, dhisVersionManager, eventUid));
    }

    /**
     * Set a simple event to send by SMS.
     * @param eventUid Event uid.
     * @return {@code Single} with the number of SMS to send.
     */
    public Single<Integer> convertSimpleEvent(String eventUid) {
        return convert(new SimpleEventConverter(localDbRepository, dhisVersionManager, eventUid));
    }

    /**
     * Generate the compressed message of a simple event.
     * @param eventUid Event uid.
     * @return {@code Single} with the compressed message.
     */
    public Single<String> compressSimpleEvent(String eventUid) {
        return compress(new SimpleEventConverter(localDbRepository, dhisVersionManager, eventUid));
    }

    /**
     * Set an enrollment to send by SMS.
     * @param enrollmentUid Enrollment uid.
     * @return {@code Single} with the number of SMS to send.
     */
    public Single<Integer> convertEnrollment(String enrollmentUid) {
        return convert(new EnrollmentConverter(localDbRepository, dhisVersionManager, enrollmentUid));
    }

    /**
     * Generate the compressed message of an enrollment.
     * @param enrollmentUid Enrollment uid.
     * @return {@code Single} with the compressed message.
     */
    public Single<String> compressEnrollment(String enrollmentUid) {
        return compress(new EnrollmentConverter(localDbRepository, dhisVersionManager, enrollmentUid));
    }

    /**
     * Set a dataSet to send by SMS.
     * @param dataSet DataSet uid.
     * @param orgUnit Organisation unit uid.
     * @param period Period identifier.
     * @param attributeOptionComboUid Attribute option combo uid.
     * @return {@code Single} with the number of SMS to send.
     */
    public Single<Integer> convertDataSet(String dataSet,
                                          String orgUnit,
                                          String period,
                                          String attributeOptionComboUid) {
        return convert(new DatasetConverter(
                localDbRepository,
                dhisVersionManager,
                dataSet,
                orgUnit,
                period,
                attributeOptionComboUid));
    }

    /**
     * Generate the compressed message of a dataSet.
     * @param dataSet DataSet uid.
     * @param orgUnit Organisation unit uid.
     * @param period Period identifier.
     * @param attributeOptionComboUid Attribute option combo uid.
     * @return {@code Single} with the compressed message.
     */
    public Single<String> compressDataSet(String dataSet,
                                          String orgUnit,
                                          String period,
                                          String attributeOptionComboUid) {
        return compress(new DatasetConverter(
                localDbRepository,
                dhisVersionManager,
                dataSet,
                orgUnit,
                period,
                attributeOptionComboUid));
    }

    /**
     * Set a relationship to send by SMS.
     * @param relationshipUid Relationship uid.
     * @return {@code Single} with the number of SMS to send.
     */
    public Single<Integer> convertRelationship(String relationshipUid) {
        return convert(new RelationshipConverter(localDbRepository, dhisVersionManager, relationshipUid));
    }

    /**
     * Generate the compressed message of a relationship.
     * @param relationshipUid Relationship uid.
     * @return {@code Single} with the compressed message.
     */
    public Single<String> compressRelationship(String relationshipUid) {
        return compress(new RelationshipConverter(localDbRepository, dhisVersionManager, relationshipUid));
    }

    /**
     * Set an event to delete by SMS.
     * @param itemToDeleteUid Event uid.
     * @return {@code Single} with the number of SMS to send.
     */
    public Single<Integer> convertDeletion(String itemToDeleteUid) {
        return convert(new DeletionConverter(localDbRepository, dhisVersionManager, itemToDeleteUid));
    }

    /**
     * Generate the compressed message of an event to delete.
     * @param itemToDeleteUid Event uid.
     * @return {@code Single} with the compressed message.
     */
    public Single<String> compressDeletion(String itemToDeleteUid) {
        return compress(new DeletionConverter(localDbRepository, dhisVersionManager, itemToDeleteUid));
    }

    private Single<Integer> convert(Converter<?> converter) {
        return setConverter(converter)
                .andThen(checkAllPreconditions())
                .andThen(generateMessage(converter))
                .flatMap(smsRepository::generateSmsParts)
                .doOnSuccess(parts -> smsParts = parts)
                .map(List::size);
    }

    private Single<String> compress(Converter<?> converter) {
        return setConverter(converter)
                .andThen(checkConfiguration())
                .andThen(generateMessage(converter));
    }

    private Completable setConverter(Converter<?> converter) {
        if (this.converter == null) {
            this.converter = converter;
            return Completable.complete();
        } else {
            return Completable.error(new IllegalStateException("SMS submit case should be used once"));
        }
    }

    private Single<String> generateMessage(Converter<?> converter) {
        return localDbRepository.generateNextSubmissionId()
                .doOnSuccess(id -> submissionId = id)
                .flatMap(converter::readAndConvert);
    }

    /**
     * Call this method to send the SMS. You must call a "convert" method before to specify the data to send. This
     * method will fail if the app is not granted the permissions required to use SMS in the device: READ_PHONE_STATE,
     * SEND_SMS, READ_SMS and RECEIVE_SMS.
     * @return {@code Observable} emitting the sending states.
     */
    public Observable<SmsRepository.SmsSendingState> send() {
        if (smsParts == null || smsParts.isEmpty()) {
            return Observable.error(new IllegalStateException("Convert method should be called first"));
        }
        return checkAllPreconditions(
        ).andThen(
                localDbRepository.addOngoingSubmission(submissionId, getSubmissionType())
        ).andThen(
                localDbRepository.getGatewayNumber()
        ).flatMapObservable(number ->
                smsRepository.sendSms(number, smsParts, SENDING_TIMEOUT)
        ).flatMap(state -> {
            if (!finishedSending && state.getSent() == state.getTotal()) {
                finishedSending = true;
                return converter.updateSubmissionState(State.SENT_VIA_SMS).andThen(
                        localDbRepository.removeOngoingSubmission(submissionId)
                ).andThen(Observable.just(state));
            }
            return Observable.just(state);
        });
    }

    /**
     * Unique identifier of this submission case.
     * @return Identifier.
     */
    public Integer getSubmissionId() {
        return submissionId;
    }

    private SubmissionType getSubmissionType() {
        if (converter instanceof TrackerEventConverter) {
            return SubmissionType.TRACKER_EVENT;
        }
        if (converter instanceof SimpleEventConverter) {
            return SubmissionType.SIMPLE_EVENT;
        }
        if (converter instanceof EnrollmentConverter) {
            return SubmissionType.ENROLLMENT;
        }
        if (converter instanceof DatasetConverter) {
            return SubmissionType.DATA_SET;
        }
        if (converter instanceof RelationshipConverter) {
            return SubmissionType.RELATIONSHIP;
        }
        if (converter instanceof DeletionConverter) {
            return SubmissionType.DELETION;
        }
        return null;
    }

    /**
     * Observe incoming SMS waiting for a response to this submission case.
     * @param fromDate Starting date to listen for messages.
     * @return {@code Completable} that completes when a confirmation is received for this case.
     */
    public Completable checkConfirmationSms(final Date fromDate) {
        return Single.zip(
                localDbRepository.getConfirmationSenderNumber(),
                localDbRepository.getWaitingResultTimeout(),
                Pair::create
        ).flatMapCompletable(pair ->
                smsRepository.listenToConfirmationSms(
                        fromDate,
                        pair.second,
                        pair.first,
                        submissionId,
                        getSubmissionType())
        ).andThen(
                converter.updateSubmissionState(State.SYNCED_VIA_SMS)
        );
    }

    public Single<Boolean> isConfirmationMessage(String sender, String message) {
        return localDbRepository.getConfirmationSenderNumber()
                .flatMap(requiredSender -> smsRepository.isAwaitedSuccessMessage(
                        sender,
                        message,
                        requiredSender,
                        submissionId,
                        getSubmissionType()
                )).doOnSuccess(isSuccess -> {
                    if (isSuccess) {
                        converter.updateSubmissionState(State.SYNCED_VIA_SMS);
                    }
                }).onErrorResumeNext(error -> {
                    if (error instanceof SmsRepository.ResultResponseException &&
                            ((SmsRepository.ResultResponseException) error).getReason() ==
                                    SmsRepository.ResultResponseIssue.RECEIVED_ERROR) {
                        return Single.just(true);
                    } else {
                        return Single.error(error);
                    }
                });
    }

    public Completable markAsSentViaSMS() {
        return converter.updateSubmissionState(State.SENT_VIA_SMS);
    }

    private Completable checkAllPreconditions() {
        return checkPermissions().andThen(checkConfiguration());
    }

    private Completable checkPermissions() {
        return Completable.mergeArray(
                mapFail(deviceStateRepository.hasCheckNetworkPermission(),
                        PreconditionFailed.Type.NO_CHECK_NETWORK_PERMISSION),
                mapFail(deviceStateRepository.hasReceiveSMSPermission(),
                        PreconditionFailed.Type.NO_RECEIVE_SMS_PERMISSION),
                mapFail(deviceStateRepository.hasSendSMSPermission(),
                        PreconditionFailed.Type.NO_SEND_SMS_PERMISSION),
                mapFail(deviceStateRepository.isNetworkConnected(),
                        PreconditionFailed.Type.NO_NETWORK)
        );
    }

    private Completable checkConfiguration() {
        return Completable.mergeArray(
                mapFail(localDbRepository.getGatewayNumber().map(number -> number.length() > 0),
                        PreconditionFailed.Type.NO_GATEWAY_NUMBER_SET),
                mapFail(localDbRepository.getUserName().map(username -> username.length() > 0),
                        PreconditionFailed.Type.NO_USER_LOGGED_IN),
                mapFail(localDbRepository.getMetadataIds().map(ids -> ids.lastSyncDate != null),
                        PreconditionFailed.Type.NO_METADATA_DOWNLOADED),
                mapFail(localDbRepository.isModuleEnabled(),
                        PreconditionFailed.Type.SMS_MODULE_DISABLED)
        );
    }

    private Completable mapFail(Single<Boolean> precondition, PreconditionFailed.Type failType) {
        return precondition.flatMapCompletable(success ->
                success ? Completable.complete() : Completable.error(new Throwable()))
                .onErrorResumeNext(error ->
                        // on any error return this one
                        Completable.error(new PreconditionFailed(failType))
                );
    }

    public static class PreconditionFailed extends Throwable {
        private final Type type;

        PreconditionFailed(Type type) {
            this.type = type;
        }

        public Type getType() {
            return type;
        }

        public enum Type {
            NO_NETWORK,
            NO_CHECK_NETWORK_PERMISSION,
            NO_RECEIVE_SMS_PERMISSION,
            NO_SEND_SMS_PERMISSION,
            NO_GATEWAY_NUMBER_SET,
            NO_USER_LOGGED_IN,
            NO_METADATA_DOWNLOADED,
            SMS_MODULE_DISABLED
        }

        @Override
        public String getMessage() {
            switch (type) {
                case NO_NETWORK:
                    return "No network";
                case NO_CHECK_NETWORK_PERMISSION:
                    return "No check network permission";
                case NO_RECEIVE_SMS_PERMISSION:
                    return "No receive smsVersionRepository permission";
                case NO_SEND_SMS_PERMISSION:
                    return "No send smsVersionRepository permission";
                case NO_GATEWAY_NUMBER_SET:
                    return "No gateway number set";
                case NO_USER_LOGGED_IN:
                    return "No user logged in";
                case NO_METADATA_DOWNLOADED:
                    return "No metadata downloaded";
                case SMS_MODULE_DISABLED:
                    return "Sms module disabled";
                default:
                    return super.getMessage();
            }
        }
    }
}
