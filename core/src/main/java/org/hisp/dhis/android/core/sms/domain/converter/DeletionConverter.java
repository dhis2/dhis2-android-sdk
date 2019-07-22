package org.hisp.dhis.android.core.sms.domain.converter;

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.sms.domain.repository.LocalDbRepository;
import org.hisp.dhis.smscompression.models.DeleteSMSSubmission;
import org.hisp.dhis.smscompression.models.SMSSubmission;

import io.reactivex.Completable;
import io.reactivex.Single;

public class DeletionConverter extends Converter<String> {
    private final String uid;

    public DeletionConverter(LocalDbRepository localDbRepository, String uid) {
        super(localDbRepository);
        this.uid = uid;
    }

    @Override
    Single<? extends SMSSubmission> convert(@NonNull String uid, String user, int submissionId) {
        return Single.fromCallable(() -> {
            DeleteSMSSubmission subm = new DeleteSMSSubmission();
            subm.setSubmissionID(submissionId);
            subm.setUserID(user);
            subm.setEvent(uid);
            return subm;
        });
    }

    @Override
    public Completable updateSubmissionState(State state) {
        // there is no submission state update for deletion
        return Completable.complete();
    }

    @Override
    Single<String> readItemFromDb() {
        return Single.just(uid);
    }
}
