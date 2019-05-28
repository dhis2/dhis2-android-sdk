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
import org.hisp.dhis.android.core.arch.repositories.object.ReadWriteObjectRepository;
import org.hisp.dhis.android.core.arch.repositories.object.ReadWriteWithUidDataObjectRepositoryImpl;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.Coordinates;
import org.hisp.dhis.android.core.common.DataStatePropagator;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.common.Unit;
import org.hisp.dhis.android.core.maintenance.D2Error;

import java.util.Date;
import java.util.Map;

public final class EventObjectRepository
        extends ReadWriteWithUidDataObjectRepositoryImpl<Event, EventObjectRepository>
        implements ReadWriteObjectRepository<Event> {

    private final DataStatePropagator dataStatePropagator;
    private Event event;

    EventObjectRepository(final EventStore store,
                          final String uid,
                          final Map<String, ChildrenAppender<Event>> childrenAppenders,
                          final RepositoryScope scope,
                          final DataStatePropagator dataStatePropagator) {
        super(store, childrenAppenders, scope,
                s -> new EventObjectRepository(store, uid, childrenAppenders, s, dataStatePropagator));
        this.dataStatePropagator = dataStatePropagator;
    }

    public Unit setOrganisationUnitUid(String organisationUnitUid) throws D2Error {
        return updateObject(updateBuilder().organisationUnit(organisationUnitUid).build());
    }

    public Unit setEventDate(Date eventDate) throws D2Error {
        return updateObject(updateBuilder().eventDate(eventDate).build());
    }

    public Unit setStatus(EventStatus eventStatus) throws D2Error {
        return updateObject(updateBuilder().status(eventStatus).build());
    }

    public Unit setCoordinate(Coordinates coordinate) throws D2Error {
        return updateObject(updateBuilder().coordinate(coordinate).build());
    }

    public Unit setCompletedDate(Date completedDate) throws D2Error {
        return updateObject(updateBuilder().completedDate(completedDate).build());
    }

    public Unit setDueDate(Date dueDate) throws D2Error {
        return updateObject(updateBuilder().dueDate(dueDate).build());
    }

    private Event.Builder updateBuilder() {
        event = getWithoutChildren();
        Date updateDate = new Date();
        State state = event.state();
        if (state != State.TO_POST && state != State.TO_DELETE) {
            state = State.TO_UPDATE;
        }

        return event.toBuilder()
                .state(state)
                .lastUpdated(updateDate)
                .lastUpdatedAtClient(BaseIdentifiableObject.dateToDateStr(updateDate));
    }

    @Override
    protected void propagateState() {
        dataStatePropagator.propagateEventState(event);
    }
}