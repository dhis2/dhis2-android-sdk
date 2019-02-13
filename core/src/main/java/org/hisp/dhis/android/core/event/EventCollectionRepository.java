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

import org.hisp.dhis.android.core.arch.repositories.children.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyWithUidCollectionRepositoryImpl;
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyWithUploadWithUidCollectionRepository;
import org.hisp.dhis.android.core.arch.repositories.filters.DateFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.EnumFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.FilterConnectorFactory;
import org.hisp.dhis.android.core.arch.repositories.filters.StringFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScopeItem;
import org.hisp.dhis.android.core.common.BaseDataModel;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.imports.WebResponse;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public final class EventCollectionRepository
        extends ReadOnlyWithUidCollectionRepositoryImpl<Event, EventCollectionRepository>
        implements ReadOnlyWithUploadWithUidCollectionRepository<Event> {

    private final EventPostCall postCall;

    @Inject
    EventCollectionRepository(final EventStore store,
                              final Collection<ChildrenAppender<Event>> childrenAppenders,
                              final List<RepositoryScopeItem> scope,
                              final EventPostCall postCall) {
        super(store, childrenAppenders, scope, new FilterConnectorFactory<>(scope,
                updatedScope -> new EventCollectionRepository(store, childrenAppenders, updatedScope, postCall)));
        this.postCall = postCall;
    }


    @Override
    public Callable<WebResponse> upload() {
        return postCall;
    }

    public StringFilterConnector<EventCollectionRepository> byUid() {
        return cf.string(EventTableInfo.Columns.UID);
    }

    public StringFilterConnector<EventCollectionRepository> byEnrollmentUid() {
        return cf.string(EventFields.ENROLLMENT);
    }

    public DateFilterConnector<EventCollectionRepository> byCreated() {
        return cf.date(EventFields.CREATED);
    }

    public DateFilterConnector<EventCollectionRepository> byLastUpdated() {
        return cf.date(EventFields.LAST_UPDATED);
    }

    public StringFilterConnector<EventCollectionRepository> byCreatedAtClient() {
        return cf.string(EventTableInfo.Columns.CREATED_AT_CLIENT);
    }

    public StringFilterConnector<EventCollectionRepository> byLastUpdatedAtClient() {
        return cf.string(EventTableInfo.Columns.LAST_UPDATED_AT_CLIENT);
    }

    public EnumFilterConnector<EventCollectionRepository, EventStatus> byStatus() {
        return cf.enumC(EventFields.STATUS);
    }

    public StringFilterConnector<EventCollectionRepository> byLatitude() {
        return cf.string(EventTableInfo.Columns.LATITUDE);
    }

    public StringFilterConnector<EventCollectionRepository> byLongitude() {
        return cf.string(EventTableInfo.Columns.LONGITUDE);
    }

    public StringFilterConnector<EventCollectionRepository> byProgramUid() {
        return cf.string(EventFields.PROGRAM);
    }

    public StringFilterConnector<EventCollectionRepository> byProgramStageUid() {
        return cf.string(EventFields.PROGRAM_STAGE);
    }

    public StringFilterConnector<EventCollectionRepository> byOrganisationUnitUid() {
        return cf.string(EventTableInfo.Columns.ORGANISATION_UNIT);
    }

    public DateFilterConnector<EventCollectionRepository> byEventDate() {
        return cf.date(EventFields.EVENT_DATE);
    }

    public DateFilterConnector<EventCollectionRepository> byCompleteDate() {
        return cf.date(EventFields.COMPLETE_DATE);
    }

    public DateFilterConnector<EventCollectionRepository> byDueDate() {
        return cf.date(EventFields.DUE_DATE);
    }

    public EnumFilterConnector<EventCollectionRepository, State> byState() {
        return cf.enumC(BaseDataModel.Columns.STATE);
    }

    public StringFilterConnector<EventCollectionRepository> byAttributeOptionComboUid() {
        return cf.string(EventFields.ATTRIBUTE_OPTION_COMBO);
    }

    public StringFilterConnector<EventCollectionRepository> byTrackedEntityInstaceUid() {
        return cf.string(EventTableInfo.Columns.TRACKED_ENTITY_INSTANCE);
    }


}