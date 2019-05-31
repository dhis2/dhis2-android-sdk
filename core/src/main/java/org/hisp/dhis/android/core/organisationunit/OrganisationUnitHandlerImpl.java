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
import androidx.annotation.Nullable;

import org.hisp.dhis.android.core.arch.handlers.IdentifiableSyncHandlerImpl;
import org.hisp.dhis.android.core.arch.handlers.LinkSyncHandler;
import org.hisp.dhis.android.core.arch.handlers.LinkSyncHandlerImpl;
import org.hisp.dhis.android.core.arch.handlers.SyncHandler;
import org.hisp.dhis.android.core.common.HandleAction;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.dataset.DataSet;
import org.hisp.dhis.android.core.dataset.DataSetOrganisationUnitLink;
import org.hisp.dhis.android.core.dataset.DataSetOrganisationUnitLinkStore;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLink;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkHelper;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkStoreImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings({"PMD.ExcessiveImports", "PMD.TooManyFields"})
class OrganisationUnitHandlerImpl extends IdentifiableSyncHandlerImpl<OrganisationUnit>
        implements OrganisationUnitHandler {
    private final LinkSyncHandler<OrganisationUnit, UserOrganisationUnitLink> userOrganisationUnitLinkHandler;
    private final LinkSyncHandler<Program, OrganisationUnitProgramLink> organisationUnitProgramLinkHandler;
    private final LinkSyncHandler<DataSet, DataSetOrganisationUnitLink> dataSetOrganisationUnitLinkHandler;
    private final SyncHandler<OrganisationUnitGroup> organisationUnitGroupHandler;
    private final LinkSyncHandler<OrganisationUnitGroup, OrganisationUnitOrganisationUnitGroupLink>
            organisationUnitGroupLinkHandler;

    private User user;
    private Set<String> programUids;
    private Set<String> dataSetUids;
    private OrganisationUnit.Scope scope;

    private Set<String> userOrganisationUnitUids;

    private Set<OrganisationUnit> userOrganisationUnitsToAdd;

    OrganisationUnitHandlerImpl(@NonNull IdentifiableObjectStore<OrganisationUnit> organisationUnitStore,
                                @NonNull LinkSyncHandler<OrganisationUnit, UserOrganisationUnitLink>
                                        userOrganisationUnitLinkHandler,
                                @NonNull LinkSyncHandler<Program, OrganisationUnitProgramLink>
                                    organisationUnitProgramLinkHandler,
                                @NonNull LinkSyncHandler<DataSet, DataSetOrganisationUnitLink>
                                    dataSetOrganisationUnitLinkHandler,
                                @Nullable SyncHandler<OrganisationUnitGroup> organisationUnitGroupHandler,
                                @NonNull LinkSyncHandler<OrganisationUnitGroup,
                                        OrganisationUnitOrganisationUnitGroupLink>
                                        organisationUnitGroupLinkHandler) {

        super(organisationUnitStore);
        this.userOrganisationUnitLinkHandler = userOrganisationUnitLinkHandler;
        this.organisationUnitGroupHandler = organisationUnitGroupHandler;
        this.organisationUnitGroupLinkHandler = organisationUnitGroupLinkHandler;
        this.organisationUnitProgramLinkHandler = organisationUnitProgramLinkHandler;
        this.dataSetOrganisationUnitLinkHandler = dataSetOrganisationUnitLinkHandler;
        this.userOrganisationUnitUids = new HashSet<>();
        this.userOrganisationUnitsToAdd = new HashSet<>();
    }

    @Override
    public void setData(Set<String> programUids, Set<String> dataSetUids, User user, OrganisationUnit.Scope scope) {
        this.programUids = programUids;
        this.dataSetUids = dataSetUids;
        this.user = user;
        this.scope = scope;
    }

    @Override
    protected void afterObjectHandled(OrganisationUnit organisationUnit, HandleAction action) {

        addUserOrganisationUnitLink(organisationUnit);
        addOrganisationUnitProgramLink(organisationUnit);
        addOrganisationUnitDataSetLink(organisationUnit);

        organisationUnitGroupHandler.handleMany(organisationUnit.organisationUnitGroups());

        addOrganisationUnitOrganisationUnitGroupLink(organisationUnit);
    }

    private void addOrganisationUnitProgramLink(@NonNull OrganisationUnit organisationUnit) {
        List<Program> orgUnitPrograms = organisationUnit.programs();
        if (orgUnitPrograms != null && programUids != null) {
            List<Program> programsToAdd = new ArrayList<>();
            for (Program program : orgUnitPrograms) {
                if (programUids.contains(program.uid())) {
                    programsToAdd.add(program);
                }
            }

            organisationUnitProgramLinkHandler.handleMany(organisationUnit.uid(), programsToAdd,
                    program -> OrganisationUnitProgramLink.builder()
                            .organisationUnit(organisationUnit.uid()).program(program.uid()).build());
        }
    }

    private void addOrganisationUnitDataSetLink(@NonNull OrganisationUnit organisationUnit) {
        List<DataSet> orgUnitDataSets = organisationUnit.dataSets();
        if (orgUnitDataSets != null && dataSetUids != null) {
            List<DataSet> dataSetsToAdd = new ArrayList<>();
            for (DataSet dataSet : orgUnitDataSets) {
                if (dataSetUids.contains(dataSet.uid())) {
                    dataSetsToAdd.add(dataSet);
                }
            }

            dataSetOrganisationUnitLinkHandler.handleMany(organisationUnit.uid(), dataSetsToAdd,
                    dataSet -> DataSetOrganisationUnitLink.builder()
                            .dataSet(dataSet.uid()).organisationUnit(organisationUnit.uid()).build());
        }
    }

    private void addOrganisationUnitOrganisationUnitGroupLink(@NonNull OrganisationUnit organisationUnit) {

        List<OrganisationUnitGroup> linkedOrganisationUnitGroups = organisationUnit.organisationUnitGroups();

        if (linkedOrganisationUnitGroups == null || linkedOrganisationUnitGroups.isEmpty()) {
            return;
        }

        organisationUnitGroupLinkHandler.handleMany(organisationUnit.uid(), linkedOrganisationUnitGroups,
                organisationUnitGroup -> OrganisationUnitOrganisationUnitGroupLink.builder()
                        .organisationUnit(organisationUnit.uid()).organisationUnitGroup(organisationUnitGroup.uid())
                        .build());
    }

    private void addUserOrganisationUnitLink(@NonNull OrganisationUnit organisationUnit) {

        // TODO Check if this is necessary to avoid duplicates
        if (userOrganisationUnitUids.add(organisationUnit.uid())) {
            userOrganisationUnitsToAdd.add(organisationUnit);
        }

    }

    private void handleUserOrganisationUnitLinks() {
        UserOrganisationUnitLink.Builder builder = UserOrganisationUnitLink.builder()
                .organisationUnitScope(scope.name()).user(user.uid());

        userOrganisationUnitLinkHandler.handleMany(user.uid(), userOrganisationUnitsToAdd,
                organisationUnit -> builder
                        .organisationUnit(organisationUnit.uid())
                        .root(UserOrganisationUnitLinkHelper.isRoot(scope, user, organisationUnit))
                        .build()
        );

        userOrganisationUnitUids = new HashSet<>();
        userOrganisationUnitsToAdd = new HashSet<>();
    }

    @Override
    protected void afterCollectionHandled(Collection<OrganisationUnit> organisationUnits) {
        handleUserOrganisationUnitLinks();
    }

    public static OrganisationUnitHandler create(DatabaseAdapter databaseAdapter) {
        return new OrganisationUnitHandlerImpl(
                OrganisationUnitStore.create(databaseAdapter),
                new LinkSyncHandlerImpl<>(UserOrganisationUnitLinkStoreImpl.create(databaseAdapter)),
                new LinkSyncHandlerImpl<>(OrganisationUnitProgramLinkStore.create(databaseAdapter)),
                new LinkSyncHandlerImpl<>(DataSetOrganisationUnitLinkStore.create(databaseAdapter)),
                new IdentifiableSyncHandlerImpl<>(OrganisationUnitGroupStore.create(databaseAdapter)),
                new LinkSyncHandlerImpl<>(OrganisationUnitOrganisationUnitGroupLinkStore.create(databaseAdapter)));
    }
}