package org.hisp.dhis.android.core.sms.mockrepos;

import org.hisp.dhis.android.core.sms.domain.repository.WebApiRepository;
import org.hisp.dhis.android.core.sms.mockrepos.testobjects.MockMetadata;
import org.hisp.dhis.smscompression.models.SMSMetadata;

import io.reactivex.Single;

public class MockWebApiRepository implements WebApiRepository {
    private SMSMetadata metadata = new MockMetadata();

    @Override
    public Single<SMSMetadata> getMetadataIds(GetMetadataIdsConfig config) {
        return Single.fromCallable(() -> metadata);
    }
}
