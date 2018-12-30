package org.hisp.dhis.android.core.sms.domain.interactor;

import android.util.Pair;

import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.sms.domain.SmsFormatConverter;
import org.hisp.dhis.android.core.sms.domain.repository.DeviceStateRepository;
import org.hisp.dhis.android.core.sms.domain.repository.LocalDbRepository;
import org.hisp.dhis.android.core.sms.domain.repository.SmsRepository;

import java.util.ArrayList;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

public class SmsSubmitCase {
    private LocalDbRepository localDbRepository;
    private SmsRepository smsRepository;
    private SmsFormatConverter converter;
    private DeviceStateRepository deviceStateRepository;

    // TODO inject repos
    public SmsSubmitCase() {

    }

    public Observable<SmsSubmissionState> submit(final Event event) {
        return checkPreconditions()
                .andThen(Single.zip(localDbRepository.getGatewayNumber(), localDbRepository.getUserName(),
                        (number, username) -> {
                            String smsContents = converter.format(username, event);
                            return new Pair<>(number, smsContents);
                        })
                ).flatMapObservable(numAndContents ->
                        smsRepository.sendSms(numAndContents.first, numAndContents.second, 120)
                ).map(smsSendingStatus -> {
                    // TODO translate properly to sms submission state
                    return new SmsSubmissionState();
                });
    }

    public Completable checkConfirmationSms(int timeoutSeconds, Event event) {
        // TODO translate event to required texts
        return localDbRepository.getConfirmationSenderNumber().flatMapCompletable(confirmationSenderNumber ->
                smsRepository.listenToConfirmationSms(timeoutSeconds, confirmationSenderNumber, null)
        );
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
            if (!checkPassed) return Completable.error(new PreconditionFailed());
            return Completable.complete();
        });

    }

    public class SmsSubmissionState {

    }

    public class PreconditionFailed extends Throwable {
    }
}
