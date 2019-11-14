package org.hisp.dhis.android.core.sms.domain.repository;

import org.hisp.dhis.smscompression.models.SMSMetadata;

import io.reactivex.Single;

public interface WebApiRepository {

    /**
     * @return Metadata object that contains ids lists needed to properly compress sms data
     */
    Single<SMSMetadata> getMetadataIds(GetMetadataIdsConfig config);

    class GetMetadataIdsConfig {
        public boolean dataElements = true;
        public boolean categoryOptionCombos = true;
        public boolean organisationUnits = true;
        public boolean users = true;
        public boolean trackedEntityTypes = true;
        public boolean trackedEntityAttributes = true;
        public boolean programs = true;
    }

    class HttpException extends RuntimeException {
        private final int code;

        public HttpException(int code) {
            this.code = code;
        }

        @Override
        public String toString() {
            return "HTTP response: " + " " + code;
        }
    }
}
