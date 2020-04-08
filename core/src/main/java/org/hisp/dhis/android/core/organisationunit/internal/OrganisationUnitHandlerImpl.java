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

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction;
import org.hisp.dhis.android.core.arch.handlers.internal.Handler;
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableHandlerImpl;
import org.hisp.dhis.android.core.arch.handlers.internal.LinkHandler;
import org.hisp.dhis.android.core.arch.handlers.internal.LinkHandlerImpl;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.dataset.DataSetOrganisationUnitLink;
import org.hisp.dhis.android.core.dataset.internal.DataSetOrganisationUnitLinkStore;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitOrganisationUnitGroupLink;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLink;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLink;
import org.hisp.dhis.android.core.user.internal.UserOrganisationUnitLinkHelper;
import org.hisp.dhis.android.core.user.internal.UserOrganisationUnitLinkStoreImpl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"PMD.ExcessiveImports"})
class OrganisationUnitHandlerImpl extends IdentifiableHandlerImpl<OrganisationUnit>
        implements OrganisationUnitHandler {
    private final LinkHandler<OrganisationUnit, UserOrganisationUnitLink> userOrganisationUnitLinkHandler;
    private final LinkHandler<ObjectWithUid, OrganisationUnitProgramLink> organisationUnitProgramLinkHandler;
    private final LinkHandler<ObjectWithUid, DataSetOrganisationUnitLink> dataSetOrganisationUnitLinkHandler;
    private final Handler<OrganisationUnitGroup> organisationUnitGroupHandler;
    private final LinkHandler<OrganisationUnitGroup, OrganisationUnitOrganisationUnitGroupLink>
            organisationUnitGroupLinkHandler;

    private User user;
    private OrganisationUnit.Scope scope;

    OrganisationUnitHandlerImpl(@NonNull IdentifiableObjectStore<OrganisationUnit> organisationUnitStore,
                                @NonNull LinkHandler<OrganisationUnit, UserOrganisationUnitLink>
                                        userOrganisationUnitLinkHandler,
                                @NonNull LinkHandler<ObjectWithUid, OrganisationUnitProgramLink>
                                        organisationUnitProgramLinkHandler,
                                @NonNull LinkHandler<ObjectWithUid, DataSetOrganisationUnitLink>
                                        dataSetOrganisationUnitLinkHandler,
                                @NonNull Handler<OrganisationUnitGroup> organisationUnitGroupHandler,
                                @NonNull LinkHandler<OrganisationUnitGroup,
                                        OrganisationUnitOrganisationUnitGroupLink>
                                        organisationUnitGroupLinkHandler) {

        super(organisationUnitStore);
        this.userOrganisationUnitLinkHandler = userOrganisationUnitLinkHandler;
        this.organisationUnitGroupHandler = organisationUnitGroupHandler;
        this.organisationUnitGroupLinkHandler = organisationUnitGroupLinkHandler;
        this.organisationUnitProgramLinkHandler = organisationUnitProgramLinkHandler;
        this.dataSetOrganisationUnitLinkHandler = dataSetOrganisationUnitLinkHandler;
    }

    @Override
    public void resetLinks() {
        userOrganisationUnitLinkHandler.resetAllLinks();
        organisationUnitProgramLinkHandler.resetAllLinks();
        dataSetOrganisationUnitLinkHandler.resetAllLinks();
        organisationUnitGroupLinkHandler.resetAllLinks();
    }

    @Override
    public void setData(User user, OrganisationUnit.Scope scope) {
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
        List<ObjectWithUid> orgUnitPrograms = organisationUnit.programs();
        if (orgUnitPrograms != null) {
            organisationUnitProgramLinkHandler.handleMany(organisationUnit.uid(), orgUnitPrograms,
                    program -> OrganisationUnitProgramLink.builder()
                            .organisationUnit(organisationUnit.uid()).program(program.uid()).build());
        }
    }

    private void addOrganisationUnitDataSetLink(@NonNull OrganisationUnit organisationUnit) {
        List<ObjectWithUid> orgUnitDataSets = organisationUnit.dataSets();
        if (orgUnitDataSets != null) {

            dataSetOrganisationUnitLinkHandler.handleMany(organisationUnit.uid(), orgUnitDataSets,
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
        UserOrganisationUnitLink.Builder builder = UserOrganisationUnitLink.builder()
                .organisationUnitScope(scope.name()).user(user.uid());

        // TODO MasterUid set to "" to avoid cleaning link table. Orgunits are paged, so the whole orguntit list is
        //  not available in the handler. Maybe the store should not be a linkStore.
        userOrganisationUnitLinkHandler.handleMany("", Collections.singletonList(organisationUnit),
                orgUnit -> builder
                        .organisationUnit(orgUnit.uid())
                        .root(UserOrganisationUnitLinkHelper.isRoot(scope, user, orgUnit))
                        .build()
        );
    }

    @Override
    public void addUserOrganisationUnitLinks(@NonNull Collection<OrganisationUnit> organisationUnits) {
        for (OrganisationUnit organisationUnit : organisationUnits) {
            addOrganisationUnitDataSetLink(organisationUnit);
        }
    }

    public static OrganisationUnitHandler create(DatabaseAdapter databaseAdapter) {
        return new OrganisationUnitHandlerImpl(
                OrganisationUnitStore.create(databaseAdapter),
                new LinkHandlerImpl<>(UserOrganisationUnitLinkStoreImpl.create(databaseAdapter)),
                new LinkHandlerImpl<>(OrganisationUnitProgramLinkStore.create(databaseAdapter)),
                new LinkHandlerImpl<>(DataSetOrganisationUnitLinkStore.create(databaseAdapter)),
                new IdentifiableHandlerImpl<>(OrganisationUnitGroupStore.create(databaseAdapter)),
                new LinkHandlerImpl<>(OrganisationUnitOrganisationUnitGroupLinkStore.create(databaseAdapter)));
    }
}