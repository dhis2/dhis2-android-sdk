package org.hisp.dhis.android.core.sms.domain.interactor;

import org.hisp.dhis.android.core.common.BaseDataModel;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.sms.domain.converter.Converter;
import org.hisp.dhis.android.core.sms.domain.converter.Converter.DataToConvert;
import org.hisp.dhis.android.core.sms.domain.converter.EnrollmentConverter;
import org.hisp.dhis.android.core.sms.domain.converter.EventConverter;
import org.hisp.dhis.android.core.sms.domain.repository.DeviceStateRepository;
import org.hisp.dhis.android.core.sms.domain.repository.LocalDbRepository;
import org.hisp.dhis.android.core.sms.domain.repository.SmsRepository;
import org.hisp.dhis.android.core.sms.domain.utils.Common;
import org.hisp.dhis.android.core.sms.domain.utils.Pair;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;

import java.util.Collection;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

public class SmsSubmitCase {
    private final static int SENDING_TIMEOUT = 120;
    private final LocalDbRepository localDbRepository;
    private final SmsRepository smsRepository;
    private final DeviceStateRepository deviceStateRepository;

    public SmsSubmitCase(LocalDbRepository localDbRepository, SmsRepository smsRepository,
                         DeviceStateRepository deviceStateRepository) {
        this.localDbRepository = localDbRepository;
        this.smsRepository = smsRepository;
        this.deviceStateRepository = deviceStateRepository;
    }

    public void acceptSMSCount(boolean accept) {
        smsRepository.acceptSMSCount(accept);
    }

    public Observable<SmsRepository.SmsSendingState> submit(final Event event) {
        return Common.getCompressionData(localDbRepository).flatMapObservable(data -> submit(
                new EventConverter(data.metadata),
                new EventConverter.EventData(event, data.user)
        ));
    }

    public Observable<SmsRepository.SmsSendingState> submit(final Enrollment enrollment,
                                                            final String trackedEntityType,
                                                            final Collection<TrackedEntityAttributeValue>
                                                                    attributes) {
        return Common.getCompressionData(localDbRepository).flatMapObservable(data -> submit(
                new EnrollmentConverter(data.metadata),
                new EnrollmentConverter.EnrollmentData(enrollment, trackedEntityType, attributes, data.user)
        ));
    }

    private <T extends DataToConvert> Observable<SmsRepository.SmsSendingState>
    submit(final Converter<T> converter, final T dataItem) {
        return checkPreconditions()
                .andThen(Single.zip(
                        localDbRepository.getGatewayNumber(),
                        converter.format(dataItem),
                        Pair::create)
                ).flatMapObservable(numAndContents ->
                        smsRepository.sendSms(numAndContents.first, numAndContents.second, SENDING_TIMEOUT))
                .flatMap(smsSendingState -> {
                    if (SmsRepository.State.ALL_SENT.equals(smsSendingState.getState())) {
                        return localDbRepository.updateSubmissionState(dataItem.getDataModel(), State.SENT_VIA_SMS)
                                .andThen(Observable.just(smsSendingState));
                    }
                    return Observable.just(smsSendingState);
                });
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
        ).andThen(
                localDbRepository.updateSubmissionState(dataModel, State.SYNCED_VIA_SMS)
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
                        PreconditionFailed.Type.NO_USER_LOGGED_IN)
        );
    }

    private Completable mapFail(Single<Boolean> precondition, PreconditionFailed.Type failType) {
        return precondition.flatMapCompletable(success ->
                success ? Completable.complete() : Completable.error(new PreconditionFailed(failType)));
    }

    public static class PreconditionFailed extends Throwable {
        private final Type type;

        public PreconditionFailed(Type type) {
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
            NO_USER_LOGGED_IN
        }
    }
}
