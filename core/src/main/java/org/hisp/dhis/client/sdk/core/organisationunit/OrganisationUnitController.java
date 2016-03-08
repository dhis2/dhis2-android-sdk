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
import org.hisp.dhis.client.sdk.core.common.controllers.AbsController;
import org.hisp.dhis.client.sdk.core.common.network.ApiException;
import org.hisp.dhis.client.sdk.core.common.persistence.ITransactionManager;
import org.hisp.dhis.client.sdk.core.common.preferences.ILastUpdatedPreferences;
import org.hisp.dhis.client.sdk.core.common.preferences.ResourceType;
import org.hisp.dhis.client.sdk.core.systeminfo.ISystemInfoApiClient;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Set;

public final class OrganisationUnitController extends AbsController<OrganisationUnit> implements
        IOrganisationUnitController {
    private final IOrganisationUnitStore organisationUnitStore;
    private final ISystemInfoApiClient systemInfoApiClient;
    private final IOrganisationUnitApiClient organisationUnitApiClient;
    private final ILastUpdatedPreferences lastUpdatedPreferences;
    private final ITransactionManager transactionManager;

    public OrganisationUnitController(IOrganisationUnitStore organisationUnitStore,
                                      ISystemInfoApiClient systemInfoApiClient,
                                      IOrganisationUnitApiClient organisationUnitApiClient,
                                      ILastUpdatedPreferences lastUpdatedPreferences,
                                      ITransactionManager transactionManager) {
        this.organisationUnitStore = organisationUnitStore;
        this.systemInfoApiClient = systemInfoApiClient;
        this.organisationUnitApiClient = organisationUnitApiClient;
        this.lastUpdatedPreferences = lastUpdatedPreferences;
        this.transactionManager = transactionManager;
    }

    private void getOrganisationUnitsFromServer() {
        DateTime serverTime = systemInfoApiClient.getSystemInfo().getServerDate();
        DateTime lastUpdated = lastUpdatedPreferences.get(ResourceType.ORGANISATION_UNITS);
        List<OrganisationUnit> updatedOrganisationUnits = organisationUnitApiClient
                .getOrganisationUnits(Fields.ALL, lastUpdated);
        List<OrganisationUnit> existingOrganisationUnits = organisationUnitApiClient
                .getOrganisationUnits(Fields.BASIC, null);
        List<OrganisationUnit> persistedOrganisationUnits = organisationUnitStore.queryAll();
        transactionManager.transact(getMergeOperations(existingOrganisationUnits,
                updatedOrganisationUnits, persistedOrganisationUnits, organisationUnitStore));
        lastUpdatedPreferences.save(ResourceType.ORGANISATION_UNITS, serverTime);
    }

    @Override
    public void sync() throws ApiException {
        getOrganisationUnitsFromServer();
    }

    @Override
    public void sync(Set<String> uids) throws ApiException {
        DateTime serverTime = systemInfoApiClient.getSystemInfo().getServerDate();

        List<OrganisationUnit> updatedOrganisationUnits = organisationUnitApiClient
                .getOrganisationUnits(Fields.ALL, null, uids.toArray(new String[uids.size()]));
        List<OrganisationUnit> existingOrganisationUnits = organisationUnitApiClient
                .getOrganisationUnits(Fields.BASIC, null, uids.toArray(new String[uids.size()]));
        List<OrganisationUnit> persistedOrganisationUnits = organisationUnitStore.queryAll();

        transactionManager.transact(getMergeOperations(existingOrganisationUnits,
                updatedOrganisationUnits, persistedOrganisationUnits, organisationUnitStore));
        lastUpdatedPreferences.save(ResourceType.ORGANISATION_UNITS, serverTime);
    }
}