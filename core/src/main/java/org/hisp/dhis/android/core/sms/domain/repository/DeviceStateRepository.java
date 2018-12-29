package org.hisp.dhis.android.core.sms.domain.repository;

import io.reactivex.Single;

public interface DeviceStateRepository {

    /**
     * @return Information if network is connected and able to send sms
     */
    Single<Boolean> isNetworkConnected();

    /**
     * @return Information if possible to check network state
     */
    Single<Boolean> hasCheckNetworkPermission();

    /**
     * @return Information if possible to send SMS
     */
    Single<Boolean> hasSendSMSPermission();

    /**
     * @return Information if possible to receive and read SMS
     */
    Single<Boolean> hasReceiveSMSPermission();
}
