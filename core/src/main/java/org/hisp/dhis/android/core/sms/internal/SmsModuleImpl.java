package org.hisp.dhis.android.core.sms.internal;

import org.hisp.dhis.android.core.sms.SmsModule;
import org.hisp.dhis.android.core.sms.domain.interactor.ConfigCase;
import org.hisp.dhis.android.core.sms.domain.interactor.QrCodeCase;
import org.hisp.dhis.android.core.sms.domain.interactor.SmsSubmitCase;
import org.hisp.dhis.android.core.sms.domain.repository.SmsRepository;
import org.hisp.dhis.android.core.sms.domain.repository.WebApiRepository;
import org.hisp.dhis.android.core.sms.domain.repository.internal.DeviceStateRepository;
import org.hisp.dhis.android.core.sms.domain.repository.internal.LocalDbRepository;
import org.hisp.dhis.android.core.sms.domain.repository.internal.SmsVersionRepository;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
class SmsModuleImpl implements SmsModule {
    private final DeviceStateRepository deviceStateRepository;
    private final LocalDbRepository localDbRepository;
    private final SmsRepository smsRepository;
    private final WebApiRepository webApiRepository;
    private final SmsVersionRepository smsVersionRepository;

    @Inject
    SmsModuleImpl(DeviceStateRepository deviceStateRepository, LocalDbRepository localDbRepository,
                  SmsRepository smsRepository, WebApiRepository webApiRepository,
                  SmsVersionRepository smsVersionRepository) {
        this.deviceStateRepository = deviceStateRepository;
        this.localDbRepository = localDbRepository;
        this.smsRepository = smsRepository;
        this.webApiRepository = webApiRepository;
        this.smsVersionRepository = smsVersionRepository;
    }

    @Override
    public ConfigCase configCase() {
        return new ConfigCase(webApiRepository, localDbRepository);
    }

    @Override
    public QrCodeCase qrCodeCase() {
        return new QrCodeCase(localDbRepository, smsVersionRepository);
    }

    @Override
    public SmsSubmitCase smsSubmitCase() {
        return new SmsSubmitCase(localDbRepository, smsRepository, deviceStateRepository, smsVersionRepository);
    }
}
