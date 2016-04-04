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

package org.hisp.dhis.client.sdk.core.common.controllers;

import org.hisp.dhis.client.sdk.core.common.network.ApiException;
import org.hisp.dhis.client.sdk.core.common.persistence.IIdentifiableObjectStore;
import org.hisp.dhis.client.sdk.core.common.preferences.DateType;
import org.hisp.dhis.client.sdk.core.common.preferences.ILastUpdatedPreferences;
import org.hisp.dhis.client.sdk.core.common.preferences.ResourceType;
import org.hisp.dhis.client.sdk.models.common.base.IdentifiableObject;
import org.joda.time.DateTime;
import org.joda.time.Seconds;

import java.util.Set;

public abstract class AbsSyncStrategyController<T extends IdentifiableObject>
        implements IIdentifiableController<T> {
    private static final int EXPIRATION_THRESHOLD = 64;

    protected final ResourceType resourceType;
    protected final IIdentifiableObjectStore<T> identifiableObjectStore;
    protected final ILastUpdatedPreferences lastUpdatedPreferences;

    protected AbsSyncStrategyController(ResourceType resourceType,
                                        IIdentifiableObjectStore<T> identifiableObjectStore,
                                        ILastUpdatedPreferences lastUpdatedPreferences) {
        this.resourceType = resourceType;
        this.identifiableObjectStore = identifiableObjectStore;
        this.lastUpdatedPreferences = lastUpdatedPreferences;
    }

    @Override
    public final void pull(SyncStrategy strategy) throws ApiException {
        pull(strategy, null);
    }

    @Override
    public final void pull(SyncStrategy strategy, Set<String> uids) throws ApiException {
        DateTime currentDate = DateTime.now();

        /* if we don't have objects with given uids in place, we have
        to force a pull even if strategy is set to be DEFAULT */
        if (SyncStrategy.FORCE_UPDATE.equals(strategy) ||
                !identifiableObjectStore.areStored(uids)) {
            synchronize(strategy, uids);

            lastUpdatedPreferences.save(resourceType, DateType.LOCAL, currentDate);
            return;
        }

        if (SyncStrategy.DEFAULT.equals(strategy) && isResourceOutdated(currentDate)) {
            synchronize(SyncStrategy.DEFAULT, uids);

            lastUpdatedPreferences.save(resourceType, DateType.LOCAL, currentDate);
        }
    }

    private boolean isResourceOutdated(DateTime currentDate) {
        DateTime lastUpdated = lastUpdatedPreferences.get(resourceType, DateType.LOCAL);
        return lastUpdated == null || Seconds.secondsBetween(lastUpdated,
                currentDate).isGreaterThan(Seconds.seconds(EXPIRATION_THRESHOLD));
    }

    protected abstract void synchronize(SyncStrategy strategy, Set<String> uids);
}
