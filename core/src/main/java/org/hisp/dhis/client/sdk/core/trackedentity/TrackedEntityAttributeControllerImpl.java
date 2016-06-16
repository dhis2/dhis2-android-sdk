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

package org.hisp.dhis.client.sdk.core.trackedentity;

import org.hisp.dhis.client.sdk.core.common.Fields;
import org.hisp.dhis.client.sdk.core.common.controllers.AbsSyncStrategyController;
import org.hisp.dhis.client.sdk.core.common.controllers.SyncStrategy;
import org.hisp.dhis.client.sdk.core.common.persistence.DbOperation;
import org.hisp.dhis.client.sdk.core.common.persistence.DbUtils;
import org.hisp.dhis.client.sdk.core.common.persistence.TransactionManager;
import org.hisp.dhis.client.sdk.core.common.preferences.DateType;
import org.hisp.dhis.client.sdk.core.common.preferences.LastUpdatedPreferences;
import org.hisp.dhis.client.sdk.core.common.preferences.ResourceType;
import org.hisp.dhis.client.sdk.core.common.utils.ModelUtils;
import org.hisp.dhis.client.sdk.core.optionset.OptionSetController;
import org.hisp.dhis.client.sdk.core.systeminfo.SystemInfoController;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityAttribute;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class TrackedEntityAttributeControllerImpl
        extends AbsSyncStrategyController<TrackedEntityAttribute>
        implements TrackedEntityAttributeController {
    private final TrackedEntityAttributeApiClient trackedEntityAttributeApiClient;
    private final TransactionManager transactionManager;
    private final SystemInfoController systemInfoController;
    private final OptionSetController optionSetController;

    public TrackedEntityAttributeControllerImpl(SystemInfoController systemInfoController,
                                                OptionSetController optionSetController,
                                                LastUpdatedPreferences lastUpdatedPreferences,
                                                TrackedEntityAttributeStore attributeStore,
                                                TrackedEntityAttributeApiClient attributeApiClient,
                                                TransactionManager transactionManager) {
        super(ResourceType.TRACKED_ENTITY_ATTRIBUTES, attributeStore, lastUpdatedPreferences);
        this.trackedEntityAttributeApiClient = attributeApiClient;
        this.transactionManager = transactionManager;
        this.systemInfoController = systemInfoController;
        this.optionSetController = optionSetController;
    }

    @Override
    protected void synchronize(SyncStrategy strategy, Set<String> uids) {
        DateTime serverTime = systemInfoController.getSystemInfo().getServerDate();
        DateTime lastUpdated = lastUpdatedPreferences.get(
                ResourceType.TRACKED_ENTITY_ATTRIBUTES, DateType.SERVER);

        List<TrackedEntityAttribute> persistedTrackedEntityAttributes =
                identifiableObjectStore.queryAll();

        // we have to download all ids from server in order to
        // find out what was removed on the server side
        List<TrackedEntityAttribute> allExistingTrackedEntityAttributes = trackedEntityAttributeApiClient
                .getTrackedEntityAttributes(Fields.BASIC, null);

        List<TrackedEntityAttribute> updatedTrackedEntityAttributes = new ArrayList<>();
        if (uids == null) {
            updatedTrackedEntityAttributes.addAll(trackedEntityAttributeApiClient
                    .getTrackedEntityAttributes(Fields.ALL, lastUpdated, null));
        } else {
            // defensive copy
            Set<String> modelsToFetch = new HashSet<>(uids);
            Set<String> modelsToUpdate = ModelUtils.toUidSet(persistedTrackedEntityAttributes);

            modelsToFetch.removeAll(modelsToUpdate);

            if (!modelsToFetch.isEmpty()) {
                updatedTrackedEntityAttributes.addAll(trackedEntityAttributeApiClient
                        .getTrackedEntityAttributes(Fields.ALL, null, modelsToFetch));
            }

            if (!modelsToUpdate.isEmpty()) {
                updatedTrackedEntityAttributes.addAll(trackedEntityAttributeApiClient
                        .getTrackedEntityAttributes(Fields.ALL, lastUpdated, modelsToUpdate));
            }
        }

        // Retrieving foreign key uids from trackedEntityAttributes
        Set<String> optionSetUids = new HashSet<>();

        List<TrackedEntityAttribute> trackedEntityAttributes = ModelUtils.merge(
                allExistingTrackedEntityAttributes, updatedTrackedEntityAttributes,
                persistedTrackedEntityAttributes);
        for (TrackedEntityAttribute trackedEntityAttribute : trackedEntityAttributes) {
            if (trackedEntityAttribute.getOptionSet() != null) {
                optionSetUids.add(trackedEntityAttribute.getOptionSet().getUId());
            }
        }

        // checking if option sets is synced
        if (!optionSetUids.isEmpty()) {
            optionSetController.pull(strategy, optionSetUids);
        }

        // we will have to perform something similar to what happens in AbsController
        List<DbOperation> dbOperations = DbUtils.createOperations(
                allExistingTrackedEntityAttributes, updatedTrackedEntityAttributes,
                persistedTrackedEntityAttributes, identifiableObjectStore);
        transactionManager.transact(dbOperations);

        lastUpdatedPreferences.save(ResourceType.TRACKED_ENTITY_ATTRIBUTES,
                DateType.SERVER, serverTime);
    }
}