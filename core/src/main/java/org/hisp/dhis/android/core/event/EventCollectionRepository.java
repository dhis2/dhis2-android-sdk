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

import org.hisp.dhis.android.core.arch.call.D2Progress;
import org.hisp.dhis.android.core.arch.handlers.internal.Transformer;
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.collection.ReadWriteWithUploadWithUidCollectionRepository;
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadWriteWithUidCollectionRepositoryImpl;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.BooleanFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.DateFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EnumFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeHelper;
import org.hisp.dhis.android.core.common.FeatureType;
import org.hisp.dhis.android.core.common.IdentifiableColumns;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.common.internal.DataStatePropagator;
import org.hisp.dhis.android.core.enrollment.EnrollmentTableInfo;
import org.hisp.dhis.android.core.event.internal.EventFields;
import org.hisp.dhis.android.core.event.internal.EventPostCall;
import org.hisp.dhis.android.core.event.internal.EventStore;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitTableInfo;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.Reusable;
import io.reactivex.Observable;

import static org.hisp.dhis.android.core.event.EventTableInfo.Columns;

@Reusable
public final class EventCollectionRepository
        extends ReadWriteWithUidCollectionRepositoryImpl<Event, EventCreateProjection, EventCollectionRepository>
        implements ReadWriteWithUploadWithUidCollectionRepository<Event, EventCreateProjection> {

    private final EventPostCall postCall;

    private final EventStore store;
    private final DataStatePropagator dataStatePropagator;

    @Inject
    EventCollectionRepository(final EventStore store,
                              final Map<String, ChildrenAppender<Event>> childrenAppenders,
                              final RepositoryScope scope,
                              final EventPostCall postCall,
                              final Transformer<EventCreateProjection, Event> transformer,
                              final DataStatePropagator dataStatePropagator) {
        super(store, childrenAppenders, scope, transformer,
                new FilterConnectorFactory<>(scope, s -> new EventCollectionRepository(
                        store, childrenAppenders, s, postCall, transformer, dataStatePropagator)));
        this.store = store;
        this.postCall = postCall;
        this.dataStatePropagator = dataStatePropagator;
    }

    @Override
    public Observable<D2Progress> upload() {
        return Observable.fromCallable(() -> byState().in(State.uploadableStates())
                .byEnrollmentUid().isNull()
                .blockingGetWithoutChildren())
                .flatMap(postCall::uploadEvents);
    }

    @Override
    public void blockingUpload() {
        upload().blockingSubscribe();
    }

    @Override
    public EventObjectRepository uid(String uid) {
        RepositoryScope updatedScope = RepositoryScopeHelper.withUidFilterItem(scope, uid);
        return new EventObjectRepository(store, uid, childrenAppenders, updatedScope, dataStatePropagator);
    }

    public StringFilterConnector<EventCollectionRepository> byUid() {
        return cf.string(Columns.UID);
    }

    public StringFilterConnector<EventCollectionRepository> byEnrollmentUid() {
        return cf.string(Columns.ENROLLMENT);
    }

    public DateFilterConnector<EventCollectionRepository> byCreated() {
        return cf.date(Columns.CREATED);
    }

    public DateFilterConnector<EventCollectionRepository> byLastUpdated() {
        return cf.date(Columns.LAST_UPDATED);
    }

    public StringFilterConnector<EventCollectionRepository> byCreatedAtClient() {
        return cf.string(Columns.CREATED_AT_CLIENT);
    }

    public StringFilterConnector<EventCollectionRepository> byLastUpdatedAtClient() {
        return cf.string(Columns.LAST_UPDATED_AT_CLIENT);
    }

    public EnumFilterConnector<EventCollectionRepository, EventStatus> byStatus() {
        return cf.enumC(Columns.STATUS);
    }

    public EnumFilterConnector<EventCollectionRepository, FeatureType> byGeometryType() {
        return cf.enumC(Columns.GEOMETRY_TYPE);
    }

    public StringFilterConnector<EventCollectionRepository> byGeometryCoordinates() {
        return cf.string(Columns.GEOMETRY_COORDINATES);
    }

    public StringFilterConnector<EventCollectionRepository> byProgramUid() {
        return cf.string(Columns.PROGRAM);
    }

    public StringFilterConnector<EventCollectionRepository> byProgramStageUid() {
        return cf.string(Columns.PROGRAM_STAGE);
    }

    public StringFilterConnector<EventCollectionRepository> byOrganisationUnitUid() {
        return cf.string(Columns.ORGANISATION_UNIT);
    }

    public DateFilterConnector<EventCollectionRepository> byEventDate() {
        return cf.date(Columns.EVENT_DATE);
    }

    public DateFilterConnector<EventCollectionRepository> byCompleteDate() {
        return cf.date(Columns.COMPLETE_DATE);
    }

    public DateFilterConnector<EventCollectionRepository> byDueDate() {
        return cf.date(Columns.DUE_DATE);
    }

    public EnumFilterConnector<EventCollectionRepository, State> byState() {
        return cf.enumC(Columns.STATE);
    }

    public StringFilterConnector<EventCollectionRepository> byAttributeOptionComboUid() {
        return cf.string(Columns.ATTRIBUTE_OPTION_COMBO);
    }

    public BooleanFilterConnector<EventCollectionRepository> byDeleted() {
        return cf.bool(Columns.DELETED);
    }

    public EventCollectionRepository byTrackedEntityInstanceUids(List<String> uids) {
        return cf.subQuery(Columns.ENROLLMENT).inLinkTable(
                EnrollmentTableInfo.TABLE_INFO.name(),
                IdentifiableColumns.UID,
                EnrollmentTableInfo.Columns.TRACKED_ENTITY_INSTANCE,
                uids
        );
    }

    public StringFilterConnector<EventCollectionRepository> byAssignedUser() {
        return cf.string(Columns.ASSIGNED_USER);
    }

    public EventCollectionRepository withTrackedEntityDataValues() {
        return cf.withChild(EventFields.TRACKED_ENTITY_DATA_VALUES);
    }

    public EventCollectionRepository withNotes() {
        return cf.withChild(EventFields.NOTES);
    }

    public EventCollectionRepository orderByEventDate(RepositoryScope.OrderByDirection direction) {
        return cf.withOrderBy(Columns.EVENT_DATE, direction);
    }

    public EventCollectionRepository orderByDueDate(RepositoryScope.OrderByDirection direction) {
        return cf.withOrderBy(Columns.DUE_DATE, direction);
    }

    public EventCollectionRepository orderByCompleteDate(RepositoryScope.OrderByDirection direction) {
        return cf.withOrderBy(Columns.COMPLETE_DATE, direction);
    }

    public EventCollectionRepository orderByCreated(RepositoryScope.OrderByDirection direction) {
        return cf.withOrderBy(Columns.CREATED, direction);
    }

    public EventCollectionRepository orderByLastUpdated(RepositoryScope.OrderByDirection direction) {
        return cf.withOrderBy(Columns.LAST_UPDATED, direction);
    }

    public EventCollectionRepository orderByCreatedAtClient(RepositoryScope.OrderByDirection direction) {
        return cf.withOrderBy(Columns.CREATED_AT_CLIENT, direction);
    }

    public EventCollectionRepository orderByLastUpdatedAtClient(RepositoryScope.OrderByDirection direction) {
        return cf.withOrderBy(Columns.LAST_UPDATED_AT_CLIENT, direction);
    }

    public EventCollectionRepository orderByOrganisationUnitName(RepositoryScope.OrderByDirection direction) {
        return cf.withExternalOrderBy(
                OrganisationUnitTableInfo.TABLE_INFO.name(),
                IdentifiableColumns.NAME,
                IdentifiableColumns.UID,
                Columns.ORGANISATION_UNIT,
                direction);
    }

    public EventCollectionRepository orderByTimeline(RepositoryScope.OrderByDirection direction) {
        return cf.withConditionalOrderBy(
                Columns.STATUS,
                String.format("IN ('%s', '%s', '%s')", EventStatus.ACTIVE, EventStatus.COMPLETED, EventStatus.VISITED),
                Columns.EVENT_DATE,
                Columns.DUE_DATE,
                direction
        );
    }

    public int countTrackedEntityInstances() {
        return store.countTeisWhereEvents(getWhereClause());
    }
}