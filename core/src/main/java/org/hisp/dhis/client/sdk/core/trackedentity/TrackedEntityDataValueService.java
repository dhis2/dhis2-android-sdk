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
import org.hisp.dhis.client.sdk.models.common.state.Action;
import org.hisp.dhis.client.sdk.models.dataelement.DataElement;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.client.sdk.models.utils.Preconditions;

import java.util.List;

public class TrackedEntityDataValueService implements ITrackedEntityDataValueService {
    private ITrackedEntityDataValueStore trackedEntityDataValueStore;
    private IStateStore stateStore;

    public TrackedEntityDataValueService(ITrackedEntityDataValueStore
                                                 trackedEntityDataValueStore, IStateStore
            stateStore) {
        this.trackedEntityDataValueStore = trackedEntityDataValueStore;
        this.stateStore = stateStore;
    }

    @Override
    public TrackedEntityDataValue create(Event event, String dataElement, boolean
            providedElsewhere, String storedBy, String value) {
        Preconditions.isNull(event, "event argument must not be null");
        Preconditions.isNull(dataElement, "dataElement argument must not be null");
        Preconditions.isNull(providedElsewhere, "providedElsewhere argument must not be null");
        Preconditions.isNull(storedBy, "storedBy argument must not be null");
        Preconditions.isNull(value, "value argument must not be null");

        TrackedEntityDataValue trackedEntityDataValue = new TrackedEntityDataValue();
        trackedEntityDataValue.setEvent(event);
        trackedEntityDataValue.setDataElement(dataElement);
        trackedEntityDataValue.setProvidedElsewhere(providedElsewhere);
        trackedEntityDataValue.setStoredBy(storedBy);
        trackedEntityDataValue.setValue(value);

        return trackedEntityDataValue;
    }

    @Override
    public List<TrackedEntityDataValue> list(Event event) {
        Preconditions.isNull(event, "Object must not be null");

        Action action = stateStore.queryActionForModel(event);

        if (!Action.TO_DELETE.equals(action)) {
            return trackedEntityDataValueStore.query(event);
        }

        return null;
    }

    @Override
    public TrackedEntityDataValue get(DataElement dataElement, Event event) {
        TrackedEntityDataValue trackedEntityDataValue = trackedEntityDataValueStore.query
                (dataElement, event);

        if (trackedEntityDataValue != null) {
            Action action = stateStore.queryActionForModel(trackedEntityDataValue);

            if (!Action.TO_DELETE.equals(action)) {
                return trackedEntityDataValue;
            }
        }
        return null;
    }

    @Override
    public TrackedEntityDataValue get(long id) {
        TrackedEntityDataValue trackedEntityDataValue = trackedEntityDataValueStore.queryById(id);

        if (trackedEntityDataValue != null) {
            Action action = stateStore.queryActionForModel(trackedEntityDataValue);

            if (!Action.TO_DELETE.equals(action)) {
                return trackedEntityDataValue;
            }
        }
        return null;
    }

    @Override
    public List<TrackedEntityDataValue> list() {
        return stateStore.queryModelsWithActions(TrackedEntityDataValue.class,
                Action.SYNCED, Action.TO_POST, Action.TO_UPDATE);
    }

    @Override
    public boolean remove(TrackedEntityDataValue object) {
        Preconditions.isNull(object, "Object must not be null");

        Action action = stateStore.queryActionForModel(object);
        if (action == null) {
            return false;
        }

        boolean status = false;
        switch (action) {
            case SYNCED:
            case TO_UPDATE: {
                status = stateStore.saveActionForModel(object, Action.TO_DELETE);
                break;
            }
            case TO_POST: {
                status = trackedEntityDataValueStore.delete(object);
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
    public boolean save(TrackedEntityDataValue object) {
        Preconditions.isNull(object, "Object must not be null");

        Action action = stateStore.queryActionForModel(object);
        if (action == null) {
            boolean status = trackedEntityDataValueStore.save(object);

            if (status) {
                status = stateStore.saveActionForModel(object, Action.TO_POST);
            }

            return status;
        }

        boolean status = false;
        switch (action) {
            case TO_POST:
            case TO_UPDATE: {
                status = trackedEntityDataValueStore.save(object);
                break;
            }
            case SYNCED: {
                status = trackedEntityDataValueStore.save(object);

                if (status) {
                    status = stateStore.saveActionForModel(object, Action.TO_UPDATE);
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
    public boolean add(TrackedEntityDataValue object) {
        Preconditions.isNull(object, "TrackedEntityDataValue argument must not be null");

        if (!trackedEntityDataValueStore.insert(object)) {
            return false;
        }
        return stateStore.saveActionForModel(object, Action.TO_POST);
    }

    @Override
    public boolean update(TrackedEntityDataValue object) {
        Preconditions.isNull(object, "TrackedEntityDataValue argument must not be null");

        Action action = stateStore.queryActionForModel(object);
        if (Action.TO_DELETE.equals(action)) {
            throw new IllegalArgumentException("The object with Action." +
                    "TO_DELETE cannot be updated");
        }

        /* if object was not posted to the server before,
        you don't have anything to update */
        if (!Action.TO_POST.equals(action)) {
            stateStore.saveActionForModel(object, Action.TO_UPDATE);
        }
        return trackedEntityDataValueStore.update(object);
    }
}
