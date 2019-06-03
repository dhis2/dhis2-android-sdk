package org.hisp.dhis.android.core.sms.domain.interactor;

import org.hisp.dhis.android.core.sms.domain.converter.EnrollmentConverter;
import org.hisp.dhis.android.core.sms.domain.converter.SimpleEventConverter;
import org.hisp.dhis.android.core.sms.domain.converter.TrackerEventConverter;
import org.hisp.dhis.android.core.sms.domain.repository.LocalDbRepository;

import io.reactivex.Single;

public class QrCodeCase {
    private final LocalDbRepository localDbRepository;

    public QrCodeCase(LocalDbRepository localDbRepository) {
        this.localDbRepository = localDbRepository;
    }

    public Single<String> generateSimpleEventCode(String eventUid) {
        return new SimpleEventConverter(localDbRepository, eventUid).readAndConvert();
    }

    public Single<String> generateTrackerEventCode(String eventUid) {
        return new TrackerEventConverter(localDbRepository, eventUid).readAndConvert();
    }

    public Single<String> generateEnrollmentCode(String enrollmentUid) {
        return new EnrollmentConverter(localDbRepository, enrollmentUid).readAndConvert();
    }
}
