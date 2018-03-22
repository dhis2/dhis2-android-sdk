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

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.common.GenericEndpointCallImpl;
import org.hisp.dhis.android.core.common.GenericHandler;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.common.UidsQuery;
import org.hisp.dhis.android.core.resource.ResourceModel;
import org.hisp.dhis.android.core.user.User;

import java.io.IOException;
import java.util.Set;

import retrofit2.Response;

import static org.hisp.dhis.android.core.organisationunit.OrganisationUnitTree.findRoots;

public class OrganisationUnitCall extends GenericEndpointCallImpl<OrganisationUnit, OrganisationUnitModel, UidsQuery> {

    private final OrganisationUnitService organisationUnitService;

    OrganisationUnitCall(@NonNull GenericCallData data,
                                 @NonNull OrganisationUnitService organisationUnitService,
                                 @NonNull GenericHandler<OrganisationUnit, OrganisationUnitModel>
                                         organisationUnitHandler,
                                 @NonNull UidsQuery uidsQuery) {
        super(data, organisationUnitHandler, ResourceModel.Type.ORGANISATION_UNIT, new OrganisationUnitModelBuilder(),
                uidsQuery);
        this.organisationUnitService = organisationUnitService;
    }

    @Override
    protected retrofit2.Call<Payload<OrganisationUnit>> getCall(UidsQuery query,
                                                                String lastUpdated) throws IOException {
        return organisationUnitService.getOrganisationUnits(
                OrganisationUnit.allFields,
                OrganisationUnit.lastUpdated.gt(lastUpdated),
                OrganisationUnit.uid.in(query.uids()),
                true,
                false);
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
            Set<String> uids = findRoots(user.organisationUnits());
            return new OrganisationUnitCall(data, data.retrofit().create(OrganisationUnitService.class),
                    handler, UidsQuery.create(uids, null));
        }
    };
}