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
import org.hisp.dhis.android.core.sms.domain.utils.Pair;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;

import java.util.ArrayList;
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

    public SmsSubmitCase(LocalDbRepository localDbRepository, SmsRepository smsRepository,
                         DeviceStateRepository deviceStateRepository) {
        this.localDbRepository = localDbRepository;
        this.smsRepository = smsRepository;
        this.deviceStateRepository = deviceStateRepository;
    }

    public Observable<SmsRepository.SmsSendingState> submit(final Event event,
                                                            final List<TrackedEntityDataValue>
                                                                    values) {
        return submit(
                new EventConverter(),
                new EventConverter.EventData(event, values));
    }

    public Observable<SmsRepository.SmsSendingState> submit(final Enrollment enrollment,
                                                            final String trackedEntityType,
                                                            final Collection<TrackedEntityAttributeValue>
                                                                    attributes) {
        return Single.zip(
                localDbRepository.getMetadataIds(),
                localDbRepository.getUserName(),
                Pair::create
        ).flatMapObservable(pair -> submit(
                new EnrollmentConverter(pair.first),
                new EnrollmentConverter.EnrollmentData(enrollment, trackedEntityType, attributes, pair.second)
        ));
    }

    public Completable checkConfirmationSms(Event event) {
        return checkConfirmationSms(new EventConverter(), event);
    }

    public Completable checkConfirmationSms(Enrollment enrollment) {
        return localDbRepository.getMetadataIds().flatMapCompletable(metadata ->
                checkConfirmationSms(new EnrollmentConverter(metadata), enrollment)
        );
    }

    public <T extends DataToConvert> Observable<SmsRepository.SmsSendingState>
    submit(final Converter<T, ?> converter, final T dataItem) {
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

    public <T extends BaseDataModel> Completable checkConfirmationSms(final Converter<?, T> converter,
                                                                      final T dataModel) {
        return Single.zip(localDbRepository.getConfirmationSenderNumber(),
                converter.getConfirmationRequiredTexts(dataModel),
                localDbRepository.getWaitingResultTimeout(),
                ResultCheckData::create
        ).flatMapCompletable(config ->
                smsRepository.listenToConfirmationSms(
                        config.waitingResultTimeout, config.confirmationSenderNumber, config.requiredStrings))
                .andThen(localDbRepository.updateSubmissionState(dataModel, State.SYNCED_VIA_SMS));
    }

    private Completable checkPreconditions() {
        ArrayList<Single<Boolean>> checks = new ArrayList<>();
        checks.add(deviceStateRepository.hasCheckNetworkPermission());
        checks.add(deviceStateRepository.hasReceiveSMSPermission());
        checks.add(deviceStateRepository.hasSendSMSPermission());
        checks.add(deviceStateRepository.isNetworkConnected());
        checks.add(localDbRepository.getGatewayNumber().map(number -> number.length() > 0));
        checks.add(localDbRepository.getUserName().map(username -> username.length() > 0));

        return Single.merge(checks).flatMapCompletable(checkPassed -> {
            if (!checkPassed) {
                return Completable.error(new PreconditionFailed());
            }
            return Completable.complete();
        });
    }

    private static class ResultCheckData {
        String confirmationSenderNumber;
        Collection<String> requiredStrings;
        int waitingResultTimeout;

        private ResultCheckData() {
        }

        static ResultCheckData create(String confirmationSenderNumber,
                                      Collection<String> requiredStrings,
                                      int waitingResultTimeout) {
            ResultCheckData data = new ResultCheckData();
            data.confirmationSenderNumber = confirmationSenderNumber;
            data.requiredStrings = requiredStrings;
            data.waitingResultTimeout = waitingResultTimeout;
            return data;
        }
    }

    public static class PreconditionFailed extends Throwable {
    }
}
