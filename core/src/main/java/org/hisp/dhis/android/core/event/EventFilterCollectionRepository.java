/*
 *  Copyright (c) 2004-2022, University of Oslo
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

package org.hisp.dhis.android.core.event;

import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyIdentifiableCollectionRepositoryImpl;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.BooleanFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EnumFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.common.AssignedUserMode;
import org.hisp.dhis.android.core.event.EventFilterTableInfo.Columns;
import org.hisp.dhis.android.core.event.internal.EventQueryCriteriaFields;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode;

import java.util.Map;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public final class EventFilterCollectionRepository
        extends ReadOnlyIdentifiableCollectionRepositoryImpl<EventFilter, EventFilterCollectionRepository> {

    @Inject
    EventFilterCollectionRepository(
            final IdentifiableObjectStore<EventFilter> store,
            final Map<String, ChildrenAppender<EventFilter>> childrenAppenders,
            final RepositoryScope scope) {
        super(store, childrenAppenders, scope, new FilterConnectorFactory<>(scope,
                s -> new EventFilterCollectionRepository(store, childrenAppenders, s)));
    }

    public StringFilterConnector<EventFilterCollectionRepository> byProgram() {
        return cf.string(Columns.PROGRAM);
    }

    public StringFilterConnector<EventFilterCollectionRepository> byProgramStage() {
        return cf.string(Columns.PROGRAM_STAGE);
    }

    public StringFilterConnector<EventFilterCollectionRepository> byDescription() {
        return cf.string(Columns.DESCRIPTION);
    }

    public BooleanFilterConnector<EventFilterCollectionRepository> byFollowUp() {
        return cf.bool(Columns.FOLLOW_UP);
    }

    public StringFilterConnector<EventFilterCollectionRepository> byOrganisationUnit() {
        return cf.string(Columns.ORGANISATION_UNIT);
    }

    public EnumFilterConnector<EventFilterCollectionRepository, OrganisationUnitMode> byOuMode() {
        return cf.enumC(Columns.OU_MODE);
    }

    public EnumFilterConnector<EventFilterCollectionRepository, AssignedUserMode> byAssignedUserMode() {
        return cf.enumC(Columns.ASSIGNED_USER_MODE);
    }

    public StringFilterConnector<EventFilterCollectionRepository> byOrder() {
        return cf.string(Columns.ORDER);
    }

    public StringFilterConnector<EventFilterCollectionRepository> byDisplayColumnOrder() {
        return cf.string(Columns.DISPLAY_COLUMN_ORDER);
    }

    public StringFilterConnector<EventFilterCollectionRepository> byEvents() {
        return cf.string(Columns.EVENTS);
    }

    public EnumFilterConnector<EventFilterCollectionRepository, EventStatus> byEventStatus() {
        return cf.enumC(Columns.EVENT_STATUS);
    }

    public StringFilterConnector<EventFilterCollectionRepository> byEventDate() {
        return cf.string(Columns.EVENT_DATE);
    }

    public StringFilterConnector<EventFilterCollectionRepository> byDueDate() {
        return cf.string(Columns.DUE_DATE);
    }

    public StringFilterConnector<EventFilterCollectionRepository> byLastUpdatedDate() {
        return cf.string(Columns.LAST_UPDATED_DATE);
    }

    public StringFilterConnector<EventFilterCollectionRepository> byCompletedDate() {
        return cf.string(Columns.COMPLETED_DATE);
    }

    public EventFilterCollectionRepository withEventDataFilters() {
        return cf.withChild(EventQueryCriteriaFields.DATA_FILTERS);
    }
}