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

package org.hisp.dhis.android.core.enrollment;

import org.hisp.dhis.android.core.arch.repositories.children.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.collection.ReadWriteWithUidCollectionRepositoryImpl;
import org.hisp.dhis.android.core.arch.repositories.filters.BooleanFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.DateFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.DoubleFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.EnumFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.FilterConnectorFactory;
import org.hisp.dhis.android.core.arch.repositories.filters.StringFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScopeHelper;
import org.hisp.dhis.android.core.common.DataStatePropagator;
import org.hisp.dhis.android.core.common.Transformer;

import java.util.Map;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public final class EnrollmentCollectionRepository extends ReadWriteWithUidCollectionRepositoryImpl
        <Enrollment, EnrollmentCreateProjection, EnrollmentCollectionRepository> {

    private final EnrollmentStore store;
    private final DataStatePropagator dataStatePropagator;

    @Inject
    EnrollmentCollectionRepository(
            final EnrollmentStore store,
            final Map<String, ChildrenAppender<Enrollment>> childrenAppenders,
            final RepositoryScope scope,
            final Transformer<EnrollmentCreateProjection, Enrollment> transformer,
            final DataStatePropagator dataStatePropagator) {
        super(store, childrenAppenders, scope, transformer, new FilterConnectorFactory<>(scope, s ->
                new EnrollmentCollectionRepository(store, childrenAppenders, s, transformer, dataStatePropagator)));
        this.store = store;
        this.dataStatePropagator = dataStatePropagator;
    }

    @Override
    public EnrollmentObjectRepository uid(String uid) {
        RepositoryScope updatedScope = RepositoryScopeHelper.withUidFilterItem(scope, uid);
        return new EnrollmentObjectRepository(store, uid, childrenAppenders, updatedScope, dataStatePropagator);
    }

    public StringFilterConnector<EnrollmentCollectionRepository> byUid() {
        return cf.string(EnrollmentTableInfo.Columns.UID);
    }

    public DateFilterConnector<EnrollmentCollectionRepository> byCreated() {
        return cf.date(EnrollmentFields.CREATED);
    }

    public DateFilterConnector<EnrollmentCollectionRepository> byLastUpdated() {
        return cf.date(EnrollmentFields.LAST_UPDATED);
    }

    public StringFilterConnector<EnrollmentCollectionRepository> byCreatedAtClient() {
        return cf.string(EnrollmentTableInfo.Columns.CREATED_AT_CLIENT);
    }

    public StringFilterConnector<EnrollmentCollectionRepository> byLastUpdatedAtClient() {
        return cf.string(EnrollmentTableInfo.Columns.LAST_UPDATED_AT_CLIENT);
    }

    public StringFilterConnector<EnrollmentCollectionRepository> byOrganisationUnit() {
        return cf.string(EnrollmentTableInfo.Columns.ORGANISATION_UNIT);
    }

    public StringFilterConnector<EnrollmentCollectionRepository> byProgram() {
        return cf.string(EnrollmentFields.PROGRAM);
    }

    public DateFilterConnector<EnrollmentCollectionRepository> byEnrollmentDate() {
        return cf.date(EnrollmentFields.ENROLLMENT_DATE);
    }

    public DateFilterConnector<EnrollmentCollectionRepository> byIncidentDate() {
        return cf.date(EnrollmentFields.INCIDENT_DATE);
    }

    public BooleanFilterConnector<EnrollmentCollectionRepository> byFollowUp() {
        return cf.bool(EnrollmentFields.FOLLOW_UP);
    }

    public EnumFilterConnector<EnrollmentCollectionRepository, EnrollmentStatus> byStatus() {
        return cf.enumC(EnrollmentFields.STATUS);
    }

    public StringFilterConnector<EnrollmentCollectionRepository> byTrackedEntityInstance() {
        return cf.string(EnrollmentFields.TRACKED_ENTITY_INSTANCE);
    }

    public DoubleFilterConnector<EnrollmentCollectionRepository> byCoordinateLatitude() {
        return cf.doubleC(EnrollmentTableInfo.Columns.LATITUDE);
    }

    public DoubleFilterConnector<EnrollmentCollectionRepository> byCoordinateLongitude() {
        return cf.doubleC(EnrollmentTableInfo.Columns.LONGITUDE);
    }

    public EnrollmentCollectionRepository withEvents() {
        return cf.withChild(EnrollmentFields.EVENTS);
    }

    public EnrollmentCollectionRepository withNotes() {
        return cf.withChild(EnrollmentFields.NOTES);
    }
}