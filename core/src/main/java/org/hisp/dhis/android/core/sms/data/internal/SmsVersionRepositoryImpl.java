package org.hisp.dhis.android.core.sms.data.internal;

import org.hisp.dhis.android.core.sms.domain.repository.internal.SmsVersionRepository;
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager;

public class SmsVersionRepositoryImpl implements SmsVersionRepository {

    private final DHISVersionManager dhisVersionManager;

    public SmsVersionRepositoryImpl(DHISVersionManager dhisVersionManager) {
        this.dhisVersionManager = dhisVersionManager;
    }

    @Override
    public Integer getSMSVersion() {
        return null;
    }
}
