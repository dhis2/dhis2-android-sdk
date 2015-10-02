/*
 * Copyright (c) 2015, University of Oslo
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

package org.hisp.dhis.android.sdk.core.controllers;

import org.hisp.dhis.android.sdk.core.network.APIException;
import org.hisp.dhis.android.sdk.core.network.IDhisApi;
import org.hisp.dhis.android.sdk.core.preferences.DateTimeManager;
import org.hisp.dhis.android.sdk.core.models.ResourceType;
import org.hisp.dhis.android.sdk.persistence.utils.DbUtils;
import org.hisp.dhis.android.sdk.models.common.meta.DbOperation;
import org.hisp.dhis.android.sdk.models.common.meta.IDbOperation;
import org.hisp.dhis.android.sdk.models.organisationunit.IOrganisationUnitStore;
import org.hisp.dhis.android.sdk.models.organisationunit.OrganisationUnit;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hisp.dhis.android.sdk.core.utils.NetworkUtils.unwrapResponse;
import static org.hisp.dhis.android.sdk.models.common.base.BaseIdentifiableObject.toMap;

public final class OrganisationUnitController implements IOrganisationUnitController {

    private final static String ORGANISATIONUNITS = "organisationUnits";
    private final static int QUERY_SIZE = 100;
    private final IDhisApi mDhisApi;

    private final IOrganisationUnitStore mOrganisationUnitStore;

    public OrganisationUnitController(IDhisApi dhisApi, IOrganisationUnitStore mOrganisationUnitStore) {
        this.mDhisApi = dhisApi;
        this.mOrganisationUnitStore = mOrganisationUnitStore;
    }

    private void getOrganisationUnitsFromServer(List<OrganisationUnit> organisationUnits) {
        ResourceType resource = ResourceType.ORGANISATIONUNITS;
        DateTime serverTime = mDhisApi.getSystemInfo().getServerDate();
        DateTime lastUpdated = DateTimeManager.getInstance()
                .getLastUpdated(resource);

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("fields", "id,level,created,name,lastUpdated,shortName,openingDate,displayName");
        List<OrganisationUnit> updatedOrganisationUnits = new ArrayList<>();
        boolean done = false;
        int iteration = 1;
        int first = 0;
        int last;
        while(!done) {
            if (organisationUnits.size() < QUERY_SIZE * iteration) {
                last = organisationUnits.size();
                done = true;
            } else {
                last = QUERY_SIZE * iteration;
            }
            queryParams.remove("filter");
            queryParams.put("filter", getOrganisationUnitFilterIdString(organisationUnits.subList(first, last)));
            List<OrganisationUnit> queriedOrganisationUnits = unwrapResponse(mDhisApi.getOrganisationUnits(queryParams), ORGANISATIONUNITS);
            updatedOrganisationUnits.addAll(queriedOrganisationUnits);
            first = last;
            iteration++;
        }
        List<OrganisationUnit> persistedOrganisationUnits = mOrganisationUnitStore.queryAll();
        Map<String, OrganisationUnit> persistedOrganisationUnitsMap = toMap(persistedOrganisationUnits);
        Map<String, OrganisationUnit> updatedOrganisationUnitsMap = toMap(updatedOrganisationUnits);
        for(OrganisationUnit persistedOrganisationUnit : persistedOrganisationUnits) {
            OrganisationUnit updatedOrganisationUnit = updatedOrganisationUnitsMap.get(persistedOrganisationUnit.getUId());
            if(updatedOrganisationUnit != null) {
                updatedOrganisationUnit.setId(persistedOrganisationUnit.getId());
            }
        }
        List<IDbOperation> operations = new ArrayList<>();
        for(OrganisationUnit updatedOrganisationUnit : updatedOrganisationUnits) {
            if(persistedOrganisationUnitsMap.containsValue(updatedOrganisationUnit.getUId())) {
                operations.add(DbOperation.with(mOrganisationUnitStore).update(updatedOrganisationUnit));
            } else {
                operations.add(DbOperation.with(mOrganisationUnitStore).insert(updatedOrganisationUnit));
            }
        }
        DbUtils.applyBatch(operations);

        DateTimeManager.getInstance()
                .setLastUpdated(ResourceType.ORGANISATIONUNITS, serverTime);
    }

    private void getOrganisationUnitsFromServer() {
        getOrganisationUnitsFromServer(mOrganisationUnitStore.queryAll());
    }

    private String getOrganisationUnitFilterIdString(List<OrganisationUnit> organisationUnits) {
        String filterString = "id:in:[";
        for(int i = 0; i<organisationUnits.size(); i++) {
            filterString += organisationUnits.get(i).getUId();
            if(i < organisationUnits.size() -1 ) {
                filterString += ',';
            }
        }
        filterString+=']';
        return filterString;
    }

    @Override
    public void sync() throws APIException {
        getOrganisationUnitsFromServer();
    }

    public void sync(List<OrganisationUnit> organisationUnits) throws APIException {
        getOrganisationUnitsFromServer(organisationUnits);
    }
}