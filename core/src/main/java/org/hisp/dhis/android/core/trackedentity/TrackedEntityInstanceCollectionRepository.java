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

package org.hisp.dhis.android.core.trackedentity;

import org.hisp.dhis.android.core.arch.handlers.internal.Transformer;
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.collection.ReadWriteWithUploadWithUidCollectionRepository;
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadWriteWithUidCollectionRepositoryImpl;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.DateFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EnumFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeHelper;
import org.hisp.dhis.android.core.common.BaseDataModel;
import org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentFields;
import org.hisp.dhis.android.core.enrollment.EnrollmentTableInfo;
import org.hisp.dhis.android.core.imports.WebResponse;
import org.hisp.dhis.android.core.period.FeatureType;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public final class TrackedEntityInstanceCollectionRepository
        extends ReadWriteWithUidCollectionRepositoryImpl
        <TrackedEntityInstance, TrackedEntityInstanceCreateProjection, TrackedEntityInstanceCollectionRepository>
        implements ReadWriteWithUploadWithUidCollectionRepository
        <TrackedEntityInstance, TrackedEntityInstanceCreateProjection> {

    private final TrackedEntityInstancePostCall postCall;
    private final TrackedEntityInstanceStore store;

    @Inject
    TrackedEntityInstanceCollectionRepository(
            final TrackedEntityInstanceStore store,
            final Map<String, ChildrenAppender<TrackedEntityInstance>> childrenAppenders,
            final RepositoryScope scope,
            final Transformer<TrackedEntityInstanceCreateProjection, TrackedEntityInstance> transformer,
            final TrackedEntityInstancePostCall postCall) {
        super(store, childrenAppenders, scope, transformer, new FilterConnectorFactory<>(scope, s ->
                new TrackedEntityInstanceCollectionRepository(store, childrenAppenders, s, transformer, postCall)));
        this.postCall = postCall;
        this.store = store;
    }

    @Override
    public Callable<WebResponse> upload() {
        return () -> postCall.call(byState().in(State.TO_POST, State.TO_UPDATE, State.TO_DELETE).getWithoutChildren());
    }

    @Override
    public TrackedEntityInstanceObjectRepository uid(String uid) {
        RepositoryScope updatedScope = RepositoryScopeHelper.withUidFilterItem(scope, uid);
        return new TrackedEntityInstanceObjectRepository(store, uid, childrenAppenders, updatedScope);
    }

    public StringFilterConnector<TrackedEntityInstanceCollectionRepository> byUid() {
        return cf.string(TrackedEntityInstanceTableInfo.Columns.UID);
    }

    public DateFilterConnector<TrackedEntityInstanceCollectionRepository> byCreated() {
        return cf.date(TrackedEntityInstanceFields.CREATED);
    }

    public DateFilterConnector<TrackedEntityInstanceCollectionRepository> byLastUpdated() {
        return cf.date(TrackedEntityInstanceFields.LAST_UPDATED);
    }

    public StringFilterConnector<TrackedEntityInstanceCollectionRepository> byCreatedAtClient() {
        return cf.string(TrackedEntityInstanceTableInfo.Columns.CREATED_AT_CLIENT);
    }

    public StringFilterConnector<TrackedEntityInstanceCollectionRepository> byLastUpdatedAtClient() {
        return cf.string(TrackedEntityInstanceTableInfo.Columns.LAST_UPDATED_AT_CLIENT);
    }

    public StringFilterConnector<TrackedEntityInstanceCollectionRepository> byOrganisationUnitUid() {
        return cf.string(TrackedEntityInstanceTableInfo.Columns.ORGANISATION_UNIT);
    }

    public StringFilterConnector<TrackedEntityInstanceCollectionRepository> byTrackedEntityType() {
        return cf.string(TrackedEntityInstanceFields.TRACKED_ENTITY_TYPE);
    }

    public StringFilterConnector<TrackedEntityInstanceCollectionRepository> byCoordinates() {
        return cf.string(TrackedEntityInstanceFields.COORDINATES);
    }

    public EnumFilterConnector<TrackedEntityInstanceCollectionRepository, FeatureType> byFeatureType() {
        return cf.enumC(TrackedEntityInstanceFields.FEATURE_TYPE);
    }

    public EnumFilterConnector<TrackedEntityInstanceCollectionRepository, State> byState() {
        return cf.enumC(BaseDataModel.Columns.STATE);
    }

    public TrackedEntityInstanceCollectionRepository byProgramUids(List<String> programUids) {
        return cf.subQuery(BaseIdentifiableObjectModel.Columns.UID).inLinkTable(
                EnrollmentTableInfo.TABLE_INFO.name(),
                EnrollmentFields.TRACKED_ENTITY_INSTANCE,
                EnrollmentFields.PROGRAM,
                programUids);
    }

    public TrackedEntityInstanceCollectionRepository withEnrollments() {
        return cf.withChild(TrackedEntityInstanceFields.ENROLLMENTS);
    }

    public TrackedEntityInstanceCollectionRepository withTrackedEntityAttributeValues() {
        return cf.withChild(TrackedEntityInstanceFields.TRACKED_ENTITY_ATTRIBUTE_VALUES);
    }

    public TrackedEntityInstanceCollectionRepository withRelationships() {
        return cf.withChild(TrackedEntityInstanceFields.RELATIONSHIPS);
    }
}
