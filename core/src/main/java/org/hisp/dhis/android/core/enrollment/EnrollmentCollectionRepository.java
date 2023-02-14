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

package org.hisp.dhis.android.core.enrollment;

import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction;
import org.hisp.dhis.android.core.arch.handlers.internal.Transformer;
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadWriteWithUidCollectionRepositoryImpl;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.BooleanFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.DateFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EnumFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeHelper;
import org.hisp.dhis.android.core.common.DataColumns;
import org.hisp.dhis.android.core.common.FeatureType;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.common.internal.TrackerDataManager;
import org.hisp.dhis.android.core.enrollment.EnrollmentTableInfo.Columns;
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentFields;
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore;

import java.util.Map;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public final class EnrollmentCollectionRepository extends ReadWriteWithUidCollectionRepositoryImpl
        <Enrollment, EnrollmentCreateProjection, EnrollmentCollectionRepository> {

    private final EnrollmentStore store;
    private final TrackerDataManager trackerDataManager;

    @Inject
    EnrollmentCollectionRepository(
            final EnrollmentStore store,
            final Map<String, ChildrenAppender<Enrollment>> childrenAppenders,
            final RepositoryScope scope,
            final Transformer<EnrollmentCreateProjection, Enrollment> transformer,
            final TrackerDataManager trackerDataManager) {
        super(store, childrenAppenders, scope, transformer, new FilterConnectorFactory<>(scope, s ->
                new EnrollmentCollectionRepository(store, childrenAppenders, s, transformer, trackerDataManager)));
        this.store = store;
        this.trackerDataManager = trackerDataManager;
    }

    @Override
    protected void propagateState(Enrollment enrollment, HandleAction action) {
        trackerDataManager.propagateEnrollmentUpdate(enrollment, action);
    }

    @Override
    public EnrollmentObjectRepository uid(String uid) {
        RepositoryScope updatedScope = RepositoryScopeHelper.withUidFilterItem(scope, uid);
        return new EnrollmentObjectRepository(store, uid, childrenAppenders, updatedScope, trackerDataManager);
    }

    public StringFilterConnector<EnrollmentCollectionRepository> byUid() {
        return cf.string(EnrollmentTableInfo.Columns.UID);
    }

    public DateFilterConnector<EnrollmentCollectionRepository> byCreated() {
        return cf.date(Columns.CREATED);
    }

    public DateFilterConnector<EnrollmentCollectionRepository> byLastUpdated() {
        return cf.date(Columns.LAST_UPDATED);
    }

    public StringFilterConnector<EnrollmentCollectionRepository> byCreatedAtClient() {
        return cf.string(Columns.CREATED_AT_CLIENT);
    }

    public StringFilterConnector<EnrollmentCollectionRepository> byLastUpdatedAtClient() {
        return cf.string(Columns.LAST_UPDATED_AT_CLIENT);
    }

    public StringFilterConnector<EnrollmentCollectionRepository> byOrganisationUnit() {
        return cf.string(Columns.ORGANISATION_UNIT);
    }

    public StringFilterConnector<EnrollmentCollectionRepository> byProgram() {
        return cf.string(Columns.PROGRAM);
    }

    public DateFilterConnector<EnrollmentCollectionRepository> byEnrollmentDate() {
        return cf.simpleDate(Columns.ENROLLMENT_DATE);
    }

    public DateFilterConnector<EnrollmentCollectionRepository> byIncidentDate() {
        return cf.simpleDate(Columns.INCIDENT_DATE);
    }

    public BooleanFilterConnector<EnrollmentCollectionRepository> byFollowUp() {
        return cf.bool(Columns.FOLLOW_UP);
    }

    public EnumFilterConnector<EnrollmentCollectionRepository, EnrollmentStatus> byStatus() {
        return cf.enumC(Columns.STATUS);
    }

    public StringFilterConnector<EnrollmentCollectionRepository> byTrackedEntityInstance() {
        return cf.string(Columns.TRACKED_ENTITY_INSTANCE);
    }

    public EnumFilterConnector<EnrollmentCollectionRepository, FeatureType> byGeometryType() {
        return cf.enumC(EnrollmentTableInfo.Columns.GEOMETRY_TYPE);
    }

    public StringFilterConnector<EnrollmentCollectionRepository> byGeometryCoordinates() {
        return cf.string(EnrollmentTableInfo.Columns.GEOMETRY_COORDINATES);
    }

    public EnumFilterConnector<EnrollmentCollectionRepository, State> byAggregatedSyncState() {
        return cf.enumC(DataColumns.AGGREGATED_SYNC_STATE);
    }

    /**
     * Use {@link #byAggregatedSyncState()} instead.
     */
    @Deprecated
    public EnumFilterConnector<EnrollmentCollectionRepository, State> byState() {
        return byAggregatedSyncState();
    }

    public EnumFilterConnector<EnrollmentCollectionRepository, State> bySyncState() {
        return cf.enumC(DataColumns.SYNC_STATE);
    }

    public BooleanFilterConnector<EnrollmentCollectionRepository> byDeleted() {
        return cf.bool(EnrollmentTableInfo.Columns.DELETED);
    }

    public EnrollmentCollectionRepository orderByCreated(RepositoryScope.OrderByDirection direction) {
        return cf.withOrderBy(Columns.CREATED, direction);
    }

    public EnrollmentCollectionRepository orderByLastUpdated(RepositoryScope.OrderByDirection direction) {
        return cf.withOrderBy(Columns.LAST_UPDATED, direction);
    }

    public EnrollmentCollectionRepository orderByCreatedAtClient(RepositoryScope.OrderByDirection direction) {
        return cf.withOrderBy(Columns.CREATED_AT_CLIENT, direction);
    }

    public EnrollmentCollectionRepository orderByLastUpdatedAtClient(RepositoryScope.OrderByDirection direction) {
        return cf.withOrderBy(Columns.LAST_UPDATED_AT_CLIENT, direction);
    }

    public EnrollmentCollectionRepository orderByEnrollmentDate(RepositoryScope.OrderByDirection direction) {
        return cf.withOrderBy(Columns.ENROLLMENT_DATE, direction);
    }

    public EnrollmentCollectionRepository orderByIncidentDate(RepositoryScope.OrderByDirection direction) {
        return cf.withOrderBy(Columns.INCIDENT_DATE, direction);
    }

    public EnrollmentCollectionRepository withNotes() {
        return cf.withChild(EnrollmentFields.NOTES);
    }
}