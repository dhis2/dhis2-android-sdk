/*
 * Copyright (c) 2016, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.client.sdk.core.systeminfo;

import org.hisp.dhis.client.sdk.core.common.controllers.SyncStrategy;
import org.hisp.dhis.client.sdk.core.common.network.ApiException;
import org.hisp.dhis.client.sdk.core.common.preferences.DateType;
import org.hisp.dhis.client.sdk.core.common.preferences.LastUpdatedPreferences;
import org.hisp.dhis.client.sdk.core.common.preferences.ResourceType;
import org.hisp.dhis.client.sdk.models.common.SystemInfo;
import org.joda.time.DateTime;
import org.joda.time.Seconds;

public class SystemInfoControllerImpl implements SystemInfoController {
    private static final int EXPIRATION_THRESHOLD = 128;

    /* API clients */
    private final SystemInfoApiClient systemInfoApiClient;

    /* Stores and preferences */
    private final SystemInfoPreferences systemInfoPreferences;
    private final LastUpdatedPreferences lastUpdatedPreferences;

    public SystemInfoControllerImpl(SystemInfoApiClient systemInfoApiClient,
                                    SystemInfoPreferences systemInfoPreferences,
                                    LastUpdatedPreferences lastUpdatedPreferences) {
        this.systemInfoApiClient = systemInfoApiClient;
        this.systemInfoPreferences = systemInfoPreferences;
        this.lastUpdatedPreferences = lastUpdatedPreferences;
    }

    @Override
    public SystemInfo getSystemInfo() throws ApiException {
        return getSystemInfo(SyncStrategy.DEFAULT);
    }

    @Override
    public SystemInfo getSystemInfo(SyncStrategy strategy) throws ApiException {
        SystemInfo systemInfo = systemInfoPreferences.get();
        DateTime currentDate = DateTime.now();

        if (SyncStrategy.FORCE_UPDATE.equals(strategy) || (SyncStrategy.DEFAULT.equals(strategy) &&
                isSystemInfoExpired(currentDate))) {
            systemInfo = systemInfoApiClient.getSystemInfo();

            lastUpdatedPreferences.save(ResourceType.SYSTEM_INFO, DateType.LOCAL, currentDate);
            systemInfoPreferences.save(systemInfo);
        }

        return systemInfo;
    }

    private boolean isSystemInfoExpired(DateTime currentDate) {
        DateTime lastUpdated = lastUpdatedPreferences.get(
                ResourceType.SYSTEM_INFO, DateType.LOCAL);
        return lastUpdated == null || Seconds.secondsBetween(lastUpdated,
                currentDate).isGreaterThan(Seconds.seconds(EXPIRATION_THRESHOLD));
    }
}
