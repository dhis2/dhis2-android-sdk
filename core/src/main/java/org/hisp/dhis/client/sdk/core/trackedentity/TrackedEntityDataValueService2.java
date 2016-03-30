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

package org.hisp.dhis.client.sdk.core.trackedentity;

import org.hisp.dhis.client.sdk.core.common.IStateStore;
import org.hisp.dhis.client.sdk.core.event.IEventStore;
import org.hisp.dhis.client.sdk.models.common.state.Action;
import org.hisp.dhis.client.sdk.models.dataelement.DataElement;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityDataValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hisp.dhis.client.sdk.models.utils.Preconditions.isNull;


public class TrackedEntityDataValueService2 implements ITrackedEntityDataValueService {
    private final ITrackedEntityDataValueStore trackedEntityDataValueStore;
    private final IEventStore eventStore;
    private final IStateStore stateStore;

    public TrackedEntityDataValueService2(ITrackedEntityDataValueStore dataValueStore,
                                          IEventStore eventStore, IStateStore stateStore) {
        this.trackedEntityDataValueStore = dataValueStore;
        this.eventStore = eventStore;
        this.stateStore = stateStore;
    }

    @Override
    public boolean save(TrackedEntityDataValue dataValue) {
        checkDataValueProperties(dataValue);

        Event event = dataValue.getEvent();
        Action action = stateStore.queryActionForModel(event);
        if (action == null) {
            throw new IllegalArgumentException("Related event is not persisted " +
                    "(you should save event first)");
        }

        switch (action) {
            case TO_POST:
            case TO_UPDATE: {
                return trackedEntityDataValueStore.save(dataValue);
            }
            case SYNCED: {
                return trackedEntityDataValueStore.save(dataValue) &&
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
    public boolean remove(TrackedEntityDataValue dataValue) {
        checkDataValueProperties(dataValue);

        Event event = dataValue.getEvent();
        Action action = stateStore.queryActionForModel(event);
        if (action == null) {
            return false;
        }

        switch (action) {
            case TO_POST:
            case TO_UPDATE: {
                return trackedEntityDataValueStore.delete(dataValue);
            }
            case SYNCED: {
                return trackedEntityDataValueStore.delete(dataValue) &&
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
    public List<TrackedEntityDataValue> list() {
        Map<Long, Action> stateMap = stateStore.queryActionsForModel(Event.class);

        List<Event> events = eventStore.queryAll();
        List<TrackedEntityDataValue> dataValues = new ArrayList<>();
        if (events == null || events.isEmpty()) {
            return dataValues;
        }

        for (Event event : events) {
            if (!Action.TO_DELETE.equals(stateMap.get(event.getId()))) {
                dataValues.addAll(event.getDataValues());
            }
        }

        return dataValues;
    }

    @Override
    public List<TrackedEntityDataValue> list(Event event) {
        // check state of event

        return trackedEntityDataValueStore.query(event);
    }

    @Override
    public TrackedEntityDataValue get(long id) {
        TrackedEntityDataValue dataValue =
                trackedEntityDataValueStore.queryById(id);

        if (dataValue == null) {
            return null;
        }

        Event persistedEvent = eventStore
                .queryById(dataValue.getEvent().getId());

        if (persistedEvent != null) {
            Action action = stateStore.queryActionForModel(persistedEvent);

            if (!Action.TO_DELETE.equals(action)) {
                return dataValue;
            }
        }

        return null;
    }

    @Override
    public TrackedEntityDataValue get(Event event, DataElement dataElement) {
        TrackedEntityDataValue dataValue =
                trackedEntityDataValueStore.query(event, dataElement);

        if (dataValue == null) {
            return null;
        }

        Event persistedEvent = eventStore
                .queryById(dataValue.getEvent().getId());

        if (persistedEvent != null) {
            Action action = stateStore.queryActionForModel(persistedEvent);

            if (!Action.TO_DELETE.equals(action)) {
                return dataValue;
            }
        }

        return null;
    }

    private static void checkDataValueProperties(TrackedEntityDataValue dataValue) {
        isNull(dataValue, "TrackedEntityDataValue must not be null");
        isNull(dataValue.getEvent(), "Event associated with " +
                "TrackedEntityDataValue must not be null");
        isNull(dataValue.getDataElement(), "DataElement associated with " +
                "TrackedEntityDataValue must not be null");
        isNull(dataValue.getStoredBy(), "storedBy field in " +
                "TrackedEntityDataValue must not be null");
    }
}
