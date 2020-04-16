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

package org.hisp.dhis.android.core.organisationunit.internal;

import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutor;
import org.hisp.dhis.android.core.arch.api.payload.internal.Payload;
import org.hisp.dhis.android.core.arch.call.executors.internal.D2CallExecutor;
import org.hisp.dhis.android.core.arch.call.fetchers.internal.CallFetcher;
import org.hisp.dhis.android.core.arch.call.fetchers.internal.UidsNoResourceCallFetcher;
import org.hisp.dhis.android.core.arch.call.internal.EndpointCall;
import org.hisp.dhis.android.core.arch.call.processors.internal.CallProcessor;
import org.hisp.dhis.android.core.arch.call.queries.internal.UidsQuery;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitCollectionRepository;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserObjectRepository;

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
    private final OrganisationUnitHandler handler;
    private final OrganisationUnitDisplayPathTransformer pathTransformer;
    private final UserObjectRepository userRepository;
    private final OrganisationUnitCollectionRepository organisationUnitRepository;

    @Inject
    SearchOrganisationUnitOnDemandCallFactory(OrganisationUnitService service,
                                              APICallExecutor apiCallExecutor,
                                              D2CallExecutor d2CallExecutor,
                                              OrganisationUnitHandler handler,
                                              OrganisationUnitDisplayPathTransformer pathTransformer,
                                              UserObjectRepository userRepository,
                                              OrganisationUnitCollectionRepository organisationUnitRepository) {
        this.service = service;
        this.apiCallExecutor = apiCallExecutor;
        this.d2CallExecutor = d2CallExecutor;
        this.handler = handler;
        this.pathTransformer = pathTransformer;
        this.userRepository = userRepository;
        this.organisationUnitRepository = organisationUnitRepository;
    }

    public Callable<List<OrganisationUnit>> create(Set<String> uids) {
        return new EndpointCall<>(fetcher(uids), processor());
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

    private CallProcessor<OrganisationUnit> processor() {
        return objectList -> {
            if (objectList != null && !objectList.isEmpty()) {
                d2CallExecutor.executeD2CallTransactionally(() -> {
                    OrganisationUnit.Scope scope = OrganisationUnit.Scope.SCOPE_TEI_SEARCH;
                    List<OrganisationUnit> orgUnits = organisationUnitRepository
                            .byOrganisationUnitScope(scope)
                            .blockingGet();

                    User user = userRepository.blockingGet().toBuilder()
                            .teiSearchOrganisationUnits(orgUnits)
                            .build();

                    handler.setData(user, scope);
                    handler.handleMany(objectList, pathTransformer);
                    return null;
                });
            }
        };
    }
}