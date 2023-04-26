/*
 *  Copyright (c) 2004-2023, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.programstageworkinglist;

import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyIdentifiableCollectionRepositoryImpl;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EnumFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.common.AssignedUserMode;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
import org.hisp.dhis.android.core.event.EventStatus;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode;
import org.hisp.dhis.android.core.programstageworkinglist.internal.ProgramStageQueryCriteriaFields;
import org.hisp.dhis.android.core.programstageworkinglist.internal.ProgramStageWorkingListTableInfo.Columns;

import java.util.Map;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public final class ProgramStageWorkingListCollectionRepository
        extends ReadOnlyIdentifiableCollectionRepositoryImpl<ProgramStageWorkingList,
        ProgramStageWorkingListCollectionRepository> {

    @Inject
    ProgramStageWorkingListCollectionRepository(
            final IdentifiableObjectStore<ProgramStageWorkingList> store,
            final Map<String, ChildrenAppender<ProgramStageWorkingList>> childrenAppenders,
            final RepositoryScope scope) {
        super(store, childrenAppenders, scope, new FilterConnectorFactory<>(scope,
                s -> new ProgramStageWorkingListCollectionRepository(store, childrenAppenders, s)));
    }

    public StringFilterConnector<ProgramStageWorkingListCollectionRepository> byProgram() {
        return cf.string(Columns.PROGRAM);
    }

    public StringFilterConnector<ProgramStageWorkingListCollectionRepository> byProgramStage() {
        return cf.string(Columns.PROGRAM_STAGE);
    }

    public StringFilterConnector<ProgramStageWorkingListCollectionRepository> byDescription() {
        return cf.string(Columns.DESCRIPTION);
    }

    public EnumFilterConnector<ProgramStageWorkingListCollectionRepository, EventStatus> byEventStatus() {
        return cf.enumC(Columns.EVENT_STATUS);
    }

    public EnumFilterConnector<ProgramStageWorkingListCollectionRepository, EnrollmentStatus> byEnrollmentStatus() {
        return cf.enumC(Columns.ENROLLMENT_STATUS);
    }

    public StringFilterConnector<ProgramStageWorkingListCollectionRepository> byOrder() {
        return cf.string(Columns.ORDER);
    }

    public StringFilterConnector<ProgramStageWorkingListCollectionRepository> byDisplayColumnOrder() {
        return cf.string(Columns.DISPLAY_COLUMN_ORDER);
    }

    public StringFilterConnector<ProgramStageWorkingListCollectionRepository> byOrganisationUnit() {
        return cf.string(Columns.ORG_UNIT);
    }

    public EnumFilterConnector<ProgramStageWorkingListCollectionRepository, OrganisationUnitMode> byOuMode() {
        return cf.enumC(Columns.OU_MODE);
    }

    public EnumFilterConnector<ProgramStageWorkingListCollectionRepository, AssignedUserMode> byAssignedUserMode() {
        return cf.enumC(Columns.ASSIGNED_USER_MODE);
    }

    public ProgramStageWorkingListCollectionRepository withDataFilters() {
        return cf.withChild(ProgramStageQueryCriteriaFields.DATA_FILTERS);
    }

    public ProgramStageWorkingListCollectionRepository withAttributeValueFilters() {
        return cf.withChild(ProgramStageQueryCriteriaFields.ATTRIBUTE_VALUE_FILTER);
    }
}