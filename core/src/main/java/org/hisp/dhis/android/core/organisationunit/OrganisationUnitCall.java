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
import org.hisp.dhis.android.core.common.APICallExecutor;
import org.hisp.dhis.android.core.common.D2CallExecutor;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.common.GenericHandler;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.data.api.Filter;
import org.hisp.dhis.android.core.resource.ResourceModel;
import org.hisp.dhis.android.core.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import static org.hisp.dhis.android.core.organisationunit.OrganisationUnitTree.findRoots;

public class OrganisationUnitCall extends SyncCall<List<OrganisationUnit>> {

    private final User user;
    private final OrganisationUnitService organisationUnitService;
    private final GenericHandler<OrganisationUnit, OrganisationUnitModel> organisationUnitHandler;
    private final GenericCallData data;

    OrganisationUnitCall(@NonNull User user,
                         @NonNull OrganisationUnitService organisationUnitService,
                         @NonNull GenericCallData data,
                         @NonNull GenericHandler<OrganisationUnit, OrganisationUnitModel>
                                 organisationUnitHandler) {
        this.user = user;
        this.organisationUnitService = organisationUnitService;
        this.data = data;
        this.organisationUnitHandler = organisationUnitHandler;
    }

    @Override
    public List<OrganisationUnit> call() throws Exception {
        super.setExecuted();

        final List<OrganisationUnit> organisationUnits = new ArrayList<>();
        final APICallExecutor apiExecutor = new APICallExecutor();

        return new D2CallExecutor().executeD2CallTransactionally(data.databaseAdapter(),
                new Callable<List<OrganisationUnit>>() {
            @Override
            public List<OrganisationUnit> call() throws Exception {
                OrganisationUnitModelBuilder modelBuilder = new OrganisationUnitModelBuilder();
                Set<String> rootOrgUnitUids = findRoots(user.organisationUnits());
                Filter<OrganisationUnit, String> lastUpdatedFilter = OrganisationUnit.lastUpdated.gt(
                        data.resourceHandler().getLastUpdated(ResourceModel.Type.ORGANISATION_UNIT)
                );

                for (String uid : rootOrgUnitUids) {
                    List<OrganisationUnit> orgUnitWithDescendants = apiExecutor.executePayloadCall(
                            getOrganisationUnitAndDescendants(uid, lastUpdatedFilter));
                    organisationUnits.addAll(orgUnitWithDescendants);
                    organisationUnitHandler.handleMany(orgUnitWithDescendants, modelBuilder);
                }

                data.resourceHandler().handleResource(ResourceModel.Type.ORGANISATION_UNIT,
                        data.serverDate());

                return organisationUnits;
            }
        });
    }

    private retrofit2.Call<Payload<OrganisationUnit>> getOrganisationUnitAndDescendants(
            @NonNull String uid,
            @Nullable Filter<OrganisationUnit, String> lastUpdatedFilter) {

        return organisationUnitService.getOrganisationUnitWithDescendants(uid, OrganisationUnit.allFields,
                lastUpdatedFilter, true, false);
    }

    public interface Factory {
        Call<List<OrganisationUnit>> create(GenericCallData data,
                                                         User user,
                                                         Set<String> programUids);
    }

    public static final OrganisationUnitCall.Factory FACTORY = new OrganisationUnitCall.Factory() {
        @Override
        public Call<List<OrganisationUnit>> create(GenericCallData data,
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