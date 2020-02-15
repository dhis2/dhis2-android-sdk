package org.hisp.dhis.android.core.sms.mockrepos;

import org.hisp.dhis.android.core.sms.domain.repository.internal.SmsVersionRepository;

public class MockSmsVersionRepository implements SmsVersionRepository {

    private final Integer MOCK_VERSION = 2;

    @Override
    public Integer getSMSVersion() {
        return MOCK_VERSION;
    }
}
