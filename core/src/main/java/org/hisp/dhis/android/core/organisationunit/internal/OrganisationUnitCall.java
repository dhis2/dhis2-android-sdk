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

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.arch.api.payload.internal.Payload;
import org.hisp.dhis.android.core.arch.helpers.UidsHelper;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserInternalAccessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import dagger.Reusable;
import io.reactivex.Flowable;
import io.reactivex.Single;

import static org.hisp.dhis.android.core.organisationunit.OrganisationUnitTree.findRoots;
import static org.hisp.dhis.android.core.organisationunit.OrganisationUnitTree.findRootsOutsideSearchScope;
import static org.hisp.dhis.android.core.organisationunit.OrganisationUnitTree.getCaptureOrgUnitsInSearchScope;

@Reusable
class OrganisationUnitCall {
    private static int PAGE_SIZE = 500;

    private final OrganisationUnitService organisationUnitService;
    private final OrganisationUnitHandler handler;
    private final OrganisationUnitDisplayPathTransformer pathTransformer;

    @Inject
    OrganisationUnitCall(@NonNull OrganisationUnitService organisationUnitService,
                         @NonNull OrganisationUnitHandler handler,
                         @NonNull OrganisationUnitDisplayPathTransformer pathTransformer) {

        this.organisationUnitService = organisationUnitService;
        this.handler = handler;
        this.pathTransformer = pathTransformer;
    }

    public Single<List<OrganisationUnit>> download(final User user) {
        return Single.defer(() -> {
            handler.resetLinks();

            Set<OrganisationUnit> rootSearchOrgUnits =
                    findRoots(UserInternalAccessor.accessTeiSearchOrganisationUnits(user));
            return downloadSearchOrgUnits(rootSearchOrgUnits, user).flatMap(searchOrgUnits ->
                    downloadDataCaptureOrgUnits(rootSearchOrgUnits, searchOrgUnits, user).map(dataCaptureOrgUnits -> {
                        searchOrgUnits.addAll(dataCaptureOrgUnits);
                        return searchOrgUnits;
                    }));
        });
    }

    private Single<List<OrganisationUnit>> downloadSearchOrgUnits(Set<OrganisationUnit> rootSearchOrgUnits, User user) {
        return downloadOrgUnits(UidsHelper.getUids(rootSearchOrgUnits), user, OrganisationUnit.Scope.SCOPE_TEI_SEARCH);
    }

    private Single<List<OrganisationUnit>> downloadDataCaptureOrgUnits(
            Set<OrganisationUnit> rootSearchOrgUnits, List<OrganisationUnit> searchOrgUnits, User user) {
        Set<OrganisationUnit> allRootCaptureOrgUnits = findRoots(UserInternalAccessor.accessOrganisationUnits(user));
        Set<OrganisationUnit> rootCaptureOrgUnitsOutsideSearchScope =
                findRootsOutsideSearchScope(allRootCaptureOrgUnits, rootSearchOrgUnits);
        linkCaptureOrgUnitsInSearchScope(getCaptureOrgUnitsInSearchScope(searchOrgUnits, allRootCaptureOrgUnits,
                rootCaptureOrgUnitsOutsideSearchScope), user);

        return downloadOrgUnits(UidsHelper.getUids(rootCaptureOrgUnitsOutsideSearchScope),
                user, OrganisationUnit.Scope.SCOPE_DATA_CAPTURE);
    }

    private Single<List<OrganisationUnit>> downloadOrgUnits(final Set<String> orgUnits,
                                                            final User user,
                                                            final OrganisationUnit.Scope scope) {
        handler.setData(user, scope);
        return Flowable.fromIterable(orgUnits)
                .flatMap(this::downloadOrganisationUnitAndDescendants)
                .reduce(new ArrayList<>(), (items, items2) -> {
                    items.addAll(items2);
                    return items;
                });
    }

    private void linkCaptureOrgUnitsInSearchScope(final Set<OrganisationUnit> orgUnits, final User user) {
        handler.setData(user, OrganisationUnit.Scope.SCOPE_DATA_CAPTURE);
        handler.addUserOrganisationUnitLinks(orgUnits);
    }

    private Flowable<List<OrganisationUnit>> downloadOrganisationUnitAndDescendants(String orgUnit) {
        AtomicInteger page = new AtomicInteger(1);
        return downloadPage(orgUnit, page)
                .repeat()
                .takeUntil(organisationUnits -> organisationUnits.size() < PAGE_SIZE);
    }

    private Single<List<OrganisationUnit>> downloadPage(String orgUnit, AtomicInteger page) {
        return Single.defer(() -> organisationUnitService.getOrganisationUnits(
                OrganisationUnitFields.allFields, OrganisationUnitFields.path.like(orgUnit),
                true, PAGE_SIZE, page.getAndIncrement())
                .map(Payload::items)
                .doOnSuccess(items -> handler.handleMany(items, pathTransformer)));
    }
}