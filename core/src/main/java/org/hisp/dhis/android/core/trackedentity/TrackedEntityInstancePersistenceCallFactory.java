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

package org.hisp.dhis.android.core.trackedentity;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModuleDownloader;
import org.hisp.dhis.android.core.user.AuthenticatedUser;
import org.hisp.dhis.android.core.user.User;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
final class TrackedEntityInstancePersistenceCallFactory {

    private final TrackedEntityInstanceHandler trackedEntityInstanceHandler;
    private final TrackedEntityInstanceUidHelper uidsHelper;
    private final ObjectWithoutUidStore<AuthenticatedUser> authenticatedUserStore;
    private final OrganisationUnitModuleDownloader organisationUnitDownloader;

    @Inject
    TrackedEntityInstancePersistenceCallFactory(
            @NonNull TrackedEntityInstanceHandler trackedEntityInstanceHandler,
            @NonNull TrackedEntityInstanceUidHelper uidsHelper,
            @NonNull ObjectWithoutUidStore<AuthenticatedUser> authenticatedUserStore,
            @NonNull OrganisationUnitModuleDownloader organisationUnitDownloader) {
        this.trackedEntityInstanceHandler = trackedEntityInstanceHandler;
        this.uidsHelper = uidsHelper;
        this.authenticatedUserStore = authenticatedUserStore;
        this.organisationUnitDownloader = organisationUnitDownloader;
    }

    public Callable<Void> getCall(final List<TrackedEntityInstance> trackedEntityInstances) {

        return () -> {
            trackedEntityInstanceHandler.handleMany(trackedEntityInstances, false);

            Set<String> searchOrgUnitUids = uidsHelper.getMissingOrganisationUnitUids(trackedEntityInstances);

            if (!searchOrgUnitUids.isEmpty()) {
                AuthenticatedUser authenticatedUser = authenticatedUserStore.selectFirst();

                Callable<List<OrganisationUnit>> organisationUnitCall =
                        organisationUnitDownloader.downloadSearchOrganisationUnits(searchOrgUnitUids,
                                User.builder().uid(authenticatedUser.user()).build());
                organisationUnitCall.call();
            }

            return null;
        };
    }
}