/*
 * Copyright (c) 2016, University of Oslo
 *
 * All rights reserved.
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

package org.hisp.dhis.client.sdk.core.event;

import org.hisp.dhis.client.sdk.core.common.StateStore;
import org.hisp.dhis.client.sdk.core.common.utils.CodeGenerator;
import org.hisp.dhis.client.sdk.models.common.state.Action;
import org.hisp.dhis.client.sdk.models.common.state.State;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.program.ProgramStage;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public class EventServiceImpl implements EventService {
    private final EventStore eventStore;
    private final StateStore stateStore;

    public EventServiceImpl(EventStore eventStore, StateStore stateStore) {
        this.eventStore = eventStore;
        this.stateStore = stateStore;
    }

    @Override
    public Event create(OrganisationUnit organisationUnit, Program program,
                        ProgramStage programStage, Event.EventStatus status) {
        isNull(organisationUnit, "organisationUnit argument must not be null");
        isNull(program, "program argument must not be null");
        isNull(programStage, "programStage argument must not be null");
        isNull(status, "status argument must not be null");

        if(!Event.EventStatus.ACTIVE.equals(status) &&
                !Event.EventStatus.COMPLETED.equals(status)) {
            throw new IllegalArgumentException("Event status must be either ACTIVE or COMPLETED");
        }

        Event event = new Event();
        event.setUId(CodeGenerator.generateCode());
        event.setCreated(DateTime.now());
        event.setLastUpdated(DateTime.now());
        event.setStatus(status);
        event.setOrgUnit(organisationUnit.getUId());
        event.setProgram(program.getUId());
        event.setProgramStage(programStage.getUId());
        return event;
    }

    @Override
    public boolean save(Event event) {
        isNull(event, "Event must not be null");

        Action action = stateStore.queryActionForModel(event);
        if (action == null) {
            return eventStore.save(event) &&
                    stateStore.saveActionForModel(event, Action.TO_POST);
        }

        switch (action) {
            case TO_POST:
            case TO_UPDATE: {
                return eventStore.save(event);
            }
            case SYNCED: {
                return eventStore.save(event) &&
                        stateStore.saveActionForModel(event, Action.TO_UPDATE);
            }
            // we cannot save what should be removed
            case TO_DELETE: {
                return false;
            }
            default: {
                throw new IllegalArgumentException("Unsupported state action");
            }
        }
    }

    @Override
    public boolean remove(Event event) {
        isNull(event, "Event object must not be null");

        Action action = stateStore.queryActionForModel(event);
        if (action == null) {
            // if there is no action stored for given event,
            // it means it was not saved before
            return false;
        }

        switch (action) {
            case SYNCED:
            case TO_UPDATE: {
                return stateStore.saveActionForModel(event, Action.TO_DELETE);
            }
            case TO_POST: {
                return eventStore.delete(event);
            }
            case TO_DELETE: {
                // if event is already marked as removed,
                // we don't have to do anything
                return false;
            }
            default: {
                throw new IllegalArgumentException("Unsupported state action");
            }
        }
    }

    @Override
    public List<Event> list() {
        return stateStore.queryModelsWithActions(Event.class,
                Action.SYNCED, Action.TO_POST, Action.TO_UPDATE);
    }

    @Override
    public List<Event> list(Set<String> uids) {
        return stateStore.queryModelsWithActions(Event.class, uids,
                Action.SYNCED, Action.TO_POST, Action.TO_UPDATE);
    }

    @Override
    public List<Event> list(OrganisationUnit organisationUnit, Program program) {
        Map<Long, Action> actionMap = stateStore.queryActionsForModel(Event.class);
        List<Event> events = eventStore.query(organisationUnit, program);

        if (events == null || events.isEmpty()) {
            return new ArrayList<>();
        }

        List<Event> filteredEvents = new ArrayList<>();
        for (Event event : events) {
            if (!Action.TO_DELETE.equals(actionMap.get(event.getId()))) {
                filteredEvents.add(event);
            }
        }

        return filteredEvents;
    }

    @Override
    public State get(Event event) {
        return stateStore.queryStateForModel(event);
    }

    @Override
    public Event get(long id) {
        Event event = eventStore.queryById(id);

        if (event != null) {
            Action action = stateStore.queryActionForModel(event);

            if (!Action.TO_DELETE.equals(action)) {
                return event;
            }
        }

        return null;
    }

    @Override
    public Event get(String uid) {
        Event event = eventStore.queryByUid(uid);

        if (event != null) {
            Action action = stateStore.queryActionForModel(event);

            if (!Action.TO_DELETE.equals(action)) {
                return event;
            }
        }

        return null;
    }
}
