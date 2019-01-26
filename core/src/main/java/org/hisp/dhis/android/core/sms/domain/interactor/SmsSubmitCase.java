package org.hisp.dhis.android.core.sms.domain.interactor;

import android.util.Pair;

import org.hisp.dhis.android.core.common.BaseDataModel;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.sms.domain.converter.Converter;
import org.hisp.dhis.android.core.sms.domain.converter.EventConverter;
import org.hisp.dhis.android.core.sms.domain.repository.DeviceStateRepository;
import org.hisp.dhis.android.core.sms.domain.repository.LocalDbRepository;
import org.hisp.dhis.android.core.sms.domain.repository.SmsRepository;

import java.util.ArrayList;
import java.util.Collection;

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

    public Observable<SmsRepository.SmsSendingState> submit(Event event) {
        return submit(new EventConverter(localDbRepository), event);
    }

    public Completable checkConfirmationSms(Event event, int timeoutSeconds) {
        return checkConfirmationSms(new EventConverter(localDbRepository), event, timeoutSeconds);
    }

    public <T extends BaseDataModel> Observable<SmsRepository.SmsSendingState>
    submit(final Converter<T, ?> converter, final T dataItem) {
        return checkPreconditions()
                .andThen(
                        Single.zip(localDbRepository.getGatewayNumber(), converter.format(dataItem), Pair::new)
                ).flatMapObservable(numAndContents ->
                        smsRepository.sendSms(numAndContents.first, numAndContents.second, 120)
                ).flatMap(smsSendingState -> {
                    if (SmsRepository.State.ALL_SENT.equals(smsSendingState.getState())) {
                        return localDbRepository.updateSubmissionState(dataItem, State.SENT_VIA_SMS)
                                .andThen(Observable.just(smsSendingState));
                    }
                    return Observable.just(smsSendingState);
                });
    }

    public <T extends BaseDataModel> Completable checkConfirmationSms(final Converter<T, ?> converter,
                                                                      final T dataItem, int timeoutSeconds) {
        Collection<String> requiredStrings = converter.getConfirmationRequiredTexts(dataItem);
        return localDbRepository.getConfirmationSenderNumber()
                .flatMapCompletable(confirmationSenderNumber ->
                        smsRepository.listenToConfirmationSms(
                                timeoutSeconds, confirmationSenderNumber, requiredStrings))
                .andThen(localDbRepository.updateSubmissionState(dataItem, State.SYNCED_VIA_SMS));
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
