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
import org.hisp.dhis.android.core.arch.helpers.GeometryHelper;
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.object.internal.ReadWriteWithUidDataObjectRepositoryImpl;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.common.Geometry;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.common.Unit;
import org.hisp.dhis.android.core.common.internal.TrackerDataManager;
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore;
import org.hisp.dhis.android.core.maintenance.D2Error;

import java.util.Date;
import java.util.Map;

public final class EnrollmentObjectRepository
        extends ReadWriteWithUidDataObjectRepositoryImpl<Enrollment, EnrollmentObjectRepository> {

    private final TrackerDataManager trackerDataManager;

    EnrollmentObjectRepository(final EnrollmentStore store,
                               final String uid,
                               final Map<String, ChildrenAppender<Enrollment>> childrenAppenders,
                               final RepositoryScope scope,
                               final TrackerDataManager trackerDataManager) {
        super(store, childrenAppenders, scope,
                s -> new EnrollmentObjectRepository(store, uid, childrenAppenders, s, trackerDataManager));
        this.trackerDataManager = trackerDataManager;
    }

    public Unit setOrganisationUnitUid(String organisationUnitUid) throws D2Error {
        return updateObject(updateBuilder().organisationUnit(organisationUnitUid).build());
    }

    public Unit setEnrollmentDate(Date enrollmentDate) throws D2Error {
        return updateObject(updateBuilder().enrollmentDate(enrollmentDate).build());
    }

    public Unit setIncidentDate(Date incidentDate) throws D2Error {
        return updateObject(updateBuilder().incidentDate(incidentDate).build());
    }

    public Unit setCompletedDate(Date completedDate) throws D2Error {
        return updateObject(updateBuilder().completedDate(completedDate).build());
    }

    public Unit setFollowUp(Boolean followUp) throws D2Error {
        return updateObject(updateBuilder().followUp(followUp).build());
    }

    public Unit setStatus(EnrollmentStatus enrollmentStatus) throws D2Error {
        Date completedDate = enrollmentStatus.equals(EnrollmentStatus.COMPLETED) ? new Date() : null;
        return updateObject(updateBuilder().status(enrollmentStatus).completedDate(completedDate).build());
    }

    public Unit setGeometry(Geometry geometry) throws D2Error {
        GeometryHelper.validateGeometry(geometry);
        return updateObject(updateBuilder().geometry(geometry).build());
    }

    private Enrollment.Builder updateBuilder() {
        Enrollment enrollment = blockingGetWithoutChildren();
        Date updateDate = new Date();
        State state = enrollment.aggregatedSyncState();
        state = state == State.TO_POST ? state : State.TO_UPDATE;

        return enrollment.toBuilder()
                .syncState(state)
                .aggregatedSyncState(state)
                .lastUpdated(updateDate)
                .lastUpdatedAtClient(updateDate);
    }

    @Override
    protected void propagateState(Enrollment enrollment, HandleAction action) {
        trackerDataManager.propagateEnrollmentUpdate(enrollment, action);
    }

    @Override
    protected void deleteObject(Enrollment enrollment) {
        trackerDataManager.deleteEnrollment(enrollment);
    }
}