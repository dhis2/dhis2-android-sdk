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

package org.hisp.dhis.client.sdk.core.organisationunit;

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
import org.hisp.dhis.client.sdk.core.systeminfo.SystemInfoController;
import org.hisp.dhis.client.sdk.core.user.UserApiClient;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class OrganisationUnitControllerImpl extends AbsSyncStrategyController<OrganisationUnit>
        implements OrganisationUnitController {

    /* Controllers */
    private final SystemInfoController systemInfoController;

    /* Api clients */
    private final OrganisationUnitApiClient organisationUnitApiClient;
    private final UserApiClient userApiClient;

    /* Utilities */
    private final TransactionManager transactionManager;

    public OrganisationUnitControllerImpl(SystemInfoController systemInfoController,
                                          OrganisationUnitApiClient organisationUnitApiClient,
                                          UserApiClient userApiClient,
                                          OrganisationUnitStore organisationUnitStore,
                                          LastUpdatedPreferences lastUpdatedPreferences,
                                          TransactionManager transactionManager) {
        super(ResourceType.ORGANISATION_UNITS, organisationUnitStore, lastUpdatedPreferences);

        this.systemInfoController = systemInfoController;
        this.organisationUnitApiClient = organisationUnitApiClient;
        this.userApiClient = userApiClient;
        this.transactionManager = transactionManager;
    }

    @Override
    protected void synchronize(SyncStrategy strategy, Set<String> uids) {
        DateTime serverTime = systemInfoController.getSystemInfo().getServerDate();
        DateTime lastUpdated = lastUpdatedPreferences.get(
                ResourceType.ORGANISATION_UNITS, DateType.SERVER);

        List<OrganisationUnit> persistedOrganisationUnits = identifiableObjectStore.queryAll();

        // we have to download all ids from server in order to
        // find out what was removed on the server side
        List<OrganisationUnit> allExistingOrganisationUnits =
                organisationUnitApiClient.getOrganisationUnits(Fields.BASIC, null, null);

        Set<String> uidSet = null;
        if (uids != null) {
            // here we want to get list of ids of programs which are
            // stored locally and list of organisation units which we want to download
            uidSet = ModelUtils.toUidSet(persistedOrganisationUnits);
            uidSet.addAll(uids);
        }

        // Retrieving only updated organisation units
        List<OrganisationUnit> updatedOrganisationUnits = organisationUnitApiClient
                .getOrganisationUnits(Fields.ALL, lastUpdated, uidSet);

        // we need to mark assigned organisation units as "assigned" before storing them
        Map<String, OrganisationUnit> assignedOrganisationUnits = ModelUtils
                .toMap(userApiClient.getUserAccount().getOrganisationUnits());

        for (OrganisationUnit updatedOrganisationUnit : updatedOrganisationUnits) {
            OrganisationUnit assignedOrganisationUnit = assignedOrganisationUnits
                    .get(updatedOrganisationUnit.getUId());
            updatedOrganisationUnit.setIsAssignedToUser(assignedOrganisationUnit != null);
        }

        // we will have to perform something similar to what happens in AbsController
        List<DbOperation> dbOperations = DbUtils.createOperations(allExistingOrganisationUnits,
                updatedOrganisationUnits, persistedOrganisationUnits, identifiableObjectStore);
        transactionManager.transact(dbOperations);

        lastUpdatedPreferences.save(ResourceType.ORGANISATION_UNITS, DateType.SERVER, serverTime);
    }
}
