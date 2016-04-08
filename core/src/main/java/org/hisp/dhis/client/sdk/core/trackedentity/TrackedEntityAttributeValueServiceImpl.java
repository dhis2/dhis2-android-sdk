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
import org.hisp.dhis.client.sdk.models.enrollment.Enrollment;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.client.sdk.models.utils.Preconditions;

import java.util.List;

public class TrackedEntityAttributeValueServiceImpl implements TrackedEntityAttributeValueService {

    private TrackedEntityAttributeValueStore trackedEntityAttributeValueStore;
    private StateStore stateStore;

    public TrackedEntityAttributeValueServiceImpl(TrackedEntityAttributeValueStore
                                                      trackedEntityAttributeValueStore,
                                                  StateStore stateStore) {
        this.trackedEntityAttributeValueStore = trackedEntityAttributeValueStore;
        this.stateStore = stateStore;
    }

    @Override
    public TrackedEntityAttributeValue get(TrackedEntityInstance trackedEntityInstance,
                                           TrackedEntityAttribute trackedEntityAttribute) {
        Preconditions.isNull(trackedEntityInstance, "Object must not be null");
        Preconditions.isNull(trackedEntityAttribute, "Object must not be null");

        Action action = stateStore.queryActionForModel(trackedEntityInstance);
        Action actionForTrackedEntityAttribute = stateStore.queryActionForModel
                (trackedEntityAttribute);

        if (!Action.TO_DELETE.equals(action) || !Action.TO_DELETE.equals
                (actionForTrackedEntityAttribute)) {
            return trackedEntityAttributeValueStore.query(trackedEntityInstance,
                    trackedEntityAttribute);
        }

        return null;
    }

    @Override
    public List<TrackedEntityAttributeValue> list(TrackedEntityInstance trackedEntityInstance) {
        Preconditions.isNull(trackedEntityInstance, "Object must not be null");

        Action action = stateStore.queryActionForModel(trackedEntityInstance);

        if (!Action.TO_DELETE.equals(action)) {
            return trackedEntityAttributeValueStore.query(trackedEntityInstance);
        }

        return null;
    }

    @Override
    public List<TrackedEntityAttributeValue> list(Enrollment enrollment) {
        Preconditions.isNull(enrollment, "Object must not be null");

        Action action = stateStore.queryActionForModel(enrollment);

        if (!Action.TO_DELETE.equals(action)) {
            return trackedEntityAttributeValueStore.query(enrollment);
        }

        return null;
    }

    @Override
    public TrackedEntityAttributeValue get(long id) {
        TrackedEntityAttributeValue trackedEntityAttributeValue =
                trackedEntityAttributeValueStore.queryById(id);

        if (trackedEntityAttributeValue != null) {
            Action action = stateStore.queryActionForModel(trackedEntityAttributeValue);

            if (!Action.TO_DELETE.equals(action)) {
                return trackedEntityAttributeValue;
            }
        }
        return null;
    }

    @Override
    public List<TrackedEntityAttributeValue> list() {
        return stateStore.queryModelsWithActions(TrackedEntityAttributeValue.class,
                Action.SYNCED, Action.TO_POST, Action.TO_UPDATE);
    }

    @Override
    public boolean remove(TrackedEntityAttributeValue object) {
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
                status = trackedEntityAttributeValueStore.delete(object);
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
    public boolean save(TrackedEntityAttributeValue object) {
        Preconditions.isNull(object, "Object must not be null");

        Action action = stateStore.queryActionForModel(object);
        if (action == null) {
            boolean status = trackedEntityAttributeValueStore.save(object);

            if (status) {
                status = stateStore.saveActionForModel(object, Action.TO_POST);
            }

            return status;
        }

        boolean status = false;
        switch (action) {
            case TO_POST:
            case TO_UPDATE: {
                status = trackedEntityAttributeValueStore.save(object);
                break;
            }
            case SYNCED: {
                status = trackedEntityAttributeValueStore.save(object);

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
}
