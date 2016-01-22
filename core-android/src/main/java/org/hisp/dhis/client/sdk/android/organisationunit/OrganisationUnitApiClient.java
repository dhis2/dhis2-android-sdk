/*
 *  Copyright (c) 2016, University of Oslo
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this
 *  * list of conditions and the following disclaimer.
 *  *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  * this list of conditions and the following disclaimer in the documentation
 *  * and/or other materials provided with the distribution.
 *  * Neither the name of the HISP project nor the names of its contributors may
 *  * be used to endorse or promote products derived from this software without
 *  * specific prior written permission.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.hisp.dhis.client.sdk.android.organisationunit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

import org.hisp.dhis.client.sdk.android.api.utils.ObjectMapperProvider;
import org.hisp.dhis.client.sdk.core.common.Fields;
import org.hisp.dhis.client.sdk.core.organisationunit.IOrganisationUnitApiClient;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;
import org.hisp.dhis.client.sdk.models.utils.IModelUtils;
import org.hisp.dhis.client.sdk.models.utils.ModelUtils;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hisp.dhis.client.sdk.android.api.utils.NetworkUtils.call;

public class OrganisationUnitApiClient implements IOrganisationUnitApiClient {

    private final OrganisationUnitApiClientRetrofit mOrganisationUnitApiClientRetrofit;
    private final IModelUtils mModelUtils;

    public OrganisationUnitApiClient(OrganisationUnitApiClientRetrofit mOrganisationUnitApiClientRetrofit, IModelUtils mModelUtils) {
        this.mOrganisationUnitApiClientRetrofit = mOrganisationUnitApiClientRetrofit;
        this.mModelUtils = mModelUtils;
    }

    @Override
    public List<OrganisationUnit> getOrganisationUnits(Fields fields, DateTime dateTime) {
        Map<String, String> queryMap = new HashMap<>();
        switch (fields) {
            case ALL:
                queryMap.put("fields", getFieldsFilter());
                break;
            case BASIC:
                queryMap.put("fields", "id");
                break;
        }
        queryMap.put("paging", "false");
        if (dateTime != null) {
            queryMap.put("lastUpdated", dateTime.toString());
        }
        JsonNode organisationUnitsNode = call(mOrganisationUnitApiClientRetrofit.getOrganisationUnits(queryMap));
        return unwrap(organisationUnitsNode, mModelUtils);
    }

    @Override
    public List<OrganisationUnit> getOrganisationUnits(Fields fields, Set<String> set, DateTime dateTime) {
        Map<String, String> queryMap = new HashMap<>();
        switch (fields) {
            case ALL:
                queryMap.put("fields", getFieldsFilter());
                break;
            case BASIC:
                queryMap.put("fields", "id");
                break;
        }
        queryMap.put("filter", getUidFilter(set));
        queryMap.put("paging", "false");
        if (dateTime != null) {
            queryMap.put("lastUpdated", dateTime.toString());
        }
        JsonNode organisationUnitsNode = call(mOrganisationUnitApiClientRetrofit.getOrganisationUnits(queryMap));
        return unwrap(organisationUnitsNode, mModelUtils);
    }

    @Override
    public OrganisationUnit getOrganisationUnit(String uid, Fields fields, DateTime dateTime) {
        Map<String, String> queryMap = new HashMap<>();
        switch (fields) {
            case ALL:
                queryMap.put("fields", getFieldsFilter());
                break;
            case BASIC:
                queryMap.put("fields", "id");
                break;
        }
        queryMap.put("paging", "false");
        if (dateTime != null) {
            queryMap.put("lastUpdated", dateTime.toString());
        }
        OrganisationUnit updatedOrganisationUnit = call(mOrganisationUnitApiClientRetrofit.getOrganisationUnit(uid, queryMap));
        return updatedOrganisationUnit;
    }

    private static String getFieldsFilter() {
        return "code,lastUpdated,id,level,created,name,shortName,displayName,displayShortName," +
                "externalAccess,featureType,openingDate,dimensionItem,parent[id]";
    }

    private static List<OrganisationUnit> unwrap(JsonNode meNode, IModelUtils modelUtils) {
        List<OrganisationUnit> organisationUnits;
        if(meNode.has("organisationUnits")) {
            TypeReference<List<OrganisationUnit>> typeRef = new TypeReference<List<OrganisationUnit>>() {};
            try {
                organisationUnits = ObjectMapperProvider.getInstance().
                        readValue(meNode.get("organisationUnits").traverse(), typeRef);
            } catch (IOException e) {
                e.printStackTrace();
                organisationUnits = new ArrayList<>();
            }
            Collections.sort(organisationUnits, new OrganisationUnitCompareByLevelComparator());
            Map<String, OrganisationUnit> organisationUnitMap = modelUtils.toMap(organisationUnits);
            for(OrganisationUnit organisationUnit : organisationUnits) {
                if(organisationUnit.getParent() != null) {
                    organisationUnit.setParent(organisationUnitMap.get(organisationUnit.getParent().getUId()));
                }
            }
        } else {
            organisationUnits = new ArrayList<>();
        }
        return organisationUnits;
    }

    private static String getUidFilter(Set<String> uids) {
        String filter = "id:in:[";
        Iterator<String> uidIterator = uids.iterator();
        while(uidIterator.hasNext()) {
            String uid = uidIterator.next();
            filter += uid;
            if(uidIterator.hasNext()) {
                filter += ',';
            }
        }
        filter += ']';
        return filter;
    }
}
