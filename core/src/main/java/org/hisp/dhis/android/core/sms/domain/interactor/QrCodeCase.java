package org.hisp.dhis.android.core.sms.domain.interactor;

import org.hisp.dhis.android.core.sms.domain.converter.EnrollmentConverter;
import org.hisp.dhis.android.core.sms.domain.converter.EventConverter;
import org.hisp.dhis.android.core.sms.domain.repository.LocalDbRepository;

import io.reactivex.Single;

public class QrCodeCase {
    private final LocalDbRepository localDbRepository;

    public QrCodeCase(LocalDbRepository localDbRepository) {
        this.localDbRepository = localDbRepository;
    }

    public Single<String> generateEventCode(String eventUid, String teiUid) {
        return new EventConverter(localDbRepository, eventUid, teiUid).readAndConvert();
    }

    public Single<String> generateEnrollmentCode(String enrollmentUid, String teiUid) {
        return new EnrollmentConverter(localDbRepository, enrollmentUid, teiUid).readAndConvert();
    }
}
