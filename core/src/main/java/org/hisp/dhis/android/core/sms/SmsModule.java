package org.hisp.dhis.android.core.sms;

import org.hisp.dhis.android.core.sms.domain.interactor.InitCase;
import org.hisp.dhis.android.core.sms.domain.interactor.QrCodeCase;
import org.hisp.dhis.android.core.sms.domain.interactor.SmsSubmitCase;
import org.hisp.dhis.android.core.sms.domain.repository.DeviceStateRepository;
import org.hisp.dhis.android.core.sms.domain.repository.LocalDbRepository;
import org.hisp.dhis.android.core.sms.domain.repository.SmsRepository;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public class SmsModule {
    private final DeviceStateRepository deviceStateRepository;
    private final LocalDbRepository localDbRepository;
    private final SmsRepository smsRepository;

    @Inject
    public SmsModule(DeviceStateRepository deviceStateRepository, LocalDbRepository localDbRepository,
                     SmsRepository smsRepository) {
        this.deviceStateRepository = deviceStateRepository;
        this.localDbRepository = localDbRepository;
        this.smsRepository = smsRepository;
    }

    public InitCase initCase() {
        return new InitCase();
    }

    public QrCodeCase qrCodeCase() {
        return new QrCodeCase(localDbRepository);
    }

    public SmsSubmitCase smsSubmitCase() {
        return new SmsSubmitCase(localDbRepository, smsRepository, deviceStateRepository);
    }
}
