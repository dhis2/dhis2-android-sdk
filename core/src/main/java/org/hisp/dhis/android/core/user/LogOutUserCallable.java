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

package org.hisp.dhis.android.core.user;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStore;

import java.util.concurrent.Callable;

public class LogOutUserCallable implements Callable<Void> {

    @NonNull
    private final UserStore userStore;

    @NonNull
    private final UserCredentialsStore userCredentialsStore;

    @NonNull
    private final UserOrganisationUnitLinkStore userOrganisationUnitLinkStore;

    @NonNull
    private final AuthenticatedUserStore authenticatedUserStore;

    @NonNull
    private final OrganisationUnitStore organisationUnitStore;

    public LogOutUserCallable(@NonNull UserStore userStore,
            @NonNull UserCredentialsStore userCredentialsStore,
            @NonNull UserOrganisationUnitLinkStore userOrganisationUnitLinkStore,
            @NonNull AuthenticatedUserStore authenticatedUserStore,
            @NonNull OrganisationUnitStore organisationUnitStore) {
        this.userStore = userStore;
        this.userCredentialsStore = userCredentialsStore;
        this.userOrganisationUnitLinkStore = userOrganisationUnitLinkStore;
        this.authenticatedUserStore = authenticatedUserStore;
        this.organisationUnitStore = organisationUnitStore;
    }

    @Override
    public Void call() throws Exception {
        // clear out all tables
        userStore.delete();
        userCredentialsStore.delete();
        userOrganisationUnitLinkStore.delete();
        authenticatedUserStore.delete();
        organisationUnitStore.delete();

        return null;
    }
}
