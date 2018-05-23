/*
 * Copyright (c) 2017, University of Oslo
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
package org.hisp.dhis.android.core.organisationunit;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.common.GenericHandler;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.data.api.Filter;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.resource.ResourceModel;
import org.hisp.dhis.android.core.user.User;

import java.io.IOException;
import java.util.Set;

import retrofit2.Response;

import static org.hisp.dhis.android.core.organisationunit.OrganisationUnitTree.findRoots;

public class OrganisationUnitCall extends SyncCall<Response<Payload<OrganisationUnit>>> {

    private final User user;
    private final OrganisationUnitService organisationUnitService;
    private final GenericHandler<OrganisationUnit, OrganisationUnitModel> organisationUnitHandler;
    private final GenericCallData genericCallData;

    OrganisationUnitCall(@NonNull User user,
                         @NonNull OrganisationUnitService organisationUnitService,
                         @NonNull GenericCallData genericCallData,
                         @NonNull GenericHandler<OrganisationUnit, OrganisationUnitModel>
                                 organisationUnitHandler) {
        this.user = user;
        this.organisationUnitService = organisationUnitService;
        this.genericCallData = genericCallData;
        this.organisationUnitHandler = organisationUnitHandler;
    }

    @Override
    public Response<Payload<OrganisationUnit>> call() throws Exception {
        super.setExecuted();
        Response<Payload<OrganisationUnit>> response = null;
        Response<Payload<OrganisationUnit>> totalResponse = null;

        Transaction transaction = genericCallData.databaseAdapter().beginNewTransaction();
        OrganisationUnitModelBuilder modelBuilder = new OrganisationUnitModelBuilder();

        try {
            Set<String> rootOrgUnitUids = findRoots(user.organisationUnits());
            Filter<OrganisationUnit, String> lastUpdatedFilter = OrganisationUnit.lastUpdated.gt(
                    genericCallData.resourceHandler().getLastUpdated(ResourceModel.Type.ORGANISATION_UNIT)
            );
            // Call OrganisationUnitService for each tree root & try to handleTrackedEntity sub-tree:
            for (String uid : rootOrgUnitUids) {
                response = getOrganisationUnit(uid, lastUpdatedFilter);
                if (response.isSuccessful()) {
                    if (totalResponse == null) {
                        totalResponse = response;
                    } else {
                        totalResponse.body().items().addAll(response.body().items());
                    }
                    organisationUnitHandler.handleMany(
                            response.body().items(),
                            modelBuilder);
                } else {
                    totalResponse = response;
                    break; //stop early unsuccessful:
                }
            }
            if (response != null && response.isSuccessful()) {
                genericCallData.resourceHandler().handleResource(ResourceModel.Type.ORGANISATION_UNIT,
                        genericCallData.serverDate());
                transaction.setSuccessful();
            }
        } finally {
            transaction.end();
        }
        return totalResponse;
    }

    private Response<Payload<OrganisationUnit>> getOrganisationUnit(
            @NonNull String uid,
            @Nullable Filter<OrganisationUnit, String> lastUpdatedFilter) throws IOException {

        return organisationUnitService.getOrganisationUnitWithDescendants(uid, OrganisationUnit.allFields,
                lastUpdatedFilter, true, false).execute();
    }

    public interface Factory {
        Call<Response<Payload<OrganisationUnit>>> create(GenericCallData data,
                                                         User user,
                                                         Set<String> programUids);
    }

    public static final OrganisationUnitCall.Factory FACTORY = new OrganisationUnitCall.Factory() {
        @Override
        public Call<Response<Payload<OrganisationUnit>>> create(GenericCallData data,
                                                                User user,
                                                                Set<String> programUids) {
            GenericHandler<OrganisationUnit, OrganisationUnitModel> handler =
                    OrganisationUnitHandler.create(data.databaseAdapter(), programUids,
                            OrganisationUnitModel.Scope.SCOPE_DATA_CAPTURE, user);
            return new OrganisationUnitCall(user, data.retrofit().create(OrganisationUnitService.class),
                    data, handler);
        }
    };
}