/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
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

import org.hisp.dhis.android.core.arch.api.executors.APICallExecutor;
import org.hisp.dhis.android.core.calls.EndpointCall;
import org.hisp.dhis.android.core.calls.fetchers.CallFetcher;
import org.hisp.dhis.android.core.calls.fetchers.UidsNoResourceCallFetcher;
import org.hisp.dhis.android.core.calls.processors.CallProcessor;
import org.hisp.dhis.android.core.common.D2CallExecutor;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.common.UidsQuery;
import org.hisp.dhis.android.core.user.User;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
final class SearchOrganisationUnitOnDemandCallFactory {

    private static final int MAX_UID_LIST_SIZE = 120;

    private final OrganisationUnitService service;
    private final APICallExecutor apiCallExecutor;
    private final D2CallExecutor d2CallExecutor;
    private final SearchOrganisationUnitHandler handler;

    @Inject
    SearchOrganisationUnitOnDemandCallFactory(OrganisationUnitService service,
                                              APICallExecutor apiCallExecutor,
                                              D2CallExecutor d2CallExecutor,
                                              SearchOrganisationUnitHandler handler) {
        this.service = service;
        this.apiCallExecutor = apiCallExecutor;
        this.d2CallExecutor = d2CallExecutor;
        this.handler = handler;
    }

    public Callable<List<OrganisationUnit>> create(Set<String> uids, User user) {
        return new EndpointCall<>(fetcher(uids), processor(user));
    }

    private CallFetcher<OrganisationUnit> fetcher(Set<String> uids) {

        return new UidsNoResourceCallFetcher<OrganisationUnit>(uids, MAX_UID_LIST_SIZE, apiCallExecutor) {

            @Override
            protected retrofit2.Call<Payload<OrganisationUnit>> getCall(UidsQuery query) {
                return service.getSearchOrganisationUnits(OrganisationUnitFields.allFields,
                        OrganisationUnitFields.uid.in(query.uids()), Boolean.FALSE);
            }
        };
    }

    private CallProcessor<OrganisationUnit> processor(final User user) {
        return objectList -> {
            if (objectList != null && !objectList.isEmpty()) {
                d2CallExecutor.executeD2CallTransactionally(() -> {
                    handler.setUser(user);
                    handler.handleMany(objectList, new OrganisationUnitDisplayPathTransformer());
                    return null;
                });
            }
        };
    }
}