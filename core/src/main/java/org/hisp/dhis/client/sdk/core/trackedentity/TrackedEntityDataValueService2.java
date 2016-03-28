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
import org.hisp.dhis.client.sdk.core.event.IEventService;
import org.hisp.dhis.client.sdk.models.common.state.Action;
import org.hisp.dhis.client.sdk.models.dataelement.DataElement;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityDataValue;

import java.util.List;

import static org.hisp.dhis.client.sdk.models.utils.Preconditions.isNull;

// TODO try to implement generic service for data
public class TrackedEntityDataValueService2 implements ITrackedEntityDataValueService {
    private final ITrackedEntityDataValueStore trackedEntityDataValueStore;
    private final IEventService eventService;
    private final IStateStore stateStore;

    public TrackedEntityDataValueService2(IEventService eventService,
                                          ITrackedEntityDataValueStore dataValueStore,
                                          IStateStore stateStore) {
        this.trackedEntityDataValueStore = dataValueStore;
        this.eventService = eventService;
        this.stateStore = stateStore;
    }

    @Override
    public boolean save(TrackedEntityDataValue dataValue) {
        checkDataValueProperties(dataValue);

        Event event = dataValue.getEvent();
        Action action = stateStore.queryActionForModel(event);

//        if (action == null) {
//            boolean status = dashboardStore.save(object);
//
//            if (status) {
//                status = stateStore.saveActionForModel(object, Action.TO_POST);
//            }
//
//            return status;
//        }

        boolean status = false;
        switch (action) {
            case TO_POST:
            case TO_UPDATE: {
                status = trackedEntityDataValueStore.save(dataValue);
                break;
            }
            case SYNCED: {
                status = trackedEntityDataValueStore.save(dataValue);

                if (status) {
                    status = stateStore.saveActionForModel(event, Action.TO_UPDATE);
                }
                break;
            }
            case TO_DELETE: {
                status = false;
                break;
            }

        }

        return status;
    }

    @Override
    public boolean remove(TrackedEntityDataValue object) {
        return false;
    }

    @Override
    public List<TrackedEntityDataValue> list() {
        return null;
    }

    @Override
    public List<TrackedEntityDataValue> list(Event event) {
        return null;
    }

    @Override
    public TrackedEntityDataValue get(long id) {
        return null;
    }

    @Override
    public TrackedEntityDataValue get(Event event, DataElement dataElement) {
        return null;
    }

    private void checkDataValueProperties(TrackedEntityDataValue dataValue) {
        isNull(dataValue, "TrackedEntityDataValue must not be null");
        isNull(dataValue.getEvent(), "Event associated with " +
                "TrackedEntityDataValue must not be null");
        isNull(dataValue.getDataElement(), "DataElement associated with " +
                "TrackedEntityDataValue must not be null");
        isNull(dataValue.getStoredBy(), "storedBy field in " +
                "TrackedEntityDataValue must not be null");
        isNull(eventService.get(dataValue.getEvent().getId()), "Given event " +
                "is not persisted locally, save event first");
    }
}
