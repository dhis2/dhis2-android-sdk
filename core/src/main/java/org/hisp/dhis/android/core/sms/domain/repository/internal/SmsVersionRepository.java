package org.hisp.dhis.android.core.sms.domain.repository.internal;

import org.hisp.dhis.android.core.maintenance.D2Error;

public interface SmsVersionRepository {

    /**
     *
     * @return Version number of current the compression library used in the backend
     */
    Integer getSMSVersion() throws D2Error;
}
