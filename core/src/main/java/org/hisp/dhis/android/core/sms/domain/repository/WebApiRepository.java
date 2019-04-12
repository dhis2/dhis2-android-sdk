package org.hisp.dhis.android.core.sms.domain.repository;

import org.hisp.dhis.smscompression.models.Metadata;

import io.reactivex.Single;

public interface WebApiRepository {

    /**
     * @return Metadata object that contains ids lists needed to properly compress sms data
     */
    Single<Metadata> getMetadataIds(GetMetadataIdsConfig config);

    class GetMetadataIdsConfig {
        public boolean dataElements;
        public boolean categoryOptionCombos;
        public boolean organisationUnits;
        public boolean users;
        public boolean trackedEntityTypes;
        public boolean trackedEntityAttributes;
        public boolean programs;
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
