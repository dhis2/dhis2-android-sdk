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

import org.hisp.dhis.android.core.arch.api.executors.internal.APIDownloader;
import org.hisp.dhis.android.core.arch.call.factories.internal.UidsCall;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitCollectionRepository;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserObjectRepository;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import dagger.Reusable;
import io.reactivex.Single;

@Reusable
final class SearchOrganisationUnitOnDemandCall implements UidsCall<OrganisationUnit> {

    private static final int MAX_UID_LIST_SIZE = 120;

    private final OrganisationUnitService service;
    private final OrganisationUnitHandler handler;
    private final APIDownloader apiDownloader;
    private final OrganisationUnitDisplayPathTransformer pathTransformer;
    private final UserObjectRepository userRepository;
    private final OrganisationUnitCollectionRepository organisationUnitRepository;

    @Inject
    SearchOrganisationUnitOnDemandCall(OrganisationUnitService service,
                                       OrganisationUnitHandler handler,
                                       APIDownloader apiDownloader,
                                       OrganisationUnitDisplayPathTransformer pathTransformer,
                                       UserObjectRepository userRepository,
                                       OrganisationUnitCollectionRepository organisationUnitRepository) {
        this.service = service;
        this.handler = handler;
        this.apiDownloader = apiDownloader;
        this.pathTransformer = pathTransformer;
        this.userRepository = userRepository;
        this.organisationUnitRepository = organisationUnitRepository;
    }

    @Override
    public Single<List<OrganisationUnit>> download(Set<String> uids) {
        return apiDownloader.downloadPartitionedWithCustomHandling(uids, MAX_UID_LIST_SIZE, items -> {
            OrganisationUnit.Scope scope = OrganisationUnit.Scope.SCOPE_TEI_SEARCH;
            List<OrganisationUnit> orgUnits = organisationUnitRepository
                    .byOrganisationUnitScope(scope)
                    .byRootOrganisationUnit(true)
                    .blockingGet();

            User user = userRepository.blockingGet().toBuilder()
                    .teiSearchOrganisationUnits(orgUnits)
                    .build();

            handler.setData(user, scope);
            handler.handleMany(items, pathTransformer);
        }, partitionUids ->
                service.getSearchOrganisationUnits(OrganisationUnitFields.allFields,
                        OrganisationUnitFields.uid.in(partitionUids), Boolean.FALSE));
    }
}