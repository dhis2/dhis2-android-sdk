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

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableSyncHandlerImpl;
import org.hisp.dhis.android.core.common.HandleAction;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLink;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkHelper;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkStore;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
class SearchOrganisationUnitHandlerImpl extends IdentifiableSyncHandlerImpl<OrganisationUnit>
    implements SearchOrganisationUnitHandler {

    private final UserOrganisationUnitLinkStore userOrganisationUnitLinkStore;
    private User user;

    @Inject
    SearchOrganisationUnitHandlerImpl(@NonNull IdentifiableObjectStore<OrganisationUnit> organisationUnitStore,
                                      @NonNull UserOrganisationUnitLinkStore userOrganisationUnitLinkStore) {
        super(organisationUnitStore);
        this.userOrganisationUnitLinkStore = userOrganisationUnitLinkStore;
    }

    @Override
    public void setUser(User user) {
        this.user = user;
    }

    @Override
    protected void afterObjectHandled(OrganisationUnit organisationUnit, HandleAction action) {

        OrganisationUnit.Scope scope = OrganisationUnit.Scope.SCOPE_TEI_SEARCH;
        UserOrganisationUnitLink.Builder builder = UserOrganisationUnitLink.builder()
                .organisationUnitScope(scope.name()).user(user.uid());

        userOrganisationUnitLinkStore.insert(builder
                .organisationUnit(organisationUnit.uid())
                .root(UserOrganisationUnitLinkHelper.isRoot(scope, user, organisationUnit))
                .build());
    }
}