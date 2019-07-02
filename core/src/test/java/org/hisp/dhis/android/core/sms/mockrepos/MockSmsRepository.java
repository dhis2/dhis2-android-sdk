package org.hisp.dhis.android.core.sms.mockrepos;

import org.hisp.dhis.android.core.sms.domain.repository.SmsRepository;
import org.hisp.dhis.android.core.sms.domain.repository.SubmissionType;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

public class MockSmsRepository implements SmsRepository {
    @Override
    public Observable<SmsSendingState> sendSms(String number, List<String> smsParts, int sendingTimeoutSeconds) {
        return Observable.defer(() -> Observable.just(
                new SmsSendingState(0, 1),
                new SmsSendingState(1, 1)
        ));
    }

    @Override
    public Single<List<String>> generateSmsParts(String value) {
        return Single.fromCallable(() -> Collections.singletonList(value));
    }

    @Override
    public Completable listenToConfirmationSms(Date fromDate, int waitingTimeoutSeconds, String requiredSender, int submissionId, SubmissionType submissionType) {
        return Completable.complete();
    }
}
