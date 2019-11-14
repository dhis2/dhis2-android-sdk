package org.hisp.dhis.android.core.sms;

import org.hisp.dhis.android.core.sms.domain.interactor.ConfigCase;
import org.hisp.dhis.android.core.sms.domain.interactor.QrCodeCase;
import org.hisp.dhis.android.core.sms.domain.interactor.SmsSubmitCase;

public interface SmsModule {
    ConfigCase configCase();
    QrCodeCase qrCodeCase();
    SmsSubmitCase smsSubmitCase();
}
