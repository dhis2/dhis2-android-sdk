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
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.common.IdentifiableHandlerImpl;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkModel;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkModelBuilder;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkStore;

import java.util.List;
import java.util.Set;

public class OrganisationUnitHandler extends IdentifiableHandlerImpl<OrganisationUnit, OrganisationUnitModel> {
    private final ObjectWithoutUidStore<UserOrganisationUnitLinkModel> userOrganisationUnitLinkStore;
    private final ObjectWithoutUidStore<OrganisationUnitProgramLinkModel> organisationUnitProgramLinkStore;
    private final Set<String> programUids;
    private final OrganisationUnitModel.Scope scope;
    private final User user;

    public OrganisationUnitHandler(@NonNull IdentifiableObjectStore<OrganisationUnitModel> organisationUnitStore,
                                   @NonNull ObjectWithoutUidStore<UserOrganisationUnitLinkModel>
                                           userOrganisationUnitLinkStore,
                                   @NonNull ObjectWithoutUidStore<OrganisationUnitProgramLinkModel>
                                           organisationUnitProgramLinkStore,
                                   @Nullable Set<String> programUids,
                                   @Nullable OrganisationUnitModel.Scope scope,
                                   @Nullable User user) {
        super(organisationUnitStore);
        this.userOrganisationUnitLinkStore = userOrganisationUnitLinkStore;
        this.organisationUnitProgramLinkStore = organisationUnitProgramLinkStore;
        this.programUids = programUids;
        this.scope = scope;
        this.user = user;
    }

    @Override
    protected void afterObjectPersisted(OrganisationUnit organisationUnit) {
        UserOrganisationUnitLinkModelBuilder modelBuilder = new UserOrganisationUnitLinkModelBuilder(scope, user);
        userOrganisationUnitLinkStore.updateOrInsertWhere(modelBuilder.buildModel(organisationUnit));

        addOrganisationUnitProgramLink(organisationUnit);
    }

    private void addOrganisationUnitProgramLink(@NonNull OrganisationUnit organisationUnit) {
        List<Program> orgUnitPrograms = organisationUnit.programs();
        if (orgUnitPrograms != null && programUids != null) {
            OrganisationUnitProgramLinkModelBuilder modelBuilder
                    = new OrganisationUnitProgramLinkModelBuilder(organisationUnit);
            for (Program program : orgUnitPrograms) {
                if (programUids.contains(program.uid())) {
                    organisationUnitProgramLinkStore.updateOrInsertWhere(modelBuilder.buildModel(program));
                }
            }
        }
    }

    public static OrganisationUnitHandler create(DatabaseAdapter databaseAdapter,
                                                 Set<String> programUids,
                                                 OrganisationUnitModel.Scope scope,
                                                 User user) {
        return new OrganisationUnitHandler(
                OrganisationUnitStore.create(databaseAdapter),
                UserOrganisationUnitLinkStore.create(databaseAdapter),
                OrganisationUnitProgramLinkStore.create(databaseAdapter),
                programUids, scope, user);
    }
}
