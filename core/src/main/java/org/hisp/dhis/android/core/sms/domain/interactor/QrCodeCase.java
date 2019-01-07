package org.hisp.dhis.android.core.sms.domain.interactor;

import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.sms.domain.SmsFormatConverter;
import org.hisp.dhis.android.core.sms.domain.repository.LocalDbRepository;

import io.reactivex.Single;

public class QrCodeCase {
    private LocalDbRepository localDbRepository;
    private SmsFormatConverter converter;

    // TODO inject repos
    public QrCodeCase() {
    }

    public Single<String> generateTextCode(final Event event) {
        return Single.zip(localDbRepository.getUserName(), localDbRepository.getDefaultCategoryOptionCombo(),
                (username, categoryOptionCombo) -> converter.format(event, username, categoryOptionCombo));
    }
}
