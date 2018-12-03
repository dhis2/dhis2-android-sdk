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

import org.hisp.dhis.android.core.arch.api.executors.APICallExecutor;
import org.hisp.dhis.android.core.arch.api.executors.APICallExecutorImpl;
import org.hisp.dhis.android.core.arch.handlers.SyncHandlerWithTransformer;
import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.D2CallExecutor;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.resource.ResourceModel;
import org.hisp.dhis.android.core.user.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import static org.hisp.dhis.android.core.organisationunit.OrganisationUnitTree.findRoots;

public class OrganisationUnitCall extends SyncCall<List<OrganisationUnit>> {

    private final User user;
    private final OrganisationUnitService organisationUnitService;
    private final SyncHandlerWithTransformer<OrganisationUnit> organisationUnitHandler;
    private final GenericCallData data;
    private final APICallExecutor apiCallExecutor;

    OrganisationUnitCall(@NonNull User user,
                         @NonNull OrganisationUnitService organisationUnitService,
                         @NonNull GenericCallData data,
                         @NonNull SyncHandlerWithTransformer<OrganisationUnit> organisationUnitHandler,
                         APICallExecutor apiCallExecutor) {
        this.user = user;
        this.organisationUnitService = organisationUnitService;
        this.data = data;
        this.organisationUnitHandler = organisationUnitHandler;
        this.apiCallExecutor = apiCallExecutor;
    }

    @Override
    public List<OrganisationUnit> call() throws Exception {
        setExecuted();

        final Set<OrganisationUnit> organisationUnits = new HashSet<>();

        return new D2CallExecutor().executeD2CallTransactionally(data.databaseAdapter(),
                new Callable<List<OrganisationUnit>>() {
            @Override
            public List<OrganisationUnit> call() throws Exception {
                Set<String> rootOrgUnitUids = findRoots(user.organisationUnits());
                for (String uid : rootOrgUnitUids) {
                    organisationUnits.addAll(apiCallExecutor.executePayloadCall(
                            getOrganisationUnitAndDescendants(uid)));
                }

                organisationUnitHandler.handleMany(organisationUnits, new OrganisationUnitDisplayPathTransformer());

                data.resourceHandler().handleResource(ResourceModel.Type.ORGANISATION_UNIT);

                return new ArrayList<>(organisationUnits);
            }
        });
    }

    private retrofit2.Call<Payload<OrganisationUnit>> getOrganisationUnitAndDescendants(@NonNull String uid) {
        return organisationUnitService.getOrganisationUnitWithDescendants(
                uid, OrganisationUnitFields.allFields, true, false);
    }

    public interface Factory {
        Call<List<OrganisationUnit>> create(GenericCallData data,
                                            User user,
                                            Set<String> programUids,
                                            Set<String> dataSetUids);
    }

    public static final OrganisationUnitCall.Factory FACTORY = new OrganisationUnitCall.Factory() {
        @Override
        public Call<List<OrganisationUnit>> create(GenericCallData data,
                                                                User user,
                                                                Set<String> programUids,
                                                                Set<String> dataSetUids) {
            SyncHandlerWithTransformer<OrganisationUnit> handler =
                    OrganisationUnitHandler.create(data.databaseAdapter(), programUids, dataSetUids,
                            OrganisationUnit.Scope.SCOPE_DATA_CAPTURE, user);
            return new OrganisationUnitCall(user, data.retrofit().create(OrganisationUnitService.class), data, handler,
                    APICallExecutorImpl.create(data.databaseAdapter()));
        }
    };
}