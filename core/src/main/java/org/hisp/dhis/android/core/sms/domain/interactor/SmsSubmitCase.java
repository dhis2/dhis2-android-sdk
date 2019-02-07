package org.hisp.dhis.android.core.sms.domain.interactor;

import android.util.Pair;

import org.hisp.dhis.android.core.common.BaseDataModel;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.enrollment.EnrollmentModel;
import org.hisp.dhis.android.core.event.EventModel;
import org.hisp.dhis.android.core.sms.domain.converter.Converter;
import org.hisp.dhis.android.core.sms.domain.converter.EnrollmentConverter;
import org.hisp.dhis.android.core.sms.domain.converter.EventConverter;
import org.hisp.dhis.android.core.sms.domain.repository.DeviceStateRepository;
import org.hisp.dhis.android.core.sms.domain.repository.LocalDbRepository;
import org.hisp.dhis.android.core.sms.domain.repository.SmsRepository;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

public class SmsSubmitCase {
    private final LocalDbRepository localDbRepository;
    private final SmsRepository smsRepository;
    private final DeviceStateRepository deviceStateRepository;

    public SmsSubmitCase(LocalDbRepository localDbRepository, SmsRepository smsRepository,
                         DeviceStateRepository deviceStateRepository) {
        this.localDbRepository = localDbRepository;
        this.smsRepository = smsRepository;
        this.deviceStateRepository = deviceStateRepository;
    }

    public Observable<SmsRepository.SmsSendingState> submit(final EventModel event,
                                                            final List<TrackedEntityDataValueModel>
                                                                    values) {
        return submit(
                new EventConverter(),
                new EventConverter.EventData(event, values));
    }

    public Observable<SmsRepository.SmsSendingState> submit(final EnrollmentModel enrollmentModel,
                                                            final Collection<TrackedEntityAttributeValueModel>
                                                                    attributes) {
        return submit(
                new EnrollmentConverter(),
                new EnrollmentConverter.EnrollmentData(enrollmentModel, attributes));
    }

    public Completable checkConfirmationSms(EventModel event) {
        return checkConfirmationSms(new EventConverter(), event);
    }

    public Completable checkConfirmationSms(EnrollmentModel enrollment) {
        return checkConfirmationSms(new EnrollmentConverter(), enrollment);
    }

    public <T extends Converter.DataToConvert> Observable<SmsRepository.SmsSendingState>
    submit(final Converter<T, ?> converter, final T dataItem) {
        return checkPreconditions()
                .andThen(
                        localDbRepository.getGatewayNumber()
                ).flatMapObservable(gatewayNumber -> {
                    String convertedValue = converter.format(dataItem);
                    return smsRepository.sendSms(gatewayNumber, convertedValue, 120);
                }).flatMap(smsSendingState -> {
                    if (SmsRepository.State.ALL_SENT.equals(smsSendingState.getState())) {
                        return localDbRepository.updateSubmissionState(dataItem.getDataModel(), State.SENT_VIA_SMS)
                                .andThen(Observable.just(smsSendingState));
                    }
                    return Observable.just(smsSendingState);
                });
    }

    public <T extends BaseDataModel> Completable checkConfirmationSms(final Converter<?, T> converter,
                                                                      final T dataModel) {
        Collection<String> requiredStrings = converter.getConfirmationRequiredTexts(dataModel);
        return Single.zip(localDbRepository.getConfirmationSenderNumber(),
                localDbRepository.getWaitingResultTimeout(), Pair::new)
                .flatMapCompletable(config ->
                        smsRepository.listenToConfirmationSms(
                                config.second, config.first, requiredStrings))
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

    public static class PreconditionFailed extends Throwable {
    }
}
