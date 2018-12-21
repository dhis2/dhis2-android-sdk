package org.hisp.dhis.android.core.sms.domain.interactor;

import android.util.Pair;

import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.sms.domain.SmsFormatConverter;
import org.hisp.dhis.android.core.sms.domain.repository.LocalDbRepository;
import org.hisp.dhis.android.core.sms.domain.repository.SmsRepository;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

public class SmsSubmitCase {
    private LocalDbRepository localDbRepository;
    private SmsRepository smsRepository;
    private SmsFormatConverter converter;

    // TODO inject repos
    public SmsSubmitCase() {

    }

    public Observable<SmsSubmissionState> submit(final Event event) {
        return checkPreconditions()
                .andThen(Single.zip(localDbRepository.getNumber(), localDbRepository.getUserName(),
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

    private Completable checkPreconditions() {
        return Completable.fromAction(() -> {
            // TODO check preconditions, throw exception in case of condition not passed
        });
    }

    public class SmsSubmissionState {

    }
}
