package org.hisp.dhis.android.core.sms.domain.interactor;

import androidx.core.util.Pair;

import org.hisp.dhis.android.core.common.BaseDataModel;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.sms.domain.converter.Converter;
import org.hisp.dhis.android.core.sms.domain.converter.DatasetConverter;
import org.hisp.dhis.android.core.sms.domain.converter.EnrollmentConverter;
import org.hisp.dhis.android.core.sms.domain.converter.SimpleEventConverter;
import org.hisp.dhis.android.core.sms.domain.converter.TrackerEventConverter;
import org.hisp.dhis.android.core.sms.domain.repository.DeviceStateRepository;
import org.hisp.dhis.android.core.sms.domain.repository.LocalDbRepository;
import org.hisp.dhis.android.core.sms.domain.repository.SmsRepository;

import java.util.Collection;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

public class SmsSubmitCase {
    private final static int SENDING_TIMEOUT = 120;
    private final LocalDbRepository localDbRepository;
    private final SmsRepository smsRepository;
    private final DeviceStateRepository deviceStateRepository;
    private Converter<?> converter;
    private List<String> smsParts;
    private Integer submissionId;
    private boolean finishedSending;

    public SmsSubmitCase(LocalDbRepository localDbRepository, SmsRepository smsRepository,
                         DeviceStateRepository deviceStateRepository) {
        this.localDbRepository = localDbRepository;
        this.smsRepository = smsRepository;
        this.deviceStateRepository = deviceStateRepository;
    }

    public Single<Integer> convertTrackerEvent(String eventUid) {
        return convert(new TrackerEventConverter(localDbRepository, eventUid));
    }

    public Single<Integer> convertSimpleEvent(String eventUid) {
        return convert(new SimpleEventConverter(localDbRepository, eventUid));
    }

    public Single<Integer> convertEnrollment(String enrollmentUid) {
        return convert(new EnrollmentConverter(localDbRepository, enrollmentUid));
    }

    public Single<Integer> convertDataSet(String orgUnit, String period, String attributeOptionComboUid) {
        return convert(new DatasetConverter(localDbRepository, orgUnit, period, attributeOptionComboUid));
    }

    private Single<Integer> convert(Converter<?> converter) {
        if (this.converter != null) {
            return Single.error(new IllegalStateException("SMS submit case should be used once"));
        }
        this.converter = converter;
        return checkPreconditions()
                .andThen(generateSubmissionId()
                ).flatMap(converter::readAndConvert
                ).flatMap(smsRepository::generateSmsParts
                ).map(parts -> {
                    smsParts = parts;
                    return parts.size();
                });
    }

    private Single<Integer> generateSubmissionId() {
        return localDbRepository.getOngoingSubmissions().flatMap(submissions -> {
            Collection<Integer> ids = submissions.keySet();
            for (int i = 0; i <= 255; i++) {
                if (!ids.contains(i)) {
                    submissionId = i;
                    return Single.just(i);
                }
            }
            submissionId = null;
            return Single.error(new TooManySubmissionsException());
        });
    }

    public Observable<SmsRepository.SmsSendingState> send() {
        if (smsParts == null || smsParts.isEmpty()) {
            return Observable.error(new IllegalStateException("Convert method should be called first"));
        }
        return checkPreconditions(
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

    private LocalDbRepository.SubmissionType getSubmissionType() {
        if (converter instanceof TrackerEventConverter) {
            return LocalDbRepository.SubmissionType.TRACKER_EVENT;
        }
        if (converter instanceof SimpleEventConverter) {
            return LocalDbRepository.SubmissionType.SIMPLE_EVENT;
        }
        if (converter instanceof EnrollmentConverter) {
            return LocalDbRepository.SubmissionType.ENROLLMENT;
        }
        return null;
    }

    public <T extends BaseDataModel> Completable checkConfirmationSms(final boolean searchReceived,
                                                                      final Collection<String> requiredStrings,
                                                                      final T dataModel) {
        return Single.zip(
                localDbRepository.getConfirmationSenderNumber(),
                localDbRepository.getWaitingResultTimeout(),
                Pair::create
        ).flatMapCompletable(pair ->
                smsRepository.listenToConfirmationSms(
                        searchReceived,
                        pair.second,
                        pair.first,
                        requiredStrings)
        );
    }

    private Completable checkPreconditions() {
        return Completable.mergeArray(
                mapFail(deviceStateRepository.hasCheckNetworkPermission(),
                        PreconditionFailed.Type.NO_CHECK_NETWORK_PERMISSION),
                mapFail(deviceStateRepository.hasReceiveSMSPermission(),
                        PreconditionFailed.Type.NO_RECEIVE_SMS_PERMISSION),
                mapFail(deviceStateRepository.hasSendSMSPermission(),
                        PreconditionFailed.Type.NO_SEND_SMS_PERMISSION),
                mapFail(deviceStateRepository.isNetworkConnected(),
                        PreconditionFailed.Type.NO_NETWORK),
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
    }

    public static class TooManySubmissionsException extends IllegalStateException {
        TooManySubmissionsException() {
            super("Too many ongoing submissions at the same time >255");
        }
    }
}
