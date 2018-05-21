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

import org.hisp.dhis.android.core.common.APICallExecutor;
import org.hisp.dhis.android.core.common.D2CallException;
import org.hisp.dhis.android.core.common.GenericHandler;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.common.UidsQuery;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Retrofit;

public final class SearchOrganisationUnitCall extends SyncCall<List<OrganisationUnit>> {
    private final OrganisationUnitService service;
    private final GenericHandler<OrganisationUnit, OrganisationUnitModel> handler;
    private final UidsQuery query;

    private SearchOrganisationUnitCall(OrganisationUnitService service,
                                       GenericHandler<OrganisationUnit, OrganisationUnitModel> handler,
                                       UidsQuery query) {
        this.service = service;
        this.handler = handler;
        this.query = query;
    }

    @Override
    public List<OrganisationUnit> call() throws D2CallException {
        Call<Payload<OrganisationUnit>> call = service.getSearchOrganisationUnits(OrganisationUnit.allFields,
                OrganisationUnit.uid.in(query.uids()), Boolean.FALSE);
        List<OrganisationUnit> organisationUnits = new APICallExecutor().executePayloadCall(call);
        handler.handleMany(organisationUnits, new OrganisationUnitModelBuilder());
        return organisationUnits;
    }

    public interface Factory {
        SearchOrganisationUnitCall create(DatabaseAdapter databaseAdapter,
                                          Retrofit retrofit,
                                          Set<String> uids,
                                          String userId);
    }

    public static final Factory FACTORY = new Factory() {

        @Override
        public SearchOrganisationUnitCall create(
                DatabaseAdapter databaseAdapter,
                Retrofit retrofit,
                Set<String> uids,
                String userId) {

            return new SearchOrganisationUnitCall(
                    retrofit.create(OrganisationUnitService.class),
                    SearchOrganisationUnitHandler.create(databaseAdapter, userId),
                    UidsQuery.create(uids, null)
            );
        }
    };
}