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

import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction;
import org.hisp.dhis.android.core.arch.helpers.GeometryHelper;
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.object.internal.ReadWriteWithUidDataObjectRepositoryImpl;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.common.Geometry;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.common.Unit;
import org.hisp.dhis.android.core.common.internal.TrackerDataManager;
import org.hisp.dhis.android.core.event.internal.EventStore;
import org.hisp.dhis.android.core.maintenance.D2Error;

import java.util.Date;
import java.util.Map;

public final class EventObjectRepository
        extends ReadWriteWithUidDataObjectRepositoryImpl<Event, EventObjectRepository> {

    private final TrackerDataManager trackerDataManager;

    EventObjectRepository(final EventStore store,
                          final String uid,
                          final Map<String, ChildrenAppender<Event>> childrenAppenders,
                          final RepositoryScope scope,
                          final TrackerDataManager trackerDataManager) {
        super(store, childrenAppenders, scope,
                s -> new EventObjectRepository(store, uid, childrenAppenders, s, trackerDataManager));
        this.trackerDataManager = trackerDataManager;
    }

    public Unit setOrganisationUnitUid(String organisationUnitUid) throws D2Error {
        return updateObject(updateBuilder().organisationUnit(organisationUnitUid).build());
    }

    public Unit setEventDate(Date eventDate) throws D2Error {
        return updateObject(updateBuilder().eventDate(eventDate).build());
    }

    public Unit setStatus(EventStatus eventStatus) throws D2Error {
        Date completedDate = eventStatus.equals(EventStatus.COMPLETED) ? new Date() : null;
        return updateObject(updateBuilder().status(eventStatus).completedDate(completedDate).build());
    }

    public Unit setCompletedDate(Date completedDate) throws D2Error {
        return updateObject(updateBuilder().completedDate(completedDate).build());
    }

    public Unit setDueDate(Date dueDate) throws D2Error {
        return updateObject(updateBuilder().dueDate(dueDate).build());
    }

    public Unit setGeometry(Geometry geometry) throws D2Error {
        GeometryHelper.validateGeometry(geometry);
        return updateObject(updateBuilder().geometry(geometry).build());
    }

    public Unit setAttributeOptionComboUid(String attributeOptionComboUid) throws D2Error {
        return updateObject(updateBuilder().attributeOptionCombo(attributeOptionComboUid).build());
    }

    public Unit setAssignedUser(String assignedUser) throws D2Error {
        return updateObject(updateBuilder().assignedUser(assignedUser).build());
    }

    private Event.Builder updateBuilder() {
        Event event = blockingGetWithoutChildren();
        Date updateDate = new Date();
        State state = event.syncState();
        state = state == State.TO_POST ? state : State.TO_UPDATE;

        return event.toBuilder()
                .syncState(state)
                .aggregatedSyncState(state)
                .lastUpdated(updateDate)
                .lastUpdatedAtClient(updateDate);
    }

    @Override
    protected void propagateState(Event event, HandleAction action) {
        trackerDataManager.propagateEventUpdate(event, action);
    }

    @Override
    protected void deleteObject(Event event) {
        trackerDataManager.deleteEvent(event);
    }
}