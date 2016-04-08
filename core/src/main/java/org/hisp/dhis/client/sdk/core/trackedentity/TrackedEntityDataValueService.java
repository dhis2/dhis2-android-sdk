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

import org.hisp.dhis.client.sdk.core.common.StateStore;
import org.hisp.dhis.client.sdk.models.common.state.Action;
import org.hisp.dhis.client.sdk.models.dataelement.DataElement;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityDataValue;

import java.util.List;

import static org.hisp.dhis.client.sdk.models.utils.Preconditions.isNull;

public class TrackedEntityDataValueService implements ITrackedEntityDataValueService {
    private final TrackedEntityDataValueStore trackedEntityDataValueStore;
    private final StateStore stateStore;

    public TrackedEntityDataValueService(
            TrackedEntityDataValueStore trackedEntityDataValueStore, StateStore stateStore) {
        this.trackedEntityDataValueStore = trackedEntityDataValueStore;
        this.stateStore = stateStore;
    }

    @Override
    public boolean save(TrackedEntityDataValue object) {
        isNull(object, "Object must not be null");

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
    public List<TrackedEntityDataValue> list() {
        return stateStore.queryModelsWithActions(TrackedEntityDataValue.class,
                Action.SYNCED, Action.TO_POST, Action.TO_UPDATE);
    }

    @Override
    public List<TrackedEntityDataValue> list(Event event) {
        isNull(event, "Object must not be null");

        Action action = stateStore.queryActionForModel(event);

        if (!Action.TO_DELETE.equals(action)) {
            return trackedEntityDataValueStore.query(event);
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
    public TrackedEntityDataValue get(Event event, DataElement dataElement) {
        isNull(event, "Event object must not be null");
        isNull(dataElement, "DataElement must not be null");

        Action action = stateStore.queryActionForModel(event);

        if (!Action.TO_DELETE.equals(action)) {
            return trackedEntityDataValueStore.query(event, dataElement);
        }

        return null;
    }

    @Override
    public boolean remove(TrackedEntityDataValue object) {
        isNull(object, "Object must not be null");

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
}
