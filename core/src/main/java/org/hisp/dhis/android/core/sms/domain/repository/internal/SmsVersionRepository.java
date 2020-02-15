package org.hisp.dhis.android.core.sms.domain.repository.internal;

public interface SmsVersionRepository {

    /**
     *
     * @return Version number of current the compression library used in the backend
     */
    Integer getSMSVersion();
}
