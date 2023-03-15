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

package org.hisp.dhis.android.core.trackedentity;

import org.hisp.dhis.android.core.arch.call.D2Progress;
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction;
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
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.common.internal.TrackerDataManager;
import org.hisp.dhis.android.core.enrollment.EnrollmentTableInfo;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceTableInfo.Columns;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceFields;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstancePostParentCall;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStore;
import org.hisp.dhis.android.core.tracker.importer.internal.JobQueryCall;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.Reusable;
import io.reactivex.Observable;

@Reusable
public final class TrackedEntityInstanceCollectionRepository
        extends ReadWriteWithUidCollectionRepositoryImpl
        <TrackedEntityInstance, TrackedEntityInstanceCreateProjection, TrackedEntityInstanceCollectionRepository>
        implements ReadWriteWithUploadWithUidCollectionRepository
        <TrackedEntityInstance, TrackedEntityInstanceCreateProjection> {

    private final TrackedEntityInstancePostParentCall postCall;
    private final TrackedEntityInstanceStore store;
    private final TrackerDataManager trackerDataManager;
    private final JobQueryCall jobQueryCall;

    @Inject
    TrackedEntityInstanceCollectionRepository(
            final TrackedEntityInstanceStore store,
            final Map<String, ChildrenAppender<TrackedEntityInstance>> childrenAppenders,
            final RepositoryScope scope,
            final Transformer<TrackedEntityInstanceCreateProjection, TrackedEntityInstance> transformer,
            final TrackerDataManager trackerDataManager,
            final TrackedEntityInstancePostParentCall postCall,
            final JobQueryCall jobQueryCall) {
        super(store, childrenAppenders, scope, transformer, new FilterConnectorFactory<>(scope, s ->
                new TrackedEntityInstanceCollectionRepository(store, childrenAppenders, s, transformer,
                        trackerDataManager, postCall, jobQueryCall)));
        this.postCall = postCall;
        this.store = store;
        this.trackerDataManager = trackerDataManager;
        this.jobQueryCall = jobQueryCall;
    }

    @Override
    protected void propagateState(TrackedEntityInstance trackedEntityInstance, HandleAction action) {
        trackerDataManager.propagateTrackedEntityUpdate(trackedEntityInstance, action);
    }

    @Override
    public Observable<D2Progress> upload() {
        return Observable.concat(
                jobQueryCall.queryPendingJobs(),
                Observable.fromCallable(() ->
                        byAggregatedSyncState().in(State.uploadableStatesIncludingError()).blockingGetWithoutChildren()
                ).flatMap(postCall::uploadTrackedEntityInstances)
        );
    }

    @Override
    public void blockingUpload() {
        upload().blockingSubscribe();
    }

    @Override
    public TrackedEntityInstanceObjectRepository uid(String uid) {
        RepositoryScope updatedScope = RepositoryScopeHelper.withUidFilterItem(scope, uid);
        return new TrackedEntityInstanceObjectRepository(store, uid, childrenAppenders, updatedScope,
                trackerDataManager);
    }

    public StringFilterConnector<TrackedEntityInstanceCollectionRepository> byUid() {
        return cf.string(Columns.UID);
    }

    public DateFilterConnector<TrackedEntityInstanceCollectionRepository> byCreated() {
        return cf.date(Columns.CREATED);
    }

    public DateFilterConnector<TrackedEntityInstanceCollectionRepository> byLastUpdated() {
        return cf.date(Columns.LAST_UPDATED);
    }

    public StringFilterConnector<TrackedEntityInstanceCollectionRepository> byCreatedAtClient() {
        return cf.string(Columns.CREATED_AT_CLIENT);
    }

    public StringFilterConnector<TrackedEntityInstanceCollectionRepository> byLastUpdatedAtClient() {
        return cf.string(Columns.LAST_UPDATED_AT_CLIENT);
    }

    public StringFilterConnector<TrackedEntityInstanceCollectionRepository> byOrganisationUnitUid() {
        return cf.string(Columns.ORGANISATION_UNIT);
    }

    public StringFilterConnector<TrackedEntityInstanceCollectionRepository> byTrackedEntityType() {
        return cf.string(Columns.TRACKED_ENTITY_TYPE);
    }

    public EnumFilterConnector<TrackedEntityInstanceCollectionRepository, FeatureType> byGeometryType() {
        return cf.enumC(Columns.GEOMETRY_TYPE);
    }

    public StringFilterConnector<TrackedEntityInstanceCollectionRepository> byGeometryCoordinates() {
        return cf.string(Columns.GEOMETRY_COORDINATES);
    }

    /**
     * @deprecated Use {@link #byAggregatedSyncState()} instead.
     */
    @Deprecated
    public EnumFilterConnector<TrackedEntityInstanceCollectionRepository, State> byState() {
        return byAggregatedSyncState();
    }

    public EnumFilterConnector<TrackedEntityInstanceCollectionRepository, State> bySyncState() {
        return cf.enumC(Columns.SYNC_STATE);
    }

    public EnumFilterConnector<TrackedEntityInstanceCollectionRepository, State> byAggregatedSyncState() {
        return cf.enumC(Columns.AGGREGATED_SYNC_STATE);
    }

    public BooleanFilterConnector<TrackedEntityInstanceCollectionRepository> byDeleted() {
        return cf.bool(Columns.DELETED);
    }

    public TrackedEntityInstanceCollectionRepository byProgramUids(List<String> programUids) {
        return cf.subQuery(Columns.UID).inLinkTable(
                EnrollmentTableInfo.TABLE_INFO.name(),
                EnrollmentTableInfo.Columns.TRACKED_ENTITY_INSTANCE,
                EnrollmentTableInfo.Columns.PROGRAM,
                programUids);
    }

    public TrackedEntityInstanceCollectionRepository orderByCreated(RepositoryScope.OrderByDirection direction) {
        return cf.withOrderBy(Columns.CREATED, direction);
    }

    public TrackedEntityInstanceCollectionRepository orderByLastUpdated(RepositoryScope.OrderByDirection direction) {
        return cf.withOrderBy(Columns.LAST_UPDATED, direction);
    }

    public TrackedEntityInstanceCollectionRepository orderByCreatedAtClient(
            RepositoryScope.OrderByDirection direction) {
        return cf.withOrderBy(Columns.CREATED_AT_CLIENT, direction);
    }

    public TrackedEntityInstanceCollectionRepository orderByLastUpdatedAtClient(
            RepositoryScope.OrderByDirection direction) {
        return cf.withOrderBy(Columns.LAST_UPDATED_AT_CLIENT, direction);
    }

    public TrackedEntityInstanceCollectionRepository withTrackedEntityAttributeValues() {
        return cf.withChild(TrackedEntityInstanceFields.TRACKED_ENTITY_ATTRIBUTE_VALUES);
    }

    public TrackedEntityInstanceCollectionRepository withProgramOwners() {
        return cf.withChild(TrackedEntityInstanceFields.PROGRAM_OWNERS);
    }
}
