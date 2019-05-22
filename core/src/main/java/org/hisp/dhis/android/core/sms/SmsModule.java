package org.hisp.dhis.android.core.sms;

import org.hisp.dhis.android.core.sms.domain.interactor.ConfigCase;
import org.hisp.dhis.android.core.sms.domain.interactor.QrCodeCase;
import org.hisp.dhis.android.core.sms.domain.interactor.SmsSubmitCase;
import org.hisp.dhis.android.core.sms.domain.repository.DeviceStateRepository;
import org.hisp.dhis.android.core.sms.domain.repository.LocalDbRepository;
import org.hisp.dhis.android.core.sms.domain.repository.SmsRepository;
import org.hisp.dhis.android.core.sms.domain.repository.WebApiRepository;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public class SmsModule {
    private final DeviceStateRepository deviceStateRepository;
    private final LocalDbRepository localDbRepository;
    private final SmsRepository smsRepository;
    private final WebApiRepository webApiRepository;

    @Inject
    public SmsModule(DeviceStateRepository deviceStateRepository, LocalDbRepository localDbRepository,
                     SmsRepository smsRepository, WebApiRepository webApiRepository) {
        this.deviceStateRepository = deviceStateRepository;
        this.localDbRepository = localDbRepository;
        this.smsRepository = smsRepository;
        this.webApiRepository = webApiRepository;
    }

    public ConfigCase configCase() {
        return new ConfigCase(webApiRepository, localDbRepository);
    }

    public QrCodeCase qrCodeCase() {
        return new QrCodeCase(localDbRepository);
    }

    public SmsSubmitCase smsSubmitCase() {
        return new SmsSubmitCase(localDbRepository, smsRepository, deviceStateRepository);
    }
}
