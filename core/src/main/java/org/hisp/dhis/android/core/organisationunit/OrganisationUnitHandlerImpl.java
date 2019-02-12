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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.arch.handlers.IdentifiableSyncHandlerImpl;
import org.hisp.dhis.android.core.arch.handlers.SyncHandler;
import org.hisp.dhis.android.core.common.CollectionCleaner;
import org.hisp.dhis.android.core.common.CollectionCleanerImpl;
import org.hisp.dhis.android.core.common.HandleAction;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.LinkModelHandler;
import org.hisp.dhis.android.core.common.LinkModelHandlerImpl;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.dataset.DataSet;
import org.hisp.dhis.android.core.dataset.DataSetModel;
import org.hisp.dhis.android.core.dataset.DataSetOrganisationUnitLinkModel;
import org.hisp.dhis.android.core.dataset.DataSetOrganisationUnitLinkModelBuilder;
import org.hisp.dhis.android.core.dataset.DataSetOrganisationUnitLinkStore;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramTableInfo;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkModel;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkModelBuilder;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkStore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings({"PMD.ExcessiveImports", "PMD.TooManyFields"})
class OrganisationUnitHandlerImpl extends IdentifiableSyncHandlerImpl<OrganisationUnit>
        implements OrganisationUnitHandler {
    private final LinkModelHandler<OrganisationUnit, UserOrganisationUnitLinkModel> userOrganisationUnitLinkHandler;
    private final LinkModelHandler<Program, OrganisationUnitProgramLinkModel> organisationUnitProgramLinkHandler;
    private final LinkModelHandler<DataSet, DataSetOrganisationUnitLinkModel> dataSetOrganisationUnitLinkHandler;
    private final SyncHandler<OrganisationUnitGroup> organisationUnitGroupHandler;
    private final LinkModelHandler<ObjectWithUid,
            OrganisationUnitOrganisationUnitGroupLinkModel> organisationUnitGroupLinkHandler;
    private final CollectionCleaner<ObjectWithUid> programCollectionCleaner;
    private final CollectionCleaner<ObjectWithUid> dataSetCollectionCleaner;
    private final CollectionCleaner<ObjectWithUid> organisationUnitGroupCollectionCleaner;
    private final Set<ObjectWithUid> orgUnitLinkedProgramUids;
    private final Set<ObjectWithUid> orgUnitLinkedDataSetUids;
    private final Set<ObjectWithUid> organisationUnitGroupUids;

    private User user;
    private Set<String> programUids;
    private Set<String> dataSetUids;
    private Set<String> userOrganisationUnitUids;

    private Set<OrganisationUnit> userOrganisationUnitsToAdd;

    OrganisationUnitHandlerImpl(@NonNull IdentifiableObjectStore<OrganisationUnit> organisationUnitStore,
                                @NonNull LinkModelHandler<OrganisationUnit, UserOrganisationUnitLinkModel>
                                    userOrganisationUnitLinkHandler,
                                @NonNull LinkModelHandler<Program, OrganisationUnitProgramLinkModel>
                                    organisationUnitProgramLinkHandler,
                                @NonNull LinkModelHandler<DataSet, DataSetOrganisationUnitLinkModel>
                                    dataSetOrganisationUnitLinkHandler,
                                @NonNull CollectionCleaner<ObjectWithUid> programCollectionCleaner,
                                @NonNull CollectionCleaner<ObjectWithUid> dataSetCollectionCleaner,
                                @NonNull CollectionCleaner<ObjectWithUid> organisationUnitGroupCollectionCleaner,
                                @Nullable SyncHandler<OrganisationUnitGroup> organisationUnitGroupHandler,
                                @NonNull LinkModelHandler<ObjectWithUid,
                                    OrganisationUnitOrganisationUnitGroupLinkModel>
                                    organisationUnitGroupLinkHandler) {

        super(organisationUnitStore);
        this.userOrganisationUnitLinkHandler = userOrganisationUnitLinkHandler;
        this.organisationUnitGroupHandler = organisationUnitGroupHandler;
        this.organisationUnitGroupLinkHandler = organisationUnitGroupLinkHandler;
        this.organisationUnitProgramLinkHandler = organisationUnitProgramLinkHandler;
        this.dataSetOrganisationUnitLinkHandler = dataSetOrganisationUnitLinkHandler;
        this.programCollectionCleaner = programCollectionCleaner;
        this.dataSetCollectionCleaner = dataSetCollectionCleaner;
        this.organisationUnitGroupCollectionCleaner = organisationUnitGroupCollectionCleaner;
        this.orgUnitLinkedProgramUids = new HashSet<>();
        this.orgUnitLinkedDataSetUids = new HashSet<>();
        this.organisationUnitGroupUids = new HashSet<>();
        this.userOrganisationUnitUids = new HashSet<>();
        this.userOrganisationUnitsToAdd = new HashSet<>();
    }

    @Override
    public void setData(Set<String> programUids, Set<String> dataSetUids, User user) {
        this.programUids = programUids;
        this.dataSetUids = dataSetUids;
        this.user = user;
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
                    orgUnitLinkedProgramUids.add(ObjectWithUid.create(program.uid()));
                }
            }

            OrganisationUnitProgramLinkModelBuilder modelBuilder
                    = new OrganisationUnitProgramLinkModelBuilder(organisationUnit);
            organisationUnitProgramLinkHandler.handleMany(organisationUnit.uid(), programsToAdd, modelBuilder);
        }
    }

    private void addOrganisationUnitDataSetLink(@NonNull OrganisationUnit organisationUnit) {
        List<DataSet> orgUnitDataSets = organisationUnit.dataSets();
        if (orgUnitDataSets != null && dataSetUids != null) {
            List<DataSet> dataSetsToAdd = new ArrayList<>();
            for (DataSet dataSet : orgUnitDataSets) {
                if (dataSetUids.contains(dataSet.uid())) {
                    dataSetsToAdd.add(dataSet);
                    orgUnitLinkedDataSetUids.add(ObjectWithUid.create(dataSet.uid()));
                }
            }

            DataSetOrganisationUnitLinkModelBuilder modelBuilder
                    = new DataSetOrganisationUnitLinkModelBuilder(organisationUnit);
            dataSetOrganisationUnitLinkHandler.handleMany(organisationUnit.uid(), dataSetsToAdd, modelBuilder);
        }
    }

    private void addOrganisationUnitOrganisationUnitGroupLink(@NonNull OrganisationUnit organisationUnit) {

        List<OrganisationUnitGroup> linkedOrganisationUnitGroups = organisationUnit.organisationUnitGroups();

        if (linkedOrganisationUnitGroups == null || linkedOrganisationUnitGroups.isEmpty()) {
            return;
        }

        Set<ObjectWithUid> linkedOrganisationUnitGroupUids = new HashSet<>();

        for (OrganisationUnitGroup organisationUnitGroup : linkedOrganisationUnitGroups) {
            linkedOrganisationUnitGroupUids.add(ObjectWithUid.create(organisationUnitGroup.uid()));
        }

        organisationUnitGroupUids.addAll(linkedOrganisationUnitGroupUids);

        organisationUnitGroupLinkHandler.handleMany(organisationUnit.uid(), linkedOrganisationUnitGroupUids,
                new OrganisationUnitOrganisationUnitGroupLinkModelBuilder(organisationUnit));
    }

    private void addUserOrganisationUnitLink(@NonNull OrganisationUnit organisationUnit) {

        if (userOrganisationUnitUids.add(organisationUnit.uid())) {
            userOrganisationUnitsToAdd.add(organisationUnit);
        }

    }

    private void handleUserOrganisationUnitLinks() {

        UserOrganisationUnitLinkModelBuilder modelBuilder = new UserOrganisationUnitLinkModelBuilder(
                OrganisationUnit.Scope.SCOPE_DATA_CAPTURE, user);

        userOrganisationUnitLinkHandler.handleMany(user.uid(), userOrganisationUnitsToAdd, modelBuilder);

        userOrganisationUnitUids = new HashSet<>();
        userOrganisationUnitsToAdd = new HashSet<>();
    }

    @Override
    protected void afterCollectionHandled(Collection<OrganisationUnit> organisationUnits) {

        programCollectionCleaner.deleteNotPresent(orgUnitLinkedProgramUids);
        dataSetCollectionCleaner.deleteNotPresent(orgUnitLinkedDataSetUids);
        organisationUnitGroupCollectionCleaner.deleteNotPresent(organisationUnitGroupUids);

        handleUserOrganisationUnitLinks();
    }

    public static OrganisationUnitHandler create(DatabaseAdapter databaseAdapter) {
        return new OrganisationUnitHandlerImpl(
                OrganisationUnitStore.create(databaseAdapter),
                new LinkModelHandlerImpl<>(
                        UserOrganisationUnitLinkStore.create(databaseAdapter)),
                new LinkModelHandlerImpl<>(
                        OrganisationUnitProgramLinkStore.create(databaseAdapter)),
                new LinkModelHandlerImpl<>(
                        DataSetOrganisationUnitLinkStore.create(databaseAdapter)),
                new CollectionCleanerImpl<>(ProgramTableInfo.TABLE_INFO.name(), databaseAdapter),
                new CollectionCleanerImpl<>(DataSetModel.TABLE, databaseAdapter),
                new CollectionCleanerImpl<>(
                        OrganisationUnitGroupTableInfo.TABLE_INFO.name(), databaseAdapter),
                new IdentifiableSyncHandlerImpl<>(OrganisationUnitGroupStore.create(databaseAdapter)),
                new LinkModelHandlerImpl<>(
                        OrganisationUnitOrganisationUnitGroupLinkStore.create(databaseAdapter)));
    }
}