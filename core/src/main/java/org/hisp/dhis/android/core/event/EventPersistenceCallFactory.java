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

package org.hisp.dhis.android.core.event;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.arch.handlers.SyncHandlerWithTransformer;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.maintenance.ForeignKeyCleaner;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModuleDownloader;
import org.hisp.dhis.android.core.user.AuthenticatedUserModel;
import org.hisp.dhis.android.core.user.User;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
final class EventPersistenceCallFactory {

    private final SyncHandlerWithTransformer<Event> eventHandler;
    private final ObjectWithoutUidStore<AuthenticatedUserModel> authenticatedUserStore;
    private final IdentifiableObjectStore<OrganisationUnit> organisationUnitStore;
    private final OrganisationUnitModuleDownloader organisationUnitDownloader;
    private final ForeignKeyCleaner foreignKeyCleaner;

    @Inject
    EventPersistenceCallFactory(
            @NonNull SyncHandlerWithTransformer<Event> eventHandler,
            @NonNull ObjectWithoutUidStore<AuthenticatedUserModel> authenticatedUserStore,
            @NonNull IdentifiableObjectStore<OrganisationUnit> organisationUnitStore,
            @NonNull OrganisationUnitModuleDownloader organisationUnitDownloader,
            @NonNull ForeignKeyCleaner foreignKeyCleaner) {
        this.eventHandler = eventHandler;
        this.authenticatedUserStore = authenticatedUserStore;
        this.organisationUnitStore = organisationUnitStore;
        this.organisationUnitDownloader = organisationUnitDownloader;
        this.foreignKeyCleaner = foreignKeyCleaner;
    }

    public Callable<Void> getCall(final Collection<Event> events) {

        return () -> {
            eventHandler.handleMany(events,
                    event -> event.toBuilder()
                            .state(State.SYNCED)
                            .build());

            Set<String> searchOrgUnitUids = getMissingOrganisationUnitUids(events);

            if (!searchOrgUnitUids.isEmpty()) {
                AuthenticatedUserModel authenticatedUserModel = authenticatedUserStore.selectFirst();

                Callable<List<OrganisationUnit>> organisationUnitCall =
                        organisationUnitDownloader.downloadSearchOrganisationUnits(
                            searchOrgUnitUids, User.builder().uid(authenticatedUserModel.user()).build());
                organisationUnitCall.call();
            }

            foreignKeyCleaner.cleanForeignKeyErrors();

            return null;
        };
    }

    private Set<String> getMissingOrganisationUnitUids(Collection<Event> events) {
        Set<String> uids = new HashSet<>();
        for (Event event : events) {
            if (event.organisationUnit() != null) {
                uids.add(event.organisationUnit());
            }
        }
        uids.removeAll(organisationUnitStore.selectUids());
        return uids;
    }
}
