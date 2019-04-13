package org.hisp.dhis.android.core.sms.domain.utils;

import org.hisp.dhis.android.core.sms.domain.repository.LocalDbRepository;
import org.hisp.dhis.smscompression.models.Metadata;

import io.reactivex.Single;

public class Common {

    private Common() {
    }

    public static Single<CompressionData> getCompressionData(LocalDbRepository localDbRepository) {
        return Single.zip(
                localDbRepository.getMetadataIds(),
                localDbRepository.getUserName(),
                CompressionData::new
        );
    }

    public static class CompressionData {
        public final String user;
        public final Metadata metadata;

        CompressionData(Metadata metadata, String user) {
            this.user = user;
            this.metadata = metadata;
        }
    }
}
